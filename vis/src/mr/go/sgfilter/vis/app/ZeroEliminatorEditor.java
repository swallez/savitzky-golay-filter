/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ZeroEliminatorEditor.java
 *
 * Created on 2009-10-26, 17:30:12
 */
package mr.go.sgfilter.vis.app;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.border.LineBorder;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;

/**
 *
 * @author Rzeźnik
 */
public class ZeroEliminatorEditor extends javax.swing.JPanel {

    private final ZeroEliminatorBean bean;

    /** Creates new form ZeroEliminatorEditor */
    public ZeroEliminatorEditor() {
        this(null);
    }

    public ZeroEliminatorEditor(ZeroEliminatorBean bean) {
        this.bean = bean;
        initComponents();
    }

    public ZeroEliminatorBean getBean() {
        return bean;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new BindingGroup();

        alignToLeftCheckBox = new JCheckBox();
        ResourceMap resourceMap =
                Application.getInstance(mr.go.sgfilter.vis.app.Application.class).
                getContext().getResourceMap(ZeroEliminatorEditor.class);
        setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), resourceMap.getString("ZeroEliminatorEditor.border.title"))); // NOI18N
        setName("ZeroEliminatorEditor"); // NOI18N
        setPreferredSize(new Dimension(470, 100));

        alignToLeftCheckBox.setText(resourceMap.getString("alignToLeftCheckBox.text")); // NOI18N
        alignToLeftCheckBox.setName("alignToLeftCheckBox"); // NOI18N

        Binding binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, this, ELProperty.create("${bean.aligningToLeft}"), alignToLeftCheckBox, BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(alignToLeftCheckBox)
                .addContainerGap(322, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(alignToLeftCheckBox)
                .addContainerGap(45, Short.MAX_VALUE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JCheckBox alignToLeftCheckBox;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
