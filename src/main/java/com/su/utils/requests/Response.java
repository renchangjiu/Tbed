package com.su.utils.requests;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author su
 * @date 2018/8/29 21:56
 */
public class Response {

    private static final Pattern CHARSET_PATTERN = Pattern.compile("charset=(.*)");

    private static final Pattern CHARSET_PATTERN2 = Pattern.compile("<meta.*?charset=['\"]?(.*?)['\"/>]");

    private CloseableHttpResponse httpResponse;
    private HttpRequestBase httpRequestBase;
    private Map<String, String> cookies;
    private String text;
    private byte[] content = new byte[0];

    /**
     * 构造方法中将响应体保存到字节数组中
     *
     * @param httpResponse    response
     * @param httpRequestBase httpRequestBase
     * @throws IOException IOException
     */
    Response(CloseableHttpResponse httpResponse, HttpRequestBase httpRequestBase, Map<String, String> cookies) throws IOException {
        this.httpResponse = httpResponse;
        this.httpRequestBase = httpRequestBase;
        this.cookies = cookies;
        HttpEntity entity = httpResponse.getEntity();
        if (entity != null) {
            InputStream in = entity.getContent();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int len = 0;
            byte[] bytes = new byte[2048];
            while ((len = in.read(bytes)) != -1) {
                out.write(bytes, 0, len);
            }
            this.content = out.toByteArray();
            out.close();
            in.close();
        }
    }


    public String getText() throws IOException {
        if (StringUtils.isEmpty(this.text)) {
            this.text = new String(this.content, this.getEncoding());
        }
        return this.text;
    }

    public byte[] getContent() {
        return this.content;
    }


    public String getEncoding() throws IOException {
        // 先从请求头中找
        String encoding = this.getEncodingFromHeader();
        if (StringUtils.isEmpty(encoding)) {
            return encoding;
        }
        String html = new String(this.content, StandardCharsets.UTF_8);
        Matcher matcher = CHARSET_PATTERN2.matcher(html);
        if (matcher.find()) {
            encoding = matcher.group(1);
        } else {
            // 若未找到, 则最终使用utf-8
            encoding = "UTF-8";
        }
        return encoding;
    }

    /**
     * 尝试从请求头中获得网页编码格式
     *
     * @return 编码方式, 如果没有找到则返回 ""
     */
    private String getEncodingFromHeader() {
        String encoding = "";
        String header = this.getHeader("Content-Type");
        Matcher m = CHARSET_PATTERN.matcher(header);
        if (m.find()) {
            encoding = m.group(1);
        }
        return encoding;
    }

    public Map<String, String> getCookies() {
        return this.cookies;
    }

    public String getCookie(String key) {
        return this.cookies.get(key);
    }

    public String getUrl() {
        return httpRequestBase.getURI().toString();
    }

    /**
     * 获取响应头, 若存在相同的响应头则随机返回其中之一, 若未找到则返回空串
     *
     * @param key key
     * @return header
     */
    public String getHeader(String key) {
        return this.getHeaders().getOrDefault(key, "");
    }

    public Map<String, String> getHeaders() {
        Map<String, String> ret = new HashMap<>();
        Header[] headers = this.httpResponse.getAllHeaders();
        for (Header header : headers) {
            ret.put(header.getName(), header.getValue());
        }
        return ret;
    }

    static String urlEncode(String string) {
        try {
            return URLEncoder.encode(string, StandardCharsets.UTF_8.displayName());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    static String urlDecode(String string) {
        try {
            return URLDecoder.decode(string, StandardCharsets.UTF_8.displayName());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }


}
