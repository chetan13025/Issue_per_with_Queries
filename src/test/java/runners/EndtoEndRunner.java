package runners;

import org.junit.AfterClass;
import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import utils.EmailReportSender;
//import utils.EmailReportSender;
//import utils.ReportUtils;
import utils.ReportUtils;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = {
   		"src/test/resources/features/users.feature",
           "src/test/resources/features/userproject.feature",
           "src/test/resources/features/userstate.feature",
           "src/test/resources/features/issue.feature",
           "src/test/resources/features/issuecode.feature",
   		"src/test/resources/features/issuecollaborator.feature",
   		"src/test/resources/features/issuecomment.feature",
   		"src/test/resources/features/issuefeaturecatalog.feature",
   		"src/test/resources/features/issuefeaturegroup.feature",
   		"src/test/resources/features/issuefeaturestatistics.feature",
   		"src/test/resources/features/issuegroup.feature",
   		"src/test/resources/features/issueimpactarea.feature",
   		"src/test/resources/features/issuelinked.feature",
   		"src/test/resources/features/issuelog.feature",
   		"src/test/resources/features/issuemilestone.feature",
   		"src/test/resources/features/issuerootcause.feature",
   		"src/test/resources/features/issuesprint.feature",
   		"src/test/resources/features/issuestatus.feature",
   		"src/test/resources/features/issuestatusgraph.feature",
   		"src/test/resources/features/issuestep.feature",
   		"src/test/resources/features/issuetestcase.feature",
   		"src/test/resources/features/issuetype.feature",
   		"src/test/resources/features/issueversion.feature",
   		"src/test/resources/features/issuevote.feature",
   		"src/test/resources/features/lucyevent.feature",
   		"src/test/resources/features/version.feature",
   		"src/test/resources/features/module.feature",
   		"src/test/resources/features/occurrence.feature",
   		"src/test/resources/features/priority.feature",
//    		20-11
   		"src/test/resources/features/rootcause.feature",
   		"src/test/resources/features/sprintproposal.feature",
   		"src/test/resources/features/testcase.feature",
   		"src/test/resources/features/unittestresult.feature",
   		"src/test/resources/features/worklog.feature",
   		"src/test/resources/features/behavior.feature",
    		"src/test/resources/features/bulk.feature",
   		"src/test/resources/features/bugtype.feature",
    		//21-11
   		"src/test/resources/features/issuecomponent.feature",
   		"src/test/resources/features/component.feature",
   		"src/test/resources/features/componentimpactarea.feature",
   		"src/test/resources/features/dependencytype.feature",
   		"src/test/resources/features/environment.feature",
   		"src/test/resources/features/impactarea.feature",
    	    		
    },
    glue = {"Issue_persistence","validators"},
    plugin = {"pretty", "reports.ExtentCucumberListener"},
    monochrome = true
)

//@CucumberOptions(
//		features = "src/test/resources/features/End_to_End_social_comments_flow.feature",
//	    glue = {"social_comments"},
//    plugin = {
//    		"pretty",
//    		"html:target/Social-report.html",
//    		"json:target/cucumber.json",
//    		"io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
//    		
//    		},
//    monochrome = true
//)



public class EndtoEndRunner {
//	@AfterClass
//	public static void tearDown() {
//	    try {
//	        // You can change extension to .html or .pdf depending on your report
//	        String latest = ReportUtils.getLatestReportFile("target", ".html");
//	        System.out.println("Latest report file: " + latest);
//
//	        if (latest != null) {
//	            // change recipient(s) as needed (comma-separated accepted by InternetAddress.parse)
//	            EmailReportSender.sendReport("chetan.patil@dharbor.com", latest);
//	        } else {
//	            System.out.println("No report file found in target/ to send.");
//	        }
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	    }
//	}
	
	// with version
	
	// @AfterClass
	// public static void tearDown() {
	//     try {
	//         // Optional: ask your Extent listener to flush if available so HTML is finalized
	//         try {
	//             // if you implemented a flush method on your listener/manager, call it:
	//             // reports.ExtentCucumberListener.flushReports();
	//         } catch (Throwable ignored) {}

	//         // Print the resolved latest file (debug)
	//         String latest = ReportUtils.getLatestReportFile("target", ".html");
	//         System.out.println("Latest report file : " + latest);

	//         // Read version stored earlier by VersionSteps
	//         String version = System.getProperty("detected.version", "").trim();

	//         // Call EmailReportSender which will find the latest HTML and include version in subject/body
	//         EmailReportSender.sendReport("chetan.patil@dharbor.com", version);

	//         System.out.println("Email triggered with version=" + version);

	//     } catch (Exception e) {
	//         e.printStackTrace();
	//     }
	// }
	}
