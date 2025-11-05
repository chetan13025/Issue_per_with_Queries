package DataCreate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import org.json.JSONObject;

import com.github.javafaker.Faker;

public class DataCreate {
	static Faker faker = new Faker();
	static Random random = new Random();

	public static String generateUserJson() {
		String firstName = faker.name().firstName();
		String lastName = faker.name().lastName();
		String userId = "USR" + faker.number().digits(6);
		int projectId = faker.number().numberBetween(1000, 99999);
		JSONObject json = new JSONObject();
		json.put("avatar", faker.internet().avatar());
		json.put("firstName", firstName);
		json.put("fullName", firstName + " " + lastName);
		json.put("lastName", lastName);
		json.put("position", faker.job().position());
		json.put("projectId", projectId);
		json.put("role", faker.job().title());
		json.put("userId", userId);
		json.put("userName", faker.name().username());

		return json.toString(1);
	}

	public static String generateUserProjcetJson() {

		int projectId = faker.number().numberBetween(1000, 9999); // Random 4-digit Project ID
		String userId = "USR" + faker.number().digits(6); // Random User ID like USR123456
		String userName = faker.name().username(); // Random username

		String json = "{\n" + "  \"projectId\": " + projectId + ",\n" + "  \"userId\": \"" + userId + "\",\n"
				+ "  \"userName\": \"" + userName + "\"\n" + "}";

		return json;
	}

	public static String generateUserStateJson() {

		String status = faker.options().option("Active", "Pending", "Inactive");
		String userId = "USR" + faker.number().digits(6);
		String userName = faker.name().username();
		String tokenExpirationDate = Instant.now().toString(); // ISO format like 1971-10-11T15:03:42.171Z

		String json = "{\n" + "  \"status\": \"" + status + "\",\n" + "  \"userId\": \"" + userId + "\",\n"
				+ "  \"userName\": \"" + userName + "\",\n" + "  \"tokenExpirationDate\": \"" + tokenExpirationDate
				+ "\"\n" + "}";

		return json;
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

		String json = "{\n" + "  \"projectId\": " + projectId + ",\n" + "  \"createUserId\": \""
				+ esc.apply(createUserId) + "\",\n" + "  \"dueDate\": \"" + esc.apply(dueDate) + "\",\n"
				+ "  \"createUserName\": \"" + esc.apply(createUserName) + "\",\n" + "  \"fixVersions\": \""
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
				+ "\"\n" + "}";

		return json;
	}

	public static String generateIssueCodeJson() {
		String projectCode = "PRJ_" + faker.number().digits(4);

		String json = "{\n" + "  \"projectCode\": \"" + projectCode + "\"\n" + "}";

		return json;
	}

	public static String generateIssueCollaboratorsJson() {

		int issueId = faker.number().numberBetween(100000, 999900);
		String userId = "USR_" + faker.number().numberBetween(10000, 99900);

		String json = "{\n" + "  \"issueId\": " + issueId + ",\n" + "  \"userId\": \"" + userId + "\"\n" + "}";

		return json;
	}

	public static String generateIssueCommentsJson() {

		int commentId = faker.number().numberBetween(1000, 9999);
		int issueId = faker.number().numberBetween(1000, 9999);
		String type = faker.options().option("Info", "Update", "Log", "History");
		String currentState = faker.options().option("Open", "In Progress", "Closed", "Reopened");
		String previousState = faker.options().option("Open", "In Progress", "Closed", "Reopened");

		String json = "{\n" + "  \"commentId\": " + commentId + ",\n" + "  \"issueId\": " + issueId + ",\n"
				+ "  \"type\": \"" + type + "\",\n" + "  \"currentState\": \"" + currentState + "\",\n"
				+ "  \"previousState\": \"" + previousState + "\"\n" + "}";

		return json;
	}

