package imager.domain.PointClouds;

import imager.domain.Correlators.CorrelatorForTwoPointClouds;
import imager.domain.ThreeDImageCreator;
import imager.domain.ThreeDObjects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;

/**
 * Created by John on 12/04/2015.
 */
public class ZPointPlane {
    //TODO - get rid of this?   Or is it useful to do rough then fine with the same zPointPlane resolution?
    static PVector defaultZPointSizePVector() { //Used to be a PVector, to give flexibility, but a bit easier as a float, so you can divide another PVector by the float.
        return new PVector(5f, 5f, 5f);
    }

    //int zMatrixSetupSteps = 1;   //Reduces resolution of zMatrix - good if done in conjunction with deltas?
    //Rather than using this, should we just reduce the resolution of the zPointPlane?
    //By reducing resolution, there are less poins to deal with.
    static boolean drawTriangles = true;


    //Points and colours //Leave these as is for now, as if we use Cuda, its easier with arrays than objects.
    float[][] zMatrix2d;


    int[][] zMatrixColors2d;  //Keep this as this one's not a ColoredPVector.
    float[][] zMatrixHues2d;
    float[][] zMatrixSaturations2d;
    float[][] zMatrixBrightnesses2d;

    //Parent, correlator, logger
    ThreeDImageCreator parentThreeDImageCreator;



    CorrelatorForTwoPointClouds correlatorForTwoPointClouds;
    PointCloudForZPointPlane pointCloudForZPointPlane;
    Logger myLogger;

    //ZPoint Dimensions
    IntRectangle2D boundingIntRectangle2DBeforeCorrelate;

    //Keeping track of correlation
    private IntPVector3D correlationOffsetIntPVector3D = new IntPVector3D(0, 0, 0);  //IMPORTANT ONE - this is the absolute shift during correlation, in ZPoints.
    ///PVector cumulativeRotatePVector;  //TODO - why does this get passed in by the constructor from the correlator?
    //TODO - is it used here and if so what for?

    //Used to construct the ZMatrix - TODO can we move this nearer to where it's actually used?
    ArrayList<FloatColoredPVector>[][] zMatrixNearestPoints2d;  //zMatrixNearestPoints is an ARRAY of small arraylists of pointCloud points (PVectors) nearest to one of the evenly spaced points in the zMatrix.


    public static boolean isDrawTriangles() {
        return drawTriangles;
    }

    public float[][] getzMatrix2d() {
        return zMatrix2d;
    }

    public int[][] getzMatrixColors2d() {
        return zMatrixColors2d;
    }

    public float[][] getzMatrixHues2d() {
        return zMatrixHues2d;
    }

    public float[][] getzMatrixSaturations2d() {
        return zMatrixSaturations2d;
    }

    public float[][] getzMatrixBrightnesses2d() {
        return zMatrixBrightnesses2d;
    }

    public ThreeDImageCreator getParentThreeDImageCreator() {
        return parentThreeDImageCreator;
    }

    public CorrelatorForTwoPointClouds getCorrelatorForTwoPointClouds() {
        return correlatorForTwoPointClouds;
    }

    public PointCloudForZPointPlane getPointCloudForZPointPlane() {
        return pointCloudForZPointPlane;
    }

    public Logger getMyLogger() {
        return myLogger;
    }

    public ArrayList<FloatColoredPVector>[][] getzMatrixNearestPoints2d() {
        return zMatrixNearestPoints2d;
    }
    public void setCorrelatorForTwoPointClouds(CorrelatorForTwoPointClouds correlatorForTwoPointClouds) {
        this.correlatorForTwoPointClouds = correlatorForTwoPointClouds;
    }

    public ZPointPlane(PointCloudForZPointPlane myPointCloudForZPointPlane, ThreeDImageCreator aThreeDImageCreator, CorrelatorForTwoPointClouds myCorrelatorForTwoPointClouds) {
        //TODO - does the pointcloud already know its min and max?  If so we should use these to calculate the bounding Rectangle.
        myLogger = LoggerFactory.getLogger(this.getClass());
        parentThreeDImageCreator = aThreeDImageCreator;
        pointCloudForZPointPlane = myPointCloudForZPointPlane;
        correlatorForTwoPointClouds = myCorrelatorForTwoPointClouds;
    }

