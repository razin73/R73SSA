import java.io.*;
import java.util.regex.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

/**
 * The R73SSA class is the main class which implements GUI.
*/
public class R73SSA extends JFrame implements WindowListener, ActionListener, ChangeListener {
    private String class_path;
    public int iState;
    public int iGpaph;
    public R73SSACalc rclc;
    public RGraph rgrph;
    private JPanel topPanel;
    private JButton btn_open;
    private JButton btn_decomp;
    private JButton btn_recon;
    private JButton btn_save;
    private JButton btn_exit;
    private JButton btn_graph;
    private JPanel centerPanel;
    public RGraphics rg;
    private JPanel centerLeftPanel;
    private JLabel label_N;
    private JLabel label_M;
    private SpinnerNumberModel smodel_M;
    private JSpinner sp_M;
    private JLabel label_K;
    private JLabel label_list;
    private JTextField tf_list;
	private ButtonGroup bg;
    private JRadioButton rb_input;
    private JRadioButton rb_matrix;
    private JRadioButton rb_eigenvalues;
    private JRadioButton rb_ev;
    private SpinnerNumberModel smodel_pc;
    private JSpinner sp_pc;
    private JRadioButton rb_pc;
    private JRadioButton rb_tev;
    private JButton btn_sup;
    private SpinnerNumberModel smodel_tpc1;
    private JSpinner sp_tpc1;
    private JButton btn_sdown;
    private SpinnerNumberModel smodel_tpc2;
    private JSpinner sp_tpc2;
    private JRadioButton rb_tpc;
    private JRadioButton rb_wcorr;
    private JRadioButton rb_res;
    private JRadioButton rb_rsd;
    private JPanel bottomPanel;
    private JLabel label_status;
    private JProgressBar progressBar;
    private JButton btn_stop;
    
