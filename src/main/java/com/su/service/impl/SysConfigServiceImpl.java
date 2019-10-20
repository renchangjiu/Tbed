package com.su.service.impl;

import com.su.dao.SysConfigMapper;
import com.su.pojo.SysConfig;
import com.su.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Hellohao
 * @version 1.0
 * @date 2019/8/15 13:48
 */
@Service
public class SysConfigServiceImpl implements SysConfigService {
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Override
    public SysConfig getstate() {
        return sysConfigMapper.getstate();
    }

    @Override
    public Integer setstate(SysConfig sysConfig) {
        return sysConfigMapper.setstate(sysConfig);
    }
}
