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
import java.util.ResourceBundle;

import mr.go.sgfilter.TrendRemover;
import mr.go.sgfilter.vis.Plot;

public class TrendRemoverBean extends PlotableComputation {

	private final TrendRemover	trendRemover	= new TrendRemover();

	public TrendRemoverBean() {
		super(PLOT);
		nodeType = NodeType.PREPROCESSOR;
	}

	@Override
	public String toString() {
		return application.getString("DE-TREND");
	}

	@Override
	protected double[] compute(double[] data) {
		data = data.clone();
		trendRemover.apply(data);
		return data;
	}

	private static final ResourceBundle	application	= ResourceBundle
															.getBundle("mr/go/sgfilter/vis/app/resources/Application");

	private static final Plot			PLOT;

	static {
		PLOT = new Plot();
		PLOT.setColor(Color.CYAN);
		PLOT.setLine(true);
		PLOT.setTag(NodeType.PREPROCESSOR);
	}
}
