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
// Created by raver119 on 31.10.2017.
//
#include <array/NDArray.h>
#include <legacy/NativeOps.h>
#include <ops/declarable/CustomOperations.h>

#include "testlayers.h"

using namespace sd;
using namespace sd::graph;

class IndexingTests : public NDArrayTests {
 public:
};

TEST_F(IndexingTests, StridedSlice_1) {
  auto x = NDArrayFactory::create<float>('c', {3, 3, 3});
  auto exp = NDArrayFactory::create<float>('c', {1, 1, 3});
  exp.p(0, 25.f);
  exp.p(1, 26.f);
  exp.p(2, 27.f);

  x.linspace(1);
  auto begin = NDArrayFactory::create<int>({2, 2, 0});
  auto end = NDArrayFactory::create<int>({3, 3, 3});
  auto strides = NDArrayFactory::create<int>({1, 1, 1});

  ops::strided_slice op;

  auto result = op.evaluate({&x, &begin, &end, &strides}, {}, {0, 0, 0, 0, 0});  //, 2,2,0,  3,3,3,  1,1,1});
  ASSERT_EQ(sd::Status::OK, result.status());

  auto z = result.at(0);
ASSERT_EQ(exp,*z);
}

TEST_F(IndexingTests, StridedSlice_2) {
  auto x = NDArrayFactory::create<float>('c', {5, 5, 5});
  auto exp = NDArrayFactory::create<float>('c', {2, 3, 3},
                                           {86.f, 87.f, 88.f, 91.f, 92.f, 93.f, 96.f, 97.f, 98.f, 111.f, 112.f, 113.f,
                                            116.f, 117.f, 118.f, 121.f, 122.f, 123.f});

  x.linspace(1);

  ops::strided_slice op;

  auto result = op.evaluate({&x}, {}, {0, 0, 0, 0, 0, 3, 2, 0, 5, 5, 3, 1, 1, 1});
  ASSERT_EQ(sd::Status::OK, result.status());

  auto z = result.at(0);

ASSERT_EQ(exp,*z);
}

TEST_F(IndexingTests, StridedSlice_3) {
  auto x = NDArrayFactory::create<float>('c', {5, 5, 5});
  auto exp = NDArrayFactory::create<float>(
      'c', {2, 3, 2}, {86.f, 88.f, 91.f, 93.f, 96.f, 98.f, 111.f, 113.f, 116.f, 118.f, 121.f, 123.f});

  x.linspace(1);

  ops::strided_slice op;

  auto result = op.evaluate({&x}, {}, {0, 0, 0, 0, 0, 3, 2, 0, 5, 5, 3, 1, 1, 2});
  ASSERT_EQ(sd::Status::OK, result.status());

  auto z = result.at(0);

ASSERT_EQ(exp,*z);
}

TEST_F(IndexingTests, SimpleSlice_1) {
  auto input = NDArrayFactory::create<float>('c', {3, 2, 3}, {1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6});

  auto exp = NDArrayFactory::create<float>('c', {1, 1, 3});
  exp.p(0, 3.0f);
  exp.p(1, 3.0f);
  exp.p(2, 3.0f);

  ops::slice op;

  auto result = op.evaluate({&input}, {}, {1, 0, 0, 1, 1, 3});
  ASSERT_EQ(sd::Status::OK, result.status());

  auto z = result.at(0);

  ASSERT_TRUE(exp.isSameShape(z));

  ASSERT_TRUE(exp.equalsTo(z));
}

TEST_F(IndexingTests, SimpleSlice_2) {
  auto input = NDArrayFactory::create<float>('c', {3, 2, 3}, {1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6});

  auto exp = NDArrayFactory::create<float>('c', {1, 2, 3});
  exp.p(0, 3.0f);
  exp.p(1, 3.0f);
  exp.p(2, 3.0f);
  exp.p(3, 4.0f);
  exp.p(4, 4.0f);
  exp.p(5, 4.0f);

  ops::slice op;

  auto result = op.evaluate({&input}, {}, {1, 0, 0, 1, 2, 3});
  ASSERT_EQ(sd::Status::OK, result.status());

  auto z = result.at(0);

  ASSERT_TRUE(exp.isSameShape(z));

  ASSERT_TRUE(exp.equalsTo(z));
}

