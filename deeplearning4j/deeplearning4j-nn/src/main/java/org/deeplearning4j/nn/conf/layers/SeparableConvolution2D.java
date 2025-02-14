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

package org.deeplearning4j.nn.conf.layers;

import lombok.*;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.ParamInitializer;
import org.deeplearning4j.nn.api.layers.LayerConstraint;
import org.deeplearning4j.nn.conf.CNN2DFormat;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.layers.convolution.SeparableConvolution2DLayer;
import org.deeplearning4j.nn.params.SeparableConvolutionParamInitializer;
import org.deeplearning4j.optimize.api.TrainingListener;
import org.deeplearning4j.util.ConvolutionUtils;
import org.deeplearning4j.util.ValidationUtils;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.*;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SeparableConvolution2D extends ConvolutionLayer {

    int depthMultiplier;

    /**
     * SeparableConvolution2D layer nIn in the input layer is the number of channels nOut is the number of filters to be
     * used in the net or in other words the channels The builder specifies the filter/kernel size, the stride and
     * padding The pooling layer takes the kernel size
     */
    protected SeparableConvolution2D(Builder builder) {
        super(builder);
        this.hasBias = builder.hasBias;
        this.depthMultiplier = builder.depthMultiplier;
        this.convolutionMode = builder.convolutionMode;
        this.dilation = builder.dilation;
        if (builder.kernelSize.length != 2) {
            throw new IllegalArgumentException("Kernel size of should be rows x columns (a 2d array)");
        }
        this.kernelSize = builder.kernelSize;
        if (builder.stride.length != 2) {
            throw new IllegalArgumentException("Stride should include stride for rows and columns (a 2d array)");
        }
        this.stride = builder.stride;
        if (builder.padding.length != 2) {
            throw new IllegalArgumentException("Padding should include padding for rows and columns (a 2d array)");
        }
        this.padding = builder.padding;
        this.cudnnAlgoMode = builder.cudnnAlgoMode;
        this.cudnnFwdAlgo = builder.cudnnFwdAlgo;
        this.cudnnBwdFilterAlgo = builder.cudnnBwdFilterAlgo;
        this.cudnnBwdDataAlgo = builder.cudnnBwdDataAlgo;
        this.cnn2dDataFormat = builder.dataFormat;


        initializeConstraints(builder);
    }

    @Override
    protected void initializeConstraints(org.deeplearning4j.nn.conf.layers.Layer.Builder<?> builder) {
        super.initializeConstraints(builder);
        if (((Builder) builder).pointWiseConstraints != null) {
            if (constraints == null) {
                constraints = new ArrayList<>();
            }
            for (LayerConstraint constraint : ((Builder) builder).pointWiseConstraints) {
                LayerConstraint clonedConstraint = constraint.clone();
                clonedConstraint.setParams(
                                Collections.singleton(SeparableConvolutionParamInitializer.POINT_WISE_WEIGHT_KEY));
                constraints.add(clonedConstraint);
            }
        }
    }

    public boolean hasBias() {
        return hasBias;
    }

    @Override
    public SeparableConvolution2D clone() {
        SeparableConvolution2D clone = (SeparableConvolution2D) super.clone();
        if (clone.kernelSize != null) {
            clone.kernelSize = clone.kernelSize.clone();
        }
        if (clone.stride != null) {
            clone.stride = clone.stride.clone();
        }
        if (clone.padding != null) {
            clone.padding = clone.padding.clone();
        }
        return clone;
    }

    @Override
    public Layer instantiate(NeuralNetConfiguration conf, Collection<TrainingListener> trainingListeners,
                             int layerIndex, INDArray layerParamsView, boolean initializeParams, DataType networkDataType) {
        LayerValidation.assertNInNOutSet("SeparableConvolution2D", getLayerName(), layerIndex, getNIn(), getNOut());

        SeparableConvolution2DLayer ret =
                        new SeparableConvolution2DLayer(conf, networkDataType);
        ret.setListeners(trainingListeners);
        ret.setIndex(layerIndex);
        ret.setParamsViewArray(layerParamsView);
        Map<String, INDArray> paramTable = initializer().init(conf, layerParamsView, initializeParams);
        ret.setParamTable(paramTable);
        ret.setConf(conf);

        return ret;
    }

    @Override
    public ParamInitializer initializer() {
        return SeparableConvolutionParamInitializer.getInstance();
    }

    @Override
    public InputType getOutputType(int layerIndex, InputType inputType) {
        if (inputType == null || inputType.getType() != InputType.Type.CNN) {
            throw new IllegalStateException("Invalid input for Convolution layer (layer name=\"" + getLayerName()
                            + "\"): Expected CNN input, got " + inputType);
        }

        CNN2DFormat format = ((InputType.InputTypeConvolutional)inputType).getFormat();

        return InputTypeUtil.getOutputTypeCnnLayersLong(inputType, kernelSize, stride, padding, dilation, convolutionMode,
                        nOut, layerIndex, getLayerName(), format, SeparableConvolution2DLayer.class);
    }


    @Getter
    @Setter
    public static class Builder extends BaseConvBuilder<Builder> {

        /**
         * Set channels multiplier of channels-wise step in separable convolution
         *
         */
        protected int depthMultiplier = 1;
        protected CNN2DFormat dataFormat;

        public Builder(int[] kernelSize, int[] stride, int[] padding) {
            super(kernelSize, stride, padding);
        }

        public Builder(int[] kernelSize, int[] stride) {
            super(kernelSize, stride);
        }

        public Builder(int... kernelSize) {
            super(kernelSize);
        }

        public Builder() {
            super();
        }

        @Override
        protected boolean allowCausal() {
            //Causal convolution - allowed for 1D only
            return false;
        }

        /**
         * Set the data format for the CNN activations - NCHW (channels first) or NHWC (channels last).
         * See {@link CNN2DFormat} for more details.<br>
         * Default: NCHW
         * @param format Format for activations (in and out)
         */
        public Builder dataFormat(CNN2DFormat format){
            this.dataFormat = format;
            return this;
        }

        /**
         * Set channels multiplier of channels-wise step in separable convolution
         *
         * @param depthMultiplier integer value, for each input map we get depthMultipler outputs in channels-wise
         * step.
         * @return Builder
         */
        public Builder depthMultiplier(int depthMultiplier) {
            this.setDepthMultiplier(depthMultiplier);
            return this;
        }

        /**
         * Set constraints to be applied to the point-wise convolution weight parameters of this layer. Default: no
         * constraints.<br> Constraints can be used to enforce certain conditions (non-negativity of parameters,
         * max-norm regularization, etc). These constraints are applied at each iteration, after the parameters have
         * been updated.
         */
        protected List<LayerConstraint> pointWiseConstraints;

        /**
         * Set constraints to be applied to the point-wise convolution weight parameters of this layer. Default: no
         * constraints.<br> Constraints can be used to enforce certain conditions (non-negativity of parameters,
         * max-norm regularization, etc). These constraints are applied at each iteration, after the parameters have
         * been updated.
         *
         * @param constraints Constraints to apply to the point-wise convolution parameters of this layer
         */
        public Builder constrainPointWise(LayerConstraint... constraints) {
            this.setPointWiseConstraints(Arrays.asList(constraints));
            return this;
        }

        /**
         * Size of the convolution rows/columns (height/width)
         *
         * @param kernelSize the height and width of the kernel
         */
        public Builder kernelSize(long... kernelSize) {
            this.setKernelSize(kernelSize);
            return this;
        }

        /**
         * Stride of the convolution rows/columns (height/width)
         *
         * @param stride the stride of the kernel (in h/w dimensions)
         */
        public Builder stride(long... stride) {
            this.setStride(stride);
            return this;
        }

        /**
         * Padding - rows/columns (height/width)
         *
         * @param padding the padding in h/w dimensions
         */
        public Builder padding(long... padding) {
            this.setPadding(padding);
            return this;
        }

        @Override
        public void setKernelSize(long... kernelSize) {
            this.kernelSize = ValidationUtils.validate2NonNegative(kernelSize, false, "kernelSize");
        }

        @Override
        public void setStride(long... stride) {
            this.stride = ValidationUtils.validate2NonNegative(stride, false, "stride");
        }

        @Override
        public void setPadding(long... padding) {
            this.padding = ValidationUtils.validate2NonNegative(padding, false, "padding");
        }

        @Override
        @SuppressWarnings("unchecked")
        public SeparableConvolution2D build() {
            ConvolutionUtils.validateConvolutionModePadding(convolutionMode, padding);
            ConvolutionUtils.validateCnnKernelStridePadding(kernelSize, stride, padding);

            return new SeparableConvolution2D(this);
        }
    }

}
