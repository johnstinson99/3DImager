package imager.domain.TestObjects;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import processing.core.PVector;

/**
 * Created by John on 25/05/2015.
 */
public class TestFrameData6Points extends SimpleOpenNI {  //Superclass just implements pixels(), which doesn't have a method in the original superclass.

    /*int[] depthMap;
    int[] colorMap;
    PVector[] depthMapRealWorld;
    int depthWidth, depthHeight;*/
    static final int noOfPoints = 6;

    public TestFrameData6Points(PApplet aParentThreeDImageCreator){
        super(aParentThreeDImageCreator);
    }

    public static int[] getDepthMap(int frameNo){
        int[] myDepthMap = {610,610,600,600,610,610};
        return myDepthMap;
    }

    public static int[] getColorMap(PApplet myParentPApplet, int frameNo){
        int myColor;
        int blue = myParentPApplet.color(50, 50, 100);
        int purple = myParentPApplet.color(0,50,100);
        int pink = myParentPApplet.color(100,50,100);
        int red = myParentPApplet.color(150,50,100);
        int[] myColorMap = new int[noOfPoints];  //_parent is set to an instance of PApplet in the SimpleOpenNI constructor.  In this case the PApplet is our own parentThreeDImageCreator.
        if(frameNo ==0) myColor = red; else myColor = blue;
        for (int i = 0; i<noOfPoints; i++) {
            myColorMap[i] = myColor;
        }
        return myColorMap;
    }

    public static PVector[] getDepthMapRealWorld(int frameNo){
        if (frameNo == 0) {
            PVector[] myDepthMapRealWorld = new PVector[noOfPoints];
            myDepthMapRealWorld[0] = new PVector(-10f, -10f, 610f);
            myDepthMapRealWorld[1] = new PVector(-10f, 10f, 610f);
            myDepthMapRealWorld[2] = new PVector(0f, -10f, 600f);
            myDepthMapRealWorld[3] = new PVector(0f, 10f, 600f);
            myDepthMapRealWorld[4] = new PVector(10f, -10f, 610f);
            myDepthMapRealWorld[5] = new PVector(10f, 10f, 610f);

            return myDepthMapRealWorld;
        } else {
            PVector myTranslationPVector = new PVector(25f,0f,0f);
            PVector translatedDepthMapRealWorld[];
            PVector frame0DepthMap[] = TestFrameData6Points.getDepthMapRealWorld(0);
            int mySize = frame0DepthMap.length;
            translatedDepthMapRealWorld = new PVector[mySize];
            for(int i = 0; i< mySize; i++){
                translatedDepthMapRealWorld[i] = PVector.add(myTranslationPVector, frame0DepthMap[i]);
            }
            return translatedDepthMapRealWorld;
        }
    }


    public static int getDepthWidth(int frameNo){ //The actual number of pixels in the image - not the real world distance.
        return 3;
    }
    public static int getDepthHeight(int frameNo){
        return 2;
    }

    /*
    protected void setup(){
        setDepthMap();
        setColorMap();
        setDepthMapRealWorld();
        setDepthWidth();
        setDepthHeight();
    }

    protected void setDepthMapRealWorld() {} //Implemented in subclass

    protected void setDepthMap() {
        depthMap = getFrame0DepthMap();
    }

    //protected void setColorMap(){
    //    colorMap = getFrame0ColorMap(_parent);
    //}

    protected void setDepthWidth(){
        depthWidth = getFrame0DepthWidth();
    }
    protected void setDepthHeight(){
        depthHeight = getFrame0DepthHeight();
    }

    public int[] getDepthMap() {
        return depthMap;
    }

    //public int[] getColorMap() {
    //    return colorMap;
    //}

    public PVector[] getDepthMapRealWorld() {
        return depthMapRealWorld;
    }

    public int getDepthWidth() {
        return depthWidth;
    }

    public int getDepthHeight() {
        return depthHeight;
    }
    */
}
