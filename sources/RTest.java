import java.io.*;

public class  RTest {
    public static void checkEigenvectors(String file, double a[][], double d[], double u[][]) {
	    double value;
        PrintWriter outputStream = null;
		
		if (a == null) return;
        try {
            outputStream = new PrintWriter(new FileWriter(file));
			for (int k = 0; k < a.length; k++) {
			    for (int i = 0; i < a.length; i++) {
			        value = 0.0;
			        for (int j = 0; j < a[i].length; j++) {
				        value += a[i][j] * u[j][k];
				    }
					value -= d[k] * u[i][k];
                    outputStream.print(value + "\t");
			        outputStream.flush();
			    }
		        outputStream.println();
				outputStream.flush();
			}
        } catch (IOException e) {
			e.printStackTrace();
		} finally {
            if (outputStream != null) {
			    outputStream.flush();
                outputStream.close();
            }
        }
	}
    public static void printVector(String file, double vector[]) {
        PrintWriter outputStream = null;
		if (vector == null) return;
        try {
            outputStream = new PrintWriter(new FileWriter(file));
			for (int i = 0; i < vector.length; i++) {
                outputStream.println(vector[i]);
			    outputStream.flush();
			}
        } catch (IOException e) {
			e.printStackTrace();
		} finally {
            if (outputStream != null) {
			    outputStream.flush();
                outputStream.close();
            }
        }
	}
    public static void printMatrix(String file, double matrix[][]) {
        PrintWriter outputStream = null;
		if (matrix == null) return;
        try {
            outputStream = new PrintWriter(new FileWriter(file));
			for (int i = 0; i < matrix.length; i++) {
			    for (int j = 0; j < matrix[i].length; j++) {
                    outputStream.print(matrix[i][j] + "\t");
			        outputStream.flush();
				}
				outputStream.println();
				outputStream.flush();
			}
        } catch (IOException e) {
			e.printStackTrace();
		} finally {
            if (outputStream != null) {
			    outputStream.flush();
                outputStream.close();
            }
        }
	}
}