package com.dnsserver.message;

import java.nio.ByteBuffer;

public record Header(

    // 16 bits
    short packetIdentifier,
    // 1 bit	1 for a reply packet, 0 for a question packet
    boolean queryResponseIndicator,
    // 4 bits
    byte operationCode,
    // 1 bit
    boolean authoritativeAnswer,
    //1 bit
    boolean truncation,
    // 1 bit
    boolean recursionDesired,
    // 1 bit
    boolean recursionAvailable,
    // 3 bits
    byte reserved,
    // 4 bits
    byte responseCode,
    // 16 bits
    short questionCount,
    // 16 bits
    short answerRecordCount,
    // 16 bits
    short authorityRecordCount,
    // 16 bits
    short additionalRecordCount
) {

  public static Header parse(ByteBuffer buffer) {
    final var packetIdentifier = buffer.getShort();
    final var flags = buffer.get();
    final var queryResponseIndicator = (flags & 0b10000000) != 0;
    final var operationCode = (byte) ((flags & 0b01111000) >> 3);
    final var authoritativeAnswer = (flags & 0b00000100) != 0;
    final var truncation = (flags & 0b00000010) != 0;
    final var recursionDesired = (flags & 0b00000001) != 0;

    final var flags2 = buffer.get();
    final var recursionAvailable = (flags2 & 0b10000000) != 0;
    final var reserved = (byte) ((flags2 & 0b01110000) >> 4);

    final var responseCode = operationCode == 0 ? (byte) (flags2 & 0b00001111) : (byte) 0b100;

    final var questionCount = buffer.getShort();
    final var answerRecordCount = buffer.getShort();
    final var authorityRecordCount = buffer.getShort();
    final var additionalRecordCount = buffer.getShort();

    return new Header(
        packetIdentifier,
        queryResponseIndicator,
        operationCode,
        authoritativeAnswer,
        truncation,
        recursionDesired,
        recursionAvailable,
        reserved,
        responseCode,
        questionCount,
        answerRecordCount,
        authorityRecordCount,
        additionalRecordCount
    );
  }


  public void encode(ByteBuffer buffer) {
    // Assuming these fields are class members or parameters passed to the constructor
    buffer.putShort(packetIdentifier);

    buffer.put((byte) (
        ((queryResponseIndicator ? 1 : 0) << 7) |  // QR bit
            ((operationCode & 0x0F) << 3) |            // Opcode (4 bits)
            ((authoritativeAnswer ? 1 : 0) << 2) |     // AA bit
            ((truncation ? 1 : 0) << 1) |              // TC bit
            (recursionDesired ? 1 : 0)                 // RD bit
    ));

    buffer.put((byte) (
        ((recursionAvailable ? 1 : 0) << 7) |      // RA bit
            ((reserved & 0x07) << 4) |                 // Reserved (3 bits)
            (responseCode & 0x0F)                      // RCODE (4 bits)
    ));

    buffer.putShort(questionCount);
    buffer.putShort(answerRecordCount);
    buffer.putShort(authorityRecordCount);
    buffer.putShort(additionalRecordCount);
  }
}

