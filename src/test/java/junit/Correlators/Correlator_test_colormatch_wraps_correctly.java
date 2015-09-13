package junit.Correlators;

import junit.framework.TestCase;
import imager.domain.Correlators.CorrelatorMoveZLast;
import imager.domain.ThreeDImageCreator;
import org.junit.Test;
//import org.junit.experimental.categories.Category;
//import imager.zz.zzTestRunner;
//import static org.junit.Assert.assertTrue;


public class Correlator_test_colormatch_wraps_correctly extends TestCase {

    //@Category(Integration)
    public final void test_Correlator_GetColorPartMatch_WrapsRound() {
        CorrelatorMoveZLast myStepByStepCorrelator = new CorrelatorMoveZLast(new ThreeDImageCreator()) ;   //set up once here to use for tests

        assertTrue(myStepByStepCorrelator.getColorPartMatch(10, 20) == 10f);
        assertTrue(myStepByStepCorrelator.getColorPartMatch(10, 200) == 65f);
        assertTrue(myStepByStepCorrelator.getColorPartMatch(200, 10) == 65f);
        assertTrue(myStepByStepCorrelator.getColorPartMatch(10, 20) == 10f);
    }
}
