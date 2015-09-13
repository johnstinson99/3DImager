package imager.domain.Transforms;

/**
 * Created by John on 01/09/2015.
 */
public class Identity extends EuclidianTransform {

    public Identity(){
        setUpIdentityMatrix();
        transformDescription = "Identity matrix";
    }

    private void setUpIdentityMatrix(){
        transformMatrix = FastPrintable4by4Matrix.getIdentityMatrix();
    }



}
