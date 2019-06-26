package com.qudini.api;

import com.thoughtworks.xstream.core.util.Base64Encoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.UnsupportedCharsetException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

@Slf4j
public class RequestSender {


    // MUST BE REMOVED FROM HERE - THIS MAY BECOME A LIB AND WILL NOT BE DEPENDENT OF THIS
//    private static String baseUri = configuration.getQudiniAppStaticData().getBaseuri();
//    private static String adminUsername = configuration.getQudiniAppStaticData().getUser();
//    private static String adminPassword = configuration.getQudiniAppStaticData().getPassword();
//    private static String defaultCharSet = configuration.getRequestStaticValues().getDefaultCharSet();
//    private static String encoding = getEncoding(adminUsername, adminPassword);
    private static CookieStore cookies;


    // PUBLIC METHODS

    /**
     * Generates the access token for qudiniApp
     *
     * @param userName the username
     * @param password the password in plain text
     * @return token string
     */
    public String generateQudiniAppToken(String userName, String password) {

        String token = new Base64Encoder()
                .encode(
                        String.format("%s:%s", userName, password).getBytes())
                .replaceAll("(?:\\r\\n|\\n\\r|\\n|\\r)", "");

        log.debug(String.format("generated access token: %s", token));

        return token;

    }

    // PROTECTED METHODS

    // -- POST REQUEST -- //

    /**
     * Executes a post request with form parameters provided as a list of NameValuePair
     *
     * @param url The full url that should be called
     * @param token The generated authorization token
     * @param paramsAsNameValuePair list of form key-value object
     * @param charSet the charset
     * @return the response as string of the executed request
     * @throws UnsupportedEncodingException
     */
    protected String sendPost(
            String url,
            String token,
            List<NameValuePair> paramsAsNameValuePair,
            String charSet)
            throws UnsupportedEncodingException{

        HttpPost httppost = httpPostBaseSpecification(url,"application/x-www-form-urlencoded", token);

        try {
            httppost.setEntity(new UrlEncodedFormEntity(paramsAsNameValuePair, charSet));
        } catch (UnsupportedEncodingException e) {

            log.error(String.format("Unsupported encoding for %s", paramsAsNameValuePair
                    .stream()
                    .map(p -> new HashMap<String,String>().put(p.getName(), p.getValue()))
                    .collect(Collectors.toList())
                    .toString()));

            throw e;
        }

        return executeRequest(httppost, false);

    }

    /**
     * Sends a post request with an empty payload object
     *
     * @param url The full path url
     * @param contentType the content type of the send payload
     * @param token the access token
     * @return the response as string of the executed request
     * @throws UnsupportedEncodingException
     */
    protected String sendPost(
            String url,
            String contentType,
            String token)
            throws UnsupportedEncodingException {

        HttpPost httppost = httpPostBaseSpecification(url, contentType, token);

        try {
            httppost.setEntity(new StringEntity("{}"));
        } catch (UnsupportedEncodingException e) {

            log.error("Could not set payload to \\{\\}");

            throw e;
        }

        return executeRequest(httppost, false);
    }

    /**
     * Executes a post request allowing to activate cookies
     *
     * @param url The full url called
     * @param contentType The content type header value
     * @param token The generated authorization token
     * @param paramsAsString the payload parameters as string
     * @param charSet the charset
     * @param withCookies cookies switch
     * @return the response as string of the executed request
     */
    protected String sendPost(
            String url,
            String contentType,
            String token,
            String paramsAsString,
            String charSet,
            boolean withCookies) {

        HttpPost httpPost = httpPostBaseSpecification(url, contentType, token, paramsAsString, charSet);

        return executeRequest(httpPost, withCookies);
    }

