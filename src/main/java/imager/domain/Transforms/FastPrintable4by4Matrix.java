package imager.domain.Transforms;

import imager.domain.ThreeDObjects.FloatColoredPVector;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by John on 08/09/2015.
 */
public class FastPrintable4by4Matrix /*extends MatrixPrintable*/ {
    private double[][] A;

    //Attempt to see if we can speed up performance of the times method.
    /*public FastPrintable4by4Matrix(double[][] myArray){
        super(myArray);
    }*/
    public FastPrintable4by4Matrix(double[][] var1) {
        this.A = var1;
    }

    private static double[][] getIdentityArray(){
        double[][] identityArray = {
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        };
        return identityArray;
    }

    public static FastPrintable4by4Matrix getIdentityMatrix(){
        return new FastPrintable4by4Matrix(FastPrintable4by4Matrix.getIdentityArray());
    }
    public double[][] getArray(){
        return A;
    }
    public void applyTransformTo(FloatColoredPVector myColoredPVector){
        //Adjusts params of myColoredPVector in situ
        float x = myColoredPVector.getX();
        float y = myColoredPVector.getY();
        float z = myColoredPVector.getZ();
        //System.out.println(A[0][0] + "*" + x + " + " + A[0][1] + "*" + y + " + " + A[0][2] + "*" + z + " + "+A[0][3]);
        //System.out.println(A[1][0] + "*" + x + " + " + A[1][1] + "*" + y + " + " + A[1][2] + "*" + z + " + "+A[1][3]);
        myColoredPVector.setX((float)(A[0][0]*x + A[0][1]*y + A[0][2]*z + A[0][3]));
        myColoredPVector.setY((float)(A[1][0]*x + A[1][1]*y + A[1][2]*z + A[1][3]));
        myColoredPVector.setZ((float) (A[2][0] * x + A[2][1] * y + A[2][2] * z + A[2][3]));
    }
    public FastPrintable4by4Matrix times(FastPrintable4by4Matrix bMatrix){
        double[][] B = bMatrix.getArray();
        double[][] resultArray = FastPrintable4by4Matrix.getIdentityArray();  //Bottom row remains that of the identiry matrix.
        for(int i = 0;i<3; i++) { //Only need 0,1,2 here as final row is always the identity.
            resultArray[i][0] = A[i][0] * B[0][0] + A[i][1] * B[1][0] + A[i][2] * B[2][0];  //Don't need last entry as B[3][0] always zero
            resultArray[i][1] = A[i][0] * B[0][1] + A[i][1] * B[1][1] + A[i][2] * B[2][1];  //Don't need last entry as B[3][1] always zero
            resultArray[i][2] = A[i][0] * B[0][2] + A[i][1] * B[1][2] + A[i][2] * B[2][2];  //Don't need last entry as B[3][2] always zero
            resultArray[i][3] = A[i][0] * B[0][3] + A[i][1] * B[1][3] + A[i][2] * B[2][3] + A[i][3];  //Do need this as B[3][3] is 1
        }
        return new FastPrintable4by4Matrix(resultArray);
    }
    /*public String toString(){
        String myString = new String();
        for (int row = 0; row < 4; row++) {
            for(int column = 0; column <4; column++) {
                myString += A[row][column] + " \t";
            }
            myString += "\n";
        }
        return myString;
    }*/


    public String toString(){
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        print(writer, 3, 5);
        return stringWriter.toString();
    }
    public void print(int var1, int var2) {
        this.print(new PrintWriter(System.out, true), var1, var2);
    }

    public void print(PrintWriter var1, int var2, int var3) {
        DecimalFormat var4 = new DecimalFormat();
        var4.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        var4.setMinimumIntegerDigits(1);
        var4.setMaximumFractionDigits(var3);
        var4.setMinimumFractionDigits(var3);
        var4.setGroupingUsed(false);
        this.print(var1, var4, var2 + 2);
    }

    public void print(NumberFormat var1, int var2)
    {
        this.print(new PrintWriter(System.out, true), var1, var2);
    }

    public void print(PrintWriter var1, NumberFormat var2, int var3) {
        var1.println();

        for(int var4 = 0; var4 < 4; ++var4) {
            for(int var5 = 0; var5 < 4; ++var5) {
                String var6 = var2.format(this.A[var4][var5]);
                int var7 = Math.max(1, var3 - var6.length());

                for(int var8 = 0; var8 < var7; ++var8) {
                    var1.print(' ');
                }

                var1.print(var6);
            }

            var1.println();
        }

        var1.println();
    }


}
