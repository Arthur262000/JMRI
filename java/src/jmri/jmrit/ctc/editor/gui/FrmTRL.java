package jmri.jmrit.ctc.editor.gui;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import jmri.jmrit.ctc.ctcserialdata.CTCSerialData;
import jmri.jmrit.ctc.ctcserialdata.CodeButtonHandlerData;
import jmri.jmrit.ctc.ctcserialdata.ProjectsCommonSubs;
import jmri.jmrit.ctc.ctcserialdata.TrafficLockingEntry;
import jmri.jmrit.ctc.editor.code.AwtWindowProperties;
import jmri.jmrit.ctc.editor.code.CheckJMRIObject;
import jmri.jmrit.ctc.editor.code.CommonSubs;
import jmri.jmrit.ctc.editor.gui.FrmMainForm;

/**
 *
 * @author Gregory J. Bedlek Copyright (C) 2018, 2019
 */
public class FrmTRL extends javax.swing.JFrame {

    /**
     * Creates new form DlgTRL
     */
    private static final String FORM_PROPERTIES = "DlgTRL";     // NOI18N
    private final AwtWindowProperties _mAwtWindowProperties;
    private boolean _mClosedNormally = false;
    public boolean closedNormally() { return _mClosedNormally; }
    private final CodeButtonHandlerData _mCodeButtonHandlerData;
    private final CTCSerialData _mCTCSerialData;
    private final CheckJMRIObject _mCheckJMRIObject;
    private final FrmMainForm _mMainForm;

    private void initOrig() {
    }
    private boolean dataChanged() {
        return false;
    }

    public FrmTRL(  AwtWindowProperties awtWindowProperties, CodeButtonHandlerData codeButtonHandlerData,
                    CTCSerialData ctcSerialData, CheckJMRIObject checkJMRIObject) {
        super();
        initComponents();
        CommonSubs.addHelpMenu(this, "package.jmri.jmrit.ctc.CTC_frmTRL", true);  // NOI18N
        _mMainForm = jmri.InstanceManager.getDefault(FrmMainForm.class);
        _mAwtWindowProperties = awtWindowProperties;
        _mCodeButtonHandlerData = codeButtonHandlerData;
        _mCTCSerialData = ctcSerialData;
        _mCheckJMRIObject = checkJMRIObject;
        initOrig();
        _mAwtWindowProperties.setWindowState(this, FORM_PROPERTIES);
        this.getRootPane().setDefaultButton(_mOK);
        updateRuleCounts();
        this.setTitle(Bundle.getMessage("TitleDlgTRL") + " " + codeButtonHandlerData.myShortStringNoComma());   // NOI18N
    }

    public static boolean dialogCodeButtonHandlerDataValid(CheckJMRIObject checkJMRIObject, CodeButtonHandlerData codeButtonHandlerData) {
        if (!valid(checkJMRIObject, codeButtonHandlerData._mTRL_LeftTrafficLockingRulesSSVList)) return false;
        if (!valid(checkJMRIObject, codeButtonHandlerData._mTRL_RightTrafficLockingRulesSSVList)) return false;
        return true;
    }

    private void updateRuleCounts() {
        _mLeftNumberOfRules.setText(Bundle.getMessage("InfoDlgTRLRules") + " " + Integer.toString(ProjectsCommonSubs.getArrayListFromSSV(_mCodeButtonHandlerData._mTRL_LeftTrafficLockingRulesSSVList).size()));        // NOI18N
        _mRightNumberOfRules.setText(Bundle.getMessage("InfoDlgTRLRules") + " " + Integer.toString(ProjectsCommonSubs.getArrayListFromSSV(_mCodeButtonHandlerData._mTRL_RightTrafficLockingRulesSSVList).size()));      // NOI18N
        _mLeftNumberOfRulesPrompt.setForeground(valid(_mCheckJMRIObject, _mCodeButtonHandlerData._mTRL_LeftTrafficLockingRulesSSVList) ? Color.black : Color.red);
        _mRightNumberOfRulesPrompt.setForeground(valid(_mCheckJMRIObject, _mCodeButtonHandlerData._mTRL_RightTrafficLockingRulesSSVList) ? Color.black : Color.red);
    }

