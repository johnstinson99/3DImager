package junit.Transforms;


import imager.domain.Transforms.FastPrintable4by4Matrix;
import junit.framework.TestCase;

/**
 * Created by John on 05/09/2015.
 */
public class TestMatrixPrintable extends TestCase {
    public void testPrintingMatrix(){
        double[][] tmpArray = {
                {1, 0, 0, 5},
                {0, 1, 0, 6},
                {0, 0, 1, 7},
                {0, 0, 0, 1}
        };
        FastPrintable4by4Matrix myMatrixPrintable = new FastPrintable4by4Matrix(tmpArray);
        System.out.println("myMatrixPrintable = "+myMatrixPrintable);
    }

}
