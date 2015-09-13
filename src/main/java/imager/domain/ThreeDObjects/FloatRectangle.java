package imager.domain.ThreeDObjects;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * Created by John on 31/05/2015.
 */
public class FloatRectangle {
    PVector min;

    public PVector getMin() {
        return min;
    }

    public PVector getMax() {
        return max;
    }

    PVector max;
    public FloatRectangle(){
    }
    public FloatRectangle setMinMax(PVector min, PVector max){
        this.min = min;
        this.max = max;
        return this;
    }
    public FloatRectangle setMinAndSize(PVector min, PVector size){
        this.min = min;
        //this.size = size;
        this.max = PVector.add(min, size);
        return this;
    }
    public static FloatRectangle overlap(FloatRectangle rect0, FloatRectangle rect1){
        PVector min0 = rect0.min;
        PVector max0 = rect0.max;
        PVector min1 = rect1.min;
        PVector max1 = rect1.max;
        if(!overlapExists(min0, max0, min1, max1)) return null;
        else {
            PVector minOverlap = new PVector();
            PVector maxOverlap = new PVector();
            minOverlap.x = Math.max(min0.x, min1.x);
            minOverlap.y = Math.max(min0.y, min1.y);
            maxOverlap.x = Math.min(max0.x, max1.x);
            maxOverlap.y = Math.min(max0.y, max1.y);
            return new FloatRectangle().setMinMax(minOverlap, maxOverlap);
        }
    }
    public static FloatRectangle sub(FloatRectangle myRect, PVector myPVector){
        //Subtract myPVector from both max and min
        FloatRectangle myResult = new FloatRectangle();
        myResult.min = PVector.sub(myRect.min, myPVector);
        myResult.max = PVector.sub(myRect.max, myPVector);
        return myResult;
    }

    public static FloatRectangle add(FloatRectangle myRect, PVector myPVector){
        //Subtract myPVector from both max and min
        FloatRectangle myResult = new FloatRectangle();
        myResult.min = PVector.add(myRect.min, myPVector);
        myResult.max = PVector.add(myRect.max, myPVector);
        return myResult;
    }

    public IntRectangle2D div(PVector myPVector){  //Could really use a PVector2D here, but would have to rewrite the base class for this.
        //TODO - do we need to add 1 to these numbers?
        IntRectangle2D resultIntRectangle2D = new IntRectangle2D().setMinMax(new IntPVector2D(), new IntPVector2D());
        resultIntRectangle2D.getMin().intX = PApplet.round(this.min.x / myPVector.x);  //TODO - test this rounds properly
        resultIntRectangle2D.getMin().intY = PApplet.round(this.min.y/myPVector.y);
        //resultIntRectangle2D.min.intZ = PApplet.round(this.min.z/myPVector.z);
        resultIntRectangle2D.getMax().intX = PApplet.round(this.max.x/myPVector.x);  //TODO - test this rounds properly
        resultIntRectangle2D.getMax().intY = PApplet.round(this.max.y/myPVector.y);
        //resultIntRectangle2D.max.intZ = PApplet.round(this.max.z/myPVector.z);
        return resultIntRectangle2D;
    }
    public void add(PVector myPVector){
        min.add(myPVector);
        max.add(myPVector);
    }

    public PVector diagonal(){
        return new PVector(max.x-min.x, max.y-min.y, max.z-min.z);
    }
    public PVector halfDiagonal(){
        return PVector.div(diagonal(), 2);
    }
    public PVector midPoint(){
        return PVector.add(min, halfDiagonal());
    }
    public PVector topCentre(){
        float myX = (max.x+min.x)/2;
        float myY = (max.y);
        float myZ = (max.z+min.z)/2;
        return new PVector(myX, myY, myZ);
    }
    //Can't copy a PVector. May be able to subclass it though.  Add this method if needed.
    /*public FloatRectangle createCopy(){
         return new FloatRectangle().setMinMax(min.createCopy(),max.createCopy());
    }*/
    static boolean overlapExists(PVector min0, PVector max0, PVector min1, PVector max1){
        return!(min0.x >max1.x ||min1.x >max0.x ||min0.y >max1.y ||min1.y >max0.y);
    }
    public String toString(){
        return "Rectangle: ("+min.x +", "+min.y +", "+ min.z+"), ("+max.x +", "+max.y +", "+max.z+")";
    }
    public void extendToInclude(PVector myVector){
            if (myVector.x < min.x) min.x = myVector.x;
            if (myVector.x > max.x) max.x = myVector.x;
            if (myVector.y < min.y) min.y = myVector.y;
            if (myVector.y > max.y) max.y = myVector.y;
            if (myVector.z < min.z) min.z = myVector.z;
            if (myVector.z > max.z) max.z = myVector.z;
    }
}
