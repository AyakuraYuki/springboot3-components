package cc.ayakurayuki.spring.components.rpc.server.exception;

import io.grpc.Metadata;
import io.grpc.Status;

public record WrappedStatus(
    Status status,
    Metadata trailers
) {}
