package com.su.service;

import com.su.pojo.Config;
import org.springframework.stereotype.Service;

@Service
public interface ConfigService {
    Config getSourceype();
    Integer setSourceype(Config config);
}