    public void setCorrelationOffsetIntPVector3D(IntPVector3D myIntPVector3D) {
        correlationOffsetIntPVector3D = myIntPVector3D;
    }

    /*public void setUpZPointPlaneAsOneOfPair(CorrelatorForTwoPointClouds aCorrelatorForTwoPointClouds) {//PVector minRealWorldPointCorrelator, PVector zMatrixScalePVector){
        correlatorForTwoPointClouds = aCorrelatorForTwoPointClouds;
        setUpZMatrixCommon(); //This does a lot of work to set up all the z Points.
    }*/

    private PVector getZPointSizePVector() {
        if (correlatorForTwoPointClouds == null) return ZPointPlane.defaultZPointSizePVector();
        else return correlatorForTwoPointClouds.getZPointSizePVector();
    }

    private void setUpBoundingIntRectangle() { //Really important one.
        myLogger.trace("pointCloudForZPointPlane = " + pointCloudForZPointPlane);
        myLogger.trace("pointCloudForZPointPlane.getBoundingRectangle() = " + pointCloudForZPointPlane.getBoundingRectangle());
        myLogger.trace("ZPointPlane.zPointSizePVector() = " + getZPointSizePVector()); //ZPointPlane.zPointSizePVector());
        boundingIntRectangle2DBeforeCorrelate = pointCloudForZPointPlane.getBoundingRectangle().div(getZPointSizePVector());  //Can't divide a PVector by a float. Try a FloatRect.
    }

    public void setUpZMatrixCommon() { //=========== THIS ONE DOES ALL THE WORK - START HERE ===================
        myLogger.debug("---------- Setting up ZMatrixCommon()");
        setUpBoundingIntRectangle();//This sets up the grid size.//Just moved this here.
        setUpArrays();    //This sets up the arrays for z and color to the right size ready to receive values.
        addArrayOfNearestUnderlyingPointsToEachZPoint(); //For each ZPoint, this sets up an array of the respective underlying PVectors.
        setUpZValuesAndColorsByAveragingUnderlyingPoints(); //This determines the z depth and color for each ZPoint by processing its array of underlying PVectors
        fillInTheGaps(); //This one is the most complex. It fills in gaps in the ZMatrix by looking at nearest neighbours.
    }

    private void setUpArrays() { //TODO just replace this wth a single array of objects which contain all these params.
        IntPVector2D diagonalIntPVector2D = boundingIntRectangle2DBeforeCorrelate.getDiagonal();
        //myLogger.info("Hhhhhh - diagonal = "+diagonalIntPVector2D.toStringReally());
        int xMax = diagonalIntPVector2D.intX + 1;  //Added + 1 here. Matrix is zero based.
        int yMax = diagonalIntPVector2D.intY + 1;
        zMatrix2d = new float[xMax][yMax];
        zMatrixColors2d = new int[xMax][yMax];
        zMatrixHues2d = new float[xMax][yMax];
        zMatrixSaturations2d = new float[xMax][yMax];
        zMatrixBrightnesses2d = new float[xMax][yMax];
        zMatrixNearestPoints2d = new ArrayList[xMax][yMax];
    }

    public int getZMatrixTotalNoOfPoints() {  //Only used to work out how many points needed for correlation match.
        IntPVector2D diagonalIntPVector2D = boundingIntRectangle2DBeforeCorrelate.getDiagonal();
        return diagonalIntPVector2D.intX * diagonalIntPVector2D.intY;
    }

