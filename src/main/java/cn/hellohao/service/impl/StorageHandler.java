package cn.hellohao.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 存储服务处理器
 *
 * @author su
 * @date 2019/10/19 14:31
 */
@Component
public class StorageHandler {

    @Autowired
    private LocalStorageServiceImpl localStorageService;

}
