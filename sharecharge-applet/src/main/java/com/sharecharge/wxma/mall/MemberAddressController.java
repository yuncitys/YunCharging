package com.sharecharge.wxma.mall;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.mall.entity.MemberAddress;
import com.sharecharge.mall.service.MemberAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping("/app/address")
public class MemberAddressController {

    @Autowired
    private MemberAddressService memberAddressService;


    @GetMapping("/list")
    @ResponseBody
    public ResultUtil list(Integer userId) {
        if (userId == null) {
            return ResultUtil.error("请先登录");
        }
        List<MemberAddress> memberAddresses = memberAddressService.list(new QueryWrapper<MemberAddress>().lambda().eq(MemberAddress::getUserId, userId).eq(MemberAddress::getDeleted, false));
        return ResultUtil.success(memberAddresses);
    }

    @GetMapping("/detail")
    @ResponseBody
    public ResultUtil detail(Integer userId, @NotNull Integer id) {
        if (userId == null) {
            return ResultUtil.error("请先登录");
        }
        MemberAddress memberAddress = memberAddressService.getOne(new QueryWrapper<MemberAddress>().lambda().eq(MemberAddress::getId, id).eq(MemberAddress::getUserId, userId).eq(MemberAddress::getDeleted, false));
        if (memberAddress == null) {
            ResultUtil.error("参数值不对");
        }
        return ResultUtil.success(memberAddress);
    }

    @PostMapping
    @ResponseBody
    public ResultUtil save(@RequestBody @Valid MemberAddress memberAddress) {
        if (memberAddress.getUserId() == null) {
            return ResultUtil.error("请先登录");
        }

        if (memberAddress.getIsDefault()) {
            // 重置其他默认的地址
            List<MemberAddress> memberAddresses = memberAddressService.list(new QueryWrapper<MemberAddress>().lambda().eq(MemberAddress::getUserId, memberAddress.getUserId()));
            memberAddresses.forEach(memberAddress1 -> {
                memberAddress1.setIsDefault(false);
                memberAddressService.updateById(memberAddress1);
            });
        }
        if (memberAddress.getId() == null) {

            memberAddressService.save(memberAddress);

        } else {

            memberAddressService.updateById(memberAddress);
        }
        return ResultUtil.success();
    }

    @DeleteMapping
    @ResponseBody
    public ResultUtil delete(Integer userId, @NotBlank(message = "{required}") String ids) {
        if (userId == null) {
            return ResultUtil.error("请先登录");
        }

        List<String> list = Arrays.asList(ids);

        MemberAddress memberAddress = new MemberAddress();
        memberAddress.setDeleted(true);
        memberAddressService.update(memberAddress, new QueryWrapper<MemberAddress>().lambda().in(MemberAddress::getId, list));

        return ResultUtil.success();
    }


}
