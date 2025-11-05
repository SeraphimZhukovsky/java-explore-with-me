package ru.practicum.client;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class BaseClient {
  protected final RestTemplate rest;

  public BaseClient(RestTemplate rest) {
    this.rest = rest;
  }

  protected ResponseEntity<Object> get(String path) {
    return get(path, null, Object.class);
  }

  protected <T> ResponseEntity<T> get(String path, @Nullable Map<String, Object> parameters, Class<T> responseType) {
    return makeAndSendRequest(HttpMethod.GET, path, parameters, null, responseType);
  }

  protected ResponseEntity<Object> post(String path, Object body) {
    return post(path, null, body, Object.class);
  }

  protected <T> ResponseEntity<T> post(String path, @Nullable Map<String, Object> parameters, Object body, Class<T> responseType) {
    return makeAndSendRequest(HttpMethod.POST, path, parameters, body, responseType);
  }

  private <T> ResponseEntity<T> makeAndSendRequest(HttpMethod method, String path,
                                                   @Nullable Map<String, Object> parameters,
                                                   @Nullable Object body,
                                                   Class<T> responseType) {
    HttpEntity<Object> requestEntity = new HttpEntity<>(body, defaultHeaders());

    ResponseEntity<T> response;
    try {
      if (parameters != null) {
        response = rest.exchange(path, method, requestEntity, responseType, parameters);
      } else {
        response = rest.exchange(path, method, requestEntity, responseType);
      }
    } catch (HttpStatusCodeException e) {
      return ResponseEntity.status(e.getStatusCode()).build();
    }
    return response;
  }

  private HttpHeaders defaultHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    return headers;
  }
}