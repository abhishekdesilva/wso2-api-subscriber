package com.wso2.api.revisioner;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.wso2.api.revisioner.utils.FileUtils;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.http.HttpHeaders;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.json.JSONObject;
import org.springframework.context.ConfigurableApplicationContext;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import com.google.gson.Gson;

@SpringBootApplication
public class Application {

    private static String HOST = "";
    private static String TRANSPORT_PORT = "";
    private static String CLIENT_KEY = "";
    private static String CLIENT_SECRET = "";
    //    private static String SANDBOX_ENDPOINT = "";
//    private static String PRODUCTION_ENDPOINT = "";
//    private static String SANDBOX_WS_ENDPOINT = "";
//    private static String PRODUCTION_WS_ENDPOINT = "";
    private static String USERNAME = "";
    private static String PASSWORD = "";
    private static String API_LIMIT = "";
    private static String IDS = "";
    private static String THROTTLE_POLICY = "";
    private static String WS_THROTTLE_POLICY = "";
    //    private static String REQ_THROTTLE_POLICY = "";
    private static final String REST = "REST";
    private static final String WS = "WS";

    private static final String APPLICATION_HOST = "application.host";
    private static final String APPLICATION_TRANSPORT_PORT = "application.transport.port";
    private static final String APPLICATION_CLIENT_KEY = "application.client.key";
    private static final String APPLICATION_CLIENT_SECRET = "application.client.secret";
    private static final String APPLICATION_USERNAME = "application.username";
    private static final String APPLICATION_PASSWORD = "application.password";
    private static final String APPLICATION_API_LIMIT = "application.api.limit";
    private static final String APPLICATION_IDS = "application.ids";
    private static final String APPLICATION_THROTTLE_POLICY = "application.throttling.policy";
    private static final String APPLICATION_WS_THROTTLE_POLICY = "application.ws.throttling.policy";

