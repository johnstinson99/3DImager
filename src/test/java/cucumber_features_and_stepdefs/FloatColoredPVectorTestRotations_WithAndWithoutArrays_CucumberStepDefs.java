package cucumber_features_and_stepdefs;

import imager.domain.ThreeDObjects.FloatColoredPVector;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import imager.domain.Transforms.EuclidianTransform;
import imager.domain.Transforms.Rotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
/**
 * Created by John on 26/07/2015.
 */
public class FloatColoredPVectorTestRotations_WithAndWithoutArrays_CucumberStepDefs {
    FloatColoredPVector pointBeforeTransform;
    FloatColoredPVector pointAfterTransform;
    float tolerance;
    @Given("^a tolerance of ([0-9]|.)+$")
    public void aToleranceOf(float myTolerance){
        tolerance = myTolerance;
    }

    @And("^a 3d point (-?[0-9]+), (-?[0-9]+), (-?[0-9]+)$")
    public void a3dPoint (int myX, int myY, int myZ){
        pointBeforeTransform = new FloatColoredPVector(myX, myY, myZ);
    }

    @When("^I rotate it by (-?[0-9]+) degrees around the (x|y|z) axis$")
    public void rotateBy(int myAngle, char myAxis) throws Throwable {
        pointAfterTransform = pointBeforeTransform.rotateDegrees(myAngle, myAxis);
    }
    @When("^I rotate it using arrays by (-?[0-9]+) degrees around the (x|y|z) axis$")
    public void rotateUsingArraysBy(int myAngle, char myAxis) throws Throwable {
        //pointAfterTransform = ArrayTransformerTest.rotateByDegrees(pointBeforeTransform, myAngle, myAxis);
        EuclidianTransform myRotation = new Rotation(Math.toRadians(myAngle), myAxis);
        pointAfterTransform = pointBeforeTransform.createCopy();
        /*pointAfterTransform = */ myRotation.apply4by4TransformTo(pointAfterTransform);
    }

    @Then("^the new coordinates should be (-?[0-9]+), (-?[0-9]+), (-?[0-9]+)$")
    public void newPoint(int myX, int myY, int myZ) throws Throwable {
        assertThat(pointAfterTransform.x)
                .isCloseTo(myX, within(tolerance));
        assertThat(pointAfterTransform.y)
                .isCloseTo(myY, within(tolerance));
        assertThat(pointAfterTransform.z)
                .isCloseTo(myZ, within(tolerance));
    }

}