    private void addArrayOfNearestUnderlyingPointsToEachZPoint() {
        //Iterate through ALL the points in the pointcloud.
        //Find the nearest evenly spaced zMatrix point to each.
        //Add pointcloud point to an ArrayList of points, owned by its respective ZPoint.
        //(Note - this will leave some gaps..)
        //      two options to solve this - either find 9 squares (points) nearest (+/-1 unit in each direction)
        //      (Easier?) or go thorugh zMatrix once done and fill blank points with average of 9 neighbours.
        for (FloatColoredPVector myFloatColoredPVector : pointCloudForZPointPlane.getOneAndOnlyArrayList()) {
            if (myFloatColoredPVector == null) myLogger.debug("Null ColoredPVector");
            if (Float.isNaN(myFloatColoredPVector.x)) myLogger.debug("ERROR - NaN in 2 - myColoredPVector.intX");
            if (Float.isNaN(myFloatColoredPVector.y)) myLogger.debug("ERROR - NaN in 2 - myColoredPVector.intY");
            IntPVector2D myZMatrixPoint = getZMatrixPointForRealWorldPVector(myFloatColoredPVector);  //This relies on correlation shift being set to zero at this stage.
            IntPVector2D my2dArrayPoint = getArrayPointForZMatrixPoint(myZMatrixPoint);
            int x = my2dArrayPoint.intX;
            int y = my2dArrayPoint.intY;
            if (x < 0 || y < 0)
                myLogger.debug("myColoredPVector = " + myFloatColoredPVector + ", my2dArrayPoint = " + my2dArrayPoint + " x = " + x + ", y = " + y);
            if (zMatrixNearestPoints2d[x][y] == null)
                zMatrixNearestPoints2d[x][y] = new ArrayList<FloatColoredPVector>(); //Create an ArrayList for the point if it doesn't already exist.  <PVector> here is a 'Generic' - it tells the arraylist it can only contain PVectors.
            zMatrixNearestPoints2d[x][y].add(myFloatColoredPVector); //try {zMatrixNearestPoints2d[intX][intY].add(myColoredPVector);} catch(NullPointerException e){System.out.println("Caught null pointer exception");
        }
    }

    //The array contains points from 0 to the diagonal. We need to translate back to find these points!
    private void setUpZValuesAndColorsByAveragingUnderlyingPoints() {
        IntPVector2D diagonalIntPVector2D = boundingIntRectangle2DBeforeCorrelate.getDiagonal();
        //3) Iterate through the zMatrix and work out the Z values for all the points which have one or more real world points associated with them.
        for (int y = 0; y <= diagonalIntPVector2D.intY; y += 1) { //zMatrixSetupSteps //Changed from < to <= Check does this cause bug?
            for (int x = 0; x <= diagonalIntPVector2D.intX; x += 1) {
                //int zMatrixIndex = intX + intY * zMatrixIntPVectorGridSize.intX;
                if (zMatrixNearestPoints2d[x][y] == null) {//if (zMatrixNearestPoints[zMatrixIndex].isEmpty()) {
                    //zMatrix[zMatrixIndex] = ThreeDImageCreator.veryHighFloat;
                    zMatrix2d[x][y] = ThreeDImageCreator.veryHighFloat;
                } //else if(zMatrixNearestPoints2d[intX][intY].isEmpty()){  //is this needed?
                //zMatrix2d[intX][intY] = ThreeDImageCreator.veryHighFloat;                }
                else {
                    setZMatrixZDepthAndColorFromNearestUnderlyingPoints(x, y);
                }
            }
        }
    }

    private void setZMatrixZDepthAndColorFromNearestUnderlyingPoints(int x, int y) {
        zMatrix2d[x][y] = getZForSmallRealWorldPointArray(zMatrixNearestPoints2d[x][y], x, y);   //zMatrix[zMatrixIndex] = getZForSmallRealWorldPointArray(zMatrixNearestPoints[zMatrixIndex], intX, intY);  //Passing all 3 parameters saves doing calc twice.
        FloatColoredPVector myAverageFloatColoredPVector = zMatrixNearestPoints2d[x][y].get(0);
        zMatrixColors2d[x][y] = myAverageFloatColoredPVector.getColor();   //TODO - MAKE THIS MORE ACCURATE BY AVERAGING COLOURS NOT TAKING THE LAST ONE.
        zMatrixHues2d[x][y] = myAverageFloatColoredPVector.getHue();  //TODO - add concept of color in the IntPVector - make it a ColoredIntPVector and have it remember HSB as well as Colour.
        zMatrixSaturations2d[x][y] = myAverageFloatColoredPVector.getSaturation();
        zMatrixBrightnesses2d[x][y] = myAverageFloatColoredPVector.getBrightness();
    }

