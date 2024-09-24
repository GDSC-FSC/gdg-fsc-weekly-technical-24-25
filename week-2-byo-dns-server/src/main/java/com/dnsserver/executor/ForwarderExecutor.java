package com.dnsserver.executor;

import com.dnsserver.message.Answer;
import com.dnsserver.message.DnsMessage;
import com.dnsserver.message.Header;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ForwarderExecutor implements RequestExecutor {

  private static final int BUFFER_SIZE = 512;

  private final DatagramSocket socket;
  private final SocketAddress remoteAddress;

  public ForwarderExecutor(DatagramSocket socket, String forwardAddress) {
    this.socket = Objects.requireNonNull(socket);
    remoteAddress = getSocketAddress(forwardAddress);
  }

  @Override
  public void send(byte[] data, SocketAddress address) throws IOException {

    var message = DnsMessage.from(data);

    List<Answer> answers = new ArrayList<>();

    // Remote server does not support multi question, so we need to send individual requests
    // and then aggregate decoded answer results

    for (var question : message.questions()) {
      var singleBuff = ByteBuffer.allocate(BUFFER_SIZE);
      message.header().encode(singleBuff);
      question.encode(singleBuff);

      // Send to remote server
      var rBytes = singleBuff.array();
      var reqPacket = new DatagramPacket(rBytes, rBytes.length, remoteAddress);
      socket.send(reqPacket);

      // Read from remote server
      singleBuff = ByteBuffer.allocate(BUFFER_SIZE);
      rBytes = singleBuff.array();

      var respPacket = new DatagramPacket(rBytes, rBytes.length, remoteAddress);

      socket.receive(respPacket);

      answers.addAll(DnsMessage.from(rBytes).answers());
    }

   // Send response to local client

    var rBuff = new DnsMessage(
        responseHeader(message),
        message.questions(),
        answers
    ).decode();

    var localPacket = new DatagramPacket(rBuff, rBuff.length, address);
    socket.send(localPacket);
  }


  private SocketAddress getSocketAddress(String address) {
    var parts = address.split(":");
    if (parts.length != 2) {
      throw new IllegalArgumentException("Invalid address");
    }

    return new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
  }

  private Header responseHeader(DnsMessage message) {
    var requestHeader = message.header();
    return new Header(
        requestHeader.packetIdentifier(),
        true,
        requestHeader.operationCode(),
        requestHeader.authoritativeAnswer(),
        requestHeader.truncation(),
        requestHeader.recursionDesired(),
        requestHeader.recursionAvailable(),
        requestHeader.reserved(),
        requestHeader.responseCode(),
        (short) message.questions().size(),
        (short)message.questions().size(),
        requestHeader.authorityRecordCount(),
        requestHeader.additionalRecordCount()
    );
  }
}
