package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndPointHitDto {
  private Long id;

  @NotBlank(message = "App cannot be blank")
  private String app;

  @NotBlank(message = "URI cannot be blank")
  private String uri;

  @NotBlank(message = "IP cannot be blank")
  private String ip;

  @NotNull(message = "Timestamp cannot be null")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime timestamp;

  public EndPointHitDto(String app, String uri, String ip, LocalDateTime timestamp) {
    this.app = app;
    this.uri = uri;
    this.ip = ip;
    this.timestamp = timestamp;
  }

  @Override
  public String toString() {
    return "EndPointHitDto{" +
            "id=" + id +
            ", app='" + app + '\'' +
            ", uri='" + uri + '\'' +
            ", ip='" + ip + '\'' +
            ", timestamp=" + timestamp +
            '}';
  }
}
