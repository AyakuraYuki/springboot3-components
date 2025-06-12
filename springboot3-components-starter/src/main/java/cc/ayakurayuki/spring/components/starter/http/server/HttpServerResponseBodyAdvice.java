package cc.ayakurayuki.spring.components.starter.http.server;

import cc.ayakurayuki.spring.components.http.server.response.JsonRsp;
import cc.ayakurayuki.spring.components.http.server.utility.HttpRequestAttributes;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.Nonnull;
import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class HttpServerResponseBodyAdvice implements ResponseBodyAdvice<Object> {

  @Override
  public boolean supports(@Nonnull MethodParameter returnType, @Nonnull Class<? extends HttpMessageConverter<?>> converterType) {
    return true;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Object beforeBodyWrite(Object body,
                                @Nonnull MethodParameter returnType,
                                @Nonnull MediaType selectedContentType,
                                @Nonnull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                @Nonnull ServerHttpRequest request,
                                @Nonnull ServerHttpResponse response) {
    int code = 0;
    String message = "";

    if (body != null) {
      code = switch (body) {
        case JsonRsp rsp -> {
          message = rsp.getMessage();
          yield rsp.getStatus();
        }

        case ObjectNode node -> {
          if (node.has("message")) {
            message = node.get("message").asText();
          } else if (node.has("msg")) {
            message = node.get("msg").asText();
          }

          if (node.has("code")) {
            yield node.get("code").asInt();
          } else if (node.has("status")) {
            yield node.get("status").asInt();
          }

          yield 0;
        }

        case Map map -> {
          try {
            if (map.containsKey("message")) {
              message = String.valueOf(map.get("message"));
            } else if (map.containsKey("msg")) {
              message = String.valueOf(map.get("msg"));
            }
          } catch (Exception ignored) {
          }

          try {
            if (map.containsKey("code")) {
              yield Integer.parseInt(String.valueOf(map.get("code")));
            } else if (map.containsKey("status")) {
              yield Integer.parseInt(String.valueOf(map.get("status")));
            }
          } catch (Exception ignored) {
          }
          yield 0;
        }

        default -> 0;
      };
    }

    HttpRequestAttributes.RESPONSE_CODE.setAttribute(code);
    HttpRequestAttributes.RESPONSE_MESSAGE.setAttribute(message);

    return body;
  }

}
