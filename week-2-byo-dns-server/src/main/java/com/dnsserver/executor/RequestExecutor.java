package com.dnsserver.executor;

import java.io.IOException;
import java.net.SocketAddress;

public interface RequestExecutor {
  void send(byte[] data, SocketAddress address) throws IOException;
}


