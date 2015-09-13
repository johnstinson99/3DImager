package imager.domain.Correlators;

import imager.domain.PointClouds.Frame;
import imager.domain.PointClouds.PointCloudForZPointPlane;
import imager.domain.PointClouds.ZPointPlane;
import imager.domain.ThreeDImageCreator;
import imager.domain.ThreeDObjects.IntPVector2D;
import imager.domain.ThreeDObjects.IntPVector3D;
import imager.domain.ThreeDObjects.IntRectangle2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processing.core.*;

import java.io.IOException;
import java.util.ArrayList;

public abstract class CorrelatorForTwoPointClouds {
    //Adjustable parameters
    //public final int zStepsPerCorrelationPosition = 1;   //Shift the correlation cloud by this number of steps each time?  //This is great - reducing this to 1 from 10 doesn't seem to slow down even though it means hundred times as many correlation checks.
    PVector zPointSizePVector = new PVector(3f, 3f, 3f); //Moved from static in zPointPlane, so it can be overridden by the correlator.

    public final boolean takeColorsIntoAccountWhenCorrelating = true;
    static int staticMostRecentSumOfMatchedPoints = 0;
    Frame frame0, frame1;
    PointCloudForZPointPlane cloud0, cloud1;
    ZPointPlane zPointPlane0, zPointPlane1;
    ThreeDImageCreator parent;
    //IntRectangle2D zPointCoordOverlapIntRect = new IntRectangle2D(); //Really important one - the overlap once the correlation shift has been applied
    int minPointsForACorrelationMatch = 100;  //It was giving 100% matches on matching a single point.   If zMatrix contains 1000 points, then more than 25% should match.
    Logger myLogger;


    //New params for splitting out inside of correlate
    float interimMinScore, overallMinScore, correlationScore;

    int angleCounter;
    int anglePositionOfMinScore;



    float totalAngleShift;
    IntPVector3D currentCorrelationOffsetIntPVector3D;
    IntPVector3D minCorrelationOffsetIntPVector3D;
    IntPVector3D maxCorrelationOffsetIntPVector3D;
    IntPVector3D overallLowestScoreZShiftIntPVector3D;
    IntPVector3D interimLowestScoreZShiftIntPVector3D;

    int rotateAnglePositionsTotal;
    float rotateAngleIncrement;
    int slideZMatrixPointIncrement;
    IntPVector3D slidePositionsEachSideIntPVector3D = new IntPVector3D(0,0,0);
    char rotateAxis;
    //int defaultMinScoreForGoodMatch;  //not really used
    float minScoreForGoodMatch;
    boolean correlateColors;
    long correlationStartTime;
    int rotateAnglePositionsLeft;

    //And for treating input params as an object
    ArrayList<CorrelatorZInputParams> myCorrelatorZInputParamsArrayList;

    int totalCorrelationSetsToRunForThisPair;
    int currentCorrelationSetForThisPair;

    //And for graphing the correlation
    ArrayList<PVector> myGraphOriginalPVectorArrayList = new ArrayList<PVector>();
    ArrayList<PVector> myGraphChartScaledPVectorArrayList = new ArrayList<PVector>();
    int myGraphMaxX;
    float myGraphMaxY = (float) 15.0;   //10 here means max intY is 1^10 in practice as we're drawing the log of the number.

    private final PVector zeroPVector = new PVector(0,0,0);
    public void setTotalAngleShift(float totalAngleShift) {
        this.totalAngleShift = totalAngleShift;
    }
    public void setAngleCounter(int angleCounter) {
        this.angleCounter = angleCounter;
    }
    public void setInterimMinScore(float interimMinScore) {
        this.interimMinScore = interimMinScore;
    }

    public PVector getzPointSizePVector() {
        return zPointSizePVector;
    }

    public boolean isTakeColorsIntoAccountWhenCorrelating() {
        return takeColorsIntoAccountWhenCorrelating;
    }

    public static int getStaticMostRecentSumOfMatchedPoints() {
        return staticMostRecentSumOfMatchedPoints;
    }

    public Frame getFrame0() {
        return frame0;
    }

    public Frame getFrame1() {
        return frame1;
    }

    public PointCloudForZPointPlane getCloud0() {
        return cloud0;
    }

    public PointCloudForZPointPlane getCloud1() {
        return cloud1;
    }

    public ZPointPlane getzPointPlane0() {
        return zPointPlane0;
    }

    public ZPointPlane getzPointPlane1() {
        return zPointPlane1;
    }

    public ThreeDImageCreator getParent() {
        return parent;
    }

    public int getMinPointsForACorrelationMatch() {
        return minPointsForACorrelationMatch;
    }

    public Logger getMyLogger() {
        return myLogger;
    }

    public float getInterimMinScore() {
        return interimMinScore;
    }

    public float getOverallMinScore() {
        return overallMinScore;
    }

    public float getCorrelationScore() {
        return correlationScore;
    }

    public int getAngleCounter() {
        return angleCounter;
    }

    public int getAnglePositionOfMinScore() {
        return anglePositionOfMinScore;
    }

    public float getTotalAngleShift() {
        return totalAngleShift;
    }

    public IntPVector3D getCurrentCorrelationOffsetIntPVector3D() {
        return currentCorrelationOffsetIntPVector3D;
    }

    public IntPVector3D getMinCorrelationOffsetIntPVector3D() {
        return minCorrelationOffsetIntPVector3D;
    }

    public IntPVector3D getMaxCorrelationOffsetIntPVector3D() {
        return maxCorrelationOffsetIntPVector3D;
    }

    public IntPVector3D getOverallLowestScoreZShiftIntPVector3D() {
        return overallLowestScoreZShiftIntPVector3D;
    }

    public IntPVector3D getInterimLowestScoreZShiftIntPVector3D() {
        return interimLowestScoreZShiftIntPVector3D;
    }

    public int getRotateAnglePositionsTotal() {
        return rotateAnglePositionsTotal;
    }