	public static String generateIssueComponentJson() {

		int componentId = faker.number().numberBetween(1000, 9999);
		int issueId = faker.number().numberBetween(1000, 9999);
		int projectId = faker.number().numberBetween(1000, 9999);
		String label = faker.lorem().word(); // random string

		String json = "{\n" + "  \"componentId\": " + componentId + ",\n" + "  \"issueId\": " + issueId + ",\n"
				+ "  \"projectId\": " + projectId + ",\n" + "  \"label\": \"" + label + "\"\n" + "}";

		return json;
	}

	public static String generateIssueFeatureCatalogJson() {

		String fieldName = faker.lorem().word();
		String ruleName = faker.lorem().word();
		int projectId = faker.number().numberBetween(1000, 9999);
		String fieldValue = faker.lorem().word();

		String json = "{\n" + "  \"fieldName\": \"" + fieldName + "\",\n" + "  \"ruleName\": \"" + ruleName + "\",\n"
				+ "  \"projectId\": " + projectId + ",\n" + "  \"fieldValue\": \"" + fieldValue + "\"\n" + "}";

		return json;
	}

	public static String generateIssueFeatureGroupJson() {

		String criteriaGroup = faker.lorem().word();
		String label = faker.lorem().word();

		String json = "{\n" + "  \"criteriaGroup\": \"" + criteriaGroup + "\",\n" + "  \"label\": \"" + label + "\"\n"
				+ "}";

		return json;
	}

	public static String generateissueFeatureStatisticsJson() {

		int quantitySprints = faker.number().numberBetween(1, 100);
		int issueId = faker.number().numberBetween(1000, 9999);
		String createUserId = "USR_" + faker.number().digits(4);
		String updateUserId = "USR_" + faker.number().digits(4);
		int progressPercentage = faker.number().numberBetween(0, 100);
		int projectId = faker.number().numberBetween(8000, 9000);
		int totalLinkedIssues = faker.number().numberBetween(10, 100);
		int totalClosedIssues = faker.number().numberBetween(5, totalLinkedIssues);

		String json = "{\n" + "  \"quantitySprints\": " + quantitySprints + ",\n" + "  \"issueId\": " + issueId + ",\n"
				+ "  \"createUserId\": \"" + createUserId + "\",\n" + "  \"updateUserId\": \"" + updateUserId + "\",\n"
				+ "  \"progressPercentage\": " + progressPercentage + ",\n" + "  \"projectId\": " + projectId + ",\n"
				+ "  \"totalLinkedIssues\": " + totalLinkedIssues + ",\n" + "  \"totalClosedIssues\": "
				+ totalClosedIssues + "\n" + "}";

		return json;
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
		String json = "{\n" + "  \"impactAreaId\": " + impactAreaId + ",\n" + "  \"issueId\": " + issueId + ",\n"
				+ "  \"projectId\": " + projectId + ",\n" + "  \"label\": \"" + label + "\"\n" + "}";

		return json;
	}

