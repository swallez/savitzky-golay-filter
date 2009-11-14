/*
 * Copyright [2009] [Marcin Rzeźnicki]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package mr.go.sgfilter.vis.app;

import java.awt.Color;
import java.text.NumberFormat;

import javax.swing.JPanel;

import mr.go.sgfilter.vis.Plot;

public class DataBean extends PlotableData implements Editable {

	protected int			leftPadSize		= 6;

	protected boolean		padding;

	protected int			rightPadSize	= 6;

	private int				currentLeftPad;

	private int				currentRigtPad;

	private final JPanel	editor;

	public DataBean() {
		super(PLOT);
		editor = new DataEditor(this);
	}

	@Override
	public JPanel getEditorPane() {
		return editor;
	}

	public int getLeftPadSize() {
		return leftPadSize;
	}

	public int getRightPadSize() {
		return rightPadSize;
	}

	public boolean isPadding() {
		return padding;
	}

	@Override
	public void setData(double[] data) {
		if (padding) {
			currentLeftPad = 0;
			currentRigtPad = 0;
			data = pad(data);
		}
		super.setData(data);
	}

	public void setLeftPadSize(int leftPadSize) {
		if (leftPadSize < 0) {
			throw new IllegalArgumentException("leftPadSize < 0");
		}
		int oldLeftPadSize = this.leftPadSize;
		this.leftPadSize = leftPadSize;
		updateData(pad(getData()));
		propertyChangeSupport.firePropertyChange(
				PROP_LEFTPADSIZE,
				oldLeftPadSize,
				leftPadSize);
	}

	public void setPadding(boolean padding) {
		boolean oldPadding = this.padding;
		this.padding = padding;
		updateData(pad(getData()));
		propertyChangeSupport.firePropertyChange(
				PROP_PADDING,
				oldPadding,
				padding);
	}

	public void setRightPadSize(int rightPadSize) {
		if (rightPadSize < 0) {
			throw new IllegalArgumentException("rightPadSize < 0");
		}
		int oldRightPadSize = this.rightPadSize;
		this.rightPadSize = rightPadSize;
		updateData(pad(getData()));
		propertyChangeSupport.firePropertyChange(
				PROP_RIGHTPADSIZE,
				oldRightPadSize,
				rightPadSize);
	}

	@Override
	public String toString() {
		double[] data = getData();
		if (data.length == 0) {
			return "[]";
		}
		NumberFormat formatter = NumberFormat.getNumberInstance();
		formatter.setMaximumFractionDigits(2);
		return '[' + formatter.format(data[0]) + " ... "
				+ formatter.format(data[data.length - 1]) + ']';
	}

	private double[] pad(double[] data) {
		double[] paddedData;
		int leftPadDelta, rightPadDelta;
		if (padding) {
			leftPadDelta = leftPadSize - currentLeftPad;
			rightPadDelta = rightPadSize - currentRigtPad;
			currentLeftPad = leftPadSize;
			currentRigtPad = rightPadSize;
		} else {
			leftPadDelta = -currentLeftPad;
			rightPadDelta = -currentRigtPad;
			currentLeftPad = 0;
			currentRigtPad = 0;
		}
		if (leftPadDelta == 0 && rightPadDelta == 0) {
			return data;
		}
		int newLength = leftPadDelta + data.length + rightPadDelta;
		int srcIndex = 0;
		int dstIndex = leftPadDelta;
		int copyLength = data.length;
		if (leftPadDelta <= 0) {
			srcIndex = -leftPadDelta;
			dstIndex = 0;
		}
		if (rightPadDelta <= 0) {
			copyLength += rightPadDelta;
		}
		paddedData = new double[newLength];
		System.arraycopy(data, srcIndex, paddedData, dstIndex, copyLength
																- srcIndex);

		return paddedData;
	}

	public static final String	PROP_LEFTPADSIZE	= "leftPadSize";

	public static final String	PROP_PADDING		= "padding";

	public static final String	PROP_RIGHTPADSIZE	= "rightPadSize";

	private static final Plot	PLOT;

	static {
		PLOT = new Plot();
		PLOT.setColor(Color.BLACK);
		PLOT.setLine(false);
		PLOT.setTag(NodeType.DATA);
	}
}
