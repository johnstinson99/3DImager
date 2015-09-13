package imager.domain.Transforms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by John on 01/09/2015.
 */
public class Rotation extends EuclidianTransform {
    //double angleRadians; //for reference only
    //char axis;   //for reference only
    Logger myLogger; //this is the one that's used

    public Rotation(double myAngleRadians, char myAxis){
        //angleRadians = myAngleRadians; //Note - arrays rotate the opposite direction.  Direction shouldn't matter as long as it's consistent throughout.
        //axis = myAxis;
        myLogger = LoggerFactory.getLogger(this.getClass());
        setUpRotateMatrixRightHand(myAngleRadians, myAxis);
        transformDescription = "Rotation of " + Math.toDegrees(myAngleRadians) + " degrees around the "+ myAxis+ " axis" ;
    }

    private void setUpRotateMatrixRightHand(double myAngleRadians, char myAxis){
        double cosAngle = Math.cos(myAngleRadians);
        double sinAngle = Math.sin(myAngleRadians);
        switch(myAxis) {
            case 'z':
                double[][] tmpArray1 = {
                        {cosAngle, -sinAngle, 0, 0},
                        {sinAngle, cosAngle, 0, 0},
                        {0, 0, 1, 0},
                        {0, 0, 0, 1}
                };
                transformMatrix = new FastPrintable4by4Matrix(tmpArray1);
                break;
            case 'x':
                double[][] tmpArray2 = {
                        {1, 0, 0, 0},
                        {0, cosAngle, -sinAngle, 0},
                        {0, sinAngle, cosAngle, 0},
                        {0, 0, 0, 1}
                };
                transformMatrix = new FastPrintable4by4Matrix(tmpArray2);
                break;
            case 'y':
                double[][] tmpArray3 = {
                        {cosAngle, 0, sinAngle, 0},
                        {0, 1, 0, 0},
                        {-sinAngle, 0, cosAngle, 0},
                        {0, 0, 0, 1}
                };
                transformMatrix = new FastPrintable4by4Matrix(tmpArray3);
                break;
            default:
                myLogger.error("Axis needs to be x,y or z in setUpRotateMatrix");
        }
    }


    private void setUpRotateMatrixLeftHand(double myAngleRadians, char myAxis){
        double cosAngle = Math.cos(myAngleRadians);
        double sinAngle = Math.sin(myAngleRadians);
        switch(myAxis) {
            case 'z':
                double[][] tmpArray1 = {
                        {cosAngle, sinAngle, 0, 0},
                        {-sinAngle, cosAngle, 0, 0},
                        {0, 0, 1, 0},
                        {0, 0, 0, 1}
                };
                transformMatrix = new FastPrintable4by4Matrix(tmpArray1);
                break;
            case 'x':
                double[][] tmpArray2 = {
                        {1, 0, 0, 0},
                        {0, cosAngle, sinAngle, 0},
                        {0, -sinAngle, cosAngle, 0},
                        {0, 0, 0, 1}
                };
                transformMatrix = new FastPrintable4by4Matrix(tmpArray2);
                break;
            case 'y':
                double[][] tmpArray3 = {
                        {cosAngle, 0, -sinAngle, 0},
                        {0, 1, 0, 0},
                        {sinAngle, 0, cosAngle, 0},
                        {0, 0, 0, 1}
                };
                transformMatrix = new FastPrintable4by4Matrix(tmpArray3);
                break;
            default:
                myLogger.error("Axis needs to be x,y or z in setUpRotateMatrix");
        }
    }

    /*public String toString(){
        return transformDescription;
    }*/

}
