@IssueMilestone

Feature: IssueMilestone API testing

Scenario: Creating IssueMilestone
Given I have a IssueMilestone
When Create New IssueMilestone
Then The IssueMilestone response should contain "id"
And The IssueMilestone response status code should be 201

Scenario: Fetching All IssueMilestones Details
When Fetch All IssueMilestone Details
Then The IssueMilestone response status code should be 200

Scenario: Fetching IssueMilestone Details with ID
When Fetch IssueMilestone Details with ID
Then The IssueMilestone response status code should be 200

Scenario: Updating PUT IssueMileStone Details with ID
When Update IssueMilestone Details
Then The IssueMilestone response status code should be 200

Scenario: Updating Patch IssueMilestone Details with ID
When Update Patch IssueMilestone
Then The IssueMilestone response status code should be 200

Scenario: Deleting IssueMilestone Details with ID
When Delete IssueMilestone with ID
Then The IssueMilestone response status code should be 204







