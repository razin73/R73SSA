import java.io.*;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;

public class RGraphics extends JComponent implements ComponentListener {
    private R73SSA r73;
    public BufferedImage img;
    
    RGraphics(R73SSA r73s) {
        r73 = r73s;
        img = null;
        setPreferredSize(new Dimension(640, 480));
        setBorder(BorderFactory.createLineBorder (Color.blue, 1));
        setBackground(Color.white);
        addComponentListener(this);
    }
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if (img != null)
            g2.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), g2.getBackground(), this);
    }
    public void componentHidden(ComponentEvent e) {}
    public void componentMoved(ComponentEvent e) {}
    public void componentResized(ComponentEvent e) {
		img = null;
        if (r73.iGpaph == 2) return;
        if (r73.iGpaph == 8) return;
        resetImg();
    }
    public void componentShown(ComponentEvent e) {}
    public void resetImg() {
        int w = getSize().width;
        int h = getSize().height;
        if (w < 1) w = 1; if (h < 1) h = 1;
		if (img == null) img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2D = img.createGraphics();
        g2D.setBackground(Color.lightGray);
        g2D.clearRect(0, 0, img.getWidth(), img.getHeight());
        if (r73.iGpaph == 1)
            r73.rgrph.drawGraph("Input", img, null, r73.rclc.X);
        if (r73.iGpaph == 3 && r73.rclc.lnd != null)
            r73.rgrph.drawGraph("Eigenvalues (ln)", img, null, r73.rclc.lnd);
        if (r73.iGpaph == 4 && r73.rclc.u1 != null)
            r73.rgrph.drawGraph("Eigenvector #" +
                Integer.toString(r73.getSpinnerValue(1)), img, null, r73.rclc.u1);
        if (r73.iGpaph == 5 && r73.rclc.v1 != null)
            r73.rgrph.drawGraph("Principal component #" +
                Integer.toString(r73.getSpinnerValue(1)), img, null, r73.rclc.v1);
        if (r73.iGpaph == 6 && r73.rclc.u1 != null && r73.rclc.u2 != null)
            r73.rgrph.drawScatterPlot(img, r73.rclc.u1, r73.rclc.u2);
        if (r73.iGpaph == 7 && r73.rclc.v1 != null && r73.rclc.v2 != null)
            r73.rgrph.drawScatterPlot(img, r73.rclc.v1, r73.rclc.v2);
        if (r73.iGpaph == 9 && r73.rclc.X_rc != null)
            r73.rgrph.drawGraph("Result", img, null, r73.rclc.X_rc);
        if (r73.iGpaph == 10 && r73.rclc.X_rd != null)
            r73.rgrph.drawGraph("Residual", img, null, r73.rclc.X_rd);
        repaint();
    }
    public void resetGraphics() {
        repaint();
    }
}