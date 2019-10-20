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
public class Keys {
    private Integer id;
    private String AccessKey;
    private String AccessSecret;
    private String Endpoint;
    private String Bucketname;
    private String RequestAddress;
    private Integer storageType;

    public Keys() {
        super();
    }

    public Keys(Integer id, String accessKey, String accessSecret, String endpoint, String bucketname,
                String requestAddress, Integer storageType) {
        super();
        this.id = id;
        AccessKey = accessKey;
        AccessSecret = accessSecret;
        Endpoint = endpoint;
        Bucketname = bucketname;
        RequestAddress = requestAddress;
        this.storageType = storageType;
    }

}
