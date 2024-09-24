package com.dnsserver.message;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.StringJoiner;

public class Domain {

  private Domain() {
  }

  public static byte[] encodeDomainName(String domain) {
    var out = new ByteArrayOutputStream();

    for (String label : domain.split("\\.")) {
      out.write(label.length());
      out.writeBytes(label.getBytes());
    }

    out.write(0);

    return out.toByteArray();
  }

  public static String decodeDomainName(ByteBuffer buffer) {

    // Compressed message format (two octet)
    // +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    // | 1  1|                OFFSET                   |
    // +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+

    boolean compressed = false;

    var labels = new StringJoiner(".");
    byte b;
    while ((b = buffer.get()) != 0) {
      if ((b & 0b1100_0000) == 0b1100_0000) {
        compressed = true;
        int pointer = (0b0011_1111 & b) << 8 | (buffer.get() & 0xFF);
        int currentPosition = buffer.position();
        buffer.position(pointer);
        labels.add(decodeDomainName(buffer));
        buffer.position(currentPosition);
      } else {
        byte[] dst = new byte[b];
        buffer.get(dst);
        labels.add(new String(dst));
      }
    }

    if (compressed) {
      buffer.position(buffer.position() - 1);
    }
    return labels.toString();
  }
}
