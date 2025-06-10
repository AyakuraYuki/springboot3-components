package cc.ayakurayuki.spring.components.rpc.server;

import io.grpc.ServerCall;

class NoopListener<ReqT> extends ServerCall.Listener<ReqT> {}
