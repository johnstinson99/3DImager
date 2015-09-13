package imager.domain.Correlators;

import imager.domain.ThreeDObjects.IntPVector3D;
import processing.core.PVector;

import java.util.ArrayList;

/**
 * Created by John on 13/09/2015.
 */
public class CollectionOfCorrelationParams {

    public static ArrayList<CorrelatorZInputParams> getCollection(){
        int defaultMinScoreForGoodMatch = 1000;
        ArrayList<CorrelatorZInputParams> myCorrelatorZInputParamsArrayList;
        myCorrelatorZInputParamsArrayList = new ArrayList<CorrelatorZInputParams>();
        // myCorrelatorZInputParamsArrayList.add(new CorrelatorZInputParams(new PVector(10f, 10f, 10f), new IntPVector3D(1,1,4), 'y', 10f, 1, 3, defaultMinScoreForGoodMatch, true)); //int mySlideZMatrixPointIncrement, int mySlidePositionsEachSide, char myRotateAxis, float myRotateAngleIncrement, int myRotateAnglePositionsLeft, int myRotateAnglePositionsTotal, float myMinScoreForGoodMatch, boolean myCorrelateColors
        //Params are (PVector myZPointSizePVector, IntPVector3D mySlidePositionsEachSideIntPVector3D, char myRotateAxis, float myRotateAngleIncrementDegrees, int myRotateAnglePositionsLeft, int myRotateAnglePositionsTotal, float myMinScoreForGoodMatch, boolean myCorrelateColors){
        myCorrelatorZInputParamsArrayList.add(new CorrelatorZInputParams(
                new PVector(10f, 10f, 3f), new IntPVector3D(3,3,3), 'y', 10f, 9, 19, defaultMinScoreForGoodMatch, true)); //int mySlideZMatrixPointIncrement, int mySlidePositionsEachSide, char myRotateAxis, float myRotateAngleIncrement, int myRotateAnglePositionsLeft, int myRotateAnglePositionsTotal, float myMinScoreForGoodMatch, boolean myCorrelateColors
        myCorrelatorZInputParamsArrayList.add(new CorrelatorZInputParams(
                new PVector(8f, 8f, 3f), new IntPVector3D(3,3,3), 'y', 5f, 1, 3, defaultMinScoreForGoodMatch, true)); //int mySlideZMatrixPointIncrement, int mySlidePositionsEachSide, char myRotateAxis, float myRotateAngleIncrement, int myRotateAnglePositionsLeft, int myRotateAnglePositionsTotal, float myMinScoreForGoodMatch, boolean myCorrelateColors
        myCorrelatorZInputParamsArrayList.add(new CorrelatorZInputParams(
                new PVector(5f, 5f, 2f), new IntPVector3D(1,1,2), 'y', 2f, 2, 3, defaultMinScoreForGoodMatch, true)); //int mySlideZMatrixPointIncrement, int mySlidePositionsEachSide, char myRotateAxis, float myRotateAngleIncrement, int myRotateAnglePositionsLeft, int myRotateAnglePositionsTotal, float myMinScoreForGoodMatch, boolean myCorrelateColors
        myCorrelatorZInputParamsArrayList.add(new CorrelatorZInputParams(
                new PVector(3f, 3f, 2f), new IntPVector3D(1,1,2), 'y', 1f, 1, 3, defaultMinScoreForGoodMatch, true)); //int mySlideZMatrixPointIncrement, int mySlidePositionsEachSide, char myRotateAxis, float myRotateAngleIncrement, int myRotateAnglePositionsLeft, int myRotateAnglePositionsTotal, float myMinScoreForGoodMatch, boolean myCorrelateColors
        //myCorrelatorZInputParamsArrayList.add(new CorrelatorZInputParams(new PVector(2f, 2f, 2f), new IntPVector3D(1,1,2), 'y', 0.5f, 1, 3, defaultMinScoreForGoodMatch, true)); //int mySlideZMatrixPointIncrement, int mySlidePositionsEachSide, char myRotateAxis, float myRotateAngleIncrement, int myRotateAnglePositionsLeft, int myRotateAnglePositionsTotal, float myMinScoreForGoodMatch, boolean myCorrelateColors
        // myCorrelatorZInputParamsArrayList.add(new CorrelatorZInputParams(new PVector(1.5f, 1.5f, 1f), new IntPVector3D(1,1,2), 'y', 0.3f, 1, 3, defaultMinScoreForGoodMatch, true)); //int mySlideZMatrixPointIncrement, int mySlidePositionsEachSide, char myRotateAxis, float myRotateAngleIncrement, int myRotateAnglePositionsLeft, int myRotateAnglePositionsTotal, float myMinScoreForGoodMatch, boolean myCorrelateColors
        return myCorrelatorZInputParamsArrayList;
    }
}