TEST_F(IndexingTests, SimpleSlice_3) {
  auto input = NDArrayFactory::create<float>('c', {3, 2, 3}, {1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6});

  auto exp = NDArrayFactory::create<float>('c', {2, 1, 3});
  exp.p(0, 3.0f);
  exp.p(1, 3.0f);
  exp.p(2, 3.0f);
  exp.p(3, 5.0f);
  exp.p(4, 5.0f);
  exp.p(5, 5.0f);

  ops::slice op;

  auto result = op.evaluate({&input}, {}, {1, 0, 0, 2, 1, 3});
  ASSERT_EQ(sd::Status::OK, result.status());

  auto z = result.at(0);

  ASSERT_TRUE(exp.isSameShape(z));

  ASSERT_TRUE(exp.equalsTo(z));
}

TEST_F(IndexingTests, SimpleSlice_4) {
  auto input = NDArrayFactory::create<double>('c', {3, 2, 3}, {1.0, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6});
  auto start = NDArrayFactory::create<double>('c', {3}, {1.0, 0.0, 0.0});
  auto stop = NDArrayFactory::create<double>('c', {3}, {2.0, 1.0, 3.0});
  auto exp = NDArrayFactory::create<double>('c', {2, 1, 3}, {3.0, 3.0, 3.0, 5.0, 5.0, 5.0});

  ops::slice op;

  auto result = op.evaluate({&input, &start, &stop});
  ASSERT_EQ(sd::Status::OK, result.status());

  auto z = result.at(0);

ASSERT_EQ(exp,*z);
}

TEST_F(IndexingTests, MaskedSlice_0) {
  auto matrix = NDArrayFactory::create<float>('c', {3, 5});
  auto tads = matrix.allTensorsAlongDimension({1});
  for (int e = 0; e < tads.size(); e++) {
    tads.at(e)->assign((float)(e + 1));
  }

  auto exp = NDArrayFactory::create<float>('c', {1, 5});
  exp.assign(2.0f);

  ops::strided_slice op;
  auto result = op.evaluate({&matrix}, {}, {0, 0, 0, 0, 0, 1, 2, 1});

  ASSERT_EQ(sd::Status::OK, result.status());

  auto z = result.at(0);
ASSERT_EQ(exp,*z);
}

TEST_F(IndexingTests, MaskedSlice_00) {
  auto matrix = NDArrayFactory::create<float>('c', {3, 5});
  auto tads = matrix.allTensorsAlongDimension({1});
  for (int e = 0; e < tads.size(); e++) {
    tads.at(e)->assign((float)(e + 1));
  }

  auto exp = NDArrayFactory::create<float>('c', {1, 2}, {2, 2});

  ops::strided_slice op;
  auto result = op.evaluate({&matrix}, {}, {0, 0, 0, 0, 0, 1, 1, 2, 3, 1, 1});

  ASSERT_EQ(sd::Status::OK, result.status());

  auto z = result.at(0);

ASSERT_EQ(exp,*z);
}

TEST_F(IndexingTests, MaskedSlice_1) {
  auto matrix = NDArrayFactory::create<float>('c', {3, 5});
  auto tads = matrix.allTensorsAlongDimension({1});
  for (int e = 0; e < tads.size(); e++) {
    tads.at(e)->assign((float)(e + 1));
  }

  auto exp = NDArrayFactory::create<float>('c', {5});
  exp.assign(2.0f);

  ops::strided_slice op;
  auto result = op.evaluate({&matrix}, {}, {0, 0, 0, 0, 1, 1, 2, 1});

  ASSERT_EQ(sd::Status::OK, result.status());

  auto z = result.at(0);
ASSERT_EQ(exp,*z);
}

