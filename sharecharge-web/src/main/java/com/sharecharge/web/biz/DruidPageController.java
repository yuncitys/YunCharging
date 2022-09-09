package com.sharecharge.web.biz;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class DruidPageController {
    @RequestMapping("/druid")
    public String druid(){
        return "redirect:/druid/index.html";
    }
}
