package com.su.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 进制转换工具类
 *
 * @author su
 * @date 2019/10/22 10:03
 */
public class BinUtils {


    /**
     * 二进制转16进制字符串
     *
     * @param src 字节数组
     * @return 16进制字符串
     */
    public static String binToHex(byte[] src) {
        if (src == null || src.length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : src) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                sb.append(0);
            }
            sb.append(hv);
        }
        return sb.toString().toUpperCase();
    }


    /**
     * 获取网络文件大小
     */
    public static long getFileLength(String downloadUrl) throws IOException {
        if (downloadUrl == null || "".equals(downloadUrl) || downloadUrl.length() <= 7) {
            return 0L;
        }
        URL url = new URL(downloadUrl);
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows 7; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.73 Safari/537.36 YNoteCef/5.8.0.1 (Windows)");
            return (long) conn.getContentLength();
        } catch (IOException e) {
            return 0L;
        } finally {
            conn.disconnect();
        }
    }


    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }


    /**
     * 根据java.io.*的流获取文件大小
     *
     * @param file
     */

    public static Integer getFileSize2(File file) {
        Integer imgsize = 0;
        FileInputStream fis = null;

        try {

            if (file.exists() && file.isFile()) {

                String fileName = file.getName();

                fis = new FileInputStream(file);
                imgsize = fis.available() / 1024;
                System.out.println("文件" + fileName + "的大小是：" + fis.available() / 1024 + "K\n");

            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (null != fis) {

                try {

                    fis.close();

                } catch (IOException e) {

                    e.printStackTrace();

                }

            }

        }
        return imgsize;
    }


}
