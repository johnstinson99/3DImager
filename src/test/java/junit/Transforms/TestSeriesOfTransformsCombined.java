package junit.Transforms;

import imager.domain.ThreeDObjects.FloatColoredPVector;
import imager.domain.Transforms.EuclidianTransform;
import imager.domain.Transforms.Rotation;
import imager.domain.Transforms.SeriesOfTransforms;
import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by John on 03/09/2015.
 */
public class TestSeriesOfTransformsCombined extends TestCase {

    public void testTransformListWithTwoElements(){
        System.out.println("Test with two elements");
        SeriesOfTransforms myTransformList = new SeriesOfTransforms();
        Rotation rotation1 = new Rotation(Math.toRadians(90),'x');
        Rotation rotation2 = new Rotation(Math.toRadians(90), 'y');
        myTransformList.add(rotation1);
        myTransformList.add(rotation2);
        FloatColoredPVector myColoredPVector = new FloatColoredPVector(10,10,10);
        System.out.println("My Transform list before combination= "+myTransformList);
        EuclidianTransform myTransform = myTransformList.getCombinedTransform();
        System.out.println("Transform after combining= "+myTransform);
        myTransform.applyTransformTo(myColoredPVector);
        assertThat(myColoredPVector).isEqualToComparingFieldByField(new FloatColoredPVector(10, -10, -10));
        /*assertThat(myColoredPVectorAfter.x).isEqualTo(10);
        assertThat(myColoredPVectorAfter.y).isEqualTo(-10);
        assertThat(myColoredPVectorAfter.z).isEqualTo(-10);*/
    }
    public void testTransformListWithOneElement(){
        System.out.println("Test with one element");
        SeriesOfTransforms myTransformList = new SeriesOfTransforms();
        Rotation rotation1 = new Rotation(Math.toRadians(90),'x');
        System.out.println("rotation1 = " + rotation1);
        myTransformList.add(rotation1);
        FloatColoredPVector myColoredPVector = new FloatColoredPVector(10,10,10);
        System.out.println("My Transform list before combination= "+myTransformList);
        EuclidianTransform myTransform = myTransformList.getCombinedTransform();
        System.out.println("Transform after combining= "+myTransform);
        myTransform.applyTransformTo(myColoredPVector);
        assertThat(myColoredPVector).isEqualToComparingFieldByField(new FloatColoredPVector(10, -10, 10));
    }
    public void testTransformListWithNolements(){
        System.out.println("Test with no elements");
        SeriesOfTransforms myTransformList = new SeriesOfTransforms();
        FloatColoredPVector myColoredPVector = new FloatColoredPVector(10,10,10);
        System.out.println("My Transform list before combination= "+myTransformList);
        EuclidianTransform myTransform = myTransformList.getCombinedTransform();
        System.out.println("Transform after combining= "+myTransform);
        myTransform.applyTransformTo(myColoredPVector);
        assertThat(myColoredPVector).isEqualToComparingFieldByField(new FloatColoredPVector(10, 10, 10));
    }
}
