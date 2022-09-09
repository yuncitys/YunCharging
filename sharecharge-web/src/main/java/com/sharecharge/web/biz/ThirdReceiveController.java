package com.sharecharge.web.biz;

import com.sharecharge.biz.emq.EmqActionBean;
import com.sharecharge.biz.emq.EmqService;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.security.annotation.Anonymous;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ThirdReceiveController {
    @Autowired
    private EmqService emqService;

    @RequestMapping(value = "/third/receive/emq/action/")
    @Anonymous
    public ResultUtil receiveEmqEvent(@RequestBody EmqActionBean emqActionBean) {
        emqService.processEmqAction(emqActionBean);
        return ResultUtil.success();
    }


}
