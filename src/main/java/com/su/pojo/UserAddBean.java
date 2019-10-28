package com.su.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author su
 * @date 2019/10/28 11:10
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UserAddBean {


    @Size(min = 3, max = 20, message = "用户名需要为 3 - 20 个字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @Email(message = "邮箱格式不正确")
    private String email;

}
