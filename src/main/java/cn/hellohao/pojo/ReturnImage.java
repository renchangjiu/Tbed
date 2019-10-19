package cn.hellohao.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author Hellohao
 * @version 1.0
 * @date 2019-07-22 11:35
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ReturnImage {
    /**
     * 供访问的URL
     */
    private String url;
    /**
     * 图片原始文件名
     */
    private String name;
    /**
     * 图片大小, 字节
     */
    private long size;


    private String imgurl;
    private String imgname;

    public ReturnImage() {
    }

    public ReturnImage(String imgurl, String imgname) {
        this.imgurl = imgurl;
        this.imgname = imgname;
    }

}
