package com.su.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author su
 * @date 2019/10/20 14:39
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class User {

    private Integer id;
    //@NotBlank(message = "用户名不能为空")
    // @Length(min = 6, max = 20, message = "用户名需要为 6 - 20 个字符")
    private String username;
    //@NotBlank(message = "密码不能为空")
    private String password;
    // @NotBlank(message = "邮箱不能为空")
    //@Email(message = "邮箱格式不正确")
    private String email;
    private String birthder;
    private Integer level;
    private String uid;
    private Integer isok;
    private Integer memory;
    private Integer groupid;


    public User() {
        super();
    }

    public User(Integer id, String username, String password, String email, String birthder, Integer level, String uid, Integer isok, Integer memory, Integer groupid) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.birthder = birthder;
        this.level = level;
        this.uid = uid;
        this.isok = isok;
        this.memory = memory;
        this.groupid = groupid;
    }

}
