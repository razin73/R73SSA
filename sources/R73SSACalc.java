import java.io.*;
import javax.swing.*;

public class  R73SSACalc {
    private R73SSA r73;
    public RCalcThread ct = null;
    public boolean bTerminated;
    public int iWay;
    private RTimer rtimer;
    private Timer timer;
    public double X[];
    public int N;
    public int K;
    public int M;
    public double X_exp[];
    public double S[];
    public double R[][];
    public double a[][];
    public double d[];
    public double u[][];
    public double lnd[];
    public double u1[];
    public double u2[];
    public double v1[];
    public double v2[];
    public double X_rc[];
    public double X_rd[];
    public double w_corr[][];
    
    R73SSACalc(R73SSA r73s) {
        r73 = r73s;
        rtimer = new RTimer(r73);
        timer = new Timer(500, rtimer);
    }
    public void runCalcThread(int iWay) {
        bTerminated = true;
        this.iWay = iWay;
        ct = new RCalcThread(r73);
        ct.setPriority(Thread.MAX_PRIORITY - 2);
        ct.iWay = iWay;
        System.out.println("runCalcThread, iWay = " + iWay);
        timer.start();
        ct.start();
        r73.setGUIElementsThreadRun();
        r73.setStatus("Processing");
    }
    public void stopCalcThread() {
        if (ct != null) ct.terminate();
    }
    public void endCalcThread() {
        System.out.println("endCalcThread");
        timer.stop();
        if (!bTerminated) {
            if (iWay == 2) r73.iState = 2;
            r73.setStatus("Ready");
        } else
            r73.setStatus("Terminated");
        ct = null;
        r73.setGUIElementsThreadStop();
        r73.rg.resetGraphics();
        if (iWay == 2 && r73.iState == 2) {
            r73.setChoice(3);
			r73.doGraph();
        }
    }
    public void open(String fileName) {
        BufferedReader in = null;
        int iRow;
        String str;
        try {
            in = new BufferedReader(new FileReader(fileName));
            iRow = 0;
            while ((str = in.readLine()) != null) iRow++;
            in.close();
            if (iRow < 2) return;
            N = iRow;
            X = new double[N];
            if (X == null) {
                N = 0;
                return;
            }
            X_rc = new double[N];
            X_rd = new double[N];
            if (X_rc == null || X_rd == null) return;
            in = new BufferedReader(new FileReader(fileName));
            iRow = 0;
            while ((str = in.readLine()) != null) {
                X[iRow] = Double.parseDouble(str);
                iRow++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void save(String fileName) {
        int i, j, k, l, i1, i2, j1, j2;
        double x_new;
        PrintWriter out = null;
		double [][] X_rec = new double[N][M];
		if (X_rec == null) return;
		for (i = 0; i < X_rec.length; i++)
			for (j = 0; j < X_rec[i].length; j++)
			    X_rec[i][j] = 0.0;
        for (int ii = 0; ii < M; ii++) {
            for (i = 0; i < K; i++) {
                v1[i] = 0.0;
                for (j = 0; j < M; j++)
                    v1[i] += X[i + j] * u[j][ii];
            }
            for (k = 0; k < N; k++) {
                x_new = 0.0;
                if (k < M) {i1 = k; j1 = 0;} else {i1 = M - 1; j1 = k - M + 1;}
                if (k < K) {i2 = 0; j2 = k;} else {i2 = k - K + 1; j2 = K - 1;}
                i = i1; j = j1;
                for (l = 0; l <= i1 - i2; l++) {
                    x_new += u[i][ii] * v1[j];
                    i--; j++;
                }
                x_new /= (double) (i1 - i2 + 1);
                X_rec[k][ii] += x_new;
            }
        }
        try {
            out = new PrintWriter(new FileWriter(fileName));
			for (i = 0; i < X_rec.length; i++) {
			    for (j = 0; j < X_rec[i].length; j++) {
                    out.print(X_rec[i][j] + "\t");
			        out.flush();
				}
				out.println();
				out.flush();
			}
        } catch (IOException e) {
			e.printStackTrace();
		} finally {
            if (out != null) {
			    out.flush();
                out.close();
            }
        }
	}
    public void calcTwoEigenvectors(int iFirst, int iSecond) {
        if (iFirst < 0 || iFirst >= M) return;
        for (int j = 0; j < M; j++) u1[j] = u[j][iFirst];
        if (iSecond < 0 || iSecond >= M) return;
        for (int j = 0; j < M; j++) u2[j] = u[j][iSecond];
    }
    public void calcTwoComponents(int iFirst, int iSecond) {
        if (iFirst < 0 || iFirst >= M) return;
        for (int i = 0; i < K; i++) {
            v1[i] = 0.0;
            for (int j = 0; j < M; j++)
                v1[i] += X[i + j] * u[j][iFirst];
            v1[i] /= Math.sqrt(d[iFirst]);
        }
        if (iSecond < 0 || iSecond >= M) return;
        for (int i = 0; i < K; i++) {
            v2[i] = 0.0;
            for (int j = 0; j < M; j++)
                v2[i] += X[i + j] * u[j][iSecond];
            v2[i] /= Math.sqrt(d[iSecond]);
        }
    }
    public void reconstruct(int iList[]) {
        int i, j, k, l, i1, i2, j1, j2;
        double x_new;
        for (k = 0; k < N; k++) {
            X_rc[k] = 0.0;
        }
        for (int ii : iList) {
            for (i = 0; i < K; i++) {
                v1[i] = 0.0;
                for (j = 0; j < M; j++)
                    v1[i] += X[i + j] * u[j][ii - 1];
            }
            for (k = 0; k < N; k++) {
                x_new = 0.0;
                if (k < M) {i1 = k; j1 = 0;} else {i1 = M - 1; j1 = k - M + 1;}
                if (k < K) {i2 = 0; j2 = k;} else {i2 = k - K + 1; j2 = K - 1;}
                i = i1; j = j1;
                for (l = 0; l <= i1 - i2; l++) {
                    x_new += u[i][ii - 1] * v1[j];
                    i--; j++;
                }
                x_new /= (double) (i1 - i2 + 1);
                X_rc[k] += x_new;
            }
        }
        for (i = 0; i < N; i++) X_rd[i] = X[i] - X_rc[i];
        r73.iState = 3;
        r73.setChoice(9);
		r73.doGraph();
    }
}