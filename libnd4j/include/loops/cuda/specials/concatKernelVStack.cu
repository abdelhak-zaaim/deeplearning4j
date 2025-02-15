/* ******************************************************************************
 *
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 *  See the NOTICE file distributed with this work for additional
 *  information regarding copyright ownership.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ******************************************************************************/

//
// @author raver119@gmail.com
// @author Yurii Shyrma, created on 15.11.2018
//
#include <loops/special_kernels.h>


namespace sd {

///////////////////////////////////////////////////////////////////////
template <typename T>
SD_DEVICE void concatKernelVStack(int numArrays, Pointer *data, Pointer *inputShapeInfos, void *vz,
                                  LongType *zShapeInfo) {
  /*
   this is special case for concat: we group bunch of vectors into 2D matrix
   also: we expect each inputShapeInfo to have EWS, be a vector, and have equal size
   */
  auto z = static_cast<T *>(vz);

  auto inputShapes = (LongType **)inputShapeInfos;
  T **input = (T **)data;

  __shared__ int inputEWS;
  __shared__ int resultEWS;
  __shared__ int inputLength;

  if (threadIdx.x == 0) {
    inputLength = shape::length(inputShapes[0]);
    inputEWS = shape::elementWiseStride(inputShapes[0]);
    resultEWS = shape::elementWiseStride(zShapeInfo);
  }
  __syncthreads();

  for (int r = blockIdx.x; r < numArrays; r += gridDim.x) {
    int zOffset = r * inputLength * resultEWS;
    T *inputData = (T *)input[r];

    for (int i = threadIdx.x; i < inputLength; i += blockDim.x) {
      z[zOffset + i * resultEWS] = inputData[i * inputEWS];
    }
  }
}

///////////////////////////////////////////////////////////////////////
template <typename T>
SD_KERNEL void execConcatKernelVStack(int numArrays, Pointer *data, Pointer *inputShapeInfos, void *vz,
                                      LongType *zShapeInfo) {
  concatKernelVStack<T>(numArrays, data, inputShapeInfos, vz, zShapeInfo);
}

///////////////////////////////////////////////////////////////////////
template <typename T>
SD_HOST void concatKernelVStackGeneric(dim3 &launchDims, cudaStream_t *stream, int numArrays, Pointer *data,
                                       Pointer *inputShapeInfos, void *vz, LongType *zShapeInfo) {
  execConcatKernelVStack<T>
      <<<launchDims.x, launchDims.y, launchDims.z, *stream>>>(numArrays, data, inputShapeInfos, vz, zShapeInfo);
  DebugHelper::checkErrorCode(stream, "concatVStack(...) failed");
}

BUILD_SINGLE_TEMPLATE(template void concatKernelVStackGeneric,
                      (dim3 & launchDims, cudaStream_t *stream, int numArrays, sd::Pointer *data,
                       sd::Pointer *inputShapeInfos, void *vz, sd::LongType *zShapeInfo),
                      SD_COMMON_TYPES);
}  // namespace sd
