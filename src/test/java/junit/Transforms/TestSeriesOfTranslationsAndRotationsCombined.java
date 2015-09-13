package junit.Transforms;

import imager.domain.ThreeDObjects.FloatColoredPVector;
import imager.domain.Transforms.*;
import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by John on 03/09/2015.
 */
public class TestSeriesOfTranslationsAndRotationsCombined extends TestCase {

    public void testTransformListRotationThenTranslation(){
        System.out.println("RotationThenTranslation");
        SeriesOfTransforms myTransformList = new SeriesOfTransforms();
        Rotation rotation1 = new Rotation(Math.toRadians(90),'x');
        Translation translation1 = new Translation(2f,3f,4f);
        //Rotation rotation2 = new Rotation(Math.toRadians(90), 'y');
        myTransformList.add(rotation1);
        myTransformList.add(translation1);
        //myTransformList.add(rotation2);
        FloatColoredPVector myColoredPVector = new FloatColoredPVector(10,10,10);
        System.out.println("My Transform list before combination= "+myTransformList);
        EuclidianTransform myTransform = myTransformList.getCombinedTransform();
        System.out.println("Transform after combining= "+myTransform);
        myTransform.applyTransformTo(myColoredPVector);
        assertThat(myColoredPVector).isEqualToComparingFieldByField(new FloatColoredPVector(12, -7, 14));
    }
    public void testTransformListTranslationThenRotation(){
        System.out.println("RotationThenTranslation");
        SeriesOfTransforms myTransformList = new SeriesOfTransforms();
        Translation translation1 = new Translation(2f,3f,4f);
        Rotation rotation1 = new Rotation(Math.toRadians(90),'x');
        //Rotation rotation2 = new Rotation(Math.toRadians(90), 'y');
        myTransformList.add(translation1);
        myTransformList.add(rotation1);
        //myTransformList.add(rotation2);
        FloatColoredPVector myColoredPVector = new FloatColoredPVector(10,10,10);
        System.out.println("My Transform list before combination= "+myTransformList);
        EuclidianTransform myTransform = myTransformList.getCombinedTransform();
        System.out.println("Transform after combining= "+myTransform);
        myTransform.applyTransformTo(myColoredPVector);
        assertThat(myColoredPVector).isEqualToComparingFieldByField(new FloatColoredPVector(12, -14, 13));
    }
}
