package junit.ThreeDObjects;

import junit.framework.TestCase;
import imager.domain.ThreeDObjects.FloatColoredPVector;
import org.junit.Test;

//import imager.zz.zzTestRunner;

public class FloatColoredPVectorTest2 extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }


   // @Category(zzTestRunner.Unit.class)
    public final void testRotateDegrees_Y(){
        //Happy path cases
        assertTrue(new FloatColoredPVector(10,10,10).rotateDegrees(0f, 'z').roughlyEqualsPositionOfPVector(new FloatColoredPVector(10, 10, 10)));
        assertTrue(new FloatColoredPVector(10,10,10).rotateDegrees(90f, 'z').roughlyEqualsPositionOfPVector(new FloatColoredPVector(-10,10,10)));
        assertTrue(new FloatColoredPVector(10,10,10).rotateDegrees(180f, 'z').roughlyEqualsPositionOfPVector(new FloatColoredPVector(-10,-10,10)));
        assertTrue(new FloatColoredPVector(10,10,10).rotateDegrees(270f, 'z').roughlyEqualsPositionOfPVector(new FloatColoredPVector(10,-10,10)));
        assertTrue(new FloatColoredPVector(10,10,10).rotateDegrees(360f, 'z').roughlyEqualsPositionOfPVector(new FloatColoredPVector(10,10,10)));

        //Boundary conditions for zeros in input
        assertTrue(new FloatColoredPVector(10,0,0).rotateDegrees(90f, 'z').roughlyEqualsPositionOfPVector(new FloatColoredPVector(0,10,0)));
        assertTrue(new FloatColoredPVector(0,10,0).rotateDegrees(90f, 'z').roughlyEqualsPositionOfPVector(new FloatColoredPVector(-10,0,0)));
        assertTrue(new FloatColoredPVector(0,0,10).rotateDegrees(90f, 'z').roughlyEqualsPositionOfPVector(new FloatColoredPVector(0,0,10)));
        assertTrue(new FloatColoredPVector(0,0,0).rotateDegrees(90f, 'z').roughlyEqualsPositionOfPVector(new FloatColoredPVector(0,0,0)));
    }

   // @Category(zzTestRunner.Unit.class)
    public final void testRotateDegrees_X(){
        //Happy path cases
        assertTrue(new FloatColoredPVector(10, 10, 10).rotateDegrees(0f, 'x').roughlyEqualsPositionOfPVector(new FloatColoredPVector(10, 10, 10)));
        assertTrue(new FloatColoredPVector(10,10,10).rotateDegrees(90f, 'x').roughlyEqualsPositionOfPVector(new FloatColoredPVector(10,-10,10)));
        assertTrue(new FloatColoredPVector(10,10,10).rotateDegrees(180f, 'x').roughlyEqualsPositionOfPVector(new FloatColoredPVector(10,-10,-10)));
        assertTrue(new FloatColoredPVector(10,10,10).rotateDegrees(270f, 'x').roughlyEqualsPositionOfPVector(new FloatColoredPVector(10,10,-10)));
        assertTrue(new FloatColoredPVector(10,10,10).rotateDegrees(360f, 'x').roughlyEqualsPositionOfPVector(new FloatColoredPVector(10,10,10)));

        //Boundary conditions for zeros in input
        assertTrue(new FloatColoredPVector(10,0,0).rotateDegrees(90f, 'x').roughlyEqualsPositionOfPVector(new FloatColoredPVector(10,0,0)));
        assertTrue(new FloatColoredPVector(0,10,0).rotateDegrees(90f, 'x').roughlyEqualsPositionOfPVector(new FloatColoredPVector(0,0,10)));
        assertTrue(new FloatColoredPVector(0,0,10).rotateDegrees(90f, 'x').roughlyEqualsPositionOfPVector(new FloatColoredPVector(0,-10,0)));
        assertTrue(new FloatColoredPVector(0,0,0).rotateDegrees(90f, 'x').roughlyEqualsPositionOfPVector(new FloatColoredPVector(0,0,0)));
    }

    //@Category(zzTestRunner.Unit.class)
    public final void testRotateDegrees_Z(){
        //Happy path cases
        assertTrue(new FloatColoredPVector(10,10,10).rotateDegrees(0f, 'y').roughlyEqualsPositionOfPVector(new FloatColoredPVector(10, 10, 10)));
        assertTrue(new FloatColoredPVector(10,10,10).rotateDegrees(90f, 'y').roughlyEqualsPositionOfPVector(new FloatColoredPVector(-10, 10, 10)));
        assertTrue(new FloatColoredPVector(10,10,10).rotateDegrees(180f, 'y').roughlyEqualsPositionOfPVector(new FloatColoredPVector(-10, 10, -10)));
        assertTrue(new FloatColoredPVector(10,10,10).rotateDegrees(270f, 'y').roughlyEqualsPositionOfPVector(new FloatColoredPVector(10, 10, -10)));
        assertTrue(new FloatColoredPVector(10,10,10).rotateDegrees(360f, 'y').roughlyEqualsPositionOfPVector(new FloatColoredPVector(10, 10, 10)));

        //Boundary conditions for zeros in input
        assertTrue(new FloatColoredPVector(0, 10, 10).rotateDegrees(90f, 'y').roughlyEqualsPositionOfPVector(new FloatColoredPVector(-10, 10, 0)));
        assertTrue(new FloatColoredPVector(10,0,10).rotateDegrees(90f, 'y').roughlyEqualsPositionOfPVector(new FloatColoredPVector(-10,0,10)));
        assertTrue(new FloatColoredPVector(10,10,0).rotateDegrees(90f, 'y').roughlyEqualsPositionOfPVector(new FloatColoredPVector(0,10,10)));
        assertTrue(new FloatColoredPVector(0,0,0).rotateDegrees(90f, 'y').roughlyEqualsPositionOfPVector(new FloatColoredPVector(0,0,0)));
    }


    public void testRotate3dRadians() throws Exception {

    }

    public void testRoughlyEqualsPositionOfPVector() throws Exception {

    }
}