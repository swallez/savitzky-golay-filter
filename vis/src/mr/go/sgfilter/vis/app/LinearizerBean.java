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

import mr.go.sgfilter.Linearizer;
import mr.go.sgfilter.vis.Plot;

public class LinearizerBean extends PlotableComputation implements Editable {

    private final JPanel editor;

    private final Linearizer linearizer = new Linearizer();

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
            this);

    public LinearizerBean() {
        super(PLOT);
        nodeType = NodeType.PREPROCESSOR;
        this.editor = new LinearizerEditor(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public JPanel getEditorPane() {
        return editor;
    }

    public float getTruncateRatio() {
        return linearizer.getTruncateRatio();
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void setTruncateRatio(float truncateRatio) {
        float oldTruncateRatio = linearizer.getTruncateRatio();
        linearizer.setTruncateRatio(truncateRatio);
        propertyChangeSupport.firePropertyChange(
                PROP_TRUNCATERATIO,
                oldTruncateRatio,
                truncateRatio);
    }

    @Override
    public String toString() {
        return application.getString("LINEARIZATION");
    }

    @Override
    protected double[] compute(double[] data) {
        data = data.clone();
        linearizer.apply(data);
        return data;
    }

    public static final String PROP_TRUNCATERATIO = "truncateRatio";

    private static final ResourceBundle application = ResourceBundle.getBundle(
            "mr/go/sgfilter/vis/app/resources/Application");

    private static final Plot PLOT;

    static {
        PLOT = new Plot();
        PLOT.setColor(Color.DARK_GRAY);
        PLOT.setLine(true);
        PLOT.setTag(NodeType.PREPROCESSOR);
    }

}