    /**
     * Overload of method {@link #sendPost(String, String, String, String, String, boolean) sendPost}
     * setting the with cookies switch to off (false)
     *
     * @param url The full url called
     * @param contentType The content type header value
     * @param token The generated authorization token
     * @param paramsAsString the payload parameters as string
     * @param charSet the charset
     * @return the response as string of the executed request
     */
    protected String sendPost(
            String url,
            String contentType,
            String token,
            String paramsAsString,
            String charSet) {

        HttpPost httpPost = httpPostBaseSpecification(url, contentType, token, paramsAsString, charSet);

        return executeRequest(httpPost, false);

    }

    // -- PUT REQUEST -- //

    protected String sendPut(
            String url,
            String token,
            List<NameValuePair> paramsAsNameValuePair,
            String charSet)
            throws UnsupportedEncodingException{

        HttpPut httpPut = httpPutBaseSpecification(url,"application/x-www-form-urlencoded", token);

        try {
            httpPut.setEntity(new UrlEncodedFormEntity(paramsAsNameValuePair, charSet));
        } catch (UnsupportedEncodingException e) {

            log.error(String.format("Unsupported encoding for %s", paramsAsNameValuePair
                    .stream()
                    .map(p -> new HashMap<String,String>().put(p.getName(), p.getValue()))
                    .collect(Collectors.toList())
                    .toString()));

            throw e;
        }

        return executeRequest(httpPut, false);

    }

    protected String sendPut(
            String url,
            String contentType,
            String token,
            String paramsAsString,
            String charSet) {

        HttpPut httpPut = httpPutBaseSpecification(url, contentType, token, paramsAsString, charSet);

        return executeRequest(httpPut, false);

    }


    // -- GET REQUESTS -- //

    protected String sendGet(String url, String token) {

        HttpGet httpGet = httpGetBaseSpecification(url, token);

        return executeRequest(httpGet, false);
    }

    // -- DELETE REQUESTS -- //

    protected String sendDelete(String url, String token) {

        HttpDelete httpDelete = httpDeleteBaseSpecification(url, token);

        return executeRequest(httpDelete, false);

    }


    // PRIVATE METHODS

    private String executeRequest(HttpUriRequest request, boolean withCookies) {

        Map<String, String> decomposedResponse = new HashMap<>();

        String responseAsStringKey = "responseAsString";

        try{

            TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;

            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                    .register("https", sslsf)
                    .register("http", new PlainConnectionSocketFactory())
                    .build();

            try (BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager(socketFactoryRegistry)) {


                if (withCookies) {
                    CloseableHttpClient httpClient = HttpClients
                            .custom()
                            .setSSLSocketFactory(sslsf)
                            .setConnectionManager(connectionManager)
                            .setDefaultCookieStore(cookies)
                            // the method below is needed to remove header error with expiration date
                            .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                            .build();

                    HttpClientContext context = HttpClientContext.create();

                    CloseableHttpResponse httpResponse = httpClient.execute(request, context);

                    cookies = context.getCookieStore();

                    decomposedResponse = httpResponseDecomposer(httpResponse);

                    httpResponse.close();

                } else {
                    CloseableHttpClient httpClient = HttpClients
                            .custom()
                            .setSSLSocketFactory(sslsf)
                            .setConnectionManager(connectionManager)
                            // the method below is needed to remove header error with expiration date
                            .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                            .build();

                    CloseableHttpResponse httpResponse = httpClient.execute(request);

                    decomposedResponse = httpResponseDecomposer(httpResponse);

                    httpResponse.close();
                }
            }


        }catch (NoSuchAlgorithmException | IOException | KeyManagementException | KeyStoreException e){

            log.error("An exception has occurred while making the intended requests, see stack trace for more details");

            log.error(e.toString());

        }

        return decomposedResponse.get(responseAsStringKey);

    }


    private HttpPost httpPostBaseSpecification(String url, String contentType, String token){

        HttpPost httpPost;

        if (url.startsWith("http:") || url.startsWith("https:")) {

            httpPost = new HttpPost(url);

            httpPost.addHeader("Content-Type", contentType);

            httpPost.addHeader("Authorization", "Basic " + token);

            log.debug("Generated base http POST specification");


        } else{

            log.error(String.format("The provided URL [ %s ] does seem to be a valid URL", url));

            throw new RuntimeException();

        }

        return httpPost;

    }

