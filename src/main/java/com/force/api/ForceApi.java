package com.force.api;

import com.force.api.http.Http;
import com.force.api.http.HttpRequest;
import com.force.api.http.HttpResponse;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

	private static final ObjectMapper jsonMapper;

	static {
		jsonMapper = new ObjectMapper();
		jsonMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
	}

	final ApiConfig config;
	ApiSession session;
	private boolean autoRenew = false;

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


	public Identity getIdentity() {
		try {

			@SuppressWarnings("unchecked")
			Map<String,Object> resp = jsonMapper.readValue(
					apiRequest(new HttpRequest()
						.url(uriBase())
						.method("GET")
						.header("Accept", "application/json")
					).getStream(),Map.class);

			return jsonMapper.readValue(
					apiRequest(new HttpRequest()
						.url((String) resp.get("identity"))
						.method("GET")
						.header("Accept", "application/json")
					).getStream(), Identity.class);
		} catch (JsonParseException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

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
		try {
			// We're trying to keep Http classes clean with no reference to JSON/Jackson
			// Therefore, we serialize to bytes before we pass object to HttpRequest().
			// But it would be nice to have a streaming implementation. We can do that
			// by using ObjectMapper.writeValue() passing in output stream, but then we have
			// polluted the Http layer.
			CreateResponse result = jsonMapper.readValue(apiRequest(new HttpRequest()
					.url(uriBase()+"/sobjects/"+type)
					.method("POST")
					.header("Accept", "application/json")
					.header("Content-Type", "application/json")
					.expectsCode(201)
					.content(jsonMapper.writeValueAsBytes(sObject))).getStream(),CreateResponse.class);

			if (result.isSuccess()) {
				return (result.getId());
			} else {
				throw new SObjectException(result.getErrors());
			}
		} catch (JsonGenerationException e) {
			throw new ResourceException(e);
		} catch (JsonMappingException e) {
			throw new ResourceException(e);
		} catch (IOException e) {
			throw new ResourceException(e);
		}
	}

	public void updateSObject(String type, String id, Object sObject) {
		try {
			// See createSObject for note on streaming ambition
			apiRequest(new HttpRequest()
				.url(uriBase()+"/sobjects/"+type+"/"+id+"?_HttpMethod=PATCH")
				.method("POST")
				.header("Accept", "application/json")
				.header("Content-Type", "application/json")
				.expectsCode(204)
				.content(jsonMapper.writeValueAsBytes(sObject))
			);
		} catch (JsonGenerationException e) {
			throw new ResourceException(e);
		} catch (JsonMappingException e) {
			throw new ResourceException(e);
		} catch (IOException e) {
			throw new ResourceException(e);
		}
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
			HttpResponse res =
				apiRequest(new HttpRequest()
					.url(uriBase()+"/sobjects/"+type+"/"+externalIdField+"/"+URLEncoder.encode(externalIdValue,"UTF-8")+"?_HttpMethod=PATCH")
					.method("POST")
					.header("Accept", "application/json")
					.header("Content-Type", "application/json")
					.content(jsonMapper.writeValueAsBytes(sObject))
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

		} catch (JsonGenerationException e) {
			throw new ResourceException(e);
		} catch (JsonMappingException e) {
			throw new ResourceException(e);
		} catch (IOException e) {
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
        try {
            HttpResponse res = apiRequest(new HttpRequest()
                    .url(queryUrl)
                    .method("GET")
                    .header("Accept", "application/json")
                    .expectsCode(200));

            // We build the result manually, because we can't pass the type information easily into
            // the JSON parser mechanism.

            QueryResult<T> result = new QueryResult<T>();
            JsonNode root = jsonMapper.readTree(res.getStream());
            result.setDone(root.get("done").getBooleanValue());
            result.setTotalSize(root.get("totalSize").getIntValue());
            if (root.get("nextRecordsUrl") != null) {
                result.setNextRecordsUrl(root.get("nextRecordsUrl").getTextValue());
            }
            List<T> records = new ArrayList<T>();
            for (JsonNode elem : root.get("records")) {
                records.add(jsonMapper.readValue(normalizeCompositeResponse(elem), clazz));
            }
            result.setRecords(records);
            return result;
        } catch (JsonParseException e) {
            throw new ResourceException(e);
        } catch (JsonMappingException e) {
            throw new ResourceException(e);
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }

    public DescribeGlobal describeGlobal() {
		try {
			return jsonMapper.readValue(apiRequest(new HttpRequest()
					.url(uriBase()+"/sobjects/")
					.method("GET")
					.header("Accept", "application/json")).getStream(),DescribeGlobal.class);
		} catch (JsonParseException e) {
			throw new ResourceException(e);
		} catch (JsonMappingException e) {
			throw new ResourceException(e);
		} catch (UnsupportedEncodingException e) {
			throw new ResourceException(e);
		} catch (IOException e) {
			throw new ResourceException(e);
		}
	}

    public <T> DiscoverSObject<T> discoverSObject(String sobject, Class<T> clazz) {
        try {
            HttpResponse res = apiRequest(new HttpRequest()
                    .url(uriBase() + "/sobjects/" + sobject)
                    .method("GET")
                    .header("Accept", "application/json")
                    .expectsCode(200));

            final JsonNode root = jsonMapper.readTree(res.getStream());
            final DescribeSObjectBasic describeSObjectBasic = jsonMapper.readValue(root.get("objectDescribe"), DescribeSObjectBasic.class);
            final List<T> recentItems = new ArrayList<T>();
            for(JsonNode item : root.get("recentItems")) {
                recentItems.add(jsonMapper.readValue(item, clazz));
            }
            return new DiscoverSObject<T>(describeSObjectBasic, recentItems);
        } catch (JsonParseException e) {
            throw new ResourceException(e);
        } catch (JsonMappingException e) {
            throw new ResourceException(e);
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }

	public DescribeSObject describeSObject(String sobject) {
		try {
			return jsonMapper.readValue(apiRequest(new HttpRequest()
					.url(uriBase()+"/sobjects/"+sobject+"/describe")
					.method("GET")
					.header("Accept", "application/json")).getStream(),DescribeSObject.class);
		} catch (JsonParseException e) {
			throw new ResourceException(e);
		} catch (JsonMappingException e) {
			throw new ResourceException(e);
		} catch (UnsupportedEncodingException e) {
			throw new ResourceException(e);
		} catch (IOException e) {
			throw new ResourceException(e);
		}
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
	 * This allows Jackson to deserialize the response into it's corresponding Object representation
	 * 
	 * @param node 
	 * @return
	 */
	private final JsonNode normalizeCompositeResponse(JsonNode node){
		Iterator<Entry<String, JsonNode>> elements = node.getFields();
		ObjectNode newNode = JsonNodeFactory.instance.objectNode();
		Entry<String, JsonNode> currNode;
		while(elements.hasNext()){
			currNode = elements.next();

			newNode.put(currNode.getKey(), 
						(		currNode.getValue().isObject() && 
								currNode.getValue().get("records")!=null
						)?
								currNode.getValue().get("records"):
									currNode.getValue()
					);
		}
		return newNode;
		
	}
}
