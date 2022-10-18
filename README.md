# Salesforce REST API Connector

Lightweight library for building Salesforce apps with OAuth authentication and data access through the Salesforce REST API.

# Usage

Releases are published on Maven Central. Include in your project with:

    <dependency>
        <groupId>com.frejo</groupId>
        <artifactId>force-rest-api</artifactId>
        <version>0.0.45</version>
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
     $ git checkout force-rest-api-0.0.41

## Authentication and Instantiation

### API versions

Salesforce updates its API version with every Salesforce release (3 times per year). The new version is supposed to always be backwards compatible, so in theory it is safe to always use the latest API version. However `force-rest-api` is designed to be conservative. The API version used may change with new versions of the library, but for a given version of the library, the version will always be `ApiVersion.DEFAULT_VERSION` unless you explicitly set it to something different. You set the API version when you instantiate an `ApiConfig`:

    ApiConfig mycfg = new ApiConfig().setApiVersionString("v99.0");

You can also use the `ApiVersion` enum to set the version:

    ApiConfig mycfg = new ApiConfig().setApiVersion(ApiVersion.V38);

But the enum may not always have the version you need and there is no particular benefit to using it compared to using a simple String.

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

### CRUD operations on root path

Sometimes you want to do CRUD operations without the standard `/services/data/<version>` path prefix. To do this you can get a ForceApi instance that uses root path:

    ForceApi api = new ForceApi(myConfig,mySession);
    api.rootPath().get("/services/apexrest/myApexClass");

`rootPath()` returns a new ForceApi instance that uses root path for the `get()`, `delete()`, `put()`, `post()`, `patch()` and `request()` methods.

## Working with API versions

You can inspect supported API versions and get more detailed info for each version using `SupportedVersions`:

    SupportedVersions versions = api.getSupportedVersions();
    System.out.println(versions.oldest());          // prints v20.0
    System.out.println(versions.contains("v25.0")); // prints true

The set of supported versions may vary based on where your organization is located. New versions are introduced 3 times a year and are rolled out gradually. During the rollout period, some organizations will have the latest version while others will not. The oldest supported version for REST API is v20.0. Salesforce API versions go further back than v20.0, but REST API does not support those older versions.

There is a direct mapping between season/year and version numbers. You can translate between season/year and version number in this way:

    ExtendedApiVersion v = new ExtendedApiversion(ExtendedApiVersion.Season.SPRING, 2012);
    System.out.println(v.getVersionString());       // prints v21.0

`ExtendedApiVersion` is called "Extended" because it goes beyond what `ApiVersion` offers and can represent more details about an API version, e.g. its season, year and URL base.

## Run Tests

This project has a mix of unit tests and integration tests that hit the actual API. To make the integration tests work you must set up a proper test fixture and reference it from environment variables. `.testenv.sample` contains a sample shell script indicating what variables must be set. Copy it to `.testenv` and once you have all the correct values, set it in the environment by running the shell command:

    source .testenv

### Login and password

