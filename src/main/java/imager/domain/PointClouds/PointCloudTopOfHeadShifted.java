package imager.domain.PointClouds;

/**
 * Created by John on 24/05/2015.
 */
public class PointCloudTopOfHeadShifted extends PointCloud {
    public PointCloudTopOfHeadShifted(Frame myParentFrame){
        super(myParentFrame);
        //setUpTopOfHeadShiftedRealWorldImageArrayListRemovingShoulders(); //moved to frame
    }

}