    static {
        Properties properties = FileUtils.readConfiguration();
        HOST = properties.getProperty(APPLICATION_HOST, "");
        TRANSPORT_PORT = properties.getProperty(APPLICATION_TRANSPORT_PORT, "");
        CLIENT_KEY = properties.getProperty(APPLICATION_CLIENT_KEY, "");
        CLIENT_SECRET = properties.getProperty(APPLICATION_CLIENT_SECRET, "");
        USERNAME = properties.getProperty(APPLICATION_USERNAME, "");
        PASSWORD = properties.getProperty(APPLICATION_PASSWORD, "");
        API_LIMIT = properties.getProperty(APPLICATION_API_LIMIT, "");
        IDS = properties.getProperty(APPLICATION_IDS, "");
        THROTTLE_POLICY = properties.getProperty(APPLICATION_THROTTLE_POLICY, "");
        WS_THROTTLE_POLICY = properties.getProperty(APPLICATION_WS_THROTTLE_POLICY, "");
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter pw = null;
        try {
            fw = FileUtils.getNewFileWriter();
            bw = new BufferedWriter(fw);
            pw = new PrintWriter(bw);
            System.out.println("HOST : " + HOST);
            System.out.println("TRANSPORT_PORT : " + TRANSPORT_PORT);
            System.out.println("CLIENT_KEY : " + CLIENT_KEY);
            System.out.println("CLIENT_SECRET : " + CLIENT_SECRET);
            System.out.println("USERNAME : " + USERNAME);
            System.out.println("PASSWORD : " + PASSWORD);
            System.out.println("API_LIMIT : " + API_LIMIT);
            System.out.println("IDS : " + IDS);
            System.out.println("THROTTLE_POLICY : " + THROTTLE_POLICY);
            System.out.println("WS_THROTTLE_POLICY : " + WS_THROTTLE_POLICY);

            pw.println("HOST : " + HOST);
            pw.println("TRANSPORT_PORT : " + TRANSPORT_PORT);
            pw.println("CLIENT_KEY : " + CLIENT_KEY);
            pw.println("CLIENT_SECRET : " + CLIENT_SECRET);
            pw.println("USERNAME : " + USERNAME);
            pw.println("PASSWORD : " + PASSWORD);
            pw.println("API_LIMIT : " + API_LIMIT);
            pw.println("IDS : " + IDS);
            pw.println("THROTTLE_POLICY : " + THROTTLE_POLICY);
            pw.println("WS_THROTTLE_POLICY : " + WS_THROTTLE_POLICY);

            if (!HOST.equals("") && !TRANSPORT_PORT.equals("") && !CLIENT_KEY.equals("") && !CLIENT_SECRET.equals("")
                    && !USERNAME.equals("") && !PASSWORD.equals("") && !API_LIMIT.equals("") && !IDS.equals("")
                    && !THROTTLE_POLICY.equals("") && !WS_THROTTLE_POLICY.equals("")) {

                Scanner userInput = new Scanner(System.in);
                System.out.println("*************************************************************");
                System.out.println("Hey, what would you like to do?");
                System.out.println("Enter \"S\" to add the subscriptions");
                System.out.println("OR");
                System.out.println("Enter \"R\" to remove the subscriptions");
                System.out.println("*************************************************************");
                String action = userInput.nextLine();

                if (action.equals("S")) {
                    String accessToken = generateAccessToken(pw);
                    System.out.println("*************************************************************");
                    pw.println("*************************************************************");
                    if (accessToken != null) {
                        HashMap<String, String> apiIdTypeMap = retrieveAllAPIIds(accessToken, pw);
                        if (apiIdTypeMap.size() != 0) {
                            System.out.println("*************************************************************");
                            pw.println("*************************************************************");
                            System.out.println("Total Number of APIs identified : " + apiIdTypeMap.size());
                            pw.println("Total Number of APIs identified : " + apiIdTypeMap.size());
                            System.out.println("*************************************************************");
                            pw.println("*************************************************************");
                            if (addSubscription(accessToken, apiIdTypeMap, pw)) {
                                System.out.println("All the APIs were subscribed successfully.");
                                pw.println("All the APIs were subscribed successfully.");
                            } else {
                                System.out.println("API Subscription was NOT successful.");
                                pw.println("API Subscription was NOT successful.");
                            }
                        }
                    }

                } else if (action.equals("R")) {
                    String accessToken = generateAccessToken(pw);
                    System.out.println("*************************************************************");
                    pw.println("*************************************************************");
                    if (accessToken != null) {
                        if (removeSubscription(accessToken, pw)) {
                            System.out.println("Subscription removing process is completed.");
                            pw.println("Subscription removing process is completed.");
                        } else {
                            System.out.println("Subscription removing process was NOT completed.");
                            pw.println("Subscription removing process was NOT completed.");
                        }
                    }
                } else {
                    System.out.println("You are only allowed to enter either S or R as the input. Please try again.");

                }
            } else {
                System.out.println("Parameters were not loaded correctly. Please check the integration.properties file.");
                pw.println("Parameters were not loaded correctly. Please check the integration.properties file.");
            }
            pw.flush();
        } catch (Exception e) {
            System.out.println("An exception has been thrown when attempting complete the process : " + e);
            pw.println("An exception has been thrown when attempting complete the process : " + e);
        } finally {
            try {
                pw.close();
                bw.close();
                fw.close();
                ctx.close();
            } catch (Exception e) {
                ctx.close();
            }
        }
    }

