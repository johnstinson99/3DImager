package junit.Transforms;

import imager.domain.ThreeDObjects.FloatColoredPVector;
import imager.domain.Transforms.Rotation;
import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by John on 04/09/2015.
 */
public class TestRotation extends TestCase {
    public void testRotation_10_10_10_aroundZ_is_minus10_10_10() {
        Rotation myRotation = new Rotation(Math.toRadians(90),'z');
        FloatColoredPVector myColoredPVector = new FloatColoredPVector(10, 10, 10);
        myRotation.applyTransformTo(myColoredPVector);
        System.out.println("after =  " + myColoredPVector);
        assertThat(myColoredPVector.roughlyEqualsPositionOfPVector(new FloatColoredPVector(-10,10,10)))
                .isEqualTo(true);
    }
    public void testRotation_10_0_0_aroundZ_is_0_10_0() {
        Rotation myRotation = new Rotation(Math.toRadians(90),'z');
        System.out.println("myRotation = "+myRotation);
        FloatColoredPVector myColoredPVector = new FloatColoredPVector(10, 0, 0);
        //System.out.println("x ="+myColoredPVector.getX()+" y = "+myColoredPVector.getY()+" z = "+myColoredPVector.getZ());
        System.out.println("myColoredPVector before = "+myColoredPVector);
        myRotation.applyTransformTo(myColoredPVector);
        System.out.println("myColoredPVector after =  " + myColoredPVector);
        assertThat(myColoredPVector.roughlyEqualsPositionOfPVector(new FloatColoredPVector(0,10,0)))
                .isEqualTo(true);
    }

}
