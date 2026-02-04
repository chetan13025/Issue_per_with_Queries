package DataCreate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.github.javafaker.Faker;

import io.opentelemetry.exporter.logging.SystemOutLogRecordExporter;

public class DataCreate {
	static Faker faker = new Faker();
	static Random random = new Random();

	public static String generateUserJson() {
	    String firstName = faker.name().firstName();
	    String lastName  = faker.name().lastName();
	    String userId    = "USR" + faker.number().digits(6);
	    int projectId    = faker.number().numberBetween(1000, 99999);

	    return "{\n" +
	            "  \"avatar\": \"" + faker.internet().avatar() + "\",\n" +
	            "  \"firstName\": \"" + firstName + "\",\n" +
	            "  \"fullName\": \"" + firstName + " " + lastName + "\",\n" +
	            "  \"lastName\": \"" + lastName + "\",\n" +
	            "  \"position\": \"" + faker.job().position() + "\",\n" +
	            "  \"projectId\": " + projectId + ",\n" +
	            "  \"role\": \"" + faker.job().title() + "\",\n" +
	            "  \"userId\": \"" + userId + "\",\n" +
	            "  \"userName\": \"" + faker.name().username() + "\"\n" +
	            "}";
	}


	public static String generateUserProjcetJson() {

		int projectId = faker.number().numberBetween(1000, 9999); // Random 4-digit Project ID
		String userId = "USR" + faker.number().digits(6); // Random User ID like USR123456
		String userName = faker.name().username(); // Random username

		return "{\n" + "  \"projectId\": " + projectId + ",\n" + "  \"userId\": \"" + userId + "\",\n"
				+ "  \"userName\": \"" + userName + "\"\n" + "}";

	}

	public static String generateUserStateJson() {

		String status = faker.options().option("Active", "Pending", "Inactive");
		String userId = "USR" + faker.number().digits(6);
		String userName = faker.name().username();
		String tokenExpirationDate = Instant.now().toString(); // ISO format like 1971-10-11T15:03:42.171Z

		return "{\n" + "  \"status\": \"" + status + "\",\n" + "  \"userId\": \"" + userId + "\",\n"
				+ "  \"userName\": \"" + userName + "\",\n" + "  \"tokenExpirationDate\": \"" + tokenExpirationDate
				+ "\"\n" + "}";
	}

	public static String generateIssueJson() {
		Faker faker = new Faker();

		java.util.function.Function<String, String> esc = s -> s == null ? ""
				: s.replace("\\", "\\\\").replace("\"", "\\\"");
		String projectId = String.valueOf(faker.number().numberBetween(3000, 9000));
		String priorityId = String.valueOf(faker.number().numberBetween(300, 305));
		String fixedAtSprintId = String.valueOf(faker.number().numberBetween(7000, 7100));
		String storyPoints = String.valueOf(faker.number().numberBetween(1, 8));
		String productId = String.valueOf(faker.number().numberBetween(8000, 9000));
		String qaEstimatedMinutes = String.valueOf(faker.number().numberBetween(60, 250));
		String sprintId = String.valueOf(faker.number().numberBetween(3500, 3600));
		String totalMinutesLogged = String.valueOf(faker.number().numberBetween(60, 200));
		String epicId = String.valueOf(faker.number().numberBetween(9900, 9999));
		String natureId = String.valueOf(faker.number().numberBetween(960, 980));
		String resolutionId = String.valueOf(faker.number().numberBetween(6100, 6200));
		String occurrenceId = String.valueOf(faker.number().numberBetween(5000, 5100));
		String issueTypeId = String.valueOf(faker.number().numberBetween(5000, 5100));
		String environmentId = String.valueOf(faker.number().numberBetween(9100, 9200));
		String moduleCustomId = String.valueOf(faker.number().numberBetween(6200, 6300));
		String defaultAttachmentId = String.valueOf(faker.number().numberBetween(4000, 4050));
		String issueStatusId = String.valueOf(faker.number().numberBetween(2800, 2900));
		String boardColumnId = String.valueOf(faker.number().numberBetween(5000, 5100));
		String teamId = String.valueOf(faker.number().numberBetween(6000, 6100));
		String devEstimatedMinutes = String.valueOf(faker.number().numberBetween(60, 180));
		String bugTypeId = String.valueOf(faker.number().numberBetween(3120, 3160));

		// strings randomized
		String createUserId = "USR_" + faker.number().digits(4);
		String createUserName = faker.name().fullName();
		String reporterName = faker.name().fullName();
		String updateUserId = "USR_" + faker.number().digits(4);
		String updateUserName = faker.name().fullName();
		String qaAssignedId = "QA_" + faker.number().digits(3);
		String devAssignedId = "DEV_" + faker.number().digits(3);
		String reporterId = "USR_" + faker.number().digits(4);
		int moduleId = faker.number().numberBetween(1000, 9999);
		String moduleLabel = faker.company().bs().replaceAll("[^a-zA-Z ]", "");
		String productName = faker.commerce().productName();
		String productShortName = productName.replaceAll("\\s+", "");
		if (productShortName.length() > 16)
			productShortName = productShortName.substring(0, 16);

		String moduleCustomLabel = faker.commerce().department();
		String sprintLabel = "Sprint-" + faker.number().numberBetween(1, 30);
		String fixedAtSprintLabel = "Sprint-" + faker.number().numberBetween(1, 30);
		String bugTypeLabel = faker.options().option("UI Bug", "API Bug", "Functional Bug", "Performance Bug");
		String priorityLabel = faker.options().option("Low", "Medium", "High", "Critical");
		String issueStatusLabel = faker.options().option("Open", "In Progress", "Closed", "Resolved", "Reopen",
				"Not a Bug", "QA Tested", "QA Ready", "QA Testing", "QA_Done");
		String projectName = faker.company().buzzword() + " " + faker.company().industry();
		String workflowId = "WF_" + faker.number().numberBetween(100, 400);
		String code = "BUG-" + faker.number().numberBetween(3000, 6000);
		String title = faker.lorem().sentence(5).replace(".", "");
		String description = faker.lorem().sentence(10);
		String epicLabel = faker.commerce().material() + " " + faker.commerce().productName();
		String natureLabel = faker.options().option("UI", "Backend", "API", "Performance", "Security");
		String resolutionLabel = faker.options().option("Fixed", "Won't Fix", "Duplicate", "Cannot Reproduce");
		String issueTypeLabel = faker.options().option("Bug", "Story", "Task", "Spike", "Sub-task",
				"Enhancement Request");
		String occurrenceLabel = faker.options().option("Mobile", "Web", "Backend", "API");
		String environmentLabel = faker.options().option("Prod", "Staging", "QA", "Dev");
		String devAssignedLabel = faker.options().option("UI Team", "Backend Team", "API Team", "Platform Team");
		String qaAssignedLabel = faker.options().option("QA UI", "QA API", "QA Mobile", "QA Platform");

		// dates randomized (ISO-8601 Z)
		String dueDate = Instant.now().plus(faker.number().numberBetween(7, 90), ChronoUnit.DAYS).toString();
		String devCompletedDate = Instant.now().plus(faker.number().numberBetween(1, 45), ChronoUnit.DAYS).toString();
		String inSprintDate = Instant.now().minus(faker.number().numberBetween(5, 40), ChronoUnit.DAYS).toString();
		String qaAssignedDate = Instant.now().plus(faker.number().numberBetween(1, 20), ChronoUnit.DAYS).toString();
		String updateDate = Instant.now().plus(faker.number().numberBetween(1, 10), ChronoUnit.DAYS).toString();
		String resolutionDate = Instant.now().plus(faker.number().numberBetween(5, 30), ChronoUnit.DAYS).toString();
		String qaCompletedDate = Instant.now().plus(faker.number().numberBetween(10, 50), ChronoUnit.DAYS).toString();
		String devAssignedDate = Instant.now().minus(faker.number().numberBetween(3, 20), ChronoUnit.DAYS).toString();
		String reporterDate = Instant.now().minus(faker.number().numberBetween(10, 30), ChronoUnit.DAYS).toString();

		// boolean stays boolean in JSON
		String isReproducedStep = String.valueOf(faker.bool().bool());

		return "{\n" + "  \"projectId\": " + projectId + ",\n" + "  \"createUserId\": \"" + esc.apply(createUserId)
				+ "\",\n" + "  \"dueDate\": \"" + esc.apply(dueDate) + "\",\n" + "  \"createUserName\": \""
				+ esc.apply(createUserName) + "\",\n" + "  \"fixVersions\": \""
				+ esc.apply("v" + faker.number().numberBetween(1, 9) + ".0.0") + "\",\n" + "  \"reporterName\": \""
				+ esc.apply(reporterName) + "\",\n" + "  \"productName\": \"" + esc.apply(productName) + "\",\n"
				+ "  \"priorityId\": " + priorityId + ",\n" + "  \"moduleCustomLabel\": \""
				+ esc.apply(moduleCustomLabel) + "\",\n" + "  \"sprintLabel\": \"" + esc.apply(sprintLabel) + "\",\n"
				+ "  \"devCompletedDate\": \"" + esc.apply(devCompletedDate) + "\",\n" + "  \"fixedAtSprintId\": "
				+ fixedAtSprintId + ",\n" + "  \"bugTypeLabel\": \"" + esc.apply(bugTypeLabel) + "\",\n"
				+ "  \"storyPoints\": " + storyPoints + ",\n" + "  \"priorityLabel\": \"" + esc.apply(priorityLabel)
				+ "\",\n" + "  \"inSprintDate\": \"" + esc.apply(inSprintDate) + "\",\n" + "  \"productId\": "
				+ productId + ",\n" + "  \"updateUserId\": \"" + esc.apply(updateUserId) + "\",\n"
				+ "  \"updateUserName\": \"" + esc.apply(updateUserName) + "\",\n" + "  \"qaEstimatedMinutes\": "
				+ qaEstimatedMinutes + ",\n" + "  \"productShortName\": \"" + esc.apply(productShortName) + "\",\n"
				+ "  \"sprintId\": " + sprintId + ",\n" + "  \"totalMinutesLogged\": " + totalMinutesLogged + ",\n"
				+ "  \"qaAssignedId\": \"" + esc.apply(qaAssignedId) + "\",\n" + "  \"epicId\": " + epicId + ",\n"
				+ "  \"natureId\": " + natureId + ",\n" + "  \"issueStatusLabel\": \"" + esc.apply(issueStatusLabel)
				+ "\",\n" + "  \"projectName\": \"" + esc.apply(projectName) + "\",\n" + "  \"workflowId\": \""
				+ esc.apply(workflowId) + "\",\n" + "  \"qaAssignedDate\": \"" + esc.apply(qaAssignedDate) + "\",\n"
				+ "  \"resolutionId\": " + resolutionId + ",\n" + "  \"updateDate\": \"" + esc.apply(updateDate)
				+ "\",\n" + "  \"code\": \"" + esc.apply(code) + "\",\n" + "  \"resolutionDate\": \""
				+ esc.apply(resolutionDate) + "\",\n" + "  \"fixedAtSprintLabel\": \"" + esc.apply(fixedAtSprintLabel)
				+ "\",\n" + "  \"description\": \"" + esc.apply(description) + "\",\n" + "  \"occurrenceId\": "
				+ occurrenceId + ",\n" + "  \"title\": \"" + esc.apply(title) + "\",\n" + "  \"natureLabel\": \""
				+ esc.apply(natureLabel) + "\",\n" + "  \"resolutionLabel\": \"" + esc.apply(resolutionLabel) + "\",\n"
				+ "  \"qaCompletedDate\": \"" + esc.apply(qaCompletedDate) + "\",\n" + "  \"epicLabel\": \""
				+ esc.apply(epicLabel) + "\",\n" + "  \"issueTypeId\": " + issueTypeId + ",\n"
				+ "  \"devAssignedDate\": \"" + esc.apply(devAssignedDate) + "\",\n" + "  \"environmentId\": "
				+ environmentId + ",\n" + "  \"reporterDate\": \"" + esc.apply(reporterDate) + "\",\n"
				+ "  \"isReproducedStep\": " + isReproducedStep + ",\n" + "  \"devAssignedId\": \""
				+ esc.apply(devAssignedId) + "\",\n" + "  \"moduleCustomId\": " + moduleCustomId + ",\n"
				+ "  \"devAssignedLabel\": \"" + esc.apply(devAssignedLabel) + "\",\n" + "  \"defaultAttachmentId\": "
				+ defaultAttachmentId + ",\n" + "  \"qaAssignedLabel\": \"" + esc.apply(qaAssignedLabel) + "\",\n"
				+ "  \"issueStatusId\": " + issueStatusId + ",\n" + "  \"boardColumnId\": " + boardColumnId + ",\n"
				+ "  \"issueTypeLabel\": \"" + esc.apply(issueTypeLabel) + "\",\n" + "  \"occurrenceLabel\": \""
				+ esc.apply(occurrenceLabel) + "\",\n" + "  \"environmentLabel\": \"" + esc.apply(environmentLabel)
				+ "\",\n" + "  \"teamId\": " + teamId + ",\n" + "  \"devEstimatedMinutes\": " + devEstimatedMinutes
				+ ",\n" + "  \"reporterId\": \"" + esc.apply(reporterId) + "\",\n" + "  \"bugTypeId\": " + bugTypeId
				+ ",\n" + "  \"affectedVersions\": \""
				+ esc.apply("v" + faker.number().numberBetween(1, 9) + "." + faker.number().numberBetween(0, 9) + ".0")
				+ "\",\n" + "  \"moduleId\": " + moduleId + ",\n" + "  \"moduleLabel\": \"" + moduleLabel + "\"\n"
				+ "}";

	}

