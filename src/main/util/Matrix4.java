package main.util;

import java.util.ArrayList;

public class Matrix4 {
    private double[][] mat;

    public Matrix4(double[][] mat) {
        this.mat = mat;
    }

    public Matrix4 getQuadricMatrix(double a, double b, double c, double d, double e, double f, double g, double h, double i, double j) {
        double[][] quadricMatrix = new double[4][4];
        quadricMatrix[0][0] = a;
        quadricMatrix[0][1] = d;
        quadricMatrix[0][2] = e;
        quadricMatrix[0][3] = g;
        quadricMatrix[1][0] = d;
        quadricMatrix[1][1] = b;
        quadricMatrix[1][2] = f;
        quadricMatrix[1][3] = h;
        quadricMatrix[2][0] = e;
        quadricMatrix[2][1] = f;
        quadricMatrix[2][2] = c;
        quadricMatrix[2][3] = i;
        quadricMatrix[3][0] = g;
        quadricMatrix[3][1] = h;
        quadricMatrix[3][2] = i;
        quadricMatrix[3][3] = j;

        return new Matrix4(quadricMatrix);
    }

    public Matrix4 getTransposed() {
        double[][] transposed = new double[4][4];
        for(int y = 0; y < 4; y++) {
            for(int x = 0; x < 4; x++) {
                transposed[y][x] = mat[x][y];
            }
        }
        return new Matrix4(transposed);
    }
}
