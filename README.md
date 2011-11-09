# Force.com REST API Connector

Current status: First commit. Lots of stuff not implemented.

Goals:

* Make it as thin as possible
* Few or no dependencies
* Other projects will handle generation of typed SObject classes and it should work here
* Automatic session renewal
* Pluggable JSON kit

## Usage

This module is not yet in a Maven repository. So you must clone and install in your local repo:

    $ git clone https://github.com/jesperfj/force-rest-api.git
    $ cd force-rest-api
    $ mvn install

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

    DataApi api = new DataApi(new ApiConfig()
        .setUsername("user@domain.com")
        .setPassword("password")
        .setClientId("longclientidalphanumstring")
        .setClientSecret("notsolongnumeric"));

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