	public static String generateIssueCodeJson() {
		String projectCode = "PRJ_" + faker.number().digits(4);

		return "{\n" + "  \"projectCode\": \"" + projectCode + "\"\n" + "}";

	}

	public static String generateIssueCollaboratorsJson() {

		int issueId = faker.number().numberBetween(100000, 999900);
		String userId = "USR_" + faker.number().numberBetween(10000, 99900);

		return "{\n" + "  \"issueId\": " + issueId + ",\n" + "  \"userId\": \"" + userId + "\"\n" + "}";

	}

	public static String generateIssueCommentsJson() {

		int commentId = faker.number().numberBetween(1000, 9999);
		int issueId = faker.number().numberBetween(1000, 9999);
		String type = faker.options().option("Info", "Update", "Log", "History");
		String currentState = faker.options().option("Open", "In Progress", "Closed", "Reopened");
		String previousState = faker.options().option("Open", "In Progress", "Closed", "Reopened");

		return "{\n" + "  \"commentId\": " + commentId + ",\n" + "  \"issueId\": " + issueId + ",\n" + "  \"type\": \""
				+ type + "\",\n" + "  \"currentState\": \"" + currentState + "\",\n" + "  \"previousState\": \""
				+ previousState + "\"\n" + "}";

	}

	public static String generateIssueComponentJson() {

		int componentId = faker.number().numberBetween(1000, 9999);
		int issueId = faker.number().numberBetween(1000, 9999);
		int projectId = faker.number().numberBetween(1000, 9999);
		String label = faker.lorem().word(); // random string

		return "{\n" + "  \"componentId\": " + componentId + ",\n" + "  \"issueId\": " + issueId + ",\n"
				+ "  \"projectId\": " + projectId + ",\n" + "  \"label\": \"" + label + "\"\n" + "}";

	}

	public static String generateIssueFeatureCatalogJson() {

		String fieldName = faker.lorem().word();
		String ruleName = faker.lorem().word();
		int projectId = faker.number().numberBetween(1000, 9999);
		String fieldValue = faker.lorem().word();

		return "{\n" + "  \"fieldName\": \"" + fieldName + "\",\n" + "  \"ruleName\": \"" + ruleName + "\",\n"
				+ "  \"projectId\": " + projectId + ",\n" + "  \"fieldValue\": \"" + fieldValue + "\"\n" + "}";

	}

	public static String generateIssueFeatureGroupJson() {

		String criteriaGroup = faker.lorem().word();
		String label = faker.lorem().word();

		return "{\n" + "  \"criteriaGroup\": \"" + criteriaGroup + "\",\n" + "  \"label\": \"" + label + "\"\n" + "}";

	}

	public static String generateIssueFeatureStatisticsJson() {

		int quantitySprints = faker.number().numberBetween(1, 100);
		int issueId = faker.number().numberBetween(1000, 9999);
		String createUserId = "USR_" + faker.number().digits(4);
		String updateUserId = "USR_" + faker.number().digits(4);
		int progressPercentage = faker.number().numberBetween(0, 100);
		int projectId = faker.number().numberBetween(8000, 9000);
		int totalLinkedIssues = faker.number().numberBetween(10, 100);
		int totalClosedIssues = faker.number().numberBetween(5, totalLinkedIssues);

		return "{\n" + "  \"quantitySprints\": " + quantitySprints + ",\n" + "  \"issueId\": " + issueId + ",\n"
				+ "  \"createUserId\": \"" + createUserId + "\",\n" + "  \"updateUserId\": \"" + updateUserId + "\",\n"
				+ "  \"progressPercentage\": " + progressPercentage + ",\n" + "  \"projectId\": " + projectId + ",\n"
				+ "  \"totalLinkedIssues\": " + totalLinkedIssues + ",\n" + "  \"totalClosedIssues\": "
				+ totalClosedIssues + "\n" + "}";

	}

	public static String generateIssueGroupJson() {
		// Generate random realistic values
		String criteriaGroup = faker.commerce().department();
		String section = faker.commerce().material();
		String label = faker.company().buzzword();
		String iconUrl = "https://example.com/icons/" + faker.internet().slug() + ".png";
		int position = faker.number().numberBetween(1000, 9999);
		int projectId = faker.number().numberBetween(3000, 5000);

		// Build JSON manually
		return "{\n" + "  \"criteriaGroup\": \"" + criteriaGroup + "\",\n" + "  \"section\": \"" + section + "\",\n"
				+ "  \"label\": \"" + label + "\",\n" + "  \"iconUrl\": \"" + iconUrl + "\",\n" + "  \"position\": "
				+ position + ",\n" + "  \"projectId\": " + projectId + "\n" + "}";
	}

	public static String generateIssueImpactAreaJson() {

		// 🎲 Generate dynamic integer-like IDs using Faker
		int impactAreaId = faker.number().numberBetween(3000, 5000);
		int issueId = faker.number().numberBetween(100, 999);
		int projectId = faker.number().numberBetween(5000, 7000);

		// 🎯 Generate random label (realistic text)
		String label = faker.company().industry(); // e.g., "E-commerce", "Information Technology"

		// 🧱 Build JSON
		return "{\n" + "  \"impactAreaId\": " + impactAreaId + ",\n" + "  \"issueId\": " + issueId + ",\n"
				+ "  \"projectId\": " + projectId + ",\n" + "  \"label\": \"" + label + "\"\n" + "}";

	}

	public static String generateIssueLinkedJson() {

		int issueId = faker.number().numberBetween(2000, 3000);
		int projectId = faker.number().numberBetween(5000, 7000);
		int relatedIssueId = faker.number().numberBetween(7000, 8000);
		String inverseDependencyType = faker.options().option("Blocks", "Relates To", "Depends On", "Duplicates");
		String dependencyType = faker.options().option("Soft", "Hard", "Critical", "Major");
		String linkedDate = Instant.now().minus(faker.number().numberBetween(100, 1000), ChronoUnit.DAYS).toString();
		String unlinkedDate = faker.date().past(15000, java.util.concurrent.TimeUnit.DAYS).toInstant().toString();
		int position = faker.number().numberBetween(8000, 9000);
		String relationship = faker.options().option("Parent", "Child", "Peer", "Linked");
		int dependencyTypeId = faker.number().numberBetween(1000, 1100);

		return "{\n" + "  \"issueId\": " + issueId + ",\n" + "  \"projectId\": " + projectId + ",\n"
				+ "  \"relatedIssueId\": " + relatedIssueId + ",\n" + "  \"inverseDependencyType\": \""
				+ inverseDependencyType + "\",\n" + "  \"dependencyType\": \"" + dependencyType + "\",\n" +
//                "  \"linkedDate\": \"" + linkedDate + "\",\n" +
                "  \"unlinkedDate\": \"" + unlinkedDate + "\",\n" +
				"  \"position\": " + position + ",\n" + "  \"relationship\": \"" + relationship + "\",\n"
				+ "  \"dependencyTypeId\": " + dependencyTypeId + "\n" + "}";

	}

