package ru.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.EndPointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.Hit;
import ru.practicum.repository.StatsRepository;
import ru.practicum.service.StatsServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceImplTest {

  @Mock
  private StatsRepository statsRepository;

  @InjectMocks
  private StatsServiceImpl statsService;

  private EndPointHitDto endPointHitDto;
  private LocalDateTime start;
  private LocalDateTime end;

  @BeforeEach
  void setUp() {
    start = LocalDateTime.now().minusHours(2); // 2 часа назад
    end = LocalDateTime.now().plusHours(1);    // через 1 час

    endPointHitDto = EndPointHitDto.builder()
            .app("ewm-main-service")
            .uri("/events/1")
            .ip("192.168.1.1")
            .timestamp(LocalDateTime.now())
            .build();
  }

  @Test
  void saveHit_ShouldSaveHitToDatabase() {
    // When
    statsService.saveHit(endPointHitDto);

    // Then
    verify(statsRepository, times(1)).save(any(Hit.class));
  }

  @Test
  void getStats_WithUniqueFalseAndNoUris_ShouldReturnAllStats() {
    // Given
    List<ViewStatsDto> expectedStats = List.of(
            new ViewStatsDto("ewm-main-service", "/events/1", 5L),
            new ViewStatsDto("ewm-main-service", "/events/2", 3L)
    );

    when(statsRepository.findStats(start, end)).thenReturn(expectedStats);

    // When
    List<ViewStatsDto> result = statsService.getStats(start, end, null, false);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getHits()).isEqualTo(5L);
    verify(statsRepository, times(1)).findStats(start, end);
  }

  @Test
  void getStats_WithUniqueTrueAndNoUris_ShouldReturnUniqueStats() {
    // Given
    List<ViewStatsDto> expectedStats = List.of(
            new ViewStatsDto("ewm-main-service", "/events/1", 3L)
    );

    when(statsRepository.findUniqueStats(start, end)).thenReturn(expectedStats);

    // When
    List<ViewStatsDto> result = statsService.getStats(start, end, null, true);

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getHits()).isEqualTo(3L);
    verify(statsRepository, times(1)).findUniqueStats(start, end);
  }

  @Test
  void getStats_WithUrisAndUniqueFalse_ShouldReturnFilteredStats() {
    // Given
    List<String> uris = List.of("/events/1", "/events/2");
    List<ViewStatsDto> expectedStats = List.of(
            new ViewStatsDto("ewm-main-service", "/events/1", 5L)
    );

    when(statsRepository.findStatsByUris(start, end, uris)).thenReturn(expectedStats);

    // When
    List<ViewStatsDto> result = statsService.getStats(start, end, uris, false);

    // Then
    assertThat(result).hasSize(1);
    verify(statsRepository, times(1)).findStatsByUris(start, end, uris);
  }

  @Test
  void getStats_WithUrisAndUniqueTrue_ShouldReturnFilteredUniqueStats() {
    // Given
    List<String> uris = List.of("/events/1");
    List<ViewStatsDto> expectedStats = List.of(
            new ViewStatsDto("ewm-main-service", "/events/1", 2L)
    );

    when(statsRepository.findUniqueStatsByUris(start, end, uris)).thenReturn(expectedStats);

    // When
    List<ViewStatsDto> result = statsService.getStats(start, end, uris, true);

    // Then
    assertThat(result).hasSize(1);
    verify(statsRepository, times(1)).findUniqueStatsByUris(start, end, uris);
  }

  @Test
  void getStats_WhenStartAfterEnd_ShouldThrowException() {
    // Given
    LocalDateTime invalidStart = end.plusHours(1);

    // When & Then
    assertThatThrownBy(() -> statsService.getStats(invalidStart, end, null, false))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Start date must be before end date");
  }

  @Test
  void getStats_WhenUrisEmpty_ShouldReturnAllStats() {
    // Given
    List<ViewStatsDto> expectedStats = List.of(
            new ViewStatsDto("ewm-main-service", "/events/1", 5L)
    );

    when(statsRepository.findStats(start, end)).thenReturn(expectedStats);

    // When
    List<ViewStatsDto> result = statsService.getStats(start, end, List.of(), false);

    // Then
    assertThat(result).hasSize(1);
    verify(statsRepository, times(1)).findStats(start, end);
  }
}