TEST_F(IndexingTests, MaskedSlice_2) {
  auto matrix = NDArrayFactory::create<float>(
      'c', {3, 3, 3}, {1.f,  1.2f, 1.3f, 2.f,  2.2f, 2.3f, 3.f,  3.2f, 3.3f, 4.f,  4.2f, 4.3f, 5.f, 5.2f,
                       5.3f, 6.f,  6.2f, 6.3f, 7.f,  7.2f, 7.3f, 8.f,  8.2f, 8.3f, 9.f,  9.2f, 9.3f});
  auto exp = NDArrayFactory::create<float>(
      'c', {3, 3}, {4.000000f, 4.200000f, 4.300000f, 5.000000f, 5.200000f, 5.300000f, 6.000000f, 6.200000f, 6.300000f});

  // output = tf.strided_slice(a, [1, 0, 0], [3, 3, 3], shrink_axis_mask=5)
  ops::strided_slice op;
  auto result = op.evaluate({&matrix}, {}, {0, 0, 0, 0, 1, 1, 0, 0, 3, 3, 3, 1, 1, 1});

  ASSERT_EQ(sd::Status::OK, result.status());

  auto z = result.at(0);

ASSERT_EQ(exp,*z);
}

TEST_F(IndexingTests, MaskedSlice_3) {
  auto matrix = NDArrayFactory::create<float>(
      'c', {3, 3, 3}, {1.f,  1.2f, 1.3f, 2.f,  2.2f, 2.3f, 3.f,  3.2f, 3.3f, 4.f,  4.2f, 4.3f, 5.f, 5.2f,
                       5.3f, 6.f,  6.2f, 6.3f, 7.f,  7.2f, 7.3f, 8.f,  8.2f, 8.3f, 9.f,  9.2f, 9.3f});
  auto exp = NDArrayFactory::create<float>('c', {2, 3}, {4.f, 4.2f, 4.3f, 7.f, 7.2f, 7.3f});

  // output = tf.strided_slice(a, [1, 0, 0], [3, 3, 3], shrink_axis_mask=5)
  ops::strided_slice op;
  auto result = op.evaluate({&matrix}, {}, {0, 0, 0, 0, 2, 1, 0, 0, 3, 3, 3, 1, 1, 1});

  ASSERT_EQ(sd::Status::OK, result.status());

  auto z = result.at(0);

ASSERT_EQ(exp,*z);
}

TEST_F(IndexingTests, MaskedSlice_4) {
  auto matrix = NDArrayFactory::create<float>(
      'c', {3, 3, 3}, {1.f,  1.2f, 1.3f, 2.f,  2.2f, 2.3f, 3.f,  3.2f, 3.3f, 4.f,  4.2f, 4.3f, 5.f, 5.2f,
                       5.3f, 6.f,  6.2f, 6.3f, 7.f,  7.2f, 7.3f, 8.f,  8.2f, 8.3f, 9.f,  9.2f, 9.3f});
  auto exp = NDArrayFactory::create<float>('c', {3}, {4.f, 4.2f, 4.3f});

  // output = tf.strided_slice(a, [1, 0, 0], [3, 3, 3], shrink_axis_mask=5)
  ops::strided_slice op;
  auto result = op.evaluate({&matrix}, {}, {0, 0, 0, 0, 3, 1, 0, 0, 3, 3, 3, 1, 1, 1});

  ASSERT_EQ(sd::Status::OK, result.status());

  auto z = result.at(0);

ASSERT_EQ(exp,*z);
}

TEST_F(IndexingTests, Live_Slice_1) {
  auto matrix = NDArrayFactory::create<float>(
      'c', {3, 3, 3}, {1.f,  1.2f, 1.3f, 2.f,  2.2f, 2.3f, 3.f,  3.2f, 3.3f, 4.f,  4.2f, 4.3f, 5.f, 5.2f,
                       5.3f, 6.f,  6.2f, 6.3f, 7.f,  7.2f, 7.3f, 8.f,  8.2f, 8.3f, 9.f,  9.2f, 9.3f});
  auto exp = NDArrayFactory::create<float>('c', {3}, {4.f, 4.2f, 4.3f});

  auto begin = NDArrayFactory::create<float>('c', {3}, {1.0f, 0.0f, 0.0f});
  auto end = NDArrayFactory::create<float>('c', {3}, {3.0f, 3.0f, 3.0f});
  auto stride = NDArrayFactory::create<float>('c', {3}, {1.0f, 1.0f, 1.0f});

  // output = tf.strided_slice(a, [1, 0, 0], [3, 3, 3], shrink_axis_mask=5)
  ops::strided_slice op;
  auto result = op.evaluate({&matrix, &begin, &end, &stride}, {}, {0, 0, 0, 0, 3});

  ASSERT_EQ(sd::Status::OK, result.status());

  auto z = result.at(0);
ASSERT_EQ(exp,*z);
}

