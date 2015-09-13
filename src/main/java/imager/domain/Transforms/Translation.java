package imager.domain.Transforms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by John on 01/09/2015.
 */
public class Translation extends EuclidianTransform {
    /*float x; //for reference only
    float y;
    float z;*/

    Logger myLogger;

    public Translation(float myX, float myY, float myZ){
        /*x = myX;
        y = myY;
        z = myZ;*/

        myLogger = LoggerFactory.getLogger(this.getClass());
        setUpTranslationMatrix(myX, myY, myZ);
        transformDescription = ("Translation: x =  " + myX + ", y =  "+ myY + ", z = " + myZ);
    }

    /*public void times(FloatColoredPVector myColoredPVector){
        myColoredPVector.set(myColoredPVector.x + x, myColoredPVector.y + y, myColoredPVector.z + z);
        // return myColoredPVector;
    }
    public FloatColoredPVector applyTransformAndReturnNewFloatColoredPVector(FloatColoredPVector myColoredPVector){
        return new FloatColoredPVector(myColoredPVector.x + x, myColoredPVector.y + y, myColoredPVector.z + z);
    }*/

    private void setUpTranslationMatrix(float myX, float myY, float myZ){
        double[][] tmpArray = {
                {1, 0, 0, myX},
                {0, 1, 0, myY},
                {0, 0, 1, myZ},
                {0, 0, 0, 1}
        };
        transformMatrix = new FastPrintable4by4Matrix(tmpArray);
    }

   /*public String toString(){
        return ("Translation: x =  " + x + ", y =  "+ y + ", z = " + z +"\n"+ super.toString());
    }*/
}
