/*
 *  ******************************************************************************
 *  *
 *  *
 *  * This program and the accompanying materials are made available under the
 *  * terms of the Apache License, Version 2.0 which is available at
 *  * https://www.apache.org/licenses/LICENSE-2.0.
 *  *
 *  *  See the NOTICE file distributed with this work for additional
 *  *  information regarding copyright ownership.
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations
 *  * under the License.
 *  *
 *  * SPDX-License-Identifier: Apache-2.0
 *  *****************************************************************************
 */

package org.nd4j.linalg.api.buffer.factory;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.indexer.Indexer;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.memory.MemoryWorkspace;

import java.nio.ByteBuffer;

/**
 * A backend agnostic abstraction
 * for creating {@link DataBuffer}
 * for a given backend.
 */
public interface DataBufferFactory {



    /**
     * Setter for the allocation mode
     * @param allocationMode
     */
    void setAllocationMode(DataBuffer.AllocationMode allocationMode);

    /**
     * Allocation mode for the data buffer
     * @return
     */
    DataBuffer.AllocationMode allocationMode();


    /**
     * This method will create new DataBuffer of the same dataType & same length
     * @param buffer
     * @return
     */
    DataBuffer createSame(DataBuffer buffer, boolean init);

    /**
     * This method will create new DataBuffer of the same dataType & same length
     * @param buffer
     * @return
     */
    DataBuffer createSame(DataBuffer buffer, boolean init, MemoryWorkspace workspace);

    /**
     * This method will create new DataBuffer of the same dataType & same length
     * @param data the data to create the buffer from
     * @return the new buffer
     */
     DataBuffer createBuffer(String[] data);

    /**
     * Create a databuffer from the given strings
     * with the given string data type.
     * Only {@link DataType#UTF8}
     * {@link DataType#UTF16}
     * {@link DataType#UTF32}
     * are accepted otherwise an {@link UnsupportedOperationException}
     * is thrown.
     * @param data the data to create the buffer from
     * @param dataType the data type to use
     * @return the new buffer
     */
     DataBuffer createTypedBuffer(String[] data,DataType dataType);


    /**
     * Create a double data buffer
     *
     * @return the new data buffer
     */
    DataBuffer createDouble(long length);

    /**
     * Create a double data buffer, with optional initialization
     *
     * @param initialize If true: initialize the buffer. If false: don't initialize.
     *
     * @return the new data buffer
     */
    DataBuffer createDouble(long length, boolean initialize);

    DataBuffer createDouble(long length, boolean initialize, MemoryWorkspace workspace);

    /**
     * Create a float data buffer
     *
     * @param length the length of the buffer
     * @return the new data buffer
     */
    DataBuffer createFloat(long length);

    /**
     * Create a float data buffer, with optional initialization
     *
     * @param length the length of the buffer
     * @param initialize If true: initialize the buffer. If false: don't initialize.
     * @return the new data buffer
     */
    DataBuffer createFloat(long length, boolean initialize);

    DataBuffer createFloat(long length, boolean initialize, MemoryWorkspace workspace);

    DataBuffer create(DataType dataType, long length, boolean initialize);

    DataBuffer create(DataType dataType, long length, boolean initialize, MemoryWorkspace workspace);


    /**
     * Create an int data buffer
     *
     * @param length the length of the data buffer
     * @return the create data buffer
     */
    DataBuffer createInt(long length);

    DataBuffer createBFloat16(long length);
    DataBuffer createByte(long length);
    DataBuffer createShort(long length);
    DataBuffer createBool(long length);
    DataBuffer createUShort(long length);
    DataBuffer createUInt(long length);
    DataBuffer createUByte(long length);
    DataBuffer createULong(long length);

    DataBuffer createBFloat16(long length, boolean initialize);
    DataBuffer createByte(long length, boolean initialize);
    DataBuffer createShort(long length, boolean initialize);
    DataBuffer createBool(long length, boolean initialize);
    DataBuffer createUShort(long length, boolean initialize);
    DataBuffer createUInt(long length, boolean initialize);
    DataBuffer createUByte(long length, boolean initialize);
    DataBuffer createULong(long length, boolean initialize);

    DataBuffer createBFloat16(long length, boolean initialize, MemoryWorkspace workspace);
    DataBuffer createByte(long length, boolean initialize, MemoryWorkspace workspace);
    DataBuffer createShort(long length, boolean initialize, MemoryWorkspace workspace);
    DataBuffer createBool(long length, boolean initialize, MemoryWorkspace workspace);
    DataBuffer createUShort(long length, boolean initialize, MemoryWorkspace workspace);
    DataBuffer createUInt(long length, boolean initialize, MemoryWorkspace workspace);
    DataBuffer createUByte(long length, boolean initialize, MemoryWorkspace workspace);
    DataBuffer createULong(long length, boolean initialize, MemoryWorkspace workspace);

    /**
     * Create an int data buffer, with optional initialization
     *
     * @param length the length of the data buffer
     * @param initialize If true: initialize the buffer. If false: don't initialize.
     * @return the create data buffer
     */
    DataBuffer createInt(long length, boolean initialize);

    DataBuffer createInt(long length, boolean initialize, MemoryWorkspace workspace);


    DataBuffer createLong(long[] data);

