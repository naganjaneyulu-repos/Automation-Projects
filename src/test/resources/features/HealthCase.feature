Feature: Healthcare Application End to End Business Scenarios


  @Member_login
  Scenario: Verify active member details
	
	Given user in on CURA Healthcare Service application
    When the user clicks on the Make appointment 
    And user enter username and  password and clicks on Login
    Then user is on Make Appoinment application
    When user selects apprioprate "<facility>" and "<HealthProgram>" and click on Book appoinment
    Then screen should state appionment confirmation
    
     Examples:
      | facility                     | HealthProgram |
      | Tokyo CURA Healthcare Center  | Medicaid |
      | Tokyo CURA Healthcare Center  | Medicare |
      | Hongkong CURA Healthcare Center | Medicaid |
      | Hongkong CURA Healthcare Center | Medicare |
      | Seoul CURA Healthcare Center | Medicaid |
      | Seoul CURA Healthcare Center  | Medicare |
      
    
    
    @Wrong_username_Member_Login
    Scenario: Verify with incorrect username  

    Given user in on CURA Healthcare Service application
    When the user clicks on the Make appointment 
    And user enter wrong username and correct password and clicks on Login
    Then screen should state Login failed confirmation
    
    @Wrong_username_Member_Login
    Scenario: Verify with incorrect password 

    Given user in on CURA Healthcare Service application
    When the user clicks on the Make appointment 
    And user enter correct username and wrong password and clicks on Login
    Then screen should state Login failed confirmation
    
    
     



