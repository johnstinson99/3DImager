package junit.Transforms;

import imager.domain.ThreeDObjects.FloatColoredPVector;
import imager.domain.Transforms.Rotation;
import imager.domain.Transforms.SeriesOfTransforms;
import junit.framework.TestCase;
import static org.assertj.core.api.Assertions.*;
/**
 * Created by John on 03/09/2015.
 */
public class TestSeriesOfTransformsPositiveAndNegativeScenarios extends TestCase {

    public void testTransformListWithTwoItems(){
        SeriesOfTransforms myTransformList = new SeriesOfTransforms();
        Rotation rotation1 = new Rotation(Math.toRadians(90),'x');
        Rotation rotation2 = new Rotation(Math.toRadians(90), 'y');
        myTransformList.add(rotation1);
        myTransformList.add(rotation2);
         FloatColoredPVector myColoredPVector = new FloatColoredPVector(10,10,10);
        //FloatColoredPVector myColoredPVectorAfter = myColoredPVectorBefore.createCopy();
        myTransformList.applyTo(myColoredPVector);
        assertThat(myColoredPVector).isEqualToComparingFieldByField(new FloatColoredPVector(10,-10,-10));
        /*assertThat(myColoredPVector.x).isEqualTo(10);
        assertThat(myColoredPVector.y).isEqualTo(-10);
        assertThat(myColoredPVector.z).isEqualTo(-10);*/
    }
    public void testTransformListWithOneItem(){
        SeriesOfTransforms myTransformList = new SeriesOfTransforms();
        Rotation rotation1 = new Rotation(Math.toRadians(90),'x');
        myTransformList.add(rotation1);
        FloatColoredPVector myColoredPVector = new FloatColoredPVector(10,10,10);
        //FloatColoredPVector myColoredPVectorAfter = myColoredPVectorBefore.createCopy();
        myTransformList.applyTo(myColoredPVector);
        assertThat(myColoredPVector).isEqualToComparingFieldByField(new FloatColoredPVector(10, -10, 10));
        /*assertThat(myColoredPVector.x).isEqualTo(10);
        assertThat(myColoredPVector.y).isEqualTo(-10);
        assertThat(myColoredPVector.z).isEqualTo(10);*/
    }
    public void testTransformListWithNoItems(){
        SeriesOfTransforms myTransformList = new SeriesOfTransforms();
        FloatColoredPVector myColoredPVector = new FloatColoredPVector(10,10,10);
        //FloatColoredPVector myColoredPVectorAfter = myColoredPVectorBefore.createCopy();
        myTransformList.applyTo(myColoredPVector);
        assertThat(myColoredPVector).isEqualToComparingFieldByField(new FloatColoredPVector(10, 10, 10));
    }
}
