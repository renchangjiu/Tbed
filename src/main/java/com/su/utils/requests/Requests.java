package com.su.utils.requests;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author su
 * @date 2018/8/29 21:39
 */
public class Requests {


    public static Response get(String url) throws Exception {
        return get(url, null, null, null, 0);
    }

    public static Response post(String url) throws Exception {
        return post(url, null, null, null, 0);
    }

    /**
     * @param url     url, url后参数将被忽略
     * @param params  请求参数
     * @param headers 请求头
     * @param timeout 超时时间, 毫秒, 小于等于0则为无限等待
     * @return 响应
     */
    public static Response get(String url, Map<String, String> params, Map<String, String> cookies, Map<String, String> headers, int timeout) throws Exception {
        CookieStore cookieStore = genCookieStore(cookies, url);
        CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();

        URIBuilder uriBuilder = new URIBuilder(url);
        // 设置请求参数
        if (params != null && params.size() != 0) {
            List<NameValuePair> list = new LinkedList<>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            uriBuilder.setParameters(list);
        }

        HttpGet httpGet = new HttpGet(uriBuilder.build());

        // 设置超时时间
        if (timeout > 0) {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).build();
            httpGet.setConfig(requestConfig);
        }

        // 设置请求头
        if (headers != null && headers.size() != 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpGet.setHeader(entry.getKey(), URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.displayName()));
            }
        }
        CloseableHttpResponse httpResponse = client.execute(httpGet);

        // // 响应的 cookie 中包含了请求的 cookie, 需要排除
        List<Cookie> collect = cookieStore.getCookies().stream().filter(cookie -> cookies == null || !cookies.containsKey(cookie.getName())).collect(Collectors.toList());
        Map<String, String> respCookie = collect.stream().collect(Collectors.toMap(Cookie::getName, cookie -> Response.urlDecode(cookie.getValue())));
        return new Response(httpResponse, httpGet, respCookie);
    }


    /**
     * @param url     url
     * @param params  请求参数
     * @param headers 请求头
     * @param timeout 超时时间, 毫秒, 小于等于0则为无限等待
     * @return 响应
     */
    public static Response post(String url, Map<String, String> params, Map<String, String> cookies, Map<String, String> headers, int timeout) throws Exception {
        CookieStore cookieStore = genCookieStore(cookies, url);
        CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        HttpPost httpPost = new HttpPost(url);
        // 设置请求参数
        List<NameValuePair> list = new LinkedList<>();
        if (params != null && params.size() != 0) {
            for (String key : params.keySet()) {
                list.add(new BasicNameValuePair(key, params.get(key)));
            }
        }
        HttpEntity entity = new UrlEncodedFormEntity(list, StandardCharsets.UTF_8);
        httpPost.setEntity(entity);

        // 设置超时时间
        if (timeout > 0) {
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).build();
            httpPost.setConfig(requestConfig);
        }

        // 设置请求头
        if (headers != null) {
            for (String key : headers.keySet()) {
                httpPost.setHeader(key, headers.get(key));
            }
        }
        CloseableHttpResponse httpResponse = client.execute(httpPost);

        // // 响应的 cookie 中包含了请求的 cookie, 需要排除
        List<Cookie> collect = cookieStore.getCookies().stream().filter(cookie -> cookies == null || !cookies.containsKey(cookie.getName())).collect(Collectors.toList());
        Map<String, String> respCookie = collect.stream().collect(Collectors.toMap(Cookie::getName, cookie -> Response.urlDecode(cookie.getValue())));
        return new Response(httpResponse, httpPost, respCookie);
    }

    private static CookieStore genCookieStore(Map<String, String> cookies, String url) throws URISyntaxException, UnsupportedEncodingException {
        CookieStore cookieStore = new BasicCookieStore();
        URIBuilder uriBuilder = new URIBuilder(url);
        if (cookies != null) {
            for (Map.Entry<String, String> entry : cookies.entrySet()) {
                BasicClientCookie cookie = new BasicClientCookie(entry.getKey(), URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.displayName()));
                cookie.setDomain(uriBuilder.getHost());
                cookieStore.addCookie(cookie);
            }
        }
        return cookieStore;
    }

}
