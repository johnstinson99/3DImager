package junit.Transforms;
import imager.domain.ThreeDObjects.FloatColoredPVector;
import imager.domain.Transforms.Rotation;
import junit.framework.TestCase;

import java.util.Random;

import java.util.ArrayList;

/**
 * Created by John on 06/09/2015.
 */
public class TestPerformanceLargeNumberOfPoints extends TestCase{
    private ArrayList<FloatColoredPVector> getArrayOfFloatColoredPVectors(double noOfColoredPVectors){
        Random myRandom = new Random();
        ArrayList<FloatColoredPVector> myList = new ArrayList<FloatColoredPVector>();
        for(double i = 0; i<noOfColoredPVectors; i++){
            myList.add( new FloatColoredPVector(myRandom.nextFloat()*10, myRandom.nextFloat()*10, myRandom.nextFloat()*10));
        }
        return myList;
    }

    public void testPrintPerformance(){
        double myRunList[] = {1,1e1,1e2, 1e3, 1e4, 1e5, 1e6};
        System.out.println("Array method");
        for (double myNumberOfRuns: myRunList){
            float myTime = getMillisecondsForNRunsArray(myNumberOfRuns);
            System.out.println(myTime);
        }
        System.out.println("Original method");
        for (double myNumberOfRuns: myRunList){
            float myTime = getMillisecondsForNRunsOriginalMethod(myNumberOfRuns);
            System.out.println(myTime);
        }
    }
    public float getMillisecondsForNRunsArray(double noOfRuns){
        ArrayList<FloatColoredPVector> myArray = getArrayOfFloatColoredPVectors(noOfRuns);
        Rotation myRotation = new Rotation(Math.toRadians(90), 'x');
        long startNanoseconds = System.nanoTime();
        myRotation.applyTransformTo(myArray);
        long endNanoseconds = System.nanoTime();
        return (float)((endNanoseconds-startNanoseconds)/1e6);
    }
    public float getMillisecondsForNRunsOriginalMethod(double noOfRuns){
        ArrayList<FloatColoredPVector> myArray = getArrayOfFloatColoredPVectors(noOfRuns);
        float myAngle = (float) Math.toRadians(90);
        long startNanoseconds = System.nanoTime();
        for(FloatColoredPVector myColoredPVector: myArray){
            myColoredPVector.rotate3dRadians(myAngle, 'x');
        }
        long endNanoseconds = System.nanoTime();
        return (float)((endNanoseconds-startNanoseconds)/1e6);
    }
}
