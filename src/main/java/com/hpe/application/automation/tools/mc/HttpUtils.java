package com.hpe.application.automation.tools.mc;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HttpUtils {

    public static final String POST = "POST";
    public static final String GET = "GET";

    private HttpUtils() {

    }

    public static HttpResponse post(ProxyInfo proxyInfo, String url, Map<String, String> headers, byte[] data) {

        HttpResponse response = null;

        try {
            response = doHttp(proxyInfo, POST, url, null, headers, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public static HttpResponse get(ProxyInfo proxyInfo, String url, Map<String, String> headers, String queryString) {

        HttpResponse response = null;
        try {
            response = doHttp(proxyInfo, GET, url, queryString, headers, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    private static HttpResponse doHttp(ProxyInfo proxyInfo, String requestMethod, String connectionUrl, String queryString, Map<String, String> headers, byte[] data) throws IOException {
        HttpResponse response = new HttpResponse();

        if ((queryString != null) && !queryString.isEmpty()) {
            connectionUrl += "?" + queryString;
        }

        URL url = new URL(connectionUrl);


        HttpURLConnection connection = (HttpURLConnection) openConnection(proxyInfo, url);

        connection.setRequestMethod(requestMethod);

        setConnectionHeaders(connection, headers);

        if (data != null && data.length > 0) {
            connection.setDoOutput(true);
            try {
                OutputStream out = connection.getOutputStream();
                out.write(data);
                out.flush();
                out.close();
            } catch (Throwable cause) {
                cause.printStackTrace();
            }
        }

        connection.connect();


        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            JSONObject jsonObject = convertStreamToJSONObject(inputStream);
            Map<String, List<String>> headerFields = connection.getHeaderFields();
            response.setHeaders(headerFields);
            response.setJsonObject(jsonObject);
        }

        connection.disconnect();

        return response;
    }

    private static URLConnection openConnection(final ProxyInfo proxyInfo, URL _url) throws IOException {

        Proxy proxy = null;

        if (proxyInfo != null && proxyInfo._host != null && proxyInfo._port != null && !proxyInfo._host.isEmpty() && !proxyInfo._port.isEmpty()) {

            try {
                int port = Integer.parseInt(proxyInfo._port.trim());
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyInfo._host, port));

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (proxy != null && proxyInfo._userName != null && proxyInfo._password != null && !proxyInfo._password.isEmpty() && !proxyInfo._password.isEmpty()) {
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(proxyInfo._userName, proxyInfo._password.toCharArray());    //To change body of overridden methods use File | Settings | File Templates.
                }
            };


            Authenticator.setDefault(authenticator);
        }

        if (proxy == null) {
            return _url.openConnection();
        }


        return _url.openConnection(proxy);
    }


    private static void setConnectionHeaders(HttpURLConnection connection, Map<String, String> headers) {

        if (connection != null && headers != null && headers.size() != 0) {
            Iterator<Map.Entry<String, String>> headersIterator = headers.entrySet().iterator();
            while (headersIterator.hasNext()) {
                Map.Entry<String, String> header = headersIterator.next();
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
        }

    }

    private static JSONObject convertStreamToJSONObject(InputStream inputStream) {
        JSONObject obj = null;

        if (inputStream != null) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer res = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    res.append(line);
                }
                obj = (JSONObject) JSONValue.parseStrict(res.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return obj;
    }


    public static ProxyInfo setProxyCfg(String host, String port, String userName, String password) {

        return new ProxyInfo(host, port, userName, password);
    }

    public static ProxyInfo setProxyCfg(String host, String port) {

        ProxyInfo proxyInfo = new ProxyInfo();

        proxyInfo._host = host;
        proxyInfo._port = port;

        return proxyInfo;
    }

    public static ProxyInfo setProxyCfg(String address, String userName, String password) {
        ProxyInfo proxyInfo = new ProxyInfo();

        if (address != null) {
            if (address.endsWith("/")) {
                int end = address.lastIndexOf("/");
                address = address.substring(0, end);
            }

            int index = address.lastIndexOf(':');
            if (index > 0) {
                proxyInfo._host = address.substring(0, index);
                proxyInfo._port = address.substring(index + 1, address.length());
            } else {
                proxyInfo._host = address;
                proxyInfo._port = "80";
            }
        }
        proxyInfo._userName = userName;
        proxyInfo._password = password;

        return proxyInfo;
    }

    static class ProxyInfo {
        String _host;
        String _port;
        String _userName;
        String _password;

        public ProxyInfo() {

        }

        public ProxyInfo(String host, String port, String userName, String password) {
            _host = host;
            _port = port;
            _userName = userName;
            _password = password;
        }

    }
}