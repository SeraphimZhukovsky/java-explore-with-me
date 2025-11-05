package ru.practicum;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.controller.StatsController;
import ru.practicum.dto.EndPointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatsController.class)
class StatsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private StatsService statsService;

  private EndPointHitDto endPointHitDto;
  private ViewStatsDto viewStatsDto;
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  @BeforeEach
  void setUp() {
    endPointHitDto = EndPointHitDto.builder()
            .app("ewm-main-service")
            .uri("/events/1")
            .ip("192.168.1.1")
            .timestamp(LocalDateTime.now())
            .build();

    viewStatsDto = new ViewStatsDto("ewm-main-service", "/events/1", 6L);
  }

  @Test
  void saveHit_ShouldReturn201() throws Exception {
    // When & Then
    mockMvc.perform(post("/hit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(endPointHitDto)))
            .andExpect(status().isCreated());

    verify(statsService, times(1)).saveHit(any(EndPointHitDto.class));
  }

  @Test
  void saveHit_WithInvalidData_ShouldReturn400() throws Exception {
    // Given
    EndPointHitDto invalidDto = EndPointHitDto.builder()
            .app("")  // empty app - invalid
            .uri("/events/1")
            .ip("192.168.1.1")
            .timestamp(LocalDateTime.now())
            .build();

    // When & Then
    mockMvc.perform(post("/hit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest());
  }

  @Test
  void getStats_ShouldReturn200AndStats() throws Exception {
    // Given
    LocalDateTime start = LocalDateTime.now().minusDays(1);
    LocalDateTime end = LocalDateTime.now().plusDays(1);

    List<ViewStatsDto> stats = List.of(viewStatsDto);

    when(statsService.getStats(any(LocalDateTime.class), any(LocalDateTime.class),
            anyList(), anyBoolean())).thenReturn(stats);

    // When & Then
    mockMvc.perform(get("/stats")
                    .param("start", start.format(formatter))
                    .param("end", end.format(formatter))
                    .param("uris", "/events/1")
                    .param("unique", "false"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].app").value("ewm-main-service"))
            .andExpect(jsonPath("$[0].uri").value("/events/1"))
            .andExpect(jsonPath("$[0].hits").value(6));

    verify(statsService, times(1)).getStats(any(LocalDateTime.class), any(LocalDateTime.class),
            eq(List.of("/events/1")), eq(false));
  }

  @Test
  void getStats_WithoutUrisAndUnique_ShouldReturnAllStats() throws Exception {
    // Given
    LocalDateTime start = LocalDateTime.now().minusHours(3);
    LocalDateTime end = LocalDateTime.now().plusHours(1);

    List<ViewStatsDto> stats = List.of(
            new ViewStatsDto("ewm-main-service", "/events/1", 5L),
            new ViewStatsDto("ewm-main-service", "/events/2", 3L)
    );

    when(statsService.getStats(any(LocalDateTime.class), any(LocalDateTime.class),
            eq(null), eq(false))).thenReturn(stats);

    // When & Then
    mockMvc.perform(get("/stats")
                    .param("start", start.format(formatter))
                    .param("end", end.format(formatter)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2));

    verify(statsService, times(1)).getStats(any(LocalDateTime.class), any(LocalDateTime.class),
            eq(null), eq(false));
  }

  @Test
  void getStats_WithUniqueTrue_ShouldReturnUniqueStats() throws Exception {
    // Given
    LocalDateTime start = LocalDateTime.now().minusDays(7);
    LocalDateTime end = LocalDateTime.now();

    List<ViewStatsDto> stats = List.of(viewStatsDto);

    when(statsService.getStats(any(LocalDateTime.class), any(LocalDateTime.class),
            eq(null), eq(true))).thenReturn(stats);

    // When & Then
    mockMvc.perform(get("/stats")
                    .param("start", start.format(formatter))
                    .param("end", end.format(formatter))
                    .param("unique", "true"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].hits").value(6));

    verify(statsService, times(1)).getStats(any(LocalDateTime.class), any(LocalDateTime.class),
            eq(null), eq(true));
  }

  @Test
  void getStats_WithoutRequiredParams_ShouldReturn400() throws Exception {
    // When & Then
    mockMvc.perform(get("/stats"))
            .andExpect(status().isBadRequest());
  }

  @Test
  void getStats_WithInvalidDateFormat_ShouldReturn400() throws Exception {
    // When & Then
    mockMvc.perform(get("/stats")
                    .param("start", "invalid-date")
                    .param("end", "2025-11-04 12:00:00"))
            .andExpect(status().isBadRequest());
  }
}