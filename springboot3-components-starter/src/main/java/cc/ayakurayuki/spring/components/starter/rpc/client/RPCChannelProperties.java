package cc.ayakurayuki.spring.components.starter.rpc.client;

import io.grpc.Deadline;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RPCChannelProperties {

  public static final RPCChannelProperties DEFAULT = new RPCChannelProperties();

  /**
   * Use plaintext mode. Default to {@code true}.
   */
  private boolean plaintext = true;

  /**
   * Enable keepalive. Default to {@code true}.
   */
  private boolean enableKeepAlive = true;

  /**
   * Sets whether keepalive will be performed when there are no outstanding RPC on a connection. Default to {@code false}.
   */
  private boolean keepAliveWithoutCalls = false;

  /**
   * The default delay in milliseconds before we send a keepalive. Defaults to {@code 1000}
   */
  private long keepAliveTime = 1000;

  /**
   * The default timeout in milliseconds for a keepalive ping request. Defaults to {@code 1000}
   */
  private long keepAliveTimeout = 1000;

  /**
   * The timeout in milliseconds for connect Defaults to {@code 100}
   */
  private int connectTimeout = 100;


  /**
   * The default timeout in seconds for idle Defaults to {@code 60}
   */
  private long idleTimeout = 60;


  /**
   * Sets the HTTP/2 flow control window. The default value is 8m
   */
  private int flowControlWindow = 1024 * 1024 * 8;

  /**
   * Sets the maximum size of metadata allowed to be received. The default is 8 KiB.
   */
  private int maxInboundMetadataSize = 8192;

  /**
   * Sets the maximum message size allowed for a single gRPC frame. Defaults to 4 MiB
   */
  private int maxInboundMessageSize = 4 * 1024 * 1024;

  /**
   * Setting if service is optional Defaults to {@code false}
   */
  private boolean optional;

  /**
   * The instance node subset size, service instance node size will be trim to subset size Defaults to {@code 30}
   */
  private int subset = 30;

  /**
   * The request context deadline timeout in milliseconds
   *
   * @see io.grpc.CallOptions#withDeadline(Deadline) Defaults to {@code 200}
   */
  private long callTimeout = 200;

  /**
   * The direct url {@code ip:port} for client connection. If direct url was set, naming resolver will disabled, all rpc calls will sent to the direct endpoint.
   */
  private String direct;

  /**
   * The naming zone
   */
  private String zone;

  /**
   * Setting to enable naming zone scheduler. Defaults to {@code true}
   */
  private boolean scheduler = true;

}