	public static String generateIssueLinkedJson() {

		int issueId = faker.number().numberBetween(2000, 3000);
		int projectId = faker.number().numberBetween(5000, 7000);
		int relatedIssueId = faker.number().numberBetween(7000, 8000);
		String inverseDependencyType = faker.options().option("Blocks", "Relates To", "Depends On", "Duplicates");
		String dependencyType = faker.options().option("Soft", "Hard", "Critical", "Major");
		String linkedDate = Instant.now().minus(faker.number().numberBetween(100, 1000), ChronoUnit.DAYS).toString();
		String unlinkedDate = Instant.now().minus(faker.number().numberBetween(1000, 5000), ChronoUnit.DAYS).toString();
		int position = faker.number().numberBetween(8000, 9000);
		String relationship = faker.options().option("Parent", "Child", "Peer", "Linked");
		int dependencyTypeId = faker.number().numberBetween(1000, 1100);

		String json = "{\n" + "  \"issueId\": " + issueId + ",\n" + "  \"projectId\": " + projectId + ",\n"
				+ "  \"relatedIssueId\": " + relatedIssueId + ",\n" + "  \"inverseDependencyType\": \""
				+ inverseDependencyType + "\",\n" + "  \"dependencyType\": \"" + dependencyType + "\",\n" +
//                "  \"linkedDate\": \"" + linkedDate + "\",\n" +
//                "  \"unlinkedDate\": \"" + unlinkedDate + "\",\n" +
				"  \"position\": " + position + ",\n" + "  \"relationship\": \"" + relationship + "\",\n"
				+ "  \"dependencyTypeId\": " + dependencyTypeId + "\n" + "}";

		return json;
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

		String json = "{\n" + "  \"issueId\": " + issueId + ",\n" + "  \"projectId\": " + projectId + ",\n"
				+ "  \"targetId\": " + targetId + ",\n" + "  \"newValue\": \"" + newValue + "\",\n"
				+ "  \"targetNewId\": " + targetNewId + ",\n" + "  \"targetType\": \"" + targetType + "\",\n"
				+ "  \"eventType\": \"" + eventType + "\",\n" + "  \"userId\": \"" + userId + "\",\n"
				+ "  \"isViewed\": " + isViewed + ",\n" + "  \"issueType\": \"" + issueType + "\",\n"
				+ "  \"sprintId\": " + sprintId + ",\n" + "  \"qaAssignedId\": \"" + qaAssignedId + "\",\n"
				+ "  \"devAssignedId\": \"" + devAssignedId + "\",\n" + "  \"comment\": \"" + comment + "\",\n"
				+ "  \"oldValue\": \"" + oldValue + "\"\n" + "}";

		return json;
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

		String json = "{\n" + "  \"projectId\": " + projectId + ",\n" + "  \"isRequired\": " + isRequired + ",\n"
				+ "  \"milestone\": \"" + milestone + "\",\n" + "  \"submilestone\": \"" + submilestone + "\",\n"
				+ "  \"color\": \"" + color + "\",\n" + "  \"position\": " + position + ",\n" + "  \"eventType\": \""
				+ eventType + "\",\n" + "  \"UiReference\": \"" + uiReference + "\",\n" + "  \"issueStatusId\": "
				+ issueStatusId + ",\n" + "  \"isTickeable\": " + isTickeable + "\n" + "}";

		return json;
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

		String json = "{\n" + "  \"issueId\": " + issueId + ",\n" + "  \"projectId\": " + projectId + ",\n"
				+ "  \"rootCauseId\": " + rootCauseId + ",\n" + "  \"comments\": \"" + comments + "\",\n"
				+ "  \"correctiveActions\": \"" + correctiveActions + "\"\n" + "}";

		return json;
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

		String json = "{\n" + "  \"sprintId\": " + sprintId + ",\n" + "  \"issueId\": " + issueId + ",\n"
				+ "  \"startSprintDate\": \"" + startSprintDate.format(formatter) + "\",\n" + "  \"sprintName\": \""
				+ sprintName + "\",\n" + "  \"endSprintDate\": \"" + endSprintDate.format(formatter) + "\",\n"
				+ "  \"type\": \"" + type + "\",\n" + "  \"projectId\": " + projectId + "\n" + "}";

		return json;
	}

