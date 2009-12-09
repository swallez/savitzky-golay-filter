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

import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import static java.lang.Math.max;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;

public class SGFilterVisUI extends ComponentUI {

	public static ComponentUI createUI(JComponent c) {
		return new SGFilterVisUI();
	}

	private static Graphics2D createG2D(BufferedImage buffer) {
		Graphics2D g2d = buffer.createGraphics();
		g2d.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		return g2d;
	}

	private static Rectangle2D findSelectionBounds(
			PlotPoint selected,
			PlotCoordinates coords) {
		double centerX = coords.getX(selected.x);
		double centerY = coords.getY(selected.getY());
		Rectangle2D bounds = new Rectangle2D.Double(
				centerX - 2 * POINT_SIZE,
				centerY - 2 * POINT_SIZE,
				4 * POINT_SIZE,
				4 * POINT_SIZE);
		return bounds;
	}

	private static Ellipse2D selectionEllipse(
			PlotPoint point,
			PlotCoordinates coords) {
		Rectangle2D bounds = findSelectionBounds(point, coords);
		Ellipse2D selection = new Ellipse2D.Double(
				bounds.getX(),
				bounds.getY(),
				bounds.getWidth(),
				bounds.getHeight());
		return selection;
	}

	private boolean					borderInstalled;

	private final NumberFormat		gridLabelFormatter	= NumberFormat
																.getNumberInstance(Locale.ROOT);

	private InputHandler			inputHandler;

	private BufferedImage			offScreen;

	private Renderer				renderer;

	private SGFilterVis				sgFilterVis;

	private PropertyChangeListener	sgFilterVisPropertyChangeListener;

	public SGFilterVisUI() {
		gridLabelFormatter.setMaximumFractionDigits(2);
	}

