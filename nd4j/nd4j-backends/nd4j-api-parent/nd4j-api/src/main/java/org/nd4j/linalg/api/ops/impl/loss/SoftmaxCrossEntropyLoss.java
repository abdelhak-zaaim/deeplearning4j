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

package org.nd4j.linalg.api.ops.impl.loss;

import lombok.NoArgsConstructor;
import org.nd4j.autodiff.loss.LossReduce;
import org.nd4j.autodiff.samediff.SDVariable;
import org.nd4j.autodiff.samediff.SameDiff;
import org.nd4j.imports.NoOpNameFoundException;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.loss.bp.SoftmaxCrossEntropyLossBp;
import org.tensorflow.framework.AttrValue;
import org.tensorflow.framework.GraphDef;
import org.tensorflow.framework.NodeDef;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class SoftmaxCrossEntropyLoss extends BaseLoss {
    public static final double DEFAULT_LABEL_SMOOTHING = 0.0;

    private double labelSmoothing = 0.0;

    public SoftmaxCrossEntropyLoss(SameDiff sameDiff, SDVariable labels, SDVariable logits,
            SDVariable weights, LossReduce lossReduce, double labelSmoothing) {
        this(sameDiff, lossReduce, logits, weights, labels, labelSmoothing);
    }

    public SoftmaxCrossEntropyLoss(SameDiff sameDiff, LossReduce lossReduce, SDVariable logits, SDVariable weights,
            SDVariable labels,
            double labelSmoothing) {
        super(sameDiff, lossReduce, logits, weights, labels);
        this.labelSmoothing = labelSmoothing;
        tArguments.add(labelSmoothing);
    }

    public SoftmaxCrossEntropyLoss(SameDiff sameDiff, LossReduce lossReduce, SDVariable logits, SDVariable weights,
            SDVariable labels) {
        this(sameDiff, lossReduce, logits, weights, labels, 0.0);
    }

    public SoftmaxCrossEntropyLoss(INDArray labels, INDArray predictions, INDArray weights, LossReduce lossReduce,
            double labelSmoothing) {
        super(lossReduce, predictions, weights, labels);
        this.labelSmoothing = labelSmoothing;
        tArguments.add(labelSmoothing);
    }

    @Override
    public void initFromTensorFlow(NodeDef nodeDef, SameDiff initWith, Map<String, AttrValue> attributesForNode,
            GraphDef graph) {
        throw new UnsupportedOperationException("Use the new Tensorflow Importer instead. This method is now removed.");

    }

    @Override
    public String opName() {
        return "softmax_cross_entropy_loss";
    }

    @Override
    public String onnxName() {
        throw new NoOpNameFoundException("No onnx op opName found for " + opName());
    }

    @Override
    public String tensorflowName() {
        return "SoftmaxCrossEntropy";
    }

    @Override
    public List<SDVariable> doDiff(List<SDVariable> grad) {
        // No external gradient
        // Args are: predictions, weights, label
        if (tArguments.size() > 0) {
            this.labelSmoothing = tArguments.get(tArguments.size() - 1);
        }
        return new SoftmaxCrossEntropyLossBp(sameDiff, lossReduce, arg(0), arg(1), arg(2), this.labelSmoothing)
                .outputs();
    }
}