	public static String generateIssueStatusGraphson() {

		int issueTypeId = faker.number().numberBetween(100, 9999);
		int statusEnd = faker.number().numberBetween(1000, 9999);
		int statusStart = faker.number().numberBetween(1000, 9999);
		String type = faker.job().field();
		int projectId = faker.number().numberBetween(1000, 9999);
		String workflowId = "WF_" + faker.number().numberBetween(100, 999);

		String json = "{\n" + "  \"issueTypeId\": " + issueTypeId + ",\n" + "  \"statusEnd\": " + statusEnd + ",\n"
				+ "  \"statusStart\": " + statusStart + ",\n" + "  \"type\": \"" + type + "\",\n" + "  \"projectId\": "
				+ projectId + ",\n" + "  \"workflowId\": \"" + workflowId + "\"\n" + "}";

		return json;
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

		String json = "{\n" + "  \"isUpdatable\": " + isUpdatable + ",\n" + "  \"isDefault\": " + isDefault + ",\n"
				+ "  \"deleted\": " + deleted + ",\n" + "  \"color\": \"" + color + "\",\n" + "  \"percentage\": "
				+ percentage + ",\n" + "  \"description\": \"" + description + "\",\n" + "  \"position\": " + position
				+ ",\n" + "  \"label\": \"" + label + "\",\n" + "  \"projectId\": " + projectId + ",\n"
				+ "  \"statusLevel\": \"" + statusLevel + "\",\n" + "  \"isFinalStatus\": " + isFinalStatus + ",\n"
				+ "  \"statusRole\": \"" + statusRole + "\"\n" + "}";

		return json;
	}

	public static String generateIssueStepJson() {

		int issueId = faker.number().numberBetween(1000, 9999);
		String icon = faker.options().option("check-circle", "alert-triangle", "clock", "code", "flag", "star");
		String step = faker.options().option("Design Phase", "Development Phase", "Testing Phase", "Code Review",
				"Deployment", "Bug Fixing", "User Acceptance");
		int projectId = faker.number().numberBetween(1000, 9999);

		String json = "{\n" + "  \"issueId\": " + issueId + ",\n" + "  \"icon\": \"" + icon + "\",\n" + "  \"step\": \""
				+ step + "\",\n" + "  \"projectId\": " + projectId + "\n" + "}";

		return json;
	}

	public static String generateIssueTestCaseJson() {

		int issueRequirementId = faker.number().numberBetween(1000, 9999);
		int issueId = faker.number().numberBetween(100, 9999);
		int testCaseId = faker.number().numberBetween(1000, 9999);

		String json = "{\n" + "  \"issueRequirementId\": " + issueRequirementId + ",\n" + "  \"issueId\": " + issueId
				+ ",\n" + "  \"testCaseId\": " + testCaseId + "\n" + "}";

		return json;
	}

	public static String generateIssueTypeJson() {

		boolean isDefault = faker.bool().bool();
		String color = faker.color().hex();
		String description = faker.lorem().sentence();
		int position = faker.number().numberBetween(100, 9999);
		String label = faker.options().option("Bug", "Feature", "Improvement", "Enhancement", "Task", "Documentation","Sub-Task","Regression");
		String iconUrl = faker.internet().avatar();
		int projectId = faker.number().numberBetween(1000, 9999);

		String json = "{\n" + "  \"isDefault\": " + isDefault + ",\n" + "  \"color\": \"" + color + "\",\n"
				+ "  \"description\": \"" + description + "\",\n" + "  \"position\": " + position + ",\n"
				+ "  \"label\": \"" + label + "\",\n" + "  \"iconUrl\": \"" + iconUrl + "\",\n" + "  \"projectId\": "
				+ projectId + "\n" + "}";

		return json;
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
//		System.out.println(generateIssueFeatureGroupJson());
//		System.out.println(generateissueFeatureStatisticsJson());
//		System.out.println(generateIssueGroupJson());
//		System.err.println(generateIssueImpactAreaJson());
//		System.out.println(generateIssueLinkedJson());
//		System.out.println(generateIssueLogJson());
//		System.out.println(generateIssueMilestoneJson());
//		System.out.println(generateIssueRootCauseJson());
//		System.out.println(generateIssueSprintJson());
//		System.out.println(generateIssueStatusGraphson());
//		System.out.println(generateIssueStatusJson());
//		System.out.println(generateIssueStepJson());
//		System.out.println(generateIssueTestCaseJson());
		System.out.println(generateIssueTypeJson());
	}
}