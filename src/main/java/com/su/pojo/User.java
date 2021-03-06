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


    private Long id;
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
    /**
     * 用户状态: 1正常/2未通过邮箱激活/3冻结
     */
    private Integer status;
    private Integer memory;
    private Long groupid;

}