    private void setZMatrixZDepthAndColorFromNearestColoredZPoints(ArrayList<IntColoredZPoint> surroundingZMatrixPoints, int x, int y) {
        IntColoredZPoint myIntColoredZPoint = surroundingZMatrixPoints.get(0);
        //TODO causes an error if you just use z.
        zMatrix2d[x][y] = myIntColoredZPoint.getZ();   //zMatrix[zMatrixIndex] = getZForSmallRealWorldPointArray(zMatrixNearestPoints[zMatrixIndex], intX, intY);  //Passing all 3 parameters saves doing calc twice.
        myLogger.trace("Setting new point z to " + myIntColoredZPoint.getZ());
        zMatrixColors2d[x][y] = myIntColoredZPoint.getColor();   //TODO - MAKE THIS MORE ACCURATE BY AVERAGING COLOURS NOT TAKING THE LAST ONE.
        myLogger.trace("Setting new point color to " + myIntColoredZPoint.getColor());
        myIntColoredZPoint.setHSBFromExistingColor();  //Only do this here as we don't need to do it for the points we're discarding at the moment.
        zMatrixHues2d[x][y] = myIntColoredZPoint.getHue();  //TODO - add concept of color in the IntPVector - make it a ColoredIntPVector and have it remember HSB as well as Colour.
        zMatrixSaturations2d[x][y] = myIntColoredZPoint.getSaturation();
        zMatrixBrightnesses2d[x][y] = myIntColoredZPoint.getBrightness();
    }

    private void fillInTheGaps() {
        IntPVector2D diagonalIntPVector2D = boundingIntRectangle2DBeforeCorrelate.getDiagonal();
        //4) There are some gaps..
        //Iterate through zMatrix finding gaps.
        //For each gap, if there are more than 4 surrounding populated points (ie not at the crisp edges..)
        //Add the surrounding points
        ArrayList<IntColoredZPoint> surroundingColoredZMatrixPoints;
        for (int y = 1; y <= diagonalIntPVector2D.intY - 1; y += 1) { //Changed from < to <= //Start one point in from the edge so you always have 8 surrounding points.
            for (int x = 1; x <= diagonalIntPVector2D.intX - 1; x += 1) {
                //int zMatrixIndex = intX + intY * zMatrixIntPVectorGridSize.intX;
                if (zMatrixNearestPoints2d[x][y] == null) {//if (zMatrixNearestPoints[zMatrixIndex].isEmpty()) {
                    surroundingColoredZMatrixPoints = new ArrayList<IntColoredZPoint>(); //old ones get garbage collected.   Can't yet find a quick way to remove all objects.
                    IntPVector2D nearZPoint = new IntPVector2D();
                    for (nearZPoint.intX = (x - 1); nearZPoint.intX <= (x + 1); nearZPoint.intX++) {
                        for (nearZPoint.intY = (y - 1); nearZPoint.intY <= (y + 1); nearZPoint.intY++) {
                            if (!((nearZPoint.intX == x) && (nearZPoint.intY == y))) { //Skip the central point as we already know there are no associated realworld points.
                                //if((nearX >=0) && (nearX <= zMatrixWidth) && (nearY >=0) && (nearY <= zMatrixHeight)){
                                //int nearPointIndex = nearX + nearY * zMatrixIntPVectorGridSize.intX;
                                if (!(zMatrixNearestPoints2d[nearZPoint.intX][nearZPoint.intY] == null)) {//if (!zMatrixNearestPoints[nearPointIndex].isEmpty()) {
                                    //TODO could remove ColoredZPoint completely and calculate this exactly the same way as before, by adding in the underlying ColoredPVectors.
                                    //TODO - that approach would be more accurate and would allow us to remove the ColoredZPoint class, but would involve more calculations.
                                    //PVector myZMatrixPoint = new PVector(getRealWorldXForZMatrixX(nearX), getRealWorldYForZMatrixY(nearY), zMatrix2d[nearX][nearY]);//PVector myZMatrixPoint = new PVector(getRealWorldXForZMatrixX(nearX), getRealWorldYForZMatrixY(nearY), zMatrix[nearPointIndex]);
                                    //PVector myZMatrixPoint = getRealWorldPVectorForZMatrixPointIncludingCorrelationOffset(nearZPoint);  //TODO - Can this be simplified?
                                    IntColoredZPoint myIntColoredZPoint = new IntColoredZPoint(parentThreeDImageCreator, x, y, zMatrix2d[nearZPoint.intX][nearZPoint.intY], zMatrixColors2d[nearZPoint.intX][nearZPoint.intY]); //HSB not set here.
                                    myLogger.trace("xxx creating ColoredZPoint with color: " + zMatrixColors2d[nearZPoint.intX][nearZPoint.intY] + " z: " + zMatrix2d[nearZPoint.intX][nearZPoint.intY]);
                                    surroundingColoredZMatrixPoints.add(myIntColoredZPoint);
                                    //if(Float.isNaN(myZMatrixPoint.z)) myLogger.debug("NaN found in 4 !!!!!!!!!!!!!!!!! nearX = "+nearZPoint.intX + " nearY = "+ nearZPoint.intY);
                                }
                                //}
                            }
                        }
                    }
                    //We've got as many collections of points as we're going to get now.
                    //See if it's valid (ie surrounded by 5 or more points with values)
                    //and if so, add the points.7
                    int noOfSurroundingColoredZPoints = surroundingColoredZMatrixPoints.size();
                    if (noOfSurroundingColoredZPoints > 4) {
                        setZMatrixZDepthAndColorFromNearestColoredZPoints(surroundingColoredZMatrixPoints, x, y);
                       /* for (int counter = 0; counter < noOfSurroundingColoredZPoints; counter++) {
                            //PVector myPVector = surroundingColoredZMatrixPoints.get(counter);
                            //Need to add the
                            setZMatrixZDepthAndColorFromNearestColoredZPoints(surroundingColoredZMatrixPoints, intX, intY);
                        }*/
                    }
                }
            }
        }
    }

