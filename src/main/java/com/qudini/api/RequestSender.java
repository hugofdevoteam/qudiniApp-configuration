package com.qudini.api;

import com.thoughtworks.xstream.core.util.Base64Encoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
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

    private static final String HTTP_SPEC_ERROR = "The provided URL [ %s ] does seem to be a valid URL";

    private String baseUri;
    private String token;

    private static CookieStore cookies;


    public RequestSender(
            String baseUri,
            String userName,
            String password) {

        this.baseUri = baseUri;
        this.token = generateQudiniAppToken(userName, password);

    }


    // PROTECTED METHODS

    // -- POST REQUEST -- //

    /**
     * Executes a post request with form parameters provided as a list of NameValuePair
     *
     * @param endpointUri           the resource Uri being called
     * @param paramsAsNameValuePair list of form key-value object
     * @param charSet               the charset
     * @return the response as string of the executed request
     * @throws UnsupportedEncodingException
     */
    public String sendPost(
            String endpointUri,
            List<NameValuePair> paramsAsNameValuePair,
            String charSet)
            throws UnsupportedEncodingException {

        HttpPost httppost = (HttpPost) httpBaseSpecification("post", endpointUri, "application/x-www-form-urlencoded", token);

        try {
            httppost.setEntity(new UrlEncodedFormEntity(paramsAsNameValuePair, charSet));
        } catch (UnsupportedEncodingException e) {

            log.error(String.format("Unsupported encoding for %s", paramsAsNameValuePair
                    .stream()
                    .map(p -> new HashMap<String, String>().put(p.getName(), p.getValue()))
                    .collect(Collectors.toList())
                    .toString()));

            throw e;
        }

        return executeRequest(httppost, false);

    }

    /**
     * Sends a post request with an empty payload object
     *
     * @param endpointUri the resource Uri being called
     * @param contentType the content type of the send payload
     * @return the response as string of the executed request
     * @throws UnsupportedEncodingException
     */
    public String sendPost(
            String endpointUri,
            String contentType)
            throws UnsupportedEncodingException {

        HttpPost httppost = (HttpPost) httpBaseSpecification("post", endpointUri, contentType, token);

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
     * @param endpointUri    the resource Uri being called
     * @param contentType    The content type header value
     * @param paramsAsString the payload parameters as string
     * @param charSet        the charset
     * @param withCookies    cookies switch
     * @return the response as string of the executed request
     */
    public String sendPost(
            String endpointUri,
            String contentType,
            String paramsAsString,
            String charSet,
            boolean withCookies) {

        HttpPost httpPost = httpPostBaseSpecification(endpointUri, contentType, token, paramsAsString, charSet);

        return executeRequest(httpPost, withCookies);
    }

    /**
     * Overload of method {@link #sendPost(String, String, String, String, boolean) sendPost}
     * setting the with cookies switch to off (false)
     *
     * @param endpointUri    the resource Uri being called
     * @param contentType    The content type header value
     * @param paramsAsString the payload parameters as string
     * @param charSet        the charset
     * @return the response as string of the executed request
     */
    public String sendPost(
            String endpointUri,
            String contentType,
            String paramsAsString,
            String charSet) {

        HttpPost httpPost = httpPostBaseSpecification(endpointUri, contentType, token, paramsAsString, charSet);

        return executeRequest(httpPost, false);

    }

    // -- PUT REQUEST -- //

    public String sendPut(
            String endpointUri,
            List<NameValuePair> paramsAsNameValuePair,
            String charSet)
            throws UnsupportedEncodingException {

        HttpPut httpPut = (HttpPut) httpBaseSpecification("put", endpointUri, "application/x-www-form-urlencoded", token);

        try {
            httpPut.setEntity(new UrlEncodedFormEntity(paramsAsNameValuePair, charSet));
        } catch (UnsupportedEncodingException e) {

            log.error(String.format("Unsupported encoding for %s", paramsAsNameValuePair
                    .stream()
                    .map(p -> new HashMap<String, String>().put(p.getName(), p.getValue()))
                    .collect(Collectors.toList())
                    .toString()));

            throw e;
        }

        return executeRequest(httpPut, false);

    }

    public String sendPut(
            String endpointUri,
            String contentType,
            String paramsAsString,
            String charSet) {

        HttpPut httpPut = httpPutBaseSpecification(endpointUri, contentType, token, paramsAsString, charSet);

        return executeRequest(httpPut, false);

    }


    // -- GET REQUESTS -- //

    public String sendGet(
            String endpointUri) {

        HttpRequestBase httpGet = httpBaseSpecification("get", endpointUri, token);

        return executeRequest(httpGet, false);
    }

    // -- DELETE REQUESTS -- //

    public String sendDelete(String endpointUri) {

        HttpRequestBase httpDelete = httpBaseSpecification("delete", endpointUri, token);

        return executeRequest(httpDelete, false);

    }


    // PRIVATE METHODS

    private String executeRequest(HttpUriRequest request, boolean withCookies) {

        Map<String, String> decomposedResponse = new HashMap<>();

        String responseAsStringKey = "responseAsString";

        try {

            TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;

            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
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

                    log.info(String.format("Performing a request for the URL: %s", request.getURI()));

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

                    log.info(String.format("Performing a request for the URL: %s", request.getURI()));

                    decomposedResponse = httpResponseDecomposer(httpResponse);

                    httpResponse.close();
                }
            }


        } catch (NoSuchAlgorithmException | IOException | KeyManagementException | KeyStoreException e) {

            log.error("An exception has occurred while making the intended requests");

            log.error(e.toString());

        }

        return decomposedResponse.get(responseAsStringKey);

    }


    private HttpRequestBase httpMethod(String methodName, String url) {

        HttpRequestBase httpRequestBase;
        String msg = String.format("Generated base http %s specification", methodName);

        switch (methodName.toLowerCase()) {
            case "post":
                httpRequestBase = new HttpPost(url);
                break;
            case "put":
                httpRequestBase = new HttpPut(url);
                break;
            case "get":
                httpRequestBase = new HttpGet(url);
                break;
            case "delete":
                httpRequestBase = new HttpDelete(url);
                break;
            default:
                String error = "Please use only 'POST, 'PUT', 'GET' or 'DELETE' with this specification";
                log.error(error);
                throw new RuntimeException(error);
        }

        log.debug(msg);
        return httpRequestBase;
    }

    private HttpRequestBase httpBaseSpecification(String httpMethod, String endpointUri, String token) {

        String url = String.format("%s%s", baseUri, endpointUri);

        HttpRequestBase httpRequestBase;

        if (url.startsWith("http:") || url.startsWith("https:")) {

            httpRequestBase = httpMethod(httpMethod, url);

            httpRequestBase.addHeader("Authorization", "Basic " + token);

            log.debug("Generated base http POST specification");


        } else {

            String error = String.format(HTTP_SPEC_ERROR, url);

            log.error(error);

            throw new RuntimeException(error);

        }

        return httpRequestBase;
    }


    private HttpRequestBase httpBaseSpecification(String httpMethod, String endpointUri, String contentType, String token) {


        HttpRequestBase httpRequestBase = httpBaseSpecification(httpMethod, endpointUri, token);


        httpRequestBase.addHeader("Content-Type", contentType);


        return httpRequestBase;

    }

    private HttpPost httpPostBaseSpecification(String url, String contentType, String token, String paramsAsString, String charSet) {
        HttpPost httpPost = (HttpPost) httpBaseSpecification("post", url, contentType, token);
        try {
            httpPost.setEntity(new StringEntity(paramsAsString, charSet));
        } catch (UnsupportedCharsetException e) {
            log.error(String.format("The charset [%s] is not valid or cannot be applied to the payload", charSet));

            throw e;
        }


        return httpPost;
    }


    private HttpPut httpPutBaseSpecification(String url, String contentType, String token, String paramsAsString, String charSet) {
        HttpPut httpPut = (HttpPut) httpBaseSpecification("put", url, contentType, token);
        try {
            httpPut.setEntity(new StringEntity(paramsAsString, charSet));
        } catch (UnsupportedCharsetException e) {
            log.error(String.format("The charset [%s] is not valid or cannot be applied to the payload", charSet));

            throw e;
        }


        return httpPut;
    }

    private Map<String, String> httpResponseDecomposer(CloseableHttpResponse httpResponse) throws IOException {

        final String statusCode = "statusCode";

        final String responseAsString = "responseAsString";

        Map<String, String> responseObj = new HashMap<>();

        responseObj.put(statusCode, String.valueOf(httpResponse.getStatusLine().getStatusCode()));

        responseObj.put(responseAsString, EntityUtils.toString(httpResponse.getEntity()));

        final String statusCodeErrorMsg = String.format("Request was not successful - status code: %s", responseObj.get(statusCode));

        final String responseBodyErrorMsg = String.format("The data insertion/fetching was compromised, the obtained response body is: %n%s", responseObj.get(responseAsString));


        if (parseInt(responseObj.get(statusCode)) >= 200 && parseInt(responseObj.get(statusCode)) < 300) {

            log.info(String.format("Request was successful - status code: %s ", responseObj.get(statusCode)));

        } else if (parseInt(responseObj.get(statusCode)) >= 400) {

            log.error(statusCodeErrorMsg);
            log.error(responseBodyErrorMsg);

        }

        return responseObj;
    }

    private String generateQudiniAppToken(String userName, String password) {

        return new Base64Encoder()
                .encode(
                        String.format("%s:%s", userName, password).getBytes())
                .replaceAll("(?:\\r\\n|\\n\\r|\\n|\\r)", "");

    }


}
