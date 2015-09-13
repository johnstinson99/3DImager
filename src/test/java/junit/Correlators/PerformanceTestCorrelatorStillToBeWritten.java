package junit.Correlators;

import junit.framework.TestCase;
import imager.domain.ThreeDImageCreator;
import org.junit.Test;
//import org.junit.experimental.categories.Category;
//import imager.zz.zzTestRunner;
//import static org.junit.Assert.assertTrue;


public class PerformanceTestCorrelatorStillToBeWritten extends TestCase {
    //ThreeDImageCreator myThreeDImageCreator = new ThreeDImageCreator();   //set up once here to use for tests

    @Test
    //@Category(Integration)
    public final void testCorrelationPerformance() {
        //This creates version1
        //ThreeDImageCreator.main(new String[]{});
        //Note - this brings up a blank screen while the test runs.

        //ThreeDImageCreator myThreeDImageCreator = new ThreeDImageCreator();

        //TODO - now find all instances and apply methods there.
        //In fact we could add a timestamp of creation and get the most recently created one.
        //Can't find any easy way of getting a pointer to the object created by main
        //As both main and runSketch are static methods and neither returns an instance.


        //Call main with a parameter that causes it to run once then terminate??
        //OR don't call main at all.  Create an instance of the class
        //BUT crucially, just call the various setup arguments without ever calling main.
        //UNFORTUNATELY, PApplet requires main to be called. So do we lose track of it?
        //OR are there alternative methods we

        //This creates a second version which includes a logger.  In fact this shouldn't work as main is static.
        //myThreeDImageCreator.main(new String[]{});  //Calling main on an instance actually calls the static method. Strange.
        //PApplet.runSketch(new String[]{}, myThreeDImageCreator);

        //The original version
        //myThreeDImageCreator.timeStartCorrelateGoing('l');
    }



}
