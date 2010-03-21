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

import mr.go.sgfilter.RamerDouglasPeuckerFilter;
import mr.go.sgfilter.vis.Plot;

public class RDPBean extends PlotableComputation implements Editable {

    private final JPanel editor;

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
            this);

    private final RamerDouglasPeuckerFilter rdp = new RamerDouglasPeuckerFilter(
            1.0);

    public RDPBean() {
        super(PLOT);
        nodeType = NodeType.DATA_FILTER;
        editor = new RamerDouglasPeuckerEditor(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public JPanel getEditorPane() {
        return editor;
    }

    public double getEpsilon() {
        return rdp.getEpsilon();
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void setEpsilon(double epsilon) {
        double oldEpsilon = rdp.getEpsilon();
        rdp.setEpsilon(epsilon);
        propertyChangeSupport.firePropertyChange(
                PROP_EPSILON,
                oldEpsilon,
                epsilon);
    }

    @Override
    public String toString() {
        return application.getString("RAMER-DOUGLAS-PEUCKER_FILTER");
    }

    @Override
    protected double[] compute(double[] data) {
        return rdp.filter(data);
    }

    public static final String PROP_EPSILON = "epsilon";

    private static final ResourceBundle application = ResourceBundle.getBundle(
            "mr/go/sgfilter/vis/app/resources/Application");

    private static final Plot PLOT;

    static {
        PLOT = new Plot();
        PLOT.setColor(Color.RED);
        PLOT.setLine(true);
        PLOT.setTag(NodeType.DATA_FILTER);
    }

}
