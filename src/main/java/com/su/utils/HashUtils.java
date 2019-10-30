package com.su.utils;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author su
 * @date 2018/12/30 12:58
 */
public class HashUtils {
    /**
     * 获取字符的 md5 值
     *
     * @param source 进行散列计算(MD5)的字符
     * @return 散列计算的结果
     */
    public static String MD5(String source) {
        try {
            // 获得实现指定摘要算法的 MessageDigest 对象
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 获得存放哈希值结果的 byte 数组
            byte[] md5Bytes = md.digest(source.getBytes());
            // 转成16进制字符串
            return new BigInteger(1, md5Bytes).toString(16);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取字符的 md5 值
     *
     * @param source 进行散列计算(MD5)的字符
     * @param salt   盐
     * @param repeat 散列次数
     * @return 散列计算的结果
     */
    public static String MD5(String source, String salt, int repeat) {
        String ret = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            for (int i = 0; i < repeat; i++) {
                if (ret.equals("")) {
                    byte[] md5Bytes = md.digest((source + salt).getBytes());
                    ret = new BigInteger(1, md5Bytes).toString(16);
                } else {
                    byte[] md5Bytes = md.digest(ret.getBytes());
                    ret = new BigInteger(1, md5Bytes).toString(16);
                }
            }
            return ret;
        } catch (Exception e) {
            return "";
        }
    }


    @Test
    public void t3estt() throws Exception {
        File file = new File("D:\\13595\\download\\BaiduNetdiskDownload\\刀剑神域.mp4");
        long start = System.currentTimeMillis();
        // System.out.println(HashUtil.MD5(file));
        System.out.println(HashUtils.MD5WithNio(file));
        System.out.println(System.currentTimeMillis() - start);
    }

    /**
     * 计算字符串的 SHA1
     *
     * @param str 需要进行散列计算(SHA)的字符
     * @return 散列计算的结果
     */
    public static String SHA(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] SHABytes = md.digest(str.getBytes());
            return new BigInteger(1, SHABytes).toString(16);
        } catch (Exception e) {
            return "";
        }
    }


    /**
     * 获取文件的 md5 值
     *
     * @param file File
     */
    public static String MD5(File file) {
        FileInputStream fis = null;
        String result = "";
        try {
            fis = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = fis.read(buffer, 0, 1024)) != -1) {
                md.update(buffer, 0, length);
            }
            BigInteger bigInt = new BigInteger(1, md.digest());
            result = bigInt.toString(16);
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (IOException ie) {
            ie.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 使用nio获取文件的 md5 值
     *
     * @param file File
     */
    public static String MD5WithNio(File file) throws IOException {
        if (!file.isFile()) {
            return "";
        }
        String result = "";
        FileChannel channel = new FileInputStream(file).getChannel();
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (channel.read(buffer) != -1) {
                buffer.flip();
                md.update(buffer);
                buffer.clear();
            }
            channel.close();
            BigInteger bigInt = new BigInteger(1, md.digest());
            result = bigInt.toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }
}
