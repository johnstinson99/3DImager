package imager.domain.PointClouds;

import imager.domain.ThreeDImageCreator;
import imager.domain.ThreeDObjects.FloatColoredPVector;
import imager.domain.ThreeDObjects.FloatRectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processing.core.*;

import java.util.ArrayList;

//import java.lang.Math; - using PApplet maths functions

public class PointCloud {  //TODO extend PApplet and remove references to PApplet.
    Frame parentFrame;  //TODO - create pointcloud on creating frame.
    ThreeDImageCreator parentThreeDImageCreator;
    ArrayList<FloatColoredPVector> oneAndOnlyArrayList = new ArrayList<FloatColoredPVector>();  //
    Logger myLogger;
    //float bigOne = ThreeDImageCreator.veryHighFloat;
    FloatRectangle boundingRectangle;

    public Frame getParentFrame() {
        return parentFrame;
    }
    public ThreeDImageCreator getParentThreeDImageCreator() {
        return parentThreeDImageCreator;
    }
    public Logger getMyLogger() {
        return myLogger;
    }

    public PointCloud(Frame myParentFrame){
        parentFrame = myParentFrame;
        parentThreeDImageCreator = parentFrame.getParentThreeDImageCreator();
        myLogger = LoggerFactory.getLogger(this.getClass());
        initialiseBoundingRectangle();
    }
    public void initialiseBoundingRectangle(){ //Only public as called from subclass. Better syntax for this?
        float bigOne = ThreeDImageCreator.veryHighFloat;
        boundingRectangle = new FloatRectangle().setMinMax(new PVector(bigOne, bigOne, bigOne),new PVector(-bigOne, -bigOne, -bigOne) );
    }
    public FloatRectangle getBoundingRectangle() {
        return boundingRectangle;
    }
    public void addColoredPVector(FloatColoredPVector aFloatColoredPVector){  //TODO - move checkExtents.. here so it's always called on adding points.  Then add it into any transforms, just by transforming the corners, not recalculating the whole thing.
        getOneAndOnlyArrayList().add(aFloatColoredPVector);  //This should work as retuns a pointer.
    }

//TODO - does this shifting work? Once shifted, how do we shift back?  Where is it called from??
    public void setExtentsFromCloudShiftedByPVector(PointCloud aPointCloud, PVector shiftPVector){ //This saves on processing the first time round.
        this.boundingRectangle = FloatRectangle.add(aPointCloud.boundingRectangle, shiftPVector);  //neat solution.
    }
    protected void checkExtentsForPVector(PVector myVector) {
        boundingRectangle.extendToInclude(myVector); //neat solution  //TODO - this does z as well. Do we use Z or can we speed up a bit?
    }

    public void setUpPGraphics(PGraphics myPGraphics){
        myPGraphics.strokeWeight(5f);
        myPGraphics.colorMode(PApplet.HSB);  //TODO dont think this is needed
        myPGraphics.stroke(parentFrame.getDefaultHue(), 255, 255);  //overwrite this later if draw real colors
    }

    public void draw(PGraphics myPGraphics, boolean useRealColors){
        setUpPGraphics(myPGraphics);
        for (FloatColoredPVector myFloatColoredPVector : getOneAndOnlyArrayList()) {
            if (useRealColors) {
                myPGraphics.stroke(myFloatColoredPVector.getColor());
            }
            myPGraphics.point(myFloatColoredPVector.x, myFloatColoredPVector.y, myFloatColoredPVector.z);
        }
    }

    public String toString(){
        //The full image has hundreds of thousands of points in the pointcloud.
       // return("ppppPrinting PointCloud for "+ this.getClass()+"\n"+ oneAndOnlyArrayList);
        return "A pointcloud with "+getOneAndOnlyArrayList().size()+ " points.";
    }

    public PVector getVectorFromMinToMaxRealWorldPoint(){
        return boundingRectangle.diagonal();  //neat
    }

    public PVector getVectorFromMinToMidRealWorldPoint(){
        return boundingRectangle.halfDiagonal();
    }

    public PVector getMidRealWorldPoint(){
        return boundingRectangle.midPoint();
    }
    public PVector getTopCentreRealWorldPoint(){  //TODO - behavior changed here from the very top point to the top centre of the overall pointCloud. Does it still work ok?
        return boundingRectangle.topCentre();
    }

    public ArrayList<FloatColoredPVector> getOneAndOnlyArrayList() {
        return oneAndOnlyArrayList;
    }

    /*public void setBoundingRectangle(){ //Always called when doing something else to minimise loops through the points.
        initialiseBoundingRectangle();
        for (ColoredPVector myColoredPVector: oneAndOnlyArrayList){
            checkExtentsForPVector(myColoredPVector);
        }
    }*/
}

