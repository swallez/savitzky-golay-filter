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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class PlotPoint {

    public final Plot plot;

    public final int x;

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
            this);

    public PlotPoint(
            Plot plot,
            int x) {
        this.plot = plot;
        this.x = x;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public double getY() {
        return plot.getData(x - plot.getXsBeginning());
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void setY(double y) {
        int x = this.x - plot.getXsBeginning();
        double oldY = plot.getData(x);
        plot.setData(x, y);
        propertyChangeSupport.firePropertyChange(PROP_Y, oldY, y);
    }

    public static final String PROP_Y = "y";

}
