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

import mr.go.sgfilter.SGFilter;
import mr.go.sgfilter.vis.Plot;

public class SGFilterBean extends PlotableComputation implements Editable {

	private double[]				coefficients;

	private int						degree					= 4;

	private final JPanel			editor;

	private final SGFilter			filter					= new SGFilter(6, 6);

	private PropertyChangeSupport	propertyChangeSupport	= new PropertyChangeSupport(
																	this);

	public SGFilterBean() {
		super(PLOT);
		nodeType = NodeType.FILTER;
		PLOT.setXsBeginning(filter.getNl());
		editor = new SGFilterEditor(this);
		coefficients = SGFilter.computeSGCoefficients(6, 6, 4);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public int getDegree() {
		return degree;
	}

	public JPanel getEditorPane() {
		return editor;
	}

	public int getNl() {
		return filter.getNl();
	}

	public int getNr() {
		return filter.getNr();
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void setDegree(int degree) {
		int oldDegree = this.degree;
		this.degree = degree;
		coefficients = SGFilter.computeSGCoefficients(getNl(), getNr(), degree);
		propertyChangeSupport
				.firePropertyChange(PROP_DEGREE, oldDegree, degree);
	}

	public void setNl(int nl) {
		int oldNl = filter.getNl();
		filter.setNl(nl);
		coefficients = SGFilter.computeSGCoefficients(getNl(), getNr(), degree);
		getPlot().setXsBeginning(nl);
		propertyChangeSupport.firePropertyChange(PROP_NL, oldNl, nl);
	}

	public void setNr(int nr) {
		int oldNr = filter.getNr();
		filter.setNr(nr);
		coefficients = SGFilter.computeSGCoefficients(getNl(), getNr(), degree);
		propertyChangeSupport.firePropertyChange(PROP_NR, oldNr, nr);
	}

	@Override
	public String toString() {
		return application.getString("FILTER");
	}

	@Override
	protected double[] compute(double[] data) {
		return filter.smooth(
				data,
				filter.getNl(),
				data.length - filter.getNr(),
				coefficients);
	}

	public static final String			PROP_DEGREE	= "degree";

	public static final String			PROP_NL		= "nl";

	public static final String			PROP_NR		= "nr";

	private static final ResourceBundle	application	= ResourceBundle
															.getBundle("mr/go/sgfilter/vis/app/resources/Application");

	private static final Plot			PLOT;

	static {
		PLOT = new Plot();
		PLOT.setColor(Color.BLUE);
		PLOT.setLine(true);
		PLOT.setTag(NodeType.FILTER);
	}
}
