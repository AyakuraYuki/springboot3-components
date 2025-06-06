package cc.ayakurayuki.spring.components.context.rpc;

import com.google.common.base.Strings;
import io.grpc.Metadata;

public class RpcHeaders {

  public static final String USER_AGENT      = "user-agent";
  // gzip
  public static final String ACCEPT_ENCODING = "grpc-accept-encoding";
  public static final String CONTENT_TYPE    = "content-type";
  // 199982u  微秒
  public static final String TIMEOUT         = "grpc-timeout";

  public static final String REMOTE_IP = "remote_ip";

  // slb x real ip
  public static final String X_REAL_IP = "x-real-ip";

  public static final String CALLER = "caller";

  public static final String MIRROR = "mirror";

  public static final String PATH = "path";

  // Propagator Header - TraceID
  public static final String AY_MICRO_TRACE_HEADER_TRACE_ID     = "aymicro-trace-id";
  // Propagator Header - TraceParent
  public static final String AY_MICRO_TRACE_HEADER_TRACE_PARENT = "aymicro-trace-parent";
  // Propagator Header - SpanID
  public static final String AY_MICRO_TRACE_HEADER_SPAN_ID      = "aymicro-span-id";

  public static final Metadata.Key<Long> CPU = Metadata.Key.of("cpu_usage", new Metadata.AsciiMarshaller<>() {
    @Override
    public String toAsciiString(Long value) {
      return String.valueOf(value == null ? 1 : value);
    }

    @Override
    public Long parseAsciiString(String serialized) {
      return Long.parseLong(Strings.isNullOrEmpty(serialized) ? "1" : serialized);
    }
  });

}