	public static String generateIssueLogJson() {
		int issueId = faker.number().numberBetween(2000, 3000);
		int projectId = faker.number().numberBetween(1000, 2000);
		int targetId = faker.number().numberBetween(7000, 8000);
		String newValue = faker.options().option("In Review", "Completed", "Blocked", "To Do");
		int targetNewId = faker.number().numberBetween(100, 200);
		String targetType = faker.options().option("Status", "Priority", "Type", "Severity");
		String eventType = faker.options().option("IssueCreated", "IssueUpdated", "StatusChanged", "CommentAdded");
		String userId = "USR_" + faker.number().numberBetween(100, 999);
		boolean isViewed = faker.bool().bool();
		String issueType = faker.options().option("Bug", "Story", "Task", "Epic");
		int sprintId = faker.number().numberBetween(2000, 3000);
		String qaAssignedId = "QA_" + faker.number().numberBetween(100, 999);
		String devAssignedId = "DEV_" + faker.number().numberBetween(100, 999);
		String[] comments = { "Issue moved successfully after review approval.",
				"QA verified and marked the issue as ready for release.",
				"Fix deployed and validated in staging environment.",
				"Code changes merged to main branch after approval.",
				"Reopened issue for further investigation by development.",
				"QA found inconsistencies; moved issue back to testing.",
				"All test cases passed, moving issue to Done.",
				"Hotfix applied to production, awaiting final verification.",
				"Dependency resolved, progress resumed on this task.",
				"Reassigned to new developer due to resource reallocation.",
				"Patch deployed successfully; closing the issue.",
				"User feedback addressed and issue closed after validation.",
				"Work in progress; awaiting database migration results.",
				"Reviewed pull request and approved for merge.",
				"Sprint scope updated; issue rescheduled for next sprint.",
				"Build failed due to missing dependencies; fixing in progress.",
				"Code review comments resolved; ready for QA testing.",
				"Defect reoccurred; reopening for further analysis.",
				"Documentation updated along with issue resolution.",
				"Environment sync completed, re-testing in progress.", "Retesting done, no further issues found.",
				"Performance improvements verified successfully.", "UI alignment issue fixed and verified.",
				"Feature enhancement completed and deployed.", "Backend validation issue resolved and tested.",
				"Database schema updated; retesting initiated." };
		String comment = faker.options().option(comments);
		String oldValue = faker.options().option("In Progress", "Open", "Testing", "Blocked");

		return "{\n" + "  \"issueId\": " + issueId + ",\n" + "  \"projectId\": " + projectId + ",\n"
				+ "  \"targetId\": " + targetId + ",\n" + "  \"newValue\": \"" + newValue + "\",\n"
				+ "  \"targetNewId\": " + targetNewId + ",\n" + "  \"targetType\": \"" + targetType + "\",\n"
				+ "  \"eventType\": \"" + eventType + "\",\n" + "  \"userId\": \"" + userId + "\",\n"
				+ "  \"isViewed\": " + isViewed + ",\n" + "  \"issueType\": \"" + issueType + "\",\n"
				+ "  \"sprintId\": " + sprintId + ",\n" + "  \"qaAssignedId\": \"" + qaAssignedId + "\",\n"
				+ "  \"devAssignedId\": \"" + devAssignedId + "\",\n" + "  \"comment\": \"" + comment + "\",\n"
				+ "  \"oldValue\": \"" + oldValue + "\"\n" + "}";

	}

	public static String generateIssueMilestoneJson() {
		int projectId = faker.number().numberBetween(5000, 7000);
		boolean isRequired = faker.bool().bool();
		String milestone = faker.options().option("Design Phase", "Development Phase", "Testing Phase",
				"Deployment Phase");
		String submilestone = faker.options().option("UI Setup", "Backend Integration", "QA Verification",
				"UAT Approval", "Final Release");
		String color = faker.color().name();
		int position = faker.number().numberBetween(1000, 5000);
		String eventType = faker.options().option("IssueCreated", "IssueUpdated", "StatusChanged", "CommentAdded",
				"MilestoneAchieved");
		String uiReference = faker.app().name() + "-" + faker.number().digits(3);
		int issueStatusId = faker.number().numberBetween(3000, 4000);
		boolean isTickeable = faker.bool().bool();

		return "{\n" + "  \"projectId\": " + projectId + ",\n" + "  \"isRequired\": " + isRequired + ",\n"
				+ "  \"milestone\": \"" + milestone + "\",\n" + "  \"submilestone\": \"" + submilestone + "\",\n"
				+ "  \"color\": \"" + color + "\",\n" + "  \"position\": " + position + ",\n" + "  \"eventType\": \""
				+ eventType + "\",\n" + "  \"UiReference\": \"" + uiReference + "\",\n" + "  \"issueStatusId\": "
				+ issueStatusId + ",\n" + "  \"isTickeable\": " + isTickeable + "\n" + "}";

	}

	public static String generateIssueRootCauseJson() {
		int issueId = faker.number().numberBetween(2000, 3000);
		int projectId = faker.number().numberBetween(6000, 7000);
		int rootCauseId = faker.number().numberBetween(7000, 8000);

		// 🔹 Realistic comment messages
		String[] commentsList = { "Incorrect calculations in report due to rounding errors.",
				"Data mismatch caused by inconsistent schema mapping.",
				"Performance degradation observed under heavy load.",
				"Validation rules missing for certain input fields.",
				"Configuration issue found in staging environment.",
				"Integration failure due to outdated API endpoint.",
				"Report generation failed because of null data entries.",
				"Incorrect status transition triggered by workflow error.",
				"Memory leak observed during long test executions.",
				"Calculation discrepancy due to floating-point precision." };
		String comments = faker.options().option(commentsList);

		// 🔹 Realistic corrective actions
		String[] correctiveActionsList = { "Update formula and add unit tests for accuracy.",
				"Refactor schema mapping logic and revalidate data model.",
				"Optimize database query and add caching mechanism.",
				"Add missing validation rules and input sanitization.",
				"Update configuration files and redeploy to staging.",
				"Replace deprecated API endpoint with latest version.",
				"Add null checks and handle missing data gracefully.",
				"Fix workflow condition and update transition logic.",
				"Implement memory cleanup and resource monitoring.",
				"Use BigDecimal for precise financial calculations." };
		String correctiveActions = faker.options().option(correctiveActionsList);

		return "{\n" + "  \"issueId\": " + issueId + ",\n" + "  \"projectId\": " + projectId + ",\n"
				+ "  \"rootCauseId\": " + rootCauseId + ",\n" + "  \"comments\": \"" + comments + "\",\n"
				+ "  \"correctiveActions\": \"" + correctiveActions + "\"\n" + "}";

	}

	public static String generateIssueSprintJson() {

		int sprintId = faker.number().numberBetween(1000, 9999);
		int issueId = faker.number().numberBetween(1000, 9999);
//	        String startSprintDate = faker.date().birthday().toInstant().toString();
		String sprintName = faker.lorem().word();
//	        String endSprintDate = faker.date().birthday().toInstant().toString();
		String type = faker.job().field();
		int projectId = faker.number().numberBetween(1000, 9999);

//	        String startSprintDate = Instant.now().minus(faker.number().numberBetween(100, 1000), ChronoUnit.DAYS).toString();
//	        String endSprintDate = Instant.now().minus(faker.number().numberBetween(100, 1000), ChronoUnit.DAYS).toString();

		LocalDateTime startSprintDate = LocalDateTime.now().minusDays(random.nextInt(30)) // up to 30 days ago
				.minusHours(random.nextInt(12));

		LocalDateTime endSprintDate = startSprintDate.plusDays(random.nextInt(19) + 3); // gap of 3–21 days

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

		return "{\n" + "  \"sprintId\": " + sprintId + ",\n" + "  \"issueId\": " + issueId + ",\n"
				+ "  \"startSprintDate\": \"" + startSprintDate.format(formatter) + "\",\n" + "  \"sprintName\": \""
				+ sprintName + "\",\n" + "  \"endSprintDate\": \"" + endSprintDate.format(formatter) + "\",\n"
				+ "  \"type\": \"" + type + "\",\n" + "  \"projectId\": " + projectId + "\n" + "}";

		
	}

	public static String generateIssueStatusGraphson() {

		int issueTypeId = faker.number().numberBetween(100, 9999);
		int statusEnd = faker.number().numberBetween(1000, 9999);
		int statusStart = faker.number().numberBetween(1000, 9999);
		String type = faker.job().field();
		int projectId = faker.number().numberBetween(1000, 9999);
		String workflowId = "WF_" + faker.number().numberBetween(100, 999);

		return "{\n" + "  \"issueTypeId\": " + issueTypeId + ",\n" + "  \"statusEnd\": " + statusEnd + ",\n"
				+ "  \"statusStart\": " + statusStart + ",\n" + "  \"type\": \"" + type + "\",\n" + "  \"projectId\": "
				+ projectId + ",\n" + "  \"workflowId\": \"" + workflowId + "\"\n" + "}";

		
	}

	public static String generateIssueStatusJson() {

		boolean isUpdatable = faker.bool().bool();
		boolean isDefault = faker.bool().bool();
		boolean deleted = faker.bool().bool();
		String color = faker.color().hex();
		int percentage = faker.number().numberBetween(0, 10000);
		String[] descriptions = { "Task successfully completed and verified by QA.",
				"Work is currently in progress with active development ongoing.",
				"Awaiting review and final approval from the manager.",
				"Issue has been reopened for further investigation.",
				"Pending design feedback before moving to implementation.",
				"Ready for deployment after successful testing.", "Blocked due to dependency on another issue.",
				"Marked for closure after resolution verification.", "Awaiting assignment to development team.",
				"Task moved to backlog for future sprint planning." };
		String description = descriptions[random.nextInt(descriptions.length)];
		int position = faker.number().numberBetween(1, 9999);
		String label = faker.options().option("In Progress", "Completed", "On Hold", "In Review", "To Do");
		int projectId = faker.number().numberBetween(1, 9999);
		String statusLevel = faker.options().option("Low", "Medium", "High", "Critical");
		boolean isFinalStatus = faker.bool().bool();
		String statusRole = faker.job().position();

		return "{\n" + "  \"isUpdatable\": " + isUpdatable + ",\n" + "  \"isDefault\": " + isDefault + ",\n"
				+ "  \"color\": \"" + color + "\",\n" + "  \"percentage\": " + percentage + ",\n"
				+ "  \"description\": \"" + description + "\",\n" + "  \"position\": " + position + ",\n"
				+ "  \"label\": \"" + label + "\",\n" + "  \"projectId\": " + projectId + ",\n"
				+ "  \"statusLevel\": \"" + statusLevel + "\",\n" + "  \"isFinalStatus\": " + isFinalStatus + ",\n"
				+ "  \"statusRole\": \"" + statusRole + "\"\n" + "}";

		
	}

