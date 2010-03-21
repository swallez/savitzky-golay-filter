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
import java.beans.PropertyVetoException;

import javax.swing.JComponent;
import javax.swing.UIManager;

public class SGFilterVis extends JComponent {

    protected Color gridColor;

    protected Color lockedSelectionColor;

    protected final PlotModel model;

    protected PlotPoint selectedPoint;

    protected boolean selectionLocked;

    private int zoom;

    private int maxZoom;

    public SGFilterVis() {
        model = new PlotModel();
        lockedSelectionColor = getForeground();
        gridColor = Color.LIGHT_GRAY;
        this.updateUI();
    }

    public Color getGridColor() {
        return gridColor;
    }

    public Color getLockedSelectionColor() {
        return lockedSelectionColor;
    }

    public PlotModel getModel() {
        return model;
    }

    public PlotPoint getSelectedPoint() {
        return selectedPoint;
    }

    public SGFilterVisUI getUI() {
        return (SGFilterVisUI) ui;
    }

    @Override
    public String getUIClassID() {
        return uiClassId;
    }

    public int getZoom() {
        return zoom;
    }

    public int getMaxZoom() {
        return maxZoom;
    }

    public boolean isSelectionLocked() {
        return selectionLocked;
    }

    public void lockSelection() {
        setSelectionLocked(true);
    }

    public void setGridColor(Color gridColor) {
        Color oldColor = this.gridColor;
        this.gridColor = gridColor;
        firePropertyChange("gridColor", oldColor, gridColor);
    }

    public void setLockedSelectionColor(Color lockedSelectionColor) {
        Color oldColor = this.lockedSelectionColor;
        this.lockedSelectionColor = lockedSelectionColor;
        firePropertyChange(
                "lockedSelectionColor",
                oldColor,
                lockedSelectionColor);
    }

    public void setSelectedPoint(PlotPoint selectedPoint)
            throws PropertyVetoException {
        PlotPoint oldPoint = this.selectedPoint;
        if (selectedPoint != null) {
            fireVetoableChange("selectedPoint", oldPoint, selectedPoint);
        } else if (selectionLocked) {
            setSelectionLocked(false);
        }
        this.selectedPoint = selectedPoint;
        firePropertyChange("selectedPoint", oldPoint, selectedPoint);
    }

    public void setSelectionLocked(boolean selectionLocked) {
        if (selectedPoint == null) {
            return;
        }
        boolean old = this.selectionLocked;
        this.selectionLocked = selectionLocked;
        firePropertyChange("selectionLocked", old, selectionLocked);
    }

    public void setUI(SGFilterVisUI newUI) {
        super.setUI(newUI);
    }

    public void setZoom(int zoom) {
        if (zoom < 0 || zoom > maxZoom) {
            throw new IllegalArgumentException("zoom");
        }
        int oldZoom = this.zoom;
        this.zoom = zoom;
        firePropertyChange("zoom", oldZoom, zoom);
    }

    public void unlockSelection() {
        setSelectionLocked(false);
    }

    @Override
    public void updateUI() {
        if (UIManager.get(getUIClassID()) != null) {
            setUI(UIManager.getUI(this));
        } else {
            setUI(new SGFilterVisUI());
        }
    }

    public void updateZoom() {
        int oldZoom = zoom;
        int oldMaxZoom = maxZoom;
        zoom = getUI().getZoom();
        maxZoom = getUI().getMaxZoom();
        firePropertyChange("maxZoom", oldMaxZoom, maxZoom);
        firePropertyChange("zoom", oldZoom, zoom);
    }

    public boolean zoomIn() {
        return getUI().zoomIn();
    }

    public boolean zoomOut() {
        return getUI().zoomOut();
    }

    private static final long serialVersionUID = -18887982027867289L;

    private static final String uiClassId = "SGFilterVisUI";

}