    private HttpPost httpPostBaseSpecification(String url, String contentType, String token, String paramsAsString, String charSet) {
        HttpPost httpPost = httpPostBaseSpecification(url, contentType, token);
        try{
            httpPost.setEntity(new StringEntity(paramsAsString, charSet));
        }catch (UnsupportedCharsetException e){
            log.error(String.format("The charset [%s] is not valid or cannot be applied to the payload", charSet));

            throw e;
        }


        return httpPost;
    }

    private HttpPut httpPutBaseSpecification(String url, String contentType, String token){

        HttpPut httpPut;

        if (url.startsWith("http:") || url.startsWith("https:")) {

            httpPut = new HttpPut(url);

            httpPut.addHeader("Content-Type", contentType);

            httpPut.addHeader("Authorization", "Basic " + token);

            log.debug("Generated base http PUT specification");


        } else{

            log.error(String.format("The provided URL [ %s ] does seem to be a valid URL", url));

            throw new RuntimeException();

        }

        return httpPut;

    }

    private HttpPut httpPutBaseSpecification(String url, String contentType, String token, String paramsAsString, String charSet) {
        HttpPut httpPut = httpPutBaseSpecification(url, contentType, token);
        try{
            httpPut.setEntity(new StringEntity(paramsAsString, charSet));
        }catch (UnsupportedCharsetException e){
            log.error(String.format("The charset [%s] is not valid or cannot be applied to the payload", charSet));

            throw e;
        }


        return httpPut;
    }

    private HttpGet httpGetBaseSpecification(String url, String token){

        HttpGet httpGet;

        if (url.startsWith("http:") || url.startsWith("https:")) {

            httpGet = new HttpGet(url);

            httpGet.addHeader("Authorization", "Basic " + token);

            log.debug("Generated base http GET specification");

        }else{

            log.error(String.format("The provided URL [ %s ] does seem to be a valid URL", url));

            throw new RuntimeException();
        }

        return httpGet;

    }


    private HttpDelete httpDeleteBaseSpecification(String url, String token){

        HttpDelete httpDelete;

        if (url.startsWith("http:") || url.startsWith("https:")) {

            httpDelete = new HttpDelete(url);

            httpDelete.addHeader("Authorization", "Basic " + token);

            log.debug("Generated base http DELETE specification");

        }else{

            log.error(String.format("The provided URL [ %s ] does seem to be a valid URL", url));

            throw new RuntimeException();
        }

        return httpDelete;

    }

    private Map<String, String> httpResponseDecomposer(CloseableHttpResponse httpResponse) throws IOException {

        final String statusCode = "statusCode";

        final String responseAsString = "responseAsString";

        Map<String, String> responseObj = new HashMap<>();

        responseObj.put(statusCode, String.valueOf(httpResponse.getStatusLine().getStatusCode()));

        responseObj.put(responseAsString, EntityUtils.toString(httpResponse.getEntity()));


        if (parseInt(responseObj.get(statusCode)) >= 200 && parseInt(responseObj.get(statusCode)) < 300){

            log.info(String.format("Request was successful - status code: %s ", responseObj.get(statusCode)));

        } else if (parseInt(responseObj.get(statusCode)) >= 400 && parseInt(responseObj.get(statusCode)) < 500){

            log.error(String.format("Request was not successful - client side problem - status code: %s", responseObj.get(statusCode)));
            log.error(String.format("The data insertion/fetching was compromised, the obtained response body is: %n%s", responseObj.get(responseAsString)));

        } else if (parseInt(responseObj.get(statusCode)) >= 500){

            log.error(String.format("Request was not successful - Server side problem - status code: %s", responseObj.get(statusCode)));
            log.error(String.format("The data insertion/fetching was compromised, the obtained response body is: %n%s", responseObj.get(responseAsString)));

        }

        return responseObj;
    }


}
