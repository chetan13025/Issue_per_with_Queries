Feature: Bulk API testing


  #@Behavior
  #Scenario: To verify that creating  Bulk Behavior
   # Given i have Bulk Behavior
    #When Create New BulkBehavior
    #Then The Behavior response status code should be 200
    #Then Validate Bulk Behavior Request Response ES DB

  #@Behavior
  #Scenario: To verify that after PUT request data should get update in DataBase
   # When Update PUT Bulk Behavior
    #Then The Behavior response status code should be 200
    #Then Validate Bulk Behavior Request Response ES DB

  #@Behavior
  #Scenario: To verify that after Delete request data should get removed from DataBase
   # When Delete Bulk Behavior
    #Then The Behavior response status code should be 200

  @IssueCollaborator
  Scenario: To verify that creating bulk issueCollaborator
    Given I have Bulk IssueCollaborator
    When Create New Bulk IssueCollaborator
    Then The IssueCollaborator response status code should be 200
    Then Validate Bulk IssueCollaborator Request Response ES DB

  @IssueCollaborator
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk IssueCollaborator
    Then The IssueCollaborator response status code should be 200
    Then Validate Bulk IssueCollaborator Request Response ES DB

  @IssueCollaborator
  Scenario: To vertify that after DELETE request data should get removed from DataBase
    When Delete Bulk IssueCollaborator
    Then The IssueCollaborator response status code should be 200
    @BuyType
  Scenario: To verify that creating Bulk BugType
    Given I have Bulk BugType
    When Create New Bulk BugType
    Then The BugType response status code should be 200
    Then Validate Bulk BugType Request Response ES DB

  @BuyType
  Scenario: To verify that after PUT request data should get update in DataBase
    Given Update PUT Bulk BugType
    Then The BugType response status code should be 200
    Then Validate Bulk BugType Request Response ES DB

  @BuyType
  Scenario: To verify that after Delete request data should get removed DataBase
    When Delete Bulk BugType
    Then The BugType response status code should be 200

  @Component
  Scenario: To verify that creating bulk Componenet
    Given I have Bulk Component
    When Create New Bulk Component
    Then The Component response status code should be 200
    Then Validate Bulk Component Request Response ES DB

  @Component
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk Component
    Then The Component response status code should be 200
    Then Validate Bulk Component Request Response ES DB

  @Component
  Scenario: To verify that after DELETE request data should get removed from DataBase
    When Delete Bulk Component
    Then The Component response status code should be 200

  @ComponentImpactArea
  Scenario: To verify that creating Bulk Componentimpactarea
    Given I have Bulk ComponentImpactArea
    Then Create New Bulk ComponentImpactArea
    Then The ComponentImpactArea response status code should be 200
    Then Validate Bulk ComponentImpactArea Request Response ES DB

  @ComponentImpactArea
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk ComponentImpactArea
    Then The ComponentImpactArea response status code should be 200
    Then Validate Bulk ComponentImpactArea Request Response ES DB

  @ComponentImpactArea
  Scenario: To verify that after DELETE request data should get removed from DataBase
    When Delete Bulk ComponentImpactArea
    Then The ComponentImpactArea response status code should be 200

  @DependencyType
  Scenario: To verify that creating Bulk DependencyType
    Given I have Bulk DependencyType
    When Create New Bulk DependencyType
    Then The DependencyType response status code should be 200
    Then Validate Bulk DependencyType Request Response ES DB

  @DependencyType
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk DependencyType
    Then The DependencyType response status code should be 200
    Then Validate Bulk DependencyType Request Response ES DB

  @DependencyType
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Bulk DependencyType
    Then The DependencyType response status code should be 200

  @Environment
  Scenario: To verify that creating Bulk Environment
    Given I have Bulk Environment
    When Create New Bulk Environment
    Then The Environment response status code should be 200
    Then Validate Bulk Environment Request Response ES DB

  @Environment
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk Environment
    Then The Environment response status code should be 200
    Then Validate Bulk Environment Request Response ES DB

  @Environment
  Scenario: To veirfy that after DELETE request data should get removed from DataBase
    When Delete Bulk Environment
    Then The Environment response status code should be 200

  @ImpactArea
  Scenario: To verify that creating Bulk ImpactArea
    Given I have Bulk ImpactArea
    When Create New Bulk ImpactArea
    Then The ImpactArea response status code should be 200
    Then Validate Bulk ImpactArea Request Response ES DB

  @ImpactArea
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk ImpactArea
    Then The ImpactArea response status code should be 200
    Then Validate Bulk ImpactArea Request Response ES DB

  @ImpactArea
  Scenario: To verify that after DELETE request data should get removed from DataBase
    When Delete Bulk ImpactArea
    Then The ImpactArea response status code should be 200

  @Issue
  Scenario: To verify that creating Bulk Issue
    Given I have Bulk Issue
    When Create New Bulk Issue
    Then The Issue response status code should be 200
    Then Validate Bulk Issue Request Response ES DB

  @Issue
  Scenario: To verify that after PUT request data should get update in dataBase
    When Update PUT Bulk Issue
    Then The Issue response status code should be 200
    Then Validate Bulk Issue Request Response ES DB

  @Issue
  Scenario: To veify that after DELETE request data should get removed from DataBase
    When Delete Bulk Issue
    Then The Issue response status code should be 200

  @IssueCode
  Scenario: To verify that creating bulk IssueCode
    Given I have Bulk IssueCode
    When Create New Bulk IssueCode
    Then The IssueCode response status code should be 200
    Then Validate Bulk IssueCode Request Response ES DB

  @IssueCode
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk IssueCode
    Then The IssueCode response status code should be 200
    Then Validate Bulk IssueCode Request Response ES DB

  @IssueCode
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Bulk IssueCode
    Then The IssueCode response status code should be 200

  @IssueCollaborator
  Scenario: To verify that creating bulk issueCollaborator
    Given I have Bulk IssueCollaborator
    When Create New Bulk IssueCollaborator
    Then The IssueCollaborator response status code should be 200
    Then Validate Bulk IssueCollaborator Request Response ES DB

  @IssueCollaborator
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk IssueCollaborator
    Then The IssueCollaborator response status code should be 200
    Then Validate Bulk IssueCollaborator Request Response ES DB

  @IssueCollaborator
  Scenario: To vertify that after DELETE request data should get removed from DataBase
    When Delete Bulk IssueCollaborator
    Then The IssueCollaborator response status code should be 200

  @IssueComments
  Scenario: To verify that  creating bulk IssueComments
    Given I have Bulk IssueComment
    When Create New Bulk IssueComment
    Then The IssueComment response status code should be 200
    Then Validate Bulk IssueComment Request Response ES DB

  @IssueComments
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk IssueComment
    Then The IssueComment response status code should be 200
    Then Validate Bulk IssueComment Request Response ES DB

  @IssueComments
  Scenario: To verify that afrer Delete request data should get removed from DataBase
    When Delete Bulk IssueComment
    Then The IssueComment response status code should be 200

  @IssueComponent
  Scenario: To verify that creating Bulk IssueComponent
    Given I have Bulk IssueComponent
    When Create New Bulk IssueComponent
    Then The IssueComponent response status code should be 200
    Then Validate Bulk IssueComponent Request Response ES DB

  @IssueComponent
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk IssueComponent
    Then The IssueComponent response status code should be 200
    Then Validate Bulk IssueComponent Request Response ES DB

  @IssueComponent
  Scenario: To verify that after DELETE request data sholud get removed from DataBase
    When Delete Bulk IssueComponent
    Then The IssueComponent response status code should be 200

  @IssueFeatureCatalog
  Scenario: To verify that creating Bulk IssueFeatureCatalog
    Given I have Bulk IssueFeatureCatalog
    When Create New Bulk IssueFeatureCatalog
    Then The IssueFeatureCatalog response status code should be 200
    Then Validate Bulk IssueFeatureCatalog Request Response ES DB

  @IssueFeatureCatalog
  Scenario: To verify that after PUT request data should get update in Database
    When Update PUT Bulk IssueFeatureCatalog
    Then The IssueFeatureCatalog response status code should be 200
    Then Validate Bulk IssueFeatureCatalog Request Response ES DB

  @IssueFeatureCatalog
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Bulk IssueFeatureCatalog
    Then The IssueFeatureCatalog response status code should be 200

  @IssueFeatureGroup
  Scenario: To verify that creating New Bulk IssueFeatureGroup
    Given I have Bulk IssueFeatureGroup
    When Create New Bulk IssueFeatureGroup
    Then The IssueFeatureGroup response status code should be 200
    Then Validate Bulk IssueFeatureGroup Request Response ES DB

  @IssueFeatureGroup
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk IssueFeatureGroup
    Then The IssueFeatureGroup response status code should be 200
    Then Validate Bulk IssueFeatureGroup Request Response ES DB

  @IssueFeatureGroup
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Bulk IssueFeatureGroup
    Then The IssueFeatureGroup response status code should be 200

  @IssueFeatureStatistics
  Scenario: To verify that creating new Bulk IssueFeatureStatistics
    Given I have Bulk IssueFeatureStatistics
    When Create New Bulk IssueFeatureStatistics
    Then The IssueFeatureStatistics response status code should be 200
    Then Validate Bulk IssueFeatureStatistics Request Response ES DB

  @IssueFeatureStatistics
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk IssueFeatureStatistics
    Then The IssueFeatureStatistics response status code should be 200
    Then Validate Bulk IssueFeatureStatistics Request Response ES DB

  @IssueFeatureStatistics
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Bulk IssueFeatureStatistics
    Then The IssueFeatureStatistics response status code should be 200

  @IssueGroup
  Scenario: To verify that creating bulk IssueGroup
    Given I have Bulk IssueGroup
    When Create New Bulk IssueGroup
    Then The IssueGroup response status code should be 200
    Then Validate Bulk IssueGroup Request Response ES DB

  @IssueGroup
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk IssueGroup
    Then The IssueGroup response status code should be 200
    Then Validate Bulk IssueGroup Request Response ES DB

  @IssueGroup
  Scenario: To verify that after DELETE request data should get removed from DataBase
    When Delete Bulk IssueGroup
    Then The IssueGroup response status code should be 200

  @IssueImpactArea
  Scenario: To verify that creating Bulk IssueImpactArea
    Given I have Bulk IssueImpactArea
    When Create New Bulk IssueImpactArea
    Then The IssueImpactArea response status code should be 200
    Then Validate Bulk IssueImpactArea Request Response ES DB

  @IssueImpactArea
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk IssueImpactArea
    Then The IssueImpactArea response status code should be 200
    Then Validate Bulk IssueImpactArea Request Response ES DB

  @IssueImpactArea
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Bulk IssueImpactArea
    Then The IssueImpactArea response status code should be 200

  @IssueLinked
  Scenario: To verify that creating Bulk Issuelinked
    Given I have Bulk IssueLinked
    When Create New Bulk IssueLinked
    Then The IssueLinked response status code should be 200
    Then Validate Bulk IssueLinked Request Response ES DB

  @IssueLinked
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk IssueLinked
    Then The IssueLinked response status code should be 200
    Then Validate Bulk IssueLinked Request Response ES DB

  @IssueLinked
  Scenario: To verify that after DELETE request data should get removed from DataBase
    When Delete Bulk IssueLinked
    Then The IssueLinked response status code should be 200

  @IssueLog
  Scenario: To verify that creating new Bulk IssueLog
    Given I have Bulk IssueLog
    When Create New Bulk IssueLog
    Then The IssueLog response status code should be 200
    Then Validate Bulk IssueLog Request Response ES DB

  @IssueLog
  Scenario: To verify that after PUT request data should get update in dataBase
    When Update PUT Bulk IssueLog
    Then The IssueLog response status code should be 200
    Then Validate Bulk IssueLog Request Response ES DB

  @IssueLog
  Scenario: To verify that after DELETE request data should get removed from DataBase
    When Delete Bulk IssueLog
    Then The IssueLog response status code should be 200

  @IssueMilestone
  Scenario: To verify that creating Bulk IssueMilestone
    Given I have Bulk IssueMilestone
    When Create New Bulk IssueMilestone
    Then The IssueMilestone response status code should be 200
    Then Validate Bulk IssueMilestone Request Response ES DB

  @IssueMilestone
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk IssueMilestone
    Then The IssueMilestone response status code should be 200
    Then Validate Bulk IssueMilestone Request Response ES DB

  @IssueMilestone
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Bulk IssueMilestone
    Then The IssueMilestone response status code should be 200

  @IssueRootCause
  Scenario: To verify that creating Bulk IssueRootCause
    Given I have Bulk IssueRootCause
    When Create New Bulk IssueRootCause
    Then The IssueRootCause response status code should be 200
    Then Validate Bulk IssueRootCause Request Response ES DB

  @IssueRootCause
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk IssueRootCause
    Then The IssueRootCause response status code should be 200
    Then Validate Bulk IssueRootCause Request Response ES DB

  @IssueRootCause
  Scenario: To verify that after PUT request data should get removed from DataBase
    When Delete Bulk IssueRootCause
    Then The IssueRootCause response status code should be 200

  @IssueSprint
  Scenario: To verify that creating new Bulk IssueSprint
    Given I have Bulk IssueSprint
    When Create New Bulk IssueSprint
    Then The IssueSprint response status code should be 200
    Then Validate Bulk IssueSprint Request Response ES DB

  @IssueSprint
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk IssueSprint
    Then The IssueSprint response status code should be 200
    Then Validate Bulk IssueSprint Request Response ES DB

  @IssueSprint
  Scenario: To verify that after Delete request data should removed from DataBase
    When Delete Bulk IssueSprint
    Then The IssueSprint response status code should be 200

  @IssueStatus
  Scenario: To verify that creating new Bulk IssueStatus
    Given I have Bulk IssueStatus
    When Create New Bulk IssueStatus
    Then The IssueStatus response status code should be 200
    Then Validate Bulk IssueStatus Request Response ES DB

  @IssueStatus
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk IssueStatus
    Then The IssueStatus response status code should be 200
    Then Validate Bulk IssueStatus Request Response ES DB

  @IssueStatus
  Scenario: To verify that after DELETE request data should get removed from DataBase
    When Delete Bulk IssueStatus
    Then The IssueStatus response status code should be 200

  @IssueStatusGraph
  Scenario: To verify that creating Bulk issueStatusGraph
    Given I have Bulk IssueStatusGraph
    When Create New Bulk IssueStatusGraph
    Then The IssueStatusGraph response status code should be 200
    Then Validate Bulk IssueStatusGraph Request Response ES DB

  @IssueStatusGraph
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk IssueStatusGraph
    Then The IssueStatusGraph response status code should be 200
    Then Validate Bulk IssueStatusGraph Request Response ES DB

  @IssueStatusGraph
  Scenario: To verify that after DELETE request data should get removed from DataBase
    When Delete Bulk IssueStatusGraph
    Then The IssueStatusGraph response status code should be 200

  @IssueStep
  Scenario: To verify that creating Bulk IssueStep
    Given I have Bulk IssueStep
    When Create New Bulk IssueStep
    Then The IssueStep response status code should be 200
    Then Validate Bulk IssueStep Request Response ES DB

  @IssueStep
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk IssueStep
    Then The IssueStep response status code should be 200
    Then Validate Bulk IssueStep Request Response ES DB

  @IssueStep
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Bulk IssueStep
    Then The IssueStep response status code should be 200

  @IssueTestCase
  Scenario: To verify that creating new Bulk IssueTestCase
    Given I have Bulk IssueTestCase
    When Create New Bulk IssueTestCase
    Then The IssueTestCase response status code should be 200
    Then Validate Bulk IssueTestCase Request Response ES DB

  @IssueTestCase
  Scenario: To verify that after PUT request data should get update in Database
    When Update PUT Bulk IssueTestCase
    Then The IssueTestCase response status code should be 200
    Then Validate Bulk IssueTestCase Request Response ES DB

  @IssueTestCase
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Bulk IssueTestCase
    Then The IssueTestCase response status code should be 200

  @IssueType
  Scenario: To verify that Creating New Bulk IssueType
    Given I have Bulk IssueType
    When Create New Bulk IssueType
    Then The IssueType response status code should be 200
    Then Validate Bulk IssueType Request Response ES DB

  @IssueType
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk IssueType
    Then The IssueType response status code should be 200
    Then Validate Bulk IssueType Request Response ES DB

  @IssueType
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Bulk IssueType
    Then The IssueType response status code should be 200

  @IssueVersion
  Scenario: To verify that creating new IssueVersion
    Given I have Bulk IssueVersion
    When Create New Bulk IssueVersion
    Then The IssueVersion response status code should be 200
    Then Validate Bulk IssueVersion Request Response ES DB

  @IssueVersion
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk IssueVersion
    Then The IssueVersion response status code should be 200
    Then Validate Bulk IssueVersion Request Response ES DB

  @IssueVersion
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Bulk IssueVersion
    Then The IssueVersion response status code should be 200

  @IssueVote
  Scenario: To verify that creating Bulk IssueVote
    Given I have Bulk IssueVote
    When Create New Bulk IssueVote
    Then The IssueVote response status code should be 200
    Then Validate Bulk IssueVote Request Response ES DB

  @IssueVote
  Scenario: To verify that after PUT request data should get update in Database
    When Update PUT Bulk IssueVote
    Then The IssueVote response status code should be 200
    Then Validate Bulk IssueVote Request Response ES DB

  @IssueVote
  Scenario: To verify that after DELETE request data should get removed from DataBase
    When Delete Bulk IssueVote
    Then The IssueVote response status code should be 200

  @LucyEvent
  Scenario: To verify that creating new Bulk LucyEvent
    Given I have Bulk LucyEvent
    When Create New Bulk LucyEvent
    Then The LucyEvent response status code should be 200
    Then Validate Bulk LucyEvent Request Response ES DB

  @LucyEvent
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk LucyEvent
    Then The LucyEvent response status code should be 200
    Then Validate Bulk LucyEvent Request Response ES DB

  @LucyEvent
  Scenario: To verify that after DELETE request data should get removed from DataBase
    When Delete Bulk LucyEvent
    Then The LucyEvent response status code should be 200

  @Module
  Scenario: To verify that creating new Bulk Module
    Given I have Bulk Module
    When Create New Bulk Module
    Then The Module response status code should be 200
    Then Validate Bulk Module Request Response ES DB

  @Module
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk Module
    Then The Module response status code should be 200
    Then Validate Bulk Module Request Response ES DB

  @Module
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Bulk Module
    Then The Module response status code should be 200

  @Nature
  Scenario: To verify that creating new Bulk Nature
    Given I have Bulk Nature
    When Create New Bulk Nature
    Then The Nature response status code should be 200
    Then Validate Bulk Nature Request Response ES DB

  @Nature
  Scenario: To verify that aftter PUT request data should get update in DataBase
    When Update PUT Bulk Nature
    Then The Nature response status code should be 200
    Then Validate Bulk Nature Request Response ES DB

  @Nature
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Bulk Nature
    Then The Nature response status code should be 200

  @Occurrence
  Scenario: To verify that creating new Occurrence
    Given I have Bulk Occurrence
    When Create New Bulk Occurrence
    Then The Occurrence response status code should be 200
    Then Validate Bulk Occurrence Request Response ES DB

  @Occurrence
  Scenario: To verify that after PUT request data should get update in DataBase
    Given Update PUT Bulk Occurrence
    Then The Occurrence response status code should be 200
    Then Validate Bulk Occurrence Request Response ES DB

  @Occurrence
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Bulk Occurrence
    Then The Occurrence response status code should be 200

  @Priority
  Scenario: To verify that creating new Bulk Priority
    Given I have Bulk Priority
    When Create New Bulk Priority
    Then The Priority response status code should be 200
    Then Validate Bulk Priority Request Response ES DB

  @Priority
  Scenario: To veify that after PUT request data should get update in DataBase
    When Update PUT Bulk Priority
    Then The Priority response status code should be 200
    Then Validate Bulk Priority Request Response ES DB

  @Priority
  Scenario: To verify that after Delete request data  should get removed from DataBase
    When Delete Bulk Priority
    Then The Priority response status code should be 200

  @Resolution
  Scenario: To verify that creating Bulk Resolution
    Given I have Bulk Resolution
    When Create New Bulk Resolution
    Then The Resolution response status code should be 200
    Then Validate Bulk Resolution Request Response ES DB

  @Resolution
  Scenario: To verify that after Update request data should get update in Database
    When Update PUT Bulk Resolution
    Then The Resolution response status code should be 200
    Then Validate Bulk Resolution Request Response ES DB

  @Resolution
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Bulk Resolution
    Then The Resolution response status code should be 200

  @RootCause
  Scenario: To verify that creating Bulk RootCause
    Given I have Bulk RootCause
    When Create New Bulk RootCause
    Then The RootCause response status code should be 200
    Then Validate Bulk RootCause Request Response ES DB

  @RootCause
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk RootCause
    Then The RootCause response status code should be 200
    Then Validate Bulk RootCause Request Response ES DB

  @RootCause
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Bulk RootCause
    Then The RootCause response status code should be 200

  @SprintProposal
  Scenario: To verify that creating new Bulk SprintProposal
    Given I have Bulk SprintProposal
    When Create New Bulk SprintProposal
    Then The SprintProposal response status code should be 200
    Then Validate Bulk SprintProposal Request Response ES DB

  @SprintProposal
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk SprintProposal
    Then The SprintProposal response status code should be 200
    Then Validate Bulk SprintProposal Request Response ES DB

  @SprintProposal
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Bulk SprintProposal
    Then The SprintProposal response status code should be 200

  @TestCase
  Scenario: To verify that creating Bulk TestCase
    Given I have Bulk TestCase
    When Create New Bulk TestCase
    Then The TestCase response status code should be 200
    Then Validate Bulk TestCase Request Response ES DB

  @TestCase
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk TestCase
    Then The TestCase response status code should be 200
    Then Validate Bulk TestCase Request Response ES DB

  @TestCase
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Bulk TestCase
    Then The TestCase response status code should be 200

  @UnitTestResult
  Scenario: To verify that creating New UnitTestResult
    Given I have Bulk UnitTestResult
    When Create New Bulk UnitTestResult
    Then The UnitTestResult response status code should be 200
    Then Validate Bulk UnitTestResult Request Response ES DB

  @UnitTestResult
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk UnitTestResult
    Then The UnitTestResult response status code should be 200
    Then Validate Bulk UnitTestResult Request Response ES DB

  @UnitTestResult
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Bulk UnitTestResult
    Then The UnitTestResult response status code should be 200

  @UserProject
  Scenario: To verify that creating New Bulk UserProject
    Given I have Bulk UserProject
    When Create New Bulk UserProject
    Then The UserProject response status code should be 200
    Then Validate Bulk UserProject Request Response ES DB

  @UserProject
  Scenario: To verify that after PUT data should get update in DataBase
    When Update PUT Bulk UserProject
    Then The UserProject response status code should be 200
    Then Validate Bulk UserProject Request Response ES DB

  @UserProject
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Bulk UserProject
    Then The UserProject response status code should be 200

  @User
  Scenario: To verify that creating new BUlk Users
    Given I have Bulk User
    When Create New Bulk User
    Then The User response status code should be 200
    Then Validate Bulk User Request Response ES DB

  @User
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk User
    Then The User response status code should be 200
    Then Validate Bulk User Request Response ES DB

  @User
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Bulk User
    Then The User response status code should be 200

  @UserState
  Scenario: To verify that creating Bulk UserState
    Given I have Bulk UserState
    When Create New Bulk UserState
    Then The UserState response status code should be 200
    Then Validate Bulk UserState Request Response ES DB

  @UserState
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk UserState
    Then The UserState response status code should be 200
    Then Validate Bulk UserState Request Response ES DB

  @UserState
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Bulk UserState
    Then The UserState response status code should be 200

  @Worklog
  Scenario: To verify that creating Bulk Worklog
    Given I have Bulk Worklog
    When Create New Bulk Worklog
    Then The Worklog response status code should be 200
    Then Validate Bulk Worklog Request Response ES DB

  @Worklog
  Scenario: To verify that after PUT request data should get update in DataBase
    When Update PUT Bulk Worklog
    Then The Worklog response status code should be 200
    Then Validate Bulk Worklog Request Response ES DB

  @Worklog
  Scenario: To verify that after Delete request data should get removed from DataBase
    When Delete Bulk Worklog
    Then The Worklog response status code should be 200


  
    
    