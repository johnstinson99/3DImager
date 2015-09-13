package imager.domain.Transforms;

import imager.domain.ThreeDObjects.FloatColoredPVector;

import java.util.ArrayList;

/**
 * Created by John on 01/09/2015.
 */
public class EuclidianTransform {
    /*public void  times(FloatColoredPVector myColoredPVector){}
    public FloatColoredPVector applyTransformAndReturnNewFloatColoredPVector(FloatColoredPVector myColoredPVector){
        return myColoredPVector;
    }*/

    String transformDescription;

    //MatrixPrintable transformMatrix;
    FastPrintable4by4Matrix transformMatrix;

    public FastPrintable4by4Matrix getTransformMatrix() {
        return transformMatrix;
    }
    public String getTransformDescription() {
        return transformDescription;
    }
    public void setTransformMatrix(FastPrintable4by4Matrix transformMatrix) {
        this.transformMatrix = transformMatrix;
    }
    public void setTransformDescription(String transformDescription) {
        this.transformDescription = transformDescription;
    }

    public void applyTransformTo(FloatColoredPVector myColoredPVector){
        //First we have to transform the point into array format.
        //double[][] arrayBeforeTransform = {{myColoredPVector.x}, {myColoredPVector.y}, {myColoredPVector.z}, {1}};
        //FastPrintable4by4Matrix matrixBeforeTransform = new FastPrintable4by4Matrix(arrayBeforeTransform);
        //Then apply times function to apply the transform
        transformMatrix.applyTransformTo(myColoredPVector); //matrixBeforeTransform.times(transformMatrix);
         //Then we have to apply the update back to the coloredPVector.
        //myColoredPVector.set((float) matrixAfterTransform.get(0, 0), (float) matrixAfterTransform.get(1, 0), (float) matrixAfterTransform.get(2, 0));
     }

    public void applyTransformTo(ArrayList<FloatColoredPVector> myArray){
        for(FloatColoredPVector myColoredPVector: myArray){
            apply4by4TransformTo(myColoredPVector);
        }
    }

    public void applyTransformTo(EuclidianTransform myTransform){
        myTransform.setTransformMatrix(
                this.transformMatrix.times(myTransform.getTransformMatrix())
        );
    }
    public void apply4by4TransformTo(FloatColoredPVector myColoredPVector){
        transformMatrix.applyTransformTo(myColoredPVector);
    }

    /*public FloatColoredPVector applyTransformAndReturnNewFloatColoredPVector(FloatColoredPVector myColoredPVector){
        double[][] arrayBeforeTransform = {{myColoredPVector.x}, {myColoredPVector.y}, {myColoredPVector.z}, {1}};
        MatrixPrintable matrixBeforeTransform = new MatrixPrintable(arrayBeforeTransform);
        MatrixPrintable matrixAfterTransform = transformMatrix.times(matrixBeforeTransform);
        return new FloatColoredPVector((float)matrixAfterTransform.get(0,0), (float)matrixAfterTransform.get(1,0), (float)matrixAfterTransform.get(2,0));
    }*/

    public String toString(){
        return transformDescription +"\n Matrix = \n"+ transformMatrix.toString();
        //("Transform matrix = "+ transformMatrix.toString());
    }
}
