package com.force.api;

import com.force.api.http.Http;
import com.force.api.http.HttpRequest;
import com.force.api.http.HttpResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.text.SimpleDateFormat;
import java.util.Date;

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

	private final ObjectMapper jsonMapper;

	private static final Logger logger = LoggerFactory.getLogger(ForceApi.class);

	private static final String SF_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";

	final ApiConfig config;
	ApiSession session;
	private boolean autoRenew = false;

	public ForceApi(ApiConfig config, ApiSession session) {
		this.config = config;
		jsonMapper = config.getObjectMapper();
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
		jsonMapper = config.getObjectMapper();
		session = Auth.authenticate(apiConfig);
		autoRenew  = true;

	}

	public ApiSession getSession() {
		return session;
	}

	public String curlHelper() {
		return "curl -s -H 'Authorization: Bearer "+session.getAccessToken()+"' "+uriBase()+" | jq .";
	}

	public ResourceRepresentation get(String path) {
		return new ResourceRepresentation(apiRequest(new HttpRequest()
				.url(uriBase()+path)
				.method("GET")
				.header("Accept", "application/json")),
				jsonMapper);
	}

	/**
	 * sends a custom REST API DELETE request
	 *
	 * @param path     service path to be called - i.e. /process/approvals/
	 * @return response from API wrapped in a ResourceRepresentation for multiple deserialization options. DELETE
	 * responses are generally empty, so you may get an error if you try to deserialize into a class by calling
	 * `as(...)` on ResourceRepresentation. The DELETE can be assumed to have succeeded if this method does not
	 * throw an exception.
	 */
	public ResourceRepresentation delete(String path) {
		return new ResourceRepresentation(apiRequest(new HttpRequest()
				.url(uriBase() + path)
				.method("DELETE")
				.header("Accept", "application/json")),
				jsonMapper);
	}

	/**
	 * sends a custom REST API POST request
	 *
	 * @param path     service path to be called - i.e. /process/approvals/
	 * @param input    this object will be serialized as JSON and sent in tbe body of the request
	 * @return response from API wrapped in a ResourceRepresentation for multiple deserialization options
	 */
	public ResourceRepresentation post(String path, Object input) {
		return request("POST", path, input);
	}

	/**
	 * sends a custom REST API PUT request (no test for this method yet).
	 *
	 * @param path     service path to be called - i.e. /process/approvals/
	 * @param input    this object will be serialized as JSON and sent in tbe body of the request
	 * @return response from API wrapped in a ResourceRepresentation for multiple deserialization options
	 */
	public ResourceRepresentation put(String path, Object input) {
		return request("PUT", path, input);
	}

	/**
	 * sends a custom REST API PATCH request
	 *
	 * @param path     service path to be called - i.e. /process/approvals/
	 * @param input    this object will be serialized as JSON and sent in tbe body of the request
	 * @return response from API wrapped in a ResourceRepresentation for multiple deserialization options
	 */
	public ResourceRepresentation patch(String path, Object input) {
		char sep = path.contains("?") ? '&' : '?';
		return request("POST", path+sep+"_HttpMethod=PATCH", input);
	}

	public ResourceRepresentation request(String method, String path, Object input) {
		try {
			return new ResourceRepresentation(apiRequest(new HttpRequest()
					.url(uriBase() + path)
					.method(method)
					.header("Accept", "application/json")
					.header("Content-Type", "application/json")
					.content(jsonMapper.writeValueAsBytes(input))),
					jsonMapper);
		} catch (JsonGenerationException e) {
			throw new ResourceException(e);
		} catch (JsonMappingException e) {
			throw new ResourceException(e);
		} catch (IOException e) {
			throw new ResourceException(e);
		}
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
					.header("Accept", "application/json")),
				jsonMapper);
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
				logger.debug("Code: {}",res.getResponseCode());
				logger.debug("Message: {}",res.getString());
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

	public <T> QueryResult<T> queryAll(String query, Class<T> clazz) {
		try {
			return queryAny(uriBase() + "/queryAll/?q=" + URLEncoder.encode(query, "UTF-8"), clazz);
		} catch (UnsupportedEncodingException e) {
			throw new ResourceException(e);
		}
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
            result.setDone(root.get("done").booleanValue());
            result.setTotalSize(root.get("totalSize").intValue());
            if (root.get("nextRecordsUrl") != null) {
                result.setNextRecordsUrl(root.get("nextRecordsUrl").textValue());
            }
            List<T> records = new ArrayList<T>();
            for (JsonNode elem : root.get("records")) {
                records.add(jsonMapper.readValue(normalizeCompositeResponse(elem).traverse(), clazz));
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

	public SupportedVersions getSupportedVersions() {
		try {
			return jsonMapper.readValue(apiRequest(new HttpRequest()
					.url(session.getApiEndpoint()+"/services/data")
					.method("GET")
					.header("Accept", "application/json")).getStream(),
					SupportedVersions.class);
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
            final DescribeSObjectBasic describeSObjectBasic = jsonMapper.readValue(root.get("objectDescribe").traverse(),
                    DescribeSObjectBasic.class);
            final List<T> recentItems = new ArrayList<T>();
            for(JsonNode item : root.get("recentItems")) {
                recentItems.add(jsonMapper.readValue(item.traverse(), clazz));
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

	/**
	 * Retrieves all the metadata for an object, including information about each field, URLs, and child relationships.
	 * Response metadata will only be returned if the object metadata has changed since the provided date.
	 * @param sobject object name
	 * @param since date that is used to identify if metadata has been changed since
     * @return the metadata for an object, null if no changes since provided date
     */
	public DescribeSObject describeSObjectIfModified(String sobject, Date since) {
	    if(since == null) {
	        return describeSObject(sobject);
        }
		try {
			HttpResponse response = apiRequest(new HttpRequest()
                    .url(uriBase()+"/sobjects/"+sobject+"/describe")
                    .method("GET")
                    .header("Accept", "application/json")
                    .header("If-Modified-Since", new SimpleDateFormat(SF_DATE_FORMAT).format(since)));
			if(response.getResponseCode() == 304) {
			    return null;
            } else {
                return jsonMapper.readValue(response.getStream(), DescribeSObject.class);
            }
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
		return(session.getApiEndpoint()+"/services/data/"+config.getApiVersionString());
	}

	private final HttpResponse apiRequest(HttpRequest req) {
		req.setAuthorization("Bearer "+session.getAccessToken());
		req.setRequestTimeout(this.config.getRequestTimeout());
		HttpResponse res = Http.send(req);
		if(res.getResponseCode()==401) {
			// Perform one attempt to auto renew session if possible
			if (autoRenew) {
				logger.debug("Session expired. Refreshing session...");
				if(session.getRefreshToken()!=null) {
					session = Auth.refreshOauthTokenFlow(config, session.getRefreshToken());
				} else {
					session = Auth.authenticate(config);
				}
				if(config.getSessionRefreshListener()!=null) {
					config.getSessionRefreshListener().sessionRefreshed(session);
				}
				req.setAuthorization("Bearer "+session.getAccessToken());
				res = Http.send(req);
			}
		}
		// 304 is a special case when the "If-Modified-Since" header is used, it is not an error,
		// it indicates that SF objects were not changed since the time specified in the "If-Modified-Since" header
		if(res.getResponseCode()>299 && res.getResponseCode()!=304) {
			if(res.getResponseCode()==401) {
				throw new ApiTokenException(res.getString());
			} else {
				throw new ApiException(res.getResponseCode(), res.getString());
			}
		} else if(req.getExpectedCode()!=-1 && res.getResponseCode()!=req.getExpectedCode()) {
			throw new RuntimeException("Unexpected response from Force API. Got response code "+res.getResponseCode()+
					". Was expecting "+req.getExpectedCode());
		} else {
			return res;
		}
	}

	/**
	 * Normalizes the JSON response in case it contains responses from
	 * relationship queries. For e.g.
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
		Iterator<Entry<String, JsonNode>> elements = node.fields();
		ObjectNode newNode = JsonNodeFactory.instance.objectNode();
		Entry<String, JsonNode> currNode;
		while(elements.hasNext()){
			currNode = elements.next();

			newNode.set(currNode.getKey(),
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
