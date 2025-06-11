package cc.ayakurayuki.spring.components.rpc.client;

import io.grpc.CallOptions;
import java.time.Duration;

public class CallOptionKeys {

  public static final CallOptions.Key<Duration> TIMEOUT_KEY = CallOptions.Key.create("cc.ayakurayuki.spring.components.rpc.client.timeout");

}