    //TODO - here so far.

    private IntPVector2D getZMatrixPointForRealWorldPVector(PVector realWorldPoint) {
        return IntPVector2D.div2DRoundingMinusPointFiveUpToZero(realWorldPoint, getZPointSizePVector()); //ZPointPlane.zPointSizePVector());
        //TODO - this may be faster and work just as well just by using div2D here, but if input points tend to be whold numbers? (Check this) then simply rounding to nearest int may cause problems.
    }

    public IntPVector2D getArrayPointForZMatrixPoint(IntPVector2D zMatrixIntPVector2D) {  //IncludingCorrelationShift
        //TODO - we should really only do this once for the entire plane. Not recalculate it for every point.
        //TODO - lots of wasted effort here.  Do it in calling class instead?
        IntPVector2D totalShift = IntPVector2D.add(boundingIntRectangle2DBeforeCorrelate.getMin(), correlationOffsetIntPVector3D);
        return IntPVector2D.sub(zMatrixIntPVector2D, totalShift);
        //TODO - do we check if within array bounds at this stage or later?
    }

    //Get z depth for a ZPoint (a float) for one of the small arrays of real world points. This is used when first building up the zMatrix.
    public float getZForSmallRealWorldPointArray(ArrayList<FloatColoredPVector> mySmallFloatColoredPVectorArrayList, int zMatrixX, int zMatrixY) {
        int noOfPoints = mySmallFloatColoredPVectorArrayList.size();
        if (noOfPoints == 0) {
            myLogger.error("ERROR - missing points while trying to set up ZPointPlane");
            return 0f;
        } else {
            return mySmallFloatColoredPVectorArrayList.get(0).z; //TODO add a more accurate, slower method here.
        }
    }


