package ru.cyberbiology;

import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.JCheckBox;
import ru.cyberbiology.util.ProjectProperties;

/**
 *
 * @author Sergey Sokolov <xlamserg@gmail.com>
 */
public class SettingsDialog extends javax.swing.JDialog {

    private final ProjectProperties properties;
    private final Map<String, String> changedSettings = new HashMap();

    public SettingsDialog(Frame parent, boolean modal) {
        super(parent, modal);
        properties = ProjectProperties.getInstance();
        initComponents();
        loadSettings();
    }

    private void loadSettings() {
        cbMultiCell.setSelected(properties.getBoolean("EnableMultiCell"));
        cbRelativeByEnergy.setSelected(properties.getBoolean("EnableRelativeByEnergy"));
        cbRelativeByMinerals.setSelected(properties.getBoolean("EnableRelativeByMinerals"));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();
        panelSettingsGeneral = new javax.swing.JPanel();
        labelDevNote = new javax.swing.JLabel();
        panelSettingaBots = new javax.swing.JPanel();
        cbMultiCell = new javax.swing.JCheckBox();
        cbRelativeByEnergy = new javax.swing.JCheckBox();
        cbRelativeByMinerals = new javax.swing.JCheckBox();
        buttonPanel = new javax.swing.JPanel();
        buttonOk = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Параметры");

        labelDevNote.setText("Здесь будут общие настройки");

        javax.swing.GroupLayout panelSettingsGeneralLayout = new javax.swing.GroupLayout(panelSettingsGeneral);
        panelSettingsGeneral.setLayout(panelSettingsGeneralLayout);
        panelSettingsGeneralLayout.setHorizontalGroup(
            panelSettingsGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSettingsGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelDevNote)
                .addContainerGap(127, Short.MAX_VALUE))
        );
        panelSettingsGeneralLayout.setVerticalGroup(
            panelSettingsGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSettingsGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelDevNote)
                .addContainerGap(84, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Общие", panelSettingsGeneral);

        cbMultiCell.setText("многоклеточность");
        cbMultiCell.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cbMultiCellStateChanged(evt);
            }
        });

        cbRelativeByEnergy.setText("родство по энергии");
        cbRelativeByEnergy.setEnabled(false);

        cbRelativeByMinerals.setText("родство по минералам");
        cbRelativeByMinerals.setToolTipText("");
        cbRelativeByMinerals.setEnabled(false);

        javax.swing.GroupLayout panelSettingaBotsLayout = new javax.swing.GroupLayout(panelSettingaBots);
        panelSettingaBots.setLayout(panelSettingaBotsLayout);
        panelSettingaBotsLayout.setHorizontalGroup(
            panelSettingaBotsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSettingaBotsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSettingaBotsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbMultiCell)
                    .addComponent(cbRelativeByEnergy)
                    .addComponent(cbRelativeByMinerals))
                .addContainerGap(145, Short.MAX_VALUE))
        );
        panelSettingaBotsLayout.setVerticalGroup(
            panelSettingaBotsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSettingaBotsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbMultiCell)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbRelativeByEnergy)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbRelativeByMinerals)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Боты", panelSettingaBots);

        getContentPane().add(tabbedPane, java.awt.BorderLayout.CENTER);

        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        buttonOk.setText("ОК");
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });
        buttonPanel.add(buttonOk);

        buttonCancel.setText("Отмена");
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        buttonPanel.add(buttonCancel);

        getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);
        getContentPane().add(filler1, java.awt.BorderLayout.NORTH);
        getContentPane().add(filler2, java.awt.BorderLayout.WEST);
        getContentPane().add(filler3, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOkActionPerformed
        saveAndClose();
    }//GEN-LAST:event_buttonOkActionPerformed

    private void cbMultiCellStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cbMultiCellStateChanged
        JCheckBox cb = (JCheckBox) evt.getSource();
        changedSettings.put("EnableMultiCell", cb.isSelected() ? "true" : "false");
    }//GEN-LAST:event_cbMultiCellStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonOk;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JCheckBox cbMultiCell;
    private javax.swing.JCheckBox cbRelativeByEnergy;
    private javax.swing.JCheckBox cbRelativeByMinerals;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JLabel labelDevNote;
    private javax.swing.JPanel panelSettingaBots;
    private javax.swing.JPanel panelSettingsGeneral;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables

    void showSettingsDialog() {
        setLocationRelativeTo(getParent());
        setVisible(true);
    }

    private void saveAndClose() {
        Set<Map.Entry<String, String>> changedSet = changedSettings.entrySet();
        for (Map.Entry<String, String> entry: changedSet) {
            properties.setProperty(entry.getKey(), entry.getValue());
        }
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

}
