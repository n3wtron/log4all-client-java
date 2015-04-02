package net.log4all.client;

import java.io.IOException;
import java.util.Date;

import net.log4all.client.exceptions.Log4AllException;

import org.apache.commons.io.IOUtils;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Created by igor on 03/06/14.
 */
public class Log4AllClient {
	private String url;
	private HttpHost proxy;
	private String application;
	private String token;

	public Log4AllClient(String url, String application, String token, HttpHost proxy) {
		this.url = url;
		this.application = application;
		this.token = token;
		this.proxy = proxy;
	}

	public Log4AllClient(String url, String application, String token) {
		this(url, application, token, null);
	}

	public Log4AllClient(String url, String application) {
		this(url, application, null, null);
	}

	public Log4AllClient(String url, String application, HttpHost proxy) {
		this(url, application, null, proxy);
	}

	private HttpClient getHttpClient() {
		HttpClientBuilder builder = HttpClientBuilder.create();
		builder.setConnectionReuseStrategy(new ConnectionReuseStrategy() {
			@Override
			public boolean keepAlive(HttpResponse httpResponse, HttpContext httpContext) {
				return false;
			}
		});
		if (this.proxy != null) {
			builder.setProxy(this.proxy);
		}
		return builder.build();
	}

	public void log(String msg, String level) throws Log4AllException {
		JSONObject jsonData = null;
		try {
			jsonData = toJSON(msg, level);
			log(jsonData);
		} catch (JSONException e) {
			throw new Log4AllException(e.getMessage(), e);
		}
	}

	public void log(String msg, String level, String stack) throws Log4AllException {
		JSONObject jsonData = null;
		try {
			jsonData = toJSON(msg, level, stack, new Date());
			log(jsonData);
		} catch (JSONException e) {
			throw new Log4AllException(e.getMessage(), e);
		}
	}

	public void log(JSONObject jsonData) throws Log4AllException {
		sendJson(jsonData);
	}

	public void log(JSONArray jsonArray) throws Log4AllException {
		sendJson(jsonArray);
	}

	public void sendJson(Object jsonData) throws Log4AllException {
		HttpPut addLogPut;
		if (jsonData instanceof JSONArray){
			addLogPut = new HttpPut(url + "/api/logs");
		}else{
			addLogPut = new HttpPut(url + "/api/log");
		}
		String rawResponse = "";
		JSONObject jsonResp;
		try {
			JSONObject jsonReq;
			if (jsonData instanceof JSONArray){
				jsonReq = new JSONObject();
				jsonReq.put("logs", jsonData);
			}else{
				jsonReq= (JSONObject) jsonData;
			}
			jsonReq.put("application", this.application);
			if (token!=null){
				jsonReq.put("application_token", this.token);	
			}
			HttpEntity postData = new StringEntity(jsonReq.toString());
			addLogPut.setEntity(postData);
			HttpResponse resp = getHttpClient().execute(addLogPut);
			rawResponse = IOUtils.toString(resp.getEntity().getContent());
			jsonResp = new JSONObject(rawResponse);
			if (!jsonResp.getBoolean("success")) {
				throw new Log4AllException(jsonResp.getString("message"));
			}
		} catch (IOException e) {
			throw new Log4AllException(e.getMessage() + "httpResp:" + rawResponse, e);
		} catch (JSONException e) {
			throw new Log4AllException(e.getMessage() + "httpResp:" + rawResponse, e);
		}
	}

	public static JSONObject toJSON(String msg, String level) throws JSONException {
		JSONObject jsonData = new JSONObject();
		jsonData.put("message", msg);
		jsonData.put("level", level);
		return jsonData;
	}

	public static JSONObject toJSON(String msg, String level, Date date) throws JSONException {
		JSONObject jsonData = new JSONObject();
		jsonData.put("message", msg);
		jsonData.put("level", level);
		jsonData.put("date", date.getTime());
		return jsonData;
	}

	public static JSONObject toJSON(String msg, String level, String stack, Date date) throws JSONException {
		JSONObject jsonData = new JSONObject();
		jsonData.put("message", msg);
		jsonData.put("level", level);
		jsonData.put("date", date.getTime());
		jsonData.put("stack", stack);
		return jsonData;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