    public float getRotateAngleIncrement() {
        return rotateAngleIncrement;
    }

    public int getSlideZMatrixPointIncrement() {
        return slideZMatrixPointIncrement;
    }

    public IntPVector3D getSlidePositionsEachSideIntPVector3D() {
        return slidePositionsEachSideIntPVector3D;
    }

    public char getRotateAxis() {
        return rotateAxis;
    }

   /*public int getDefaultMinScoreForGoodMatch() {
        return defaultMinScoreForGoodMatch;
    }*/

    /*public float getMinScoreForGoodMatch() {
        return minScoreForGoodMatch;
    }*/

    public boolean isCorrelateColors() {
        return correlateColors;
    }

    public long getCorrelationStartTime() {
        return correlationStartTime;
    }

    public int getRotateAnglePositionsLeft() {
        return rotateAnglePositionsLeft;
    }

    public ArrayList<CorrelatorZInputParams> getMyCorrelatorZInputParamsArrayList() {
        return myCorrelatorZInputParamsArrayList;
    }

    public int getTotalCorrelationSetsToRunForThisPair() {
        return totalCorrelationSetsToRunForThisPair;
    }

    public int getCurrentCorrelationSetForThisPair() {
        return currentCorrelationSetForThisPair;
    }

    public ArrayList<PVector> getMyGraphOriginalPVectorArrayList() {
        return myGraphOriginalPVectorArrayList;
    }

    public ArrayList<PVector> getMyGraphChartScaledPVectorArrayList() {
        return myGraphChartScaledPVectorArrayList;
    }

    public int getMyGraphMaxX() {
        return myGraphMaxX;
    }

    public float getMyGraphMaxY() {
        return myGraphMaxY;
    }


    public CorrelatorForTwoPointClouds(ThreeDImageCreator p){
        myLogger = LoggerFactory.getLogger(this.getClass());
        parent = p;
        parent.setCorrelatorForTwoPointClouds(this);
        currentCorrelationOffsetIntPVector3D = new IntPVector3D(0,0,0);  //TODO reset this on the next correlate.
    }

    public static void incrementCorrelationPositionTowardsMax(IntPVector3D myCurrentPos, IntPVector3D maxPos, IntPVector3D minPos){
        //IntPVector3D result = myCurrentPos.createCopy();
        if(myCurrentPos.equals(maxPos)){
            System.out.println("ERROR - tried to increment position when already at max");
            return;
        }
        myCurrentPos.intX++;
        if(myCurrentPos.intX>maxPos.intX){
            myCurrentPos.intX = minPos.intX;
            myCurrentPos.intY++;
        }
        if(myCurrentPos.intY>maxPos.intY){
            myCurrentPos.intY = minPos.intY;
            myCurrentPos.intZ++;
        }
    }


    public void startCorrelate(boolean thisIsTheLastOne, String complexity){
        //TODO - input params aren't used in this method. Clean up?
        //Set up a collection of setup objects containing these parameters. Loop through the collection.

        myCorrelatorZInputParamsArrayList = CollectionOfCorrelationParams.getCollection();
        this.setUpInputParamsVariables();  //Sets up totalCorrelationsToRunForThisPair and  currentCorrelationNumberForThisPair;
        prepareForNextSetOfCorrelations();
    }
    //comment for GIT

    public void setupFrames(Frame f0, Frame f1){
        frame0 = f0;
        frame1 = f1;
        cloud0 = f0.getPointCloudCorrelationRealWorld();
        cloud1 = f1.getPointCloudCorrelationRealWorld();
        //setCloud0Extents();  //These are now set on setup of Cloud0.
        setCloud1ExtentsAndZPointPlanes(); //(false, 0,'intX');
    }
//new comment with more words
//added new change

    public void setCloud1ExtentsAndZPointPlanes() {
        setUpZPointPlanes();    //changes on rotate
        //setupOverlapIntRect();
    }

    private IntRectangle2D getOverlapIntRect(){
        IntRectangle2D zPointPlane0Rect = zPointPlane0.getCorrelationShiftedBoundingIntRectangle2D();  //No correlation shift in this case.
        IntRectangle2D shiftedZPointPlane1Rect = zPointPlane1.getCorrelationShiftedBoundingIntRectangle2D(); //It should be fine to pass reference here and not create copy, as its only used to work out overlap. //IntRectangle.sub(getZPointPlane1Rect(), zPointPlane1.getMinCorrelationShiftedIntPVectorZPointCoord());
        return IntRectangle2D.overlap(zPointPlane0Rect, shiftedZPointPlane1Rect);
    }

    //Rotate and move second cloud to correlate
    public void rotateSecondCloud(float beta, char myAxis, boolean setUpZPointPlane) {
        //Cumulative rotation is stored in the pointcloud itself.
        //Pointcloud will keep a deep copy of the original
        //So the total cumulative shifts and rotations can be applied to the original.
        myLogger.debug("ROTATING SECOND POINGCLOUD beta = "+beta+", myAxis = "+myAxis);
        cloud1.rotateCloudAndSetPointCloudExtentsWithoutSettingUpZPointPlane(beta, myAxis);
        if (setUpZPointPlane)
            zPointPlane1 = cloud1.setUpZPointPlaneAndMapCorrelator(this);
     }
    public void rotateSecondCloudWithoutSettingUpZPointPlane(float beta, char myAxis) { //TODO - is this ever called?
        cloud1.rotateCloudAndSetPointCloudExtentsWithoutSettingUpZPointPlane(beta, myAxis);  //Don't set up zPointPlane yet at end of correlate. Wait until after rotate and shift.
    }

    public void setUpZPointPlanes() {
        zPointPlane0 = cloud0.setUpZPointPlaneAndMapCorrelator(this);
        zPointPlane1 = cloud1.setUpZPointPlaneAndMapCorrelator(this);
        minPointsForACorrelationMatch = PApplet.min(zPointPlane0.getZMatrixTotalNoOfPoints(), zPointPlane1.getZMatrixTotalNoOfPoints()) / 4;  //TODO - just look at the size of the overlap instead.   That way you can bypass a lot of the correlation.
    }

