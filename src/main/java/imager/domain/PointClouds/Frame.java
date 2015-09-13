package imager.domain.PointClouds;

import imager.domain.Correlators.CorrelatorForTwoPointClouds;
import imager.domain.ThreeDImageCreator;
import imager.domain.ThreeDObjects.FloatColoredPVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processing.core.PVector;

/**
 * Created by John on 21/05/2015.
 */
public class Frame {
    //Constructor
    ThreeDImageCreator parentThreeDImageCreator;
    //SimpleOpenNI myParentSimpleOpenNI; //This context refers to an original image set for Kinect captured ones and a set of test images when testing.
    int frameNo; //for debugging
    Integer defaultHue;
    Frame previousFrame;
    Logger myLogger;
    CorrelatorForTwoPointClouds correlatorForTwoPointClouds;

    //Pointclouds
    PointCloudOriginalImage pointCloudOriginalRealWorld;
    PointCloudTopOfHeadShifted pointCloudTopOfHeadShiftedRealWorld;
    PointCloudForZPointPlane pointCloudCorrelationRealWorld;
    PointCloudFinalImage pointCloudFinalRealWorld;
    PointCloud cloudArray[];

    //Final rotate and shift
    PVector finalCorrelationRealWorldRotateToPreviousCloud = new PVector(0f, 0f, 0f);
    PVector finalCorrelationRealWorldShiftToPreviousCloud = new PVector(0f, 0f, 0f);  //just this image

    public Logger getMyLogger() {
        return myLogger;
    }

    public CorrelatorForTwoPointClouds getCorrelatorForTwoPointClouds() {
        return correlatorForTwoPointClouds;
    }

    public PointCloudOriginalImage getPointCloudOriginalRealWorld() {
        return pointCloudOriginalRealWorld;
    }

    public PointCloudTopOfHeadShifted getPointCloudTopOfHeadShiftedRealWorld() {
        return pointCloudTopOfHeadShiftedRealWorld;
    }

    public PointCloudForZPointPlane getPointCloudCorrelationRealWorld() {
        return pointCloudCorrelationRealWorld;
    }

    public PointCloudFinalImage getPointCloudFinalRealWorld() {
        return pointCloudFinalRealWorld;
    }

    public PointCloud[] getCloudArray() {
        return cloudArray;
    }

    public PVector getFinalCorrelationRealWorldRotateToPreviousCloud() {
        return finalCorrelationRealWorldRotateToPreviousCloud;
    }

    public PVector getFinalCorrelationRealWorldShiftToPreviousCloud() {
        return finalCorrelationRealWorldShiftToPreviousCloud;
    }

    public Integer getDefaultHue() {
        return defaultHue;
    }

    public int getFrameNo() {
        return frameNo;
    }

    public Frame getPreviousFrame() {
        return previousFrame;
    }

    public ThreeDImageCreator getParentThreeDImageCreator() {
        return parentThreeDImageCreator;
    }

    public Frame(ThreeDImageCreator myThreeDImageCreator, int myFrameNo, int myHue, Frame myPreviousFrame, boolean isTest) {  //constructor  parentThreeDImageCreatorSimpleOpenNI
        parentThreeDImageCreator = myThreeDImageCreator;
        frameNo = myFrameNo;
        previousFrame = myPreviousFrame;
        defaultHue = myHue;
        //parentContext = parentThreeDImageCreator.getNi();  //How to deal with this for a test image?
        myLogger = LoggerFactory.getLogger(this.getClass());
        //setDisplayWidthAndHeight();  //Get directly from context now using getters.
        //pointCloudOriginalRealWorld = new PointCloudOriginalImage(this);  //The constructor does a lot here. It adds all the points from the original image.
        myLogger.debug("Frame constructor - myFrameNo = "+myFrameNo+" myHue = "+myHue+" myPreviousFrame = "+myPreviousFrame + " isTest = "+ isTest);
        pointCloudOriginalRealWorld = new PointCloudOriginalImage(this, isTest);  //The constructor does a lot here. It adds all the points from the original image.
        pointCloudTopOfHeadShiftedRealWorld = new PointCloudTopOfHeadShifted(this);  //These get set up below
        pointCloudCorrelationRealWorld = new PointCloudForZPointPlane(this);  //These get set up below
        pointCloudFinalRealWorld = new PointCloudFinalImage(this);  //These get set up below
        setUpRemainingPointClouds(); //Shift top of head to origin
        printClouds();
    }