TEST_F(IndexingTests, Test_StridedSlice_1) {
  auto x = NDArrayFactory::create<float>('c', {1, 2}, {5.f, 2.f});
  auto a = NDArrayFactory::create<float>('c', {1}, {0.f});
  auto b = NDArrayFactory::create<float>('c', {1}, {1.f});
  auto c = NDArrayFactory::create<float>('c', {1}, {1.f});
  auto exp = NDArrayFactory::create<float>({5.0f, 2});

  ops::strided_slice op;
  auto result = op.evaluate({&x, &a, &b, &c}, {}, {0, 0, 0, 0, 1});

  ASSERT_EQ(sd::Status::OK, result.status());

  auto z = result.at(0);

ASSERT_EQ(exp,*z);
}

TEST_F(IndexingTests, Test_StridedSlice_2) {
  auto x = NDArrayFactory::create<float>('c', {2, 3}, {1, 2, 3, 4, 5, 6});
  auto a = NDArrayFactory::create<float>('c', {2}, {1, 1});
  auto b = NDArrayFactory::create<float>('c', {2}, {2, 2});
  auto c = NDArrayFactory::create<float>('c', {2}, {1, 1});
  auto exp = NDArrayFactory::create<float>('c', {1}, {5.0});

  ops::strided_slice op;
  auto result = op.evaluate({&x, &a, &b, &c}, {}, {0, 0, 0, 0, 1});

  ASSERT_EQ(sd::Status::OK, result.status());

  auto z = result.at(0);
ASSERT_EQ(exp,*z);
}

TEST_F(IndexingTests, Test_StridedSlice_3) {
  auto x = NDArrayFactory::create<float>('c', {2, 3}, {1, 2, 3, 4, 5, 6});
  auto a = NDArrayFactory::create<float>('c', {2}, {1, 2});
  auto b = NDArrayFactory::create<float>('c', {2}, {2, 3});
  auto c = NDArrayFactory::create<float>('c', {2}, {1, 1});
  auto exp = NDArrayFactory::create<float>('c', {1}, {6.0});

  ops::strided_slice op;
  auto result = op.evaluate({&x, &a, &b, &c}, {}, {0, 0, 0, 0, 1});

  ASSERT_EQ(sd::Status::OK, result.status());

  auto z = result.at(0);

ASSERT_EQ(exp,*z);
}

TEST_F(IndexingTests, Test_StridedSlice_4) {
  auto x = NDArrayFactory::create<float>('c', {1, 2}, {5, 2});
  auto a = NDArrayFactory::create<float>('c', {1}, {0.});
  auto b = NDArrayFactory::create<float>('c', {1}, {1});
  auto c = NDArrayFactory::create<float>('c', {1}, {1});
  auto exp = NDArrayFactory::create<float>({5.0f, 2});

  ops::strided_slice op;
  auto result = op.evaluate({&x, &a, &b, &c}, {}, {0, 0, 0, 0, 1});
  ASSERT_EQ(sd::Status::OK, result.status());

  auto z = result.at(0);
ASSERT_EQ(exp,*z);
}

TEST_F(IndexingTests, Test_Subarray_Strided_1) {
  auto x = NDArrayFactory::create<float>('c', {3, 3}, {1, 2, 3, 4, 5, 6, 7, 8, 9});
  auto exp = NDArrayFactory::create<float>('c', {3, 2}, {1, 3, 4, 6, 7, 9});
  auto sub = x({0, 0, 0, 0, 3, 2}, true, true);

  ASSERT_TRUE(exp.isSameShape(sub));
  ASSERT_TRUE(exp.equalsTo(sub));
}


