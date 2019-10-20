package com.su.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author su
 * @date 2019/10/19 17:41
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Image {
    private Long id;

    private String name;

    private String url;

    private Long userId;

    /**
     * 图片大小, 单位: KB
     */
    private Long size = 0L;

    private Integer abnormal;

    private Integer source;

    private String updatetime;

    private String userName;

    private Integer storageType;

    private String starttime;

    private String stoptime;

}
	
		