    //TODO - Merge this method with setUpInitialCorrelationVariables()
    public void setUpInitialCorrelationVariablesFromObject(CorrelatorZInputParams myCorrelatorZInputParams){
        PVector myZPointSizePVector = myCorrelatorZInputParams.getzPointSizeSizePVector();
        int mySlideZMatrixPointIncrement = myCorrelatorZInputParams.getSlideZMatrixPointIncrement();
        IntPVector3D mySlidePositionsEachSideIntPVector3D = myCorrelatorZInputParams.getSlidePositionsEachSide();
        char myRotateAxis = myCorrelatorZInputParams.getRotateAxis();
        float myRotateAngleIncrementRadians = myCorrelatorZInputParams.getRotateAngleIncrementRadians();
        int myRotateAnglePositionsLeft = myCorrelatorZInputParams.getRotateAnglePositionsLeft();
        int myRotateAnglePositionsTotal = myCorrelatorZInputParams.getRotateAnglePositionsTotal();
        float myMinScoreForGoodMatch = myCorrelatorZInputParams.getMinScoreForGoodMatch();
        boolean myCorrelateColors = myCorrelatorZInputParams.getCorrelateColors();
        int xSlidePositions = mySlidePositionsEachSideIntPVector3D.intX*2+1;
        int ySlidePositions = mySlidePositionsEachSideIntPVector3D.intY*2+1;
        int zSlidePositions = mySlidePositionsEachSideIntPVector3D.intZ*2+1;
        IntPVector3D totalSlidePositionsIntPVector = new IntPVector3D(xSlidePositions, ySlidePositions, zSlidePositions);
        myLogger.info("about to set up GraphMaxX for totalSlidePositions "+ totalSlidePositionsIntPVector);
        setUpGraphMaxX(totalSlidePositionsIntPVector, myRotateAnglePositionsTotal);
        setUpInitialCorrelationVariables(myZPointSizePVector, mySlideZMatrixPointIncrement, mySlidePositionsEachSideIntPVector3D, myRotateAxis, myRotateAngleIncrementRadians, myRotateAnglePositionsLeft, myRotateAnglePositionsTotal, myMinScoreForGoodMatch, myCorrelateColors);
    }

    public void setUpGraphMaxX(IntPVector3D totalSlidePositionsIntPVector, int myRotateAnglePositionsTotal){
        myLogger.error("setUpGraphMaxX in superclass- Should never get here");
    }; //Should never get called

    public void setUpInitialCorrelationVariables(PVector myZPointSizePVector, int mySlideZMatrixPointIncrement, IntPVector3D mySlidePositionsEachSideIntPVector3D, char myRotateAxis, float myRotateAngleIncrement, int myRotateAnglePositionsLeft, int myRotateAnglePositionsTotal, float myMinScoreForGoodMatch, boolean myCorrelateColors){
        //This is a big step away from functional programming.
        //The Correlator object itself keeps track of all these variables,
        //Through the various stages of correlation.
        zPointSizePVector = myZPointSizePVector;
        overallMinScore = ThreeDImageCreator.veryHighFloat;
        rotateAngleIncrement = myRotateAngleIncrement;
        rotateAxis = myRotateAxis;
        myRotateAnglePositionsLeft = myRotateAnglePositionsLeft;
        float minAngle = -rotateAngleIncrement * myRotateAnglePositionsLeft;
        myLogger.debug("minAngle = " + minAngle);
        anglePositionOfMinScore = (int) (-minAngle / rotateAngleIncrement);  //Takes it back from correlation start position to original start position.
        slidePositionsEachSideIntPVector3D = mySlidePositionsEachSideIntPVector3D;
        setStartCurrentAndEndOffsets(mySlidePositionsEachSideIntPVector3D);
        slideZMatrixPointIncrement = mySlideZMatrixPointIncrement;
        //noOfCountShifts = slidePositionsEachSide * 2 + 1;//		int min = -60;
        overallLowestScoreZShiftIntPVector3D = new IntPVector3D(0,0,0);
        interimLowestScoreZShiftIntPVector3D = new IntPVector3D(0,0,0);
        rotateAnglePositionsTotal = myRotateAnglePositionsTotal;
        //Set start position
        rotateSecondCloud(minAngle, rotateAxis, true); //final param is set up ZPointPLane.
        //shiftSecondZMatrixByIntPVector(xCountMin, yCountMin, zCountMin);
        setSecondZMatrixOffsetTo(minCorrelationOffsetIntPVector3D); //eg -1,-1,-1
        totalAngleShift = minAngle; //AHA - this only gets set up when we set up the thing for the first time??
        correlationScore = ThreeDImageCreator.veryHighFloat;
        minScoreForGoodMatch = myMinScoreForGoodMatch;
        correlateColors = myCorrelateColors;
        angleCounter = 0;
    }

    public void setStartCurrentAndEndOffsets(IntPVector3D mySlidePositionsEachSideIntPVector3D){
        //Should never get here
    }
    public void setSecondZMatrixOffsetTo(IntPVector3D myIntPVector3D){
       // myIntPVector3D.copyValuesTo(zPointPlane1.correlationOffsetIntPVector3D); ///TODO - Test this - should pass a reference to the IntPVector and change values of the referenced objec
        zPointPlane1.setCorrelationOffsetIntPVector3D(new IntPVector3D(myIntPVector3D.intX, myIntPVector3D.intY, myIntPVector3D.intZ));
    }