	public static String generateIssueStepJson() {

		int issueId = faker.number().numberBetween(1000, 9999);
		int projectId = faker.number().numberBetween(1000, 9999);
		String icon = faker.internet().image(); // random image URL
		String[] steps = { "Created", "In Progress", "Development", "Code Review", "Testing", "QA Review", "UAT",
				"Ready for Deployment", "Deployed", "Completed" };
		String step = steps[random.nextInt(steps.length)];

		return "{\n" + "  \"issueId\": " + issueId + ",\n" + "  \"projectId\": " + projectId + ",\n"
				+ "  \"icon\": \"" + icon + "\",\n" + "  \"step\": \"" + step + "\"\n" + "}";

		
	}

	public static String generateIssueTestCaseJson() {

		int issueRequirementId = faker.number().numberBetween(1000, 9999);
		int issueId = faker.number().numberBetween(100, 9999);
		int testCaseId = faker.number().numberBetween(1000, 9999);

		return "{\n" + "  \"issueRequirementId\": " + issueRequirementId + ",\n" + "  \"issueId\": " + issueId
				+ ",\n" + "  \"testCaseId\": " + testCaseId + "\n" + "}";
	}

	public static String generateIssueTypeJson() {

		boolean isDefault = faker.bool().bool();
		String color = faker.color().hex();
		String description = faker.lorem().sentence();
		int position = faker.number().numberBetween(100, 9999);
		String label = faker.options().option("Bug", "Feature", "Improvement", "Enhancement", "Task", "Documentation",
				"Sub-Task", "Regression");
		String iconUrl = faker.internet().avatar();
		int projectId = faker.number().numberBetween(1000, 9999);

		return "{\n" + "  \"isDefault\": " + isDefault + ",\n" + "  \"color\": \"" + color + "\",\n"
				+ "  \"description\": \"" + description + "\",\n" + "  \"position\": " + position + ",\n"
				+ "  \"label\": \"" + label + "\",\n" + "  \"iconUrl\": \"" + iconUrl + "\",\n" + "  \"projectId\": "
				+ projectId + "\n" + "}";

		
	}

	public static String generateIssueVersionJson() {
		int issueId = faker.number().numberBetween(1000, 9999);
		int projectVersionId = faker.number().numberBetween(100, 999);
		String type = faker.options().option("Bug", "Task", "Feature", "Improvement", "Epic", "Sub-Task");
		String versionName = faker.app().version();
		int projectId = faker.number().numberBetween(1000, 9999);
		String versionCode = "v" + faker.number().digits(3);

		return "{\n" + "  \"issueId\": " + issueId + ",\n" + "  \"projectVersionId\": " + projectVersionId
				+ ",\n" + "  \"type\": \"" + type + "\",\n" + "  \"versionName\": \"" + versionName + "\",\n"
				+ "  \"projectId\": " + projectId + ",\n" + "  \"versionCode\": \"" + versionCode + "\"\n" + "}";

		
	}

	public static String generateIssueVoteJson() {
		int issueId = faker.number().numberBetween(1000, 9999);
		String userId = faker.name().username();

		return "{\n" + "  \"issueId\": " + issueId + ",\n" + "  \"userId\": \"" + userId + "\"\n" + "}";

		
	}

	public static String generateLucyEventJson() {
		String modalLocation = faker.address().cityName();
		String actionType = faker.options().option("Click", "Open", "Close", "Hover", "Submit", "Select");
		String modalType = faker.options().option("Confirmation", "Alert", "Form", "Popup", "Dialog", "Tooltip");
		String additional = faker.lorem().sentence();
		String eventType = faker.options().option("UserAction", "SystemEvent", "Validation", "Notification");
		String elementSuggestion = faker.options().option("Button", "Checkbox", "TextField", "Dropdown", "Link",
				"Icon");
		String title = faker.book().title();
		String message = faker.lorem().sentence();
		String elementFocus = faker.options().option("YesButton", "CancelButton", "InputField", "SubmitButton",
				"SearchBar");

		return "{\n" + "  \"modalLocation\": \"" + modalLocation + "\",\n" + "  \"actionType\": \"" + actionType
				+ "\",\n" + "  \"modalType\": \"" + modalType + "\",\n" + "  \"additional\": \"" + additional + "\",\n"
				+ "  \"eventType\": \"" + eventType + "\",\n" + "  \"elementSuggestion\": \"" + elementSuggestion
				+ "\",\n" + "  \"title\": \"" + title + "\",\n" + "  \"message\": \"" + message + "\",\n"
				+ "  \"elementFocus\": \"" + elementFocus + "\"\n" + "}";

		
	}

	public static String generateModuleJson() {

		String color = faker.color().hex();
		String description = faker.lorem().sentence();
		int position = faker.number().numberBetween(1000, 9999);
		String label = faker.commerce().productName();
		String fileName = faker.lorem().characters(6, true, true).toLowerCase();
		String iconUrl = "https://example.com/img/" + fileName + ".png";
		int projectId = faker.number().numberBetween(1000, 9999);

		return "{\n" + "  \"color\": \"" + color + "\",\n" + "  \"description\": \"" + description + "\",\n"
				+ "  \"position\": " + position + ",\n" + "  \"label\": \"" + label + "\",\n" + "  \"iconUrl\": \""
				+ iconUrl + "\",\n" + "  \"projectId\": " + projectId + "\n" + "}";

		
	}

	public static String generateNatureJson() {

		boolean isDefault = faker.bool().bool();
		String color = faker.color().hex();
		String[] descriptions = { "Indicates a reported bug that requires verification and resolution.",
				"Represents an issue affecting functionality that needs debugging.",
				"Marks a defect discovered during testing and awaiting triage.",
				"Used to track an error impacting core user experience.",
				"Represents a task assigned for development or improvement.",
				"This task requires follow-up to ensure timely delivery.",
				"A pending action item necessary for the project workflow.",
				"A scheduled task that contributes to the sprint goals.",
				"Part of a module requiring updates or enhancements.",
				"Indicates work related to a specific application module.",
				"Module-related activity that needs documentation and review.",
				"Enhancement or fix associated with a key system module.",
				"Kanban stage representing items currently in progress.",
				"Kanban label used to organize workflow and track status.",
				"Used to manage items moving through the Kanban pipeline.",
				"Indicates the current workflow state in the Kanban board.",
				"Jira label applied for categorizing and filtering issues.",
				"Used to group Jira tickets under a specific project workflow.",
				"A Jira-associated item that requires review before closing.",
				"Jira task awaiting assignment or prioritization." };
		int position = faker.number().numberBetween(1000, 9999);
		String label = faker.commerce().productName();
		String fileName = faker.lorem().characters(6, true, true).toLowerCase();
		String iconUrl = "https://example.com/icons/" + fileName + ".png";
		int projectId = faker.number().numberBetween(100, 999);
		String description = descriptions[random.nextInt(descriptions.length)];

		return "{\n" + "  \"isDefault\": " + isDefault + ",\n" + "  \"color\": \"" + color + "\",\n"
				+ "  \"description\": \"" + description + "\",\n" + "  \"position\": " + position + ",\n"
				+ "  \"label\": \"" + label + "\",\n" + "  \"iconUrl\": \"" + iconUrl + "\",\n" + "  \"projectId\": "
				+ projectId + "\n" + "}";

		
	}

	public static String generateOccurrenceJson() {

		Faker faker = new Faker();

		boolean isDefault = faker.bool().bool();
		String color = faker.color().hex();
		String[] descriptions = { "Indicates a reported bug that requires verification and resolution.",
				"Represents an issue affecting functionality that needs debugging.",
				"Marks a defect discovered during testing and awaiting triage.",
				"Used to track an error impacting core user experience.",
				"Represents a task assigned for development or improvement.",
				"This task requires follow-up to ensure timely delivery.",
				"A pending action item necessary for the project workflow.",
				"A scheduled task that contributes to sprint goals.",
				"Part of a module requiring updates or enhancements.",
				"Indicates work related to a specific application module.",
				"Module-related activity needing documentation and review.",
				"Enhancement or fix associated with a key system module.",
				"Kanban stage representing items currently in progress.",
				"Kanban label used to organize workflow and track status.",
				"Used to manage items moving through the Kanban pipeline.",
				"Indicates the current workflow state on the Kanban board.",
				"Jira label applied for categorizing and filtering issues.",
				"Used to group Jira tickets under a specific project workflow.",
				"A Jira-associated item that requires review before closing.",
				"Jira task awaiting assignment or prioritization." };
		int position = faker.number().numberBetween(1000, 9999);
		String label = faker.commerce().productName();
		String fileName = faker.lorem().characters(6, true, true).toLowerCase();
		String iconUrl = "https://example.com/icons/" + fileName + ".png";
		String description = descriptions[random.nextInt(descriptions.length)];
		int projectId = faker.number().numberBetween(100, 999);

		return "{\n" + "  \"isDefault\": " + isDefault + ",\n" + "  \"color\": \"" + color + "\",\n"
				+ "  \"description\": \"" + description + "\",\n" + "  \"position\": " + position + ",\n"
				+ "  \"label\": \"" + label + "\",\n" + "  \"iconUrl\": \"" + iconUrl + "\",\n" + "  \"projectId\": "
				+ projectId + "\n" + "}";

		
	}

