package com.su.quartz;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;


@EnableScheduling
@Component
public class FirstJob {

    public void task() {
        TempImgTask tempImgTask = new TempImgTask();
        tempImgTask.start();
    }
}