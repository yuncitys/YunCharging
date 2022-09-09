package com.sharecharge.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sharecharge.biz.entity.Card;
import com.sharecharge.core.util.ResultUtil;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface CardService extends IService<Card> {
     ResultUtil findCardList(Map map);
}
