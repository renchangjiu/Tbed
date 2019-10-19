package cn.hellohao.pojo;

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
    // 默认的时间字符串格式

    private Integer id;

    private String name;

    private String url;

    private Integer userid;

    private Long size = 0L;

    private Integer abnormal;

    private Integer source;

    private String updatetime;

    private String username;

    private Integer storageType;

    private String starttime;

    private String stoptime;

}
	
		

