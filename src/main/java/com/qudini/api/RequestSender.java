package com.qudini.api;

import com.thoughtworks.xstream.core.util.Base64Encoder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.List;

import static com.qudini.configuration.GlobalConfiguration.configuration;

public class RequestSender {



    private String appUrl = configuration.getQudiniAppStaticData().getBaseuri();
    private String admin_username = configuration.getQudiniAppStaticData().getUser();
    private String admin_password = configuration.getQudiniAppStaticData().getPassword();
    private String encoding = getEncoding(admin_username, admin_password);
    private static CookieStore cookies;


    protected String sendPost(String path, List<NameValuePair> params) {

        HttpPost httppost = new HttpPost(appUrl + path);

        httppost.addHeader("Authorization", "Basic " + encoding);
        httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");

        try {
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String responseString = executeRequest(httppost, false);

        return responseString;
    }

    protected String sendPost(String path) {

        HttpPost httppost = new HttpPost(appUrl + path);

        httppost.addHeader("Authorization", "Basic " + encoding);

        try {
            httppost.setEntity(new StringEntity("{}"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String responseString = executeRequest(httppost, false);

        return responseString;
    }

    protected String sendPost(String path, String params) {

        HttpPost httppost;
        if (path.contains("https:")) {
            httppost = new HttpPost(path);
            httppost.addHeader("Content-Type", "application/json");
        } else httppost = new HttpPost(appUrl + path);

        httppost.addHeader("Authorization", "Basic " + encoding);
        httppost.setEntity(new StringEntity(params, "UTF-8"));

        String responseString = executeRequest(httppost, false);

        return responseString;
    }

    protected String sendGet(String path, String userName, String password) {
        String encoding = getEncoding(userName, password);
        HttpGet httpGet;
        if (path.contains("https:")) {
            httpGet = new HttpGet(path);
        } else {
            httpGet = new HttpGet(appUrl + path);
        }

        httpGet.addHeader("Authorization", "Basic " + encoding);

        String responseString = executeRequest(httpGet, false);

        return responseString;
    }

    protected String sendDelete(String path) {
        HttpDelete httpDelete = new HttpDelete(appUrl + path);

        httpDelete.addHeader("Authorization", "Basic " + encoding);

        String responseString = executeRequest(httpDelete, false);

        return responseString;
    }

    private String getEncoding(String userName, String password) {
        String encoding = new Base64Encoder().encode((userName + ":" + password).getBytes()).replaceAll("(?:\\r\\n|\\n\\r|\\n|\\r)", "");
        return encoding;
    }

    private String executeRequest(HttpUriRequest request, boolean withCookies) {
        TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
        SSLSocketFactory sf;
        String responseString = null;

        try {
            sf = new SSLSocketFactory(
                    acceptingTrustStrategy, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            SchemeRegistry registry = new SchemeRegistry();

            registry.register(new Scheme("https", 8443, sf));

            ClientConnectionManager ccm = new PoolingClientConnectionManager(registry);

            DefaultHttpClient httpclient = new DefaultHttpClient(ccm);

            if (withCookies) {
                httpclient.setCookieStore(cookies);
            }

            //Execute and get the response.
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            // get cookies
            cookies = httpclient.getCookieStore();

            responseString = EntityUtils.toString(entity);

            System.out.println("********Status Code************");
            System.out.println(response.getStatusLine());
            System.out.println("*******************************");
            System.out.println("********************************************");
            System.out.println(responseString);
            System.out.println("********************************************");
            System.out.println();

        } catch (NoSuchAlgorithmException | IOException | UnrecoverableKeyException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }

        return responseString;
    }

    protected String sendPut(String path, List<NameValuePair> params) {

        HttpPut httpput = new HttpPut(appUrl + path);

        httpput.addHeader("Authorization", "Basic " + encoding);
        httpput.addHeader("Content-Type", "application/x-www-form-urlencoded");

        try {
            httpput.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String responseString = executeRequest(httpput, false);

        return responseString;
    }

    protected String sendPut(String path, String params) {

        HttpPut httpput;
        if (path.contains("https:")) {
            httpput = new HttpPut(path);
            httpput.addHeader("Content-Type", "application/json");
        } else httpput = new HttpPut(appUrl + path);

        httpput.addHeader("Authorization", "Basic " + encoding);

        httpput.setEntity(new StringEntity(params, "UTF-8"));

        String responseString = executeRequest(httpput, false);

        return responseString;
    }

    protected String sendPost(String path, String params, boolean withCookies) {

        HttpPost httppost;
        if (path.contains("https:")) {
            httppost = new HttpPost(path);
            httppost.addHeader("Content-Type", "application/json");
        } else httppost = new HttpPost(appUrl + path);

        httppost.addHeader("Authorization", "Basic " + encoding);
        httppost.setEntity(new StringEntity(params, "UTF-8"));

        return executeRequest(httppost, withCookies);
    }
}
