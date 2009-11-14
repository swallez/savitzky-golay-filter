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

import mr.go.sgfilter.vis.Plot;

public abstract class PlotableComputation implements Plotable {

	protected NodeType	nodeType;

	private final Plot	plot;

	public PlotableComputation(
			Plot plot) {
		this.plot = plot;
	}

	public NodeType getNodeType() {
		return nodeType;
	}

	public Plot getPlot() {
		return plot;
	}

	public void updatePlot(double[] data) {
		if (data == Plot.EMPTY_DATA) {
			return;
		}
		double[] newData = compute(data);
		plot.setData(newData);
	}

	abstract protected double[] compute(double[] data);
}