    //Get colours
    /*public int getZMatrixColorOrMinusFiveNinesFor(IntPVector2D myZPoint) {
        //if (isInBounds(myZPoint)) {
            return zMatrixColors2d[myZPoint.intX][myZPoint.intY];//return zMatrixColors[zMatrixY * zMatrixIntPVectorGridSize.intX + zMatrixX];
       // } else {
       //     return ThreeDImageCreator.invalidInt;
       // }
    }*/
    /*public float getZMatrixHueForArrayPosition(IntPVector2D myArrayPoint) {
        //if ((zMatrixX < zMatrixPointWidth) & (zMatrixX >= 0) & (zMatrixY < zMatrixPointHeight) & (zMatrixY >= 0)) {
        return zMatrixHues2d[myArrayPoint.intX][myArrayPoint.intY];//return zMatrixHues[zMatrixY * zMatrixIntPVectorGridSize.intX + zMatrixX];
        //} else {
        //    return ThreeDImageCreator.invalidInt;
        //}
    }
    public float getZMatrixSaturationForXYPosition(IntPVector2D myZPoint) {
        //if ((zMatrixX < zMatrixPointWidth) & (zMatrixX >= 0) & (zMatrixY < zMatrixPointHeight) & (zMatrixY >= 0)) {
        return zMatrixSaturations2d[myZPoint.intX][myZPoint.intY];
        //} else {
        //    return ThreeDImageCreator.invalidInt;
        //}
    }
    public float getZMatrixBrightnessForXYPosition(IntPVector2D myZPoint) {
        //if ((zMatrixX < zMatrixPointWidth) & (zMatrixX >= 0) & (zMatrixY < zMatrixPointHeight) & (zMatrixY >= 0)) {
        return zMatrixBrightnesses2d[myZPoint.intX][myZPoint.intY];
        //} else {
        //    return ThreeDImageCreator.invalidInt;
        //}
    }*/

    //Draw
    public void draw(PGraphics myPGraphics) {
        parentThreeDImageCreator.colorMode(PApplet.HSB);
        parentThreeDImageCreator.strokeWeight(1f);
        parentThreeDImageCreator.rectMode(PConstants.CENTER);
        PVector myZPointSizePVector = getZPointSizePVector(); //ZPointPlane.myZPointSizePVector();
        IntPVector2D myMin2DPoint = getCorrelationShiftedBoundingIntRectangle2D().getMin();
        float myZCorrelationOffsetFloat = correlationOffsetIntPVector3D.intZ * getZPointSizePVector().z;  //ZPointPlane.zPointSizePVector().z;

        //myLogger.info("draw");
        IntPVector2D diagonalIntPVector2D = boundingIntRectangle2DBeforeCorrelate.getDiagonal();
        IntPVector2D my2DArrayPoint = new IntPVector2D();
        PVector[][] realWorldPointArray = new PVector[diagonalIntPVector2D.intX+1][diagonalIntPVector2D.intY+1];

        //Fill real world point array.  More efficient for triangles to just do this once here.
        for(my2DArrayPoint.intX=0;my2DArrayPoint.intX<=diagonalIntPVector2D.intX;my2DArrayPoint.intX++) {
            for (my2DArrayPoint.intY = 0; my2DArrayPoint.intY <= diagonalIntPVector2D.intY; my2DArrayPoint.intY++) {
                realWorldPointArray[my2DArrayPoint.intX][my2DArrayPoint.intY] =
                        realWorldPVectorFor2DArrayPoint(my2DArrayPoint, myMin2DPoint, myZPointSizePVector, myZCorrelationOffsetFloat);
            }
        }
        if (ZPointPlane.drawTriangles) {
            drawTriangles(my2DArrayPoint, diagonalIntPVector2D, myPGraphics, realWorldPointArray);
        } else {
            drawCuboids(my2DArrayPoint, diagonalIntPVector2D, myPGraphics, realWorldPointArray, myZPointSizePVector);
        }
    }

