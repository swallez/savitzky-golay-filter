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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ResourceBundle;

import javax.swing.JPanel;

import mr.go.sgfilter.ContinuousPadder;
import mr.go.sgfilter.vis.Plot;

public class ContinuousPadderBean extends PlotableComputation implements
		Editable {

	private final JPanel			editor;

	private final ContinuousPadder	padder;

	private PropertyChangeSupport	propertyChangeSupport	= new PropertyChangeSupport(
																	this);

	public ContinuousPadderBean() {
		super(PLOT);
		nodeType = NodeType.PREPROCESSOR;
		padder = new ContinuousPadder();
		editor = new ContinuousPadderEditor(this);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public JPanel getEditorPane() {
		return editor;
	}

	public boolean isPaddingLeft() {
		return padder.isPaddingLeft();
	}

	public boolean isPaddingRight() {
		return padder.isPaddingRight();
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void setPaddingLeft(boolean paddingLeft) {
		boolean oldPaddingLeft = padder.isPaddingLeft();
		padder.setPaddingLeft(paddingLeft);
		propertyChangeSupport.firePropertyChange(
				PROP_PADDINGLEFT,
				oldPaddingLeft,
				paddingLeft);
	}

	public void setPaddingRight(boolean paddingRight) {
		boolean oldPaddingRight = padder.isPaddingRight();
		padder.setPaddingRight(paddingRight);
		propertyChangeSupport.firePropertyChange(
				PROP_PADDINGRIGHT,
				oldPaddingRight,
				paddingRight);
	}

	@Override
	public String toString() {
		return application.getString("CONTINUOUS_PAD");
	}

	@Override
	protected double[] compute(double[] data) {
		data = data.clone();
		padder.apply(data);
		return data;
	}

	public static final String			PROP_PADDINGLEFT	= "paddingLeft";

	public static final String			PROP_PADDINGRIGHT	= "paddingRight";

	private static final ResourceBundle	application			= ResourceBundle
																	.getBundle("mr/go/sgfilter/vis/app/resources/Application");

	private static final Plot			PLOT;

	static {
		PLOT = new Plot();
		PLOT.setColor(Color.ORANGE);
		PLOT.setLine(true);
		PLOT.setTag(NodeType.PREPROCESSOR);
	}
}
