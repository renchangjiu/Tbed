package cn.hellohao.controller;

import cn.hellohao.exception.StorageSourceInitException;
import cn.hellohao.utils.Print;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.net.SocketException;

/**
 * @author su
 * @date 2019/10/19 19:23
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ModelAndView test(StorageSourceInitException ssie) {
        ModelAndView modelAndView = new ModelAndView("index");
        Print.Normal("异常拦截成功：" + ssie.getMessage());
        modelAndView.addObject("error", ssie.getMessage());
        return modelAndView;
    }

    @ExceptionHandler
    public String test4(SocketException e) {
        ModelAndView modelAndView = new ModelAndView("/index");
        Print.Normal("存储源配置不正确，初始化失败" + e.getMessage());
        e.printStackTrace();
        modelAndView.addObject("error", e.getMessage());
        return e.getMessage();
    }

    @ExceptionHandler
    public String test3(Exception e) {
        ModelAndView modelAndView = new ModelAndView("/index");
        Print.Normal("系统内部错误：" + e.getMessage());
        e.printStackTrace();
        modelAndView.addObject("error", e.getMessage());
        return e.getMessage();
    }

}
