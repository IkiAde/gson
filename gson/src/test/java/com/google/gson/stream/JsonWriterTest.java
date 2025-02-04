/*
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gson.stream;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

import com.google.gson.FormattingStyle;
import com.google.gson.JsonArray;
import com.google.gson.internal.LazilyParsedNumber;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;

import junit.framework.TestCase;

@SuppressWarnings("resource")
public class JsonWriterTest extends TestCase{
  protected StringWriter stringWriter;
  protected JsonWriter jsonWriter;
  @Override
  public void setUp(){
    this.stringWriter= new StringWriter();
    this.jsonWriter= new JsonWriter(this.stringWriter);
  }
  public void testTopLevelValueTypes() throws IOException {
    StringWriter string1 = new StringWriter();
    JsonWriter jsonWriter1 = new JsonWriter(string1);
    jsonWriter1.value(true);
    jsonWriter1.close();
    assertThat(string1.toString()).isEqualTo("true");

    StringWriter string2 = new StringWriter();
    JsonWriter jsonWriter2 = new JsonWriter(string2);
    jsonWriter2.nullValue();
    jsonWriter2.close();
    assertThat(string2.toString()).isEqualTo("null");

    StringWriter string3 = new StringWriter();
    JsonWriter jsonWriter3 = new JsonWriter(string3);
    jsonWriter3.value(123);
    jsonWriter3.close();
    assertThat(string3.toString()).isEqualTo("123");

    StringWriter string4 = new StringWriter();
    JsonWriter jsonWriter4 = new JsonWriter(string4);
    jsonWriter4.value(123.4);
    jsonWriter4.close();
    assertThat(string4.toString()).isEqualTo("123.4");

    StringWriter string5 = new StringWriter();
    JsonWriter jsonWritert = new JsonWriter(string5);
    jsonWritert.value("a");
    jsonWritert.close();
    assertThat(string5.toString()).isEqualTo("\"a\"");
  }




  public void testNullStringValue() throws IOException {
    jsonWriter.beginObject();
    jsonWriter.name("a");
    jsonWriter.value((String) null);
    jsonWriter.endObject();
    assertThat(stringWriter.toString()).isEqualTo("{\"a\":null}");
  }


  public void testJsonValue() throws IOException {
    jsonWriter.beginObject();
    jsonWriter.name("a");
    jsonWriter.jsonValue("{\"b\":true}");
    jsonWriter.name("c");
    jsonWriter.value(1);
    jsonWriter.endObject();
    assertThat(stringWriter.toString()).isEqualTo("{\"a\":{\"b\":true},\"c\":1}");
  }








  public void testNonFiniteFloatsWhenLenient() throws IOException {
    jsonWriter.setLenient(true);
    jsonWriter.beginArray();
    jsonWriter.value(Float.NaN);
    jsonWriter.value(Float.NEGATIVE_INFINITY);
    jsonWriter.value(Float.POSITIVE_INFINITY);
    jsonWriter.endArray();
    assertThat(stringWriter.toString()).isEqualTo("[NaN,-Infinity,Infinity]");
  }


  public void testNonFiniteDoublesWhenLenient() throws IOException {
    jsonWriter.setLenient(true);
    jsonWriter.beginArray();
    jsonWriter.value(Double.NaN);
    jsonWriter.value(Double.NEGATIVE_INFINITY);
    jsonWriter.value(Double.POSITIVE_INFINITY);
    jsonWriter.endArray();
    assertThat(stringWriter.toString()).isEqualTo("[NaN,-Infinity,Infinity]");
  }


  public void testNonFiniteNumbersWhenLenient() throws IOException {
    jsonWriter.setLenient(true);
    jsonWriter.beginArray();
    jsonWriter.value(Double.valueOf(Double.NaN));
    jsonWriter.value(Double.valueOf(Double.NEGATIVE_INFINITY));
    jsonWriter.value(Double.valueOf(Double.POSITIVE_INFINITY));
    jsonWriter.value(new LazilyParsedNumber("Infinity"));
    jsonWriter.endArray();
    assertThat(stringWriter.toString()).isEqualTo("[NaN,-Infinity,Infinity,Infinity]");
  }


  public void testFloats() throws IOException {
    jsonWriter.beginArray();
    jsonWriter.value(-0.0f);
    jsonWriter.value(1.0f);
    jsonWriter.value(Float.MAX_VALUE);
    jsonWriter.value(Float.MIN_VALUE);
    jsonWriter.value(0.0f);
    jsonWriter.value(-0.5f);
    jsonWriter.value(2.2250739E-38f);
    jsonWriter.value(3.723379f);
    jsonWriter.value((float) Math.PI);
    jsonWriter.value((float) Math.E);
    jsonWriter.endArray();
    jsonWriter.close();
    assertThat(stringWriter.toString()).isEqualTo("[-0.0,"
        + "1.0,"
        + "3.4028235E38,"
        + "1.4E-45,"
        + "0.0,"
        + "-0.5,"
        + "2.2250739E-38,"
        + "3.723379,"
        + "3.1415927,"
        + "2.7182817]");
  }


  public void testDoubles() throws IOException {
    jsonWriter.beginArray();
    jsonWriter.value(-0.0);
    jsonWriter.value(1.0);
    jsonWriter.value(Double.MAX_VALUE);
    jsonWriter.value(Double.MIN_VALUE);
    jsonWriter.value(0.0);
    jsonWriter.value(-0.5);
    jsonWriter.value(2.2250738585072014E-308);
    jsonWriter.value(Math.PI);
    jsonWriter.value(Math.E);
    jsonWriter.endArray();
    jsonWriter.close();
    assertThat(stringWriter.toString()).isEqualTo("[-0.0,"
        + "1.0,"
        + "1.7976931348623157E308,"
        + "4.9E-324,"
        + "0.0,"
        + "-0.5,"
        + "2.2250738585072014E-308,"
        + "3.141592653589793,"
        + "2.718281828459045]");
  }


  public void testLongs() throws IOException {
    jsonWriter.beginArray();
    jsonWriter.value(0);
    jsonWriter.value(1);
    jsonWriter.value(-1);
    jsonWriter.value(Long.MIN_VALUE);
    jsonWriter.value(Long.MAX_VALUE);
    jsonWriter.endArray();
    jsonWriter.close();
    assertThat(stringWriter.toString()).isEqualTo("[0,"
        + "1,"
        + "-1,"
        + "-9223372036854775808,"
        + "9223372036854775807]");
  }


  public void testNumbers() throws IOException {
    jsonWriter.beginArray();
    jsonWriter.value(new BigInteger("0"));
    jsonWriter.value(new BigInteger("9223372036854775808"));
    jsonWriter.value(new BigInteger("-9223372036854775809"));
    jsonWriter.value(new BigDecimal("3.141592653589793238462643383"));
    jsonWriter.endArray();
    jsonWriter.close();
    assertThat(stringWriter.toString()).isEqualTo("[0,"
        + "9223372036854775808,"
        + "-9223372036854775809,"
        + "3.141592653589793238462643383]");
  }

  /**
   * Tests writing {@code Number} instances which are not one of the standard JDK ones.
   */

  public void testNumbersCustomClass() throws IOException {
    String[] validNumbers = {
        "-0.0",
        "1.0",
        "1.7976931348623157E308",
        "4.9E-324",
        "0.0",
        "0.00",
        "-0.5",
        "2.2250738585072014E-308",
        "3.141592653589793",
        "2.718281828459045",
        "0",
        "0.01",
        "0e0",
        "1e+0",
        "1e-0",
        "1e0000", // leading 0 is allowed for exponent
        "1e00001",
        "1e+1",
    };

    JsonArray jsonArray = new JsonArray();
    for (String validNumber : validNumbers) {
      jsonArray.addNumber(new LazilyParsedNumber(validNumber));
    }

    jsonWriter.jsonValue(jsonArray.toString());
    jsonWriter.close();
    assertThat(stringWriter.toString()).isEqualTo(jsonArray.toString());
  }






  public void testBooleans() throws IOException {
    jsonWriter.beginArray();
    jsonWriter.value(true);
    jsonWriter.value(false);
    jsonWriter.endArray();
    assertThat(stringWriter.toString()).isEqualTo("[true,false]");
  }


  public void testBoxedBooleans() throws IOException {
    jsonWriter.beginArray();
    jsonWriter.value((Boolean) true);
    jsonWriter.value((Boolean) false);
    jsonWriter.value((Boolean) null);
    jsonWriter.endArray();
    assertThat(stringWriter.toString()).isEqualTo("[true,false,null]");
  }


  public void testNulls() throws IOException {

    jsonWriter.beginArray();
    jsonWriter.nullValue();
    jsonWriter.endArray();
    assertThat(stringWriter.toString()).isEqualTo("[null]");
  }


  public void testStrings() throws IOException {
    jsonWriter.beginArray();
    jsonWriter.value("a");
    jsonWriter.value("a\"");
    jsonWriter.value("\"");
    jsonWriter.value(":");
    jsonWriter.value(",");
    jsonWriter.value("\b");
    jsonWriter.value("\f");
    jsonWriter.value("\n");
    jsonWriter.value("\r");
    jsonWriter.value("\t");
    jsonWriter.value(" ");
    jsonWriter.value("\\");
    jsonWriter.value("{");
    jsonWriter.value("}");
    jsonWriter.value("[");
    jsonWriter.value("]");
    jsonWriter.value("\0");
    jsonWriter.value("\u0019");
    jsonWriter.endArray();
    assertThat(stringWriter.toString()).isEqualTo("[\"a\","
        + "\"a\\\"\","
        + "\"\\\"\","
        + "\":\","
        + "\",\","
        + "\"\\b\","
        + "\"\\f\","
        + "\"\\n\","
        + "\"\\r\","
        + "\"\\t\","
        + "\" \","
        + "\"\\\\\","
        + "\"{\","
        + "\"}\","
        + "\"[\","
        + "\"]\","
        + "\"\\u0000\","
        + "\"\\u0019\"]");
  }


  public void testUnicodeLineBreaksEscaped() throws IOException {
    jsonWriter.beginArray();
    jsonWriter.value("\u2028 \u2029");
    jsonWriter.endArray();
    assertThat(stringWriter.toString()).isEqualTo("[\"\\u2028 \\u2029\"]");
  }


  public void testEmptyArray() throws IOException {
    jsonWriter.beginArray();
    jsonWriter.endArray();
    assertThat(stringWriter.toString()).isEqualTo("[]");
  }


  public void testEmptyObject() throws IOException {
    jsonWriter.beginObject();
    jsonWriter.endObject();
    assertThat(stringWriter.toString()).isEqualTo("{}");
  }


  public void testObjectsInArrays() throws IOException {
    jsonWriter.beginArray();
    jsonWriter.beginObject();
    jsonWriter.name("a").value(5);
    jsonWriter.name("b").value(false);
    jsonWriter.endObject();
    jsonWriter.beginObject();
    jsonWriter.name("c").value(6);
    jsonWriter.name("d").value(true);
    jsonWriter.endObject();
    jsonWriter.endArray();
    assertThat(stringWriter.toString()).isEqualTo("[{\"a\":5,\"b\":false},"
        + "{\"c\":6,\"d\":true}]");
  }


  public void testArraysInObjects() throws IOException {
    jsonWriter.beginObject();
    jsonWriter.name("a");
    jsonWriter.beginArray();
    jsonWriter.value(5);
    jsonWriter.value(false);
    jsonWriter.endArray();
    jsonWriter.name("b");
    jsonWriter.beginArray();
    jsonWriter.value(6);
    jsonWriter.value(true);
    jsonWriter.endArray();
    jsonWriter.endObject();
    assertThat(stringWriter.toString()).isEqualTo("{\"a\":[5,false],"
        + "\"b\":[6,true]}");
  }


  public void testDeepNestingArrays() throws IOException {
    for (int i = 0; i < 20; i++) {
      jsonWriter.beginArray();
    }
    for (int i = 0; i < 20; i++) {
      jsonWriter.endArray();
    }
    assertThat(stringWriter.toString()).isEqualTo("[[[[[[[[[[[[[[[[[[[[]]]]]]]]]]]]]]]]]]]]");
  }


  public void testDeepNestingObjects() throws IOException {
    jsonWriter.beginObject();
    for (int i = 0; i < 20; i++) {
      jsonWriter.name("a");
      jsonWriter.beginObject();
    }
    for (int i = 0; i < 20; i++) {
      jsonWriter.endObject();
    }
    jsonWriter.endObject();
    assertThat(stringWriter.toString()).isEqualTo("{\"a\":{\"a\":{\"a\":{\"a\":{\"a\":{\"a\":{\"a\":{\"a\":{\"a\":{\"a\":"
        + "{\"a\":{\"a\":{\"a\":{\"a\":{\"a\":{\"a\":{\"a\":{\"a\":{\"a\":{\"a\":{"
        + "}}}}}}}}}}}}}}}}}}}}}");
  }


  public void testRepeatedName() throws IOException {
    jsonWriter.beginObject();
    jsonWriter.name("a").value(true);
    jsonWriter.name("a").value(false);
    jsonWriter.endObject();
    // JsonWriter doesn't attempt to detect duplicate names
    assertThat(stringWriter.toString()).isEqualTo("{\"a\":true,\"a\":false}");
  }


  public void testPrettyPrintObject() throws IOException {
    jsonWriter.setIndent("   ");

    jsonWriter.beginObject();
    jsonWriter.name("a").value(true);
    jsonWriter.name("b").value(false);
    jsonWriter.name("c").value(5.0);
    jsonWriter.name("e").nullValue();
    jsonWriter.name("f").beginArray();
    jsonWriter.value(6.0);
    jsonWriter.value(7.0);
    jsonWriter.endArray();
    jsonWriter.name("g").beginObject();
    jsonWriter.name("h").value(8.0);
    jsonWriter.name("i").value(9.0);
    jsonWriter.endObject();
    jsonWriter.endObject();

    String expected = "{\n"
        + "   \"a\": true,\n"
        + "   \"b\": false,\n"
        + "   \"c\": 5.0,\n"
        + "   \"e\": null,\n"
        + "   \"f\": [\n"
        + "      6.0,\n"
        + "      7.0\n"
        + "   ],\n"
        + "   \"g\": {\n"
        + "      \"h\": 8.0,\n"
        + "      \"i\": 9.0\n"
        + "   }\n"
        + "}";
    assertThat(stringWriter.toString()).isEqualTo(expected);
  }


  public void testPrettyPrintArray() throws IOException {
    jsonWriter.setIndent("   ");

    jsonWriter.beginArray();
    jsonWriter.value(true);
    jsonWriter.value(false);
    jsonWriter.value(5.0);
    jsonWriter.nullValue();
    jsonWriter.beginObject();
    jsonWriter.name("a").value(6.0);
    jsonWriter.name("b").value(7.0);
    jsonWriter.endObject();
    jsonWriter.beginArray();
    jsonWriter.value(8.0);
    jsonWriter.value(9.0);
    jsonWriter.endArray();
    jsonWriter.endArray();

    String expected = "[\n"
        + "   true,\n"
        + "   false,\n"
        + "   5.0,\n"
        + "   null,\n"
        + "   {\n"
        + "      \"a\": 6.0,\n"
        + "      \"b\": 7.0\n"
        + "   },\n"
        + "   [\n"
        + "      8.0,\n"
        + "      9.0\n"
        + "   ]\n"
        + "]";
    assertThat(stringWriter.toString()).isEqualTo(expected);
  }


  public void testLenientWriterPermitsMultipleTopLevelValues() throws IOException {

    jsonWriter.setLenient(true);
    jsonWriter.beginArray();
    jsonWriter.endArray();
    jsonWriter.beginArray();
    jsonWriter.endArray();
    jsonWriter.close();
    assertThat(stringWriter.toString()).isEqualTo("[][]");
  }


  public void testStrictWriterDoesNotPermitMultipleTopLevelValues() throws IOException {
    jsonWriter.beginArray();
    jsonWriter.endArray();
    try {
      jsonWriter.beginArray();
      fail();
    } catch (IllegalStateException expected) {
    }
  }


  public void testClosedWriterThrowsOnStructure() throws IOException {
    jsonWriter.beginArray();
    jsonWriter.endArray();
    jsonWriter.close();
    try {
      jsonWriter.beginArray();
      fail();
    } catch (IllegalStateException expected) {
    }
    try {
      jsonWriter.endArray();
      fail();
    } catch (IllegalStateException expected) {
    }
    try {
      jsonWriter.beginObject();
      fail();
    } catch (IllegalStateException expected) {
    }
    try {
      jsonWriter.endObject();
      fail();
    } catch (IllegalStateException expected) {
    }
  }


  public void testClosedWriterThrowsOnName() throws IOException {
    jsonWriter.beginArray();
    jsonWriter.endArray();
    jsonWriter.close();
    try {
      jsonWriter.name("a");
      fail();
    } catch (IllegalStateException expected) {
    }
  }


  public void testClosedWriterThrowsOnValue() throws IOException {

    jsonWriter.beginArray();
    jsonWriter.endArray();
    jsonWriter.close();
    try {
      jsonWriter.value("a");
      fail();
    } catch (IllegalStateException expected) {
    }
  }


  public void testClosedWriterThrowsOnFlush() throws IOException {

    jsonWriter.beginArray();
    jsonWriter.endArray();
    jsonWriter.close();
    try {
      jsonWriter.flush();
      fail();
    } catch (IllegalStateException expected) {
    }
  }


  public void testWriterCloseIsIdempotent() throws IOException {

    jsonWriter.beginArray();
    jsonWriter.endArray();
    jsonWriter.close();
    jsonWriter.close();
  }


  public void testSetGetFormattingStyle() throws IOException {
    String lineSeparator = "\r\n";

    jsonWriter.setFormattingStyle(FormattingStyle.DEFAULT.withIndent(" \t ").withNewline(lineSeparator));

    jsonWriter.beginArray();
    jsonWriter.value(true);
    jsonWriter.value("text");
    jsonWriter.value(5.0);
    jsonWriter.nullValue();
    jsonWriter.endArray();

    String expected = "[\r\n"
        + " \t true,\r\n"
        + " \t \"text\",\r\n"
        + " \t 5.0,\r\n"
        + " \t null\r\n"
        + "]";
    assertThat(stringWriter.toString()).isEqualTo(expected);

    assertThat(jsonWriter.getFormattingStyle().getNewline()).isEqualTo(lineSeparator);
  }
}