    private static String generateAccessToken(PrintWriter pw) {
        final String REQUEST_BODY = "{\"grant_type\": \"password\",\n" +
                "\"username\":\"" + USERNAME + "\",\n" +
                "\"password\":\"" + PASSWORD + "\",\n" +
                "\"scope\":\"apim:admin apim:api_key apim:app_import_export apim:app_manage apim:store_settings apim:sub_alert_manage apim:sub_manage apim:subscribe openid apim:subscribe\"\n" +
                "}";

        String appCredentials = CLIENT_KEY + ":" + CLIENT_SECRET;
        String encodedString = Base64.getEncoder().encodeToString(appCredentials.getBytes());

        HttpPost httpPost = new HttpPost("https://" + HOST + ":" + TRANSPORT_PORT + "/oauth2/token");
        try {
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            StringEntity entity = new StringEntity(REQUEST_BODY);
            httpPost.setEntity(entity);

            httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedString);

            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            CloseableHttpClient client = HttpClients.custom()
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build();

            CloseableHttpResponse response = client.execute(httpPost);

            if (response.getStatusLine().getStatusCode() == 200) {
                StringBuilder sb = new StringBuilder();
                String line;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
                        StandardCharsets.UTF_8))) {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    //System.out.println("FULL TOKEN RESPONSE : " + sb.toString());
                    JSONObject object = new JSONObject(sb.toString());
                    String accessToken = (String) object.get("access_token");
                    System.out.println("ACCESS TOKEN : " + accessToken);
                    pw.println("ACCESS TOKEN : " + accessToken);
                    return accessToken;

                } catch (Exception e) {
                    System.out.println("An exception has been thrown when attempting to read the response for the token service : " + e);
                    pw.println("An exception has been thrown when attempting to read the response for the token service : " + e);
                    return null;
                }
            } else {
                System.out.println("Generate Token Returned Status Code : " + response.getStatusLine().getStatusCode());
                pw.println("Generate Token Returned Status Code : " + response.getStatusLine().getStatusCode());
                return null;
            }

        } catch (Exception e) {
            System.out.println("An exception has been thrown when attempting complete the token generation process : " + e);
            pw.println("An exception has been thrown when attempting complete the token generation process : " + e);
            return null;
        }
    }

    private static HashMap<String, String> retrieveAllAPIIds(String accessToken, PrintWriter pw) {
        HttpGet httpGet = new HttpGet("https://" + HOST + ":" + TRANSPORT_PORT + "/api/am/devportal/v2.1/apis?limit=" + API_LIMIT);
        try {
            httpGet.setHeader("Accept", "application/json");

            httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            CloseableHttpClient client = HttpClients.custom()
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build();

            CloseableHttpResponse response = client.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == 200) {
                StringBuilder sb = new StringBuilder();
                String line;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
                        StandardCharsets.UTF_8))) {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
//                    System.out.println("Full Response : " + sb.toString());
                    JSONObject object = new JSONObject(sb.toString());
//                    List<String> apiObjArr = new ArrayList<>();
                    HashMap<String, String> hashMap = new HashMap<>();
                    JSONArray jsonArray = object.getJSONArray("list");
//                    System.out.println("List of All APIs : " + jsonArray);
                    if (jsonArray != null) {
                        //Iterating JSON array
                        for (int i = 0; i < jsonArray.length(); i++) {
                            //Adding each element of JSON array into ArrayList
                            JSONObject tempObj = (JSONObject) jsonArray.get(i);
                            String lifeCycleStatus = tempObj.getString("lifeCycleStatus");
                            String type = tempObj.getString("type");
                            if (!lifeCycleStatus.equals("DEPRECATED") && !lifeCycleStatus.equals("RETIRED")) {
                                hashMap.put((String) tempObj.get("id"), type);
                            } else {
                                System.out.println("Identified the API : " + tempObj.getString("id") + " is " + lifeCycleStatus);
                                pw.println("Identified the API : " + tempObj.getString("id") + " is " + lifeCycleStatus);
                            }
                        }
                    }
                    return hashMap;

                } catch (Exception e) {
                    System.out.println("An exception has been thrown when attempting to read the response for the publisher get all api service : " + e);
                    pw.println("An exception has been thrown when attempting to read the response for the publisher get all api service : " + e);
                    return null;
                }
            } else {
                System.out.println("Retrieve All APIs Returned Status Code : " + response.getStatusLine().getStatusCode());
                pw.println("Retrieve All APIs Returned Status Code : " + response.getStatusLine().getStatusCode());
                return null;
            }

        } catch (Exception e) {
            System.out.println("An exception has been thrown when attempting complete the retrieve all APIs process : " + e);
            pw.println("An exception has been thrown when attempting complete the retrieve all APIs process : " + e);
            return null;
        }
    }

    private static List<String> checkAPISubscription(String accessToken, String appId, PrintWriter pw) {
        HttpGet httpGet = new HttpGet("https://" + HOST + ":" + TRANSPORT_PORT + "/api/am/devportal/v2.1/subscriptions?applicationId=" + appId);
        try {
            httpGet.setHeader("Accept", "application/json");

            httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            CloseableHttpClient client = HttpClients.custom()
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build();

            CloseableHttpResponse response = client.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == 200) {
                StringBuilder sb = new StringBuilder();
                String line;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
                        StandardCharsets.UTF_8))) {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
//                    System.out.println("Full Response : " + sb.toString());
                    JSONObject object = new JSONObject(sb.toString());
                    JSONArray jsonArray = object.getJSONArray("list");
//                    System.out.println("List of All APIs : " + jsonArray);
                    List<String> apiIdsList = new ArrayList<>();
                    if (jsonArray != null) {
                        //Iterating JSON array
                        for (int i = 0; i < jsonArray.length(); i++) {
                            //Adding each element of JSON array into ArrayList
                            JSONObject tempObj = (JSONObject) jsonArray.get(i);
                            String apiId = tempObj.getString("apiId");
                            apiIdsList.add(apiId);
                        }
                    }
//                    System.out.println("All API Ids : "+apiIdsList.toString());
                    return apiIdsList;

                } catch (Exception e) {
                    System.out.println("An exception has been thrown when attempting to read the response for the publisher get all api service : " + e);
                    pw.println("An exception has been thrown when attempting to read the response for the publisher get all api service : " + e);
                    return null;
                }
            } else {
                System.out.println("Retrieve All APIs Returned Status Code : " + response.getStatusLine().getStatusCode());
                pw.println("Retrieve All APIs Returned Status Code : " + response.getStatusLine().getStatusCode());
                return null;
            }

        } catch (Exception e) {
            System.out.println("An exception has been thrown when attempting complete the check subscription process : " + e);
            pw.println("An exception has been thrown when attempting complete the check subscription process : " + e);
            return null;
        }
    }

    private static boolean addSubscription(String accessToken, HashMap<String, String> apiIdTypeMap, PrintWriter pw) {


        HttpPost httpPost = new HttpPost("https://" + HOST + ":" + TRANSPORT_PORT + "/api/am/devportal/v2.1/subscriptions/multiple");
        try {
//            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");
            String[] appIds = IDS.split(",");
            System.out.println("Total Number of Applications read from the configuration : " + appIds.length);
            pw.println("Total Number of Applications read from the configuration : " + appIds.length);
            System.out.println("*************************************************************");
            pw.println("*************************************************************");
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < appIds.length; i++) {
                List<String> subscribedApiIdsList = checkAPISubscription(accessToken, appIds[i], pw);
                for (Map.Entry<String, String> entry : apiIdTypeMap.entrySet()) {
                    String apiId = entry.getKey();
                    String apiType = entry.getValue();
                    if (!subscribedApiIdsList.contains(apiId)) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("apiId", apiId);
                        jsonObject.put("applicationId", appIds[i]);
                        if (apiType.equals(WS)) {
                            jsonObject.put("throttlingPolicy", WS_THROTTLE_POLICY);
                        } else {
                            jsonObject.put("throttlingPolicy", THROTTLE_POLICY);
                        }
                        jsonArray.put(jsonObject);
                        System.out.println("API Id : " + apiId + " API Type : " + apiType + " is going to be subscribed to : " + appIds[i]);
                        pw.println("API Id : " + apiId + " API Type : " + apiType + " is going to be subscribed to : " + appIds[i]);
                    } else {
                        System.out.println("API Id : " + apiId + " API Type : " + apiType + " is already subscribed to Application Id : " + appIds[i]);
                        pw.println("API Id : " + apiId + " API Type : " + apiType + " is already subscribed to Application Id : " + appIds[i]);
                        System.out.println("*************************************************************");
                        pw.println("*************************************************************");
                    }
                }
                System.out.println("*************************************************************");
                pw.println("*************************************************************");
            }

            StringEntity entity = new StringEntity(jsonArray.toString());
            httpPost.setEntity(entity);

            httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            CloseableHttpClient client = HttpClients.custom()
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build();

            CloseableHttpResponse response = client.execute(httpPost);

            if (response.getStatusLine().getStatusCode() == 200) {
                StringBuilder sb = new StringBuilder();
                String line;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
                        StandardCharsets.UTF_8))) {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
//                    System.out.println("Full Updated API Response : " + sb.toString());
                    return true;

                } catch (IOException e) {
                    System.out.println("An exception has been thrown when attempting to read the response for the dev portal add subscription service : " + e);
                    pw.println("An exception has been thrown when attempting to read the response for the dev portal add subscription service : " + e);
                    return false;
                } catch (Exception e) {
                    return false;
                }
            } else {
                System.out.println("Add Subscription API Returned Status Code : " + response.getStatusLine().getStatusCode());
                pw.println("Add Subscription API Returned Status Code : " + response.getStatusLine().getStatusCode());
                return false;
            }

        } catch (Exception e) {
            System.out.println("An exception has been thrown when attempting complete the add subscription process : " + e);
            pw.println("An exception has been thrown when attempting complete the add subscription process : " + e);
            return false;
        }
    }

    private static boolean removeSubscription(String accessToken, PrintWriter pw) {
        String[] appIds = IDS.split(",");
        System.out.println("Total Number of Applications read from the configuration : " + appIds.length);
        pw.println("Total Number of Applications read from the configuration : " + appIds.length);
        System.out.println("*************************************************************");
        pw.println("*************************************************************");
        for (int i = 0; i < appIds.length; i++) {
            List<String> subscriptionIds = getSubscription(accessToken, appIds[i], pw);
            if (subscriptionIds != null && subscriptionIds.size() > 0) {
                for (String subId : subscriptionIds) {
                    HttpDelete httpDelete = new HttpDelete("https://" + HOST + ":" + TRANSPORT_PORT + "/api/am/devportal/v2/subscriptions/" + subId);
                    try {
                        httpDelete.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

                        TrustManager[] trustAllCerts = new TrustManager[]{
                                new X509TrustManager() {
                                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                        return new X509Certificate[0];
                                    }

                                    public void checkClientTrusted(
                                            java.security.cert.X509Certificate[] certs, String authType) {
                                    }

                                    public void checkServerTrusted(
                                            java.security.cert.X509Certificate[] certs, String authType) {
                                    }
                                }
                        };
                        SSLContext sslContext = SSLContext.getInstance("SSL");
                        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                        CloseableHttpClient client = HttpClients.custom()
                                .setSSLContext(sslContext)
                                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                                .build();

                        CloseableHttpResponse response = client.execute(httpDelete);

                        if (response.getStatusLine().getStatusCode() == 200) {
                            StringBuilder sb = new StringBuilder();
                            String line;
                            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
                                    StandardCharsets.UTF_8))) {
                                while ((line = reader.readLine()) != null) {
                                    sb.append(line + "\n");
                                }
                                System.out.println("Subscription Id : " + subId + " has been successfully removed for Application Id : " + appIds[i]);
                                pw.println("Subscription Id : " + subId + " has been successfully removed for Application Id : " + appIds[i]);
                            } catch (Exception e) {
                                System.out.println("An exception has been thrown when attempting to read the response for the dev portal add subscription service : " + e);
                                pw.println("An exception has been thrown when attempting to read the response for the dev portal add subscription service : " + e);
                            }
                        } else {
                            System.out.println("Add Subscription API Returned Status Code : " + response.getStatusLine().getStatusCode());
                            pw.println("Add Subscription API Returned Status Code : " + response.getStatusLine().getStatusCode());
                            continue;
                        }

                    } catch (Exception e) {
                        System.out.println("An exception has been thrown when attempting complete the remove subscription process : " + e);
                        pw.println("An exception has been thrown when attempting complete the remove subscription process : " + e);
                        return false;
                    }
                }
                System.out.println("*************************************************************");
                pw.println("*************************************************************");
            }
        }
        return true;
    }

    private static List<String> getSubscription(String accessToken, String appId, PrintWriter pw) {

        HttpGet httpGet = new HttpGet("https://" + HOST + ":" + TRANSPORT_PORT + "/api/am/devportal/v2/subscriptions?applicationId=" + appId);
        try {
            httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            CloseableHttpClient client = HttpClients.custom()
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build();

            CloseableHttpResponse response = client.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == 200) {
                StringBuilder sb = new StringBuilder();
                String line;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
                        StandardCharsets.UTF_8))) {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    JSONObject object = new JSONObject(sb.toString());
                    JSONArray listOfSubsArr = (JSONArray) object.get("list");
                    List<String> subscripitionList = new ArrayList<>();
                    if (listOfSubsArr.length() > 0) {
                        for (int i = 0; i < listOfSubsArr.length(); i++) {
                            JSONObject jsonObject = listOfSubsArr.getJSONObject(i);
                            subscripitionList.add(jsonObject.getString("subscriptionId"));
                        }
                    } else {
                        System.out.println("No subscriptions available for the Application Id : " + appId);
                        pw.println("No subscriptions available for the Application Id : " + appId);
                    }
                    return subscripitionList;

                } catch (IOException e) {
                    System.out.println("An exception has been thrown when attempting to read the response for the dev portal add subscription service : " + e);
                    pw.println("An exception has been thrown when attempting to read the response for the dev portal add subscription service : " + e);
                    return null;
                } catch (Exception e) {
                    return null;
                }
            } else {
                System.out.println("Add Subscription API Returned Status Code : " + response.getStatusLine().getStatusCode());
                pw.println("Add Subscription API Returned Status Code : " + response.getStatusLine().getStatusCode());
                return null;
            }

        } catch (Exception e) {
            System.out.println("An exception has been thrown when attempting complete the get subscription process : " + e);
            pw.println("An exception has been thrown when attempting complete the get subscription process : " + e);
            return null;
        }
    }

}