    public boolean resetImagesAfterCorrelationAndReturnResult(){
        myLogger.debug("yyyy in resetImagesAfterCorrelationAndReturnResult");
        float angleShiftBackToMinimumPosition;
        // anglePositionOfMinScore should always be correct whether or not a lower minimum has been found.
        //       	  This shifts back to start position + This is the number of shifts as zero based.
        myLogger.info("rotateAnglePositionsTotal = " + rotateAnglePositionsTotal);
        myLogger.info("anglePositionOfMinScore (zero based) = " + anglePositionOfMinScore);
        myLogger.info("rotateAngleIncrementRadians = " + rotateAngleIncrement);
        if (overallMinScore < ThreeDImageCreator.veryHighFloat) {
            //TODO - expensive only do it if there's a change.
            angleShiftBackToMinimumPosition = (-(rotateAnglePositionsTotal - 1) + anglePositionOfMinScore) * rotateAngleIncrement; //+ minAngle - totalAngleShift;
            //angleShiftBackToMinimumPosition = -1;
            if (angleShiftBackToMinimumPosition == 0)
                myLogger.info("No angle shift - leave it as it is");
            else{
                myLogger.info("angleShiftBackToMinimumPosition = " + angleShiftBackToMinimumPosition);
                rotateSecondCloud(angleShiftBackToMinimumPosition, rotateAxis, false); //final param is boolean setUpZPointPlane.
                myLogger.debug("Setting angle back angleShiftBackToMinimumPosition = " + angleShiftBackToMinimumPosition + " rotate axis = " + rotateAxis);
            }
            PVector myOffsetPVector = IntPVector3D.mult(overallLowestScoreZShiftIntPVector3D,zPointSizePVector);
            if(myOffsetPVector.equals(zeroPVector))
                myLogger.info("No shift needed");
            else {
                myLogger.info("Shifting pointCloud by" + myOffsetPVector);
                zPointPlane1.getPointCloudForZPointPlane().shiftAllPointsByPVector(myOffsetPVector);
            }
            //Only move the underlying cloud if something has changed.
            if(angleShiftBackToMinimumPosition != 0 || !myOffsetPVector.equals(zeroPVector)) {
                myLogger.debug("in reset - pointCloudForZPointPlane.setBoundingRectangle()");

                //zPointPlane1.pointCloudForZPointPlane.setBoundingRectangle();  //Could do this as part of the rotate, but then would also need to do it as part of the shift.
                //zPointPlane1.setUpZMatrixCommon();
                //zPointPlane1 = cloud1.setUpZPointPlane(); //this calls setUpZMatrixCommon.  Very important that we set the zPointPlane variable here.
                zPointPlane1 = cloud1.setUpZPointPlaneAndMapCorrelator(this);
            }
            else{
                myLogger.debug("Not resetting plane");
            }
        } else {

            /*
            //Work this out later.
            angleShiftBackToMinimumPosition = ;//TODO work this out.
            //PApplet.println("NO MATCH FOUND!!!!");
            myLogger.warn("NO MATCH FOUND");
            //angleShiftBack = (-(rotateAnglePositionsTotal-1) + anglePositionOfMinScore)*rotateAngleIncrementRadians ;
            //xShiftZMatrixPoints = yShiftZMatrixPoints = zShiftZMatrixPoints = slidePositionsEachSide * slideZMatrixPointIncrement;
            //TODO - is this where it's going wrong?  No match found, yet we still shift the matrix?
            //TODO - should we just set the min back to the original min instead?
            //TODO ?????????????????????
            //TODO ?????????????????????????
            //xShiftZMatrixPoints = yShiftZMatrixPoints = zShiftZMatrixPoints = slidePositionsEachSide;
            //shiftIntPVector.set(0, 0, 0);
            //Don't need to do anything here, as lowestScoreZShiftIntPVector was set up to (0,0,0) originally
            */
        }
        //Rotate correlating cloud (and zMatrix) back to minimum point.
        //The xyz shifts are virtual, but we do still need to move the underlying image, to get it ready for the next correlation.
        boolean finalResult = overallMinScore < minScoreForGoodMatch; //must calc this before resetting variables.
        resetAllCorrelationVariables();  //This may not be needed if there's another correlation to follow, but it helps clean up.

        //Time it
        Long myEndTime = System.currentTimeMillis(); //Defaults.getTimeInMS();
        long diffInMS = myEndTime - parent.getCorrelationStartTime();
        float diffInSecs = diffInMS/1000f;
        myLogger.info("");
        myLogger.info("===================================");
        myLogger.info("Time difference in seconds = "+diffInSecs ); // "Diff in ms = "+ diffInMS);
        myLogger.info("===================================");

        return finalResult;
    }


    private void resetAllCorrelationVariables(){
        currentCorrelationOffsetIntPVector3D = new IntPVector3D(0,0,0);
        overallMinScore = ThreeDImageCreator.veryHighFloat;
        correlationScore = ThreeDImageCreator.veryHighFloat;
        angleCounter = 0;
        anglePositionOfMinScore = 0; //TODO need to check we've remembered this somewhere.
        totalAngleShift = 0;
    }

