package com.hjh.myshiro.service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: HJH
 * @Date: 2019-08-05 9:12
 */
public interface ShiroService {

    public Map loadFilterChainDefinitions();

    public void updatePerission();
}
