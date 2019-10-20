package com.su.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Images {

    //id, imgname, imgurl, userid
    private Integer id;
    private String imgname;
    private String imgurl;
    private Long userid;
    private Integer sizes = 0;
    private Integer abnormal;
    private Integer source;
    private String updatetime;
    private String username;
    private Integer storageType;
    private String starttime;
    private String stoptime;


}
	
		

