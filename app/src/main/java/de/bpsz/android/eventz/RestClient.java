package de.bpsz.android.eventz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * small Client for Rest Webservices, using java.net.*
 * use like this:
 * public String getFancyStuff(String param1, String param2) throws IOException {
 * RestClient client = new RestClient("http://localhost:52199/shop/Kunde/(");
 * client.addParam("name", param1);
 * client.addParam("email", param2);
 * client.execute(RequestMethod.GET);
 * return client.getResponse();
 * }
 */
public class RestClient {
	
	private Map<String, String> params;
	private Map<String, String> headers;
	
	private String url;
	
	private int responseCode;
	private String message;
	
	private String response;
	
	enum RequestMethod {
		GET, POST;
	}
	
	public String getResponse() {
		return this.response;
	}
	
	public String getErrorMessage() {
		return message;
	}
	
	public int getResponseCode() {
		return responseCode;
	}

	
	public RestClient(String url) {
		this.url = url;
		params = new HashMap<String, String>();
		headers = new HashMap<String, String>();
	}
	
	public void addParam(String key, String value) {
		if (key == null | value == null) {
			return;
		}
		params.put(key, value);
	}
	
	public void addHeaders(String key, String value) {
		if (key == null || value == null) {
			return;
		}
		headers.put(key, value);
	}
	
	public void execute (RequestMethod method) throws IOException {
		switch (method) {
		case GET: {
			String combinedParam = "";
			if (!params.isEmpty()) {
				combinedParam += "?";
				for (Entry<String, String> p : params.entrySet()) {
					String paramString = p.getKey() + "=" + URLEncoder.encode(p.getValue(), "UTF-8");
					if (combinedParam.length() > 1) {
						combinedParam += "&" + paramString;
					} else {
						combinedParam += paramString;
					}
				}
			}
			
			URL restServiceURL = new URL(url + combinedParam);
			HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
			
			httpConnection.setRequestMethod("GET");
			httpConnection.setRequestProperty("Accept", "application/json");
			
			responseCode = httpConnection.getResponseCode();
			if (responseCode != 200) {
				    throw new RuntimeException("HTTP GET Request Failed with Error code : " + httpConnection.getResponseCode());
			}
			
			executeRequest(httpConnection);
			
			break;
		}
		case POST:
			
			// doPost
			
			break;
		default:
			break;
		}
	}
	
	private void executeRequest(HttpURLConnection httpConnection) {
		
		try {
			InputStream inputStream = httpConnection.getInputStream();
			response = convertStreamToString(inputStream);
			inputStream.close();
			
		} catch (IOException e){
			httpConnection.disconnect();
			e.printStackTrace();
		}
	}

	private static String convertStreamToString(InputStream inputStream) {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder sb = new StringBuilder();
		
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
}
