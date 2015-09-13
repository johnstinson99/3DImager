package imager.domain.Correlators;

import imager.domain.ThreeDObjects.IntPVector3D;
import processing.core.PVector;

/**
 * Created by John on 25/04/2015.
 */
public class CorrelatorZInputParams {
    PVector zPointSizePVector;
    int slideZMatrixPointIncrement;
    IntPVector3D slidePositionsEachSideIntPVector3D;
    char rotateAxis;
    float rotateAngleIncrementRadians;
    int rotateAnglePositionsLeft;
    int rotateAnglePositionsTotal;
    float minScoreForGoodMatch;
    boolean correlateColors;

    public CorrelatorZInputParams(PVector myZPointSizePVector, /*int mySlideZMatrixPointIncrement,*/ IntPVector3D mySlidePositionsEachSideIntPVector3D, char myRotateAxis, float myRotateAngleIncrementDegrees, int myRotateAnglePositionsLeft, int myRotateAnglePositionsTotal, float myMinScoreForGoodMatch, boolean myCorrelateColors){
        zPointSizePVector = myZPointSizePVector;
        /*slideZMatrixPointIncrement = mySlideZMatrixPointIncrement;*/
        slidePositionsEachSideIntPVector3D = mySlidePositionsEachSideIntPVector3D;
        rotateAxis = myRotateAxis;
        rotateAngleIncrementRadians = (float)Math.toRadians(myRotateAngleIncrementDegrees);
        rotateAnglePositionsLeft = myRotateAnglePositionsLeft;
        rotateAnglePositionsTotal = myRotateAnglePositionsTotal;
        minScoreForGoodMatch = myMinScoreForGoodMatch;
        correlateColors = myCorrelateColors;
    }

    public PVector getzPointSizeSizePVector() {return zPointSizePVector;}

    public int getSlideZMatrixPointIncrement() {
        return slideZMatrixPointIncrement;
    }

    public IntPVector3D getSlidePositionsEachSide() {
        return slidePositionsEachSideIntPVector3D;
    }

    public char getRotateAxis() {
        return rotateAxis;
    }

    public float getRotateAngleIncrementRadians() {
        return rotateAngleIncrementRadians;
    }

    public int getRotateAnglePositionsLeft() {
        return rotateAnglePositionsLeft;
    }

    public int getRotateAnglePositionsTotal() {
        return rotateAnglePositionsTotal;
    }

    public float getMinScoreForGoodMatch() {
        return minScoreForGoodMatch;
    }

    public boolean getCorrelateColors() {
        return correlateColors;
    }


}
