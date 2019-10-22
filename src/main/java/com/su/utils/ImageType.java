package com.su.utils;


/**
 * @author su
 * @date 2019/10/22 10:00
 */
public class ImageType {
    /**
     * 判断图片的文件格式<br/>
     * 常用文件的文件头如下：(以前六位为准)<br/>
     * JPEG (jpg)，文件头：FF D8 FF
     * PNG (png)， 文件头：89 50 4E 47
     * GIF (gif)， 文件头：47 49 46 38
     * TIFF (tif)，文件头：49 49 2A 00
     * bmp，       文件头：42 4D
     *
     * @param head 文件的前2个字节
     */
    public static String checkImageType(byte[] head) {
        if (head == null || head.length <= 0) {
            return "";
        }
        String hex = BinUtils.binToHex(head);
        switch (hex) {
            case "FFD8":
                return "jpg";
            case "8950":
                return "png";
            case "4749":
                return "gif";
            case "4949":
                return "tif";
            case "424D":
                return "bmp";
            default:
                return "";
        }
    }
}