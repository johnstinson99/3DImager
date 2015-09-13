package junit.ThreeDObjects;

import junit.framework.TestCase;
import imager.domain.ThreeDObjects.IntPVector2D;
import imager.domain.ThreeDObjects.IntRectangle2D;
import org.junit.Test;

//import imager.zz.zzTestRunner;

public class IntRectangleTest extends TestCase {
    IntRectangle2D baseRect;
    IntRectangle2D[] nonOverlappingRect = new IntRectangle2D[5];
    IntRectangle2D[] overlappingRect = new IntRectangle2D[4];
    IntRectangle2D[] overlappingResult = new IntRectangle2D[4];

    public IntRectangleTest() {
        baseRect = new IntRectangle2D().setMinMax(new IntPVector2D(5, 5), new IntPVector2D(10, 10));
        nonOverlappingRect[0]  = new IntRectangle2D().setMinMax(new IntPVector2D(11, 11), new IntPVector2D(15, 15));
        nonOverlappingRect[1]  = new IntRectangle2D().setMinMax(new IntPVector2D(0,0), new IntPVector2D(4, 4));
        nonOverlappingRect[2] = new IntRectangle2D().setMinMax(new IntPVector2D(0,0), new IntPVector2D(5,4));
        nonOverlappingRect[3]  = new IntRectangle2D().setMinMax(new IntPVector2D(-5, -5), new IntPVector2D(4, 4));
        nonOverlappingRect[4]  = new IntRectangle2D().setMinMax(new IntPVector2D(0, -5), new IntPVector2D(0, 4));

        overlappingRect[0]  = new IntRectangle2D().setMinMax(new IntPVector2D(7, 7), new IntPVector2D(12, 12));
        overlappingResult[0]  = new IntRectangle2D().setMinMax(new IntPVector2D(7, 7), new IntPVector2D(10, 10));
        overlappingRect[1]  = new IntRectangle2D().setMinMax(new IntPVector2D(7,2), new IntPVector2D(12,7));
        overlappingResult[1]  = new IntRectangle2D().setMinMax(new IntPVector2D(7, 5), new IntPVector2D(10, 7));
        overlappingRect[2]  = new IntRectangle2D().setMinMax(new IntPVector2D(2,2), new IntPVector2D(7,7));
        overlappingResult[2]  = new IntRectangle2D().setMinMax(new IntPVector2D(5, 5), new IntPVector2D(7,7));
        overlappingRect[3]  = new IntRectangle2D().setMinMax(new IntPVector2D(2,7), new IntPVector2D(7,12));
        overlappingResult[3]  = new IntRectangle2D().setMinMax(new IntPVector2D(5, 7), new IntPVector2D(7,10));

    }
    @Test
   // @Category(zzTestRunner.Unit.class)
    public final void dummyTest(){
        assertTrue(1 == 1);
    }

    @Test
    public final void testRectanglesThatDontOverlap(){
        for(int i = 0; i< nonOverlappingRect.length; i++) {
            boolean rectsDontOverlap = IntRectangle2D.overlap(baseRect, nonOverlappingRect[i]) == null;
            System.out.println("Check these two don't overlap: "+rectsDontOverlap+": " + nonOverlappingRect[0] + ", " + nonOverlappingRect[i]);
            assertTrue(rectsDontOverlap);
        }
     }

    @Test
    public final void testRectanglesThatDOOverlap(){
        for(int i = 0; i< overlappingRect.length; i++) {
            IntRectangle2D actualResult = IntRectangle2D.overlap(baseRect, overlappingRect[i]);
            IntRectangle2D expectedResult = overlappingResult[i];
            System.out.println("Check these two DO overlap: " + baseRect + ", " + overlappingRect[i]);
            System.out.println("Expected result = "+ expectedResult+" Actual result = "+actualResult);
            assertEquals(expectedResult.toString(), actualResult.toString());
        }
    }


    public void setUp() throws Exception {
        super.setUp();

    }

}