    public R73SSA(String title) {
        super(title);
        Pattern p = Pattern.compile("/[^/]+\\.[jJ][aA][rR]$");
        Matcher m = p.matcher(getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
        class_path = m.replaceFirst("/");
        iState = 0;
        iGpaph = 0;
        rclc = new R73SSACalc(this);
        rgrph = new RGraph();
        setLayout(new BorderLayout());
        initComponents();
    }
    private void initComponents() {
        topPanel = new JPanel();
        btn_open = createButton("open");
        btn_decomp = createButton("decomp");
        btn_recon = createButton("recon");
        btn_save = createButton("save");
        btn_exit = createButton("exit");
        btn_graph = createButton("graph");
        topPanel.add(btn_open);
        topPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        topPanel.add(btn_decomp);
        topPanel.add(btn_recon);
        topPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        topPanel.add(btn_save);
        topPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        topPanel.add(btn_exit);
        getContentPane().add(topPanel, BorderLayout.NORTH);
        
        centerPanel = new JPanel();
        rg = new RGraphics(this);
        centerLeftPanel = new JPanel();
        centerLeftPanel.setLayout(new GridBagLayout());
        label_N = new JLabel("   N = 0");
        label_M = new JLabel("   M = ");
        smodel_M = new SpinnerNumberModel(1, 1, 10000, 1);
        sp_M = new JSpinner(smodel_M);
        sp_M.setEnabled(false);
        sp_M.addChangeListener(this);
        label_K = new JLabel("   K = ");
        label_list = new JLabel("   Composition List:");
        tf_list = new JTextField("");
        tf_list.setEnabled(false);
        rb_input = new JRadioButton("Input");
        rb_input.setActionCommand("rb_input");
        rb_input.setSelected(true);
        rb_matrix = new JRadioButton("Matrix");
        rb_matrix.setActionCommand("rb_matrix");
        rb_eigenvalues = new JRadioButton("Eigenvalues");
        rb_eigenvalues.setActionCommand("rb_eigenvalues");
        rb_ev = new JRadioButton("Eigenvector");
        rb_ev.setActionCommand("rb_ev");
        smodel_pc = new SpinnerNumberModel(1, 1, 10000, 1);
        sp_pc = new JSpinner(smodel_pc);
        sp_pc.addChangeListener(this);
        rb_pc = new JRadioButton("Principal component");
        rb_pc.setActionCommand("rb_pc");
        rb_tev = new JRadioButton("Two eigenvectors");
        rb_tev.setActionCommand("rb_tev");
        btn_sup = createButton("sup");
        smodel_tpc1 = new SpinnerNumberModel(1, 1, 10000, 1);
        sp_tpc1 = new JSpinner(smodel_tpc1);
        sp_tpc1.addChangeListener(this);
        btn_sdown = createButton("sdown");
        smodel_tpc2 = new SpinnerNumberModel(1, 1, 10000, 1);
        sp_tpc2 = new JSpinner(smodel_tpc2);
        sp_tpc2.addChangeListener(this);
        rb_tpc = new JRadioButton("Two principal components");
        rb_tpc.setActionCommand("rb_tpc");
        rb_wcorr = new JRadioButton("w-correlations");
        rb_wcorr.setActionCommand("rb_wcorr");
        rb_res = new JRadioButton("Result");
        rb_res.setActionCommand("rb_res");
        rb_rsd = new JRadioButton("Residual");
        rb_rsd.setActionCommand("rb_rsd");
        bg = new ButtonGroup();
        bg.add(rb_input);
        bg.add(rb_matrix);
        bg.add(rb_eigenvalues);
        bg.add(rb_ev);
        bg.add(rb_pc);
        bg.add(rb_tev);
        bg.add(rb_tpc);
        bg.add(rb_wcorr);
        bg.add(rb_res);
        bg.add(rb_rsd);
        rb_input.addActionListener(this);
        rb_matrix.addActionListener(this);
        rb_eigenvalues.addActionListener(this);
        rb_ev.addActionListener(this);
        rb_pc.addActionListener(this);
        rb_tev.addActionListener(this);
        rb_tpc.addActionListener(this);
        rb_wcorr.addActionListener(this);
        rb_res.addActionListener(this);
        rb_rsd.addActionListener(this);
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.PAGE_START;
        
        c.gridwidth = 2; c.gridx = 0; c.gridy = 0;
        centerLeftPanel.add(label_N, c);
        c.gridwidth = 1; c.gridx = 0; c.gridy = 1;
        centerLeftPanel.add(label_M, c);
        c.gridwidth = 1; c.gridx = 1; c.gridy = 1;
        centerLeftPanel.add(sp_M, c);
        c.gridwidth = 2; c.gridx = 0; c.gridy = 2;
        centerLeftPanel.add(label_K, c);
        c.gridwidth = 2; c.gridx = 0; c.gridy = 3;
        centerLeftPanel.add(label_list, c);
        c.gridwidth = 1; c.gridx = 1; c.gridy = 4;
        centerLeftPanel.add(tf_list, c);
        c.gridwidth = 2; c.gridx = 0; c.gridy = 5;
        centerLeftPanel.add(rb_input, c);
        c.gridwidth = 2; c.gridx = 0; c.gridy = 6;
        centerLeftPanel.add(rb_matrix, c);
        c.gridwidth = 2; c.gridx = 0; c.gridy = 7;
        centerLeftPanel.add(rb_eigenvalues, c);
        c.gridwidth = 2; c.gridx = 0; c.gridy = 8;
        centerLeftPanel.add(rb_ev, c);
        c.gridwidth = 1; c.gridx = 1; c.gridy = 9;
        centerLeftPanel.add(sp_pc, c);
        c.gridwidth = 2; c.gridx = 0; c.gridy = 10;
        centerLeftPanel.add(rb_pc, c);
        c.gridwidth = 2; c.gridx = 0; c.gridy = 11;
        centerLeftPanel.add(rb_tev, c);
        c.gridwidth = 1; c.gridx = 0; c.gridy = 12;
        centerLeftPanel.add(btn_sup, c);
        c.gridwidth = 1; c.gridx = 1; c.gridy = 12;
        centerLeftPanel.add(sp_tpc1, c);
        c.gridwidth = 1; c.gridx = 0; c.gridy = 13;
        centerLeftPanel.add(btn_sdown, c);
        c.gridwidth = 1; c.gridx = 1; c.gridy = 13;
        centerLeftPanel.add(sp_tpc2, c);
        c.gridwidth = 2; c.gridx = 0; c.gridy = 14;
        centerLeftPanel.add(rb_tpc, c);
        c.gridwidth = 2; c.gridx = 0; c.gridy = 15;
        centerLeftPanel.add(rb_wcorr, c);
        c.gridwidth = 2; c.gridx = 0; c.gridy = 16;
        centerLeftPanel.add(rb_res, c);
        c.gridwidth = 2; c.gridx = 0; c.gridy = 17;
        centerLeftPanel.add(rb_rsd, c);
        c.gridwidth = 2; c.gridx = 0; c.gridy = 18;
        centerLeftPanel.add(Box.createRigidArea(new Dimension(10, 10)), c);
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 2; c.gridx = 0; c.gridy = 19;
        centerLeftPanel.add(btn_graph, c);
        
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.PAGE_END;
        c.gridwidth = 2; c.gridx = 0; c.gridy = 16;
        centerLeftPanel.add(Box.createVerticalGlue(), c);
        
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(centerLeftPanel, BorderLayout.EAST);
        centerPanel.add(rg, BorderLayout.CENTER);
        getContentPane().add(centerPanel, BorderLayout.CENTER);
        
        bottomPanel = new JPanel();
        label_status = new JLabel("Ready");
        progressBar = new JProgressBar(0, 0);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setEnabled(false);
        btn_stop = createButton("stop");
        btn_stop.setEnabled(false);
        bottomPanel.add(label_status);
        bottomPanel.add(progressBar);
        bottomPanel.add(btn_stop);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        
        addWindowListener(this);
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(
            (screenSize.width - getSize().width) / 2,
            (screenSize.height - getSize().height) / 2);
        setGUIElements();
    }
    public JButton createButton(String btn) {
        JButton btnRes = new JButton(new ImageIcon(class_path + "images/" + btn + ".gif"));
        btnRes.setDisabledIcon(new ImageIcon(class_path + "images/" + btn + "_dis.gif"));
        Dimension buttonSize = new Dimension(btnRes.getIcon().getIconWidth(), btnRes.getIcon().getIconHeight());
        btnRes.setPreferredSize(buttonSize);
        btnRes.setMinimumSize(buttonSize);
        btnRes.setActionCommand("btn_" + btn);
        btnRes.addActionListener(this);
        return btnRes;
    }
    public void setGUIElements() {
        if (iState < 1) btn_decomp.setEnabled(false); else btn_decomp.setEnabled(true);
        if (iState < 2) btn_recon.setEnabled(false); else btn_recon.setEnabled(true);
        if (iState < 2) btn_save.setEnabled(false); else btn_save.setEnabled(true);
        if ((iState == 0) ||
            (iState == 1 && !rb_input.isSelected() && !rb_matrix.isSelected()) ||
            (iState == 2 && (rb_res.isSelected() || rb_rsd.isSelected())))
            btn_graph.setEnabled(false); else btn_graph.setEnabled(true);
        if (iState >= 1) sp_M.setEnabled(true); else sp_pc.setEnabled(false);
        if (iState >= 2) sp_pc.setEnabled(rb_ev.isSelected() || rb_pc.isSelected()); else sp_pc.setEnabled(false);
        if (iState >= 2) tf_list.setEnabled(true); else tf_list.setEnabled(false);
        if (iState >= 2) {
            btn_sup.setEnabled(rb_tev.isSelected() || rb_tpc.isSelected());
            sp_tpc1.setEnabled(rb_tev.isSelected() || rb_tpc.isSelected());
        } else {
            btn_sup.setEnabled(false);
            sp_tpc1.setEnabled(false);
        }
        if (iState >= 2) {
            btn_sdown.setEnabled(rb_tev.isSelected() || rb_tpc.isSelected());
            sp_tpc2.setEnabled(rb_tev.isSelected() || rb_tpc.isSelected());
        } else {
            btn_sdown.setEnabled(false);
            sp_tpc2.setEnabled(false);
        }
    }
    public void setGUIElementsThreadRun() {
        btn_open.setEnabled(false);
        btn_decomp.setEnabled(false);
        btn_recon.setEnabled(false);
        btn_save.setEnabled(false);
        btn_graph.setEnabled(false);
        progressBar.setEnabled(true);
        btn_stop.setEnabled(true);
        sp_M.setEnabled(false);
        sp_pc.setEnabled(false);
        tf_list.setEnabled(false);
        btn_sup.setEnabled(false);
        sp_tpc1.setEnabled(false);
        btn_sdown.setEnabled(false);
        sp_tpc2.setEnabled(false);
    }
    public void setGUIElementsThreadStop() {
        btn_open.setEnabled(true);
        progressBar.setMaximum(0);
        progressBar.setValue(0);
        progressBar.setEnabled(false);
        btn_stop.setEnabled(false);
        setGUIElements();
    }
    public void setThreadProgress() {
        progressBar.setEnabled(true);
        if (rclc.ct != null) {
            progressBar.setMaximum(rclc.ct.total);
            progressBar.setValue(rclc.ct.current);
        } else {
            progressBar.setMaximum(0);
            progressBar.setValue(0);
        }
    }
    public void setStatus(String msg) {
        label_status.setText(msg);
    }
    public void setChoice(int i) {
        bg.setSelected(bg.getSelection(), false);
        if (i == 1) rb_input.setSelected(true);
        if (i == 2) rb_matrix.setSelected(true);
        if (i == 3) rb_eigenvalues.setSelected(true);
        if (i == 4) rb_ev.setSelected(true);
        if (i == 5) rb_pc.setSelected(true);
        if (i == 6) rb_tev.setSelected(true);
        if (i == 7) rb_tpc.setSelected(true);
        if (i == 8) rb_wcorr.setSelected(true);
        if (i == 9) rb_res.setSelected(true);
        if (i == 10) rb_rsd.setSelected(true);
        setGUIElements();
    }
    public int getSpinnerValue(int sp) {
        if (sp == 1) return smodel_pc.getNumber().intValue();
        if (sp == 2) return smodel_tpc1.getNumber().intValue();
        if (sp == 3) return smodel_tpc2.getNumber().intValue();
        return 0;
    }
    public void doGraph() {
	    iGpaph = 0;
        if (rb_input.isSelected()) {
            iGpaph = 1;
            rg.resetImg();
        }
        if (rb_matrix.isSelected()) {
            iGpaph = 2;
            rg.resetImg();
            rclc.runCalcThread(1);
        }
        if (rb_eigenvalues.isSelected()) {
            iGpaph = 3;
            rg.resetImg();
        }
        if (rb_ev.isSelected()) {
            iGpaph = 4;
            rclc.calcTwoEigenvectors(getSpinnerValue(1) - 1, -1);
            rg.resetImg();
        }
        if (rb_pc.isSelected()) {
            iGpaph = 5;
            rclc.calcTwoComponents(getSpinnerValue(1) - 1, -1);
            rg.resetImg();
        }
        if (rb_tev.isSelected()) {
            iGpaph = 6;
            rclc.calcTwoEigenvectors(getSpinnerValue(2) - 1, getSpinnerValue(3) - 1);
            rg.resetImg();
        }
        if (rb_tpc.isSelected()) {
            iGpaph = 7;
            rclc.calcTwoComponents(getSpinnerValue(2) - 1, getSpinnerValue(3) - 1);
            rg.resetImg();
        }
        if (rb_wcorr.isSelected()) {
            iGpaph = 8;
            rg.resetImg();
            rclc.runCalcThread(3);
        }
        if (rb_res.isSelected()) {
            iGpaph = 9;
            rg.resetImg();
        }
        if (rb_rsd.isSelected()) {
            iGpaph = 10;
            rg.resetImg();
        }
//		RTest.printMatrix("MatrixTest.txt", rclc.R);
//		RTest.printVector("EigenvaluesTest.txt", rclc.d);
//		RTest.printMatrix("EigenvectorsTest.txt", rclc.u);
//		RTest.checkEigenvectors("CheckEigenvectorsTest.txt", rclc.R, rclc.d, rclc.u);
    }
    public static void main(String args[]) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new R73SSA("R73SSA").setVisible(true);
            }
        });
    }
    public void actionPerformed(ActionEvent e) {
        String str_acommand;
        
        str_acommand = e.getActionCommand();
        if (str_acommand.compareToIgnoreCase("btn_open") == 0) {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(class_path));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt", "TXT");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                iGpaph = 0;
                iState = 0;
                setGUIElements();
                rclc.M = 0;
                label_N.setText("   N = 0");
                rclc.K = 0;
                label_K.setText("   K = 0");
                smodel_M.setMinimum(1);
                smodel_M.setValue(1);
                smodel_M.setMaximum(1);
                rclc.open(chooser.getSelectedFile().getPath());
                if (rclc.N < 2) return;
                label_N.setText("   N = " + Integer.toString(rclc.N));
                int M = rclc.N / 2;
                smodel_M.setMinimum(2);
                smodel_M.setValue(M);
                smodel_M.setMaximum(rclc.N);
                iState = 1;
                setChoice(1);
				doGraph();
            }
        }
        if (str_acommand.compareToIgnoreCase("btn_decomp") == 0) {
            iGpaph = 2;
            iState = 1;
            setChoice(2);
            rclc.M = smodel_M.getNumber().intValue();
            rclc.K = rclc.N - rclc.M + 1;
            smodel_pc.setMinimum(1);
            smodel_pc.setValue(1);
            smodel_pc.setMaximum(rclc.M);
            smodel_tpc1.setMinimum(1);
            smodel_tpc1.setValue(1);
            smodel_tpc1.setMaximum(rclc.M);
            smodel_tpc2.setMinimum(1);
            smodel_tpc2.setValue(1);
            smodel_tpc2.setMaximum(rclc.M);
            rg.resetImg();
            rclc.runCalcThread(2);
        }
        if (str_acommand.compareToIgnoreCase("btn_recon") == 0) {
            iGpaph = 0;
            iState = 2;
            setGUIElements();
            int iList[] = RAnalyseString.analyse(tf_list.getText());
            if (iList != null && iList[iList.length - 1] <= rclc.M) {
                rclc.reconstruct(iList);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Composition List is wrong.",
                    "String analysis error",
                    JOptionPane.ERROR_MESSAGE);
            }
            setGUIElements();
        }
        if (str_acommand.compareToIgnoreCase("btn_save") == 0) {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(class_path));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt", "TXT");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                rclc.save(chooser.getSelectedFile().getPath());
            }
        }
        if (str_acommand.compareToIgnoreCase("rb_input") == 0) {
            if (iState > 0) doGraph();
        }
        if (str_acommand.compareToIgnoreCase("rb_matrix") == 0) {
            ;
        }
        if (str_acommand.compareToIgnoreCase("rb_eigenvalues") == 0) {
            if (iState > 1) doGraph();
        }
        if (str_acommand.compareToIgnoreCase("rb_ev") == 0) {
            if (iState > 1) doGraph();
        }
        if (str_acommand.compareToIgnoreCase("rb_pc") == 0) {
            if (iState > 1) doGraph();
        }
        if (str_acommand.compareToIgnoreCase("rb_tev") == 0) {
            if (iState > 1) doGraph();
        }
        if (str_acommand.compareToIgnoreCase("rb_tpc") == 0) {
            if (iState > 1) doGraph();
        }
        if (str_acommand.compareToIgnoreCase("rb_wcorr") == 0) {
            ;
        }
        if (str_acommand.compareToIgnoreCase("rb_res") == 0) {
            if (iState > 2) doGraph();
        }
        if (str_acommand.compareToIgnoreCase("rb_rsd") == 0) {
            if (iState > 2) doGraph();
        }
        if (str_acommand.compareToIgnoreCase("btn_exit") == 0) {
            dispose();
            System.exit(0);
        }
        if (str_acommand.compareToIgnoreCase("btn_graph") == 0) {
            doGraph();
        }
        if (str_acommand.compareToIgnoreCase("btn_sup") == 0) {
            Object o;
            if ((o = smodel_tpc1.getNextValue()) != null)
                smodel_tpc1.setValue(o);
            if ((o = smodel_tpc2.getNextValue()) != null)
                smodel_tpc2.setValue(o);
        }
        if (str_acommand.compareToIgnoreCase("btn_sdown") == 0) {
            Object o;
            if ((o = smodel_tpc1.getPreviousValue()) != null)
                smodel_tpc1.setValue(o);
            if ((o = smodel_tpc2.getPreviousValue()) != null)
                smodel_tpc2.setValue(o);
        }
        if (str_acommand.compareToIgnoreCase("btn_stop") == 0) {
            rclc.stopCalcThread();
        }
        if (rclc.ct != null) return;
        setGUIElements();
    }
    public void windowClosing(WindowEvent e) {
        dispose();
        System.exit(0);
    }
    public void windowOpened(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void stateChanged(ChangeEvent e) {
        JSpinner mySpinner = (JSpinner)(e.getSource());
        if (mySpinner == null) return;
        SpinnerNumberModel myModel = (SpinnerNumberModel)(mySpinner.getModel());
        if (mySpinner == sp_M) {
            iState = 1;
            rclc.M = smodel_M.getNumber().intValue();
            rclc.K = rclc.N - rclc.M + 1;
            setGUIElements();
            label_K.setText("   K = " + Integer.toString(rclc.K));
        }
        if ((mySpinner == sp_pc || mySpinner == sp_tpc1 || mySpinner == sp_tpc2) &&
		    (rb_ev.isSelected() || rb_pc.isSelected() || rb_tev.isSelected() || rb_tpc.isSelected()))
            doGraph();
    }
}