    public void doActualCorrelation(){
        //long correlationStartTime, int slideZMatrixPointIncrement, int slidePositionsEachSide, char rotateMyAxis, float rotateAngleIncrementRadians, int rotateAnglePositionsLeft, int rotateAnglePositionsTotal, float minScoreForGoodMatch, boolean correlateColors, float totalAngleShift
        myLogger.debug("doActualCorrelation");
        correlationScore = correlationScoreOnePosition();  //dont draw lines or print correlation result

        addPointToChart(correlationScore);

        //Try drawing parent. This may not work here.
        //myLogger.info("TRYING TO DRAW...");
        //parent.draw();
        if (correlationScore < interimMinScore){
            currentCorrelationOffsetIntPVector3D.copyValuesTo(interimLowestScoreZShiftIntPVector3D);
            interimMinScore = correlationScore;
        }

        if (correlationScore < overallMinScore) {
            try {
                //parent.excelLogFileWriter.
                parent.getExcelLogFileWriter().append(correlationStartTime + ", " + frame1.getFrameNo() + ", correlateBoxAtAnglesDontDrawSteps, " + slideZMatrixPointIncrement + "-" + slidePositionsEachSideIntPVector3D.toStringReally() + "-" + rotateAxis + "-" + rotateAngleIncrement + "-" + rotateAnglePositionsLeft + "-" + rotateAnglePositionsTotal + "-" + minScoreForGoodMatch + "-" + correlateColors + ", " + System.currentTimeMillis() + ", " + totalAngleShift + ", " + currentCorrelationOffsetIntPVector3D + ", " + staticMostRecentSumOfMatchedPoints + ", " + correlationScore + ", " + overallMinScore);
                parent.getExcelLogFileWriter().newLine();
                parent.getExcelLogFileWriter().flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            overallMinScore = correlationScore;
            /*xCountOfMinScore = xCount;
            yCountOfMinScore = yCount;
            zCountOfMinScore = zCount;*/
            currentCorrelationOffsetIntPVector3D.copyValuesTo(overallLowestScoreZShiftIntPVector3D);  //TODO - check this
            anglePositionOfMinScore = angleCounter;
            //PApplet.println("Score = " + correlationScore + " Most recent matched points = " + staticMostRecentSumOfMatchedPoints + " overallMinScore = " + overallMinScore + " angleNo = " + angleCounter + " intX = " + xCount + " intY = " + yCount + " z = " + zCount);
        }
        //myLogger.debug("Score = " + correlationScore + " Most recent matched points = " + staticMostRecentSumOfMatchedPoints + " overallMinScore = " + overallMinScore + " angleNo = " + angleCounter + " intX = " + xCount + " intY = " + yCount + " z = " + zCount);
    }

    public float correlationScoreOnePosition() {
        //int mySteps = zStepsPerCorrelationPosition;
        float sumOfScoresForAllCellsForThisPosition = 0;
        int sumOfMatchedPoints = 0;
        IntRectangle2D overlapIntRectangle2D = getOverlapIntRect();
        staticMostRecentSumOfMatchedPoints = 0;  //This is useful for debugging so calling method knows the last Sum of matched points.
        float colorMatchScore;
        float scoreForThisCell;
        myLogger.debug("");
        myLogger.debug("======================================================");
        myLogger.debug("In correlatinoScoreOnePosition zPoinCoordOverlapIntRect = "+overlapIntRectangle2D);
        myLogger.debug("zPointPlane0 = "+zPointPlane0);
        myLogger.debug("zPointPlane1 = "+zPointPlane1);
        //myLogger.debug("mySteps = "+mySteps );
        int minX = overlapIntRectangle2D.getMin().intX;
        int maxX = overlapIntRectangle2D.getMax().intX;
        int minY = overlapIntRectangle2D.getMin().intY;
        int maxY = overlapIntRectangle2D.getMax().intY;
        IntPVector2D zPoint2dBeingProcessed = new IntPVector2D();
        float zCorrelationShift = currentCorrelationOffsetIntPVector3D.intZ*zPointSizePVector.z;
        for (zPoint2dBeingProcessed.intY = minY; zPoint2dBeingProcessed.intY <= maxY; zPoint2dBeingProcessed.intY++) {
            for (zPoint2dBeingProcessed.intX = minX; zPoint2dBeingProcessed.intX <= maxX; zPoint2dBeingProcessed.intX++) {
                //IntPVector2D zPointPlane1BeingProcessed2d = IntPVector2D.sub(zPoint2dBeingProcessed, currentCorrelationOffsetIntPVector3D);  //subtact is correct here. Imagine two 2*2 squares, first overlapping, then shift one of them by (1,1) //Note, the z gets ignored here in the 2d conversion but used later.
                IntPVector2D arrayIntPVector2D_0 = zPointPlane0.getArrayPointForZMatrixPoint(zPoint2dBeingProcessed);
                IntPVector2D arrayIntPVector2D_1 = zPointPlane1.getArrayPointForZMatrixPoint(zPoint2dBeingProcessed);
                //myLogger.debug("zPoint2dBeingProcessed = "+ zPoint2dBeingProcessed);
                //myLogger.debug("arrayIntPVector2D_0 = "+arrayIntPVector2D_0 + ", zPointPlane 0 correlation offset = "+ zPointPlane0.getCorrelationOffsetIntPVector3D());
                //myLogger.debug("arrayIntPVector2D_1 = "+arrayIntPVector2D_1 + ", zPointPlane 1 correlation offset = "+ zPointPlane1.getCorrelationOffsetIntPVector3D());
                float dist = getZDistBetweenTwoArrayPointsWithoutZCorrelationShift(zPointPlane0, zPointPlane1, arrayIntPVector2D_0, arrayIntPVector2D_1);
                if(dist != ThreeDImageCreator.veryHighFloat){
                    dist += zCorrelationShift;  //Corrrelation shift of zPlane1 in positive direction means greater distance in positive direction, as distance is 1-0.
                    float distScore = 1+ dist*dist; //Need to add one if there's an exact match, to retain the colour data.
                    if (takeColorsIntoAccountWhenCorrelating) {
                        //Need to add 1 as if theres a complete match we still need to retain the distance data.
                        colorMatchScore = 1+ hueComparisonScoreForMultiplicationForArrayPoint(zPointPlane0, zPointPlane1, arrayIntPVector2D_0, arrayIntPVector2D_1);
                        scoreForThisCell = distScore * colorMatchScore;
                        //myLogger.debug("dist = " + dist);
                        //myLogger.debug("distScore = "+distScore + ", colorMatchScore = "+ colorMatchScore + ", scoreForThisCell = "+ scoreForThisCell);
                    }
                    sumOfScoresForAllCellsForThisPosition += scoreForThisCell;
                    sumOfMatchedPoints++;
                }
            }
        }
        staticMostRecentSumOfMatchedPoints = sumOfMatchedPoints;  //static is for debugging only.  Can remove later.
        //Sometimes sumOfScoresForAllCellsForThisPosition can be NaN, even thought sumOfMatchedPoints = 862???  Is sumOfMatchedPoints being taken from the cloud as it's larger than I'd expect.
        //if(xCount ==2) myLogger.debug("NAN???? sumOfMatchedPoints = "+sumOfMatchedPoints + " minPointsForACorrelationMatch = "+ minPointsForACorrelationMatch + " sumOfScoresForAllCellsForThisPosition = "+sumOfScoresForAllCellsForThisPosition);
        if (sumOfMatchedPoints > minPointsForACorrelationMatch)
            return (sumOfScoresForAllCellsForThisPosition / sumOfMatchedPoints);
        else
            return ThreeDImageCreator.veryHighFloat;  //Return very big number if no match.
    }

    //private drawLines(){
                            /*if (drawLines) {
                        float realWorldX = zPointPlane0.getRealWorldXForZMatrixX(zPointPlane0x);
                        float realWorldY = zPointPlane0.getRealWorldYForZMatrixY(zPointPlane0y);

                        if (compareColors) {
                            parent.stroke(0, 40, colorMatchScore);
                        } else {
                            if (realWorldZ0 > realWorldZ1) {
                                parent.stroke(0, 40, 200); //200//colorMatchScore //Brighter if cloud1 in front of cloud0.
                            } else {
                                parent.stroke(0, 40, 120); //200//colorMatchScore //Brighter if cloud1 in front of cloud0.
                            }
                        }
                        parent.line(realWorldX, realWorldY, realWorldZ0, realWorldX, realWorldY, realWorldZ1);

                    }*/
    //}
    /*private float getZDistBetweenTwoArrayPointsWithoutZCorrelationShift(ZPointPlane zPointPlane0, ZPointPlane zPointPlane1, int zPointPlane0x, int zPointPlane0y, int zPointPlane1x, int zPointPlane1y) {
        //TODO - waste of time - getRealWOrldZFor adds the same offset to both ponts, then we just take the difference between them?
        float realWorldZ0 = zPointPlane0.getCorrelationShiftedRealWorldZFor(zPointPlane0x, zPointPlane0y);
        float realWorldZ1 = zPointPlane1.getCorrelationShiftedRealWorldZFor(zPointPlane1x, zPointPlane1y) + cloud1.cumulativeCorrelationRealWorldShiftToPreviousCloudPVector.z;
        //if(xCount ==2) myLogger.debug("realWorldZ0 = "+realWorldZ0+ " realWorldZ1 = "+ realWorldZ1 + " other bit = "+ cloud1.cumulativeCorrelationRealWorldShiftToPreviousCloudPVector.z);
        if ((realWorldZ0 != ThreeDImageCreator.veryHighFloat) && (realWorldZ1 != ThreeDImageCreator.veryHighFloat))
            return realWorldZ1 - realWorldZ0;
        else
            return ThreeDImageCreator.veryHighFloat;
    }*/
    /*private float getZDistBetweenTwoArrayPointsWithoutZCorrelationShift(ZPointPlane zPointPlane0, ZPointPlane zPointPlane1, IntPVector arrayIntPVector2D_0, IntPVector arrayIntPVector2D_1) {
        //TODO - waste of time - getRealWOrldZFor adds the same offset to both ponts, then we just take the difference between them?
        float realWorldZ0 = zPointPlane0.getCorrelationShiftedRealWorldZFor(arrayIntPVector2D_0);
        myLogger.debug("getCorrelationShiftedRealWorldZFor(arrayIntPVector2D_0) "+ arrayIntPVector2D_0);
        float realWorldZ1 = zPointPlane1.getCorrelationShiftedRealWorldZFor(arrayIntPVector2D_1) + cloud1.cumulativeCorrelationRealWorldShiftToPreviousCloudPVector.z;
        myLogger.debug("getCorrelationShiftedRealWorldZFor(arrayIntPVector2D_1) "+ arrayIntPVector2D_1);
        //if(xCount ==2) myLogger.debug("realWorldZ0 = "+realWorldZ0+ " realWorldZ1 = "+ realWorldZ1 + " other bit = "+ cloud1.cumulativeCorrelationRealWorldShiftToPreviousCloudPVector.z);
        if ((realWorldZ0 != ThreeDImageCreator.veryHighFloat) && (realWorldZ1 != ThreeDImageCreator.veryHighFloat))
            return realWorldZ1 - realWorldZ0;
        else
            return ThreeDImageCreator.veryHighFloat;
    }*/
    private float getZDistBetweenTwoArrayPointsWithoutZCorrelationShift(ZPointPlane zPointPlane0, ZPointPlane zPointPlane1, IntPVector2D arrayIntPVector2D_0, IntPVector2D arrayIntPVector2D_1) {
        // The correlation offset is applied first.
        //IntPVector z0ArrayVector = getArrayPointForZMatrixPointIncludingCorrelationShift()
        float z0 = zPointPlane0.getzMatrix2d()[arrayIntPVector2D_0.intX][arrayIntPVector2D_0.intY]; // arrayIntPVector2D_0.intZ;
        float z1 = zPointPlane1.getzMatrix2d()[arrayIntPVector2D_1.intX][arrayIntPVector2D_1.intY];
        float invalidFloat = ThreeDImageCreator.veryHighFloat;

        if (z0== invalidFloat  || z1 ==  invalidFloat) return  invalidFloat;
        else return (z1- z0);
    }
    /*private float hueComparisonScoreForMultiplication(ZPointPlane zPointPlane0, ZPointPlane zPointPlane1, int cloud0x, int cloud0y, int cloud1x, int cloud1y) {
        int colorCloud0 = zPointPlane0.getZMatrixColorOrMinusFiveNinesFor(cloud0x, cloud0y);
        int colorCloud1 = zPointPlane1.getZMatrixColorOrMinusFiveNinesFor(cloud1x, cloud1y);
        float hue0 = zPointPlane0.getZMatrixHueForXYPosition(cloud0x, cloud0y);
        float hue1 = zPointPlane1.getZMatrixHueForXYPosition(cloud1x, cloud1y);
        if ((colorCloud0 != ThreeDImageCreator.invalidInt) && (colorCloud1 != ThreeDImageCreator.invalidInt))   //valid colors
            return getColorPartMatch(hue0, hue1);
        else return 1;
    }*/
    private float hueComparisonScoreForMultiplicationForArrayPoint(ZPointPlane zPointPlane0, ZPointPlane zPointPlane1, IntPVector2D arrayIntPVector2D_0, IntPVector2D arrayIntPVector2D_1) { //TODO - is this the right place in the matrix or do we need to offset?
        //Does the number include the correlation offset?HNG4FOR3
        int colorCloud0 = zPointPlane0.getzMatrixColors2d()[arrayIntPVector2D_0.intX][arrayIntPVector2D_0.intY];
        int colorCloud1 = zPointPlane1.getzMatrixColors2d()[arrayIntPVector2D_1.intX][arrayIntPVector2D_1.intY];
        float hue0 = zPointPlane0.getzMatrixHues2d()[arrayIntPVector2D_0.intX][arrayIntPVector2D_0.intY];
        float hue1 = zPointPlane1.getzMatrixHues2d()[arrayIntPVector2D_1.intX][arrayIntPVector2D_1.intY];
        if ((colorCloud0 != ThreeDImageCreator.invalidInt) && (colorCloud1 != ThreeDImageCreator.invalidInt))   //valid colors
            return getColorPartMatch(hue0, hue1);
        else return 1;
    }
    /*private float colorComparisonScoreForMultiplication(ZPointPlane zPointPlane0, ZPointPlane zPointPlane1, int cloud0x, int cloud0y, int cloud1x, int cloud1y){
        //For multiplication, return 1 if invalid color match. For addition, return 0.
        int colorCloud0 = zPointPlane0.getZMatrixColorOrMinusFiveNinesFor(cloud0x, cloud0y);
        int colorCloud1 = zPointPlane1.getZMatrixColorOrMinusFiveNinesFor(cloud1x, cloud1y);
        float hue0 = zPointPlane0.getZMatrixHueForXYPosition(cloud0x, cloud0y);
        float hue1 = zPointPlane1.getZMatrixHueForXYPosition(cloud1x, cloud1y);
        float saturation0 = zPointPlane0.getZMatrixSaturationForXYPosition(cloud0x, cloud0y);
        float saturation1 = zPointPlane1.getZMatrixSaturationForXYPosition(cloud1x, cloud1y);
        float brightness0 = zPointPlane0.getZMatrixBrightnessForXYPosition(cloud0x, cloud0y);
        float brightness1 = zPointPlane1.getZMatrixBrightnessForXYPosition(cloud1x, cloud1y);
        if ((colorCloud0 != ThreeDImageCreator.invalidInt) && (colorCloud1 != ThreeDImageCreator.invalidInt))   //valid colors
            return colorMatchScore(hue0, hue1, saturation0, saturation1, brightness0, brightness1);
        else return 1;
    }*/

    public float colorMatchScore(float hue1, float saturation1, float brightness1, float hue2, float saturation2, float brightness2) { //Can colour be represented in HSB format to avoid overhead of parent.hue etc?
        float hueDiff = getColorPartMatch(hue1, hue2); //Returns number 0-125
        float satDiff = getColorPartMatch(saturation1, saturation2); //Returns number 0-125
        float brightDiff = getColorPartMatch(brightness1, brightness2); //Returns number 0-125
        return 1 + (hueDiff + satDiff + brightDiff);  //TODO - CONVERT THIS TO MULTIPLICATION AND TEST.
    }

    public float getColorPartMatch(float colorPart1, float colorPart2) {
        float diff = colorPart1 - colorPart2;
        float absDiff = Math.abs(diff);
        if (absDiff > 127) {
            absDiff = 255 - absDiff;
        }
        return absDiff;
    }
    //public void startCorrelate(boolean thisIsTheLastOne, String complexity){}//implemented in subclass
    public boolean stepCorrelateReturnTrueWhenDone(){  //implemented in one subclass. Should never get here.
        myLogger.error("in stepCorrelateReturnTrueWhenDone in superclass. Shouldnt get here. Should cover in subclass.");
        return true;
    }

    //this.setUpInitialCorrelationVariables(4, 1, 'intY', 0.1f, 8, 17, defaultMinScoreForGoodMatch, true);
    //this.setUpInitialCorrelationVariables(1, 3, 'intY', 0.05f, 5, 11, defaultMinScoreForGoodMatch, true);
    //int mySlideZMatrixPointIncrement, int mySlidePositionsEachSide, char myRotateAxis, float myRotateAngleIncrement, int myRotateAnglePositionsLeft, int myRotateAnglePositionsTotal, float myMinScoreForGoodMatch, boolean myCorrelateColors
    public void setUpInputParamsVariables(){
        totalCorrelationSetsToRunForThisPair = myCorrelatorZInputParamsArrayList.size();
        currentCorrelationSetForThisPair = 0;
    };

    /*The next bit just moved up from subclass, 17 July 2015*/

    public void prepareForNextSetOfCorrelations(){
        myLogger.debug("prepareForNextSetofCorrelations, currentCorrelationSetForThisPair = " + currentCorrelationSetForThisPair);
        setUpInitialCorrelationVariablesFromObject(myCorrelatorZInputParamsArrayList.get(currentCorrelationSetForThisPair));
        clearChart();  //Calls method in superclass so we can use this in the fast correlator too.
        setUpZPointPlanes(); //New 15 July 2015
    }

    public void clearChart(){
        myGraphOriginalPVectorArrayList.clear();  //Clear out the chart as about to start another set of correlations
        myGraphChartScaledPVectorArrayList.clear();
    }

    private void addPointToChart(float correlationScore){
        PVector unadjustedPVector = new PVector(myGraphOriginalPVectorArrayList.size(), correlationScore);
        myGraphOriginalPVectorArrayList.add(unadjustedPVector);  //Clear out the chart as about to start another set of correlations
        myGraphChartScaledPVectorArrayList.add(convertToChartPVector(unadjustedPVector));
    }
    private PVector convertToChartPVector(PVector inputPoint){
        int chartWidth = parent.getGraphPGraphicsWidth();
        int chartHeight = parent.getGraphPGraphicsHeight();
        //myLogger.debug("inputPoint = "+inputPoint+" myGraphMaxX = "+myGraphMaxX+"chartWidth = "+chartWidth);

        float chartX = (inputPoint.x/myGraphMaxX)*chartWidth;
        float logOutputY = PApplet.log(inputPoint.y); //todo use Javas math log funtion?
        float chartY = ((logOutputY/myGraphMaxY)*chartHeight);
        if (chartY>chartHeight) chartY = chartHeight;
        //Note the origin is in the top left, so intY coordinate needs to be inverted.
        return new PVector(chartX, chartHeight- chartY);
    }

    public void drawChart(PGraphics myPGraphics){
        if (myGraphChartScaledPVectorArrayList.isEmpty()) {}
        else {
            if (myGraphChartScaledPVectorArrayList.size() == 1) {}
            else{
                myPGraphics.beginDraw();
                myPGraphics.clear();
                myPGraphics.colorMode(PApplet.HSB, 255, 255, 255);
                myPGraphics.background(200,200, 0,0);
                myPGraphics.stroke(200,0,255);
                PVector prevScaledPVector, currentScaledPVector;
                prevScaledPVector = myGraphChartScaledPVectorArrayList.get(0);

                for(int x = 1; x< myGraphChartScaledPVectorArrayList.size(); x++){
                    //myLogger.debug("Drawing graph, intX = "+intX);
                    currentScaledPVector = myGraphChartScaledPVectorArrayList.get(x);
                    PVector originalPVector = myGraphOriginalPVectorArrayList.get(x); //TODO remove this later
                    //myLogger.debug("Original point = " + originalPVector + " Chart point = " + currentScaledPVector);
                    myPGraphics.line(prevScaledPVector.x, prevScaledPVector.y, currentScaledPVector.x, currentScaledPVector.y);
                    prevScaledPVector = currentScaledPVector;
                }
                 myPGraphics.endDraw();
            }
        }
    }
    private int getTextYForZeroBasedRow(int rowNoZeroBased, int fontHeight){
         int fontGap = fontHeight/6;
        return (fontHeight + fontGap) * (1+rowNoZeroBased);
    }
    private int getTextX(){
        return 10;
    }

    public void drawText(PGraphics textPGraphics, PFont textBoxFont){
        //myLogger.debug("IN DRAWTEXT!!!!");
        int fontHeight = 20;
        String[] myStrings = new String[27];
        //TODO - add code to pad the length
        myStrings[0]  = "overallMinScore = " + overallMinScore;
        myStrings[1]  = "corScore = " + correlationScore;
        myStrings[2]  = "minForMat= " + minScoreForGoodMatch;
        myStrings[3]  = "=========================";
        myStrings[4]  = "offset   = " + currentCorrelationOffsetIntPVector3D;
        myStrings[5]  = "angleCnt = " + angleCounter;
        myStrings[6]  = "pointSize =" + zPointSizePVector;
        myStrings[7]  = "Frame =    " + frame1.getFrameNo();

        myStrings[8]  = "==========================";
        myStrings[9]  = "min pos  = " + overallLowestScoreZShiftIntPVector3D;
        myStrings[10] = "angMinScr= " + anglePositionOfMinScore;
        myStrings[11] = "";
        myStrings[12] = "";
        myStrings[13] = "==========================";
        myStrings[14] = ""; //"noCtShft = " + noOfCountShifts;
        myStrings[15] = "rPosTotal= " + rotateAnglePositionsTotal;
        myStrings[16] = "rAngIncr = " + rotateAngleIncrement;
        myStrings[17] = "rotPosLft= " + rotateAnglePositionsLeft;
        myStrings[18] = "rotateAx = " + rotateAxis;
        myStrings[19] = "totAngShf= " + totalAngleShift;
        myStrings[20] = "==========================";
        myStrings[21] = "zPtIncr  = " + slideZMatrixPointIncrement;
        myStrings[22] = "slidePos = " + slidePositionsEachSideIntPVector3D;
        myStrings[23] = "corCols  = " + correlateColors;
        myStrings[24]  = "start pos= " + minCorrelationOffsetIntPVector3D;
        myStrings[25]  = "end pos  = " + maxCorrelationOffsetIntPVector3D;

        textPGraphics.beginDraw();
        textPGraphics.clear();
        textPGraphics.colorMode(PApplet.HSB, 255, 255, 255, 255);
        textPGraphics.background(0, 0, 0,0);   //Final zero makes it transparent
        textPGraphics.textFont(textBoxFont, fontHeight);
        textPGraphics.stroke(50,50,50);
        for(int i=0;i<myStrings.length; i++){
            if (!(myStrings[i]==null))
            textPGraphics.text(myStrings[i], getTextX(), getTextYForZeroBasedRow(i, fontHeight ), 0);
        }

        textPGraphics.endDraw();
    }

    public void shiftSecondZMatrixByIntPVector(IntPVector2D shiftIntPVector2D){
        currentCorrelationOffsetIntPVector3D.add(shiftIntPVector2D);  //Neat
    }

    public PVector getZPointSizePVector(){
        return zPointSizePVector;
    }
    /*    float overallMinScore, correlationScore;
    int xCount, yCount, zCount, angleCounter;
    int anglePositionOfMinScore;
    float totalAngleShift;
    int xStartShiftzMatrixPoints, yStartShiftzMatrixPoints, zStartShiftzMatrixPoints;
    int noOfCountShifts;
    int xCountOfMinScore, yCountOfMinScore, zCountOfMinScore;
    int rotateAnglePositionsTotal;
    float rotateAngleIncrementRadians;
    int slideZMatrixPointIncrement;
    int slidePositionsEachSide;
    char rotateAxis;
    int defaultMinScoreForGoodMatch;  //not really used
    float minScoreForGoodMatch;
    boolean correlateColors;
    long correlationStartTime;
    int rotateAnglePositionsLeft;*/
}