    private void setUpArrayOfPointCloudsForEasyDebugPrintingAndSettingExtents(){ //Only used for printing all four clouds.
        cloudArray = new PointCloud[4];
        cloudArray[0] = getPointCloudOriginalRealWorld();
        cloudArray[1] = getPointCloudTopOfHeadShiftedRealWorld();
        cloudArray[2] = getPointCloudCorrelationRealWorld();
        cloudArray[3] = getPointCloudFinalRealWorld();
    }
    /*public SimpleOpenNI getParentContext(){
        //return myParentSimpleOpenNI;
        return parentThreeDImageCreator.getNi();
    }*/

    /*private void setDisplayWidthAndHeight(){
        depthWidth = getParentContext().depthWidth();
        // halfDepthWidth = (int) (depthWidth / 2);
        depthHeight = getParentContext().depthHeight();
        //halfDepthHeight = (int) (depthHeight / 2);
    }*/

    public void setUpRemainingPointClouds() {  //setUpTopOfHeadShiftedRealWorldImageArrayListRemovingShoulders
        setUpArrayOfPointCloudsForEasyDebugPrintingAndSettingExtents();
        //PVector myShiftVector = new PVector(0, 0, 0);  //How much to offset the image for better display/rotate etc, if not overridden by actual top of head.   //PVector(0, 100, -800);
        //if (alignTopOfHeadBeforeCorrelating) {  //TODO - Don't think this is used right now. We have to shift this so we can rotate round origin.
        PVector myShiftVector = getPointCloudOriginalRealWorld().getMidRealWorldPoint();   //myShiftVector = pointCloudOriginalRealWorld.getTopCentreRealWorldPoint();
        myShiftVector.mult(-1f);
        //}
        myLogger.debug("tttt - myShiftVector = "+ myShiftVector);
        for (FloatColoredPVector anOriginalRealWorldImageArrayList : getPointCloudOriginalRealWorld().getOneAndOnlyArrayList()) {
            FloatColoredPVector myFloatColoredPVector = anOriginalRealWorldImageArrayList.createCopy();
            //if(alignTopOfHeadBeforeCorrelating){
            myFloatColoredPVector.add(myShiftVector);   //Shift the top if needed.
            //}
            //if (myColoredPVector.intY > maxHeightLimit) { //Moved to original Image when cutting off the background.
                //myColoredPVector.intY += absHalfHeightLimit;  //Move the origin half way down the head for easier correlation //TODO put the origin in the middle of the head.
                getPointCloudTopOfHeadShiftedRealWorld().addColoredPVector(myFloatColoredPVector.createCopy());
                getPointCloudCorrelationRealWorld().addColoredPVector(myFloatColoredPVector.createCopy());  //Also set up the correlation PVector (this is the one that will be rotated later to create rotated zMatrices).
                getPointCloudFinalRealWorld().addColoredPVector(myFloatColoredPVector.createCopy()); //Also set up the final image which will be rotated later.
            //}
        }
        for (int i = 1; i<4; i++) cloudArray[i].setExtentsFromCloudShiftedByPVector(getPointCloudOriginalRealWorld(), myShiftVector);
        //The line above just copies extents over Actually need to set the extents earlier when we do headshift etc.
    }

