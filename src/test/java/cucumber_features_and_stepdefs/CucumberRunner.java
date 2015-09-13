package cucumber_features_and_stepdefs;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;
/**
 * John Stinson 26/07/2015
 * Runner for Cucumber
 */
@RunWith(Cucumber.class)
@CucumberOptions(plugin = "json:target/cucumber-report.json")
public class CucumberRunner {


}
