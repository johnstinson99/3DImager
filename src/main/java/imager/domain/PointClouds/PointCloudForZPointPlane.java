package imager.domain.PointClouds;

import imager.domain.Correlators.CorrelatorForTwoPointClouds;
import imager.domain.ThreeDObjects.FloatColoredPVector;
import processing.core.PGraphics;
import processing.core.PVector;

/**
 * Created by John on 24/05/2015.
 *
 */
public class PointCloudForZPointPlane extends PointCloud {
    CorrelatorForTwoPointClouds correlatorForTwoPointClouds;

    ZPointPlane zPointPlane;
    PVector cumulativeCorrelationRealWorldRotateToPreviousCloudPVector = new PVector(0f, 0f, 0f);
    PVector cumulativeCorrelationRealWorldShiftToPreviousCloudPVector = new PVector(0f, 0f, 0f);

    public PointCloudForZPointPlane(Frame myParentFrame){ //Call parent's constructor. This is required. It can't just be implied.
        super(myParentFrame);
        correlatorForTwoPointClouds = getParentThreeDImageCreator().getCorrelatorForTwoPointClouds(); //TODO - is this right? All PointClouds point to same correlator. In which case why not just leave correlator associated with the ThreeDImage
    }

    public void printAnglesAndOffsets() {
        getMyLogger().debug("cumulativeCorrelationRealWorldRotateToNextCloud = " + cumulativeCorrelationRealWorldRotateToPreviousCloudPVector + " cumulativeCorrelationRealWorldShiftToNextCloud = " + cumulativeCorrelationRealWorldShiftToPreviousCloudPVector);
    }

    public CorrelatorForTwoPointClouds getCorrelatorForTwoPointClouds() {
        return correlatorForTwoPointClouds;
    }

    public ZPointPlane getzPointPlane() {
        return zPointPlane;
    }

    public PVector getCumulativeCorrelationRealWorldRotateToPreviousCloudPVector() {
        return cumulativeCorrelationRealWorldRotateToPreviousCloudPVector;
    }

    public PVector getCumulativeCorrelationRealWorldShiftToPreviousCloudPVector() {
        return cumulativeCorrelationRealWorldShiftToPreviousCloudPVector;
    }

    public void resetCumulativeCorrelationOffsetsAndCorrelationCloud() {
        cumulativeCorrelationRealWorldRotateToPreviousCloudPVector.set(0f, 0f, 0f);
        cumulativeCorrelationRealWorldShiftToPreviousCloudPVector.set(0f, 0f, 0f);
        getParentFrame().resetCorrelationCloudFromTopOfHeadCloud();
    }
    public void saveCumulativeCorrelationOffsetsAsFinal() {
        //Both the correlator and final image need these final rotates and shifts, so put them up at the frame level.
        getParentFrame().getFinalCorrelationRealWorldRotateToPreviousCloud().set(cumulativeCorrelationRealWorldRotateToPreviousCloudPVector.x, cumulativeCorrelationRealWorldRotateToPreviousCloudPVector.y, cumulativeCorrelationRealWorldRotateToPreviousCloudPVector.z);
        getParentFrame().getFinalCorrelationRealWorldRotateToPreviousCloud().set(cumulativeCorrelationRealWorldShiftToPreviousCloudPVector.x, cumulativeCorrelationRealWorldShiftToPreviousCloudPVector.y, cumulativeCorrelationRealWorldShiftToPreviousCloudPVector.z);
    }

    public ZPointPlane rotateCloudAndSetupZPointPlane(float beta, char myAxis) {
        rotateCloudAndSetPointCloudExtentsWithoutSettingUpZPointPlane(beta, myAxis);
        //this.zPointPlane = setUpZPointPlane();
        this.zPointPlane = setUpZPointPlaneAndMapCorrelator(correlatorForTwoPointClouds);
        return this.zPointPlane;
    }

    public void rotateCloudAndSetPointCloudExtentsWithoutSettingUpZPointPlane(float beta, char myAxis){  //Public as just want to call this without setting up zPointPlane at end of correlation.
        //TODO - surely this should be new copy of a cloud rather than rotating the original cloud?
        //TODO - if we had a number of copies, we could draw them with different brightnesses indicating strength of match?
        //TODO - Would it be more efficient here to do matrix multiplication, applying a rotation matrix?
        for (FloatColoredPVector myVector : getOneAndOnlyArrayList()) {
            if(Float.isNaN(myVector.x)) getMyLogger().error("foooound NaN before rotateCloudAndSetupZPointPlane");
            myVector.rotate3dRadians(beta, myAxis); //rotatePoint(myVector, beta, myAxis);
            if(Float.isNaN(myVector.x)) getMyLogger().error("foooound NaN after rotateCloudAndSetupZPointPlane");
            checkExtentsForPVector(myVector);  //Checking extents here saves doing a full pass later. //TODO- just rotate the two extents rather than checking every single point.
        }
        switch (myAxis) {
            case 'x':
                cumulativeCorrelationRealWorldRotateToPreviousCloudPVector.x += beta;
                break;
            case 'y':
                cumulativeCorrelationRealWorldRotateToPreviousCloudPVector.y += beta;
                break;
            case 'z':
                cumulativeCorrelationRealWorldRotateToPreviousCloudPVector.z += beta;
                break;
        }
    }
    public ZPointPlane setUpZPointPlaneAndMapCorrelator(CorrelatorForTwoPointClouds myCorrelator){
        //Set correlator to null if one doesn't yet exist and defaults will apply for zPointPlane.
        zPointPlane = new ZPointPlane(this, getParentThreeDImageCreator(), myCorrelator);
        zPointPlane.setCorrelatorForTwoPointClouds(myCorrelator);
        zPointPlane.setUpZMatrixCommon();  //New
        return zPointPlane;

        /*ZPointPlane myZPointPlane = setUpZPointPlane();
        myZPointPlane.correlatorForTwoPointClouds = myCorrelator;
        return myZPointPlane;*/
    }

    /*public ZPointPlane setUpZPointPlane(){
        //Call this when no correlator. ZPointPlane will have default size.
        zPointPlane = new ZPointPlane(this, parentThreeDImageCreator);
        zPointPlane.setUpZMatrixCommon();  //New
        return zPointPlane;
    }*/

    public void drawShifted(PGraphics myPGraphics, boolean useRealColors){
        setUpPGraphics(myPGraphics);
        for (FloatColoredPVector myFloatColoredPVector : getOneAndOnlyArrayList()) {
            if (useRealColors) {
                myPGraphics.stroke(myFloatColoredPVector.getColor());
            }
            PVector shiftedPVector = PVector.add(myFloatColoredPVector, cumulativeCorrelationRealWorldShiftToPreviousCloudPVector);
            //drawColoredPVector(myPGraphics, myColoredPVector, true);  //It cant draw itself as it needs to know its parent.
            myPGraphics.point(shiftedPVector.x, shiftedPVector.y, shiftedPVector.z);
        }
    }
    public void shiftAllPointsByPVector(PVector myShiftPVector){
        initialiseBoundingRectangle();
        for(FloatColoredPVector myFloatColoredPVector : getOneAndOnlyArrayList()){
            myFloatColoredPVector.add(myShiftPVector);
            checkExtentsForPVector(myFloatColoredPVector);
        }

    }
}
