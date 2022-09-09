package com.sharecharge.wxma.biz;

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
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("app/card")
@RequiredArgsConstructor
public class AppCardController {
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
    public ResultUtil findCardList(@RequestParam("adminId")Integer adminId,
                                   @RequestParam("page") Integer page,
                                   @RequestParam("limit") Integer limit,
                                   String cardNo, Integer cardStatus, String createTimeStart, String createTimeEnd) {

        Map map = new HashMap();
        map.put("curPageStarRow", (page - 1) * limit);
        map.put("limit", limit);
        map.put("cardNo", cardNo);
        map.put("cardStatus", cardStatus);
        map.put("createTimeStart", createTimeStart);
        map.put("createTimeEnd", createTimeEnd);
        map.put("ids",parentOrSonService.getSonByCurAdmin(adminId));
        return cardService.findCardList(map);
    }

    /**
     * 删除充电卡
     */
    @RequestMapping("deleteCard")
    public ResultUtil deleteCardById(Integer id) {
        try {
            Card card=new Card();
            card.setId(id);
            card.setIsDelete(1);
            ResultUtil resultUtil = new ResultUtil();
            resultUtil.setData(cardService.updateById(card));
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
    @ResponseBody
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
}
