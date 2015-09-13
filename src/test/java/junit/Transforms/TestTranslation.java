package junit.Transforms;

import imager.domain.ThreeDObjects.FloatColoredPVector;
import imager.domain.Transforms.Translation;
import junit.framework.TestCase;
import static org.assertj.core.api.Assertions.*;


/**
 * Created by John on 04/09/2015.
 */
public class TestTranslation extends TestCase {
    public void testTranslation() {
        Translation myTranslation = new Translation(2.0f, 3.0f, 4.0f);
        FloatColoredPVector myColoredPVector = new FloatColoredPVector(10, 10, 10);
        myTranslation.applyTransformTo(myColoredPVector);
        System.out.println("after =  " + myColoredPVector);
        assertThat(myColoredPVector.roughlyEqualsPositionOfPVector(new FloatColoredPVector(12, 13, 14)))
                .isEqualTo(true);
    }
}
