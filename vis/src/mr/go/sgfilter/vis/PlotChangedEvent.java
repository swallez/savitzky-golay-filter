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

import java.util.EventObject;

public class PlotChangedEvent extends EventObject {

    protected final Plot changed;

    protected final int plotIndex;

    protected final String plotProperty;

    public PlotChangedEvent(
            PlotModel source,
            Plot changed,
            int plotIndex,
            String plotProperty) {
        super(source);
        this.changed = changed;
        this.plotIndex = plotIndex;
        this.plotProperty = plotProperty;
    }

    public Plot getChanged() {
        return changed;
    }

    public int getPlotIndex() {
        return plotIndex;
    }

    public String getPlotProperty() {
        return plotProperty;
    }

    private static final long serialVersionUID = -5541511534559402100L;

}
