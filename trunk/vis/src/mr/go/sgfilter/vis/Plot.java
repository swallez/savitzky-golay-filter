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

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class Plot implements Serializable {

    private Color color = Color.BLACK;

    private double[] data;

    private boolean line;

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
            this);

    private Object tag;

    private boolean visible = true;

    private int xsBeginning;

    public Plot() {
        this(EMPTY_DATA);
    }

    public Plot(
            double[] data) {
        this.data = data;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public Color getColor() {
        return color;
    }

    public double[] getData() {
        return data;
    }

    public double getData(int index) {
        return this.data[index];
    }

    public Object getTag() {
        return tag;
    }

    public int getXsBeginning() {
        return xsBeginning;
    }

    public int getXsEnd() {
        return xsBeginning + data.length;
    }

    public boolean isLine() {
        return line;
    }

    public boolean isVisible() {
        return visible;
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void setColor(Color color) {
        Color oldColor = this.color;
        this.color = color;
        propertyChangeSupport.firePropertyChange(PROP_COLOR, oldColor, color);
    }

    public void setData(double[] data) {
        double[] oldData = this.data;
        this.data = data;
        propertyChangeSupport.firePropertyChange(PROP_DATA, oldData, data);
    }

    public void setData(int index, double newData) {
        double oldData = this.data[index];
        this.data[index] = newData;
        propertyChangeSupport.fireIndexedPropertyChange(
                PROP_DATA,
                index,
                oldData,
                newData);
    }

    public void setLine(boolean line) {
        boolean oldLine = this.line;
        this.line = line;
        propertyChangeSupport.firePropertyChange(PROP_LINE, oldLine, line);
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public void setVisible(boolean visible) {
        boolean oldVisible = this.visible;
        this.visible = visible;
        propertyChangeSupport.firePropertyChange(
                PROP_VISIBLE,
                oldVisible,
                visible);
    }

    public void setXsBeginning(int xsBeginning) {
        int oldXsBeginning = this.xsBeginning;
        this.xsBeginning = xsBeginning;
        propertyChangeSupport.firePropertyChange(
                PROP_XSBEGINNING,
                oldXsBeginning,
                xsBeginning);
    }

    public static final double[] EMPTY_DATA = new double[0];

    public static final String PROP_COLOR = "color";

    public static final String PROP_DATA = "data";

    public static final String PROP_LINE = "line";

    public static final String PROP_VISIBLE = "visible";

    public static final String PROP_XSBEGINNING = "xsBeginning";

    private static final long serialVersionUID = -511933825464087092L;

}
