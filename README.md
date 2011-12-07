# Force.com REST API Connector

Lightweight library for building Force.com apps with OAuth authentication and data access through the Force.com REST API.

# Usage

You can either include as a dependency and pull the module from its github maven repo or you can build locally.

## Dependency on published release

The version number is bumped regularly. Check the tags list or commit messages for latest version.

### Maven

    <repositories>
        <repository>
            <id>force-rest-api</id>
            <name>force-rest-api repository on GitHub</name>
            <url>http://jesperfj.github.com/force-rest-api/repository/</url>            
        </repository>
    </repositories>

    ...
    
    <dependency>
        <groupId>com.force.api</groupId>
        <artifactId>force-rest-api</artifactId>
        <version>0.0.3</version>
    </dependency>

## Build and link locally

    $ git clone https://github.com/jesperfj/force-rest-api.git
    $ cd force-rest-api
    $ mvn install -DskipTests

### Maven

The latest HEAD always builds to a snapshot:

    <dependency>
        <groupId>com.force.api</groupId>
        <artifactId>force-rest-api</artifactId>
        <version>0.0.3-SNAPSHOT</version>
    </dependency>

## Write code

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

# Cutting a Release

This project uses the Maven release plugin with Github pages as Maven repo to facilitate quick releases. Snapshots are never released. They are reserved for local builds. See [Christian Kaltepoth's post](http://chkal.blogspot.com/2010/09/maven-repositories-on-github.html) for an excellent guide to setting up Github pages as a maven repo. To cut a release: (adjust repo references as necessary below if you're on a fork or doing this on your own repo)

### Clone this repo

(Adjust URL if you're on a fork or doing the same thing on another repo)

    $ git clone git@github.com:jesperfj/force-rest-api.git

### Clone the github pages branch of this repo into `release-repo` sub-dir

    $ cd force-rest-api
    $ git clone -b gh-pages git@github.com:jesperfj/force-rest-api.git release-repo

### Prepare a new release

Set up your test properties as described above in the testing section. Tests must pass. Edit `pom.xml` and adjust the SCM information to match your repo and github info if different from this one. Run

    $ mvn release:prepare

This will create a couple of commits and push to your github repo. A new release has now been created and snapshotted in github, but it has not yet actually been built and deployed.

### Build and deploy the new release

You can't use `mvn release:perform` because it will be missing your test fixture. Instead just do it manually. Check out the commit that was tagged as the new release. Something like:

    $ git checkout force-rest-api-0.0.4

Build and deploy

    $ mvn clean deploy -DupdateReleaseInfo

Commit the new release and push to gh-pages

    $ cd release-repo
    $ git add .
    $ git commit -m "Release 0.0.4"
    $ git push origin gh-pages

### Clean up

    $ cd ..
    $ mvn release:clean

(deletes temporary and backup files)

    $ git checkout master

gets you back to master from detached HEAD.


# Project Goals:

* Make it as thin as possible
* Few or no dependencies
* Other projects will handle generation of typed SObject classes and it should work here
* Automatic session renewal
* Pluggable JSON kit
* Make sure it's Spring friendly. [This solution](http://stackoverflow.com/questions/2901166/how-to-make-spring-accept-fluent-non-void-setters) may be necessary.

# License

BSD 3-clause license

# Author

Jesper Joergensen
