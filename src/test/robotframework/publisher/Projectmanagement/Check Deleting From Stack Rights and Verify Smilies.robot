*** Settings ***
Documentation     Assign dataproviders and publishers from the list and check deleting the last element from the stack in not possible. Verify the smilies of metadata.
Resource          ../../resources/login_resource.robot
Resource          ../../resources/click_element_resource.robot
Resource          ../../resources/search_resource.robot
Resource          ../../resources/project_management_resource.robot

*** Variables ***
${PROJECT_NAME}  smileyproject

*** Test Cases ***
Check Deleting The Last Dataprovider and Publisher From Stack is Not Possible
   Create Project  ${PROJECT_NAME}${BROWSER}
   Click on Cockpit Button
   Assign Dataprovider From List
   Click Element Through Tooltips   xpath=//md-card[@group='dataProviders']//md-list-item//div//strong[contains(.,'dataprovidertest1')]//following::md-icon[contains(.,'delete_forever')]
   Click Element Through Tooltips   xpath=//md-card[@group='dataProviders']//md-list-item//div//strong[contains(.,'dataprovidertest2')]//following::md-icon[contains(.,'delete_forever')]
   Page Should Contain Element  xpath=//md-card[@group='dataProviders']//md-list-item//div//strong[contains(.,'dataprovider')]//following::button[@disabled='disabled']
   Assign Publisher From List
   Click Element Through Tooltips   xpath=//md-card[@group='publishers']//md-list-item//div//strong[contains(.,'publishertest1')]//following::md-icon[contains(.,'delete_forever')]
   Click Element Through Tooltips   xpath=//md-card[@group='publishers']//md-list-item//div//strong[contains(.,'publishertest2')]//following::md-icon[contains(.,'delete_forever')]
   Page Should Contain Element  xpath=//md-card[@group='publishers']//md-list-item//div//strong[contains(.,'publisher')]//following::button[@disabled='disabled']

Check The Sentiments of Metadata
   Select Survey Checkbox
   Select Instruments Checkbox
   Select Questions Checkbox
   Select Datasets Checkbox
   Select Variable Checkbox
   Save Changes
   Click on Cockpit Button
   Switch To Status Tab
   Verify The Sentiments From The List of Meatadata
   Save Changes
   Delete project by name  ${PROJECT_NAME}${BROWSER}

*** Keywords ***
Assign Dataprovider From List
    @{DP_ITEMS}    Create List    dataprovider   dataprovidertest1    dataprovidertest2
    :FOR    ${DP}    IN    @{DP_ITEMS}
    \    Assign a dataprovider   ${DP}

Assign Publisher From List
     @{PL_ITEMS}    Create List    publishertest1   publishertest2
    :FOR    ${PL}    IN    @{PL_ITEMS}
    \    Assign a publisher  ${PL}

Verify The Sentiments From The List of Meatadata
    @{MD_ITEMS}    Create List    studies   surveys   instruments   questions   dataSets    variables
    @{SN_ITEMS}    Create List    sentiment_very_dissatisfied   sentiment_satisfied   sentiment_very_satisfied
    :FOR   ${MD_DT}   IN  @{MD_ITEMS}
    \   Run Keyword If   '@{SN_ITEMS}[0]' == 'sentiment_very_dissatisfied'    Page Should Contain Element    xpath=//md-card[@type="${MD_DT}"]//following::md-icon[contains(., "@{SN_ITEMS}[0]")]
    \   Click Element Through Tooltips  xpath=//md-card[@type="${MD_DT}"]//md-checkbox[contains(.,"Datengeber Fertig")]
    \   Sleep  1s    # to avoid failing in edge
    \   Run Keyword If    '@{SN_ITEMS}[1]' == 'sentiment_satisfied'     Page Should Contain Element    xpath=//md-card[@type="${MD_DT}"]//following::md-icon[contains(., "@{SN_ITEMS}[1]")]
    \   Click Element Through Tooltips  xpath=//md-card[@type="${MD_DT}"]//md-checkbox[contains(.,"Publisher Fertig")]
    \   Sleep  1s   # to avoid failing in edge
    \   Run Keyword If    '@{SN_ITEMS}[2]' == 'sentiment_very_satisfied'    Page Should Contain Element    xpath=//md-card[@type="${MD_DT}"]//following::md-icon[contains(., "@{SN_ITEMS}[2]")]




