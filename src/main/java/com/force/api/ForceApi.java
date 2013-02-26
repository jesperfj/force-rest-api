package com.force.api;

import com.force.api.http.Http;
import com.force.api.http.HttpRequest;
import com.force.api.http.HttpResponse;
import com.google.gson.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.Map.Entry;

/**
 * main class for making API calls.
 *
 * This class is cheap to instantiate and throw away. It holds a user's session
 * as state and thus should never be reused across multiple user sessions,
 * unless that's explicitly what you want to do.
 *
 * For web apps, you should instantiate this class on every request and feed it
 * the session information as obtained from a session cookie or similar. An
 * exception to this rule is if you make all API calls as a single API user.
 * Then you can keep a static reference to this class.
 *
 * @author jjoergensen
 *
 */
public class ForceApi {

	final ApiConfig config;
	ApiSession session;
	private boolean autoRenew = false;

    final static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();

	public ForceApi(ApiConfig config, ApiSession session) {
		this.config = config;
		this.session = session;
		if(session.getRefreshToken()!=null) {
			autoRenew = true;
		}
	}

	public ForceApi(ApiSession session) {
		this(new ApiConfig(), session);
	}

	public ForceApi(ApiConfig apiConfig) {
		config = apiConfig;
		session = Auth.authenticate(apiConfig);
		autoRenew  = true;

	}


    public Identity getIdentity()
    {
        @SuppressWarnings("unchecked")
        HttpResponse response = apiRequest(new HttpRequest()
                .url(uriBase())
                .method("GET")
                .header("Accept", "application/json")
        );
        Map<String, Object> resp = gson.fromJson(response.getString(), Map.class);

        return gson.fromJson(
                apiRequest(new HttpRequest()
                        .url((String) resp.get("identity"))
                        .method("GET")
                        .header("Accept", "application/json")
                ).getString(), Identity.class);
    }


	public ResourceRepresentation getSObject(String type, String id) throws ResourceException {
		// Should we return null or throw an exception if the record is not found?
		// Right now will just throw crazy runtimeexception with no explanation
		return new ResourceRepresentation(apiRequest(new HttpRequest()
					.url(uriBase()+"/sobjects/"+type+"/"+id)
					.method("GET")
					.header("Accept", "application/json")));
	}

	public String createSObject(String type, Object sObject) {

			// We're trying to keep Http classes clean with no reference to JSON
			// Therefore, we serialize to bytes before we pass object to HttpRequest().
			// But it would be nice to have a streaming implementation. We can do that
			// by using ObjectMapper.writeValue() passing in output stream, but then we have
			// polluted the Http layer.
            byte[] sObjectAsJsonBytes = gson.toJson(sObject).getBytes();
 			CreateResponse result = gson.fromJson(apiRequest(new HttpRequest()
					.url(uriBase()+"/sobjects/"+type)
					.method("POST")
					.header("Accept", "application/json")
					.header("Content-Type", "application/json")
					.expectsCode(201)
					.content(sObjectAsJsonBytes)).getString(),CreateResponse.class);

			if (result.isSuccess()) {
				return (result.getId());
			} else {
				throw new SObjectException(result.getErrors());
			}

	}

	public void updateSObject(String type, String id, Object sObject) {

			// See createSObject for note on streaming ambition
            byte[] sObjectAsJsonBytes = gson.toJson(sObject).getBytes();
			apiRequest(new HttpRequest()
				.url(uriBase()+"/sobjects/"+type+"/"+id+"?_HttpMethod=PATCH")
				.method("POST")
				.header("Accept", "application/json")
				.header("Content-Type", "application/json")
				.expectsCode(204)
				.content(sObjectAsJsonBytes)
			);

	}

	public void deleteSObject(String type, String id) {
		apiRequest(new HttpRequest()
			.url(uriBase()+"/sobjects/"+type+"/"+id)
			.method("DELETE")
		);
	}

