package com.su.utils;

import com.su.pojo.Imgreview;
import com.su.pojo.Key;
import com.su.pojo.User;
import com.su.service.ImgreviewService;

import java.util.Map;

public class JianHuangThread extends Thread {

    private Key key;
    private Map hashMap;
    private ImgreviewService imgreviewService;
    private User user;

    public JianHuangThread(ImgreviewService imgreviewService, Key key, User user, Map<String, Integer> hashMap) {
        this.key = key;
        this.user = user;
        this.imgreviewService = imgreviewService;
        this.hashMap = hashMap;
    }

    @Override
    public void run() {
        Imgreview imgreview = imgreviewService.selectByPrimaryKey(1);
        if (user != null) {
            ImageReview.imgJB(hashMap, key.getRequestAddress() + "/", key, imgreview);
        } else {
            ImageReview.imgJB(hashMap, key.getRequestAddress() + "/", key, imgreview);
        }
    }
}
