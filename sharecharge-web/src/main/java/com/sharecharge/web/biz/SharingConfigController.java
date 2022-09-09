package com.sharecharge.web.biz;

import com.alibaba.fastjson.JSONObject;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.system.entity.DbSharingConfig;
import com.sharecharge.system.service.DbSharingConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("sys/systemConfig")
@RequiredArgsConstructor
public class SharingConfigController {
    final DbSharingConfigService sharingConfigService;

    @RequestMapping("selectAllConfig")
    public ResultUtil selectAllConfig() {
        try {
            String MAX_CASH_WITHDRAW = sharingConfigService.getGlobalConfig(DbSharingConfig.MAX_CASH_WITHDRAW);
            String IS_PRESTORE = sharingConfigService.getGlobalConfig(DbSharingConfig.IS_PRESTORE);
            Map map = new HashMap();
            map.put("MAX_CASH_WITHDRAW", MAX_CASH_WITHDRAW);
            map.put("IS_PRESTORE", IS_PRESTORE);
            return ResultUtil.success(map);
        } catch (Exception e) {
            return ResultUtil.error("查询失败");
        }

    }


    @RequestMapping("saveConfig")
    public ResultUtil saveConfig(@RequestBody String jsonConfig) {
        try {
            Map jsonObject = (Map) JSONObject.parse(jsonConfig);
            Map map = new HashMap();
            map.put("sharingConfigs", jsonObject);
            sharingConfigService.insertByBatch(map);
            return ResultUtil.success("保存成功");
        } catch (Exception e) {
            return ResultUtil.error("保存失败");
        }
    }
}