	public static String generatePriorityJson() {

		Faker faker = new Faker();

		boolean isDefault = faker.bool().bool();
		String color = faker.color().hex();
		String[] descriptions = { "Indicates a reported bug that requires verification and resolution.",
				"Represents an issue affecting functionality that needs debugging.",
				"Marks a defect discovered during testing and awaiting triage.",
				"Used to track an error impacting core user experience.",
				"Represents a task assigned for development or improvement.",
				"This task requires follow-up to ensure timely delivery.",
				"A pending action item necessary for the project workflow.",
				"A scheduled task that contributes to sprint goals.",
				"Part of a module requiring updates or enhancements.",
				"Indicates work related to a specific application module.",
				"Module-related activity needing documentation and review.",
				"Enhancement or fix associated with a key system module.",
				"Kanban stage representing items currently in progress.",
				"Kanban label used to organize workflow and track status.",
				"Used to manage items moving through the Kanban pipeline.",
				"Indicates the current workflow state on the Kanban board.",
				"Jira label applied for categorizing and filtering issues.",
				"Used to group Jira tickets under a specific project workflow.",
				"A Jira-associated item that requires review before closing.",
				"Jira task awaiting assignment or prioritization." };
		int position = faker.number().numberBetween(1000, 9999);
		String label = faker.commerce().productName();
		String fileName = faker.lorem().characters(6, true, true).toLowerCase();
		String iconUrl = "https://example.com/icons/" + fileName + ".png";
		String description = descriptions[random.nextInt(descriptions.length)];
		int projectId = faker.number().numberBetween(100, 999);

		return "{\n" + "  \"isDefault\": " + isDefault + ",\n" + "  \"color\": \"" + color + "\",\n"
				+ "  \"description\": \"" + description + "\",\n" + "  \"position\": " + position + ",\n"
				+ "  \"label\": \"" + label + "\",\n" + "  \"iconUrl\": \"" + iconUrl + "\",\n" + "  \"projectId\": "
				+ projectId + "\n" + "}";

		
	}

	public static String generateResolutionJson() {

		String color = faker.color().hex();
		String[] descriptions = { "Represents a bug impacting functionality that needs resolution.",
				"Indicates a defect found during testing and awaiting triage.",
				"Bug item that requires developer attention for fixing.",
				"Represents a task assigned to the current sprint cycle.",
				"A pending task that contributes to overall workflow progress.",
				"Task item requiring review before moving to completion.",
				"Related to a specific module requiring updates or validation.",
				"Module-related action needed for project enhancement.",
				"Work item associated with a particular system module.",
				"Kanban stage representing current progress state.",
				"Item used to track workflow movement in Kanban board.",
				"Kanban label to categorize progress and priority.",
				"Jira ticket category used for better issue organization.",
				"An item categorized for tracking inside Jira workflow.",
				"Jira-related description for sprint planning and tracking." };
		String description = descriptions[random.nextInt(descriptions.length)];
		String fileName1 = faker.lorem().characters(6, true, true).toLowerCase();
		String fileName2 = faker.lorem().characters(6, true, true).toLowerCase();

		String categoryIconUrl = "https://example.com/category/" + fileName1 + ".png";
		int position = faker.number().numberBetween(1000, 9999);
		String label = faker.commerce().productName();
		String iconUrl = "https://example.com/icons/" + fileName2 + ".png";
		String category = faker.commerce().department(); // random category name
		int projectId = faker.number().numberBetween(1000, 9999);

		return "{\n" + "  \"color\": \"" + color + "\",\n" + "  \"description\": \"" + description + "\",\n"
				+ "  \"categoryIconUrl\": \"" + categoryIconUrl + "\",\n" + "  \"position\": " + position + ",\n"
				+ "  \"label\": \"" + label + "\",\n" + "  \"iconUrl\": \"" + iconUrl + "\",\n" + "  \"category\": \""
				+ category + "\",\n" + "  \"projectId\": " + projectId + "\n" + "}";

		
	}

	public static String generateRootCauseJson() {

		String[] descriptions = { "Work item related to sprint planning and workflow optimization.",
				"Task created to improve module stability and enhance user experience.",
				"Bug identified during regression testing requiring immediate Fix.",
				"Module enhancement request to improve system performance.",
				"Kanban board update required for progress tracking and visibility.",
				"Technical debt cleanup to improve code quality and maintainability.",
				"Story created to support new feature development for the upcoming release." };
		String description = descriptions[random.nextInt(descriptions.length)];

		String[] labels = { "API Module", "UI Fix", "Backend Task", "Bug Tracking", "Release Planning",
				"Sprint Activities", "Environment Setup", "Regression Cases", "Functional Module",
				"Workflow Category" };

		String label = labels[random.nextInt(labels.length)];

		int projectId = faker.number().numberBetween(1000, 9999);

		return "{\n" + "  \"description\": \"" + description + "\",\n" + "  \"label\": \"" + label + "\",\n"
				+ "  \"projectId\": " + projectId + "\n" + "}";

		
	}

	public static String generateSprintProposalJson() {
		Faker faker = new Faker();

		int sprintId = faker.number().numberBetween(0, 999);
		int issueId = faker.number().numberBetween(1000, 99999);

		String[] userPrefixes = { "User", "QA", "Dev", "Tester", "Agent", "Prod" };
		String prefix = userPrefixes[faker.number().numberBetween(0, userPrefixes.length)];
		String userId = prefix + "_" + faker.number().digits(4);

		return "{\n" + "  \"sprintId\": " + sprintId + ",\n" + "  \"issueId\": " + issueId + ",\n"
				+ "  \"userId\": \"" + userId + "\"\n" + "}";

		
	}

	public static String generateTestCaseJson() {

		int issueId = faker.number().numberBetween(1000, 99999);

		String[] prefixes = { "BUG", "TASK", "STORY", "EPIC", "FIX", "DEFECT" };
		String prefix = prefixes[faker.number().numberBetween(0, prefixes.length)];
		String code = prefix + "-" + faker.number().numberBetween(100, 9999);

		String fileName = faker.lorem().characters(6, true).toLowerCase();
		String iconUrl = "https://example.com/icons/" + fileName + ".png";

		String[] titles = { "Fix UI rendering issue on dashboard", "Improve API response time for product service",
				"Add validation for user input fields", "Update workflow rules for Sprint cycle",
				"Refactor module to improve maintainability", "Resolve authentication token refresh issue",
				"Enhance logging and monitoring for backend service" };

		String title = titles[random.nextInt(titles.length)];

		// Project ID
		int projectId = faker.number().numberBetween(1000, 9999);

		// Realistic status values
		String[] statuses = { "Open", "In Progress", "Resolved", "QA Review", "Closed", "Reopened", "Not a BUG",
				"QA Ready", "QA_Done", "Closed", "Done", "Resolved" };
		String status = statuses[random.nextInt(statuses.length)];

		return "{\n" + "  \"issueId\": " + issueId + ",\n" + "  \"code\": \"" + code + "\",\n"
				+ "  \"iconUrl\": \"" + iconUrl + "\",\n" + "  \"title\": \"" + title + "\",\n" + "  \"projectId\": "
				+ projectId + ",\n" + "  \"status\": \"" + status + "\"\n" + "}";

		
	}

	public static String generateUnitTestResultJson() {
		Faker faker = new Faker();

		int issueId = faker.number().numberBetween(1000, 99999);
		int projectId = faker.number().numberBetween(1000, 9999);

		String[] Comments = { "Pushed the fix to the develop branch. Waiting for build pipeline to complete.",
				"Refactored the API logic to improve performance by ~20%.",
				"Root cause identified: null pointer due to missing validation.",
				"Added unit tests for the new module. Coverage increased to 85%.",
				"Working on edge-case scenarios reported during testing.",
				"Optimized DB query; execution time reduced from 2.8s to 400ms.",
				"Fix applied, please verify in the latest QA build.",
				"Implemented retry mechanism to handle intermittent failures.",
				"Tested the fix; issue persists under specific conditions.",
				"Verified on the latest build. The fix works as expected.",
				"Regression testing completed. No major blockers found.",
				"Added detailed reproduction steps in the ticket.",
				"Observed inconsistent behavior; attaching screenshots.",
				"Re-opening the issue as the bug is still reproducible.",
				"Validated across Chrome, Firefox, and Edge — all good.",
				"Completed smoke testing. Ready for full regression.", "Please prioritize this for the current sprint.",
				"Team to review and update the estimated timelines.",
				"Marking this as a high-impact item for this release.",
				"Align with product team to confirm requirements.", "Need an update before EOD for sprint planning.",
				"Escalating due to customer impact — fix ASAP.",
				"Please ensure cross-functional review before closing.",
				"Coordinate with QA to finalize acceptance criteria." };

		String comment = Comments[random.nextInt(Comments.length)];

		return "{\n" + "  \"issueId\": " + issueId + ",\n" + "  \"comments\": \"" + comment + "\",\n"
				+ "  \"projectId\": " + projectId + "\n" + "}";

		
	}

	public static String generateWorkLogJson() {
		Faker faker = new Faker();

		// Random IDs
		int issueId = faker.number().numberBetween(1000, 99999);
		int projectId = faker.number().numberBetween(1000, 9999);
		int minutesWorked = faker.number().numberBetween(30, 10000);

		// Authors
		String author = faker.name().username();
		String updateAuthor = faker.name().username();

		// Developer comments
		String[] descriptions = { "Pushed latest fix to repository; awaiting CI build.",
				"Refactored controller logic to improve maintainability.",
				"Identified root cause — null check missing in service layer.",
				"Optimized API response time by updating database indexes.",
				"Added new unit tests; coverage increased to 88%.", "Resolved integration issue between modules.",
				"Updated dependency versions to fix compatibility issues.",
				"Tested on latest build; the issue is still reproducible.",
				"Validated fix across multiple browsers — all working fine.",
				"Re-opening the issue due to failed regression steps.",
				"Attached screen recordings for reproducibility.", "Completed smoke tests; ready for full regression.",
				"Observed inconsistent behavior in edge cases.", "Verified on QA environment; ready for UAT.",
				"Please prioritize this task for the current sprint.",
				"Required before release freeze — treat as high priority.",
				"Coordinate with QA to expedite verification.", "Need timeline confirmation for delivery planning.",
				"Marking this as a critical customer-impacting issue.",
				"Ensure cross-team communication for smooth rollout.",
				"Review required before marking this issue as done." };

		String description = descriptions[random.nextInt(descriptions.length)];

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

		LocalDateTime created = faker.date().past(5000, TimeUnit.DAYS).toInstant().atZone(ZoneId.of("UTC"))
				.toLocalDateTime();

		LocalDateTime updated = faker.date().past(3000, TimeUnit.DAYS).toInstant().atZone(ZoneId.of("UTC"))
				.toLocalDateTime();

		LocalDateTime startDate = faker.date().past(9000, TimeUnit.DAYS).toInstant().atZone(ZoneId.of("UTC"))
				.toLocalDateTime();

		return "{\n" + "  \"issueId\": " + issueId + ",\n" + "  \"author\": \"" + author + "\",\n"
				+ "  \"created\": \"" + created.format(formatter) + "\",\n" + "  \"minutesWorked\": " + minutesWorked
				+ ",\n" + "  \"updateAuthor\": \"" + updateAuthor + "\",\n" + "  \"description\": \"" + description
				+ "\",\n" + "  \"projectId\": " + projectId + ",\n" + "  \"updated\": \"" + updated.format(formatter)
				+ "\",\n" + "  \"startDate\": \"" + startDate.format(formatter) + "\"\n" + "}";

		
	}