	public CreateOrUpdateResult createOrUpdateSObject(String type, String externalIdField, String externalIdValue, Object sObject) {
		try {
			// See createSObject for note on streaming ambition
            byte[] sObjectAsJsonBytes = gson.toJson(sObject).getBytes();
			HttpResponse res =
				apiRequest(new HttpRequest()
					.url(uriBase()+"/sobjects/"+type+"/"+externalIdField+"/"+URLEncoder.encode(externalIdValue,"UTF-8")+"?_HttpMethod=PATCH")
					.method("POST")
					.header("Accept", "application/json")
					.header("Content-Type", "application/json")
					.content(sObjectAsJsonBytes)
				);
			if(res.getResponseCode()==201) {
				return CreateOrUpdateResult.CREATED;
			} else if(res.getResponseCode()==204) {
				return CreateOrUpdateResult.UPDATED;
			} else {
				System.out.println("Code: "+res.getResponseCode());
				System.out.println("Message: "+res.getString());
				throw new RuntimeException();
			}

		}  catch (IOException e) {
			throw new ResourceException(e);
		}
	}

	public <T> QueryResult<T> query(String query, Class<T> clazz) {
        try {
            return queryAny(uriBase() + "/query/?q=" + URLEncoder.encode(query, "UTF-8"), clazz);
        } catch (UnsupportedEncodingException e) {
            throw new ResourceException(e);
        }
    }

	public QueryResult<Map> query(String query) {
		return query(query, Map.class);
	}

    public <T> QueryResult<T> queryMore(String nextRecordsUrl, Class<T> clazz) {
        return queryAny(session.getApiEndpoint() + nextRecordsUrl, clazz);
    }

    public QueryResult<Map> queryMore(String nextRecordsUrl) {
        return queryMore(nextRecordsUrl, Map.class);
    }


    private <T> QueryResult<T> queryAny(String queryUrl, Class<T> clazz) {

        HttpResponse res = apiRequest(new HttpRequest()
                .url(queryUrl)
                .method("GET")
                .header("Accept", "application/json")
                .expectsCode(200));

        // We build the result manually, because we can't pass the type information easily into
        // the JSON parser mechanism.

        QueryResult<T> result = new QueryResult<T>();
        JsonObject root = new JsonParser().parse(res.getString()).getAsJsonObject();
        result.setDone(root.get("done").getAsBoolean());
        result.setTotalSize(root.get("totalSize").getAsInt());
        if (root.get("nextRecordsUrl") != null)
        {
            result.setNextRecordsUrl(root.get("nextRecordsUrl").getAsString());
        }
        List<T> records = new ArrayList<T>();
        for (JsonElement elem : root.get("records").getAsJsonArray())
        {
            records.add(gson.fromJson(normalizeCompositeResponse(elem.getAsJsonObject()), clazz));
        }
        result.setRecords(records);
        return result;

    }

    public DescribeGlobal describeGlobal()
    {
        return gson.fromJson(apiRequest(new HttpRequest()
                .url(uriBase() + "/sobjects/")
                .method("GET")
                .header("Accept", "application/json")).getString(), DescribeGlobal.class);
    }

    public <T> DiscoverSObject<T> discoverSObject(String sobject, Class<T> clazz)
    {
        HttpResponse res = apiRequest(new HttpRequest()
                .url(uriBase() + "/sobjects/" + sobject)
                .method("GET")
                .header("Accept", "application/json")
                .expectsCode(200));

        JsonObject root = new JsonParser().parse(res.getString()).getAsJsonObject();

        final DescribeSObjectBasic describeSObjectBasic = gson.fromJson(root.get("objectDescribe"), DescribeSObjectBasic.class);
        final List<T> recentItems = new ArrayList<T>();
        for (JsonElement item : root.get("recentItems").getAsJsonArray())
        {
            recentItems.add(gson.fromJson(item, clazz));
        }
        return new DiscoverSObject<T>(describeSObjectBasic, recentItems);
    }

    public DescribeSObject describeSObject(String sobject)
    {
       return gson.fromJson(apiRequest(new HttpRequest()
                .url(uriBase() + "/sobjects/" + sobject + "/describe")
                .method("GET")
                .header("Accept", "application/json")).getString(), DescribeSObject.class);

    }

