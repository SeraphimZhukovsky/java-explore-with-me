package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "hits")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hit {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "app", nullable = false)
  private String app;

  @Column(name = "uri", nullable = false, length = 512)
  private String uri;

  @Column(name = "ip", nullable = false, length = 45)
  private String ip;

  @Column(name = "created", nullable = false)
  private LocalDateTime timestamp;

  @Override
  public String toString() {
    return "Hit{" +
            "id=" + id +
            ", app='" + app + '\'' +
            ", uri='" + uri + '\'' +
            ", ip='" + ip + '\'' +
            ", timestamp=" + timestamp +
            '}';
  }
}