The Code Validator API and Service allows individuals to validate codes and display name for Health IT value sets that are used in clinical documents, such as the Consolidate Clinical Document Architecture (C-CDA).

This project repository contains both code validator API as well as a simple Spring-based Restful web service wrapper.

There are three option to utilize this code validator API.

1. Deploy the Code Validator Web Service Project to a JEE Web Container, such as Tomcat.  
    You'll need to make sure there is a file on the classpath called "environment.properties".  This file must include the following configuration items:
        
        vocabulary.localRepositoryDir=/directory/location/to/code_repositories/
        vocabulary.orientDbConfigFile=/directory/location/to/orientdb-server-config.xml
        vocabulary.primaryDbName=primary
        vocabulary.secondaryDbName=secondary
    
  You'll also need to make sure that you have a configuration file for the internal orientdb database.  The following is a sample orient DB file:
   
      <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        <orient-server>
          <handlers>
            <handler class="com.orientechnologies.orient.server.handler.OJMXPlugin">
               <parameters>
                   <parameter value="false" name="enabled"/>
                   <parameter value="true" name="profilerManaged"/>
               </parameters>
           </handler>
           <handler class="com.orientechnologies.orient.server.plugin.mail.OMailPlugin">
               <parameters>
                    <parameter value="false" name="enabled"/>
                    <parameter value="localhost" name="profile.default.mail.smtp.host"/>
                   <parameter value="25" name="profile.default.mail.smtp.port"/>
                    <parameter value="true" name="profile.default.mail.smtp.auth"/>
                    <parameter value="true" name="profile.default.mail.smtp.starttls.enable"/>
                    <parameter value="" name="profile.default.mail.smtp.user"/>
                    <parameter value="" name="profile.default.mail.smtp.password"/>
                </parameters>
            </handler>
            <handler class="com.orientechnologies.orient.server.handler.OAutomaticBackup">
                <parameters>
                    <parameter value="false" name="enabled"/>
                    <parameter value="4h" name="delay"/>
                    <parameter value="backup" name="target.directory"/>
                    <parameter value="${DBNAME}-${DATE:yyyyMMddHHmmss}.json" name="target.fileName"/>
                    <parameter value="" name="db.include"/>
                    <parameter value="" name="db.exclude"/>
                </parameters>
            </handler>
            <handler class="com.orientechnologies.orient.server.handler.OServerSideScriptInterpreter">
                <parameters>
                    <parameter value="false" name="enabled"/>
                </parameters>
            </handler>
        </handlers>
        <network>
            <protocols>
                <protocol implementation="com.orientechnologies.orient.server.network.protocol.binary.ONetworkProtocolBinary" name="binary"/>
                <protocol implementation="com.orientechnologies.orient.server.network.protocol.http.ONetworkProtocolHttpDb" name="http"/>
            </protocols>
            <listeners>
                <listener protocol="binary" socket="default" port-range="2424-2430" ip-address="0.0.0.0"/>
                <listener protocol="http" socket="default" port-range="2480-2490" ip-address="0.0.0.0">
                    <commands>
                        <command implementation="com.orientechnologies.orient.server.network.protocol.http.command.get.OServerCommandGetStaticContent" pattern="GET|www GET|studio/ GET| GET|*.htm GET|*.html GET|*.xml GET|*.jpeg GET|*.jpg GET|*.png GET|*.gif GET|*.js GET|*.css GET|*.swf GET|*.ico GET|*.txt GET|*.otf GET|*.pjs GET|*.svg" stateful="false">
                            <parameters>
                                <entry value="Cache-Control: no-cache, no-store, max-age=0, must-revalidate\r\nPragma: no-cache" name="http.cache:*.htm *.html"/>
                                <entry value="Cache-Control: max-age=120" name="http.cache:default"/>
                            </parameters>
                        </command>
                    </commands>
                    <parameters>
                        <parameter value="utf-8" name="network.http.charset"/>
                    </parameters>
                </listener>
            </listeners>
        </network>
        <storages>
            <storage loaded-at-startup="true" userPassword="admin" userName="admin" path="plocal:/Users/chris/Development/tomcat/tomcat-CCDA/databases/primary" name="primary"/>
            <storage loaded-at-startup="true" userPassword="admin" userName="admin" path="plocal:/Users/chris/Development/tomcat/tomcat-CCDA/databases/secondary" name="secondary"/>
        </storages>
        <users>
            <user resources="*" password="1C9E93E16EA272C1B6C95AC591CE56E52153AB30C7F08C958475B2016AECA2F5" name="root"/>
            <user resources="connect,server.listDatabases,server.dblist" password="guest" name="guest"/>
        </users>
        <properties>
            <entry value="info" name="log.console.level"/>
            <entry value="fine" name="log.file.level"/>
        </properties>
       </orient-server>
2. Use the included Servlet Listener in an existing web project.
    The Servlet Listener requires the same "environment.properties" and orientdb configuration as step 1.  It also requires the servlet listener to be configured in the application's web.xml, as follows:
      
        <listener>
          <listener-class>
            org.sitenv.vocabularies.servlet.listener.VocabularyValidationListener
          </listener-class>
        </listener>
3. Manually initialize the ValidationEngine (See the VocabularyValidationListner for insight into how to manually initialize the ValidationEngine)

The ValidationEngine performs the code/displayname validations; it also launches a watchdog thread, which monitors the code value set repository directory for changes.  If a code value set file is added, removed, or updated, the watchdog will reload the vocabularies, first in the inactive database, then it will swap to the newly loaded database and load the vocabularies into the previously active (now inactive) database. 

Once the ValidationEngine has been initialized, code validations can be performed with the following methods on the ValidationEngine:

    public static DisplayNameValidationResult validateCodeSystem(String codeSystemName, String displayName, String code);
    public static DisplayNameValidationResult validateDisplayNameForCodeByCodeSystemName(String codeSystemName, String displayName, String code);
    public static DisplayNameValidationResult validateDisplayNameForCode(String codeSystem, String displayName, String code);
    public static boolean validateCodeByCodeSystemName(String codeSystemName, String code);
    public static boolean validateCode(String codeSystem, String code);
    public static boolean validateDisplayNameByCodeSystemName(String codeSystemName, String displayName);
    public static boolean validateDisplayName(String codeSystem, String displayName);
  
Code Value Set Repository
  The Validation Engine, and corresponding document loaders, require a specific directory hierarchy for the vocabulary code value set files. The hirearchy is as follows:
  
    code_repository
      ICD9CM_DX (files can be obtained from http://www.cms.gov/Medicare/Coding/ICD9ProviderDiagnosticCodes/codes.html)
      ICD9CM_SG (files can be obtained from http://www.cms.gov/Medicare/Coding/ICD9ProviderDiagnosticCodes/codes.html)
      ICD10CM (files can be obtained from http://www.cms.gov/Medicare/Coding/ICD10/2015-ICD-10-CM-and-GEMs.html)
      ICD10PCS (files can be obtained from http://www.cms.gov/Medicare/Coding/ICD10/2015-ICD-10-PCS-and-GEMs.html)
      LOINC (files can be obtained from http://loinc.org/downloads)
      RXNORM (files can be obtained from http://www.nlm.nih.gov/research/umls/rxnorm/docs/rxnormfiles.html)
      SNOMED-CT (files can be obtained from http://www.nlm.nih.gov/research/umls/Snomed/us_edition.html)