You need credentials to a Force.com developer org to run tests against. These go in the `username` and `password` vaiables. Needless to say, don't use credentials for a production org containing sensitive data. If you don't have a developer org, [sign up for one](http://www.developerforce.com/events/regular/registration.php?d=70130000000EjHb). It's free. Remember to append the security token to your chosen password in the `password` variable.

### Client ID and Secret

Once you have signed up for an org, create a Connected App:

* In Setup, type "App" into Quick Find and select "App Manager"
* Click "New Connected App" in the upper right corner.
* Choose any name for your application
* Choose any callback URL (you'll need to set this properly when web server flow is supported)
* Choose some contact email
* Click "Save"
* Copy "Consumer Key" and set as the `clientId` environment variable
* Click on "Click to reveal" and copy "Consumer Secret" and set as the `clientSecret` environment variable

### Add `externalId__c` to Account SObject

Use the Force.com Web UI to add a custom field called `externalId__c` and mark it as an external ID field:

* (sorry, you have to figure out how to do this yourself. Will add instructions or automate it later)

### Create a second user for IP restrictions test

To test IP restrictions failure handling you need additional test setup:

* Go to Manage Users --> Profiles and create a new profile based on "Standard Platform User". Call it "IP Restricted User"
* Set Login IP Ranges for the new profile to something obscure like 1.1.1.1-1.1.1.1. Hit save and confirm that it's ok even though your user is not logged in from this range.
* Create a new user and reset password
* Log in as the new user and generate a security token
* Set username in the `iprestrictedUsername` env var and password (with token appended) in the `iprestrictedPassword` env var.
* Log back in with the admin user and go to Manage Users --> Profiles
* TODO: complete these instructions

### Run Tests

Before running the whole test suite, it is a good idea to run a single login test to check if the configuration is correct. If the username/password is not configured correctly, the test suite will trigger an account lock-out due to the many failed attempts. Run a single test such as `testSoapLogin` with:

    mvn -Dtest=com.force.api.AuthTest#testSoapLogin test


Now run tests with

    $ mvn test

You will see some log messages that look like errors or warnings. That's expected and does not indicate test failures. You can add debug logging with:

    $ mvn test -Dorg.slf4j.simpleLogger.defaultLogLevel=debug

### Interactive end-to-end OAuth handshake Test

This test is not run as part of the test suite because it requires manual intervention. Run it like this:

    mvn -Dtest=com.force.api.EndToEndOAuthFlowExample test

# Cutting a Release

This project now uses [Alex Fontaine's](http://axelfontaine.com/blog/final-nail.html) release process because the release plugin is a pretty insane piece of software that should never exist. The pom.xml version number checked into SCM is always `0-SNAPSHOT`. Mapping releases back to source code now relies on git tags only.

The project is set up to release to Maven Central. If you have forked it and want to deploy your own version, you will need to update groupId and set up your own Sonatype credentials and GPG. Assuming this is all correctly set up. Here's how you cut a new release:

First ensure all your code is checked in (with `git status` or the like). Then run tests one extra time and also test javadoc generation since it's easy to introduce errors in javadoc comments that will break the deploy:

    $ mvn test javadoc:javadoc

Note. You must have `JAVA_HOME` set for this to succeed. On Mac, set it with

    $ export JAVA_HOME=$(/usr/libexec/java_home)

Now find the latest version number with `git tag` (or in Maven central depending on what you trust most). Bump the version number to that plus one:

    $ mvn versions:set -DnewVersion=<new-version>

For example:

    $ mvn versions:set -DnewVersion=0.0.50

This will update pom.xml locally to the new version and leaving it uncommitted (which is what you want). Now run

    $ mvn scm:tag

This tags the local and remote repository with the full module name, e.g. force-rest-api-0.0.50. Now deploy:

    $ mvn clean deploy -DperformRelease

That command will fail if you don't have gpg installed. Install on MacOS with

    $ brew install gpg

When you're done, reset the local version change to pom.xml with:

    $ mvn versions:revert

Just as a validation, try to push local changes including tags:

    $ git push origin master --tags

There should be nothing to push. If something is messed up, delete the tags in Github and in your local repo and start over.

# Release History

## 0.0.45

* No major changes
* Default API version bumped to v55.
* Update Jetty version used for OAuth test (only a test change)

## 0.0.44

* Default API version bumped to v51. Added v50 and v51 to supported versions.
* Add ability to access root path with API calls `get put patch post delete request` using `ForceApi.rootPath()` (briefly explained in README). Thanks @ModeratelyComfortableChair for suggestion.
* login and password strings are now XML escaped when doing `soaploginPasswordFlow`. The characters `< > & ' "` are replaced with their &...; XML escape codes. Thanks @fredboutin for suggestion.
* JUnit test dependency bumped from 4.10 to 4.13.1
* Added test dependencies for jetty-util and jetty-http because apparently they are now needed.

## 0.0.43

* The full request is no longer logged on a bad request to prevent sensitive data from ending up in logs. Contributed by [faf0-addepar](https://github/faf0-addepar)
* Default API version bumped to v49 (Summer 2020). Code added to handle v46+ new behavior on upsert. 2021-03-19 NOTE: There was a bug here. Default version in 0.0.43 was actually v45.

## 0.0.42

* Add JsonAlias to support Platform Events. Contributed by [rgoers](https://github.com/rgoers).
* Note that default API version for this release is old: v45. Tests are failing on v46 and up due to some undocumented changes in response codes.

## 0.0.41

* Introduces [ForceApi.describeSObjectIfModified](https://github.com/jesperfj/force-rest-api/blob/75f718a2385d8daa42cee93c2b13d88b8dd4c5d9/src/main/java/com/force/api/ForceApi.java#L431) to make it more efficient to poll Salesforce for metadata changes to an SObject.
* Change jackson-databind version range to [2.9.10.3,) to address security alert
* This release is happening a long time after 0.0.40. It is possible a few other things might have changed.

## 0.0.40

* You can now specify a request timeout in [ApiConfig](src/main/java/com/force/api/ApiConfig.java).

## 0.0.39

* Change jackson-databind version range to [2.5,2.9] to address a problem where maven pulls in 3.0.0-SNAPSHOT and the compilation breaks.

## 0.0.38

* Introduces ability to use a custom Jackson ObjectMapper. This can be used to support JodaTime for example. It also allows developers to choose how null values should be treated during serialization and deserialization. Before, null values were always ignored which is not always what you want. The custom ObjectMapper is set on [ApiConfig](src/main/java/com/force/api/ApiConfig.java). It will be used everywhere in [ForceApi](src/main/java/com/force/api/ForceApi.java) and [ResourceRepresentation](src/main/java/com/force/api/ResourceRepresentation.java), but not in the [Auth](src/main/java/com/force/api/Auth.java) class.
* Allows any 2.x jackson-databind version 2.5 or newer. Tests have only been run with 2.5.0 and 2.9.1.

## 0.0.37

* Remove specific response code checks from generic REST api calls. Different resources may return different response codes on the same verb, e.g. POST to chatter resources returns 201, but POST to `/process/approvals/` return 200. The library already checks the bounds of the response code and throws an exception if it is not between 200 and 299. The strict check on response codes is considered a bug introduced in 0.0.35 and fixed with this release.

## 0.0.36

* Introduced [SessionRefreshListener](src/main/java/com/force/api/SessionRefreshListener.java) so you can register a listener and be notified when ForceApi refreshes the access token. See the [test](src/test/java/com/force/api/SessionRefreshTest.java) for sample code.

## 0.0.35

* Introduced generic REST api calls `get`, `delete`, `post`, `put` and `patch` on [ForceApi][forceapi] for any arbitrary path. This allows force-rest-api to be used for the many non-sObject resources exposed in Force.com REST API. See [ChatterTest](src/test/java/com/force/api/chatter/ChatterTest.java) for an example.
* Added `getSession()` convenience method on [ForceApi][forceapi] as requested by several people. It took me a little while to become comfortable with it.
* Added `curlHelper()` convenience method on [ForceApi][forceapi] to easily print a curl command string with valid access token for debugging purposes.

## 0.0.34

* Introduced `ForceApi.getSupportedVersions` and friends to enabled more advanced version handling. Thanks to @cswendrowski for the contributions. See "Working with API versions" in this README.

## 0.0.33

* Update to Salesforce API v39

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

[forceapi]: src/main/java/com/force/api/ForceApi.java
