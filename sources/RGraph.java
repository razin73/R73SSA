import java.awt.image.*;
import java.lang.*;
import java.text.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.*;

public class RGraph {
    private String title;
    private double X[];
    private double Y[];
    private int w, h;
    private double minX, maxX, minY, maxY;
    private int brdXLeft, brdYBottom, brdXRight, brdYTop;
    private int txtXW, txtXH, txtYW, txtYH, axisXW, axisXH, axisYW, axisYH;
    private double grdMinX, grdMaxX, grdMinY, grdMaxY;
    private int kX, kY;
    private double dX, dY;
    private double orderX, orderY;
    private double shiftX, shiftY;
    private double extMinX, extMaxX, extMinY, extMaxY;
    private double a11, a22, b1, b2;
    RGraph() {
    }
    public void drawGraph(String title1, BufferedImage img, double X1[], double Y1[]) {
        title = title1; X = X1; Y = Y1;
        if (Y == null) return;
        w = img.getWidth();
        h = img.getHeight();
        Graphics2D g2D = img.createGraphics();
        g2D.setBackground(new Color(128, 0, 128));
        g2D.clearRect(0, 0, w, h);
        FontMetrics metrics = g2D.getFontMetrics(g2D.getFont());
        txtXW = metrics.stringWidth(" 000."); txtXH = metrics.getHeight();
        txtYW = metrics.stringWidth(" 000."); txtYH = metrics.getHeight();
        brdXLeft = 4; brdYBottom = 6;
        brdXRight = 4; brdYTop = 4 + txtYH + 4 + txtYH / 2;
        axisXH = 3;
        axisYW = 3;
        axisXW = w - brdXLeft - txtYW - axisYW - brdXRight;
        axisYH = h - brdYBottom - txtXH - axisXH - brdYTop;
        setMinMax();
        calcAxes();
        drawAxes(img);
        for (int i = 0; i < X.length - 1; i++) {
            g2D.drawLine(xx(X[i]), yy(Y[i]), xx(X[i + 1]), yy(Y[i + 1]));
        }
    }
    private void setMinMax() {
        if (X == null) {
            X = new double[Y.length];
            for (int i = 0; i < Y.length; i++) X[i] = (double) i;
        }
        minX = X[0];
        maxX = X[X.length - 1];
        for (int i = 0; i < X.length; i++) {
            if (Double.isNaN(X[i])) continue;
            if (minX > X[i] || Double.isNaN(minX)) minX = X[i];
            if (maxX < X[i] || Double.isNaN(maxX)) maxX = X[i];
        }
        minY = Y[0];
        maxY = Y[Y.length - 1];
        for (int i = 0; i < Y.length; i++) {
            if (Double.isNaN(Y[i])) continue;
            if (minY > Y[i] || Double.isNaN(minY)) minY = Y[i];
            if (maxY < Y[i] || Double.isNaN(maxY)) maxY = Y[i];
        }
    }
    private void calcAxes() {
        kX = (axisXW - txtXW) / txtXW;
        kY = (axisYH - txtYH) / txtYH;
        if (kX < 1) kX = 1;
        if (kY < 1) kY = 1;
        dX = (maxX - minX) / ((double) kX);
        dY = (maxY - minY) / ((double) kY);
        orderX = Math.floor(Math.log10(dX));
        orderY = Math.floor(Math.log10(dY));
        dX = Math.ceil(dX / Math.pow(10.0, orderX));
        dY = Math.ceil(dY / Math.pow(10.0, orderY));
        kX = (int) Math.floor((maxX - minX) / dX / Math.pow(10.0, orderX));
        kY = (int) Math.floor((maxY - minY) / dY / Math.pow(10.0, orderY));
        if (kX < 1) kX = 1;
        if (kY < 1) kY = 1;
        grdMinX = Math.round(minX / Math.pow(10.0, orderX)) * Math.pow(10.0, orderX);
        grdMinY = Math.round(minY / Math.pow(10.0, orderY)) * Math.pow(10.0, orderY);
        grdMaxX = grdMinX + dX * Math.pow(10.0, orderX) * ((double) kX);
        grdMaxY = grdMinY + dY * Math.pow(10.0, orderY) * ((double) kY);
        extMinX = Math.min(minX, grdMinX);
        extMaxX = Math.max(maxX, grdMaxX);
        extMinY = Math.min(minY, grdMinY);
        extMaxY = Math.max(maxY, grdMaxY);
        double variationOrderX = Math.pow(10.0, Math.ceil(Math.log10(grdMaxX - grdMinX)));
        shiftX = Math.floor(grdMaxX / variationOrderX) * variationOrderX;
        double variationOrderY = Math.pow(10.0, Math.ceil(Math.log10(grdMaxY - grdMinY)));
        shiftY = Math.floor(grdMaxY / variationOrderY) * variationOrderY;
        a11 = (double) axisXW / (extMaxX - extMinX);
        b1 = (double) (w - brdXRight) - a11 * extMaxX;
        a22 = (double) axisYH / (extMinY - extMaxY);
        b2 = (double) brdYTop - a22 * extMaxY;
    }
    private int xx(double x) {
        return (int) (a11 * x + b1);
    }
    private int yy(double y) {
        return (int) (a22 * y + b2);
    }
    private void drawAxes(BufferedImage img) {
        Graphics2D g2D = img.createGraphics();
        g2D.drawLine(xx(extMinX), yy(extMinY), xx(extMaxX), yy(extMinY));
        g2D.drawLine(xx(extMinX), yy(extMinY), xx(extMinX), yy(extMaxY));
        for (int i = 0; i <= kX; i++) {
            int grdX = (int) Math.round(grdMinX / Math.pow(10.0, orderX)) + i * (int) dX;
            double dGrdX = (double) grdX * Math.pow(10.0, orderX);
            g2D.drawLine(xx(dGrdX), yy(extMinY), xx(dGrdX), yy(extMinY) + axisXH);
            grdX -= (int) (shiftX /  Math.pow(10.0, orderX));
            g2D.drawString(Integer.toString(grdX), xx(dGrdX) - txtXW / 2, yy(extMinY) + axisXH + txtXH);
        }
        for (int i = 0; i <= kY; i++) {
            int grdY = (int) Math.round(grdMinY / Math.pow(10.0, orderY)) + i * (int) dY;
            double dGrdY = (double) grdY * Math.pow(10.0, orderY);
            g2D.drawLine(xx(extMinX), yy(dGrdY), xx(extMinX) - axisYW, yy(dGrdY));
            grdY -= (int) (shiftY / Math.pow(10.0, orderY));
            g2D.drawString(Integer.toString(grdY), xx(extMinX) - axisYW - txtYW, yy(dGrdY) + txtYH / 3);
        }
        String strOrderX = "x * E" + Integer.toString((int) orderX);
        String strOrderY = "y * E" + Integer.toString((int) orderY);
        DecimalFormat df = new DecimalFormat("#,##0.######E0");
        if (shiftX > 0) strOrderX += " + " + df.format(shiftX);
        if (shiftX < 0) strOrderX += " - " + df.format(-shiftX);
        if (shiftY > 0) strOrderY += " + " + df.format(shiftY);
        if (shiftY < 0) strOrderY += " - " + df.format(-shiftY);
        FontMetrics metrics = g2D.getFontMetrics(g2D.getFont());
        int txtWidth = metrics.stringWidth(strOrderX);
        g2D.drawString(strOrderX, w - brdXRight - txtWidth, 4 + txtYH);
        g2D.drawString(strOrderY, brdXLeft, 4 + txtYH);
        txtWidth = metrics.stringWidth(title);
        g2D.drawString(title, w / 2 - txtWidth / 2, 4 + txtYH);
    }
    public void drawScatterPlot(BufferedImage img, double X1[], double Y1[]) {
        X = X1; Y = Y1;
        w = img.getWidth();
        h = img.getHeight();
        Graphics2D g2D = img.createGraphics();
        g2D.setBackground(new Color(0, 128, 0));
        g2D.clearRect(0, 0, w, h);
        brdXLeft = 4; brdYBottom = 4;
        brdXRight = 4; brdYTop = 4;
        axisXW = w - brdXLeft - brdXRight;
        axisYH = h - brdYBottom - brdYTop;
        setMinMax();
        extMinX = minX;
        extMaxX = maxX;
        extMinY = minY;
        extMaxY = maxY;
        a11 = (double) axisXW / (extMaxX - extMinX);
        b1 = (double) (w - brdXRight) - a11 * extMaxX;
        a22 = (double) axisYH / (extMinY - extMaxY);
        b2 = (double) brdYTop - a22 * extMaxY;
        for (int i = 0; i < X.length - 1; i++) {
            g2D.drawLine(xx(X[i]), yy(Y[i]), xx(X[i + 1]), yy(Y[i + 1]));
        }
    }
}