    private void drawCuboids(IntPVector2D my2DArrayPoint, IntPVector2D diagonalIntPVector2D, PGraphics myPGraphics, PVector[][] realWorldPointArray, PVector myZPointSizePVector) {
        myPGraphics.stroke(parentThreeDImageCreator.color(0, 0, 0));  //black outline round each triangle?  Remove if not.
        //myPGraphics.stroke(myColor);
        for (my2DArrayPoint.intX = 0; my2DArrayPoint.intX <= diagonalIntPVector2D.intX; my2DArrayPoint.intX++) {
            for (my2DArrayPoint.intY = 0; my2DArrayPoint.intY <= diagonalIntPVector2D.intY; my2DArrayPoint.intY++) {
                PVector myPVector = realWorldPointArray[my2DArrayPoint.intX][my2DArrayPoint.intY];
                if (myPVector != null) {
                    int myColor = zMatrixColors2d[my2DArrayPoint.intX][my2DArrayPoint.intY];
                   myPGraphics.fill(myColor);
                    drawACuboid(myPGraphics, realWorldPointArray[my2DArrayPoint.intX][my2DArrayPoint.intY], myZPointSizePVector);
                }
            }
        }
    }

    private void drawTriangles(IntPVector2D my2DArrayPoint, IntPVector2D diagonalIntPVector2D, PGraphics myPGraphics, PVector[][] realWorldPointArray){
        myPGraphics.stroke(parentThreeDImageCreator.color(0, 0, 0));  //black outline round each triangle?  Remove if not.
        //myPGraphics.stroke(parentThreeDImageCreator.color(255,0,255));
        for(my2DArrayPoint.intX=0;my2DArrayPoint.intX<diagonalIntPVector2D.intX;my2DArrayPoint.intX++) {  //Must be less than not less than or equal to.
            for (my2DArrayPoint.intY = 0; my2DArrayPoint.intY < diagonalIntPVector2D.intY; my2DArrayPoint.intY++) {
                int myColor = zMatrixColors2d[my2DArrayPoint.intX][my2DArrayPoint.intY];
                myPGraphics.fill(myColor);
                //myPGraphics.fill(parentThreeDImageCreator.color(0,0,0));
                myPGraphics.stroke(myColor);
                drawATriangle(myPGraphics,
                        realWorldPointArray[my2DArrayPoint.intX][my2DArrayPoint.intY],
                        realWorldPointArray[my2DArrayPoint.intX+1][my2DArrayPoint.intY],
                        realWorldPointArray[my2DArrayPoint.intX][my2DArrayPoint.intY+1]);
                drawATriangle(myPGraphics,
                        realWorldPointArray[my2DArrayPoint.intX+1][my2DArrayPoint.intY],
                        realWorldPointArray[my2DArrayPoint.intX][my2DArrayPoint.intY+1],
                        realWorldPointArray[my2DArrayPoint.intX+1][my2DArrayPoint.intY+1]);
            }
        }
    }

    private void drawATriangle(PGraphics myPGraphics, PVector p1, PVector p2, PVector p3){
        if (p1 != null && p2 != null && p3 != null) {
            myPGraphics.beginShape(PApplet.TRIANGLES);
            myPGraphics.vertex(p1.x, p1.y, p1.z);
            myPGraphics.vertex(p2.x, p2.y, p2.z);
            myPGraphics.vertex(p3.x, p3.y, p3.z);
            myPGraphics.endShape();
        }
    }

    private void drawACuboid(PGraphics myPGraphics, PVector p1,PVector myZPointSizePVector){
        myPGraphics.pushMatrix(); //Remember coordinates before translation
        myPGraphics.translate(p1.x, p1.y, p1.z);
        myPGraphics.box(myZPointSizePVector.x, myZPointSizePVector.y, myZPointSizePVector.z);
        myPGraphics.popMatrix(); //Move back to original coordinate system before translation
    }
    private void drawCuboids(PGraphics myPGraphics){
     }

    private PVector realWorldPVectorFor2DArrayPoint(IntPVector2D my2DArrayPoint, IntPVector2D myMin2DPoint, PVector myZPointSizePVector, float myZCorrelationOffsetFloat ){
        float myZFloat = zMatrix2d[my2DArrayPoint.intX][my2DArrayPoint.intY];
        if (myZFloat == ThreeDImageCreator.veryHighFloat) return null;
        else {
            IntPVector2D myZMatrixPoint2D = IntPVector2D.add(myMin2DPoint, my2DArrayPoint);
            PVector myRealWorldPoint3D = IntPVector2D.mult(myZMatrixPoint2D, myZPointSizePVector);
            myRealWorldPoint3D.z = myZFloat + myZCorrelationOffsetFloat;
            return myRealWorldPoint3D;
        }
    }

