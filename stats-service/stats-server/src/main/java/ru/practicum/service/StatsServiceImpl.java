package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndPointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.Hit;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

  private final StatsRepository statsRepository;

  @Override
  @Transactional
  public void saveHit(EndPointHitDto endpointHitDto) {
    Hit hit = Hit.builder()
            .app(endpointHitDto.getApp())
            .uri(endpointHitDto.getUri())
            .ip(endpointHitDto.getIp())
            .timestamp(endpointHitDto.getTimestamp())
            .build();
    statsRepository.save(hit);
  }

  @Override
  public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end,
                                     List<String> uris, Boolean unique) {
    if (start != null && end != null && start.isAfter(end)) {
      throw new IllegalArgumentException("Start date must be before end date");
    }
    boolean isUnique = Boolean.TRUE.equals(unique);
    boolean hasUris = uris != null && !uris.isEmpty();

    if (!hasUris) {
      if (isUnique) {
        return statsRepository.findUniqueStats(start, end);
      } else {
        return statsRepository.findStats(start, end);
      }
    } else {
      if (isUnique) {
        return statsRepository.findUniqueStatsByUris(start, end, uris);
      } else {
        return statsRepository.findStatsByUris(start, end, uris);
      }
    }
  }
}