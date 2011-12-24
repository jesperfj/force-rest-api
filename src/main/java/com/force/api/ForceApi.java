package com.force.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.force.api.http.Http;
import com.force.api.http.HttpRequest;
import com.force.api.http.HttpResponse;

public class ForceApi {

	private static final ObjectMapper jsonMapper;

	static {
		jsonMapper = new ObjectMapper();
		jsonMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
	}
	
	ApiConfig config;
	ApiSession session;
	Identity identity;

	public ForceApi(ApiConfig config, ApiSession session) {
		this.config = config;
		this.session = session;
	}

	public ForceApi(ApiConfig apiConfig) {
		config = apiConfig;
		session = Auth.authenticate(apiConfig);
	}


	public Identity getIdentity() {
		if(identity!=null) {
			return identity;
		}
		try {
			
			@SuppressWarnings("unchecked")
			Map<String,Object> resp = jsonMapper.readValue(
					Http.send(new HttpRequest()
						.url(session.getApiEndpoint()+"/services/data/"+config.getApiVersion()+"/")
						.method("GET")
						.header("Accept", "application/json")
						.header("Authorization","OAuth "+session.getAccessToken())
					).getStream(),Map.class);
			
			identity = jsonMapper.readValue(
					Http.send(new HttpRequest()
						.url((String) resp.get("identity"))
						.method("GET")
						.header("Accept", "application/json")
						.header("Authorization","OAuth " + session.getAccessToken())
					).getStream(), Identity.class);
			return identity;
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
		return new ResourceRepresentation(Http.send(new HttpRequest()
					.url(session.getApiEndpoint()+"/services/data/"+config.getApiVersion()+"/sobjects/"+type+"/"+id)
					.method("GET")
					.header("Accept", "application/json")
					.header("Authorization", "OAuth "+session.getAccessToken())));
	}

	public String createSObject(String type, Object sObject) {
		try {
			// We're trying to keep Http classes clean with no reference to JSON/Jackson
			// Therefore, we serialize to bytes before we pass object to HttpRequest().
			// But it would be nice to have a streaming implementation. We can do that
			// by using ObjectMapper.writeValue() passing in output stream, but then we have
			// polluted the Http layer.
			HttpResponse res = 
				Http.send(new HttpRequest()
					.url(session.getApiEndpoint()+"/services/data/"+config.getApiVersion()+"/sobjects/"+type)
					.method("POST")
					.header("Authorization", "OAuth "+session.getAccessToken())
					.header("Accept", "application/json")
					.header("Content-Type", "application/json")
					.content(jsonMapper.writeValueAsBytes(sObject))
				);

			if(res.getResponseCode()!=201) {
				// TODO: fix
				System.out.println("Code: "+res.getResponseCode());
				System.out.println("Message: "+res.getString());
				throw new RuntimeException();
			}
			CreateResponse result = jsonMapper.readValue(res.getStream(),CreateResponse.class);

			if (result.isSuccess()) {
				return (result.getId());
			} else {
				throw new OperationFailedException(result.getErrors());
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
			HttpResponse res = 
				Http.send(new HttpRequest()
					.url(session.getApiEndpoint()+"/services/data/"+config.getApiVersion()+"/sobjects/"+type+"/"+id+"?_HttpMethod=PATCH")
					.method("POST")
					.header("Authorization", "OAuth "+session.getAccessToken())
					.header("Accept", "application/json")
					.header("Content-Type", "application/json")
					.content(jsonMapper.writeValueAsBytes(sObject))
				);
			if(res.getResponseCode()!=204) {
				// TODO: fix
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

	public void deleteSObject(String type, String id) {
		Http.send(new HttpRequest()
			.url(session.getApiEndpoint()+"/services/data/"+config.getApiVersion()+"/sobjects/"+type+"/"+id)
			.method("DELETE")
			.header("Authorization", "OAuth "+session.getAccessToken())
		);
	}

	public CreateOrUpdateResult createOrUpdateSObject(String type, String externalIdField, String externalIdValue, Object sObject) {
		try {
			// See createSObject for note on streaming ambition
			HttpResponse res = 
				Http.send(new HttpRequest()
					.url(session.getApiEndpoint()+"/services/data/"+config.getApiVersion()+"/sobjects/"+type+"/"+externalIdField+"/"+URLEncoder.encode(externalIdValue,"UTF-8")+"?_HttpMethod=PATCH")
					.method("POST")
					.header("Authorization", "OAuth "+session.getAccessToken())
					.header("Accept", "application/json")
					.header("Content-Type", "application/json")
					.content(jsonMapper.writeValueAsBytes(sObject))
				);
			if(res.getResponseCode()==201) {
				return CreateOrUpdateResult.CREATED;
			} else if(res.getResponseCode()==204) {
				return CreateOrUpdateResult.UPDATED;
			} else {
				// TODO: fix
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
			HttpResponse res = Http.send(new HttpRequest()
					.url(session.getApiEndpoint()+"/services/data/"+config.getApiVersion()+"/query/?q="+URLEncoder.encode(query,"UTF-8"))
					.method("GET")
					.header("Accept", "application/json")
					.header("Authorization", "OAuth "+session.getAccessToken()));

			// We build the result manually, because we can't pass the type information easily into 
			// the JSON parser mechanism.

			if(res.getResponseCode()==200) {
				QueryResult<T> result = new QueryResult<T>();
				JsonNode root = jsonMapper.readTree(res.getStream());
				result.setDone(root.get("done").getBooleanValue());
				result.setTotalSize(root.get("totalSize").getIntValue());
				if(root.get("nextRecodsUrl")!=null) {
					result.setNextRecordsUrl(root.get("nextRecordsUrl").getTextValue());
				}
				List<T> records = new ArrayList<T>();
				for(JsonNode elem : root.get("records")) {
					records.add(jsonMapper.readValue(elem,clazz));
				}
				result.setRecords(records);
				return result;
			} else {
				// TODO: fix
				System.out.println("Code: "+res.getResponseCode());
				System.out.println("Message: "+res.getString());
				throw new RuntimeException();
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
	
	public DescribeGlobal describeGlobal() {
		try {
			return jsonMapper.readValue(Http.send(new HttpRequest()
					.url(session.getApiEndpoint()+"/services/data/"+config.getApiVersion()+"/sobjects/")
					.method("GET")
					.header("Accept", "application/json")
					.header("Authorization", "OAuth "+session.getAccessToken())).getStream(),DescribeGlobal.class);
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
	
	public DescribeSObject describeSObject(String sobject) {
		try {
			return jsonMapper.readValue(Http.send(new HttpRequest()
					.url(session.getApiEndpoint()+"/services/data/"+config.getApiVersion()+"/sobjects/"+sobject+"/describe")
					.method("GET")
					.header("Accept", "application/json")
					.header("Authorization", "OAuth "+session.getAccessToken())).getStream(),DescribeSObject.class);
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
}
