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

package org.nd4j.linalg.cpu.nativecpu.buffer;


import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.indexer.Indexer;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.memory.MemoryWorkspace;

import java.nio.ByteBuffer;

/**
 * Data buffer for floats
 *
 * @author Adam Gibson
 */
public class BoolBuffer extends BaseCpuDataBuffer {

    /**
     * Meant for creating another view of a buffer
     *
     * @param pointer the underlying buffer to create a view from
     * @param indexer the indexer for the pointer
     * @param length  the length of the view
     */
    public BoolBuffer(Pointer pointer, Indexer indexer, long length) {
        super(pointer, indexer, length);
    }

    /**
     * Create a float buffer with the given length
     * @param length the float buffer with the given length
     */
    public BoolBuffer(long length) {
        super(length);
    }



    public BoolBuffer(long length, boolean initialize) {
        super(length, initialize);
    }

    public BoolBuffer(long length, boolean initialize, MemoryWorkspace workspace) {
        super(length, initialize, workspace);
    }

    public BoolBuffer(int length, int elementSize) {
        super(length, elementSize);
    }

    public BoolBuffer(int length, int elementSize, long offset) {
        super(length, elementSize);
    }

    public BoolBuffer(ByteBuffer underlyingBuffer, DataType dataType, long length) {
        super(underlyingBuffer, dataType, length);
    }

    /**
     * Initialize the opType of this buffer
     */
    @Override
    protected void initTypeAndSize() {
        type = DataType.BOOL;
        elementSize = 1;
    }


    public BoolBuffer(float[] data) {
        this(data, true);
    }

    public BoolBuffer(float[] data, MemoryWorkspace workspace) {
        this(data, true, workspace);
    }

    public BoolBuffer(int[] data) {
        this(data, true);
    }

    public BoolBuffer(double[] data) {
        this(data, true);
    }

    public BoolBuffer(int[] data, boolean copyOnOps) {
        super(data, copyOnOps);
    }


    public BoolBuffer(double[] data, boolean copyOnOps) {
        super(data, copyOnOps);
    }


    public BoolBuffer(float[] floats, boolean copy) {
        super(floats, copy);
    }

    public BoolBuffer(float[] floats, boolean copy, MemoryWorkspace workspace) {
        super(floats, copy, workspace);
    }

    public BoolBuffer(float[] data, boolean copy, long offset) {
        super(data, copy);
    }

    public BoolBuffer(float[] data, boolean copy, long offset, MemoryWorkspace workspace) {
        super(data, copy, workspace);
    }

    @Override
    protected DataBuffer create(long length) {
        return new BoolBuffer(length);
    }


    @Override
    public DataBuffer create(double[] data) {
        return new BoolBuffer(data);
    }

    @Override
    public DataBuffer create(float[] data) {
        return new BoolBuffer(data);
    }

    @Override
    public DataBuffer create(int[] data) {
        return new BoolBuffer(data);
    }


}
