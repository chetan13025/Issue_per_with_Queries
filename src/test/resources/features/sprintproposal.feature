@SprintProposal

Feature: SprintProposal API testing

Scenario: To verify that Creating new SprintProposal
Given I have a SprintProposal
When Create New SprintProposal
Then The SprintProposal response should contain "id"
Then Validate API ES and DB for SprintProposalId after POST skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
And The SprintProposal response status code should be 201

Scenario: To verify that GET request fetched All data
When Fetch All SprintProposal Details
Then The SprintProposal response status code should be 200

Scenario: To verify that GET with ID Fetched Correct data
When Fetch SprintProposal Details with ID
Then The SprintProposal response status code should be 200

Scenario: To verify that after PUT request data should get update in DataBase
When Update SprintProposal Details
Then Validate API ES and DB for SprintProposalId after PUT skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The SprintProposal response status code should be 200

Scenario: To verify that after Patch request data should get update in DataBase
When Update Patch SprintProposal
Then Validate API ES and DB for SprintProposalId after PATCH skipping ES true excludingEverywhere "null" excludeFromRequest "id,deleted" excludeFromEs "deleted"
Then The SprintProposal response status code should be 200

Scenario: To verify that after Delete request data should get removed from DataBase
When Delete SprintProposal with ID
Then The SprintProposal response status code should be 204





