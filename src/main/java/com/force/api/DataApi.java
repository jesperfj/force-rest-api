package com.force.api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class DataApi {

	private static final ObjectMapper jsonMapper = new ObjectMapper();

	ApiConfig config;
	ApiSession session;
	Identity identity;

	public DataApi(ApiConfig config, ApiSession session) {
		this.config = config;
		this.session = session;
	}

	public DataApi(ApiConfig apiConfig) {
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
	
	
	public ResourceRepresentation get(Resource resource) throws ResourceException {
		return new ResourceRepresentation(Http.send(new HttpRequest()
					.url(session.getApiEndpoint()+"/services/data/"+config.getApiVersion()+resource.getPath())
					.method("GET")
					.header("Accept", "application/json")
					.header("Authorization", "OAuth "+session.getAccessToken())));
	}
	
}
