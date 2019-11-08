package main.util;


public class Matrix4 {
    private double[][] mat;

    public Matrix4(){
        mat = new double[4][4];
    }

    public Matrix4(double[][] mat) {
        this.mat = mat;
    }

    public Matrix4(double a, double b, double c, double d, double e, double f,
                   double g, double h, double i, double j, double k, double l,
                   double m, double n, double o, double p){
        mat = new double[4][4];

        mat[0][0] = a;
        mat[0][1] = b;
        mat[0][2] = c;
        mat[0][3] = d;
        mat[1][0] = e;
        mat[1][1] = f;
        mat[1][2] = g;
        mat[1][3] = h;
        mat[2][0] = i;
        mat[2][1] = j;
        mat[2][2] = k;
        mat[2][3] = l;
        mat[3][0] = m;
        mat[3][1] = n;
        mat[3][2] = o;
        mat[3][3] = p;
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

        this.mat = quadricMatrix;
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

    public double[][] getMatrixArray(){
        return mat;
    }

    public Matrix4 multiply(Matrix4 matrix4){
        double[][] matrix = matrix4.getMatrixArray();

        double[][] returnMatrix = new double[4][4];
        if(matrix.length != 4){
            System.out.println("Kann nicht ausgeführt werden. Spaltenanzahl =/= Zeilenanzahl.");
        }
        else{
            for (int i = 0; i < 4; i++) { // aRow
                for (int j = 0; j < 4; j++) { // bColumn
                    for (int k = 0; k < 4; k++) { // aColumn
                        returnMatrix[i][j] += mat[i][k] * matrix[k][j];
                    }
                }
            }
        }

        return new Matrix4(returnMatrix);
    }
}
