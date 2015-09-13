package imager.domain.ThreeDObjects;

//Based on Processing.org IntPVector

import processing.core.PVector;

public class IntPVector2D {
    //private static final long serialVersionUID = -6717872085945400694L;
    public int intX;
    public int intY;
    //public int intZ;  //This has to be private as overridden by a float in ColoredZPoint and it causes errors if you access intZ here directly by accident..  TODO -

    public IntPVector2D() {
    }

    /*public IntPVector2D(int intX, int intY/*, int IntZ) {
        this.intX = intX;
        this.intY = intY;
        //this.intZ = IntZ;
    }*/

    public IntPVector2D(int intX, int intY) {
        this.intX = intX;
        this.intY = intY;
        //this.intZ = 0;
    }

    /*public void set(int x, int y, int z) {
        this.intX = x;
        this.intY = y;
        //this.intZ = z;
    }*/

    public void set(int x, int y) {
        this.intX = x;
        this.intY = y;
    }

    public IntPVector2D get() {
        return new IntPVector2D(this.intX, this.intY/*, this.intZ*/);
    }

    /*public int[] get(int[] target) {
        if(target == null) {
            return new int[]{this.intX, this.intY, this.intZ};
        } else {
            if(target.length >= 2) {
                target[0] = this.intX;
                target[1] = this.intY;
            }

            if(target.length >= 3) {
                target[2] = this.intZ;
            }

            return target;
        }
    }*/

    public void add(IntPVector2D v) {
        this.intX += v.intX;
        this.intY += v.intY;
        /*this.intZ += v.intZ;*/
    }

    public void add(int x, int y, int z) {
        this.intX += x;
        this.intY += y;
        //this.intZ += z;
    }

    public static IntPVector2D add(IntPVector2D v1, IntPVector2D v2) {
        return add(v1, v2, null);
    }

    public static IntPVector2D add(IntPVector2D v1, IntPVector2D v2, IntPVector2D target) {
        if(target == null) {
            target = new IntPVector2D(v1.intX + v2.intX, v1.intY + v2.intY/*, v1.intZ + v2.intZ*/);
        } else {
            target.set(v1.intX + v2.intX, v1.intY + v2.intY/*, v1.intZ + v2.intZ*/);
        }

        return target;
    }

    public static IntPVector2D sub(IntPVector2D v1, IntPVector2D v2) {
        return sub(v1, v2, null);
    }

    public static IntPVector2D sub(IntPVector2D v1, IntPVector2D v2, IntPVector2D target) {
        if(target == null) {
            target = new IntPVector2D(v1.intX - v2.intX, v1.intY - v2.intY/*, v1.intZ - v2.intZ*/);
        } else {
            target.set(v1.intX - v2.intX, v1.intY - v2.intY/*, v1.intZ - v2.intZ*/);
        }

        return target;
    }

    public static PVector mult(IntPVector2D v, PVector q){ //Should really be in PVector, but I don't own the PVector source code.
        return new PVector(v.intX*q.x, v.intY*q.y, 0/*, v.intZ*q.z*/);
    }


    public static IntPVector2D div2DRoundingMinusPointFiveUpToZero(PVector v, PVector size) {  //This is used for dividing real world size by the size of a ZPoint, to yield an integer size of a ZPointPlane in ZPoints.
        return new IntPVector2D((int)((v.x +0.0000001f)/ size.x), (int)((v.y+0.0000001f) / size.y));
    }

    public int dist(IntPVector2D v) {
        int dx = this.intX - v.intX;
        int dy = this.intY - v.intY;
        /*int dz = this.intZ - v.intZ;*/
        return (int)Math.sqrt((double)(dx * dx + dy * dy /*+ dz * dz*/));
    }

    public static int dist(IntPVector2D v1, IntPVector2D v2) {
        int dx = v1.intX - v2.intX;
        int dy = v1.intY - v2.intY;
        //int dz = v1.intZ - v2.intZ;
        return (int)Math.sqrt((double)(dx * dx + dy * dy /*+ dz * dz*/));
    }

    public String toString() {
        return (""); //Too time consuming to print all these for real image.
        //return "[ " + this.intX + ", " + this.intY /*+ ", " + this.intZ */+ " ]";
    }

    public String toStringReally(){
        return "[" + this.intX + ", " + this.intY + "]";
    }

    public boolean equals(Object obj) {
        if(!(obj instanceof IntPVector2D)) {
            return false;
        } else {
            IntPVector2D p = (IntPVector2D)obj;
            return this.intX == p.intX && this.intY == p.intY /*&& this.intZ == p.intZ*/;
        }
    }

    static IntPVector2D max(IntPVector2D intPVector2D0, IntPVector2D intPVector2D1){
        int xMin = (intPVector2D0.intX > intPVector2D1.intX) ? intPVector2D0.intX : intPVector2D1.intX;
        int yMin = (intPVector2D0.intY > intPVector2D1.intY) ? intPVector2D0.intY : intPVector2D1.intY;
        //int zMin = (intPVector2D0.intZ > intPVector2D1.intZ) ? intPVector2D0.intZ : intPVector2D1.intZ;
        return new IntPVector2D(xMin,yMin/*,zMin*/);
    }

    public IntPVector2D createCopy()  {
        return new IntPVector2D(this.intX, this.intY/*, this.intZ*/);
    }
    public void copyValuesTo(IntPVector2D myIntPVector2D){
        myIntPVector2D.intX = this.intX;
        myIntPVector2D.intY = this.intY;
        /*myIntPVector2D.intZ = this.intZ;*/
    }
}