    private static boolean valid(CheckJMRIObject checkJMRIObject, String trafficLockingRulesSSVList) {
        for (String groupingListString : ProjectsCommonSubs.getArrayListFromSSV(trafficLockingRulesSSVList)) {
            if (!checkJMRIObject.validClass(new TrafficLockingEntry(groupingListString))) return false; // Error
        }
        return true;    // All valid
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        _mEditLeftTrafficLockingRules = new javax.swing.JButton();
        _mEditRightTrafficLockingRules = new javax.swing.JButton();
        _mLeftNumberOfRulesPrompt = new javax.swing.JLabel();
        _mRightNumberOfRulesPrompt = new javax.swing.JLabel();
        _mOK = new javax.swing.JButton();
        _mLeftNumberOfRules = new javax.swing.JLabel();
        _mRightNumberOfRules = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getMessage("TitleDlgTRL"));
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        _mEditLeftTrafficLockingRules.setText("Edit");
        _mEditLeftTrafficLockingRules.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _mEditLeftTrafficLockingRulesActionPerformed(evt);
            }
        });

        _mEditRightTrafficLockingRules.setText("Edit");
        _mEditRightTrafficLockingRules.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _mEditRightTrafficLockingRulesActionPerformed(evt);
            }
        });

        _mLeftNumberOfRulesPrompt.setText(Bundle.getMessage("LabelDlgTRLLeft"));

        _mRightNumberOfRulesPrompt.setText(Bundle.getMessage("LabelDlgTRLRight"));

        _mOK.setText(Bundle.getMessage("ButtonOK"));
        _mOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _mOKActionPerformed(evt);
            }
        });

        _mLeftNumberOfRules.setText(Bundle.getMessage("InfoDlgTRLRulesQuestion"));

        _mRightNumberOfRules.setText(Bundle.getMessage("InfoDlgTRLRulesQuestion"));

        jLabel10.setText(Bundle.getMessage("InfoDlgTRLNote2"));

        jLabel4.setText(Bundle.getMessage("InfoDlgTRLNote1"));

        jLabel11.setText(Bundle.getMessage("InfoDlgTRLNote3"));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(_mLeftNumberOfRulesPrompt)
                            .addComponent(_mRightNumberOfRulesPrompt))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(_mEditRightTrafficLockingRules)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(_mRightNumberOfRules, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(_mEditLeftTrafficLockingRules)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(_mLeftNumberOfRules, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel10))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel11))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(164, 164, 164)
                        .addComponent(_mOK)))
                .addContainerGap(164, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(_mLeftNumberOfRulesPrompt)
                    .addComponent(_mEditLeftTrafficLockingRules)
                    .addComponent(_mLeftNumberOfRules))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(_mEditRightTrafficLockingRules)
                    .addComponent(_mRightNumberOfRulesPrompt)
                    .addComponent(_mRightNumberOfRules))
                .addGap(13, 13, 13)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addComponent(_mOK)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        _mAwtWindowProperties.saveWindowState(this, FORM_PROPERTIES);
        if (CommonSubs.allowClose(this, dataChanged())) dispose();
    }//GEN-LAST:event_formWindowClosing

    private void _mEditLeftTrafficLockingRulesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__mEditLeftTrafficLockingRulesActionPerformed
        if (_mMainForm._mTRL_RulesFormOpen) return;
        _mMainForm._mTRL_RulesFormOpen = true;
        FrmTRL_Rules dialog = new FrmTRL_Rules( _mAwtWindowProperties, _mCodeButtonHandlerData,
                                                true, _mCTCSerialData, _mCheckJMRIObject);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (dialog.closedNormally()) {
                    _mClosedNormally = true;
                    updateRuleCounts();
                }
                _mMainForm._mTRL_RulesFormOpen = false;
            }
        });
        dialog.setVisible(true);  // MUST BE AFTER "addWindowListener"!  BUG IN AWT/SWING!
    }//GEN-LAST:event__mEditLeftTrafficLockingRulesActionPerformed

    private void _mEditRightTrafficLockingRulesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__mEditRightTrafficLockingRulesActionPerformed
        if (_mMainForm._mTRL_RulesFormOpen) return;
        _mMainForm._mTRL_RulesFormOpen = true;
        FrmTRL_Rules dialog = new FrmTRL_Rules( _mAwtWindowProperties, _mCodeButtonHandlerData,
                                                false, _mCTCSerialData, _mCheckJMRIObject);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (dialog.closedNormally()) {
                    _mClosedNormally = true;
                    updateRuleCounts();
                }
                _mMainForm._mTRL_RulesFormOpen = false;
            }
        });
        dialog.setVisible(true);  // MUST BE AFTER "addWindowListener"!  BUG IN AWT/SWING!
    }//GEN-LAST:event__mEditRightTrafficLockingRulesActionPerformed

    private void _mOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__mOKActionPerformed
        dispose();
    }//GEN-LAST:event__mOKActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton _mEditLeftTrafficLockingRules;
    private javax.swing.JButton _mEditRightTrafficLockingRules;
    private javax.swing.JLabel _mLeftNumberOfRules;
    private javax.swing.JLabel _mLeftNumberOfRulesPrompt;
    private javax.swing.JButton _mOK;
    private javax.swing.JLabel _mRightNumberOfRules;
    private javax.swing.JLabel _mRightNumberOfRulesPrompt;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel4;
    // End of variables declaration//GEN-END:variables
}
