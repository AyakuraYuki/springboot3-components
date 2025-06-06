package cc.ayakurayuki.spring.components.trace;

/**
 * {@link SpanTags} presents some common span tags for easy to use
 *
 * @author Ayakura Yuki
 */
public class SpanTags {

  /**
   * the software packages (frameworks, modules or components) relative to
   * this span, like "grpc", "django", "JDBI"
   *
   * <p>生成此Span所相关的软件包，框架，类库或模块。如 "grpc", "django", "JDBI"
   */
  public static final String COMPONENT = "component";

  /**
   * database instance name
   * <p>For example, if the `jdbc.url` is "jdbc:mysql://127.0.0.1:3306/customers",
   * the instance name is "customer".
   *
   * <p>数据库实例名称。以Java为例，如果 jdbc.url="jdbc:mysql://127.0.0.1:3306/customers"，
   * 实例名为 "customers"
   */
  public static final String DB_INSTANCE = "db.instance";

  /**
   * a database access statement corresponding to a specific database type
   * <p>For example:
   * <ul>
   *   <li>
   *     For database type {@code db.type="sql"}, the statement could be
   *     {@code "SELECT * FROM user_table"}.
   *   </li>
   *   <li>
   *     For database type {@code db.type="redis"}, the statement could be
   *     {@code "SET my:key 'my_value'"}.
   *   </li>
   * </ul>
   *
   * <p>一个针对给定数据库类型的数据库访问语句。例如，针对数据库类型 db.type="sql"，
   * 语句可能是 "SELECT * FROM user_table"；针对数据库类型为 db.type="redis"，
   * 语句可能是 "SET my:key 'my_value'"
   */
  public static final String DB_STATEMENT = "db.statement";

  /**
   * The database type. For any SQL-compliant databases, the value should be "sql".
   * For other database types, use lowercase database type names such as "cassandra",
   * "hbase", or "redis".
   *
   * <p>数据库类型。对于任何支持SQL的数据库，取值为 "sql"；否则，使用小写的数据类型名称，
   * 如 "cassandra"、"hbase"、"redis"
   */
  public static final String DB_TYPE = "db.type";

  /**
   * database username, like "readonly_user" or "reporting_user"
   *
   * <p>访问数据库的用户名。如 "readonly_user" 或 "reporting_user"
   */
  public static final String DB_USER = "db.user";

  /**
   * set to true, indicating that the entire span fails
   *
   * <p>设置为true，说明整个Span失败。
   * 译者注：Span内发生异常不等于error=true，这里由被监控的应用系统决定
   */
  public static final String ERROR = "error";

  /**
   * http method, like "GET", "POST"
   *
   * <p>Span相关的HTTP请求方法。例如 "GET", "POST"
   */
  public static final String HTTP_METHOD = "http.method";

  /**
   * http response status code
   *
   * <p>Span相关的HTTP返回码。例如 200, 503, 404
   */
  public static final String HTTP_STATUS_CODE = "http.status_code";

  /**
   * business status code included by the HTTP 200 response
   *
   * <p>Span相关的HTTP请求业务code，比如站内请求一般会返回业务code码
   */
  public static final String HTTP_BUS_CODE = "http.bus_code";

  /**
   * an url of handled http request
   *
   * <p>被处理的trace片段锁对应的请求URL。 例如 "https://domain.net/path/to?resource=here"
   */
  public static final String HTTP_URL = "http.url";


  /**
   * The address for message delivery or exchange. For example in Kafka,
   * this tag can be used to store the topic name on both producer and
   * consumer.
   *
   * <p>消息投递或交换的地址。例如，在Kafka中，在生产者或消费者两端，可以使用此tag来存储"topic name"
   */
  public static final String MESSAGE_BUS_DESTINATION = "message_bus.destination";

  /**
   * Remote address, suitable for use on the client side of network calls.
   * The stored content could be "ip:port", "hostname", domain name, or even
   * a JDBC connection string such as "mysql://prod-db:3306".
   *
   * <p>远程地址。适合在网络调用的客户端使用。存储的内容可能是"ip:port"，"hostname"，
   * 域名，甚至是一个JDBC的连接串，如 "mysql://prod-db:3306"
   */
  public static final String PEER_ADDRESS = "peer.address";

  /**
   * Remote hostname like "opentracing.io" or "internal.dns.name".
   *
   * <p>远端主机名。例如 "opentracing.io", "internal.dns.name"
   */
  public static final String PEER_HOSTNAME = "peer.hostname";

  /**
   * Remote IPv4 address
   *
   * <p>远端 IPv4 地址。例如 "127.0.0.1"
   */
  public static final String PEER_IPV4 = "peer.ipv4";

  /**
   * Remote IPv6 address
   *
   * <p>远程 IPv6 地址，使用冒号分隔的元祖，每个元素为4位16进制数。例如 "2001:0db8:85a3:0000:0000:8a2e:0370:7334"
   */
  public static final String PEER_IPV6 = "peer.ipv6";

  /**
   * Remote port
   *
   * <p>远程端口。如 80。
   */
  public static final String PEER_PORT = "peer.port";

  /**
   * Remote service name (targeting non-standardized remote service name)
   *
   * <p>远程服务名（针对没有被标准化定义的"service"）。例如 "elasticsearch",
   * "a_custom_microservice", "memcache"
   */
  public static final String PEER_SERVICE = "peer.service";

  /**
   * Invoker's role in different scenes, for example:
   * <ul>
   *   <li>In RPC case, roles are "client", "server".</li>
   *   <li>In message queue case, roles are "producer", "consumer" or others.</li>
   * </ul>
   *
   * <p>基于RPC的调用角色，"client" 或 "server". 基于消息的调用角色，"producer" 或 "consumer"
   */
  public static final String SPAN_KIND = "span.kind";

  /**
   * size of request parameters in bytes
   *
   * <p>请求入参大小
   */
  public static final String SERVER_THROUGH_IN = "server-throughput-in";

  /**
   * size of response body in bytes
   *
   * <p>响应包大小
   */
  public static final String SERVER_THROUGH_OUT = "server-throughput-out";

  /**
   * Propagator Header - TraceID
   */
  public static final String AY_MICRO_TRACE_HEADER_TRACE_ID = "aymicro-trace-id";

  /**
   * Propagator Header - TraceParent
   */
  public static final String AY_MICRO_TRACE_HEADER_TRACE_PARENT = "aymicro-trace-parent";

  /**
   * Propagator Header - SpanID
   */
  public static final String AY_MICRO_TRACE_HEADER_SPAN_ID = "aymicro-span-id";

}
