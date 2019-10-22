package com.su.pojo;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author su
 * @date 2019/10/19 12:25
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Key {
    private Integer id;
    private String AccessKey;
    private String AccessSecret;
    private String Endpoint;
    private String Bucketname;
    private String RequestAddress;
    private Integer storageType;


}
