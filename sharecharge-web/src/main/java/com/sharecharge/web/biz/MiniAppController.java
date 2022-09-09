package com.sharecharge.web.biz;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MiniAppController {
    @RequestMapping("/MP_verify_U9SndySetmky1Swt.txt")
    public String minAppVerify() {
        //直接返回你下载的授权文件里的内容就好
        return "U9SndySetmky1Swt";
    }


}
