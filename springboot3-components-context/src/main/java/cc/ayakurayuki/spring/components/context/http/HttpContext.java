package cc.ayakurayuki.spring.components.context.http;

import cc.ayakurayuki.spring.components.context.Context;
import cc.ayakurayuki.spring.components.context.IP;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Yann
 */
@Slf4j
public final class HttpContext extends Context<String, String[], String, String> {

  private static final Joiner.MapJoiner parameterJoiner = Joiner.on("&").withKeyValueSeparator("=");
  private static final Joiner           joiner          = Joiner.on(",").skipNulls();
  private static final List<String>     excludeParams   = Lists.newArrayList("phone");

  private HttpContext(String patternUrl, HttpServletRequest request) {
    super();
    final String clientIP = request.getRemoteAddr();
    String realIP = this.getRealIp(request);
    int realPort = this.getRealPort(request);
    // http headers metadata
    HashMap<String, String> metadata = new HashMap<>();
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      metadata.put(headerName, request.getHeader(headerName));
    }
    Optional.ofNullable(metadata.get(HttpHeaders.MIRROR))
        .map(StringUtils::isNotEmpty)
        .ifPresent(this::setMirror);
    this.setIp(new IP(clientIP, realIP, realPort));
    this.setParameter(request.getParameterMap());
    this.setCaller(metadata.getOrDefault(HttpHeaders.CALLER, "unknown"));
    this.setPath(patternUrl);
    this.setMetadata(metadata);
    this.setDebugParameter(debugParameters(request));
    try {
      this.setDebugHeader(this.assembleRequestHeadersToQueryString(request));
    } catch (Exception e) {
      log.error("this.setDebugHeader exception!!", e);
    }
  }

  public static HttpContext create(String patternUrl, HttpServletRequest request) {
    return new HttpContext(patternUrl, request);
  }

  public static String assembleParamsToQueryString(Map<String, String> params, String charset) throws UnsupportedEncodingException {
    if (params == null || params.isEmpty()) {
      return null;
    }

    StringBuilder paramString = new StringBuilder();
    boolean first = true;

    for (Iterator<Entry<String, String>> iterator = params.entrySet().iterator(); iterator.hasNext(); first = false) {
      Map.Entry<String, String> p = iterator.next();
      String key = p.getKey();
      String val = p.getValue();
      if (!first) {
        paramString.append("&");
      }
      paramString.append(key);
      paramString.append("=");
      if (val == null) {
        continue;
      }
      if (charset != null && !charset.isEmpty()) {
        String encodedValue = urlEncode(val, charset);
        paramString.append(encodedValue);
      } else {
        paramString.append(val);
      }
    }

    return paramString.toString();
  }

  public static String urlEncode(String value, String charset) throws UnsupportedEncodingException {
    if (value == null) {
      return null;
    }
    return URLEncoder.encode(value, charset)
        .replace("+", "%20")
        .replace("*", "%2A")
        .replace("%7E", "~");
  }

  private String assembleRequestHeadersToQueryString(HttpServletRequest req) throws UnsupportedEncodingException {
    Map<String, String> result = new HashMap<>();
    Enumeration<String> headerNames = req.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      String value = "";
      try {
        Enumeration<String> headers = req.getHeaders(headerName);
        value = joiner.join(headers.asIterator());
      } catch (Exception e) {
        log.warn(String.format("error while building full request headers, fallback to single header value. error: %s", e.getMessage()), e);
        value = req.getHeader(headerName);
      }
      result.put(headerName, value);
    }
    return assembleParamsToQueryString(result, null);
  }

  /**
   * 获取客户端真实的IP
   *
   * @return 客户端的真实IP
   */
  private String getRealIp(HttpServletRequest req) {
    String localIp = "127.0.0.1";
    String ip = req.getHeader(HttpHeaders.X_REAL_IP);
    if (ip == null || ip.trim().isEmpty()) {
      ip = req.getHeader(HttpHeaders.X_FORWARDED_FOR);
    }
    if (ip == null) {
      ip = localIp;
    }
    ip = ip.split(",")[0].trim();
    if (localIp.equals(ip)) {
      return req.getRemoteAddr();
    }
    return ip;
  }

  /**
   * 获取客户端真实的端口
   *
   * @return 客户端真实的端口
   */
  public int getRealPort(HttpServletRequest req) {
    String portString = req.getHeader(HttpHeaders.X_FORWARDED_PORT);
    int port;
    if (StringUtils.isNumeric(portString)) {
      port = Integer.parseInt(portString);
    } else {
      port = req.getRemotePort();
    }
    return port;
  }

  private String debugParameters(HttpServletRequest request) {
    Map<String, String> parameters = request.getParameterMap()
        .entrySet()
        .stream()
        .filter(s -> !excludeParams.contains(s.getKey()))
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            e -> {
              String[] values = e.getValue();
              return joiner.join(values);
            },
            (key1, key2) -> key2
        ));
    return parameterJoiner.join(parameters);
  }

}
