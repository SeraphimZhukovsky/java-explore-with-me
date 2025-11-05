package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.EndPointHitDto;
import ru.practicum.dto.ViewStatsDto;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
    super(builder
            .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
            .requestFactory(SimpleClientHttpRequestFactory::new)
            .build()
    );
  }

  public void saveHit(EndPointHitDto endpointHit) {
    post("/hit", endpointHit);
  }

  public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("start", start.format(FORMATTER));
    parameters.put("end", end.format(FORMATTER));
    parameters.put("unique", unique != null ? unique : false);

    String path = "/stats?start={start}&end={end}&unique={unique}";

    if (uris != null && !uris.isEmpty()) {
      parameters.put("uris", String.join(",", uris));
      path += "&uris={uris}";
    }

    ResponseEntity<ViewStatsDto[]> response = get(path, parameters, ViewStatsDto[].class);
    ViewStatsDto[] body = response.getBody();

    return body != null ? Arrays.asList(body) : List.of();
  }
}