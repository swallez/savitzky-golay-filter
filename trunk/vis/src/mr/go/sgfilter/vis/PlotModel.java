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

package mr.go.sgfilter.vis;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.event.EventListenerList;

public class PlotModel {

	private final EventListenerList				listenerList	= new EventListenerList();

	private int									maxX;

	private int									maxXPlotIndex	= -1;

	private double								maxY			= Double.NEGATIVE_INFINITY;

	private int									maxYPlotIndex	= -1;

	private double								minY			= Double.POSITIVE_INFINITY;

	private int									minYPlotIndex	= -1;

	private final List<Plot>					plots;

	private final PropertyChangeListenerImpl	propertyChangeListener;

	public PlotModel() {
		propertyChangeListener = new PropertyChangeListenerImpl();
		plots = new ArrayList<Plot>();
	}

	public PlotModel(
			Plot[] plots) {
		propertyChangeListener = new PropertyChangeListenerImpl();
		this.plots = new ArrayList<Plot>(plots.length);
		for (Plot plot : plots) {
			this.plots.add(plot);
			plot.addPropertyChangeListener(propertyChangeListener);
		}
		setMaxima();
	}

	public void addPlot(int index, Plot plot) {
		this.plots.add(index, plot);
		plot.addPropertyChangeListener(propertyChangeListener);
		updateMaxima(plot, index);
		PlotChangedEvent ev = new PlotChangedEvent(this, plot, index, null);
		firePlotAdded(ev);
	}

	public void addPlot(final Plot plot) {
		this.plots.add(plot);
		int index = this.plots.size() - 1;
		plot.addPropertyChangeListener(propertyChangeListener);
		updateMaxima(plot, index);
		PlotChangedEvent ev = new PlotChangedEvent(this, plot, index, null);
		firePlotAdded(ev);
	}

	public void addPlotChangeListener(PlotChangeListener listener) {
		listenerList.add(PlotChangeListener.class, listener);
	}

	public void computeExactMaxima() {
		setMaxima();
	}

	public int getMaximumLength() {
		return maxX;
	}

	public double getMaximumValue() {
		return maxY;
	}

	public double getMinimumValue() {
		return minY;
	}

	public Plot getPlot(int plotIndex) {
		return this.plots.get(plotIndex);
	}

	public int getPlotsCount() {
		return this.plots.size();
	}

	public int indexOf(Plot plot) {
		return this.plots.indexOf(plot);
	}

	public boolean isReady() {
		return maxY != Double.NEGATIVE_INFINITY
				&& minY != Double.POSITIVE_INFINITY;
	}

	public void removeAll() {
		if (!plots.isEmpty()) {
			maxXPlotIndex = -1;
			maxYPlotIndex = -1;
			minYPlotIndex = -1;
			maxX = 0;
			maxY = Double.NEGATIVE_INFINITY;
			minY = Double.POSITIVE_INFINITY;
			for (Plot plot : plots) {
				plot.removePropertyChangeListener(propertyChangeListener);
			}
			plots.clear();
			firePlotsRemoved();
		}
	}

	public void removePlot(int index) {
		Plot removedPlot = this.plots.remove(index);
		removedPlot.removePropertyChangeListener(propertyChangeListener);
		if (maximaChanged(index)) {
			setMaxima();
		}
		PlotChangedEvent ev = new PlotChangedEvent(
				this,
				removedPlot,
				index,
				null);
		firePlotRemoved(ev);
	}

	public void removePlot(Plot plot) {
		int index = this.plots.indexOf(plot);
		if (index == -1) {
			return;
		}
		removePlot(index);
	}

	public void removePlotChangeListener(PlotChangeListener listener) {
		listenerList.remove(PlotChangeListener.class, listener);
	}

