package com.su.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author su
 * @date 2019/10/20 17:19
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Group {
    /**
     * 默认分组ID, 游客即属于该分组
     */
    public static final Long DEFAULT_ID = 1L;

    private Long id;
    private String name;
    private Long keyId;
}
