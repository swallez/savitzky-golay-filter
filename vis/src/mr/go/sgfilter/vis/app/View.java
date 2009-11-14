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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.beancontext.BeanContextMembershipEvent;
import java.beans.beancontext.BeanContextMembershipListener;
import java.beans.beancontext.BeanContextSupport;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import mr.go.sgfilter.vis.Plot;
import mr.go.sgfilter.vis.PlotPoint;
import mr.go.sgfilter.vis.SGFilterVis;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;

/**
 * The application's main frame.
 */
public class View extends FrameView {

	// End of variables declaration//GEN-END:variables
	private JDialog					aboutBox;

	private BindingGroup			bindingGroup;

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private JScrollPane				bottomPanelContents;

	private BeanContextSupport		components	= new BeanContextSupport();

	private JMenu					componentsMenu;

	private JTree					componentsTree;

	private JCheckBoxMenuItem		continuousPadderMenuItem;

	private JFileChooser			csvFileChooser;

	private Plot					currentPlot;

	private DataBean				dataBean;

	private JMenu					dataFilterMenu;

	private JMenuItem				enterDataMenuItem;

	private final ItemListener		itemListener;

	private JCheckBoxMenuItem		linearizerMenuItem;

	private JMenuItem				loadDataMenuItem;

	private JCheckBoxMenuItem		meanValuePadderMenuItem;

	private JMenuBar				menuBar;

	private JButton					moveDownButton;

	private JButton					moveUpButton;

	private final PlottingService	plottingService;

	private JMenu					preprocessorMenu;

	private JCheckBoxMenuItem		ramerDouglasPeuckerMenuItem;

	private SGFilterVis				sGFilterVis;

	private JCheckBoxMenuItem		trendRemoverMenuItem;

	private JCheckBoxMenuItem		zeroEliminatorMenuItem;

	private JButton					zoomInButton;

	private JButton					zoomOutButton;

	public View(
			SingleFrameApplication app) {
		super(app);
		plottingService = new PlottingService();
		itemListener = new ComponentMenuItemChangeListener();
		initComponents();

		components.addBeanContextMembershipListener(plottingService);
		setUpFrame();
		setUpComponentsTree();
		setUpMenu();
		components.add(new SGFilterBean());
	}

	public Plot getCurrentPlot() {
		return currentPlot;
	}

	public void setCurrentPlot(Plot currentPlot) {
		Plot oldPlot = this.currentPlot;
		this.currentPlot = currentPlot;
		firePropertyChange("currentPlot", oldPlot, currentPlot);
	}

	@Action
	public void showAboutBox() {
		if (aboutBox == null) {
			JFrame mainFrame = mr.go.sgfilter.vis.app.Application
					.getApplication()
					.getMainFrame();
			aboutBox = new AboutBox(mainFrame);
			aboutBox.setLocationRelativeTo(mainFrame);
		}
		mr.go.sgfilter.vis.app.Application.getApplication().show(aboutBox);
	}

