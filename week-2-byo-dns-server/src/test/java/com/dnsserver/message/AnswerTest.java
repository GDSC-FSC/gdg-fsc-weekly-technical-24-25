package com.dnsserver.message;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;
import org.junit.jupiter.api.Test;

public class AnswerTest {

  @Test
  void parse() {
    var domain = "www.google.com";
    var buf = ByteBuffer.allocate(512);

    new Answer(
        domain,
        (short) 1,
        (short) 1,
        60,
        (short) 4,
        new byte[]{8, 8, 8, 8}
    ).encode(buf);

    var answer = Answer.parse(ByteBuffer.wrap(buf.array()));

    assertEquals(domain, answer.domain());
    assertEquals(1, answer.QType());
    assertEquals(1, answer.QClass());
    assertEquals(60, answer.TTL());
    assertEquals(4, answer.RDLength());
    assertArrayEquals(new byte[]{8, 8, 8, 8}, answer.RData());
  }
}