	@Override
	public void installUI(JComponent c) {
		sgFilterVis = (SGFilterVis) c;
		if (sgFilterVis.getBorder() == null) {
			borderInstalled = true;
			sgFilterVis.setBorder(new LineBorder(Color.BLACK));
		}
		sgFilterVis.setDoubleBuffered(false);

		PlotModel model = sgFilterVis.getModel();
		renderer = new Renderer();
		inputHandler = new InputHandler();

		sgFilterVis.addMouseMotionListener(inputHandler);
		sgFilterVis.addMouseListener(inputHandler);
		sgFilterVis.addKeyListener(inputHandler);
		sgFilterVis.addComponentListener(renderer);
		sgFilterVisPropertyChangeListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String propertyName = evt.getPropertyName();
				if (propertyName.equals("selectionLocked")
					|| propertyName.equals("lockedSelectionColor")) {
					onSelectionLockedChange(sgFilterVis.isSelectionLocked());
				} else if (propertyName.equals("selectedPoint")) {
					onSelectedPointChange(
							sgFilterVis.getSelectedPoint(),
							(PlotPoint) evt.getOldValue());
				}

			}
		};
		sgFilterVis
				.addPropertyChangeListener(sgFilterVisPropertyChangeListener);
		model.addPlotChangeListener(renderer);
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);
		Graphics2D g2d = (Graphics2D) g;
		Insets insets = sgFilterVis.getInsets();
		offScreenDrawing: if (offScreen == null) {
			Graphics2D g2dOff;
			PlotCoordinates coords = renderer.getCoordinates();
			int plotWidth = coords.getPlotWidth();
			int plotHeight = coords.getPlotHeight();
			offScreen = createOffScreenBuffer(plotWidth, plotHeight);
			offScreen.setAccelerationPriority(0.8f);
			g2dOff = createG2D(offScreen);
			g2dOff.setColor(sgFilterVis.getBackground());
			g2dOff.fillRect(0, 0, plotWidth, plotHeight);

			if (!sgFilterVis.getModel().isReady()) {
				break offScreenDrawing;
			}

			double gridLine = coords.getGridMin();
			final double gridMax = coords.getGridMax();
			final double unit = coords.getGridUnit();
			g2dOff.setStroke(GRID_STROKE);
			while (gridLine <= gridMax) {
				final int gridY = (int) coords.getY(gridLine);
				g2dOff.setColor(sgFilterVis.getForeground());
				g2dOff.drawString(
						gridLabelFormatter.format(gridLine),
						4,
						gridY - 1);
				g2dOff.setColor(sgFilterVis.getGridColor());
				g2dOff.drawLine(0, gridY, plotWidth, gridY);
				gridLine += unit;
			}

			for (BufferedImage buf : renderer.getImages()) {
				g2dOff.drawImage(buf, 0, 0, null);
			}

			if (sgFilterVis.isSelectionLocked()
				&& sgFilterVis.getSelectedPoint() != null) {
				g2dOff.setStroke(SELECTION_STROKE);
				g2dOff.setColor(sgFilterVis.getLockedSelectionColor());
				Ellipse2D selection = selectionEllipse(sgFilterVis
						.getSelectedPoint(), coords);
				g2dOff.draw(selection);
			}
			g2dOff.dispose();
		}
		g2d.drawImage(offScreen, insets.left, insets.top, null);
	}

	@Override
	public void uninstallUI(JComponent c) {
		PlotModel model = sgFilterVis.getModel();
		sgFilterVis.removeMouseListener(inputHandler);
		sgFilterVis.removeMouseMotionListener(inputHandler);
		sgFilterVis.removeKeyListener(inputHandler);
		sgFilterVis.removeComponentListener(renderer);
		sgFilterVis
				.removePropertyChangeListener(sgFilterVisPropertyChangeListener);
		model.removePlotChangeListener(renderer);

		if (borderInstalled) {
			sgFilterVis.setBorder(null);
		}

		inputHandler = null;
		renderer = null;
		sgFilterVis = null;
	}

	public boolean zoomIn() {
		return renderer.zoomIn();
	}

	public boolean zoomOut() {
		return renderer.zoomOut();
	}

	private BufferedImage createOffScreenBuffer(int width, int height) {
		return sgFilterVis.getGraphicsConfiguration().createCompatibleImage(
				width,
				height,
				Transparency.TRANSLUCENT);
	}

	private void flushOffScreenBuffer() {
		if (offScreen != null) {
			offScreen.flush();
			offScreen = null;
		}
	}

	private void onSelectedPointChange(PlotPoint selected, PlotPoint old) {
		if (!sgFilterVis.isSelectionLocked()) {
			return;
		}
		Rectangle clip = null;
		if (selected != null) {
			clip = pointClip(selected);
			if (old != null) {
				clip.add(pointClip(old));
			}
		} else if (old != null) {
			clip = pointClip(old);
		}
		flushOffScreenBuffer();
		sgFilterVis.repaint(clip);
	}

	private void onSelectionLockedChange(boolean isLocked) {
		if (sgFilterVis.getSelectedPoint() != null) {
			Rectangle clip = pointClip(sgFilterVis.selectedPoint);
			flushOffScreenBuffer();
			sgFilterVis.repaint(clip);
		}
	}

	private Rectangle pointClip(PlotPoint point) {
		Rectangle clip = findSelectionBounds(point, renderer.getCoordinates())
				.getBounds();
		clip.grow(2, 2);
		return clip;
	}

	private class InputHandler extends MouseInputAdapter implements KeyListener {

		private int			markerLeft;

		private boolean		markerPainted;

		private int			markerWidth;

		private int			mouseX;

		private final Timer	timer;

		public InputHandler() {
			timer = new Timer(200, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					paintMarker();
				}
			});
			timer.setRepeats(false);
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.isActionKey()) {
				PlotPoint selected = sgFilterVis.selectedPoint;
				if (selected == null) {
					return;
				}
				PlotCoordinates coords = renderer.getCoordinates();
				int x;
				switch (e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					x = selected.x;
					if (x > selected.plot.getXsBeginning()) {
						selected = new PlotPoint(selected.plot, x - 1);
						mouseX = coords.xOnComponent((int) coords.getX(x - 1));
						select(selected);
					}
					break;
				case KeyEvent.VK_RIGHT:
					x = selected.x;
					if (x + 1 < selected.plot.getXsEnd()) {
						selected = new PlotPoint(selected.plot, x + 1);
						mouseX = coords.xOnComponent((int) coords.getX(x + 1));
						select(selected);
					}
					break;
				case KeyEvent.VK_UP:
					selected.setY(selected.getY() + coords.getGridUnit());
					break;
				case KeyEvent.VK_DOWN:
					selected.setY(selected.getY() - coords.getGridUnit());
					break;
				default:
					return;
				}
				e.consume();
				if (markerPainted) {
					clearMarker();
				}
				paintMarker();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			int clickCount = e.getClickCount();
			PlotPoint selected;
			if (clickCount == 1) {
				selected = sgFilterVis.getSelectedPoint();
				if (selected != null) {
					if (!sgFilterVis.isSelectionLocked()) {
						sgFilterVis.lockSelection();
					} else {
						PlotCoordinates coords = renderer.getCoordinates();
						int y = coords.yFromComponentCoordinates(e.getY());
						selected.setY(coords.getPlotY(y));
					}
				}
			} else if (clickCount == 2) {
				selected = sgFilterVis.getSelectedPoint();
				sgFilterVis.unlockSelection();
				if (selected != null) {
					unselect();
				}
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			PlotPoint selected = sgFilterVis.getSelectedPoint();
			if (selected == null) {
				return;
			}
			PlotCoordinates coords = renderer.getCoordinates();
			int y = coords.yFromComponentCoordinates(e.getY());
			selected.setY(coords.getPlotY(y));
		}

		@Override
		public void mouseExited(MouseEvent e) {
			timer.stop();
			if (!sgFilterVis.isSelectionLocked()) {
				unselect();
			}
			if (markerPainted) {
				clearMarker();
				markerPainted = false;
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			timer.stop();
			boolean unlocked = !sgFilterVis.isSelectionLocked();
			if (unlocked) {
				unselect();
			}
			mouseX = e.getX();
			if (markerPainted) {
				clearMarker();
				markerPainted = false;
			}
			if (unlocked) {
				PlotPoint p = renderer.getCoordinates().getPoint(
						mouseX,
						e.getY());
				if (p != null) {
					select(p);
				}
			}
			timer.start();
		}

		private void clearMarker() {
			flushOffScreenBuffer();
			sgFilterVis.paintImmediately(
					markerLeft,
					0,
					markerWidth,
					sgFilterVis.getHeight());
		}

		private void paintMarker() {
			if (offScreen == null) {
				return;
			}
			int x, y;
			PlotCoordinates coords = renderer.getCoordinates();
			x = coords.xFromComponentCoordinates(mouseX);
			final PlotPoint[] points = coords.getPoints(x);
			if (points == null) {
				return;
			}
			int plotHeight = coords.getPlotHeight();
			int plotWidth = coords.getPlotWidth();
			Graphics2D g2d = createG2D(offScreen);
			g2d.setColor(sgFilterVis.getForeground());
			g2d.setFont(sgFilterVis.getFont());
			int linePos = (int) coords.getX(points[0].x);
			int labelPos, labelWidth = 0;
			final String[] labels = new String[points.length];
			FontMetrics fontMetrics = g2d.getFontMetrics();
			int lineHeight = (int) ceil(fontMetrics
					.getLineMetrics("0", g2d)
					.getHeight());
			g2d.drawLine(linePos, 0, linePos, plotHeight);
			for (int i = 0; i < points.length; i++) {
				final PlotPoint p = points[i];
				if (p == null) {
					continue;
				}
				final String label = Double.toString(p.getY());
				labelWidth = max(labelWidth, fontMetrics.stringWidth(label));
				labels[i] = label;
			}
			int adornedLabelWidth = labelWidth + 5;
			markerWidth = adornedLabelWidth + 1;
			if (linePos + adornedLabelWidth >= plotWidth) {
				labelPos = linePos - adornedLabelWidth;
				markerLeft = coords.xOnComponent(labelPos);
			} else {
				labelPos = linePos + 5;
				markerLeft = coords.xOnComponent(linePos);
			}
			y = lineHeight;
			for (int i = 0; i < labels.length; i++) {
				final String label = labels[i];
				if (label == null) {
					continue;
				}
				y += lineHeight;
				g2d.setColor(points[i].plot.getColor());
				g2d.drawString(label, labelPos, y);
			}
			markerPainted = true;
			g2d.dispose();
			sgFilterVis.paintImmediately(
					markerLeft,
					0,
					markerWidth,
					sgFilterVis.getHeight());
			sgFilterVis.requestFocusInWindow();
		}

		private void select(PlotPoint p) {
			try {
				sgFilterVis.setSelectedPoint(p);
				sgFilterVis.setCursor(Cursor
						.getPredefinedCursor(Cursor.HAND_CURSOR));
			} catch (PropertyVetoException e) {
				// we don't care
			}

		}

		private void unselect() {
			try {
				sgFilterVis.setSelectedPoint(null);
				sgFilterVis.setCursor(Cursor.getDefaultCursor());
			} catch (PropertyVetoException e) {
				// we don't care
			}

		}
	}

	private static class PlotCoordinates {

		private AffineTransform		affineMatrix;

		private double				gridMax;

		private double				gridMin;

		private double				gridUnit;

		private final SGFilterVis	host;

		private Insets				insets;

		private double				maxY;

		private double				scaleX;

		private double				scaleY;

		private double				yPad	= 1;

		private double				zoom	= 0.75;

		public PlotCoordinates(
				SGFilterVis host) {
			this.host = host;
		}

		public AffineTransform getAffineMatrix() {
			return affineMatrix;
		}

		public double getGridMax() {
			return gridMax;
		}

		public double getGridMin() {
			return gridMin;
		}

		public double getGridUnit() {
			return gridUnit;
		}

		public int getPlotHeight() {
			insets = host.getInsets(insets);
			return host.getHeight() - (insets.top + insets.bottom);
		}

		public int getPlotWidth() {
			insets = host.getInsets(insets);
			return host.getWidth() - (insets.right + insets.left);
		}

		public double getPlotX(int x) {
			return (x - D_X) / scaleX;
		}

		public double getPlotY(int y) {
			return maxY * yPad - (y - D_Y) / (scaleY * zoom);
		}

		public PlotPoint getPoint(int x, int y) {
			final PlotPoint[] points = getPoints(x);
			if (points == null) {
				return null;
			}
			for (int i = 0; i < points.length; i++) {
				final PlotPoint p = points[i];
				if (p == null) {
					continue;
				}
				final double _y = getY(p.getY());
				if (_y - POINT_SIZE <= y && y <= _y + POINT_SIZE) {
					return p;
				}
			}
			return null;
		}

		public PlotPoint[] getPoints(int x) {
			if (scaleX == 0) {
				return null;
			}
			if (x < 0 || x >= getPlotWidth()) {
				return null;
			}
			int plotRange_from = (int) ceil(getPlotX(x - POINT_SIZE));
			int plotRange_to = (int) (getPlotX(x + POINT_SIZE));
			if (plotRange_from > plotRange_to || plotRange_from < 0) {
				return null;
			}
			PlotModel model = host.getModel();
			int count = model.getPlotsCount();
			PlotPoint[] result = new PlotPoint[count];
			for (int i = 0; i < count; i++) {
				final Plot plot = model.getPlot(i);
				if (plotRange_from < plot.getXsEnd()
					&& plotRange_from >= plot.getXsBeginning()) {
					result[i] = new PlotPoint(plot, plotRange_from);
				}
			}
			return result;
		}

		public Rectangle getRectange(Rectangle plotRectangle) {
			return new Rectangle(
					(int) getX(plotRectangle.x),
					(int) getY(plotRectangle.y),
					(int) ceil(plotRectangle.width * scaleX),
					(int) ceil(plotRectangle.height * scaleY * zoom));
		}

		public double getX(int plotX) {
			return D_X + plotX * scaleX;
		}

		public double getY(double plotY) {
			return D_Y + scaleY * zoom * (maxY * yPad - plotY);
		}

		public Rectangle toRectangleOnComponent(Rectangle rectangle) {
			rectangle.setLocation(
					xOnComponent(rectangle.x),
					yOnComponent(rectangle.y));
			return rectangle;
		}

		public void update(double maxX, double maxY, double minY) {
			if (maxX != 0 && maxY - minY != 0) {
				this.maxY = maxY;
				double plotWidth = getPlotWidth();
				double plotHeight = getPlotHeight();
				scaleX = (plotWidth - POINT_SIZE) / (maxX - 1);
				scaleY = (plotHeight - PLOT_INSET) / (maxY - minY);
				setAffineTx();
				setGrid();
			}
		}

		public int xFromComponentCoordinates(int x) {
			insets = host.getInsets(insets);
			return x - insets.left;
		}

		public int xOnComponent(int x) {
			insets = host.getInsets(insets);
			return x + insets.left;
		}

		public int yFromComponentCoordinates(int y) {
			insets = host.getInsets(insets);
			return y - insets.top;
		}

		public int yOnComponent(int y) {
			insets = host.getInsets(insets);
			return y + insets.top;
		}

		public boolean zoomIn() {
			if (zoom * scaleY >= 2) {
				zoom /= 2;
				yPad *= 1.5;
				setAffineTx();
				setGrid();
				return true;
			}
			return false;
		}

		public boolean zoomOut() {
			if (zoom <= 0.375) {
				zoom *= 2;
				yPad /= 1.5;
				setAffineTx();
				setGrid();
				return true;
			}
			return false;
		}

		private void setAffineTx() {
			affineMatrix = new AffineTransform(1, 0, 0, 1, D_X, D_Y);
			affineMatrix.scale(scaleX, zoom * scaleY);
			affineMatrix.scale(1, -1);
			affineMatrix.translate(0, -yPad * maxY);
		}

		private void setGrid() {
			double gridUnit;
			double unitSize = scaleY * zoom;
			int index = Arrays.binarySearch(UNITS, MESH_SIZE / unitSize);
			if (index < 0) {
				int insertionPoint = -(index + 1);
				if (insertionPoint != UNITS.length) {
					gridUnit = UNITS[insertionPoint];
				} else {
					gridUnit = UNITS[insertionPoint - 1];
				}
			} else {
				gridUnit = UNITS[index];
			}
			gridMax = gridUnit * floor(getPlotY(0) / gridUnit);
			gridMin = gridUnit * ceil(getPlotY(getPlotHeight()) / gridUnit);
			this.gridUnit = gridUnit;
		}

		private static final int		D_X			= POINT_SIZE / 2;

		private static final int		D_Y			= PLOT_INSET;

		private static final double		MESH_SIZE	= 40;

		private static final double[]	UNITS		= new double[]
													{ 0.1, 0.2, 0.5, 1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 100000, 500000 };
	}

	private class Renderer extends ComponentAdapter implements
			PlotChangeListener {

		private final PlotCoordinates		coords;

		private boolean						hasDirtyBuffers;

		private final List<BufferedImage>	images;

		private final PlotModel				model;

		private final List<Path2D>			plotPaths;

		public Renderer() {
			this.model = sgFilterVis.getModel();
			int count = model.getPlotsCount();
			this.plotPaths = new ArrayList<Path2D>(count);
			this.images = new ArrayList<BufferedImage>(count);
			this.coords = new PlotCoordinates(sgFilterVis);
			if (count > 0) {
				updateScale();
				hasDirtyBuffers = true;
				for (int i = 0; i < count; i++) {
					final Path2D path = createPath(model.getPlot(i));
					plotPaths.add(path);
					images.add(null);
				}
			}
		}

		@Override
		public void colorChanged(PlotChangedEvent ev) {
			Plot changed = ev.changed;
			if (!changed.isVisible()) {
				return;
			}
			Path2D path = plotPaths.get(ev.plotIndex);
			Graphics2D g2d = getDrawingSurface(ev.plotIndex, true);
			repaintPath(g2d, path, changed.getColor(), changed.isLine());
			g2d.dispose();
			flushOffScreenBuffer();
			sgFilterVis.repaint();
		}

		@Override
		public void componentResized(ComponentEvent e) {
			if (model.isReady()) {
				updateScale();
				disposeImages();
			}
			flushOffScreenBuffer();
		}

		@Override
		public void dataChanged(PlotChangedEvent ev) {
			Plot changed = ev.changed;
			Path2D path = createPath(changed);
			plotPaths.set(ev.plotIndex, path);
			if (!changed.isVisible()) {
				return;
			}
			Graphics2D g2d = getDrawingSurface(ev.plotIndex, true);
			repaintPath(g2d, path, changed.getColor(), changed.isLine());
			g2d.dispose();
			flushOffScreenBuffer();
			sgFilterVis.repaint();
		}

		@Override
		public void dataChanged(PlotChangedEvent ev, int index) {
			Plot changed = ev.changed;
			Path2D path = createPath(changed);
			plotPaths.set(ev.plotIndex, path);
			if (!changed.isVisible()) {
				return;
			}
			flushOffScreenBuffer();
			if (!isBufferDirty(ev.plotIndex)) {
				int maxY = (int) ceil(model.getMaximumValue());
				int minY = (int) model.getMinimumValue();
				Rectangle clip = new Rectangle(
						(index - 1) + changed.getXsBeginning(),
						maxY,
						3,
						maxY - minY);
				Graphics2D g2d = getDrawingSurface(ev.plotIndex, true, clip);
				repaintPath(g2d, path, changed.getColor(), changed.isLine());
				g2d.dispose();
			}
			sgFilterVis.repaint();
		}

		public PlotCoordinates getCoordinates() {
			return coords;
		}

		public List<BufferedImage> getImages() {
			if (hasDirtyBuffers) {
				updateImages();
				hasDirtyBuffers = false;
			}
			return images;
		}

		@Override
		public void isLineChanged(PlotChangedEvent ev) {
			Plot changed = ev.changed;
			if (!changed.isVisible()) {
				return;
			}
			Path2D path = plotPaths.get(ev.plotIndex);
			Graphics2D g2d = getDrawingSurface(ev.plotIndex, true);
			repaintPath(g2d, path, changed.getColor(), changed.isLine());
			g2d.dispose();
			flushOffScreenBuffer();
			sgFilterVis.repaint();
		}

		@Override
		public void maximaChanged() {
			updateScale();
			disposeImages();
		}

		@Override
		public void plotAdded(PlotChangedEvent ev) {
			Plot changed = ev.changed;
			Path2D path = createPath(changed);
			plotPaths.add(ev.plotIndex, path);
			images.add(ev.plotIndex, null);
			hasDirtyBuffers = true;
			flushOffScreenBuffer();
			sgFilterVis.repaint();
		}

		@Override
		public void plotRemoved(PlotChangedEvent ev) {
			try {
				PlotPoint selected = sgFilterVis.getSelectedPoint();
				if (selected != null
					&& ev.plotIndex == model.indexOf(selected.plot)) {
					sgFilterVis.setSelectedPoint(null);
				}
			} catch (PropertyVetoException e) {
				// we warned
			}
			plotPaths.remove(ev.plotIndex);
			BufferedImage buf = images.remove(ev.plotIndex);
			if (buf != null) {
				buf.flush();
			}
			if (ev.changed.isVisible()) {
				flushOffScreenBuffer();
				sgFilterVis.repaint();
			}
		}

		@Override
		public void plotsRemoved() {
			try {
				sgFilterVis.setSelectedPoint(null);
			} catch (PropertyVetoException e) {
				// we warned
			}
			disposeImages();
			images.clear();
			plotPaths.clear();
			flushOffScreenBuffer();
			sgFilterVis.repaint();
		}

		@Override
		public void visibilityChanged(PlotChangedEvent ev) {
			Plot changed = ev.changed;
			Graphics2D g2d;
			if (changed.isVisible()) {
				g2d = getDrawingSurface(ev.plotIndex);
				Path2D path = plotPaths.get(ev.plotIndex);
				repaintPath(g2d, path, changed.getColor(), changed.isLine());
			} else {
				g2d = getDrawingSurface(ev.plotIndex, true);
			}
			g2d.dispose();
			flushOffScreenBuffer();
			sgFilterVis.repaint();
		}

		public boolean zoomIn() {
			if (coords.zoomIn()) {
				disposeImages();
				flushOffScreenBuffer();
				sgFilterVis.repaint();
				return true;
			}
			return false;
		}

		public boolean zoomOut() {
			if (coords.zoomOut()) {
				disposeImages();
				flushOffScreenBuffer();
				sgFilterVis.repaint();
				return true;
			}
			return false;
		}

		private Path2D createPath(Plot plot) {
			double[] data = plot.getData();
			int count = data.length;
			if (count == 0) {
				return null;
			}
			Path2D result = new Path2D.Double(Path2D.WIND_NON_ZERO, count);
			int xsBeginning = plot.getXsBeginning();
			result.moveTo(xsBeginning, data[0]);
			for (int i = 1; i < count; i++) {
				result.lineTo(i + xsBeginning, data[i]);
			}
			return result;
		}

		private void disposeImages() {
			for (BufferedImage buf : images) {
				if (buf != null) {
					buf.flush();
				}
			}
			Collections.fill(images, null);
			hasDirtyBuffers = true;
		}

		private Graphics2D getDrawingSurface(int index) {
			return getDrawingSurface(index, false, null);
		}

		private Graphics2D getDrawingSurface(int index, boolean clear) {
			return getDrawingSurface(index, clear, null);
		}

		private Graphics2D getDrawingSurface(
				int index,
				boolean clear,
				Rectangle clip) {
			int plotWidth = coords.getPlotWidth();
			int plotHeight = coords.getPlotHeight();
			boolean freshBuffer = updateBuffer(index, plotWidth, plotHeight);
			BufferedImage buf = images.get(index);
			Graphics2D result = createG2D(buf);
			if (clip != null) {
				result.setClip(coords.getRectange(clip));
			}
			if (freshBuffer || clear) {
				result.setColor(TRANSPARENT_BACKGROUND);
				Composite composite = result.getComposite();
				result.setComposite(AlphaComposite.Clear);
				result.fillRect(0, 0, plotWidth, plotHeight);
				result.setComposite(composite);
			}
			return result;
		}

		private boolean isBufferDirty(int index) {
			return images.get(index) == null;
		}

		private void paintPath(Graphics2D g2d, Path2D path, boolean isLine) {
			if (path == null) {
				return;
			}
			if (isLine) {
				g2d.draw(path.createTransformedShape(coords.getAffineMatrix()));
			} else {
				double[] coords = new double[6];
				final PathIterator pathIterator = path
						.getPathIterator(this.coords.getAffineMatrix());
				while (!pathIterator.isDone()) {
					pathIterator.currentSegment(coords);
					g2d.fillRect(
							(int) (coords[0] - POINT_SIZE / 2),
							(int) (coords[1] - POINT_SIZE / 2),
							POINT_SIZE,
							POINT_SIZE);
					pathIterator.next();
				}
			}
		}

		private void repaintPath(
				Graphics2D g2d,
				Path2D path,
				Color color,
				boolean isLine) {
			g2d.setColor(color);
			g2d.setStroke(PLOT_STROKE);
			paintPath(g2d, path, isLine);
		}

		private boolean updateBuffer(int index, int width, int height) {
			BufferedImage buf = images.get(index);
			if (buf == null) {
				buf = createOffScreenBuffer(width, height);
				buf.setAccelerationPriority(0.5f);
				images.set(index, buf);
				return true;
			}
			return false;
		}

		private void updateImages() {
			int count = images.size();
			for (int i = 0; i < count; i++) {
				if (images.get(i) == null) {
					final Plot plot = model.getPlot(i);
					Graphics2D g2d;
					if (plot.isVisible()) {
						g2d = getDrawingSurface(i);
						repaintPath(
								g2d,
								plotPaths.get(i),
								plot.getColor(),
								plot.isLine());
					} else {
						g2d = getDrawingSurface(i, true);
					}
					g2d.dispose();
				}
			}
		}

		private void updateScale() {
			double maxX = model.getMaximumLength();
			double maxY = model.getMaximumValue();
			double minY = model.getMinimumValue();
			coords.update(maxX, maxY, minY);
		}
	}

	private static final Stroke	GRID_STROKE				= new BasicStroke(
																1.0f,
																BasicStroke.CAP_SQUARE,
																BasicStroke.JOIN_MITER,
																10f,
																new float[]
																{ 1.0f, 5.0f },
																0);

	private static final int	PLOT_INSET				= 30;

	private static final Stroke	PLOT_STROKE				= new BasicStroke(
																1.0f,
																BasicStroke.CAP_SQUARE,
																BasicStroke.JOIN_ROUND);

	private static final int	POINT_SIZE				= 3;

	private static final Stroke	SELECTION_STROKE		= new BasicStroke(2.0f);

	private static final Color	TRANSPARENT_BACKGROUND	= new Color(0, 0, 0, 0);

}
