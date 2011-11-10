# Force.com REST API Connector

Current status: First commit. Lots of stuff not implemented.

Goals:

* Make it as thin as possible
* Few or no dependencies
* Other projects will handle generation of typed SObject classes and it should work here
* Automatic session renewal
* Pluggable JSON kit
* Make sure it's Spring friendly. [This solution](http://stackoverflow.com/questions/2901166/how-to-make-spring-accept-fluent-non-void-setters) may be necessary.

## Usage

This module is not yet in a Maven repository. So you must clone and install in your local repo:

    $ git clone https://github.com/jesperfj/force-rest-api.git
    $ cd force-rest-api
    $ mvn install -DskipTests

Add as dependency to your project

### Maven

    <dependency>
        <groupId>com.force.api</groupId>
        <artifactId>force-rest-api</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>

### Gradle

    compile 'com.force.api:force-rest-api:0.0.1-SNAPSHOT'

### Username / Password Authentication

Authenticate using just login and password and get an account record:

    DataApi api = new DataApi(new ApiConfig()
        .setUsername("user@domain.com")
        .setPassword("password"));

### Get an SObject

    Account a = api.get(new SObjectResource()
        .setId("001aaaaaaaaaaaAXY")
    	.setType("Account")).as(Account.class);

This assumes you have an Account class defined with proper Jackson deserialization annotations. For example:

    import org.codehaus.jackson.annotate.JsonIgnoreProperties;
    import org.codehaus.jackson.annotate.JsonSetter;

    @JsonIgnoreProperties(ignoreUnknown=true)
    public class Account {

    	String id;
    	String name;
    	public String getId() {return id;}
    	public String getName() {return name;}
	
    	@JsonSetter(value="Id")
    	public void setId(String id) {this.id = id;}

    	@JsonSetter(value="Name")
    	public void setName(String name) {this.name = name;}
    }


### OAuth Username/Password Authentication Flow

As [documented here](https://help.salesforce.com/help/doc/en/remoteaccess_oauth_username_password_flow.htm)

    DataApi api = new DataApi(new ApiConfig()
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
    
    DataApi api = new DataApi(s.getApiConfig(),s);


### Instantiate with existing accessToken and endpoint

If you already have an access token and endpoint (e.g. from a cookie), you can pass an ApiSession instance to DataApi:

    ApiConfig c = new ApiConfig()
        .setRefreshToken("refreshtoken")
        .setClientId("longclientidalphanumstring")
        .setClientSecret("notsolongnumeric"),
    
    ApiSession s = new ApiSession()
	    .setApiConfig(c)
	    .setAccessToken("accessToken")
	    .setApiEndpoint("apiEndpoint");
    
    DataApi api = new DataApi(c,s);

## Run Tests

This project currently only has integration-y tests (they hit the actual API). To make them work copy `src/test/resources/test.properties.sample` to `src/test/resources/test.properties` and replace the properties in the file with actual values

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

### Set a Test Account ID

* Click on the drop-down in the top right corner and select "Sales"
* Select the "Accounts" tab
* In the "View" drop-down, select "All Accounts"
* Click on one of the accounts in the list
* Copy the Path part of the URL in your Browser's location. It should look something like this: "001A000000h6kkf".
* Set "accountId" in test.properties to this value

Now run tests with

    $ mvn test