	public static String generateBehaviorJson() {
		Faker faker = new Faker();

		int issueId = faker.number().numberBetween(1000, 99999);
		int projectId = faker.number().numberBetween(1000, 9999);

		String[] Behaviors = { "Triggers API call when issue status is updated.",
				"Syncs issue data with third-party API service.", "Validates request payload before API submission.",
				"Auto-refreshes API token during workflow transition.", "Records API response metadata for auditing.",
				"Throttles outgoing API calls to prevent rate limit.",
				"Shows inline validation message before form submission.",
				"Disables UI controls until required fields are filled.",
				"Updates UI theme dynamically based on project settings.",
				"Highlights modified fields during issue editing.",
				"Expands/Collapses sections automatically based on status.",
				"Executes scheduled background job for stale issues.", "Triggers workflow hook after database update.",
				"Caches frequently accessed issue metadata.", "Runs pre-update validation on backend service.",
				"Logs backend events for issue transitions.", "Performs dependency checks before updating issue.",
				"Moves issue to next workflow stage automatically.", "Sends alert when SLA threshold is reached.",
				"Auto-assigns developer based on workload.", "Prevents transition if mandatory fields are missing.",
				"Creates sub-task during workflow initialization." };

		String behavior = Behaviors[random.nextInt(Behaviors.length)];
		// Behavior types
		String[] behaviorTypes = { "API", "UI", "Backend", "Workflow", "Trigger", "Validation", "Notification",
				"Automation" };

		String behaviorType = behaviorTypes[random.nextInt(behaviorTypes.length)];
		String fileName = faker.lorem().characters(6, true).toLowerCase();
		String iconUrl = "https://example.com/icons/" + fileName + ".png";

		return "{\n" + "  \"issueId\": " + issueId + ",\n" + "  \"icon\": \"" + iconUrl + "\",\n"
				+ "  \"behavior\": \"" + behavior + "\",\n" + "  \"behaviorType\": \"" + behaviorType + "\",\n"
				+ "  \"projectId\": " + projectId + "\n" + "}";

		
	}

	public static String generateBugTypeJson() {

		boolean isDefault = faker.bool().bool();
		String color = faker.color().hex();
		String[] descriptions = { "Represents default category used in sprint workflow.",
				"Used for organizing tasks related to product enhancements.",
				"Default grouping for module-level planning.",
				"Category created to classify backend-related work items.",
				"Primary bucket for UI/UX improvements in the project.",
				"Used to segment automation tasks and QA activities.",
				"Supports classification of issues based on workflow stages." };

		String description = descriptions[random.nextInt(descriptions.length)];

		String[] labels = { "Backend Module", "UI Improvements", "API Enhancements", "Sprint Tasks",
				"Release Activities", "Bug Fixes", "DevOps Utilities", "Workflow Rules" };

		String label = labels[random.nextInt(labels.length)];
		// position
		int position = faker.number().numberBetween(1000, 9999);

		// Icon URL
		String file = faker.lorem().characters(6, true).toLowerCase();
		String iconUrl = "https://example.com/icons/" + file + ".png";

		// Project ID
		int projectId = faker.number().numberBetween(1000, 9999);

		return "{\n" + "  \"isDefault\": " + isDefault + ",\n" + "  \"color\": \"" + color + "\",\n"
				+ "  \"description\": \"" + description + "\",\n" + "  \"position\": " + position + ",\n"
				+ "  \"label\": \"" + label + "\",\n" + "  \"iconUrl\": \"" + iconUrl + "\",\n" + "  \"projectId\": "
				+ projectId + "\n" + "}";

	}

	public static String generateComponentJson() {

		String[] descriptions = { "Handles UI rendering, layout management and user interaction flows.",
				"Manages dynamic components, widgets, and front-end state updates.",
				"Responsible for responsive layouts and cross-browser compatibility.",
				"Implements user experience improvements and accessibility guidelines.",
				"Controls dashboard widgets and client-side data visualization.",
				"Provides reusable UI components shared across application screens.",
				"Handles client-side form validations and input sanitization.",
				"Updates UI state based on backend or API responses.",
				"Executes core business rules and domain logic for the application.",
				"Manages server-side processing of workflows and automation tasks.",
				"Handles database transactions, migrations, and query optimization.",
				"Processes background jobs and scheduled tasks.",
				"Controls service orchestration and inter-service communication.",
				"Manages caching, indexing, and backend performance tuning.",
				"Implements data aggregation and complex server-side operations.",
				"Supports secure access to internal services and backend resources.",
				"Manages REST API endpoints and request/response life cycles.",
				"Handles authentication tokens, security policies, and API rate limiting.",
				"Provides integrations with third-party services and external systems.",
				"Validates incoming requests and enforces schema constraints.",
				"Implements API versioning and migration support.",
				"Supports webhook processing and outbound notifications.",
				"Handles serialization, deserialization, and payload transformation.",
				"Ensures consistent API contract for frontend and service clients.",
				"Manages workflow transitions and rule-based automation.",
				"Controls issue lifecycle events from creation to closure.",
				"Applies conditional logic for transitions and validations.",
				"Generates audit logs for workflow-led changes.",
				"Coordinates automatic notifications triggered by workflow events.",
				"Executes automation scripts during state changes.",
				"Enforces mandatory field checks before allowing transitions.",
				"Supports complex multi-step approval sequences." };

		String description = descriptions[random.nextInt(descriptions.length)];

		String[] labels = { "Workflow Engine", "Backend Services", "UI Module", "API Gateway", "Auth Module",
				"Reporting Module", "Storage Handler", "Sprint Manager", "Data Processor", "Integration Service",
				"Notification Engine", "Validation Service", "Automation Module", "Event Handler" };

		String label = labels[faker.number().numberBetween(0, labels.length)];

		int moduleId = faker.number().numberBetween(1000, 9999);
		int projectId = faker.number().numberBetween(1000, 9999);

		return "{\n" + "  \"description\": \"" + description + "\",\n" + "  \"label\": \"" + label + "\",\n"
				+ "  \"moduleId\": " + moduleId + ",\n" + "  \"projectId\": " + projectId + "\n" + "}";

	}

	public static String generateComponentImpactAreaJson() {

		int componentId = faker.number().numberBetween(1000, 9999);
		int impactAreaId = faker.number().numberBetween(1000, 9999);

		return "{\n" + "  \"componentId\": " + componentId + ",\n" + "  \"impactAreaId\": " + impactAreaId + "\n"
				+ "}";

	}

	public static String generateDependencyTypeJson() {

		String[] inverseLabels = { "Blocked By", "Related To", "Duplicate Of", "Caused By", "Depends On", "Linked With",
				"Parent Of", "Child Of" };
		String inverseLabel = inverseLabels[faker.number().numberBetween(0, inverseLabels.length)];

		String[] labels = { "Blocks", "Relates", "Duplicate", "Causes", "Depends", "Links", "Parent", "Child" };
		String label = labels[faker.number().numberBetween(0, labels.length)];

		String[] descriptions = { "Indicates relationship between two issues.",
				"Marks logical dependency across modules.", "Represents workflow-based connection.",
				"Defines cross-component reference mapping.", "Classifies related tasks affecting each other.",
				"Identifies links required for sprint progress.", "Maps issue relationship for planning decisions.",
				"Tracks issue influence within the project." };
		String description = descriptions[faker.number().numberBetween(0, descriptions.length)];

		String color = faker.color().hex();

		String fileName = faker.lorem().characters(6, true).toLowerCase();
		String iconUrl = "https://example.com/icons/" + fileName + ".png";

		boolean isRelated = faker.bool().bool();
		boolean isDefault = faker.bool().bool();
		boolean isBlocker = faker.bool().bool();

		int position = faker.number().numberBetween(1000, 9999);
		int projectId = faker.number().numberBetween(1000, 9999);

		return "{\n" + "  \"inverseLabel\": \"" + inverseLabel + "\",\n" + "  \"isRelated\": " + isRelated
				+ ",\n" + "  \"isDefault\": " + isDefault + ",\n" + "  \"color\": \"" + color + "\",\n"
				+ "  \"description\": \"" + description + "\",\n" + "  \"isBlocker\": " + isBlocker + ",\n"
				+ "  \"position\": " + position + ",\n" + "  \"label\": \"" + label + "\",\n" + "  \"iconUrl\": \""
				+ iconUrl + "\",\n" + "  \"projectId\": " + projectId + "\n" + "}";

		
	}

	public static String generateEnvironmentJson() {

		boolean isDefault = faker.bool().bool();

		String[] descriptions = { "Represents a default workflow state.", "Indicates tasks ready for development.",
				"Marks items currently under processing.", "Shows items pending verification or QA.",
				"Final state after all validations are complete.", "Represents a blocked or halted workflow state.",
				"Used for tasks awaiting dependencies.", "Indicates initial state after ticket creation.",
				"Represents tickets rejected during review.", "Marks tasks awaiting deployment approval." };

		String description = descriptions[faker.number().numberBetween(0, descriptions.length)];

		String[] labels = { "To Do", "In Progress", "Under Review", "QA Testing", "Done", "Blocked", "Pending", "New",
				"Rejected", "Ready for Deploy" };

		String label = labels[faker.number().numberBetween(0, labels.length)];

		String color = faker.color().hex();

		String fileName = faker.lorem().characters(8, true, true).toLowerCase();
		String iconUrl = "https://example.com/icons/" + fileName + ".png";

		int position = faker.number().numberBetween(1000, 9999);
		int projectId = faker.number().numberBetween(1000, 9999);

		return "{\n" + "  \"isDefault\": " + isDefault + ",\n" + "  \"color\": \"" + color + "\",\n"
				+ "  \"description\": \"" + description + "\",\n" + "  \"position\": " + position + ",\n"
				+ "  \"label\": \"" + label + "\",\n" + "  \"iconUrl\": \"" + iconUrl + "\",\n" + "  \"projectId\": "
				+ projectId + "\n" + "}";

		
	}

