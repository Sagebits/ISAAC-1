/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * You may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributions from 2013-2017 where performed either by US government 
 * employees, or under US Veterans Health Administration contracts. 
 *
 * US Veterans Health Administration contributions by government employees
 * are work of the U.S. Government and are not subject to copyright
 * protection in the United States. Portions contributed by government 
 * employees are USGovWork (17USC §105). Not subject to copyright. 
 * 
 * Contribution by contractors to the US Veterans Health Administration
 * during this period are contractually contributed under the
 * Apache License, Version 2.0.
 *
 * See: https://www.usa.gov/government-works
 * 
 * Contributions prior to 2013:
 *
 * Copyright (C) International Health Terminology Standards Development Organisation.
 * Licensed under the Apache License, Version 2.0.
 *
 */



package sh.isaac.model;

//~--- non-JDK imports --------------------------------------------------------

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import sh.isaac.api.externalizable.ByteArrayDataBuffer;

//~--- classes ----------------------------------------------------------------

/**
 *
 * @author kec
 */
public class DataBufferTest {
   public DataBufferTest() {}

   //~--- methods -------------------------------------------------------------

   @After
   public void tearDown() {}

   @AfterClass
   public static void tearDownClass() {}

   /**
    * Test of trimToSize method, of class ByteArrayDataBuffer.
    */
   @Test
   public void testCompress() {
      System.out.println("compress");

      ByteArrayDataBuffer instance = new ByteArrayDataBuffer(10);

      instance.putInt(5);
      instance.trimToSize();
      assertEquals(instance.getData().length, 4);
   }

   @Test
   public void testEdgeUTF() {
      System.out.println("readUTF");

      String              str      = "is-a";
      ByteArrayDataBuffer instance = new ByteArrayDataBuffer(10);

      instance.putShort((short) 4);
      instance.putInt(13);
      instance.putInt(16);
      instance.putUTF(str);
      instance.putInt(11);
      instance.rewind();
      assertEquals(4, instance.getShort());
      assertEquals(13, instance.getInt());
      assertEquals(16, instance.getInt());

      String result = instance.readUTF();

      assertEquals(str, result);
      assertEquals(11, instance.getInt());
   }

   /**
    * Test of get method, of class ByteArrayDataBuffer.
    */
   @Test
   public void testGet() {
      System.out.println("get");

      byte[]              src      = new byte[] { 1, 2, 3 };
      int                 offset   = 0;
      int                 length   = 3;
      ByteArrayDataBuffer instance = new ByteArrayDataBuffer(10);

      instance.put(src);
      instance.rewind();

      byte[] result = new byte[3];

      instance.get(result, offset, length);
      assertArrayEquals(src, result);
   }

   /**
    * Test of getByte method, of class ByteArrayDataBuffer.
    */
   @Test
   public void testGetByte() {
      System.out.println("getByte");

      ByteArrayDataBuffer instance  = new ByteArrayDataBuffer(10);
      byte                expResult = 5;

      instance.putByte(expResult);
      instance.rewind();

      byte result = instance.getByte();

      assertEquals(expResult, result);
   }

   /**
    * Test of getChar method, of class ByteArrayDataBuffer.
    */
   @Test
   public void testGetChar() {
      System.out.println("getChar");

      ByteArrayDataBuffer instance  = new ByteArrayDataBuffer(10);
      char                expResult = ' ';

      instance.putChar(expResult);
      instance.rewind();

      char result = instance.getChar();

      assertEquals(expResult, result);
   }

   /**
    * Test of getData method, of class ByteArrayDataBuffer.
    */
   @Test
   public void testGetData() {
      System.out.println("getData");

      ByteArrayDataBuffer instance  = new ByteArrayDataBuffer(10);
      byte[]              expResult = new byte[10];
      byte[]              result    = instance.getData();

      assertArrayEquals(expResult, result);
   }

   /**
    * Test of getDouble method, of class ByteArrayDataBuffer.
    */
   @Test
   public void testGetDouble() {
      System.out.println("getDouble");

      ByteArrayDataBuffer instance  = new ByteArrayDataBuffer(10);
      double              expResult = Double.MAX_VALUE;

      instance.putDouble(expResult);
      instance.rewind();

      double result = instance.getDouble();

      assertEquals(expResult, result, 0.0);
   }

   /**
    * Test of getFloat method, of class ByteArrayDataBuffer.
    */
   @Test
   public void testGetFloat() {
      System.out.println("getFloat");

      ByteArrayDataBuffer instance  = new ByteArrayDataBuffer(10);
      float               expResult = 7.3F;

      instance.putFloat(expResult);
      instance.rewind();

      float result = instance.getFloat();

      assertEquals(expResult, result, 0.0);
   }

   /**
    * Test of getInt method, of class ByteArrayDataBuffer.
    */
   @Test
   public void testGetInt() {
      System.out.println("getInt");

      ByteArrayDataBuffer instance  = new ByteArrayDataBuffer(10);
      int                 expResult = 9;

      instance.putInt(expResult);
      instance.rewind();

      int result = instance.getInt();

      assertEquals(expResult, result);
   }

   /**
    * Test of getLong method, of class ByteArrayDataBuffer.
    */
   @Test
   public void testGetLong() {
      System.out.println("getLong");

      ByteArrayDataBuffer instance  = new ByteArrayDataBuffer(10);
      long                expResult = Long.MAX_VALUE - 5;

      instance.putLong(expResult);
      instance.rewind();

      long result = instance.getLong();

      assertEquals(expResult, result);
   }

   /**
    * Test of getPosition method, of class ByteArrayDataBuffer.
    */
   @Test
   public void testGetPosition() {
      System.out.println("getPosition");

      ByteArrayDataBuffer instance  = new ByteArrayDataBuffer(10);
      int                 expResult = 0;
      int                 result    = instance.getPosition();

      assertEquals(expResult, result);
   }

