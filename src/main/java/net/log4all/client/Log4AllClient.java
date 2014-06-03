package net.log4all.client;

import net.log4all.client.exceptions.Log4AllException;
import org.apache.commons.io.IOUtils;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by igor on 03/06/14.
 */
public class Log4AllClient {
    private String url;
    private HttpHost proxy;

    public Log4AllClient(String url) {
        this.url = url;
    }

    public Log4AllClient(String url, HttpHost proxy) {
        this.url = url;
        this.proxy = proxy;
    }

    private HttpClient getHttpClient(){
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setConnectionReuseStrategy(new ConnectionReuseStrategy() {
            @Override
            public boolean keepAlive(HttpResponse httpResponse, HttpContext httpContext) {
                return false;
            }
        });
        if (this.proxy!=null){
            builder.setProxy(this.proxy);
        }
        return builder.build();
    }

    public boolean log(String msg) throws Log4AllException {
        HttpPost addLogPost = new HttpPost(url+"/api/logs/add");
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("log",msg);
            HttpEntity postData = new StringEntity(jsonData.toString());
            addLogPost.setEntity(postData);
            HttpResponse resp = getHttpClient().execute(addLogPost);
            String rawResponse = IOUtils.toString(resp.getEntity().getContent());
            JSONObject jsonResp = new JSONObject(rawResponse);
            return jsonResp.getBoolean("result");
        } catch (Exception e) {
            throw new Log4AllException(e.getMessage(),e);
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
