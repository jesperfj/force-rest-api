# Force.com REST API Connector

Lightweight library for building Force.com apps with OAuth authentication and data access through the Force.com REST API.

# Usage

Releases are published on Maven Central. Include in your project with:

    <dependency>
        <groupId>com.frejo</groupId>
        <artifactId>force-rest-api</artifactId>
        <version>0.0.28</version>
    </dependency>

## Build and link locally

    $ git clone https://github.com/jesperfj/force-rest-api.git
    $ cd force-rest-api
    $ mvn install -DskipTests

The version number is never updated in SCM. So builds will always produce a module with version 0-SNAPSHOT. Add it as a dependency to your local builds with:

    <dependency>
        <groupId>com.frejo</groupId>
        <artifactId>force-rest-api</artifactId>
        <version>0-SNAPSHOT</version>
    </dependency>

To check out the source code for a particular version found in Maven Central, use the corresponding git tag, e.g:

     $ git clone https://github.com/jesperfj/force-rest-api.git
     $ cd force-rest-api
     $ git checkout force-rest-api-0.0.28

## Authentication and Instantiation

### Username / Password Authentication

Authenticate using just login and password:

    ForceApi api = new ForceApi(new ApiConfig()
        .setUsername("user@domain.com")
        .setPassword("password"));

### OAuth Username/Password Authentication Flow

