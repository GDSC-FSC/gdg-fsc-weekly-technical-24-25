package com.dnsserver.message;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.junit.jupiter.api.Test;

public class HeaderTest {

  @Test
  void parse() {

    var buff = ByteBuffer.allocate(12).order(ByteOrder.BIG_ENDIAN);
    buff.putShort((short)1234);
    buff.put((byte) 0b10000001);
    buff.put((byte) 0b10000000);
    buff.putShort((short) 1);
    buff.putShort((short) 1);
    buff.putShort((short) 0);
    buff.putShort((short) 0);

    var header = Header.parse(ByteBuffer.wrap(buff.array()));

    assertEquals(1234, header.packetIdentifier());
    assertEquals(true, header.queryResponseIndicator());
    assertEquals(0, header.operationCode());
    assertEquals(false, header.authoritativeAnswer());
    assertEquals(false, header.truncation());
    assertEquals(true, header.recursionDesired());
    assertEquals(true, header.recursionAvailable());
    assertEquals(0, header.reserved());
    assertEquals(0, header.responseCode());
    assertEquals(1, header.questionCount());
    assertEquals(1, header.answerRecordCount());
    assertEquals(0, header.authorityRecordCount());
    assertEquals(0, header.additionalRecordCount());
  }

  @Test
  void parseOpCode() {
    var buff = ByteBuffer.allocate(12).order(ByteOrder.BIG_ENDIAN);
    buff.putShort((short)1234);
    buff.put((byte) 0b10111001); // OpCode 7
    buff.put((byte) 0b10000000);
    buff.putShort((short) 1);
    buff.putShort((short) 1);
    buff.putShort((short) 0);
    buff.putShort((short) 0);

    var header = Header.parse(ByteBuffer.wrap(buff.array()));

    assertEquals(7, header.operationCode());
    assertEquals(4, header.responseCode());

  }

  @Test
  void encode() {
    var header = new Header(
        (short) 1234,
        true,
        (byte) 0b111,
        false,
        false,
        true,
        true,
        (byte) 0b000,
        (byte) 0b0100,
        (short) 1,
        (short) 1,
        (short) 0,
        (short) 0
    );

    var buff = ByteBuffer.allocate(12).order(ByteOrder.BIG_ENDIAN);

    header.encode(buff);

    buff = buff.rewind();

    assertEquals(1234, buff.getShort());
    assertEquals((byte) 0b10111001, buff.get());
    assertEquals((byte) 0b10000100, buff.get());
    assertEquals(1, buff.getShort());
    assertEquals(1, buff.getShort());
    assertEquals(0, buff.getShort());
    assertEquals(0, buff.getShort());

  }
}
