package com.qudini.api;

import com.thoughtworks.xstream.core.util.Base64Encoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
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
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.qudini.configuration.GlobalConfiguration.configuration;

@Slf4j
public class RequestSender {

    private static String baseUri = configuration.getQudiniAppStaticData().getBaseuri();
    private static String adminUsername = configuration.getQudiniAppStaticData().getUser();
    private static String adminPassword = configuration.getQudiniAppStaticData().getPassword();
    private static String defaultCharSet = configuration.getRequestStaticValues().getDefaultCharSet();
    private static String encoding = getEncoding(adminUsername, adminPassword);
    private static CookieStore cookies;


    protected String sendPost(String resourcesUri, List<NameValuePair> params) throws UnsupportedEncodingException{

        HttpPost httppost = httpPostBaseSpecification(baseUri + resourcesUri);

        httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");

        try {
            httppost.setEntity(new UrlEncodedFormEntity(params, defaultCharSet));
        } catch (UnsupportedEncodingException e) {

            log.error(String.format("Unsupported encoding for %s", params
                    .stream()
                    .map(p -> new HashMap<>().put(p.getName(), p.getValue()))
                    .collect(Collectors.toList())
                    .toString()));

            throw e;
        }

        return executeRequest(httppost, false);

    }

    protected String sendPost(String resourcesUri) {

        HttpPost httppost = httpPostBaseSpecification(baseUri + resourcesUri);

        try {
            httppost.setEntity(new StringEntity("{}"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return executeRequest(httppost, false);
    }

    protected String sendPost(String path, String params, boolean withCookies) {

        HttpPost httpPost;
        httpPost = httpPostBaseSpecification(path);
        httpPost.setEntity(new StringEntity(params, defaultCharSet));

        return executeRequest(httpPost, withCookies);
    }

    protected String sendPost(String path, String params) {

        HttpPost httppost;
        if (path.contains("https:")) {
            httppost = new HttpPost(path);
            httppost.addHeader("Content-Type", "application/json");
        } else httppost = new HttpPost(baseUri + path);

        //httppost.addHeader("Authorization", "Basic " + encoding);
        httppost.setEntity(new StringEntity(params, defaultCharSet));

        String responseString = executeRequest(httppost, false);

        return responseString;
    }

    protected String sendGet(String path, String userName, String password) {
        String encoding = getEncoding(userName, password);
        HttpGet httpGet;
        if (path.contains("https:")) {
            httpGet = new HttpGet(path);
        } else {
            httpGet = new HttpGet(baseUri + path);
        }

        httpGet.addHeader("Authorization", "Basic " + encoding);

        String responseString = executeRequest(httpGet, false);

        return responseString;
    }

    protected String sendDelete(String path) {
        HttpDelete httpDelete = new HttpDelete(baseUri + path);

        httpDelete.addHeader("Authorization", "Basic " + encoding);

        String responseString = executeRequest(httpDelete, false);

        return responseString;
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

        HttpPut httpput = new HttpPut(baseUri + path);

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
        } else httpput = new HttpPut(baseUri + path);

        httpput.addHeader("Authorization", "Basic " + encoding);

        httpput.setEntity(new StringEntity(params, "UTF-8"));

        String responseString = executeRequest(httpput, false);

        return responseString;
    }

    // private methods

    private static String getEncoding(String userName, String password) {
        return new Base64Encoder().encode((userName + ":" + password).getBytes()).replaceAll("(?:\\r\\n|\\n\\r|\\n|\\r)", "");
    }

    private HttpPost httpPostBaseSpecification(String url){

        HttpPost httpPost;

        if (isValidateUrl(url) && url.toLowerCase().startsWith("https:")) {

            log.debug("using SSL/TLS encrypted connection for POST request specification");

            httpPost = new HttpPost(url);

            httpPost.addHeader("Content-Type", "application/json");

        } else if (isValidateUrl(url) && url.toLowerCase().startsWith("http:")){

            log.debug("using unencrypted connection for POST request specification");

            httpPost = new HttpPost(baseUri + url);

        } else{

            log.error(String.format("The provided URL %s does seem to be valid", url));

            throw new RuntimeException();

        }

        httpPost.addHeader("Authorization", "Basic " + encoding);

        return httpPost;

    }

    private HttpPost httpPostBaseSpecification(String url, String params){
        HttpPost httpPost = httpPostBaseSpecification(url);

        httpPost.setEntity(new StringEntity(params, "UTF-8"));

        return httpPost;
    }

    private boolean isValidateUrl(String path){
        UrlValidator urlValidator = new UrlValidator();
        return urlValidator.isValid(path);
    }


}
