package imager.domain.PointClouds;

import SimpleOpenNI.SimpleOpenNI;
import imager.domain.TestObjects.TestFrameData10Points;
import imager.domain.ThreeDObjects.FloatColoredPVector;
import processing.core.PVector;

/**
 * Created by John on 24/05/2015.
 */
public class PointCloudOriginalImage extends PointCloud {
    //PVector cameraOrigin = new PVector(0,0,0); //TODO - THIS IS ONLY USED TO CALC DISTANCE FROM THE ORIGIN - JUST USE Z INSTEAD - SHOULD BE FAR QUICKER.
    int maxDepthLimit = 1000; //used to remove the background.
    int maxHeightLimit = -250; //Cut off shoulders as they've rotated

    public PointCloudOriginalImage(Frame myParentFrame, boolean isTest){
        super(myParentFrame);
        setUpOriginalRealWorldImageArrayListAndExtents(isTest);
        myLogger.warn("NO OF POINTS = "+oneAndOnlyArrayList.size());
    }


    public void setUpOriginalRealWorldImageArrayListAndExtents(boolean isTest) {
        //Adjust these
        int originalRealWorldImageSteps = 1;   //Reduce resolution all the way through (not great as reduces resolution of final image)

        int depthWidth, depthHeight;//, halfDepthWidth, halfDepthHeight;
        depthWidth = getDepthWidth(isTest); //parentContext.depthWidth();
        depthHeight = getDepthHeight(isTest);//parentContext.depthHeight();
        int[] tempDepthMap = getDepthMap(isTest);  //.clone();  //Can do shallow clone here as it's an array of primitives.
        int[] tempColorMap = getColorMap(isTest); //.clone();
        PVector[] tempDepthMapRealWorld = getDepthMapRealWorld(isTest);
        for (int y = 0; y < depthHeight; y += originalRealWorldImageSteps) {
            for (int x = 0; x < depthWidth; x += originalRealWorldImageSteps) {
                int index = x + y * depthWidth;
                if (tempDepthMap[index] > 0) {
                    PVector myVector = tempDepthMapRealWorld[index];
                    getMyLogger().trace("index = " + index + "myVector = " + myVector);
                    if(Float.isNaN(myVector.x)) getMyLogger().error("fffffffffffound NaN in myVector");
                    else {
                        if (myVector.z < maxDepthLimit && myVector.y > maxHeightLimit) { //New - originally used this if (cameraOrigin.dist(myColoredPVector) < maxDepthLimit)  but assume this takes a bit of maths. CameraOrigin was set to 0,0,0
                            //myLogger.debug("myVector.z = "+myVector.z +" maxDepthLimit = "+maxDepthLimit);
                            FloatColoredPVector myFloatColoredPVector = new FloatColoredPVector(getParentThreeDImageCreator(), myVector, tempColorMap[index]);
                            addColoredPVector(myFloatColoredPVector);
                            checkExtentsForPVector(myFloatColoredPVector);  //Checking extents here saves another full iteration through all the points later.
                        }
                    }
                }
            }
        }
    }
    private SimpleOpenNI getParentContext(){
            return getParentThreeDImageCreator().getNi();
    }
    private int getFrameNo(){
        return getParentFrame().getFrameNo();
    }
    private int[] getDepthMap(boolean isTest){
        if(isTest) return TestFrameData10Points.getDepthMap(getFrameNo());
        else return getParentContext().depthMap();
    }
    private int[] getColorMap(boolean isTest){
        if(isTest) return TestFrameData10Points.getColorMap(getParentThreeDImageCreator(), getFrameNo());
        else return getParentContext().rgbImage().pixels;
    }
    private PVector[] getDepthMapRealWorld(boolean isTest){
        if(isTest) return TestFrameData10Points.getDepthMapRealWorld(getFrameNo());
        else return getParentContext().depthMapRealWorld();
    }
    public int getDepthWidth(boolean isTest){
        if(isTest) return TestFrameData10Points.getDepthWidth(getFrameNo());
        else return getParentContext().depthWidth();
    }
    public int getDepthHeight(boolean isTest){
        if(isTest) return TestFrameData10Points.getDepthHeight(getFrameNo());
        else return getParentContext().depthHeight();
    }
}
