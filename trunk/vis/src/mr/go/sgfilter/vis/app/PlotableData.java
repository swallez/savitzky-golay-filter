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

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import mr.go.sgfilter.vis.Plot;

public class PlotableData implements Plotable {

	protected PropertyChangeSupport	propertyChangeSupport	= new PropertyChangeSupport(
																	this);

	private final Plot				plot;

	private boolean					silentDataUpdate;

	protected PlotableData(
			Plot plot) {
		this.plot = plot;
		plot.addPropertyChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				if (!silentDataUpdate
					&& evt.getPropertyName().equals(Plot.PROP_DATA)) {
					if (evt instanceof IndexedPropertyChangeEvent) {
						propertyChangeSupport.fireIndexedPropertyChange(
								PROP_DATA,
								((IndexedPropertyChangeEvent) evt).getIndex(),
								evt.getOldValue(),
								evt.getNewValue());
					} else {
						propertyChangeSupport.firePropertyChange(PROP_DATA, evt
								.getOldValue(), evt.getNewValue());
					}
				}
			}
		});
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public double[] getData() {
		return plot.getData();
	}

	public double getData(int index) {
		return plot.getData(index);
	}

	public NodeType getNodeType() {
		return nodeType;
	}

	public Plot getPlot() {
		return plot;
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void setData(double[] data) {
		plot.setData(data);
	}

	public void setData(int index, double newData) {
		plot.setData(index, newData);
	}

	public final void updatePlot(double[] data) {
	}

	protected void updateData(double[] data) {
		silentDataUpdate = true;
		plot.setData(data);
		silentDataUpdate = false;
	}

	public static final String		PROP_DATA	= "data";

	private static final NodeType	nodeType	= NodeType.DATA;
}
