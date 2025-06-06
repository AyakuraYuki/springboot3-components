package cc.ayakurayuki.spring.components.utility.http;

import cc.ayakurayuki.spring.components.utility.annotation.Beta;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;

/**
 * Wrap JDK standard HttpClient
 *
 * @author Ayakura Yuki
 */
@Slf4j
public final class HttpUtils {

  /**
   * 获取新的 HttpClient 实例
   * <p>
   * 默认设置连接超时时间 30 秒
   * <p>
   * 一般来说不需要手动创建 HttpClient 实例，下面提供的 execute 方法均自动创建实例
   * <p>
   * 但是如果你需要对 HttpClient 实例自定义参数，则可以使用 {@link #newBuilder()} 获得一个新的 Client Builder 自定义实例构造参数。
   */
  public static HttpClient newClient() {
    return HttpClient.newBuilder()
        .version(Version.HTTP_1_1)
        .connectTimeout(Duration.ofSeconds(30))
        .followRedirects(Redirect.ALWAYS)
        .build();
  }

  /**
   * 获取新的 HttpClient 实例，并且支持设置连接超时时间
   * <p>
   * 一般来说不需要手动创建 HttpClient 实例，下面提供的 execute 方法均自动创建实例
   * <p>
   * 但是如果你需要对 HttpClient 实例自定义参数，则可以使用 {@link #newBuilder()} 获得一个新的 Client Builder 自定义实例构造参数。
   *
   * @param connectTimeout 自定义的连接超时时间，默认超时为 30 秒
   */
  public static HttpClient newClient(Duration connectTimeout) {
    if (connectTimeout == null) {
      connectTimeout = Duration.ofSeconds(30);
    }
    return HttpClient.newBuilder()
        .version(Version.HTTP_1_1)
        .connectTimeout(connectTimeout)
        .followRedirects(Redirect.ALWAYS)
        .build();
  }

  /**
   * 获取 HttpClient 实例的构造器，以供自定义 HttpClient 实例参数
   */
  public static HttpClient.Builder newBuilder() {
    return HttpClient.newBuilder()
        .version(Version.HTTP_1_1)
        .connectTimeout(Duration.ofSeconds(30))
        .followRedirects(Redirect.ALWAYS);
  }

  /**
   * 获取一个做了基础包装的 HttpRequest 构造器
   */
  public static HttpRequest.Builder wrappedRequest() {
    return HttpRequest.newBuilder()
        .header(HttpHeaders.USER_AGENT, "AYMicroHTTPClient/1.0");
  }

  /**
   * 利用 {@link BodyPublisher} 包装 form 表单格式的请求数据
   * <p>
   * 表单中如果有列表参数的，需要在外部将列表转换成用英文逗号拼接的字符串
   * <p>
   * 该包装方法会对 key/value 做一次 URLEncode 编码。如果你不需要 URLEncode 转码请求内容，可以使用 {@link #ofRawFormData(Map)} 包装请求数据。
   *
   * @param data 表单参数
   */
  public static BodyPublisher ofFormData(Map<String, String> data) {
    if (data == null || data.isEmpty()) {
      return BodyPublishers.noBody();
    }
    StringBuilder bodyBuilder = new StringBuilder();
    data.forEach((key, value) -> {
      String k = URLEncoder.encode(key, StandardCharsets.UTF_8);
      String v = URLEncoder.encode(value, StandardCharsets.UTF_8);
      bodyBuilder.append(k).append("=").append(v).append("&");
    });
    String body = bodyBuilder.substring(0, bodyBuilder.length() - 1);
    log.info("request body: {}", body);
    return BodyPublishers.ofString(body);
  }

  /**
   * 利用 {@link BodyPublisher} 包装 form 表单格式的请求数据
   * <p>
   * 表单中如果有列表参数的，需要在外部将列表转换成用英文逗号拼接的字符串
   * <p>
   * 该包装方法会按原样构造请求 body。
   *
   * @param data 表单参数
   */
  public static BodyPublisher ofRawFormData(Map<String, String> data) {
    if (data == null || data.isEmpty()) {
      return BodyPublishers.noBody();
    }
    StringBuilder bodyBuilder = new StringBuilder();
    data.forEach((key, value) -> bodyBuilder.append(key)
        .append("=")
        .append(value)
        .append("&"));
    String body = bodyBuilder.substring(0, bodyBuilder.length() - 1);
    log.info("request body: {}", body);
    return BodyPublishers.ofString(body);
  }

