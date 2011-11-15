package com.force.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class Auth {

	private static final ObjectMapper jsonMapper = new ObjectMapper();

	static public final ApiSession oauthLoginPasswordFlow(ApiConfig c) {
		try {
			@SuppressWarnings("unchecked")
			Map<String,Object> resp = jsonMapper.readValue(
					Http.send(new HttpFormPost()
						.url(c.getLoginEndpoint()+"/services/oauth2/token")
						.param("grant_type","password")
						.param("client_id",c.getClientId())
						.param("client_secret", c.getClientSecret())
						.param("username",c.getUsername())
						.param("password",c.getPassword())
					).getStream(),Map.class);
			return new ApiSession(c,(String)resp.get("access_token"),(String)resp.get("instance_url"));
			
		} catch (JsonParseException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	
	static public final ApiSession soaploginPasswordFlow(ApiConfig c) {
		try {
			URL url = new URL(c.getLoginEndpoint()+"/services/Soap/u/23.0");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.addRequestProperty("Content-Type", "text/xml");
			conn.addRequestProperty("SOAPAction", "login");
			OutputStream out = conn.getOutputStream();
			byte[] msg = new String(
					"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"+
					"<env:Envelope xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"+
					"              xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"+
					"              xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"+
					"    <env:Body>\n"+
			        "        <n1:login xmlns:n1=\"urn:partner.soap.sforce.com\">\n"+
			        "            <n1:username>"+c.getUsername()+"</n1:username>\n"+
			        "            <n1:password>"+c.getPassword()+"</n1:password>\n"+
			        "        </n1:login>\n"+
			        "    </env:Body>\n"+
			        "</env:Envelope>\n").getBytes("UTF-8");
			out.write(msg);
			out.flush();
			InputStream in = conn.getInputStream();
			StringBuilder b = new StringBuilder();
			byte[] buf = new byte[2000];
			int n = 0;
			while((n=in.read(buf))!=-1) {
				b.append(new String(buf,0,n));
			}
			String s = b.toString();
			//System.out.println(s);
			String accessToken = s.replaceAll("^.*<sessionId>(.*)</sessionId>.*$","$1").trim();
			String apiEndpoint = "https://"+s.replaceAll("^.*<serverUrl>.*https://([^/]*)/.*</serverUrl>.*$","$1").trim();
			//String organizationId = s.replaceAll("^.*<organizationId>(.*)</organizationId>.*$","$1").trim();
			//String userId = s.replaceAll("^.*<userId>(.*)</userId>.*$","$1").trim();
			//System.out.println("accessToken:"+accessToken);
			//System.out.println("apiEndpoint: "+apiEndpoint);
			//System.out.println("userId: "+userId);
			//System.out.println("organizationId: "+organizationId);
							
			return new ApiSession(c,accessToken, apiEndpoint);
			
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
	}

	static public final String startOAuthWebServerFlow(AuthorizationRequest req) {
		try {
			return req.apiConfig.getLoginEndpoint()+
					"/services/oauth2/authorize"+
					"?response_type=code"+
					"&client_id="+URLEncoder.encode(req.apiConfig.getClientId(),"UTF-8")+
					(req.scope!=null ? "&scope="+URLEncoder.encode(req.scope,"UTF-8") : "") +
					"&redirect_uri="+URLEncoder.encode(req.apiConfig.redirectURI,"UTF-8")+
					(req.state!=null ? "&state="+URLEncoder.encode(req.state,"UTF-8") : "") +
					(req.immediate ? "&immediate=true" : "") +
					(req.display!=null ? "&display="+req.display : "");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	static public final ApiSession completeOAuthWebServerFlow(AuthorizationResponse res) {
		try {
			Map<?,?> resp = jsonMapper.readValue(
					Http.send(new HttpFormPost()
						.url(res.apiConfig.getLoginEndpoint()+"/services/oauth2/token")
						.header("Accept","application/json")
						.param("grant_type","authorization_code")
						.param("client_id",res.apiConfig.getClientId())
						.param("client_secret", res.apiConfig.getClientSecret())
						.param("redirect_uri",res.apiConfig.getRedirectURI())
						.preEncodedParam("code",res.code)
					).getStream(),Map.class);

			return new ApiSession()
					.setApiConfig(res.apiConfig.clone().setRefreshToken((String)resp.get("refresh_token")))
					.setAccessToken((String)resp.get("access_token"))
					.setApiEndpoint((String)resp.get("instance_url"));
			
		} catch (JsonParseException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	static public final ApiSession authenticate(ApiConfig c) {
		if(c.getUsername()!=null && c.getPassword()!=null && c.getClientId()!=null && c.getClientSecret()!=null) {
			// username/password oauth flow
			return oauthLoginPasswordFlow(c);
		}
		else if(c.getUsername()!=null && c.getPassword()!=null) {
			return soaploginPasswordFlow(c);
		}
		return null;
	}

}
