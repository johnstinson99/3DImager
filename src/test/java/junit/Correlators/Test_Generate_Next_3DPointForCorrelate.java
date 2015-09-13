package junit.Correlators;

import junit.framework.TestCase;
import imager.domain.Correlators.CorrelatorMoveZLast;
import imager.domain.ThreeDObjects.IntPVector3D;
import org.junit.Test;


/**
 * Created by John on 23/07/2015.
 */
public class Test_Generate_Next_3DPointForCorrelate extends TestCase{
    @Test
    public final void test_NextAfterStart_IncrementsX( ){
        IntPVector3D minPos = new IntPVector3D(-2,-2,-2);
        IntPVector3D maxPos = new IntPVector3D (2,2,2);
        IntPVector3D currentPos = new IntPVector3D(-2,-2,-2);
        IntPVector3D expectedResult = new IntPVector3D (-1, -2, -2);
        CorrelatorMoveZLast.incrementCorrelationPositionTowardsMax(currentPos, maxPos, minPos);
        assertTrue(currentPos.equals(expectedResult));
    }
    @Test
    public final void test_NextAfterMaxX_IncrementsY( ){
        IntPVector3D minPos = new IntPVector3D(-2,-2,-2);
        IntPVector3D maxPos = new IntPVector3D (2,2,2);
        IntPVector3D currentPos = new IntPVector3D(2,-2,-2);
        IntPVector3D expectedResult = new IntPVector3D (-2,-1,-2);
        CorrelatorMoveZLast.incrementCorrelationPositionTowardsMax(currentPos, maxPos, minPos);
        assertTrue(currentPos.equals(expectedResult));
    }
    @Test
    public final void test_NextAfterMaxY_IncrementsZ( ){
        IntPVector3D minPos = new IntPVector3D(-2,-2,-2);
        IntPVector3D maxPos = new IntPVector3D (2,2,2);
        IntPVector3D currentPos = new IntPVector3D(2,2,-2);
        IntPVector3D expectedResult = new IntPVector3D (-2,-2,-1);
        CorrelatorMoveZLast.incrementCorrelationPositionTowardsMax(currentPos, maxPos, minPos);
        assertTrue(currentPos.equals(expectedResult));
    }
    @Test
    public final void test_NextAfterMaxZ_GivesError( ){
        IntPVector3D minPos = new IntPVector3D(-2,-2,-2);
        IntPVector3D maxPos = new IntPVector3D (2,2,2);
        IntPVector3D currentPos = new IntPVector3D(2,2,2);
        IntPVector3D expectedResult = new IntPVector3D (2,2,2);
        CorrelatorMoveZLast.incrementCorrelationPositionTowardsMax(currentPos, maxPos, minPos);
        assertTrue(currentPos.equals(expectedResult));
    }

}
