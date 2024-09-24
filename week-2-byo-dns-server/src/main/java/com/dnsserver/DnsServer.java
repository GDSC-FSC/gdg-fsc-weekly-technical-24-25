package com.dnsserver;

import com.dnsserver.executor.LocalExecutor;
import com.dnsserver.executor.ForwarderExecutor;
import com.dnsserver.executor.RequestExecutor;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DnsServer implements AutoCloseable {

  private final Logger logger = Logger.getLogger(DnsServer.class.getName());

  private int port = 0;
  private String forwarderAddress = null;
  private DatagramSocket serverSocket;

  public DnsServer(int port, String forwarderAddress) {
    this.port = port;
    this.forwarderAddress = forwarderAddress;
  }

  public void start() throws IOException {
    serverSocket = new DatagramSocket(port);

    RequestExecutor executor = getExecutor(serverSocket, forwarderAddress);

    while (!serverSocket.isClosed()) {
      try {
        final byte[] buf = new byte[512];
        final DatagramPacket packet = new DatagramPacket(buf, buf.length);

        serverSocket.receive(packet);

        executor.send(buf, packet.getSocketAddress());

      } catch (IOException e) {
        logger.log(Level.SEVERE, "Failed to process request: {0}", e.getMessage());
      }
    }
  }

  private RequestExecutor getExecutor(DatagramSocket socket, String forwarder) {
    if (forwarder != null) {
      return new ForwarderExecutor(socket, forwarderAddress);
    }
    return new LocalExecutor(serverSocket);
  }

  @Override
  public void close() throws IOException {
    if (!serverSocket.isClosed()) {
      serverSocket.close();
    }
  }
}