  /**
   * 发起 HTTP 请求，获取文本格式的响应数据
   *
   * @param request 请求对象
   *
   * @return 响应数据的文本格式结果
   */
  public static String execute(HttpRequest request) {
    return execute(newClient(), request);
  }

  /**
   * 发起 HTTP 请求，获取文本格式的响应数据
   *
   * @param client  自定义的 HttpClient 实例
   * @param request 请求对象
   *
   * @return 响应数据的文本格式结果
   */
  public static String execute(HttpClient client, HttpRequest request) {
    if (client == null) {
      client = newClient();
    }
    try {
      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
      return response.body();
    } catch (IOException | InterruptedException e) {
      log.error(e.getMessage(), e);
      return "";
    }
  }

  /**
   * 发起 HTTP 请求，返回原始的 HttpResponse 对象，其中的响应数据是文本格式的内容
   *
   * @param request 请求对象
   *
   * @return 包含了文本格式响应结果的 Response 对象
   */
  public static HttpResponse<String> executeWithStringResponse(HttpRequest request) {
    return executeWithStringResponse(newClient(), request);
  }

  /**
   * 发起 HTTP 请求，返回原始的 HttpResponse 对象，其中的响应数据是文本格式的内容
   *
   * @param client  自定义的 HttpClient 实例
   * @param request 请求对象
   *
   * @return 包含了文本格式响应结果的 Response 对象
   */
  public static HttpResponse<String> executeWithStringResponse(HttpClient client, HttpRequest request) {
    if (client == null) {
      client = newClient();
    }
    try {
      return client.send(request, BodyHandlers.ofString());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  /**
   * 发起异步 HTTP 请求，返回原始的 HttpResponse 对象，其中的响应数据是文本格式的内容
   *
   * @param request 请求对象
   *
   * @return 包含了文本格式响应结果的 Response 对象
   */
  public static CompletableFuture<HttpResponse<String>> executeAsync(HttpRequest request) {
    return executeAsync(newClient(), request);
  }

  /**
   * 发起异步 HTTP 请求，返回原始的 HttpResponse 对象，其中的响应数据是文本格式的内容
   *
   * @param client  自定义的 HttpClient 实例
   * @param request 请求对象
   *
   * @return 包含了文本格式响应结果的 Response 对象
   */
  public static CompletableFuture<HttpResponse<String>> executeAsync(HttpClient client, HttpRequest request) {
    if (client == null) {
      client = newClient();
    }
    return client.sendAsync(request, BodyHandlers.ofString());
  }

  /**
   * 发起 HTTP 请求，获取文本格式的响应数据，并且在遇到异常时直接抛出
   *
   * @param request 请求对象
   *
   * @return 响应数据的文本格式结果
   */
  public static String executeWithException(HttpRequest request) throws IOException, InterruptedException {
    return executeWithException(newClient(), request);
  }

  /**
   * 发起 HTTP 请求，获取文本格式的响应数据，并且在遇到异常时直接抛出
   *
   * @param client  自定义的 HttpClient 实例
   * @param request 请求对象
   *
   * @return 响应数据的文本格式结果
   */
  public static String executeWithException(HttpClient client, HttpRequest request) throws IOException, InterruptedException {
    if (client == null) {
      client = newClient();
    }
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    return response.body();
  }

  /**
   * 发起 HTTP 请求，获取字节格式的响应数据
   *
   * @param request 请求对象
   *
   * @return 响应数据的字节格式字节数组
   */
  public static byte[] executeForByteArray(HttpRequest request) {
    return executeForByteArray(newClient(), request);
  }

  /**
   * 发起 HTTP 请求，获取字节格式的响应数据
   *
   * @param client  自定义的 HttpClient 实例
   * @param request 请求对象
   *
   * @return 响应数据的字节格式字节数组
   */
  public static byte[] executeForByteArray(HttpClient client, HttpRequest request) {
    if (client == null) {
      client = newClient();
    }
    try {
      HttpResponse<byte[]> response = client.send(request, BodyHandlers.ofByteArray());
      return response.body();
    } catch (IOException | InterruptedException e) {
      log.error(e.getMessage(), e);
      return new byte[0];
    }
  }

  /**
   * 发起异步 HTTP 请求，返回原始的 HttpResponse 对象，其中的响应数据是字节格式的内容
   *
   * @param request 请求对象
   *
   * @return 包含了字节格式响应结果的 Response 对象
   */
  public static CompletableFuture<HttpResponse<byte[]>> executeForByteArrayAsync(HttpRequest request) {
    return executeForByteArrayAsync(newClient(), request);
  }

  /**
   * 发起异步 HTTP 请求，返回原始的 HttpResponse 对象，其中的响应数据是字节格式的内容
   *
   * @param client  自定义的 HttpClient 实例
   * @param request 请求对象
   *
   * @return 包含了字节格式响应结果的 Response 对象
   */
  public static CompletableFuture<HttpResponse<byte[]>> executeForByteArrayAsync(HttpClient client, HttpRequest request) {
    if (client == null) {
      client = newClient();
    }
    return client.sendAsync(request, BodyHandlers.ofByteArray());
  }

  /**
   * 发起 HTTP 请求，获取输入流对象
   *
   * @param request 请求对象
   *
   * @return 可以读取响应数据输入流的对象
   */
  public static InputStream executeForInputStream(HttpRequest request) {
    return executeForInputStream(newClient(), request);
  }

  /**
   * 发起 HTTP 请求，获取输入流对象
   *
   * @param client  自定义的 HttpClient 实例
   * @param request 请求对象
   *
   * @return 可以读取响应数据输入流的对象
   */
  public static InputStream executeForInputStream(HttpClient client, HttpRequest request) {
    if (client == null) {
      client = newClient();
    }
    try {
      HttpResponse<InputStream> response = client.send(request, BodyHandlers.ofInputStream());
      return response.body();
    } catch (IOException | InterruptedException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  /**
   * 发起异步 HTTP 请求，返回原始的 HttpResponse 对象，包含了输入流对象
   *
   * @param request 请求对象
   *
   * @return 包含了输入流对象的 Response 对象
   */
  public static CompletableFuture<HttpResponse<InputStream>> executeForInputStreamAsync(HttpRequest request) {
    return executeForInputStreamAsync(newClient(), request);
  }

  /**
   * 发起异步 HTTP 请求，返回原始的 HttpResponse 对象，包含了输入流对象
   *
   * @param client  自定义的 HttpClient 实例
   * @param request 请求对象
   *
   * @return 包含了输入流对象的 Response 对象
   */
  public static CompletableFuture<HttpResponse<InputStream>> executeForInputStreamAsync(HttpClient client, HttpRequest request) {
    if (client == null) {
      client = newClient();
    }
    return client.sendAsync(request, BodyHandlers.ofInputStream());
  }

  /**
   * 发起 HTTP 请求，返回原始 HttpResponse 对象
   *
   * @param request     请求对象
   * @param bodyHandler 响应结果的处理器
   * @param <T>         响应数据的类型
   *
   * @return 包含了使用指定的处理器解析的响应结果的 Response 对象
   */
  @Beta
  public static <T> HttpResponse<T> executeWithResponse(HttpRequest request, BodyHandler<T> bodyHandler) {
    return executeWithResponse(newClient(), request, bodyHandler);
  }

  /**
   * 发起 HTTP 请求，返回原始 HttpResponse 对象
   *
   * @param client      自定义的 HttpClient 实例
   * @param request     请求对象
   * @param bodyHandler 响应结果的处理器
   * @param <T>         响应数据的类型
   *
   * @return 包含了使用指定的处理器解析的响应结果的 Response 对象
   */
  @Beta
  public static <T> HttpResponse<T> executeWithResponse(HttpClient client, HttpRequest request, BodyHandler<T> bodyHandler) {
    if (client == null) {
      client = newClient();
    }
    try {
      return client.send(request, bodyHandler);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  /**
   * 发起异步 HTTP 请求，返回原始的 Response 对象
   *
   * @param request     请求对象
   * @param bodyHandler 响应结果的处理器
   * @param <T>         响应数据的类型
   *
   * @return 原始的 Response 对象
   */
  @Beta
  public static <T> CompletableFuture<HttpResponse<T>> executeWithResponseAsync(HttpRequest request, BodyHandler<T> bodyHandler) {
    return executeWithResponseAsync(newClient(), request, bodyHandler);
  }

  /**
   * 发起异步 HTTP 请求，返回原始的 Response 对象
   *
   * @param client      自定义的 HttpClient 实例
   * @param request     请求对象
   * @param bodyHandler 响应结果的处理器
   * @param <T>         响应数据的类型
   *
   * @return 原始的 Response 对象
   */
  @Beta
  public static <T> CompletableFuture<HttpResponse<T>> executeWithResponseAsync(HttpClient client, HttpRequest request, BodyHandler<T> bodyHandler) {
    if (client == null) {
      client = newClient();
    }
    return client.sendAsync(request, bodyHandler);
  }

}