	private void editColorButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_editColorButtonActionPerformed
		Color newColor = JColorChooser.showDialog(getFrame(), getResourceMap()
				.getString("colorChooser.title"), currentPlot.getColor());
		if (newColor != null) {
			currentPlot.setColor(newColor);
		}
	}// GEN-LAST:event_editColorButtonActionPerformed

	private void enterDataMenuItemActionPerformed(ActionEvent evt) {// GEN-FIRST:event_enterDataMenuItemActionPerformed
		JFrame mainFrame = mr.go.sgfilter.vis.app.Application
				.getApplication()
				.getMainFrame();
		DataInputDialog dataInputDialog = new DataInputDialog(mainFrame);
		dataInputDialog.setLocationRelativeTo(mainFrame);
		try {
			double[] data = dataInputDialog.openDialog();
			if (data == null) {
				return;
			}
			if (dataBean != null) {
				components.remove(dataBean);
			}
			dataBean = new DataBean();
			dataBean.setData(data);
			components.add(dataBean);
		} catch (InputMismatchException e) {
			throw new RuntimeException("INPUT_PARSE_EXCEPTION");
		}
		sGFilterVis.getModel().computeExactMaxima();
	}// GEN-LAST:event_enterDataMenuItemActionPerformed

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {
		GridBagConstraints gridBagConstraints;
		bindingGroup = new BindingGroup();
		JPanel mainPanel = new JPanel();
		JPanel leftPanel = new JPanel();
		JScrollPane jScrollPane1 = new JScrollPane();
		componentsTree = new JTree();
		JPanel rightPanel = new JPanel();
		JLabel selectedPointLabel = new JLabel();
		JSpinner selectedPointYSpinner = new JSpinner();
		JLabel lockedSelectionIcon = new JLabel();
		JLabel plotLabel = new JLabel();
		JSeparator jSeparator1 = new JSeparator();
		JCheckBox showPlotCheckBox = new JCheckBox();
		JCheckBox drawLineCheckBox = new JCheckBox();
		JLabel colorLabel = new JLabel();
		JSeparator plotColorLine = new JSeparator();
		JButton editColorButton = new JButton();
		moveUpButton = new JButton();
		moveDownButton = new JButton();
		JLabel zoomLabel = new JLabel();
		JSeparator jSeparator2 = new JSeparator();
		zoomInButton = new JButton();
		zoomOutButton = new JButton();
		sGFilterVis = new SGFilterVis();
		JPanel bottomPanel = new JPanel();
		bottomPanelContents = new JScrollPane();
		menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu();
		loadDataMenuItem = new JMenuItem();
		enterDataMenuItem = new JMenuItem();
		JMenuItem exitMenuItem = new JMenuItem();
		componentsMenu = new JMenu();
		dataFilterMenu = new JMenu();
		ramerDouglasPeuckerMenuItem = new JCheckBoxMenuItem();
		preprocessorMenu = new JMenu();
		zeroEliminatorMenuItem = new JCheckBoxMenuItem();
		trendRemoverMenuItem = new JCheckBoxMenuItem();
		meanValuePadderMenuItem = new JCheckBoxMenuItem();
		continuousPadderMenuItem = new JCheckBoxMenuItem();
		linearizerMenuItem = new JCheckBoxMenuItem();
		JMenu helpMenu = new JMenu();
		JMenuItem aboutMenuItem = new JMenuItem();
		csvFileChooser = new JFileChooser();

		mainPanel.setMinimumSize(new Dimension(640, 480));
		mainPanel.setName("mainPanel"); // NOI18N
		mainPanel.setPreferredSize(new Dimension(800, 554));
		mainPanel.setLayout(new GridBagLayout());

		leftPanel.setMinimumSize(new Dimension(130, 454));
		leftPanel.setName("leftPanel"); // NOI18N
		leftPanel.setPreferredSize(new Dimension(130, 579));
		leftPanel.setLayout(new BorderLayout());

		jScrollPane1.setName("jScrollPane1"); // NOI18N

		componentsTree.setModel(null);
		componentsTree.setName("componentsTree"); // NOI18N
		jScrollPane1.setViewportView(componentsTree);

		leftPanel.add(jScrollPane1, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 8;
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		mainPanel.add(leftPanel, gridBagConstraints);

		rightPanel.setBorder(BorderFactory.createEtchedBorder());
		rightPanel.setMinimumSize(new Dimension(130, 454));
		rightPanel.setName("rightPanel"); // NOI18N
		rightPanel.setPreferredSize(new Dimension(130, 579));

		Binding binding = Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE,
				this,
				ELProperty.create("${currentPlot.color}"),
				rightPanel,
				BeanProperty.create("foreground"));
		bindingGroup.addBinding(binding);

		selectedPointLabel.setLabelFor(selectedPointYSpinner);
		ResourceMap resourceMap = Application
				.getInstance(mr.go.sgfilter.vis.app.Application.class)
				.getContext()
				.getResourceMap(View.class);
		selectedPointLabel.setText(resourceMap
				.getString("selectedPointLabel.text")); // NOI18N
		selectedPointLabel.setName("selectedPointLabel"); // NOI18N

		selectedPointYSpinner.setModel(new SpinnerNumberModel(Double
				.valueOf(0.0d), null, null, Double.valueOf(0.5d)));
		selectedPointYSpinner.setEditor(new NumberEditor(
				selectedPointYSpinner,
				"0.####"));
		selectedPointYSpinner.setName("selectedPointYSpinner"); // NOI18N

		binding = Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE,
				sGFilterVis,
				ELProperty.create("${selectedPoint.y}"),
				selectedPointYSpinner,
				BeanProperty.create("value"));
		bindingGroup.addBinding(binding);
		binding = Bindings.createAutoBinding(
				UpdateStrategy.READ,
				sGFilterVis,
				ELProperty.create("${selectedPoint!=null}"),
				selectedPointYSpinner,
				BeanProperty.create("enabled"));
		bindingGroup.addBinding(binding);

		lockedSelectionIcon.setIcon(resourceMap
				.getIcon("lockedSelectionIcon.icon")); // NOI18N
		lockedSelectionIcon.setToolTipText(resourceMap
				.getString("lockedSelectionIcon.toolTipText")); // NOI18N
		lockedSelectionIcon.setName("lockedSelectionIcon"); // NOI18N

		binding = Bindings.createAutoBinding(
				UpdateStrategy.READ,
				sGFilterVis,
				ELProperty.create("${selectionLocked}"),
				lockedSelectionIcon,
				BeanProperty.create("enabled"));
		bindingGroup.addBinding(binding);

		lockedSelectionIcon.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent evt) {
				lockedSelectionIconMouseClicked(evt);
			}
		});

		plotLabel.setFont(resourceMap.getFont("plotLabel.font")); // NOI18N
		plotLabel.setText(resourceMap.getString("plotLabel.text")); // NOI18N
		plotLabel.setName("plotLabel"); // NOI18N

		jSeparator1.setMinimumSize(new Dimension(50, 2));
		jSeparator1.setName("jSeparator1"); // NOI18N
		jSeparator1.setPreferredSize(new Dimension(50, 2));

		showPlotCheckBox
				.setText(resourceMap.getString("showPlotCheckBox.text")); // NOI18N
		showPlotCheckBox.setName("showPlotCheckBox"); // NOI18N

		binding = Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE,
				this,
				ELProperty.create("${currentPlot.visible}"),
				showPlotCheckBox,
				BeanProperty.create("selected"));
		bindingGroup.addBinding(binding);
		binding = Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE,
				this,
				ELProperty.create("${currentPlot != null}"),
				showPlotCheckBox,
				BeanProperty.create("enabled"));
		bindingGroup.addBinding(binding);

		drawLineCheckBox
				.setText(resourceMap.getString("drawLineCheckBox.text")); // NOI18N
		drawLineCheckBox.setName("drawLineCheckBox"); // NOI18N

		binding = Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE,
				this,
				ELProperty.create("${currentPlot.line}"),
				drawLineCheckBox,
				BeanProperty.create("selected"));
		bindingGroup.addBinding(binding);
		binding = Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE,
				this,
				ELProperty.create("${currentPlot != null}"),
				drawLineCheckBox,
				BeanProperty.create("enabled"));
		bindingGroup.addBinding(binding);

		colorLabel.setText(resourceMap.getString("colorLabel.text")); // NOI18N
		colorLabel.setName("colorLabel"); // NOI18N

		plotColorLine.setMinimumSize(new Dimension(50, 2));
		plotColorLine.setName("plotColorLine"); // NOI18N
		plotColorLine.setPreferredSize(new Dimension(50, 2));

		binding = Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE,
				this,
				ELProperty.create("${currentPlot.color}"),
				plotColorLine,
				BeanProperty.create("foreground"));
		bindingGroup.addBinding(binding);

		editColorButton.setIcon(resourceMap.getIcon("editColorButton.icon")); // NOI18N
		editColorButton.setText(resourceMap.getString("editColorButton.text")); // NOI18N
		editColorButton.setToolTipText(resourceMap
				.getString("editColorButton.toolTipText")); // NOI18N
		editColorButton.setName("editColorButton"); // NOI18N

		binding = Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE,
				this,
				ELProperty.create("${currentPlot != null}"),
				editColorButton,
				BeanProperty.create("enabled"));
		bindingGroup.addBinding(binding);

		editColorButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				editColorButtonActionPerformed(evt);
			}
		});

		moveUpButton.setIcon(resourceMap.getIcon("moveUpButton.icon")); // NOI18N
		moveUpButton.setToolTipText(resourceMap
				.getString("moveUpButton.toolTipText")); // NOI18N
		moveUpButton.setEnabled(false);
		moveUpButton.setName("moveUpButton"); // NOI18N
		moveUpButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				moveUpButtonActionPerformed(evt);
			}
		});

		moveDownButton.setIcon(resourceMap.getIcon("moveDownButton.icon")); // NOI18N
		moveDownButton.setToolTipText(resourceMap
				.getString("moveDownButton.toolTipText")); // NOI18N
		moveDownButton.setEnabled(false);
		moveDownButton.setName("moveDownButton"); // NOI18N
		moveDownButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				moveDownButtonActionPerformed(evt);
			}
		});

		zoomLabel.setFont(resourceMap.getFont("zoomLabel.font")); // NOI18N
		zoomLabel.setText(resourceMap.getString("zoomLabel.text")); // NOI18N
		zoomLabel.setName("zoomLabel"); // NOI18N

		jSeparator2.setName("jSeparator2"); // NOI18N

		zoomInButton.setIcon(resourceMap.getIcon("zoomInButton.icon")); // NOI18N
		zoomInButton.setName("zoomInButton"); // NOI18N
		zoomInButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				zoomInButtonActionPerformed(evt);
			}
		});

		zoomOutButton.setIcon(resourceMap.getIcon("zoomOutButton.icon")); // NOI18N
		zoomOutButton.setName("zoomOutButton"); // NOI18N
		zoomOutButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				zoomOutButtonActionPerformed(evt);
			}
		});

		GroupLayout rightPanelLayout = new GroupLayout(rightPanel);
		rightPanel.setLayout(rightPanelLayout);
		rightPanelLayout
				.setHorizontalGroup(rightPanelLayout
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								rightPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												rightPanelLayout
														.createParallelGroup(
																Alignment.LEADING)
														.addComponent(
																jSeparator2,
																GroupLayout.DEFAULT_SIZE,
																106,
																Short.MAX_VALUE)
														.addComponent(
																zoomLabel,
																GroupLayout.DEFAULT_SIZE,
																106,
																Short.MAX_VALUE)
														.addGroup(
																rightPanelLayout
																		.createSequentialGroup()
																		.addGap(
																				10,
																				10,
																				10)
																		.addComponent(
																				plotColorLine,
																				GroupLayout.DEFAULT_SIZE,
																				96,
																				Short.MAX_VALUE))
														.addComponent(
																selectedPointYSpinner,
																GroupLayout.DEFAULT_SIZE,
																106,
																Short.MAX_VALUE)
														.addComponent(
																selectedPointLabel,
																GroupLayout.DEFAULT_SIZE,
																106,
																Short.MAX_VALUE)
														.addComponent(
																lockedSelectionIcon)
														.addComponent(
																plotLabel,
																GroupLayout.DEFAULT_SIZE,
																106,
																Short.MAX_VALUE)
														.addComponent(
																jSeparator1,
																GroupLayout.DEFAULT_SIZE,
																106,
																Short.MAX_VALUE)
														.addComponent(
																showPlotCheckBox,
																GroupLayout.DEFAULT_SIZE,
																106,
																Short.MAX_VALUE)
														.addComponent(
																drawLineCheckBox,
																GroupLayout.DEFAULT_SIZE,
																106,
																Short.MAX_VALUE)
														.addComponent(
																colorLabel,
																GroupLayout.DEFAULT_SIZE,
																106,
																Short.MAX_VALUE)
														.addComponent(
																editColorButton)
														.addGroup(
																rightPanelLayout
																		.createSequentialGroup()
																		.addComponent(
																				moveUpButton)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				moveDownButton))
														.addGroup(
																rightPanelLayout
																		.createSequentialGroup()
																		.addComponent(
																				zoomInButton)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				zoomOutButton)))
										.addContainerGap()));
		rightPanelLayout.setVerticalGroup(rightPanelLayout.createParallelGroup(
				Alignment.LEADING).addGroup(
				rightPanelLayout
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(selectedPointLabel)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(
								selectedPointYSpinner,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(lockedSelectionIcon)
						.addGap(18, 18, 18)
						.addComponent(plotLabel)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(
								jSeparator1,
								GroupLayout.PREFERRED_SIZE,
								2,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(showPlotCheckBox)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(drawLineCheckBox)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(colorLabel)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(
								plotColorLine,
								GroupLayout.PREFERRED_SIZE,
								2,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(editColorButton)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(
								rightPanelLayout.createParallelGroup(
										Alignment.LEADING).addComponent(
										moveUpButton).addComponent(
										moveDownButton))
						.addGap(18, 18, 18)
						.addComponent(zoomLabel)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(
								jSeparator2,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(
								rightPanelLayout.createParallelGroup(
										Alignment.LEADING).addComponent(
										zoomInButton).addComponent(
										zoomOutButton))
						.addContainerGap(234, Short.MAX_VALUE)));

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 8;
		gridBagConstraints.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
		gridBagConstraints.weighty = 0.4;
		mainPanel.add(rightPanel, gridBagConstraints);

		sGFilterVis.setBackground(new Color(255, 255, 255));
		sGFilterVis.setBorder(BorderFactory
				.createLineBorder(new Color(0, 0, 0)));
		sGFilterVis.setMinimumSize(new Dimension(400, 300));
		sGFilterVis.setName("sGFilterVis"); // NOI18N
		sGFilterVis.setOpaque(true);
		sGFilterVis.setPreferredSize(new Dimension(540, 454));
		sGFilterVis.setLockedSelectionColor(Color.MAGENTA);
		sGFilterVis.addVetoableChangeListener(new VetoableChangeListener() {

			public void vetoableChange(PropertyChangeEvent evt)
					throws PropertyVetoException {
				sGFilterVisVetoableChange(evt);
			}
		});

		GroupLayout sGFilterVisLayout = new GroupLayout(sGFilterVis);
		sGFilterVis.setLayout(sGFilterVisLayout);
		sGFilterVisLayout.setHorizontalGroup(sGFilterVisLayout
				.createParallelGroup(Alignment.LEADING)
				.addGap(0, 536, Short.MAX_VALUE));
		sGFilterVisLayout.setVerticalGroup(sGFilterVisLayout
				.createParallelGroup(Alignment.LEADING)
				.addGap(0, 476, Short.MAX_VALUE));

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 7;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.NORTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 0.1;
		gridBagConstraints.insets = new Insets(0, 1, 1, 1);
		mainPanel.add(sGFilterVis, gridBagConstraints);

		bottomPanel.setMinimumSize(new Dimension(400, 100));
		bottomPanel.setName("bottomPanel"); // NOI18N
		bottomPanel.setPreferredSize(new Dimension(540, 125));
		bottomPanel.setLayout(new BorderLayout());

		bottomPanelContents.setBorder(null);
		bottomPanelContents.setName("bottomPanelContents"); // NOI18N
		bottomPanelContents.setPreferredSize(new Dimension(540, 125));
		bottomPanel.add(bottomPanelContents, BorderLayout.CENTER);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.SOUTH;
		mainPanel.add(bottomPanel, gridBagConstraints);

		menuBar.setName("menuBar"); // NOI18N

		fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
		fileMenu.setName("fileMenu"); // NOI18N

		loadDataMenuItem
				.setText(resourceMap.getString("loadDataMenuItem.text")); // NOI18N
		loadDataMenuItem.setName("loadDataMenuItem"); // NOI18N
		loadDataMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				loadDataMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(loadDataMenuItem);

		enterDataMenuItem.setText(resourceMap
				.getString("enterDataMenuItem.text")); // NOI18N
		enterDataMenuItem.setName("enterDataMenuItem"); // NOI18N
		enterDataMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				enterDataMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(enterDataMenuItem);

		ActionMap actionMap = Application
				.getInstance(mr.go.sgfilter.vis.app.Application.class)
				.getContext()
				.getActionMap(View.class, this);
		exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
		exitMenuItem.setName("exitMenuItem"); // NOI18N
		fileMenu.add(exitMenuItem);

		menuBar.add(fileMenu);

		componentsMenu.setText(resourceMap.getString("componentsMenu.text")); // NOI18N
		componentsMenu.setName("componentsMenu"); // NOI18N

		dataFilterMenu.setText(resourceMap.getString("dataFilterMenu.text")); // NOI18N
		dataFilterMenu.setName("dataFilterMenu"); // NOI18N

		ramerDouglasPeuckerMenuItem.setText(resourceMap
				.getString("ramerDouglasPeuckerMenuItem.text")); // NOI18N
		ramerDouglasPeuckerMenuItem.setActionCommand("0"); // NOI18N
		ramerDouglasPeuckerMenuItem.setName("ramerDouglasPeuckerMenuItem"); // NOI18N
		dataFilterMenu.add(ramerDouglasPeuckerMenuItem);

		componentsMenu.add(dataFilterMenu);

		preprocessorMenu
				.setText(resourceMap.getString("preprocessorMenu.text")); // NOI18N
		preprocessorMenu.setName("preprocessorMenu"); // NOI18N

		zeroEliminatorMenuItem.setText(resourceMap
				.getString("zeroEliminatorMenuItem.text")); // NOI18N
		zeroEliminatorMenuItem.setActionCommand("1"); // NOI18N
		zeroEliminatorMenuItem.setName("zeroEliminatorMenuItem"); // NOI18N
		preprocessorMenu.add(zeroEliminatorMenuItem);

		trendRemoverMenuItem.setText(resourceMap
				.getString("trendRemoverMenuItem.text")); // NOI18N
		trendRemoverMenuItem.setActionCommand("2"); // NOI18N
		trendRemoverMenuItem.setName("trendRemoverMenuItem"); // NOI18N
		preprocessorMenu.add(trendRemoverMenuItem);

		meanValuePadderMenuItem.setText(resourceMap
				.getString("meanValuePadderMenuItem.text")); // NOI18N
		meanValuePadderMenuItem.setActionCommand("3"); // NOI18N
		meanValuePadderMenuItem.setName("meanValuePadderMenuItem"); // NOI18N
		preprocessorMenu.add(meanValuePadderMenuItem);

		continuousPadderMenuItem.setText(resourceMap
				.getString("continuousPadderMenuItem.text")); // NOI18N
		continuousPadderMenuItem.setActionCommand("4"); // NOI18N
		continuousPadderMenuItem.setName("continuousPadderMenuItem"); // NOI18N
		preprocessorMenu.add(continuousPadderMenuItem);

		linearizerMenuItem.setText(resourceMap
				.getString("linearizerMenuItem.text")); // NOI18N
		linearizerMenuItem.setActionCommand("5"); // NOI18N
		linearizerMenuItem.setName("linearizerMenuItem"); // NOI18N
		preprocessorMenu.add(linearizerMenuItem);

		componentsMenu.add(preprocessorMenu);

		menuBar.add(componentsMenu);

		helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
		helpMenu.setName("helpMenu"); // NOI18N

		aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
		aboutMenuItem.setName("aboutMenuItem"); // NOI18N
		helpMenu.add(aboutMenuItem);

		menuBar.add(helpMenu);

		csvFileChooser.setCurrentDirectory(new File("."));
		csvFileChooser.setDialogTitle(resourceMap
				.getString("csvFileChooser.dialogTitle")); // NOI18N
		csvFileChooser.setFileFilter(new CsvFileFilter());
		csvFileChooser.setDoubleBuffered(true);
		csvFileChooser.setName("csvFileChooser"); // NOI18N

		setComponent(mainPanel);
		setMenuBar(menuBar);

		bindingGroup.bind();
	}// </editor-fold>//GEN-END:initComponents

	private void loadDataMenuItemActionPerformed(ActionEvent evt) {// GEN-FIRST:event_loadDataMenuItemActionPerformed
		int retVal = csvFileChooser.showOpenDialog(getFrame());
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File csvFile = csvFileChooser.getSelectedFile();
			if (dataBean != null) {
				components.remove(dataBean);
			}
			try {
				dataBean = new CsvDataBean(csvFile);
			} catch (IOException ex) {
				throw new RuntimeException("DATA_IO_EXCEPTION", ex);
			} catch (InputMismatchException ex) {
				throw new RuntimeException("DATA_PARSE_EXCEPTION", ex);
			}
			components.add(dataBean);
			sGFilterVis.getModel().computeExactMaxima();
		}
	}// GEN-LAST:event_loadDataMenuItemActionPerformed

	private void lockedSelectionIconMouseClicked(MouseEvent evt) {// GEN-FIRST:event_lockedSelectionIconMouseClicked
		sGFilterVis.unlockSelection();
	}// GEN-LAST:event_lockedSelectionIconMouseClicked

	private void moveDownButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_moveDownButtonActionPerformed
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) componentsTree
				.getLeadSelectionPath()
				.getLastPathComponent();
		Object userObject = selectedNode.getUserObject();
		DefaultMutableTreeNode newNode = plottingService
				.moveBeanDown(userObject);
		componentsTree.setSelectionPath(new TreePath(newNode.getPath()));
	}// GEN-LAST:event_moveDownButtonActionPerformed

	private void moveUpButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_moveUpButtonActionPerformed
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) componentsTree
				.getLeadSelectionPath()
				.getLastPathComponent();
		Object userObject = selectedNode.getUserObject();
		DefaultMutableTreeNode newNode = plottingService.moveBeanUp(userObject);
		componentsTree.setSelectionPath(new TreePath(newNode.getPath()));
	}// GEN-LAST:event_moveUpButtonActionPerformed

	private void setUpComponentsTree() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(
				getResourceMap().getString("AVAILABLE_COMPONENTS"));
		MutableTreeNode data = new DefaultMutableTreeNode(NodeType.DATA);
		MutableTreeNode dataFilters = new DefaultMutableTreeNode(
				NodeType.DATA_FILTER);
		MutableTreeNode preprocessors = new DefaultMutableTreeNode(
				NodeType.PREPROCESSOR);
		MutableTreeNode sgFilter = new DefaultMutableTreeNode(NodeType.FILTER);
		root.add(data);
		root.add(dataFilters);
		root.add(preprocessors);
		root.add(sgFilter);
		DefaultTreeModel treeModel = new DefaultTreeModel(root);
		treeModel.addTreeModelListener(new TreeModelListenerImpl());
		componentsTree.setModel(treeModel);

		TreeSelectionModel selectionModel = componentsTree.getSelectionModel();
		selectionModel
				.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		selectionModel
				.addTreeSelectionListener(new TreeSelectionListenerImpl());
	}

	private void setUpFrame() {
		JFrame frame = getFrame();
		frame.setMinimumSize(new Dimension(640, 480));
		frame.setPreferredSize(new Dimension(800, 600));
		frame.pack();
	}

	private void setUpMenu() {
		ramerDouglasPeuckerMenuItem.addItemListener(itemListener);
		zeroEliminatorMenuItem.addItemListener(itemListener);
		continuousPadderMenuItem.addItemListener(itemListener);
		trendRemoverMenuItem.addItemListener(itemListener);
		linearizerMenuItem.addItemListener(itemListener);
		meanValuePadderMenuItem.addItemListener(itemListener);
	}

	private void sGFilterVisVetoableChange(PropertyChangeEvent evt)
			throws java.beans.PropertyVetoException {// GEN-FIRST:event_sGFilterVisVetoableChange
		if (evt.getPropertyName().equals("selectedPoint")) {
			PlotPoint plotPoint = (PlotPoint) evt.getNewValue();
			if (plotPoint.plot.getTag() != NodeType.DATA) {
				throw new PropertyVetoException("Plot not editable", evt);
			}
		}
	}// GEN-LAST:event_sGFilterVisVetoableChange

	private void zoomInButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_zoomInButtonActionPerformed
		if (!sGFilterVis.zoomIn()) {
			zoomInButton.setEnabled(false);
		} else {
			zoomOutButton.setEnabled(true);
		}
	}// GEN-LAST:event_zoomInButtonActionPerformed

	private void zoomOutButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_zoomOutButtonActionPerformed
		if (!sGFilterVis.zoomOut()) {
			zoomOutButton.setEnabled(false);
		} else {
			zoomInButton.setEnabled(true);
		}
	}// GEN-LAST:event_zoomOutButtonActionPerformed

	private class ComponentMenuItemChangeListener implements ItemListener {

		public void itemStateChanged(ItemEvent e) {
			AbstractButton item = (AbstractButton) e.getSource();
			Object bean = beans[Integer.parseInt(item.getActionCommand())];
			if (e.getStateChange() == ItemEvent.SELECTED) {
				components.add(bean);
			} else {
				components.remove(bean);
			}
		}
	}

	private class PlottingService implements BeanContextMembershipListener {

		private PlotableChangeListener	changeListener	= new PlotableChangeListener();

		private List<Plotable>			plotables		= new ArrayList<Plotable>();

		public PlottingService() {
		}

		public void childrenAdded(BeanContextMembershipEvent bcme) {
			final Iterator<?> beanIterator = bcme.iterator();
			while (beanIterator.hasNext()) {
				final Object bean = beanIterator.next();
				if (bean instanceof Plotable) {
					final Plotable plotable = (Plotable) bean;
					add(plotable);
					install(bean);
				}
			}
		}

		public void childrenRemoved(BeanContextMembershipEvent bcme) {
			final Iterator<?> beanIterator = bcme.iterator();
			while (beanIterator.hasNext()) {
				final Object bean = beanIterator.next();
				if (bean instanceof Plotable) {
					final Plotable plotable = (Plotable) bean;
					remove(plotable);
					uninstall(bean);
				}
			}
		}

		public boolean isBeanAbleToMoveDown(Object bean) {
			if (bean instanceof Plotable) {
				return !((Plotable) bean).getNodeType().isThisNodeTheLastLeaf(
						componentsTree,
						bean);
			}
			return false;
		}

		public boolean isBeanAbleToMoveUp(Object bean) {
			if (bean instanceof Plotable) {
				return !((Plotable) bean).getNodeType().isThisNodeTheFirstLeaf(
						componentsTree,
						bean);
			}
			return false;
		}

		public DefaultMutableTreeNode moveBeanDown(Object bean) {
			int index = plotables.indexOf(bean);
			Collections.swap(plotables, index, index + 1);
			DefaultMutableTreeNode node = ((Plotable) bean)
					.getNodeType()
					.moveThisNodeDown(componentsTree, bean);
			replot(index);
			return node;
		}

		public DefaultMutableTreeNode moveBeanUp(Object bean) {
			int index = plotables.indexOf(bean) - 1;
			Collections.swap(plotables, index, index + 1);
			DefaultMutableTreeNode node = ((Plotable) bean)
					.getNodeType()
					.moveThisNodeUp(componentsTree, bean);
			replot(index);
			return node;
		}

		private void add(Plotable plotable) {
			int leafIndex = plotable.getNodeType().addThisNodeToTree(
					componentsTree,
					plotable);
			plotables.add(leafIndex, plotable);
			replot(leafIndex);
			sGFilterVis.getModel().addPlot(leafIndex, plotable.getPlot());
		}

		private void install(Object bean) throws IllegalArgumentException {
			try {
				Class<PropertyChangeListener> propertyChangeListenerType = PropertyChangeListener.class;
				BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
				final EventSetDescriptor[] esds = beanInfo
						.getEventSetDescriptors();
				for (EventSetDescriptor esd : esds) {
					if (propertyChangeListenerType.isAssignableFrom(esd
							.getListenerType())) {
						esd.getAddListenerMethod().invoke(bean, changeListener);
					}
				}
			} catch (IntrospectionException ex) {
			} catch (IllegalAccessException ex) {
			} catch (InvocationTargetException ex) {
			}
		}

		private void remove(Plotable plotable) {
			int leafIndex = plotable.getNodeType().removeThisNodeFromTree(
					componentsTree,
					plotable);
			plotables.remove(leafIndex);
			sGFilterVis.getModel().removePlot(plotable.getPlot());
			if (leafIndex != plotables.size()) {
				replot(leafIndex);
			}
		}

		private void replot(int indexOfChange) {
			if (indexOfChange == 0) {
				replotAll();
				return;
			}
			int plotCount = plotables.size();
			double[] data = plotables
					.get(indexOfChange - 1)
					.getPlot()
					.getData();
			Plotable changed;
			do {
				changed = plotables.get(indexOfChange++);
				changed.updatePlot(data);
				data = changed.getPlot().getData();
			} while (indexOfChange < plotCount);
		}

		private void replotAll() {
			Plotable plotable = plotables.get(0);
			double[] data = plotable.getPlot().getData();
			for (int i = 0; i < plotables.size(); i++) {
				plotable = plotables.get(i);
				plotable.updatePlot(data);
				data = plotable.getPlot().getData();
			}
		}

		private void uninstall(Object bean) throws IllegalArgumentException {
			try {
				Class<PropertyChangeListener> propertyChangeListenerType = PropertyChangeListener.class;
				BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
				final EventSetDescriptor[] esds = beanInfo
						.getEventSetDescriptors();
				for (EventSetDescriptor esd : esds) {
					if (propertyChangeListenerType.isAssignableFrom(esd
							.getListenerType())) {
						esd.getRemoveListenerMethod().invoke(
								bean,
								changeListener);
					}
				}
			} catch (IntrospectionException ex) {
			} catch (IllegalAccessException ex) {
			} catch (InvocationTargetException ex) {
			}
		}

		private class PlotableChangeListener implements PropertyChangeListener {

			public PlotableChangeListener() {
			}

			public void propertyChange(PropertyChangeEvent evt) {
				Object source = evt.getSource();
				replot(plotables.indexOf(source));
			}
		}
	}

	private class TreeModelListenerImpl implements TreeModelListener {

		public TreeModelListenerImpl() {
		}

		public void treeNodesChanged(TreeModelEvent e) {
		}

		public void treeNodesInserted(TreeModelEvent e) {
			Object firstInsertedNode = e.getChildren()[0];
			componentsTree.setSelectionPath(e.getTreePath().pathByAddingChild(
					firstInsertedNode));
		}

		public void treeNodesRemoved(TreeModelEvent e) {
			componentsTree.setSelectionPath(e.getTreePath());
		}

		public void treeStructureChanged(TreeModelEvent e) {
		}
	}

	private class TreeSelectionListenerImpl implements TreeSelectionListener {

		public TreeSelectionListenerImpl() {
		}

		public void valueChanged(TreeSelectionEvent e) {
			bottomPanelContents.setViewport(null);
			TreePath selectionPath = e.getNewLeadSelectionPath();
			if (selectionPath == null) {
				return;
			}
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPath
					.getLastPathComponent();
			Object nodeObject = node.getUserObject();
			if (nodeObject != null) {
				if (nodeObject instanceof Editable) {
					bottomPanelContents.setViewportView(((Editable) nodeObject)
							.getEditorPane());
					bottomPanelContents.revalidate();
				}
				if (nodeObject instanceof Plotable) {
					setCurrentPlot(((Plotable) nodeObject).getPlot());
				}
				moveUpButton.setEnabled(plottingService
						.isBeanAbleToMoveUp(nodeObject));
				moveDownButton.setEnabled(plottingService
						.isBeanAbleToMoveDown(nodeObject));
			}
		}
	}

	private static final Object[]	beans	= new Object[]
											{ new RDPBean(), new ZeroEliminatorBean(), new TrendRemoverBean(), new MeanValuePadderBean(), new ContinuousPadderBean(), new LinearizerBean() };
}