	public static String generateImpactAreaJson() {

		String[] descriptions = { "Represents a functional module within the project.",
				"Defines a task category used in sprint planning.",
				"Describes a reusable component across the application.",
				"Represents a workflow group for issue transitions.",
				"Indicates a logical grouping for related issues.",
				"Defines an area impacted during development changes.",
				"Used for categorizing issues in backlog and sprints.",
				"Represents a core behavior within the system architecture.",
				"Handles UI rendering and component layouts.", "Manages user interaction flows and visual behavior.",
				"Provides reusable widgets for page designs.", "Controls responsive layout and styling logic.",
				"Supports user experience and design guidelines.",
				"Implements client-side state updates and animations.",
				"Maintains dashboard visuals and data displays.", "Handles form validations and user input handling.",
				"Processes business logic and core domain operations.",
				"Handles database queries and transaction execution.", "Manages scheduled jobs and background workers.",
				"Orchestrates internal services and components.", "Responsible for server-side performance tuning.",
				"Implements data aggregation and storage logic.", "Manages internal workflows and event pipelines.",
				"Handles caching, indexing, and session control." };

		String description = descriptions[faker.number().numberBetween(0, descriptions.length)];

		// Labels
		String[] labels = { "Core Module", "Task Group", "UI Component", "Workflow Unit", "API Category",
				"Backend Module", "Feature Area", "Impact Zone" };

		String label = labels[faker.number().numberBetween(0, labels.length)];

		int projectId = faker.number().numberBetween(1000, 9999);

		 return "{\n" + "  \"description\": \"" + description + "\",\n" + "  \"label\": \"" + label + "\",\n"
				+ "  \"projectId\": " + projectId + "\n" + "}";

	
	}

	/////////////////////////////-------------------------BULK-------------------------////////////////////////////////

	public static String generateBehaviorBulkJson() {

		return "{\n" + "  \"trace\": false,\n" + "  \"skipErrors\": false,\n" + "  \"updateExisting\": false,\n"
				+ "  \"bulk\": [\n" + generateBehaviorJson() + ",\n" + generateBehaviorJson() + "\n" + "  ],\n"
				+ "  \"skipSofDeleteValidation\": false\n" + "}";
	}

	

	public static String generateBugTypeBulkJson() {

		return "{\n" + "  \"trace\": false,\n" + "  \"skipErrors\": false,\n" + "  \"updateExisting\": false,\n"
				+ "  \"bulk\": [\n" + generateBugTypeJson() + ",\n" + generateBugTypeJson() + "\n" + "  ],\n"
				+ "  \"skipSofDeleteValidation\": false\n" + "}";
	}

	

	public static String generateComponentBulkJson() {

		return "{\n" + "  \"trace\": true,\n" + "  \"skipErrors\": false,\n" + "  \"updateExisting\": false,\n"
				+ "  \"bulk\": [\n" + generateComponentJson() + ",\n" + generateComponentJson() + "\n" + "  ],\n"
				+ "  \"skipSofDeleteValidation\": false\n" + "}";
	}

	

	public static String generateComponentImpactAreaBulkJson() {

		int componentId = faker.number().numberBetween(2000, 100000);
		int componentId1 = faker.number().numberBetween(5000, 100000);
		int impactAreaId = faker.number().numberBetween(4000, 100000);
		int impactAreaId1 = faker.number().numberBetween(6000, 100000);
		return "{\n" + "  \"trace\": false,\n" + "  \"skipErrors\": false,\n" + "  \"updateExisting\": false,\n"
				+ "  \"bulk\": [\n" + "    {\n" + "      \"componentId\": " + componentId + ",\n"
				+ "      \"impactAreaId\": " + impactAreaId + "\n" + "    },\n" + "    {\n" + "      \"componentId\": "
				+ componentId1 + ",\n" + "      \"impactAreaId\": " + impactAreaId1 + "\n" + "    }\n" + "  ],\n"
				+ "  \"skipSofDeleteValidation\": false\n" + "}";
	}

	public static String generateDependencyTypeBulkJson() {

		return "{\n" + "  \"trace\": true,\n" + "  \"skipErrors\": true,\n" + "  \"updateExisting\": true,\n"
				+ "  \"bulk\": [\n" + generateDependencyTypeJson() + ",\n" + generateDependencyTypeJson() + "\n"
				+ "  ],\n" + "  \"skipSofDeleteValidation\": true\n" + "}";
	}

	

	public static String generateEnvironmentBulkJson() {

		return "{\n" + "  \"trace\": false,\n" + "  \"skipErrors\": true,\n" + "  \"updateExisting\": true,\n"
				+ "  \"bulk\": [\n" + generateEnvironmentJson() + ",\n" + generateEnvironmentJson() + "\n" + "  ],\n"
				+ "  \"skipSofDeleteValidation\": true\n" + "}";
	}

	

	public static String generateImpactAreaBulkJson() {

		return "{\n" + "  \"trace\": false,\n" + "  \"skipErrors\": true,\n" + "  \"updateExisting\": true,\n"
				+ "  \"bulk\": [\n" + generateImpactAreaJson() + ",\n" + generateImpactAreaJson() + "\n"
				+ "  ],\n" + "  \"skipSofDeleteValidation\": false\n" + "}";
	}

	
	
	public static String generateIssueBulkJson() {

	    return "{\n" +
	            "  \"trace\": true,\n" +
	            "  \"skipErrors\": true,\n" +
	            "  \"updateExisting\": true,\n" +
	            "  \"bulk\": [\n" +
	            generateIssueJson() + ",\n" +
	            generateIssueJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": false\n" +
	            "}";
	}
	
	public static String generateIssueCodeBulkJson() {

	    String projectCode1 = faker.bothify("PRJ-####");
	    String projectCode2 = faker.bothify("PRJ-####");

	    return "{\n" +
	            "  \"trace\": true,\n" +
	            "  \"skipErrors\": true,\n" +
	            "  \"updateExisting\": true,\n" +
	            "  \"bulk\": [\n" +
	            "    {\n" +
	            "      \"projectCode\": \"" + projectCode1 + "\"\n" +
	            "    },\n" +
	            "    {\n" +
	            "      \"projectCode\": \"" + projectCode2 + "\"\n" +
	            "    }\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": true\n" +
	            "}";
	}
	
	public static String generateIssueCollaboratorBulkJson() {

	    int issueId1 = faker.number().numberBetween(1000, 9999);
	    int issueId2 = faker.number().numberBetween(1000, 9999);

	    String userId1 = "USR" + faker.number().digits(6);
	    String userId2 = "USR" + faker.number().digits(6);

	    return "{\n" +
	            "  \"trace\": true,\n" +
	            "  \"skipErrors\": false,\n" +
	            "  \"updateExisting\": false,\n" +
	            "  \"bulk\": [\n" +
	            "    {\n" +
	            "      \"issueId\": " + issueId1 + ",\n" +
	            "      \"userId\": \"" + userId1 + "\"\n" +
	            "    },\n" +
	            "    {\n" +
	            "      \"issueId\": " + issueId2 + ",\n" +
	            "      \"userId\": \"" + userId2 + "\"\n" +
	            "    }\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": false\n" +
	            "}";
	}

	
	public static String generateIssueCommentBulkJson() {

	    return "{\n" +
	            "  \"trace\": true,\n" +
	            "  \"skipErrors\": true,\n" +
	            "  \"updateExisting\": false,\n" +
	            "  \"bulk\": [\n" +
	            generateIssueCommentsJson() + ",\n" +
	            generateIssueCommentsJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": false\n" +
	            "}";
	}


	
	
	public static String generateIssueComponentBulkJson() {

	    return "{\n" +
	            "  \"trace\": false,\n" +
	            "  \"skipErrors\": false,\n" +
	            "  \"updateExisting\": false,\n" +
	            "  \"bulk\": [\n" +
	            generateIssueComponentJson() + ",\n" +
	            generateIssueComponentJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": false\n" +
	            "}";
	}
	
	public static String generateIssueFeatureCatalogBulkJson() {

	    return "{\n" +
	            "  \"trace\": true,\n" +
	            "  \"skipErrors\": false,\n" +
	            "  \"updateExisting\": false,\n" +
	            "  \"bulk\": [\n" +
	            generateIssueFeatureCatalogJson() + ",\n" +
	            generateIssueFeatureCatalogJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": true\n" +
	            "}";
	}

	
	
	public static String generateIssueFeatureGroupBulkJson() {

	    return "{\n" +
	            "  \"trace\": true,\n" +
	            "  \"skipErrors\": false,\n" +
	            "  \"updateExisting\": false,\n" +
	            "  \"bulk\": [\n" +
	            generateIssueFeatureGroupJson() + ",\n" +
	            generateIssueFeatureGroupJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": false\n" +
	            "}";
	}
	
	
	public static String generateIssueFeatureStatisticsBulkJson() {

	    return "{\n" +
	            "  \"trace\": true,\n" +
	            "  \"skipErrors\": false,\n" +
	            "  \"updateExisting\": false,\n" +
	            "  \"bulk\": [\n" +
	            generateIssueFeatureStatisticsJson() + ",\n" +
	            generateIssueFeatureStatisticsJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": true\n" +
	            "}";
	}
	
	
	public static String generateIssueGroupBulkJson() {

	    return "{\n" +
	            "  \"trace\": true,\n" +
	            "  \"skipErrors\": true,\n" +
	            "  \"updateExisting\": true,\n" +
	            "  \"bulk\": [\n" +
	            generateIssueGroupJson() + ",\n" +
	            generateIssueGroupJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": true\n" +
	            "}";
	}
	public static String generateIssueImpactAreaBulkJson() {

	    return "{\n" +
	            "  \"trace\": false,\n" +
	            "  \"skipErrors\": false,\n" +
	            "  \"updateExisting\": true,\n" +
	            "  \"bulk\": [\n" +
	            generateIssueImpactAreaJson() + ",\n" +
	            generateIssueImpactAreaJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": false\n" +
	            "}";
	}
	
	public static String generateIssueLinkedBulkJson() {

	    return "{\n" +
	            "  \"trace\": false,\n" +
	            "  \"skipErrors\": true,\n" +
	            "  \"updateExisting\": false,\n" +
	            "  \"bulk\": [\n" +
	            generateIssueLinkedJson() + ",\n" +
	            generateIssueLinkedJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": true\n" +
	            "}";
	}


	public static String generateIssueLogBulkJson() {

	    return "{\n" +
	            "  \"trace\": false,\n" +
	            "  \"skipErrors\": false,\n" +
	            "  \"updateExisting\": true,\n" +
	            "  \"bulk\": [\n" +
	            generateIssueLogJson() + ",\n" +
	            generateIssueLogJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": false\n" +
	            "}";
	}
	
