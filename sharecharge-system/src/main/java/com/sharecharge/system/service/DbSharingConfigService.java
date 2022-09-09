package com.sharecharge.system.service;


import com.sharecharge.system.entity.DbSharingConfig;

import java.util.List;
import java.util.Map;

public interface DbSharingConfigService {

    String getGlobalConfig(String key);

    boolean createOrUpdateGlobalConfig(String key, String value);

    List<DbSharingConfig> selectAll();

    int insertByBatch(Map sharingConfigs);


}
