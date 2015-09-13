package imager.domain.Transforms;

import imager.domain.ThreeDObjects.FloatColoredPVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by John on 03/09/2015.
 */
public class SeriesOfTransforms {
    ArrayList<EuclidianTransform> euclidianTransformList;
    Logger myLogger;

    public SeriesOfTransforms(){
        euclidianTransformList = new ArrayList<EuclidianTransform>();
        myLogger = LoggerFactory.getLogger(this.getClass());
    }

    public void add(EuclidianTransform myEuclidianTransform){
        euclidianTransformList.add(myEuclidianTransform);
    }
    public void applyTo(FloatColoredPVector myColoredPVector){
        for(EuclidianTransform myEuclidianTransform : euclidianTransformList){
            System.out.println("Applying transform\n"+myEuclidianTransform);
            myEuclidianTransform.applyTransformTo(myColoredPVector);
        }
    }
    public EuclidianTransform getCombinedTransform(){
        //Returns identity matrix if no transforms in list.
        /*if (euclidianTransformList.size()==0) {
            myLogger.error("Error - tried to combine transforms but no transforms set");
            return null;
        }
        if (euclidianTransformList.size()==1) return euclidianTransformList.get(1);
*/
        EuclidianTransform transform = new Identity();
        transform.setTransformDescription("Combined transformation made up of: "+ transform.getTransformDescription());
        //System.out.println("transform = "+ transform);
        for(EuclidianTransform nextTransform: euclidianTransformList){
            //TODO ADD THIS BACK!!!
            nextTransform.applyTransformTo(transform);
            transform.setTransformDescription(transform.getTransformDescription() + "\nFollowed by: " + nextTransform.getTransformDescription());
            //System.out.println("nextTransform = "+ nextTransform + ", transform = " + transform);
        }
        //System.out.println("Overall transform = "+transform);
        return transform;
    }

    public String toString(){
        String returnString = "Un-combined list of transforms containing:\n";
        for(EuclidianTransform myTransform: euclidianTransformList){
            returnString += myTransform;
        }
        returnString += "End of list";
        return returnString;
    }

}
