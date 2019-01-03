package com.company.uitls;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.XML;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by lelezhang on 2019/1/3.
 */
public class HTTPUtils {
    private static RequestConfig config;

    public HTTPUtils(){
        config = RequestConfig.custom()
                .setConnectionRequestTimeout(3000)
                .setConnectTimeout(3000)
                .setSocketTimeout(3000)
                .build();
    }

    /**
     * 自定义超时时间
     * @param connectionRequestTimeout 指从连接池获取连接的timeout
     * @param connectTimeout 指客户端和服务器建立连接的timeout，超时后会ConnectionTimeOutException
     * @param socketTimeout 指客户端从服务器读取数据的timeout，超出后会抛出SocketTimeOutException
     */
    public HTTPUtils(int connectionRequestTimeout, int connectTimeout, int socketTimeout){
        config = RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout)
                .build();
    }

    /**
     * post请求
     * @param url String
     * @param header String
     * @param requestBody String
     * @return 自定义Response
     */
    public static Response post(String url, String header, String requestBody) throws IOException {
        new HTTPUtils();
        CloseableHttpClient httpclient = buildSSLCloseableHttpClient(url);
        HttpPost httppost = new HttpPost(url);
        httppost.setConfig(config);
        if (header != null && !header.equals("")) {
            for (Map.Entry<String, String> entry : getRequestHeader(header).entrySet()) {
                httppost.setHeader(entry.getKey(), entry.getValue());
            }
        }
        httppost.setEntity(new StringEntity(requestBody));
        CloseableHttpResponse response = httpclient.execute(httppost);
        return getResponse(response);

    }

    /**
     * get请求
     * @param url String
     * @param header String
     * @return 自定义Response
     */
    public static Response get(String url, String header) throws IOException {
        new HTTPUtils();
        CloseableHttpClient httpclient = buildSSLCloseableHttpClient(url);
        HttpGet httpget = new HttpGet(url);
        httpget.setConfig(config);
        if (header != null && !header.equals("")) {
            for (Map.Entry<String, String> entry : getRequestHeader(header).entrySet()) {
                httpget.setHeader(entry.getKey(), entry.getValue());
            }
        }
        CloseableHttpResponse response = httpclient.execute(httpget);
        return getResponse(response);
    }

    /**
     * header格式[{"key1":"value1"},{"key2":"value2"},{"key3":"value3"}]
     * @param header String
     * @return Map<String, String>
     */
    private static Map<String, String> getRequestHeader(String header){
        Map<String, String> headerMap = new HashMap<String, String>();
        JSONArray headerArray = JSONArray.parseArray(header);
        for (int i=0; i<headerArray.size(); i++){
            JSONObject headerObject = headerArray.getJSONObject(i);
            for (String key : headerObject.keySet()){
                headerMap.put(key, headerObject.getString(key));
            }
        }
        return headerMap;
    }

    /**
     * 获取response的header
     * @param headers Header[]
     * @return Map<String, String>
     */
    private static Map<String, String> getResponseHeader(Header[] headers){
        Map<String, String> headerMap = new HashMap<String, String>();
        for (Header header : headers) {
            headerMap.put(header.getName(), header.getValue());
        }
        return headerMap;
    }

    /**
     * https忽略证书
     * @return CloseableHttpClient
     */
    private static CloseableHttpClient buildSSLCloseableHttpClient(String url)  {
        SSLContext sslContext = null;
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) {
                    return true;
                }
            }).build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslContext, new String[] { "TLSv1" }, null,
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        return url.startsWith("https:") ? HttpClients.custom().setSSLSocketFactory(sslsf).build() : HttpClients.createDefault();
    }

    /**
     * 获取自定义Response
     * @param response CloseableHttpResponse
     * @return Response
     */
    private static Response getResponse(CloseableHttpResponse response){
        Response res = null;
        try {
            String result = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
            res = new Response();
            res.responseCode=response.getStatusLine().getStatusCode();
            res.responseHeader=getResponseHeader(response.getAllHeaders());
            res.responseBody= result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    /**
     * json to xml
     * @param json String
     * @return
     */
    public static String json2xml(String json) {
        org.json.JSONObject jsonObj = new org.json.JSONObject(json);
        return "<xml>" + XML.toString(jsonObj) + "</xml>";
    }

    /**
     * xml to json
     * @param xml String
     * @return
     */
    public static String xml2json(String xml) {
        org.json.JSONObject xmlJSONObj = XML.toJSONObject(xml.replace("<xml>", "").replace("</xml>", ""));
        return xmlJSONObj.toString();
    }

    @Data
    public static class Response{
        private int responseCode;
        private Map<String, String> responseHeader;
        private Object responseBody;

        public int getResponseCode() {
            return responseCode;
        }

        public Map<String, String> getResponseHeader() {
            return responseHeader;
        }

        public Object getResponseBody() {
            return responseBody;
        }
    }

    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }


}