   /**
    * Test of getShort method, of class ByteArrayDataBuffer.
    */
   @Test
   public void testGetShort() {
      System.out.println("getShort");

      ByteArrayDataBuffer instance  = new ByteArrayDataBuffer(10);
      short               expResult = 19;

      instance.putShort(expResult);
      instance.rewind();

      short result = instance.getShort();

      assertEquals(expResult, result);
   }

   /**
    * Test of put method, of class ByteArrayDataBuffer.
    */
   @Test
   public void testPut() {
      System.out.println("put");

      byte[]              src      = new byte[] { 3, 2, 9 };
      int                 offset   = 0;
      int                 length   = 3;
      ByteArrayDataBuffer instance = new ByteArrayDataBuffer(10);

      instance.put(src);
      instance.rewind();

      byte[] result = new byte[3];

      instance.get(result, offset, length);
      assertArrayEquals(src, result);
   }

   /**
    * Test of putByte method, of class ByteArrayDataBuffer.
    */
   @Test
   public void testPutByte() {
      System.out.println("putByte");

      ByteArrayDataBuffer instance  = new ByteArrayDataBuffer(10);
      byte                expResult = 23;

      instance.putByte(expResult);
      instance.rewind();

      byte result = instance.getByte();

      assertEquals(expResult, result);
   }

   /**
    * Test of putChar method, of class ByteArrayDataBuffer.
    */
   @Test
   public void testPutChar() {
      System.out.println("putChar");

      ByteArrayDataBuffer instance  = new ByteArrayDataBuffer(10);
      char                expResult = '&';

      instance.putChar(expResult);
      instance.rewind();

      char result = instance.getChar();

      assertEquals(expResult, result);
   }

   /**
    * Test of putDouble method, of class ByteArrayDataBuffer.
    */
   @Test
   public void testPutDouble() {
      System.out.println("putDouble");

      ByteArrayDataBuffer instance  = new ByteArrayDataBuffer(10);
      double              expResult = Double.MIN_VALUE;

      instance.putDouble(expResult);
      instance.rewind();

      double result = instance.getDouble();

      assertEquals(expResult, result, 0.0);
   }

   /**
    * Test of putFloat method, of class ByteArrayDataBuffer.
    */
   @Test
   public void testPutFloat() {
      System.out.println("putFloat");

      ByteArrayDataBuffer instance  = new ByteArrayDataBuffer(10);
      float               expResult = 9.14F;

      instance.putFloat(expResult);
      instance.rewind();

      float result = instance.getFloat();

      assertEquals(expResult, result, 0.0);
   }

   /**
    * Test of putInt method, of class ByteArrayDataBuffer.
    */
   @Test
   public void testPutInt() {
      System.out.println("putInt");

      ByteArrayDataBuffer instance  = new ByteArrayDataBuffer(10);
      int                 expResult = 19;

      instance.putInt(expResult);
      instance.rewind();

      int result = instance.getInt();

      assertEquals(expResult, result);
   }

   /**
    * Test of putLong method, of class ByteArrayDataBuffer.
    */
   @Test
   public void testPutLong() {
      System.out.println("putLong");

      ByteArrayDataBuffer instance  = new ByteArrayDataBuffer(10);
      long                expResult = Long.MIN_VALUE + 7;

      instance.putLong(expResult);
      instance.rewind();

      long result = instance.getLong();

      assertEquals(expResult, result);
   }

   /**
    * Test of putShort method, of class ByteArrayDataBuffer.
    */
   @Test
   public void testPutShort() {
      System.out.println("putShort");

      ByteArrayDataBuffer instance  = new ByteArrayDataBuffer(10);
      short               expResult = 37;

      instance.putShort(expResult);
      instance.rewind();

      short result = instance.getShort();

      assertEquals(expResult, result);
   }

   /**
    * Test of putUTF method, of class ByteArrayDataBuffer.
    */
   @Test
   public void testPutUTF() {
      System.out.println("putUTF");

      String              str      = "Now is the time for all † ‡ °";
      ByteArrayDataBuffer instance = new ByteArrayDataBuffer(10);

      instance.putUTF(str);
      instance.rewind();

      String result = instance.readUTF();

      assertEquals(str, result);
   }

   /**
    * Test of readUTF method, of class ByteArrayDataBuffer.
    */
   @Test
   public void testReadUTF() {
      System.out.println("readUTF");

      String              str      = "Fine for all ˜ Â the time for all † ‡ °";
      ByteArrayDataBuffer instance = new ByteArrayDataBuffer(10);

      instance.putUTF(str);
      instance.rewind();

      String result = instance.readUTF();

      assertEquals(str, result);
   }

   /**
    * Test of reset method, of class ByteArrayDataBuffer.
    */
   @Test
   public void testReset() {
      System.out.println("clear");

      ByteArrayDataBuffer instance  = new ByteArrayDataBuffer(10);
      int                 expResult = 5;

      instance.setPosition(expResult);
      instance.clear();

      int result = instance.getPosition();

      assertEquals(0, result);
   }

   /**
    * Test of setPosition method, of class ByteArrayDataBuffer.
    */
   @Test
   public void testSetPosition() {
      System.out.println("setPosition");

      ByteArrayDataBuffer instance  = new ByteArrayDataBuffer(10);
      int                 expResult = 5;

      instance.setPosition(expResult);

      int result = instance.getPosition();

      assertEquals(expResult, result);
   }

   //~--- set methods ---------------------------------------------------------

   @Before
   public void setUp() {}

   @BeforeClass
   public static void setUpClass() {}
}

