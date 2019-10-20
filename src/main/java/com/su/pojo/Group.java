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
    private Long id;
    private String name;
    // private Long keyId;

    private String groupname;
    private Integer keyid;


}
