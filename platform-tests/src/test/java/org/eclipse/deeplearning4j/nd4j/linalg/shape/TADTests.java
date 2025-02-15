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

package org.eclipse.deeplearning4j.nd4j.linalg.shape;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import org.nd4j.common.tests.tags.NativeTag;
import org.nd4j.common.tests.tags.TagNames;
import org.nd4j.linalg.BaseNd4jTestWithBackends;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.shape.Shape;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.common.primitives.Pair;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.nd4j.linalg.indexing.NDArrayIndex.all;
import static org.nd4j.linalg.indexing.NDArrayIndex.point;

@Slf4j
@NativeTag
@Tag(TagNames.NDARRAY_INDEXING)
public class TADTests extends BaseNd4jTestWithBackends {


    @ParameterizedTest
    @MethodSource("org.nd4j.linalg.BaseNd4jTestWithBackends#configs")
    public void testStall(Nd4jBackend backend) {
        //[4, 3, 3, 4, 5, 60, 20, 5, 1, 0, 1, 99], dimensions: [1, 2, 3]
        INDArray arr = Nd4j.create(3, 3, 4, 5);
        arr.tensorAlongDimension(0, 1, 2, 3);
    }



    /**
     * This test checks for TADs equality between Java & native
     *
     * @throws Exception
     */
    @ParameterizedTest
    @MethodSource("org.nd4j.linalg.BaseNd4jTestWithBackends#configs")
    public void testEquality1(Nd4jBackend backend) {

        char[] order = new char[] {'c', 'f'};
        int[] dim_e = new int[] {0, 2};
        int[] dim_x = new int[] {1, 3};
        List<long[]> dim_3 = Arrays.asList(new long[] {0, 2, 3}, new long[] {0, 1, 2}, new long[] {1, 2, 3},
                new long[] {0, 1, 3});


        for (char o : order) {
            INDArray array = Nd4j.create(new int[] {3, 5, 7, 9}, o);
            for (int e : dim_e) {
                for (int x : dim_x) {

                    long[] shape = new long[] {e, x};
                    Arrays.sort(shape);
                    INDArray assertion = array.tensorAlongDimension(0, shape);
                    INDArray test = array.tensorAlongDimension(0, shape);

                    assertEquals(assertion, test);

                }
            }
        }

        for (char o : order) {
            INDArray array = Nd4j.create(new int[] {9, 7, 5, 3}, o);
            for (long[] shape : dim_3) {
                Arrays.sort(shape);

                INDArray assertion = array.tensorAlongDimension(0, shape);
                INDArray test = array.tensorAlongDimension(0, shape);
                assertEquals(assertion, test);
            }
        }
    }

    @ParameterizedTest
    @MethodSource("org.nd4j.linalg.BaseNd4jTestWithBackends#configs")
    public void testMysteriousCrash(Nd4jBackend backend) {
        INDArray arrayF = Nd4j.create(new int[] {1, 1, 4, 4}, 'f');
        INDArray arrayC = Nd4j.create(new int[] {1, 1, 4, 4}, 'c');
        INDArray javaCTad = arrayC.tensorAlongDimension(0, 2, 3);
        INDArray javaFTad = arrayF.tensorAlongDimension(0, 2, 3);
        Pair<DataBuffer, DataBuffer> tadBuffersF =
                Nd4j.getExecutioner().getTADManager().getTADOnlyShapeInfo(arrayF, 2, 3);
        Pair<DataBuffer, DataBuffer> tadBuffersC =
                Nd4j.getExecutioner().getTADManager().getTADOnlyShapeInfo(arrayC, 2, 3);

//        log.info("Got TADShapeF: {}", Arrays.toString(tadBuffersF.getFirst().asInt()) + " with java "
//                        + javaFTad.shapeInfoDataBuffer());
//        log.info("Got TADShapeC: {}", Arrays.toString(tadBuffersC.getFirst().asInt()) + " with java "
//                        + javaCTad.shapeInfoDataBuffer());
    }

    @ParameterizedTest
    @MethodSource("org.nd4j.linalg.BaseNd4jTestWithBackends#configs")
    public void testTADEWSStride(){
        INDArray orig = Nd4j.linspace(1, 600, 600).reshape('f', 10, 1, 60);

        for( int i=0; i<60; i++ ){
            INDArray tad = orig.tensorAlongDimension(i, 0, 1);
            //TAD: should be equivalent to get(all, all, point(i))
            INDArray get = orig.get(all(), all(), point(i));

            String str = String.valueOf(i);
            assertEquals(get, tad,str);
            assertEquals(get.elementWiseStride(), tad.elementWiseStride(),str);

            char orderTad = Shape.getOrder(tad.shape(), tad.stride(), 1);
            char orderGet = Shape.getOrder(get.shape(), get.stride(), 1);

            assertEquals('f', orderTad);
            assertEquals('f', orderGet);

            long ewsTad = Shape.elementWiseStride(tad.shape(), tad.stride(), tad.ordering() == 'f');
            long ewsGet = Shape.elementWiseStride(get.shape(), get.stride(), get.ordering() == 'f');

            assertEquals(1, ewsTad);
            assertEquals(1, ewsGet);
        }
    }

    @Override
    public char ordering() {
        return 'c';
    }

    /**
     * this method compares rank, shape and stride for two given shapeBuffers
     * @param shapeA
     * @param shapeB
     * @return
     */
    protected boolean compareShapes(@NonNull DataBuffer shapeA, @NonNull DataBuffer shapeB) {
        if (shapeA.dataType() != DataType.INT)
            throw new IllegalStateException("ShapeBuffer should have dataType of INT");

        if (shapeA.dataType() != shapeB.dataType())
            return false;

        int rank = shapeA.getInt(0);
        if (rank != shapeB.getInt(0))
            return false;

        for (int e = 1; e <= rank * 2; e++) {
            if (shapeA.getInt(e) != shapeB.getInt(e))
                return false;
        }

        return true;
    }
}
