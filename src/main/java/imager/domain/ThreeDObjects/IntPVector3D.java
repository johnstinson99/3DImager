package imager.domain.ThreeDObjects;

import processing.core.PVector;

/**
 * Created by John on 13/06/2015.
 */
public class IntPVector3D extends IntPVector2D {
    //private static final long serialVersionUID = -6717872085945400694L;
    public int intZ;

    public IntPVector3D() {
    }

    /*public IntPVector2D(int intX, int intY/*, int IntZ) {
        this.intX = intX;
        this.intY = intY;
        //this.intZ = IntZ;
    }*/

    public IntPVector3D(int intX, int intY, int intZ) {
        this.intX = intX;
        this.intY = intY;
        this.intZ = intZ;
    }

    public void set(int x, int y, int z) {
        this.intX = x;
        this.intY = y;
        this.intZ = z;
    }


    public IntPVector3D get() {
        return new IntPVector3D(this.intX, this.intY, this.intZ);
    }

    public void add(IntPVector3D v) {
        this.intX += v.intX;
        this.intY += v.intY;
        this.intZ += v.intZ;
    }

    public void add(int x, int y, int z) {
        this.intX += x;
        this.intY += y;
        this.intZ += z;
    }

    public static IntPVector3D add(IntPVector3D v1, IntPVector3D v2) {
        return add(v1, v2, null);
    }

    public static IntPVector3D add(IntPVector3D v1, IntPVector3D v2, IntPVector3D target) {
        if(target == null) {
            target = new IntPVector3D(v1.intX + v2.intX, v1.intY + v2.intY, v1.intZ + v2.intZ);
        } else {
            target.set(v1.intX + v2.intX, v1.intY + v2.intY, v1.intZ + v2.intZ);
        }

        return target;
    }

    public static IntPVector3D sub(IntPVector3D v1, IntPVector3D v2) {
        return sub(v1, v2, null);
    }

    public static IntPVector3D sub(IntPVector3D v1, IntPVector3D v2, IntPVector3D target) {
        if(target == null) {
            target = new IntPVector3D(v1.intX - v2.intX, v1.intY - v2.intY, v1.intZ - v2.intZ);
        } else {
            target.set(v1.intX - v2.intX, v1.intY - v2.intY);
        }

        return target;
    }

    public static PVector mult(IntPVector3D v, PVector q){ //Should really be in PVector, but I don't own the PVector source code.
        return new PVector(v.intX*q.x, v.intY*q.y, v.intZ*q.z);
    }
    public static IntPVector3D mult(IntPVector3D v, int i){
        return new IntPVector3D(v.intX*i, v.intY*i, v.intZ*i);
    }

  /*  public static IntPVector2D div3DRoundingMinusPointFiveUpToZero(PVector v, PVector size) {  //This is used for dividing real world size by the size of a ZPoint, to yield an integer size of a ZPointPlane in ZPoints.
        return new IntPVector2D((int)((v.x +0.0000001f)/ size.x), (int)((v.y+0.0000001f) / size.y));
    }*/

    public int dist(IntPVector3D v) {
        int dx = this.intX - v.intX;
        int dy = this.intY - v.intY;
        int dz = this.intZ - v.intZ;
        return (int)Math.sqrt((double)(dx * dx + dy * dy + dz * dz));
    }

    public static int dist(IntPVector3D v1, IntPVector3D v2) {
        int dx = v1.intX - v2.intX;
        int dy = v1.intY - v2.intY;
        int dz = v1.intZ - v2.intZ;
        return (int)Math.sqrt((double)(dx * dx + dy * dy + dz * dz));
    }

    public String toString() {
        return "[" + this.intX + ", " + this.intY + ", " + this.intZ + "]";
    }

    public boolean equals(Object obj) {
        if(!(obj instanceof IntPVector3D)) {
            return false;
        } else {
            IntPVector3D p = (IntPVector3D)obj;
            return this.intX == p.intX && this.intY == p.intY && this.intZ == p.intZ;
        }
    }

    static IntPVector3D max(IntPVector3D intPVector3D0, IntPVector3D intPVector3D1){
        int xMin = (intPVector3D0.intX > intPVector3D1.intX) ? intPVector3D0.intX : intPVector3D1.intX;
        int yMin = (intPVector3D0.intY > intPVector3D1.intY) ? intPVector3D0.intY : intPVector3D1.intY;
        int zMin = (intPVector3D0.intZ > intPVector3D1.intZ) ? intPVector3D0.intZ : intPVector3D1.intZ;
        return new IntPVector3D(xMin,yMin,zMin);
    }

    public IntPVector3D createCopy()  {
        return new IntPVector3D(this.intX, this.intY, this.intZ);
    }
    public void copyValuesTo(IntPVector3D myIntPVector3D){
        myIntPVector3D.intX = this.intX;
        myIntPVector3D.intY = this.intY;
        myIntPVector3D.intZ = this.intZ;
    }
}
