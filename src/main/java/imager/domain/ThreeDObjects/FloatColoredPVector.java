package imager.domain.ThreeDObjects;

import imager.domain.ThreeDImageCreator;
import imager.domain.Transforms.Rotation;
import processing.core.*;


public class FloatColoredPVector extends PVector {
    private static final long serialVersionUID = 1L;

    int color;
    float hue;
    float saturation;
    float brightness;
    ThreeDImageCreator parentThreeDImageCreator;

    /*public ColoredPVector(ThreeDImageCreator myParent){
        parent = myParent;
        //setHSB();
    }*/
    public FloatColoredPVector(ThreeDImageCreator myParent, PVector myPVector, int myColor){ //This is the main constructor.
        super(myPVector.x, myPVector.y, myPVector.z);
        parentThreeDImageCreator = myParent;
        setColorAndHSB(myColor);
        //setHSB();
    }

    public FloatColoredPVector(ThreeDImageCreator myParent, PVector myPVector){  //This constructor is used in clone so as not to have to recalculate HSB.
        super(myPVector.x, myPVector.y, myPVector.z);
        parentThreeDImageCreator = myParent;
        //setHSB();
    }

    public FloatColoredPVector(float myX, float myY, float myZ) {
        //Only used in testing.  Also needs a parent to be set in real life
        x = myX;
        y = myY;
        z = myZ;
    }

    public float getX(){
        return x;
    }
    public float getY(){
        return y;
    }
    public float getZ(){
        return z;
    }
    public void setX(float myX){
        x = myX;
    }
    public void setY(float myY){
        y = myY;
    }
    public void setZ(float myZ){
        z = myZ;
    }

    public FloatColoredPVector() {
        //Only used in testing.  Also needs a parent to be set in real life
    }
    public void setColorAndHSB(int myColor){
        color = myColor;
        setHSB(myColor);
    }
    public int getColor(){
        return color;
    }
    public float getHue() {
        return hue;
    }
    public float getSaturation() {
        return saturation;
    }
    public float getBrightness() {
        return brightness;
    }
    private void setHSB(int myColor){
        //It should be more efficient to calculate these once here on setup, rather than calculating them from color each time they're needed.
        //Calclation of HSB was surprisingly to me taking 20-30% of the time in the correlation process.
        hue = parentThreeDImageCreator.hue(myColor);
        saturation = parentThreeDImageCreator.saturation(myColor);
        brightness = parentThreeDImageCreator.brightness(myColor);
    }
    public FloatColoredPVector createCopy()  {
        FloatColoredPVector newFloatColoredPVector = new FloatColoredPVector(parentThreeDImageCreator, this);
        newFloatColoredPVector.color = this.color;
        //Don't call setHSB here as it could be slow. HSB are already calculated so just copy them over.
        newFloatColoredPVector.hue = this.hue;
        newFloatColoredPVector.saturation = this.saturation;
        newFloatColoredPVector.brightness = this.brightness;
        return newFloatColoredPVector;
    }
    public String toString() {
        return (x + ", " + y + ", " + z + "\n");
    }
    public FloatColoredPVector rotateDegrees(float betaDegrees, char myAxis){
        rotate3dRadians(PApplet.radians(betaDegrees), myAxis);
        return this;
    }

    public FloatColoredPVector rotate3dRadians(float beta, char myAxis){
        Rotation myRotation = new Rotation(beta, myAxis);
        myRotation.applyTransformTo(this);
        return this;
    }

    public FloatColoredPVector rotate3dRadiansOld(float beta, char myAxis){
        float alpha;
        float r;
        float theta;
        float angleAddFactor;  // set to Pi (180 degrees).  as aTan only goes between +/-90 degreees and needs 180 to be added sometimes.
        switch (myAxis) {
            case 'x':
                if(y ==0) y = 0.00000001f;; //return the same point
                //TODO - check this
                //PApplet.println("UNTESTED CODE ROTATING ROUND X AXIS");
                alpha = PApplet.atan(z/y);
                if (y < 0) {
                    angleAddFactor = PApplet.PI;
                } else {
                    angleAddFactor = 0f;
                }
                //fudge worked out in Excel as atan only gives value between -90 and +90 degrees.
                alpha += angleAddFactor;
                r = PApplet.sqrt(PApplet.sq(y) + PApplet.sq(z));
                theta = beta + alpha;
                y = r * PApplet.cos(theta);
                z = r * PApplet.sin(theta);
                break;
            case 'y':
                if(x ==0) x=0.00000001f;

                alpha = PApplet.atan(z/x);
                if (x < 0) {
                    angleAddFactor = PApplet.PI;
                } else {
                    angleAddFactor = 0f;
                }
                //fudge worked out in Excel as atan only gives value between -90 and +90 degrees.
                alpha += angleAddFactor;
                r = PApplet.sqrt(PApplet.sq(x) + PApplet.sq(z));
                theta = beta + alpha;
                x = r * PApplet.cos(theta);
                z  = r * PApplet.sin(theta);
                //if (Float.isNaN(newX))
                //    myLogger.error("Rotaaaaate after NaN - alpha = " + alpha + "angleAddFactor = " + angleAddFactor + "r = " + r + " inputVector.intX = " + inputVector.intX);
                //inputVector.intX = newX;
                //inputVector.z = newY;

                break;
            case 'z':  //easiest case
                if(x ==0)x = 0.00000001f;

                alpha = PApplet.atan(y/x);
                if (x < 0) {
                    angleAddFactor = PApplet.PI;
                } else {
                    angleAddFactor = 0f;
                }
                //fudge worked out in Excel as atan only gives value between -90 and +90 degrees.
                alpha += angleAddFactor;
                r = PApplet.sqrt(PApplet.sq(x) + PApplet.sq(y));
                theta = beta + alpha;
                x = r * PApplet.cos(theta);
                y = r * PApplet.sin(theta);

                break;
        }
        return this;
    }

    public boolean roughlyEqualsPositionOfPVector(PVector myPVector){
        float tolerance = 0.5f;
        float minX = x-tolerance;
        float maxX = x+tolerance;
        float minY = y-tolerance;
        float maxY = y+tolerance;
        float minZ = z-tolerance;
        float maxZ = z+tolerance;
        boolean myResult = this.testBetween(minX, myPVector.x, maxX)&& this.testBetween(minY, myPVector.y, maxY) && this.testBetween(minZ, myPVector.z, maxZ);
        //System.out.println("Actual result = "+this+" Expected result = "+myPVector+" Match = "+myResult);
        return(myResult );
    }

   private boolean testBetween(float a, float b, float c){
       //tests b is between a and c
       //one of these two tests need to pass to ensure this is true
       boolean firstTest = (a<b && b<c);
       boolean secondTest = (a>b && b>c);
       //System.out.println("firstTest = "+firstTest+" secondTest = "+secondTest + " a = "+a+" b = "+b + "c = "+c);
       return firstTest || secondTest;
   }
}
