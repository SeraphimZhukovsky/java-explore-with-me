package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewStatsDto {
  private String app;
  private String uri;
  private Long hits;

  @Override
  public String toString() {
    return "ViewStatsDto{" +
            "app='" + app + '\'' +
            ", uri='" + uri + '\'' +
            ", hits=" + hits +
            '}';
  }
}