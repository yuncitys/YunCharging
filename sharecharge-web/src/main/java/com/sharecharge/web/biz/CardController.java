package com.sharecharge.web.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sharecharge.biz.entity.Card;
import com.sharecharge.biz.service.CardService;
import com.sharecharge.core.constant.ExceptionConstant;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.core.util.poi.ExcelUtil;
import com.sharecharge.security.util.SecurityUtil;
import com.sharecharge.system.service.DbAdminUserService;
import com.sharecharge.system.service.DbParentOrSonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("sys/card")
@RequiredArgsConstructor
public class CardController {
    final CardService cardService;
    final DbAdminUserService adminUserService;
    final DbParentOrSonService parentOrSonService;

    /**
     * 查询充电卡列表
     *
     * @param page
     * @param limit
     * @param cardNo
     * @param createTimeStart
     * @param createTimeEnd
     * @return
     * @Param cardStatus
     */
    @RequestMapping("cardList")
    @PreAuthorize("@ps.hasPermission(':sys:card:cardList')")
    public ResultUtil findCardList(@RequestParam("page") Integer page,
                                   @RequestParam("limit") Integer limit,
                                   String cardNo, Integer cardStatus, String createTimeStart, String createTimeEnd) {

        Map map = new HashMap();
        map.put("curPageStarRow", (page - 1) * limit);
        map.put("limit", limit);
        map.put("cardNo", cardNo);
        map.put("cardStatus", cardStatus);
        map.put("createTimeStart", createTimeStart);
        map.put("createTimeEnd", createTimeEnd);
        map.put("ids",parentOrSonService.getSonByCurAdmin(SecurityUtil.getUserId()) );
        return cardService.findCardList(map);
    }

    /**
     * 删除充电卡
     */
    @RequestMapping("deleteCard")
    @PreAuthorize("@ps.hasPermission(':card:cardList:delete')")
    public ResultUtil deleteCardById(Integer id) {
        try {
//            Card card=new Card();
//            card.setId(id);
//            card.setIsDelete(1);
            ResultUtil resultUtil = new ResultUtil();
            resultUtil.setData(cardService.removeById(id));
            resultUtil.setMsg("删除充电卡信息成功");
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            return resultUtil;
        } catch (Exception e) {
            return ResultUtil.error("删除充电卡失败");
        }
    }


    /**
     * 充电卡电费充值
     *
     * @param realityPayMoney：实际充值金额
     * @param giveMoney：平台赠送金额
     * @param cardNo
     * @return
     */
    @RequestMapping("updateElectricity")
    @PreAuthorize("@ps.hasPermission(':card:cardList:addMoney')")
    public ResultUtil updateElectricity(Double realityPayMoney, Double giveMoney, String cardNo) {
        try {
            Card one = cardService.getOne(new QueryWrapper<Card>().lambda().eq(Card::getCardNo, cardNo));
            if (Objects.isNull(one)) {
                return ResultUtil.error("该卡号不存在");
            }
            if (one.getCardStatus() == 1) {
                return ResultUtil.error("电卡已挂失");
            }
            one.setRealityPayMoney(one.getRealityPayMoney()+realityPayMoney);
            one.setGiveMoney(one.getGiveMoney()+giveMoney);
            ResultUtil resultUtil = new ResultUtil();
            resultUtil.setData(cardService.updateById(one));
            resultUtil.setMsg("充值电费成功");
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            return resultUtil;
        } catch (Exception e) {
            return ResultUtil.error("充值电费失败");
        }
    }

    /**
     * 添加充电卡
     *
     * @return
     */
    @RequestMapping("addCard")
    @PreAuthorize("@ps.hasPermission(':card:cardList:oneAdd')")
    public ResultUtil addCard(Card card) {
        try {
            card.setAdminId(SecurityUtil.getUserId());
            Card one = cardService.getOne(new QueryWrapper<Card>().lambda().eq(Card::getCardNo, card.getCardNo()));
            if (!Objects.isNull(one)) {
                return ResultUtil.error("该卡已存在");
            }
            cardService.save(card);
            return ResultUtil.success("添加充电卡成功");
        } catch (Exception e) {
            return ResultUtil.error("系统异常");
        }
    }

    /**
     * 根据卡号编辑卡信息
     *
     * @param card
     * @return
     */
    @RequestMapping("updateCard")
    @PreAuthorize("@ps.hasPermission(':card:cardList:edit')")
    public ResultUtil updateCard(Card card) {
        try {
            cardService.updateById(card);
            return ResultUtil.error("修改信息成功");
        } catch (Exception e) {
            return ResultUtil.error("系统异常");
        }
    }


    /**
     * 挂失充电卡
     *
     * @param cardNo
     * @return
     */
    @RequestMapping("lossCard")
    @PreAuthorize("@ps.hasPermission(':card:cardList:loss')")
    public ResultUtil lossCard(String cardNo) {
        try {
            Card one = cardService.getOne(new QueryWrapper<Card>().lambda().eq(Card::getCardNo, cardNo));
            if (Objects.isNull(one)) {
                return ResultUtil.error("该卡号不存在");
            }
            if (one.getCardStatus() == 1) {
                return ResultUtil.error("电卡已挂失");
            }
            one.setCardStatus(1);
            ResultUtil resultUtil=new ResultUtil();
            resultUtil.setMsg("挂失成功");
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            resultUtil.setData(cardService.updateById(one));
            return resultUtil;
        } catch (Exception e) {
            return ResultUtil.error("系统错误");
        }
    }

    /**
     * 批量导入IC
     *
     * @param file
     * @return
     */
    @RequestMapping("uploadICExcel")
    @PreAuthorize("@ps.hasPermission(':card:cardList:allAdd')")
    public ResultUtil uploadICExcel(MultipartFile file) {
        ExcelUtil<Card> excelUtil=new ExcelUtil<>(Card.class);
        try {
            List<Card> cards = excelUtil.importExcel(file.getInputStream());
            boolean b = cardService.saveBatch(cards);
            return ResultUtil.success("导入成功");
        } catch (Exception e) {
            log.info("系统异常：{}",e.getMessage());
            return ResultUtil.error("系统异常");
        }
    }
}
