import java.lang.Thread;
import java.awt.image.*;
import java.awt.*;
import java.awt.geom.*;

public class RCalcThread extends Thread {
    private R73SSA r73;
    private R73SSACalc rclc;
    protected boolean bTerminated = false;
    public boolean bFinished = false;
    public int current;
    public int total;
    public int iWay = 0;
    
    public RCalcThread(R73SSA r73s) {
        super();
        r73 = r73s;
        rclc = r73.rclc;
    }
    public void run() {
        if (iWay == 1) drawMatrix(false);
        if (iWay == 2) {
            drawMatrix(true);
            decompose();
        }
        if (iWay == 3) drawMatrixW();
        bFinished = true;
        rclc.bTerminated = bTerminated;
        EventQueue.invokeLater(new Runnable() { 
            public void run() { 
                rclc.endCalcThread();
            } 
        });
    }
    public void terminate() {
        bTerminated = true;
    }
//-----------------------------------------
    private double a11, a22, b1, b2;
	private double dRMin, dRMax;
	private double dRAvg, dRSD;
    
    private void drawMatrix(boolean bFlag) {
        int w, h;
        if (bTerminated) return;
        total = ((rclc.M + 1) * rclc.M) / 2;
        if (bFlag) total += 50 * ((rclc.M * (rclc.M - 1)) / 2);
        current = 0;
        w = r73.rg.img.getWidth();
        h = r73.rg.img.getHeight();
        Graphics2D g2D = r73.rg.img.createGraphics();
        g2D.setBackground(new Color(255, 249, 128));
        g2D.clearRect(0, 0, w, h);
        if (bFlag) {
            rclc.R = new double[rclc.M][rclc.M];
            rclc.a = new double[rclc.M][rclc.M];
            rclc.d = new double[rclc.M];
            rclc.u = new double[rclc.M][rclc.M];
            if (rclc.R == null || rclc.a == null || rclc.d == null || rclc.u == null) {
                bTerminated = true;
                return;
            }
        }
//        prepare();
        prepare1();
        if (bTerminated) return;
        double minV, maxV;
//        minV = -1.0; maxV = 1.0;
        minV = dRMin; maxV = dRMax;
        a11 = (double) Math.min(w, h) / (double) rclc.M;
        b1 = 0.0;
        a22 = a11; b2 = b1;
        double aV = -1.0 / (maxV - minV);
        double bV = maxV / (maxV - minV);
        int iWidth = (int) Math.ceil(a11);
        for (int i = 0; i < rclc.M; i++)
            for (int j = 0; j <= i; j++) {
                if (bTerminated) return;
                double val = r(i, j);
                float fval = (float) (aV * val + bV);
                if (fval < 0) fval = 0.0f;
                if (fval > 1) fval = 1.0f;
                g2D.setColor(new Color(fval, fval, fval));
                g2D.fill(new Rectangle2D.Double(xx((double) i), yy((double) j), iWidth, iWidth));
                g2D.fill(new Rectangle2D.Double(xx((double) j), yy((double) i), iWidth, iWidth));
                if (bFlag) {
                    rclc.R[i][j] = val; rclc.R[j][i] = val;
                    rclc.a[i][j] = val; rclc.a[j][i] = val;
                }
                current++;
            }
    }
    private int xx(double x) {
        return (int) (a11 * x + b1);
    }
    private int yy(double y) {
        return (int) (a22 * y + b2);
    }
    private void drawMatrixW() {
        int w, h;
        if (bTerminated) return;
        total = ((rclc.M + 1) * rclc.M) / 2;
        current = 0;
        w = r73.rg.img.getWidth();
        h = r73.rg.img.getHeight();
        Graphics2D g2D = r73.rg.img.createGraphics();
        g2D.setBackground(new Color(96, 196, 196));
        g2D.clearRect(0, 0, w, h);
        rclc.w_corr = new double[rclc.M][rclc.M];
        if (rclc.w_corr == null) {
            bTerminated = true;
            return;
        }
        if (bTerminated) return;
        double minV, maxV;
        minV = -1.0; maxV = 1.0;
        a11 = (double) Math.min(w, h) / (double) rclc.M;
        b1 = 0.0;
        a22 = a11; b2 = b1;
        double aV = -1.0 / (maxV - minV);
        double bV = maxV / (maxV - minV);
        int iWidth = (int) Math.ceil(a11);
        for (int i = 0; i < rclc.M; i++)
            for (int j = 0; j <= i; j++) {
                if (bTerminated) return;
                double val = calc_w_corr(i, j);
                float fval = (float) (aV * val + bV);
                if (fval < 0) fval = 0.0f;
                if (fval > 1) fval = 1.0f;
                g2D.setColor(new Color(fval, fval, fval));
                g2D.fill(new Rectangle2D.Double(xx((double) i), yy((double) j), iWidth, iWidth));
                g2D.fill(new Rectangle2D.Double(xx((double) j), yy((double) i), iWidth, iWidth));
                rclc.w_corr[i][j] = val; rclc.w_corr[j][i] = val;
                current++;
            }
    }
//-----------------------------------------
    private void prepare() {
        if (bTerminated) return;
        rclc.X_exp = new double[rclc.M];
        if (rclc.X_exp == null) {
            bTerminated = true;
            return;
        }
        rclc.S = new double[rclc.M];
        if (rclc.S == null) {
            bTerminated = true;
            return;
        }
        for (int i = 0; i < rclc.M; i++) {
            rclc.X_exp[i] = 0.0;
            for (int j = 0; j < rclc.K; j++) {
                if (bTerminated) return;
                rclc.X_exp[i] += rclc.X[i + j];
            }
            rclc.X_exp[i] /= (double) rclc.K;
        }
        for (int i = 0; i < rclc.M; i++) {
            rclc.S[i] = 0.0;
            for (int j = 0; j < rclc.K; j++) {
                if (bTerminated) return;
                rclc.S[i] += (rclc.X[i + j] - rclc.X_exp[i]) * (rclc.X[i + j] - rclc.X_exp[i]);
            }
            rclc.S[i] /= (double) rclc.K;
            rclc.S[i] = Math.sqrt(rclc.S[i]);
        }
    }
//-----------------------------------------
    private void prepare1() {
        dRAvg = 0.0; dRSD = 0.0;
        for (int k = 0; k < rclc.N; k++) {
            if (bTerminated) return;
		    dRAvg += rclc.X[k];
		    dRSD += rclc.X[k] * rclc.X[k];
        }
		dRAvg = dRAvg / (double) rclc.N;
		dRSD = Math.sqrt(dRSD / (double) rclc.N - dRAvg * dRAvg);
		dRMin = (dRAvg - dRSD) * (dRAvg - dRSD) * (double) rclc.K;
		dRMax = (dRAvg + dRSD) * (dRAvg + dRSD) * (double) rclc.K;
    }
//-----------------------------------------
    private double r(int i, int j) {
        double res = 0.0;
//        for (int l = 0; l < rclc.K; l++)
//            res += (rclc.X[i + l] - rclc.X_exp[i]) * (rclc.X[j + l] - rclc.X_exp[j]);
//        return 1.0 / (double) rclc.K / rclc.S[i] / rclc.S[j] * res;
        for (int l = 0; l < rclc.K; l++)
            res += rclc.X[i + l] * rclc.X[j + l];
        return res;
    }
//-----------------------------------------
    private double calc_w_corr(int ii, int jj) {
        int i, j, k, l, i1, i2, j1, j2;
        double f_ii, f_jj;
        double v_ii_jj, v_ii_ii, v_jj_jj;
        v_ii_jj = 0.0;
        v_ii_ii = 0.0;
        v_jj_jj = 0.0;
        for (i = 0; i < rclc.K; i++) {
            rclc.v1[i] = 0.0;
            rclc.v2[i] = 0.0;
            for (j = 0; j < rclc.M; j++) {
                rclc.v1[i] += rclc.X[i + j] * rclc.u[j][ii];
                rclc.v2[i] += rclc.X[i + j] * rclc.u[j][jj];
            }
        }
        for (k = 0; k < rclc.N; k++) {
            f_ii = 0.0;
            f_jj = 0.0;
            if (k < rclc.M) {i1 = k; j1 = 0;} else {i1 = rclc.M - 1; j1 = k - rclc.M + 1;}
            if (k < rclc.K) {i2 = 0; j2 = k;} else {i2 = k - rclc.K + 1; j2 = rclc.K - 1;}
            i = i1; j = j1;
            for (l = 0; l <= i1 - i2; l++) {
                f_ii += rclc.u[i][ii] * rclc.v1[j];
                f_jj += rclc.u[i][jj] * rclc.v2[j];
                i--; j++;
            }
            f_ii /= (double) (i1 - i2 + 1);
            f_jj /= (double) (i1 - i2 + 1);
            v_ii_jj += (f_ii * f_jj) * (double) (i1 - i2 + 1);
            v_ii_ii += (f_ii * f_ii) * (double) (i1 - i2 + 1);
            v_jj_jj += (f_jj * f_jj) * (double) (i1 - i2 + 1);
        }
        return v_ii_jj / Math.sqrt(v_ii_ii) / Math.sqrt(v_jj_jj);
    }
//-----------------------------------------
    private void decompose() {
        if (bTerminated) return;
        jacobi(rclc.a, rclc.M, rclc.d, rclc.u);
        eigsrt(rclc.d, rclc.u, rclc.M);
        rclc.lnd = new double[rclc.M];
        rclc.u1 = new double[rclc.M];
        rclc.u2 = new double[rclc.M];
        rclc.v1 = new double[rclc.K];
        rclc.v2 = new double[rclc.K];
        if (rclc.lnd == null ||
		    rclc.u1 == null || rclc.u2 == null ||
		    rclc.v1 == null || rclc.v2 == null) {
            bTerminated = true;
            return;
        }
        for (int i = 0; i < rclc.M; i++) rclc.lnd[i] = Math.log(rclc.d[i]);
    }
//-----------------------------------------
/** Minimal value. */
    public static final double MINVAL = 0.0000001;
/**
 * Computes all eigenvalues and eigenvectors of a real symmetric matrix a[1..n][1..n]. On
 * output, elements of a above the diagonal are destroyed. d[1..n] returns the eigenvalues of a.
 * v[1..n][1..n] is a matrix whose columns contain, on output, the normalized eigenvectors of
 * a. nrot returns the number of Jacobi rotations that were required.
 * <i>Numerical recipes in C : the art of scientific computing /
 * William H. Press . . . [et al.]. – 2nd ed
 * Copyright (c) Cambridge University Press 1988, 1992
 * 11.1 Jacobi Transformations of a Symmetric Matrix
 * pp. 467 - 468</i>
 * @param a a symmetric matrix.
 * @param n the matrix dimension.
 * @param d the eigenvalues of a.
 * @param v the normalized eigenvectors of a.
 */
    public int jacobi(double a[][], int n, double d[], double v[][]) {
        int nrot;
        int j, iq, ip, i;
        double tresh, theta, tau, t, sm, s, h, g, c, b[], z[];
        
        if (bTerminated) return -2;
        b = new double[n];
        z = new double[n];
        if (b == null || z == null) return -1;
        for (ip = 0; ip < n; ip++) {
            for (iq = 0; iq < n; iq++) v[ip][iq] = 0.0;
            v[ip][ip] = 1.0;
        }
        for (ip = 0; ip < n; ip++) {
            b[ip] = d[ip] = a[ip][ip];
            z[ip] = 0.0;
        }
        nrot = 0;
        for (i = 0; i < 50; i++) {
            sm = 0.0;
            for (ip = 0; ip < n - 1; ip++)
                for (iq = ip + 1; iq < n; iq++)
                    sm += Math.abs(a[ip][iq]);
            if (sm < MINVAL) return nrot;
            if (i < 4)
                tresh = 0.2 * sm / (n * n);
            else
                tresh = 0.0;
            for (ip = 0; ip < n - 1; ip++)
                for (iq = ip + 1; iq < n; iq++) {
                    if (bTerminated) return -2;
                    g = 100.0 * Math.abs(a[ip][iq]);
                    if (i > 4 && g < MINVAL)
                        a[ip][iq] = 0.0;
                    else if (Math.abs(a[ip][iq]) > tresh) {
                        h = d[iq] - d[ip];
                        if (g < MINVAL)
                            t = (a[ip][iq]) / h;
                        else {
                            theta = 0.5 * h / (a[ip][iq]);
                            t = 1.0 / (Math.abs(theta) + Math.sqrt(1.0 + theta * theta));
                            if (theta < 0.0) t = -t;
                        }
                        c = 1.0 / Math.sqrt(1 + t * t);
                        s = t * c;
                        tau = s / (1.0 + c);
                        h = t * a[ip][iq];
                        z[ip] -= h;
                        z[iq] += h;
                        d[ip] -= h;
                        d[iq] += h;
                        a[ip][iq] = 0.0;
                        for (j = 0; j <= ip - 1; j++) {
                            g = a[j][ip]; h = a[j][iq];
                            a[j][ip] = g - s * (h + g * tau);
                            a[j][iq] = h + s * (g - h * tau);
                        }
                        for (j = ip + 1; j <= iq - 1; j++) {
                            g = a[ip][j]; h = a[j][iq];
                            a[ip][j] = g - s * (h + g * tau);
                            a[j][iq] = h + s * (g - h * tau);
                        }
                        for (j = iq + 1; j < n; j++) {
                            g = a[ip][j]; h = a[iq][j];
                            a[ip][j] = g - s * (h + g * tau);
                            a[iq][j] = h + s * (g - h * tau);
                        }
                        for (j = 0; j < n; j++) {
                            g = v[j][ip]; h = v[j][iq];
                            v[j][ip] = g - s * (h + g * tau);
                            v[j][iq] = h + s * (g - h * tau);
                        }
                        nrot++;
                    }
                    current++;
                }
            for (ip = 0; ip < n; ip++) {
                b[ip] += z[ip];
                d[ip] = b[ip];
                z[ip] = 0.0;
            }
        }
        return nrot;
    }
/**
 * Given the eigenvalues d[1..n] and eigenvectors v[1..n][1..n] as output from jacobi
 * this routine sorts the eigenvalues into descending order, and rearranges
 * the columns of v correspondingly. The method is straight insertion.
 * <i>Numerical recipes in C : the art of scientific computing /
 * William H. Press . . . [et al.]. – 2nd ed
 * Copyright (c) Cambridge University Press 1988, 1992
 * 11.1 Jacobi Transformations of a Symmetric Matrix
 * pp. 468 - 469</i>
 * @param d eigenvalues.
 * @param v normalized eigenvectors.
 * @param n the matrix dimension.
*/
    public void eigsrt(double d[], double v[][], int n) {
        int k, j, i;
        double p;
        
        if (bTerminated) return;
        for (i = 0; i < n - 1; i++) {
            k = i; p = d[k];
            for (j = i + 1; j < n; j++)
                if (d[j] >= p) {k = j; p = d[k];}
            if (k != i) {
                d[k] = d[i];
                d[i] = p;
                for (j = 0; j < n; j++) {
                    p = v[j][i];
                    v[j][i] = v[j][k];
                    v[j][k] = p;
                }
            }
        }
    }
//-----------------------------------------
}