package com.su.service.impl;

import com.su.dao.NoticeMapper;
import com.su.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public String getNotice() {
        return noticeMapper.getNotice();
    }
}
