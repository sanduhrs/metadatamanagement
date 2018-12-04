*** Settings ***
Documentation     Resource used by all search and view test cases
Library           ExtendedSelenium2Library
Resource          click_element_resource.robot
Variables         ../common_variables.yaml

*** Keywords ***
Search for
    [Arguments]    ${query}
    Click Element Through Tooltips    xpath=//input[@id='query']
    Input Text    id=query    ${query}

Select project by name
    [Arguments]    ${projectname}
    Input Text    xpath=//md-sidenav//input[@type='search']    ${projectname}

Click on search result by id
    [Arguments]    ${id}
    Click Element Through Tooltips    xpath=//a//span[text()='${id}']

Activate Filter by name
    [Arguments]    ${filtername}
    Click Element Through Tooltips    xpath=//search-filter-panel//md-select
    Click Element Through Tooltips    xpath=//md-select-menu//md-option[contains(., '${filtername}')]

Choose Filter Option by id
    [Arguments]    ${id}
    Click Element Through Tooltips    xpath=//md-virtual-repeat-container//li//span[text()='${id}']

Click on study tab
    ${url} =    Get Location
    Run Keyword If    '/de/' in '${url}'    Click Element Through Tooltips    xpath=//md-pagination-wrapper/md-tab-item[contains(.,'Studie')]
    Run Keyword If    '/en/' in '${url}'    Click Element Through Tooltips    xpath=//md-pagination-wrapper/md-tab-item[contains(.,'Study')]

Click on surveys tab
    ${url} =    Get Location
    Run Keyword If    '/de/' in '${url}'    Click Element Through Tooltips    xpath=//md-pagination-wrapper/md-tab-item[contains(.,'Erhebung')]
    Run Keyword If    '/en/' in '${url}'    Click Element Through Tooltips    xpath=//md-pagination-wrapper/md-tab-item[contains(.,'Survey')]

Click on instruments tab
    ${url} =    Get Location
    Run Keyword If    '/de/' in '${url}'    Click Element Through Tooltips    xpath=//md-pagination-wrapper/md-tab-item[contains(.,'Instrumente')]
    Run Keyword If    '/en/' in '${url}'    Click Element Through Tooltips    xpath=//md-pagination-wrapper/md-tab-item[contains(.,'Instruments')]

Click on questions tab
    ${url} =    Get Location
    Run Keyword If    '/de/' in '${url}'    Click Element Through Tooltips    xpath=//md-pagination-wrapper/md-tab-item[contains(.,'Fragen')]
    Run Keyword If    '/en/' in '${url}'    Click Element Through Tooltips    xpath=//md-pagination-wrapper/md-tab-item[contains(.,'Questions')]

Click on data set tab
    ${url} =    Get Location
    Run Keyword If    '/de/' in '${url}'    Click Element Through Tooltips    xpath=//md-pagination-wrapper/md-tab-item[contains(.,'Datensätze')]
    Run Keyword If    '/en/' in '${url}'    Click Element Through Tooltips    xpath=//md-pagination-wrapper/md-tab-item[contains(.,'Data Sets')]

Click on variable tab
    ${url} =    Get Location
    Run Keyword If    '/de/' in '${url}'    Click Element Through Tooltips    xpath=//md-pagination-wrapper/md-tab-item[contains(.,'Variablen')]
    Run Keyword If    '/en/' in '${url}'    Click Element Through Tooltips    xpath=//md-pagination-wrapper/md-tab-item[contains(.,'Variables')]

Click on publications tab
    ${url} =    Get Location
    Run Keyword If    '/de/' in '${url}'    Click Element Through Tooltips    xpath=//md-pagination-wrapper/md-tab-item[contains(.,'Publikationen')]
    Run Keyword If    '/en/' in '${url}'    Click Element Through Tooltips    xpath=//md-pagination-wrapper/md-tab-item[contains(.,'Publications')]

Click Survey Edit Button
    Click Element Through Tooltips    xpath=//ui-view//a/md-icon[text()='mode_edit']

Click on Cockpit Button
    Click Element Through Tooltips    xpath= //md-icon[@md-svg-src='assets/images/icons/12206436.desktop-mac-dashboard.svg']