    /*
    private void drawCuboidFor2DArrayPoint(PGraphics myPGraphics, IntPVector2D my2DArrayPoint, IntPVector2D myMin2DPoint, float myZCorrelationOffsetFloat, PVector myZPointSizePVector){
        float myZFloat = zMatrix2d[my2DArrayPoint.intX][my2DArrayPoint.intY];
        if (myZFloat != ThreeDImageCreator.veryHighFloat) {
            IntPVector2D myZMatrixPoint2D = IntPVector2D.add(myMin2DPoint, my2DArrayPoint);
            PVector myRealWorldPoint3D = IntPVector2D.mult(myZMatrixPoint2D, myZPointSizePVector);
            myRealWorldPoint3D.z = myZFloat+myZCorrelationOffsetFloat;
             int myColor = zMatrixColors2d[my2DArrayPoint.intX][my2DArrayPoint.intY];
            //TODO - draw with point at centre of cuboid.
            //myPGraphics.beginDraw();
            myPGraphics.stroke(parentThreeDImageCreator.color(0, 0, 0));  //black outline round each box.
            myPGraphics.fill(myColor);
            myPGraphics.pushMatrix(); //Remember coordinates before translation
            myPGraphics.translate(myRealWorldPoint3D.x, myRealWorldPoint3D.y, myRealWorldPoint3D.z);
            myPGraphics.box(myZPointSizePVector.x, myZPointSizePVector.y, myZPointSizePVector.z);
            myPGraphics.popMatrix(); //Move back to original coordinate system before translation
            //myPGraphics.endDraw();
            myLogger.trace("drawCuboidFor2DArrayPoint, color " + myColor + " Location " + my2DArrayPoint);
        }
    }
*/
    //added
    private PVector getRealWorldCorrelationOffset(){
        return IntPVector2D.mult(correlationOffsetIntPVector3D, getZPointSizePVector());
    }

    //Get realworld point.
    public PVector getMinCorrelationShiftedRealWorldPoint() {
        return PVector.add(pointCloudForZPointPlane.getBoundingRectangle().getMin(), getRealWorldCorrelationOffset());
        //return minCorrelationShiftedRealWorldPoint;  //TODO - need to calc this now, backwards from the correlationOffsetIntPVector and zPointSizePVector()
    }

    public IntRectangle2D getBoundingIntRectangle2DBeforeCorrelate(){
        return this.boundingIntRectangle2DBeforeCorrelate;
    }
    public IntRectangle2D getCorrelationShiftedBoundingIntRectangle2D(){
        return IntRectangle2D.add(this.boundingIntRectangle2DBeforeCorrelate, this.correlationOffsetIntPVector3D);
    }

    //Printing
    public String toString(){
        return ("unfeasible to return string with all points for real image");
        /*return ("zPointPlane including correlation shift = "+ getCorrelationShiftedBoundingIntRectangle2D()+"\n" +
                "Min = "+ getCorrelationShiftedBoundingIntRectangle2D().min+"\n" +
                "Correlation shift = "+correlationOffsetIntPVector3D+"\n"+
                "Points including correlation shift = \n" + this.stringContainingAllPoints());  //TODO can remove the individual points later for simplicity.*/
    }

    private String stringContainingAllPoints() {
        IntPVector2D myDiagonal = boundingIntRectangle2DBeforeCorrelate.getDiagonal();
        String myString = new String();
        for (int y = 0; y <= myDiagonal.intY; y += 1) {//changed from < to <=
            for (int x = 0; x <= myDiagonal.intX; x += 1) {
                myString = myString+("intX = "+x+", intY = "+y+", zMatrix2d = "+zMatrix2d[x][y]+", zMatrixColor2d = "+zMatrixColors2d[x][y]+"\n");
            }
        }
        return myString;
    }
    //Unused
    private float getDistanceBetween(float x1, float y1, float x2, float y2) { //TODO - more complex method
        return PApplet.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }
    public IntPVector3D getCorrelationOffsetIntPVector3D() {
        return correlationOffsetIntPVector3D;
    }
}
