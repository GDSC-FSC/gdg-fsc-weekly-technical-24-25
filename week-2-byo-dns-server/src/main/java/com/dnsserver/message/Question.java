package com.dnsserver.message;

import java.nio.ByteBuffer;

public record Question(String domain, short QType, short QClass) {

  public void encode(ByteBuffer buffer) {
    buffer.put(Domain.encodeDomainName(domain));
    buffer.putShort(QType);
    buffer.putShort(QClass);
  }

  public static Question parse(ByteBuffer buffer) {
    var encodedName = Domain.decodeDomainName(buffer);
    return new Question(
        encodedName,
        buffer.getShort(),
        buffer.getShort()
    );
  }
}