	public static String generateIssueMilestoneBulkJson() {

	    return "{\n" +
	            "  \"trace\": true,\n" +
	            "  \"skipErrors\": false,\n" +
	            "  \"updateExisting\": false,\n" +
	            "  \"bulk\": [\n" +
	            generateIssueMilestoneJson() + ",\n" +
	            generateIssueMilestoneJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": false\n" +
	            "}";
	}
	
	public static String generateIssueRootCauseBulkJson() {

	    return "{\n" +
	            "  \"trace\": false,\n" +
	            "  \"skipErrors\": false,\n" +
	            "  \"updateExisting\": true,\n" +
	            "  \"bulk\": [\n" +
	            generateIssueRootCauseJson() + ",\n" +
	            generateIssueRootCauseJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": true\n" +
	            "}";
	}
	public static String generateIssueStatusGraphBulkJson() {

	    return "{\n" +
	            "  \"trace\": true,\n" +
	            "  \"skipErrors\": true,\n" +
	            "  \"updateExisting\": true,\n" +
	            "  \"bulk\": [\n" +
	            generateIssueStatusGraphson() + ",\n" +
	            generateIssueStatusGraphson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": true\n" +
	            "}";
	}

	
	public static String generateIssueSprintBulkJson() {

	    return "{\n" +
	            "  \"trace\": true,\n" +
	            "  \"skipErrors\": false,\n" +
	            "  \"updateExisting\": false,\n" +
	            "  \"bulk\": [\n" +
	            generateIssueSprintJson() + ",\n" +
	            generateIssueSprintJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": false\n" +
	            "}";
	}
	
	public static String generateIssueStatusBulkJson() {

	    return "{\n" +
	            "  \"trace\": true,\n" +
	            "  \"skipErrors\": true,\n" +
	            "  \"updateExisting\": false,\n" +
	            "  \"bulk\": [\n" +
	            generateIssueStatusJson() + ",\n" +
	            generateIssueStatusJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": true\n" +
	            "}";
	}
	
	public static String generateIssueStepsBulkJson() {

	    return "{\n" +
	            "  \"trace\": false,\n" +
	            "  \"skipErrors\": false,\n" +
	            "  \"updateExisting\": true,\n" +
	            "  \"bulk\": [\n" +
	            generateIssueStepJson() + ",\n" +
	            generateIssueStepJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": true\n" +
	            "}";
	}
	
	public static String generateIssueTestCaseBulkJson() {

	    return "{\n" +
	            "  \"trace\": false,\n" +
	            "  \"skipErrors\": true,\n" +
	            "  \"updateExisting\": true,\n" +
	            "  \"bulk\": [\n" +
	            generateIssueTestCaseJson() + ",\n" +
	            generateIssueTestCaseJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": false\n" +
	            "}";
	}
	
	public static String generateIssueTypeBulkJson() {

	    return "{\n" +
	            "  \"trace\": false,\n" +
	            "  \"skipErrors\": true,\n" +
	            "  \"updateExisting\": true,\n" +
	            "  \"bulk\": [\n" +
	            generateIssueTypeJson() + ",\n" +
	            generateIssueTypeJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": true\n" +
	            "}";
	}

	public static String generateIssueVersionBulkJson() {

	    return "{\n" +
	            "  \"trace\": false,\n" +
	            "  \"skipErrors\": true,\n" +
	            "  \"updateExisting\": true,\n" +
	            "  \"bulk\": [\n" +
	            generateIssueVersionJson() + ",\n" +
	            generateIssueVersionJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": true\n" +
	            "}";
	}
	

	public static String generateIssueVoteBulkJson() {

	    return "{\n" +
	            "  \"trace\": true,\n" +
	            "  \"skipErrors\": false,\n" +
	            "  \"updateExisting\": false,\n" +
	            "  \"bulk\": [\n" +
	            generateIssueVoteJson() + ",\n" +
	            generateIssueVoteJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": false\n" +
	            "}";
	}

	public static String generateLucyEventBulkJson() {

	    return "{\n" +
	            "  \"trace\": false,\n" +
	            "  \"skipErrors\": false,\n" +
	            "  \"updateExisting\": false,\n" +
	            "  \"bulk\": [\n" +
	            generateLucyEventJson() + ",\n" +
	            generateLucyEventJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": false\n" +
	            "}";
	}
	
	public static String generateModuleBulkJson() {

	    return "{\n" +
	            "  \"trace\": false,\n" +
	            "  \"skipErrors\": false,\n" +
	            "  \"updateExisting\": true,\n" +
	            "  \"bulk\": [\n" +
	            generateModuleJson() + ",\n" +
	            generateModuleJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": false\n" +
	            "}";
	}
	
	public static String generateNatureBulkJson() {

	    return "{\n" +
	            "  \"trace\": false,\n" +
	            "  \"skipErrors\": true,\n" +
	            "  \"updateExisting\": true,\n" +
	            "  \"bulk\": [\n" +
	            generateNatureJson() + ",\n" +
	            generateNatureJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": true\n" +
	            "}";
	}
	
	public static String generateOccurrenceBulkJson() {

	    return "{\n" +
	            "  \"trace\": false,\n" +
	            "  \"skipErrors\": true,\n" +
	            "  \"updateExisting\": true,\n" +
	            "  \"bulk\": [\n" +
	            generateOccurrenceJson() + ",\n" +
	            generateOccurrenceJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": true\n" +
	            "}";
	}
	public static String generatePriorityBulkJson() {

	    return "{\n" +
	            "  \"trace\": false,\n" +
	            "  \"skipErrors\": true,\n" +
	            "  \"updateExisting\": true,\n" +
	            "  \"bulk\": [\n" +
	            generatePriorityJson() + ",\n" +
	            generatePriorityJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": true\n" +
	            "}";
	}
	
	public static String generateResolutionBulkJson() {

	    return "{\n" +
	            "  \"trace\": true,\n" +
	            "  \"skipErrors\": true,\n" +
	            "  \"updateExisting\": true,\n" +
	            "  \"bulk\": [\n" +
	            generateResolutionJson() + ",\n" +
	            generateResolutionJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": false\n" +
	            "}";
	}
	
	public static String generateRootCauseBulkJson() {

	    return "{\n" +
	            "  \"trace\": true,\n" +
	            "  \"skipErrors\": true,\n" +
	            "  \"updateExisting\": true,\n" +
	            "  \"bulk\": [\n" +
	            generateRootCauseJson() + ",\n" +
	            generateRootCauseJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": false\n" +
	            "}";
	}
	
	
	public static String generateSprintProposalBulkJson() {

	    return "{\n" +
	            "  \"trace\": true,\n" +
	            "  \"skipErrors\": true,\n" +
	            "  \"updateExisting\": false,\n" +
	            "  \"bulk\": [\n" +
	            generateSprintProposalJson() + ",\n" +
	            generateSprintProposalJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": true\n" +
	            "}";
	}
	
	public static String generateTestCaseBulkJson() {

		return "{\n" + "  \"trace\": false,\n" + "  \"skipErrors\": false,\n" + "  \"updateExisting\": false,\n"
				+ "  \"bulk\": [\n" + generateTestCaseJson() + ",\n" + generateTestCaseJson() + "\n" + "  ],\n"
				+ "  \"skipSofDeleteValidation\": true\n" + "}";
	}
	public static String generateUnitTestResultBulkJson() {

		return "{\n" + "  \"trace\": false,\n" + "  \"skipErrors\": false,\n" + "  \"updateExisting\": false,\n"
				+ "  \"bulk\": [\n" + generateUnitTestResultJson() + ",\n" + generateUnitTestResultJson() + "\n" + "  ],\n"
				+ "  \"skipSofDeleteValidation\": false\n" + "}";
	}
	
	public static String generateUserProjcetBulkJson() {

	    return "{\n" +
	            "  \"trace\": true,\n" +
	            "  \"skipErrors\": true,\n" +
	            "  \"updateExisting\": false,\n" +
	            "  \"bulk\": [\n" +
	            generateUserProjcetJson() + ",\n" +
	            generateUserProjcetJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": true\n" +
	            "}";
	}
	
	public static String generateUserBulkJson() {

	    return "{\n" +
	            "  \"trace\": false,\n" +
	            "  \"skipErrors\": true,\n" +
	            "  \"updateExisting\": true,\n" +
	            "  \"bulk\": [\n" +
	            generateUserJson() + ",\n" +
	            generateUserJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": false\n" +
	            "}";
	}
	
	public static String generateUserStateBulkJson() {

	    return "{\n" +
	            "  \"trace\": false,\n" +
	            "  \"skipErrors\": true,\n" +
	            "  \"updateExisting\": true,\n" +
	            "  \"bulk\": [\n" +
	            generateUserStateJson() + ",\n" +
	            generateUserStateJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": false\n" +
	            "}";
	}
	
	public static String generateWorklogBulkJson() {

	    return "{\n" +
	            "  \"trace\": true,\n" +
	            "  \"skipErrors\": false,\n" +
	            "  \"updateExisting\": true,\n" +
	            "  \"bulk\": [\n" +
	            generateWorkLogJson() + ",\n" +
	            generateWorkLogJson() + "\n" +
	            "  ],\n" +
	            "  \"skipSofDeleteValidation\": true\n" +
	            "}";
	}
	
	public static void main(String[] args) {
//        System.out.println(generateUserJson());
//        System.out.println(generateUserProjcetJson());
//		System.out.println(generateUserStateJson());
//		System.out.println(generateIssueJson());
//		System.out.println(generateIssueCodeJson());
//		System.out.println(generateIssueCollaboratorsJson());
//		System.out.println(generateIssueCommentsJson());
//		System.out.println(generateIssueComponentJson());
//		System.out.println(generateIssueFeatureCatalogJson());
//		
//		System.out.println(generateComponentImpactAreaBulkJson());
//		System.out.println(generateDependencyTypeBulkJson());
//		System.out.println(generateEnvironmentBulkJson());
//		System.out.println(generateImpactAreaBulkJson());
//		System.out.println(generateIssueCodeBulkJson());
//		System.out.println(generateIssueComponentBulkJson());
//		System.out.println(generateIssueFeatureCatalogBulkJson());
		System.out.println(generateIssueLinkedJson());
	}
}