package imager.domain.PointClouds;

import imager.domain.ThreeDObjects.FloatColoredPVector;
import processing.core.PVector;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

/**
 * Created by John on 24/05/2015.
 */
public class PointCloudFinalImage extends PointCloud {
    public PointCloudFinalImage(Frame myParentFrame){
        super(myParentFrame);
    }

    //TODO - refactor this. It's a mix of clouds and frames. Implement it at frame level?
    public synchronized void alignPointsInFinalImageToCloud0() { //TODO split out arrayList of ColoredPVectors into its own class that knows how to rotate or shift all points.
        //PApplet.println("****&&&**** alignPointsInFinalImageToCloud0 finalCorrelationRealWorldShiftToCloud0 = " + finalCorrelationRealWorldShiftToCloud0 + " finalCorrelationRealWorldRotateToCloud0  = " + finalCorrelationRealWorldRotateToCloud0);
        //PApplet.println(" finalCorrelationRealWorldShiftToNextCloud = " + finalCorrelationRealWorldShiftToPreviousCloud);
        getMyLogger().info(" finalCorrelationRealWorldRotateToNextCloud = " + getParentFrame().getFinalCorrelationRealWorldRotateToPreviousCloud());
        //PApplet.println(" finalCorrelationRealWorldShiftToCloud0 = " + finalCorrelationRealWorldShiftToCloud0);
        //PApplet.println(" finalCorrelationRealWorldRotateToCloud0 = " + finalCorrelationRealWorldRotateToCloud0);
        PVector myWorkingCloudFinalRealWorldRotateToNextCloud, myWorkingCloudFinalRealWorldShiftToNextCloud;
        PointCloud workingCloud = this;
        while (workingCloud != null) {
            //Iterator<ColoredPVector> myIterator = oneAndOnlyArrayList.iterator();  //For each point in final image.
            for(FloatColoredPVector myFloatColoredPVector : getOneAndOnlyArrayList()) {
                myWorkingCloudFinalRealWorldRotateToNextCloud = workingCloud.getParentFrame().getFinalCorrelationRealWorldRotateToPreviousCloud();
                myWorkingCloudFinalRealWorldShiftToNextCloud = workingCloud.getParentFrame().getFinalCorrelationRealWorldShiftToPreviousCloud();
            //while (myIterator.hasNext()) {
                //CHANGED TO ROTATE BEFORE SHIFTING.   THIS SHOULD IMPROVE IT...
                //ColoredPVector myVector = myIterator.next();
                /*rotatePoint(myVector, myWorkingCloudFinalRealWorldRotateToNextCloud.intX, 'intX');
                rotatePoint(myVector, myWorkingCloudFinalRealWorldRotateToNextCloud.intY, 'intY');
                rotatePoint(myVector, myWorkingCloudFinalRealWorldRotateToNextCloud.z, 'z');*/
                //TODO - three separate rotates needed here? Or just single one?
                myFloatColoredPVector.rotate3dRadians(myWorkingCloudFinalRealWorldRotateToNextCloud.x, 'x');
                myFloatColoredPVector.rotate3dRadians(myWorkingCloudFinalRealWorldRotateToNextCloud.y, 'y');
                myFloatColoredPVector.rotate3dRadians(myWorkingCloudFinalRealWorldRotateToNextCloud.z, 'z');
                myFloatColoredPVector.add(myWorkingCloudFinalRealWorldShiftToNextCloud);   //Add the final correlation shift INCLDING SHIFTS FROM PREVIOUS CLOUD SHIFTS (these aren't actually added to any collections of points, they're treated as offsets throughout the correlation to speed it up.
                //finalRealWorldImageArrayList.add(myVector);  //could use this to draw later..
                //myModel.finalPointCloud.add(myVector);
            }
            workingCloud = workingCloud.getParentFrame().getPreviousFrame().getPointCloudCorrelationRealWorld();   // myPreviousPointCloud
        }
    }

    public int getRandomNumberPlusOrMinusAPercentageAround(int numberAroundWhichToAddRandomness) {
        float plusOrMinusFraction = 0.5f;
        int randomRange = (int) (numberAroundWhichToAddRandomness * (plusOrMinusFraction * 2));
        int randomInt = getParentThreeDImageCreator().randomGenerator.nextInt(randomRange) + 1;
        return ((int) (numberAroundWhichToAddRandomness * (1 - plusOrMinusFraction)) + randomInt);
        //PApplet.println ("base no = " + numberAroundWhichToAddRandomness + " rnd no = " + myRnd);
    }

    public void writeAsciiFormatToFileWriter(Writer myWriter, int pointSkip) {
        Iterator<FloatColoredPVector> myIterator;

        myIterator = getOneAndOnlyArrayList().iterator();
        getMyLogger().info("Writing image to file");

        //int pointCounter = 0;
        int pointCounter = 0;
        int mySkipNo = getRandomNumberPlusOrMinusAPercentageAround(pointSkip);
        FloatColoredPVector myColoredVector;
        myIterator.next(); //TODO - DEFECT RISK - used to be ColoredPVector myColoredVector = myIterator.next, but this is reduendant as already done in the loop. May miss the first one?
        while (myIterator.hasNext()) {
            myColoredVector = myIterator.next();
            if (pointCounter == mySkipNo) {
                try {
                        myWriter.write(myColoredVector.toString());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }  //It cant draw itself as it needs to know its parent.
                pointCounter = 0;
                mySkipNo = getRandomNumberPlusOrMinusAPercentageAround(pointSkip);
            } else pointCounter++;
        }
    }

}
