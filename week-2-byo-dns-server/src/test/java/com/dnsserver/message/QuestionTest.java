package com.dnsserver.message;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;
import org.junit.jupiter.api.Test;

public class QuestionTest {

  @Test
  void encode() {
    var question = new Question("codecrafters.io", (short) 1, (short) 1);
    var questionBuffer = ByteBuffer.allocate(21);
    question.encode(questionBuffer);

    var qBytes = new byte[]{
        12, //Length
        99, 111, 100, 101, 99, 114, 97, 102, 116, 101, 114, 115, // Name
        2, //  Length
        105, 111, // TLD
        0, // End
        0, 1, // QType
        0, 1}; // QClass


    assertArrayEquals(qBytes, questionBuffer.array());
  }

  @Test
  void parse() {

    var qBytes = new byte[]{
        12, //Length
        99, 111, 100, 101, 99, 114, 97, 102, 116, 101, 114, 115, // Name
        2, //  Length
        105, 111, // TLD
        0, // End
        0, 1, // QType
        0, 1}; // QClass

    var question = Question.parse(ByteBuffer.wrap(qBytes));

    assertEquals("codecrafters.io", question.domain());
    assertEquals(1, question.QType());
    assertEquals(1, question.QClass());
  }

  @Test
  void parseCompressed() {

    var qBytes = new byte[]{
        12, // First question
        99, 111, 100, 101, 99, 114, 97, 102, 116, 101, 114, 115, // Name
        2,
        105, 111,
        0,
        0, 1,
        0, 1,
        (byte) 3,// Compressed question
        119, 119,119,  //www
        (byte) 0b1100_0000,
        (byte) 0b0000_0000,
        0,
        0, 1,
        0, 1,
    };

    var buffer = ByteBuffer.wrap(qBytes);

      var question = Question.parse(buffer);
      var compressedQuestion = Question.parse(buffer);

      assertEquals("codecrafters.io", question.domain());
      assertEquals(1, question.QType());
      assertEquals(1, question.QClass());

      assertEquals("www.codecrafters.io", compressedQuestion.domain());
  }
}
