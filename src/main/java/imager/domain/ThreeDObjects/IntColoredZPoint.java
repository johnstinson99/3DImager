package imager.domain.ThreeDObjects;

import imager.domain.ThreeDImageCreator;

/**
 * Created by John on 31/05/2015.
 */
public class IntColoredZPoint extends IntPVector2D {

    int color;
    float hue;
    float saturation;
    float brightness;
    float zFloat;
    ThreeDImageCreator parentThreeDImageCreator;

    /*public ColoredPVector(ThreeDImageCreator myParent){
        parent = myParent;
        //setHSB();
    }*/
    public IntColoredZPoint(ThreeDImageCreator myParent, int x, int y, float z, int color){
        this.parentThreeDImageCreator = myParent;
        this.intX = x;
        this.intY = y;
        this.color = color;
        this.zFloat = z;
    }

    public float getZ(){
        return zFloat;
    }

    public void setColorAndHSB(int myColor){
        color = myColor;
        setHSBFromExistingColor();
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

    public void setHSBFromExistingColor(){
        //It should be more efficient to calculate these once here on setup, rather than calculating them from color each time they're needed.
        //Calclation of HSB was surprisingly to me taking 20-30% of the time in the correlation process.
        hue = parentThreeDImageCreator.hue(color);
        saturation = parentThreeDImageCreator.saturation(color);
        brightness = parentThreeDImageCreator.brightness(color);
    }
}