	protected void fireMaximaChanged() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PlotChangeListener.class) {
				((PlotChangeListener) listeners[i + 1]).maximaChanged();
			}
		}
	}

	protected void firePlotAdded(PlotChangedEvent plotEvent) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PlotChangeListener.class) {
				((PlotChangeListener) listeners[i + 1]).plotAdded(plotEvent);
			}
		}
	}

	protected void firePlotColorChanged(PlotChangedEvent ev) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PlotChangeListener.class) {
				((PlotChangeListener) listeners[i + 1]).colorChanged(ev);
			}
		}
	}

	protected void firePlotDataChanged(PlotChangedEvent ev) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PlotChangeListener.class) {
				((PlotChangeListener) listeners[i + 1]).dataChanged(ev);
			}
		}
	}

	protected void firePlotDataChanged(PlotChangedEvent ev, int index) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PlotChangeListener.class) {
				((PlotChangeListener) listeners[i + 1]).dataChanged(ev, index);
			}
		}
	}

	protected void firePlotLineChanged(PlotChangedEvent ev) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PlotChangeListener.class) {
				((PlotChangeListener) listeners[i + 1]).isLineChanged(ev);
			}
		}
	}

	protected void firePlotRemoved(PlotChangedEvent plotEvent) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PlotChangeListener.class) {
				((PlotChangeListener) listeners[i + 1]).plotRemoved(plotEvent);
			}
		}
	}

	protected void firePlotsRemoved() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PlotChangeListener.class) {
				((PlotChangeListener) listeners[i + 1]).plotsRemoved();
			}
		}
	}

	protected void firePlotVisibilityChanged(PlotChangedEvent ev) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PlotChangeListener.class) {
				((PlotChangeListener) listeners[i + 1]).visibilityChanged(ev);
			}
		}
	}

	private boolean maximaChanged(int index) {
		return index == maxXPlotIndex || index == maxYPlotIndex
				|| index == minYPlotIndex;
	}

	private void setMaxima() {
		final int count = plots.size();
		int resultx = 0;
		double resultYMax = Double.NEGATIVE_INFINITY;
		double resultYMin = Double.POSITIVE_INFINITY;
		boolean changed = false;
		for (int i = 0; i < count; i++) {
			final Plot plot = plots.get(i);
			final double[] values = plot.getData().clone();
			final int length = values.length;
			if (length == 0) {
				continue;
			}
			Arrays.sort(values);
			final double max = values[length - 1];
			final double min = values[0];
			if (resultx < length) {
				resultx = length;
				maxXPlotIndex = i;
				changed = true;
			}
			if (resultYMax < max) {
				resultYMax = max;
				maxYPlotIndex = i;
				changed = true;
			}
			if (resultYMin > min) {
				resultYMin = min;
				minYPlotIndex = i;
				changed = true;
			}
		}
		maxX = resultx;
		maxY = resultYMax;
		minY = resultYMin;
		if (changed) {
			fireMaximaChanged();
		}
	}

	private void updateMaxima(Plot newPlot, int plotIndex) {
		double[] values = newPlot.getData().clone();
		int length = values.length;
		if (length == 0) {
			return;
		}
		Arrays.sort(values);
		double max = values[length - 1];
		double min = values[0];
		boolean changed = false;
		if (maxX < length) {
			maxX = length;
			maxXPlotIndex = plotIndex;
			changed = true;
		}
		if (maxY < max) {
			maxY = max;
			maxYPlotIndex = plotIndex;
			changed = true;
		}
		if (min < minY) {
			minY = min;
			minYPlotIndex = plotIndex;
			changed = true;
		}
		if (changed) {
			fireMaximaChanged();
		}
	}

	private class PropertyChangeListenerImpl implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			Plot plot = (Plot) evt.getSource();
			int indexOfPlot = indexOf(plot);
			String propertyName = evt.getPropertyName();
			PlotChangedEvent ev = new PlotChangedEvent(
					PlotModel.this,
					plot,
					indexOfPlot,
					propertyName);
			if (propertyName.equals(Plot.PROP_DATA)) {
				if (evt instanceof IndexedPropertyChangeEvent) {
					double newY = (Double) evt.getNewValue();
					boolean changed = false;
					if (newY > maxY) {
						changed = true;
						maxY = newY;
						maxYPlotIndex = indexOfPlot;
					} else if (newY < minY) {
						changed = true;
						minY = newY;
						minYPlotIndex = indexOfPlot;
					}
					if (changed) {
						fireMaximaChanged();
					}
					IndexedPropertyChangeEvent ipce = (IndexedPropertyChangeEvent) evt;
					firePlotDataChanged(ev, ipce.getIndex());
				} else {
					updateMaxima(plot, indexOfPlot);
					firePlotDataChanged(ev);
				}
			} else if (propertyName.equals(Plot.PROP_VISIBLE)) {
				firePlotVisibilityChanged(ev);
			} else if (propertyName.equals(Plot.PROP_COLOR)) {
				firePlotColorChanged(ev);
			} else if (propertyName.equals(Plot.PROP_LINE)) {
				firePlotLineChanged(ev);
			} else if (propertyName.equals(Plot.PROP_XSBEGINNING)) {
				if (plot.getXsEnd() > maxX) {
					maxX = plot.getXsEnd();
					maxXPlotIndex = ev.getPlotIndex();
					fireMaximaChanged();
				}
			}
		}
	}
}
