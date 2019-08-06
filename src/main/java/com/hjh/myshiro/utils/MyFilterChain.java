package com.hjh.myshiro.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

/**
 * @Description:
 * @Author: HJH
 * @Date: 2019-08-05 10:20
 */
@Component
@ConfigurationProperties(prefix = "com.hjh")
@PropertySource("classpath:shiro-filter-chain.properties")
public class MyFilterChain {

    public LinkedHashMap<String, String> filterchain;

    public LinkedHashMap<String, String> getFilterchain() {
        return filterchain;
    }

    public void setFilterchain(LinkedHashMap<String, String> filterchain) {
        this.filterchain = filterchain;
    }
}
