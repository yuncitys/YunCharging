package com.sharecharge.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sharecharge.biz.entity.NetworkDot;
import com.sharecharge.core.util.ResultUtil;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface NetworkDotService extends IService<NetworkDot> {
     ResultUtil list(Map map);

     List<NetworkDot> findNetWorkDotApp(Map map);

     List<Map> findDeviceByNetWorkId(Integer id);
}
