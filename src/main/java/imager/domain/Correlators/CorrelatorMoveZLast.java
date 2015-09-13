package imager.domain.Correlators;

import imager.domain.ThreeDImageCreator;
import imager.domain.ThreeDObjects.IntPVector3D;

/**
 * Created by John on 17/07/2015.
 */
public class CorrelatorMoveZLast extends CorrelatorForTwoPointClouds{

    int correlationPhase = 0;

    public CorrelatorMoveZLast(ThreeDImageCreator myThreeDImageCreator) {
        super(myThreeDImageCreator);
    }

    public void setUpGraphMaxX(IntPVector3D totalSlidePositionsIntPVector, int myRotateAnglePositionsTotal){ //Called at start of full correlation
        //myLogger.debug("*** totalSlidePositions = "+totalSlidePositions+ " myRotateAnglePositionsTotal = "+ myRotateAnglePositionsTotal+ " ***");
        myGraphMaxX = (totalSlidePositionsIntPVector.intX*totalSlidePositionsIntPVector.intY + (totalSlidePositionsIntPVector.intZ - 1))* myRotateAnglePositionsTotal;
        getMyLogger().info("myGraphMaxX = " + myGraphMaxX);
    }

    public void setStartCurrentAndEndOffsets(IntPVector3D mySlidePositionsEachSideIntPVector3D){  //Called at start of full correlation
        minCorrelationOffsetIntPVector3D = new IntPVector3D(-mySlidePositionsEachSideIntPVector3D.intX, -mySlidePositionsEachSideIntPVector3D.intY, 0);
        currentCorrelationOffsetIntPVector3D = minCorrelationOffsetIntPVector3D.createCopy();
        maxCorrelationOffsetIntPVector3D = new IntPVector3D(mySlidePositionsEachSideIntPVector3D.intX, mySlidePositionsEachSideIntPVector3D.intY, 0);
        getMyLogger().info("minCorrelationOffsetIntPVector3D = " + minCorrelationOffsetIntPVector3D);
    }

    public void setUpZPointPlanes(){ //Called on every rotate
        setInterimMinScore(ThreeDImageCreator.veryHighFloat);
        interimLowestScoreZShiftIntPVector3D = new IntPVector3D(0,0,0);
        //correlationPhase = 0;
        super.setUpZPointPlanes();
    }

    public boolean stepCorrelateReturnTrueWhenDone(){
        getMyLogger().debug("=============== 1 pressed ================================");
        getMyLogger().debug("About to correlate for these two positions:");
        getMyLogger().debug("zPointPlane0 = \n" + getzPointPlane0());
        getMyLogger().debug("zPointPlane1 = \n" + getzPointPlane1());
        this.setSecondZMatrixOffsetTo(currentCorrelationOffsetIntPVector3D);  //New
        getMyLogger().debug("cccorrelation offset of zPointPLane0 = " + getzPointPlane0().getCorrelationOffsetIntPVector3D());
        getMyLogger().debug("cccorrelation offset of zPointPLane1 = " + getzPointPlane1().getCorrelationOffsetIntPVector3D());
        doActualCorrelation();  //do this every pass unless angle has cycled.
        switch (correlationPhase) {
            case 0:  //0 means move X and Y around.  Could alter this to a minimum hunting algo.
                incrementCorrelationPositionTowardsMax(currentCorrelationOffsetIntPVector3D, maxCorrelationOffsetIntPVector3D, minCorrelationOffsetIntPVector3D);
                if (currentCorrelationOffsetIntPVector3D.equals(maxCorrelationOffsetIntPVector3D)) {
                    currentCorrelationOffsetIntPVector3D = interimLowestScoreZShiftIntPVector3D.createCopy();  //Not min!! Min is the lowest x and y, interimLowestScore is the position with the lowest score.
                    correlationPhase = 1;
                }
                break;
            case 1:  //1 means move z in negative direction
                currentCorrelationOffsetIntPVector3D.intZ --;
                if(currentCorrelationOffsetIntPVector3D.intZ <= -slidePositionsEachSideIntPVector3D.intZ){  //<= in case slide Z = 0
                    correlationPhase = 2;
                    currentCorrelationOffsetIntPVector3D.intZ = 1;
                }
                break;
            case 2:  //2 means move z in positive direction
                currentCorrelationOffsetIntPVector3D.intZ ++;
                if(currentCorrelationOffsetIntPVector3D.intZ >= slidePositionsEachSideIntPVector3D.intZ){
                    correlationPhase = 3;  //3 means everything finished and ready to reset position and rotate.
                }
                break;
            case 3: //3 means finished and ready to rotate
                correlationPhase = 0;
                if(moveToOverallMinPositionAndRotateReturnTrueWhenFinished()) {
                    return true;  //returns true if everything done.
                }
        }

        if(getParent().isDrawEachStep()) {
            return false; //Returning false here means there's still more to do on the next pass. //Only return true earlier if we've completed all the correlation sets.
        } else return stepCorrelateReturnTrueWhenDone();

    }

    private boolean moveToOverallMinPositionAndRotateReturnTrueWhenFinished(){  //Does rotating actually lose the min position?
        minCorrelationOffsetIntPVector3D.copyValuesTo(currentCorrelationOffsetIntPVector3D);
        //angleCounter++;
        setAngleCounter(getAngleCounter()+1);
        if(getAngleCounter() < rotateAnglePositionsTotal) {  //Important - only rotate if we're not on the last one.
            rotateSecondCloud(rotateAngleIncrement, rotateAxis, true);
            //totalAngleShift += rotateAngleIncrement;
            setTotalAngleShift(totalAngleShift+rotateAngleIncrement);
        }
        if (getAngleCounter() == rotateAnglePositionsTotal) { //if angle cycles, WE'RE DONE return result and reset. //TODO - should this be > rather than == like the others?
            //WE'RE DONE - RESET EVERYTHING
            setAngleCounter(0);
            currentCorrelationSetForThisPair++;
            boolean gotAMatch = resetImagesAfterCorrelationAndReturnResult();  //THIS ONE SHOULD SHIFT BACK TO FINAL POSITION.
            //TODO - do something with gotAMatch
            if(currentCorrelationSetForThisPair == totalCorrelationSetsToRunForThisPair){  //Test if we've really finished ALL correlations
                currentCorrelationSetForThisPair = 0;
                return (true);
            } else super.prepareForNextSetOfCorrelations();  //Prepare for next correlation sets up the shift box size and rotation params for next set of correlations
            getMyLogger().info("Should return image to min position at this point, before resetting variables for next correlation run");
            getMyLogger().info("gotAMatch = " + gotAMatch);
        }
        return false;
    }
}