    private final String uriBase() {
		return(session.getApiEndpoint()+"/services/data/"+config.getApiVersion());
	}
	
	private final HttpResponse apiRequest(HttpRequest req) {
		req.setAuthorization("OAuth "+session.getAccessToken());
		HttpResponse res = Http.send(req);
		if(res.getResponseCode()==401) {
			// Perform one attempt to auto renew session if possible
			if(autoRenew) {
				System.out.println("Session expired. Refreshing session...");
				if(session.getRefreshToken()!=null) {
					session = Auth.refreshOauthTokenFlow(config, session.getRefreshToken());
				} else {
					session = Auth.authenticate(config);
				}
				req.setAuthorization("OAuth "+session.getAccessToken());
				res = Http.send(req);
			}
		}
		if(res.getResponseCode()>299) {
			if(res.getResponseCode()==401) {
				throw new ApiTokenException(res.getString());
			} else {
				throw new ApiException(res.getResponseCode(), res.getString());
			}
		} else if(req.getExpectedCode()!=-1 && res.getResponseCode()!=req.getExpectedCode()) {
			throw new RuntimeException("Unexpected response from Force API. Got response code "+res.getResponseCode()+
					". Was expecing "+req.getExpectedCode());
		} else {
			return res;
		}
	}
	
	/**
	 * Normalizes the JSON response in case it contains responses from
	 * Relationsip queries. For e.g.
	 * 
	 * <code>
	 * Query:
	 *   select Id,Name,(select Id,Email,FirstName from Contacts) from Account
	 *   
	 * Json Response Returned:
	 * 
	 * {
	 *	  "totalSize" : 1,
	 *	  "done" : true,
	 *	  "records" : [ {
	 *	    "attributes" : {
	 *	      "type" : "Account",
	 *	      "url" : "/services/data/v24.0/sobjects/Account/0017000000TcinJAAR"
	 *	    },
	 *	    "Id" : "0017000000TcinJAAR",
	 *	    "Name" : "test_acc_04_01",
	 *	    "Contacts" : {
	 *	      "totalSize" : 1,
	 *	      "done" : true,
	 *	      "records" : [ {
	 *	        "attributes" : {
	 *	          "type" : "Contact",
	 *	          "url" : "/services/data/v24.0/sobjects/Contact/0037000000zcgHwAAI"
	 *	        },
	 *	        "Id" : "0037000000zcgHwAAI",
	 *	        "Email" : "contact@email.com",
	 *	        "FirstName" : "John"
	 *	      } ]
	 *	    }
	 *	  } ]
	 *	}
	 * </code>
	 * 
	 * Will get normalized to:
	 * 
	 * <code>
	 * {
	 *	  "totalSize" : 1,
	 *	  "done" : true,
	 *	  "records" : [ {
	 *	    "attributes" : {
	 *	      "type" : "Account",
	 *	      "url" : "/services/data/v24.0/sobjects/Account/accountId"
	 *	    },
	 *	    "Id" : "accountId",
	 *	    "Name" : "test_acc_04_01",
	 *	    "Contacts" : [ {
	 *	        "attributes" : {
	 *	          "type" : "Contact",
	 *	          "url" : "/services/data/v24.0/sobjects/Contact/contactId"
	 *	        },
	 *	        "Id" : "contactId",
	 *	        "Email" : "contact@email.com",
	 *	        "FirstName" : "John"
	 *	    } ]
	 *	  } ]
	 *	} 
	 * </code
	 * 
	 * This allows Gson to deserialize the response into it's corresponding Object representation
	 * 
	 * @param node 
	 * @return
	 */
	private final JsonObject  normalizeCompositeResponse(JsonObject node){

        Set<Entry<String, JsonElement>> elements= node.entrySet();
		JsonObject newNode = new JsonObject();
        for (Entry<String, JsonElement> currNode : elements)
        {
			newNode.add(currNode.getKey(),
						(		currNode.getValue().isJsonObject() &&
								currNode.getValue().getAsJsonObject().get("records")!=null
						)?
                                currNode.getValue().getAsJsonObject().get("records"):
									currNode.getValue()
					);
		}
		return newNode;
	}
}
