package imager.domain.ThreeDObjects;

/**
 * Created by John on 31/05/2015.
 */
public class IntRectangle2D {
    IntPVector2D min, max;
    public IntPVector2D getMax() {
        return max;
    }

    public IntPVector2D getMin() {
        return min;
    }
    public IntRectangle2D(){
    }
    public IntRectangle2D setMinMax(IntPVector2D min, IntPVector2D max){
        this.min = min;
        this.max = max;
        return this;
    }
    public IntRectangle2D setMinAndSize(IntPVector2D min, IntPVector2D size){
        this.min = min;
        //this.size = size;
        this.max = IntPVector2D.add(min, size);
        return this;
    }
    public static IntRectangle2D add(IntRectangle2D rect1, IntPVector2D myIntPVector2D) {
        return new IntRectangle2D().setMinMax(IntPVector2D.add(rect1.getMin(), myIntPVector2D), IntPVector2D.add(rect1.getMax(), myIntPVector2D));
    }
    public IntPVector2D getDiagonal(){
        return IntPVector2D.sub(max, min);
    }
    public static IntRectangle2D overlap(IntRectangle2D rect0, IntRectangle2D rect1){
        IntPVector2D min0 = rect0.min;
        IntPVector2D max0 = rect0.max;
        IntPVector2D min1 = rect1.min;
        IntPVector2D max1 = rect1.max;
        if(!overlapExists(min0, max0, min1, max1)) return null;
        else {
            IntPVector2D minOverlap = new IntPVector2D();
            IntPVector2D maxOverlap = new IntPVector2D();
            minOverlap.intX = Math.max(min0.intX, min1.intX);
            minOverlap.intY = Math.max(min0.intY, min1.intY);
            maxOverlap.intX = Math.min(max0.intX, max1.intX);
            maxOverlap.intY = Math.min(max0.intY, max1.intY);
            return new IntRectangle2D().setMinMax(minOverlap, maxOverlap);
        }
    }
    public static IntRectangle2D sub(IntRectangle2D myRect, IntPVector2D myIntPVector2D){
        //Subtract myIntPVector from both max and min
        IntRectangle2D myResult = new IntRectangle2D();
        myResult.min = IntPVector2D.sub(myRect.min, myIntPVector2D);
        myResult.max = IntPVector2D.sub(myRect.max, myIntPVector2D);
        return myResult;
    }


    public IntRectangle2D createCopy(){
         return new IntRectangle2D().setMinMax(min.createCopy(),max.createCopy());
    }
    static boolean overlapExists(IntPVector2D min0, IntPVector2D max0, IntPVector2D min1, IntPVector2D max1){
        return!(min0.intX >max1.intX ||min1.intX >max0.intX ||min0.intY >max1.intY ||min1.intY >max0.intY);
    }
    public String toString(){
        return "Rectangle: ("+min.intX +", "+min.intY +"), ("+max.intX +", "+max.intY +")";
    }
}