    DataBuffer createLong(long[] data, boolean copy);

    DataBuffer createLong(long length);

    /**
     * Create an int data buffer, with optional initialization
     *
     * @param length the length of the data buffer
     * @param initialize If true: initialize the buffer. If false: don't initialize.
     * @return the create data buffer
     */
    DataBuffer createLong(long length, boolean initialize);

    DataBuffer createLong(long[] data, MemoryWorkspace workspace);

    DataBuffer createLong(long length, boolean initialize, MemoryWorkspace workspace);

    /**
     * Creates a double data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createDouble(int[] data);

    /**
     * Creates a double data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createFloat(int[] data);

    /**
     * Creates a double data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createInt(int[] data);

    DataBuffer createInt(int[] data, MemoryWorkspace workspace);

    DataBuffer createInt(int[] data, boolean copy, MemoryWorkspace workspace);

    /**
     * Creates a double data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createDouble(double[] data);


    /**
     * Creates a float data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createFloat(double[] data);

    /**
     * Creates an int data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createInt(double[] data);

    /**
     * Creates a double data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createDouble(float[] data);

    /**
     * Creates a float data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createFloat(float[] data);

    DataBuffer createFloat(float[] data, MemoryWorkspace workspace);

    /**
     * Creates an int data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createInt(float[] data);


    /**
     * Creates a double data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createDouble(int[] data, boolean copy);

    /**
     * Creates a double data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createFloat(int[] data, boolean copy);

    /**
     * Creates a double data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createInt(int[] data, boolean copy);

    /**
     * Creates a long data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createLong(int[] data, boolean copy);


    /**
     * Creates a double data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createDouble(long[] data, boolean copy);

    /**
     * Creates a float data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createFloat(long[] data, boolean copy);

    /**
     * Creates a int data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createInt(long[] data, boolean copy);

    /**
     * Creates a double data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createDouble(double[] data, boolean copy);

    /**
     * Creates a double data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createDouble(double[] data, MemoryWorkspace workspace);

    /**
     * Creates a double data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createDouble(double[] data, boolean copy, MemoryWorkspace workspace);



    /**
     * Creates a float data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createFloat(double[] data, boolean copy);

    /**
     * Creates an int data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createInt(double[] data, boolean copy);

    /**
     * Creates a double data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createDouble(float[] data, boolean copy);

    /**
     * Creates a float data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createFloat(float[] data, boolean copy);

    DataBuffer createFloat(float[] data, boolean copy, MemoryWorkspace workspace);

    /**
     * Creates an int data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createInt(float[] data, boolean copy);


    /**
     * Create a data buffer based on the
     * given pointer, data buffer opType,
     * and length of the buffer
     * @param pointer the pointer to use
     * @param type the opType of buffer
     * @param length the length of the buffer
     * @param indexer
     * @return the data buffer
     * backed by this pointer with the given
     * opType and length.
     */
    DataBuffer create(Pointer pointer, DataType type, long length, Indexer indexer);

    DataBuffer create(Pointer pointer, Pointer specialPointer, DataType type, long length, Indexer indexer);

    /**
     *
     * @param doublePointer
     * @param length
     * @return
     */
    DataBuffer create(DoublePointer doublePointer, long length);

    /**
     *
     * @param intPointer
     * @param length
     * @return
     */
    DataBuffer create(IntPointer intPointer, long length);

    /**
     *
     * @param floatPointer
     * @param length
     * @return
     */
    DataBuffer create(FloatPointer floatPointer, long length);

    DataBuffer createBuffer(ByteBuffer underlyingBuffer, DataType dataType, long length);

    /**
     * Creates half-precision data buffer
     *
     * @param length length of new data buffer
     * @return
     */
    DataBuffer createHalf(long length);

    /**
     * Creates half-precision data buffer
     *
     * @param length length of new data buffer
     * @param initialize true if memset should be used on allocated memory, false otherwise
     * @return
     */
    DataBuffer createHalf(long length, boolean initialize);

    DataBuffer createHalf(long length, boolean initialize, MemoryWorkspace workspace);

    /**
     * Creates a half-precision data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createHalf(float[] data, boolean copy);

    DataBuffer createHalf(float[] data, MemoryWorkspace workspace);

    DataBuffer createHalf(float[] data, boolean copy, MemoryWorkspace workspace);



    /**
     * Creates a half-precision data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createHalf(double[] data, boolean copy);


    /**
     * Creates a half-precision data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createHalf(long offset, float[] data);

    /**
     * Creates a half-precision data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createHalf(int[] data, boolean copy);

    /**
     * Creates a half-precision data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createHalf(float[] data);

    /**
     * Creates a half-precision data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createHalf(double[] data);

    /**
     * Creates a half-precision data buffer
     *
     * @param data the data to create the buffer from
     * @return the new buffer
     */
    DataBuffer createHalf(int[] data);


    Class<? extends DataBuffer> intBufferClass();

    Class<? extends DataBuffer> longBufferClass();

    Class<? extends DataBuffer> halfBufferClass();

    Class<? extends DataBuffer> floatBufferClass();

    Class<? extends DataBuffer> doubleBufferClass();

    DataBuffer createUtf8Buffer(byte[] data, long product);
}