    public void resetCorrelationCloudFromTopOfHeadCloud() { //TODO - is this the missing bit causing bug in the step by step correlator?
        getPointCloudCorrelationRealWorld().getOneAndOnlyArrayList().clear();
        for (FloatColoredPVector aTopOfHeadShiftedRealWorldImageArrayList : getPointCloudTopOfHeadShiftedRealWorld().getOneAndOnlyArrayList()) {
            FloatColoredPVector myFloatColoredPVector = aTopOfHeadShiftedRealWorldImageArrayList.createCopy();
            getPointCloudCorrelationRealWorld().getOneAndOnlyArrayList().add(myFloatColoredPVector);  //Also set up the correlation PVector (this is the one that will be rotated later to create rotated zMatrices).
        }
    }
    public synchronized void alignPointsInFinalImageToCloud0() { //TODO split out arrayList of ColoredPVectors into its own class that knows how to rotate or shift all points.
        //PApplet.println("****&&&**** alignPointsInFinalImageToCloud0 finalCorrelationRealWorldShiftToCloud0 = " + finalCorrelationRealWorldShiftToCloud0 + " finalCorrelationRealWorldRotateToCloud0  = " + finalCorrelationRealWorldRotateToCloud0);
        //PApplet.println(" finalCorrelationRealWorldShiftToNextCloud = " + finalCorrelationRealWorldShiftToPreviousCloud);
        myLogger.info(" finalCorrelationRealWorldRotateToNextCloud = " + finalCorrelationRealWorldRotateToPreviousCloud);
        //PApplet.println(" finalCorrelationRealWorldShiftToCloud0 = " + finalCorrelationRealWorldShiftToCloud0);
        //PApplet.println(" finalCorrelationRealWorldRotateToCloud0 = " + finalCorrelationRealWorldRotateToCloud0);
        PVector myWorkingCloudFinalRealWorldRotateToNextCloud, myWorkingCloudFinalRealWorldShiftToNextCloud;
        //PointCloud workingCloud = this;
        Frame workingFrame = this;//Start of whith this as the working frame, then work backwards.
        //while (workingCloud != null) {
        while (workingFrame != null) {
            //Iterator<ColoredPVector> myIterator = oneAndOnlyArrayList.iterator();  //For each point in final image.
            for(FloatColoredPVector myFloatColoredPVector : workingFrame.getPointCloudFinalRealWorld().getOneAndOnlyArrayList()) {
                //myWorkingCloudFinalRealWorldRotateToNextCloud = workingCloud.parentFrame.finalCorrelationRealWorldRotateToPreviousCloud;
                //myWorkingCloudFinalRealWorldShiftToNextCloud = workingCloud.parentFrame.finalCorrelationRealWorldShiftToPreviousCloud;
                myWorkingCloudFinalRealWorldRotateToNextCloud = getFinalCorrelationRealWorldRotateToPreviousCloud();
                myWorkingCloudFinalRealWorldShiftToNextCloud = getFinalCorrelationRealWorldShiftToPreviousCloud();
                //while (myIterator.hasNext()) {
                //CHANGED TO ROTATE BEFORE SHIFTING.   THIS SHOULD IMPROVE IT...
                //ColoredPVector myVector = myIterator.next();
                /*rotatePoint(myVector, myWorkingCloudFinalRealWorldRotateToNextCloud.intX, 'intX');
                rotatePoint(myVector, myWorkingCloudFinalRealWorldRotateToNextCloud.intY, 'intY');
                rotatePoint(myVector, myWorkingCloudFinalRealWorldRotateToNextCloud.z, 'z');*/
                //TODO - three separate rotates needed here? Or just single one?
                //TODO - this looks slow. Is there a way to speed it up? You can't just add all the rotates and all the shifts here as a rotate shift rotate shift has different result than a rotate rotate shift shift.
                myFloatColoredPVector.rotate3dRadians(myWorkingCloudFinalRealWorldRotateToNextCloud.x, 'x');
                myFloatColoredPVector.rotate3dRadians(myWorkingCloudFinalRealWorldRotateToNextCloud.y, 'y');
                myFloatColoredPVector.rotate3dRadians(myWorkingCloudFinalRealWorldRotateToNextCloud.z, 'z');
                myFloatColoredPVector.add(myWorkingCloudFinalRealWorldShiftToNextCloud);   //Add the final correlation shift INCLDING SHIFTS FROM PREVIOUS CLOUD SHIFTS (these aren't actually added to any collections of points, they're treated as offsets throughout the correlation to speed it up.
                //finalRealWorldImageArrayList.add(myVector);  //could use this to draw later..
                //myModel.finalPointCloud.add(myVector);
            }
            //workingCloud = workingCloud.parentFrame.previousFrame.pointCloudCorrelationRealWorld;   // myPreviousPointCloud
            workingFrame = workingFrame.getPreviousFrame();
        }
    }
    public void printClouds() {
        for(PointCloud aPointCloud : getCloudArray()){
            myLogger.debug(aPointCloud.toString());
        }
    }


    /*public int getDepthWidth(){
        return depthWidth;
    }

    public int getDepthHeight() {
        return depthHeight;
    }*/
}
