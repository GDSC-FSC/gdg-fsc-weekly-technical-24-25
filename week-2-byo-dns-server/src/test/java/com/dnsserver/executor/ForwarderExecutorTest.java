package com.dnsserver.executor;

import com.dnsserver.message.Header;
import com.dnsserver.message.Question;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.Test;

class ForwarderExecutorTest {

  @Test
  void ExecuteTest() throws Exception {
    String forwarder = "8.8.8.8:53";
    DatagramSocket socket = new DatagramSocket(2053);
    var buf = ByteBuffer.allocate(512);

    var executor = new ForwarderExecutor(socket, forwarder);

    var header = new Header(
        (short) 1234,
        false,
        (byte) 0b0,
        false,
        false,
        true,
        false,
        (byte) 0b0,
        (byte) 0b0,
        (short) 1, // 2 questions
        (short) 0,
        (short) 0,
        (short) 0
    );

    var question = new Question("codecrafters.io", (short) 1, (short) 1);
    var question2 = new Question("stackoverflow.com", (short) 1, (short) 1);

    header.encode(buf);
    question.encode(buf);
    question2.encode(buf);

    executor.send(buf.array(), socket.getLocalSocketAddress());
  }
}