As [documented here](https://help.salesforce.com/help/doc/en/remoteaccess_oauth_username_password_flow.htm)

    ForceApi api = new ForceApi(new ApiConfig()
        .setUsername("user@domain.com")
        .setPassword("password")
        .setClientId("longclientidalphanumstring")
        .setClientSecret("notsolongnumeric"));

### OAuth Web Server Flow

As [documented here](https://help.salesforce.com/help/doc/en/remoteaccess_oauth_web_server_flow.htm)

    String url = Auth.startOAuthWebServerFlow(new AuthorizationRequest()
    	.apiConfig(new ApiConfig()
    		.setClientId("longclientidalphanumstring")
    		.setRedirectURI("https://myapp.mydomain.com/oauth"))
    	.state("mystate"));

    // redirect browser to url
    // Browser will get redirected back to your app after user authentication at
    // https://myapp.mydomain.com/oauth with a code parameter. Now do:

	ApiSession s = Auth.completeOAuthWebServerFlow(new AuthorizationResponse()
		.apiConfig(new ApiConfig()
			.setClientId("longclientidalphanumstring")
			.setClientSecret("notsolongnumeric")
			.setRedirectURI("https://myapp.mydomain.com/oauth"))
		.code("alphanumericstringpassedbackinbrowserrequest"));
    
    ForceApi api = new ForceApi(s.getApiConfig(),s);


### Instantiate with existing accessToken and endpoint

If you already have an access token and endpoint (e.g. from a cookie), you can pass an ApiSession instance to ForceApi:

    ApiConfig c = new ApiConfig()
        .setRefreshToken("refreshtoken")
        .setClientId("longclientidalphanumstring")
        .setClientSecret("notsolongnumeric"),
    
    ApiSession s = new ApiSession()
	    .setApiConfig(c)
	    .setAccessToken("accessToken")
	    .setApiEndpoint("apiEndpoint");
    
    ForceApi api = new ForceApi(c,s);

## CRUD and Query Operations

### Get an SObject

    Account res = api.getSObject("Account", "001D000000INjVe").as(Account.class);

This assumes you have an Account class defined with proper Jackson deserialization annotations. For example:

    import org.codehaus.jackson.annotate.JsonIgnoreProperties;
    import org.codehaus.jackson.annotate.JsonProperty;

    @JsonIgnoreProperties(ignoreUnknown=true)
    public class Account {

    	@JsonProperty(value="Id")
    	String id;
    	@JsonProperty(value="Name")
    	String name;
    	@JsonProperty(value="AnnualRevenue")
    	private Double annualRevenue;
    	@JsonProperty(value="externalId__c")
    	String externalId;	
	
    	public String getId() { return id; }
    	public void setId(String id) { this.id = id; }
    	public String getName() { return name; }
    	public void setName(String name) { this.name = name; }
    	public Double getAnnualRevenue() { return annualRevenue; }
    	public void setAnnualRevenue(Double value) { annualRevenue = value; }
    	public String getExternalId() { return externalId; }
    	public void setExternalId(String externalId) { this.externalId = externalId; }
    }

### Create SObject

    Account a = new Account();
    a.setName("Test account");
    String id = api.createSObject("account", a);

### Update SObject

    a.setName("Updated Test Account");
    api.updateSObject("account", id, a);

### Create or Update SObject

    a = new Account();
    a.setName("Perhaps existing account");
    a.setAnnualRevenue(3141592.65);
    api.createOrUpdateSObject("account", "externalId__c", "1234", a);

### Delete an SObject

    api.deleteSObject("account", id);

### Query SObjects

    QueryResult<Account> res = api.query("SELECT id FROM Account WHERE name LIKE 'Test account%'", Account.class);


## Run Tests

This project currently only has integration-y tests (they hit the actual API). To make them work copy `src/test/resources/test.properties.sample` to `src/test/resources/test.properties` and replace the properties in the file with actual values.

### Login and password

Add your Force.com developer org login and password. Needless to say, don't use credentials for a production org containing sensitive data. If you don't have a developer org, [sign up for one](http://www.developerforce.com/events/regular/registration.php?d=70130000000EjHb). It's free.

### Client ID and Secret

Once you have signed up for an org, navigate to the Remote Access Setup:

* Click on "Admin User" drop-down in upper-right
* Select Setup
* In the left-side navigation pane, under "App Setup", click on "Develop"
* Select "Remote Access"

Now create a new Remote Access Application:

* Click on "New"
* Choose any name for your application
* Choose any callback URL (you'll need to set this properly when web server flow is supported)
* Choose some contact email
* Click "Save"
* Copy "Consumer Key" to the property "clientId" in test.properties
* Click on "Click to reveal" and copy "Consumer Secret" to "clientSecret" in test.properties

### Add `externalId__c` to Account SObject

Use the Force.com Web UI to add a custom field called `externalId__c` and mark it as an external ID field:

* (sorry, you have to figure out how to do this yourself. Will add instructions or automate it later)

### Create a second user for IP restrictions test

To test IP restrictions failure handling you need additional test setup:

* Go to Manage Users --> Profiles and create a new profile based on "Standard Platform User". Call it "IP Restricted User"
* Set Login IP Ranges for the new profile to something obscure like 1.1.1.1-1.1.1.1. Hit save and confirm that it's ok even though your user is not logged in from this range.
* Create a new user and reset password
* Log in as the new user and generate a security token
* Set username and password (with token appended) in test.properties.
* Log back in with the admin user and go to Manage Users --> Profiles

### Run Tests

Now run tests with

    $ mvn test

You will see some log messages that look like errors or warnings. That's expected and does not indicate test failures.

### Interactive end-to-end OAuth handshake Test

This test is not run as part of the test suite because it requires manual intervention. Run it like this:

    mvn -Dtest=com.force.api.EndToEndOAuthFlowExample test

# Cutting a Release

This project now uses [Alex Fontaine's](http://axelfontaine.com/blog/final-nail.html) release process because the release plugin is a pretty insane piece of software that should never exist. The pom.xml version number checked into SCM is always `0-SNAPSHOT`. Mapping releases back to source code now relies on git tags only.

The project is set up to release to Maven Central. If you have forked it and want to deploy your own version, you will need to update groupId and set up your own Sonatype credentials and GPG. Assuming this is all correctly set up. Here's how you cut a new release:

First ensure all your code is checked in (with `git status` or the like). Then find the latest version number with `git tag` (or in Maven central depending on what you trust most). Bump the version number to that plus one:

    $ mvn versions:set -DnewVersion=<new-version> scm:tag

For example:

    $ mvn versions:set -DnewVersion=0.0.50 scm:tag

This will update pom.xml locally to the new version and tag the repo with the new version number. Now deploy:

    $ mvn clean deploy -DperformRelease

When you're done, reset the local version change to pom.xml with:

    $ mvn versions:revert

Remember to push your recent changes and tags to origin:

    $ git push origin master


# Release History

## 0.0.32

* Add explicit authentication error handling. Addresses issue #32

## 0.0.31

* Update to v37
* Add queryAll

## 0.0.30

* Fix NullPointerException in ApiConfig.setForceURL. Thanks [steventamm](https://github.com/steventamm).

## 0.0.29

* Update to Force.com API version 36

## 0.0.28

* No feature changes
* Project now configured to release to Maven Central
* No longer uses maven-release-plugin
* Version number in source code is always 0-SNAPSHOT
* Use git tags to map from Maven Central version to corresponding source code

## 0.0.23

* Upgrade to Jackson 2. Thanks to [emckissick](https://github.com/emckissick) for the pull request.

## 0.0.22

* Include Javadoc in release jars

## 0.0.21

* Made various fixes to get tests passing again after a long period of inactivity
* end-to-end oauth test has been renamed to exlude it from test suite. Run it manually instead. It no longer uses HtmlUnit but instead requires manual intervention
* ApiVersion is now up to date up to v33.0.
* API version can now be set as a string. Setting it as an ApiVersion enum has been deprecated. There doesn't seem to be much value in strongly typing the api version.

## 0.0.20

* [thysmichels](https://github.com/thysmichels) noticed that Spring 14 broke this library because [Identity.java](src/main/java/com/force/api/Identity.java) was set to strictly map to the underlying JSON resource. This class now uses `ignoreUnknown=true` so it should be more robust to changes.

## 0.0.19

* [ryanbrainard](https://github.com/ryanbrainard) added QueryMore and various other enhancements to make it work better with [RichSObjects](https://github.com/ryanbrainard/richsobjects)
* [Burn0ut07](https://github.com/Burn0ut07) added PickListEntry support to DescribeSObject

## 0.0.18

* Some relationship queries work now. See QueryTest for an example.
* Tested with Jackson 1.9.7

## 0.0.17

* Modified deserialization of query results to better supper queries that return graphs of records.

## 0.0.16

* Added more testing, including an end-to-end oauth flow test using HtmlUnit
* Scope is now an enum

## 0.0.15

* ApiSession now serializable, so it can be cached in Memcached and similar

## 0.0.14

* Fixed bug in DescribeSObject. Had inlineHelpText as boolean instead of String

## 0.0.13

* More complete DescribeSObject. Can now be used to generate Java classes. An example can be found in the tests based on [PojoCodeGenerator](https://github.com/forcedotcom/wsc/blob/master/src/main/java/com/sforce/rest/tools/PojoCodeGenerator.java)

## 0.0.12

* 0.0.11 broke describeSObject. Fixed now and added test

## 0.0.11

0.0.10 was botched. Missed a checkin

## 0.0.10

* Basic exceptions
* Some internal refactorings
* First attempt at session renewal

## 0.0.9

* Minimalistic Describe

## 0.0.8

* Added revoke support ([read more](http://blogs.developerforce.com/developer-relations/2011/11/revoking-oauth-2-0-access-tokens-and-refresh-tokens.html))
* Refactored refreshToken out of ApiConfig

## 0.0.7

* Added support for OAuth refresh_token flow
* Added a bit more debug info to createSObject
* Should work with Jackson 1.9.1 and 1.9.2. Both are accepted in the version range

## 0.0.6

* Tested with Winter '12, API version 23
* Requires (and explicitly declares dependency on) Jackson 1.9.1. Not tested with other Jackson versions.
* Basic CRUD and query functionality for SObjects
* OAuth functionality that covers all Force.com options
* Only happy path tested, almost no error scenarios or edge cases covered except for some sporadic debug output
* Focused on typed access. But you must build SObject classes manually for now (or use builders available elsewhere)

# Project Goals:

* Make it as thin as possible
  * Status: Both ForceApi and Auth classes are very thin wrappers on top of the APIs.
* Few or no dependencies
  * Status: Currently only depends on Jackson. Could consider supporting gson as well for added flexibility
* Other projects will handle generation of typed SObject classes and it should work here
* Automatic session renewal
  * Status: Added in 0.0.10 and testable in 0.0.12. Waiting for feedback to see if it works.
* Pluggable JSON kit
  * Status: Not yet. This is currently low priority
* Make sure it's Spring friendly. [This solution](http://stackoverflow.com/questions/2901166/how-to-make-spring-accept-fluent-non-void-setters) may be necessary.
  * Status: No Spring work has been done yet
* Consider adding newrelic hooks.

# License

[BSD 2-clause license](http://opensource.org/licenses/bsd-license.php)

# Author

Jesper Joergensen
