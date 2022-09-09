package com.sharecharge.web.biz;

import com.sharecharge.biz.entity.NetworkDot;
import com.sharecharge.biz.service.NetworkDotService;
import com.sharecharge.core.constant.ExceptionConstant;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.security.util.SecurityUtil;
import com.sharecharge.system.service.DbAdminUserService;
import com.sharecharge.system.service.DbParentOrSonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("sys/networkDot")
@RequiredArgsConstructor
public class NetworkDotController {
    final NetworkDotService networkDotService;
    final DbAdminUserService adminUserService;
    final DbParentOrSonService parentOrSonService;

    /**
     * 查询网点信息
     *
     * @param page
     * @param limit
     * @param networkAddress
     * @return
     */
    @RequestMapping("findNetworkDot")
    @PreAuthorize("@ps.hasPermission(':sys:networkDot:findNetworkDot')")
    public ResultUtil findNetworkDotInfo(@RequestParam("page") Integer page,//当前页
                                         @RequestParam("limit") Integer limit,//每页条数
                                         String networkAddress, Integer dealerId) {
        Map map=new HashMap();
        map.put("curPageStarRow", (page - 1) * limit);//当前页起始行
        map.put("limit", limit);
        map.put("dealerId", dealerId);
        map.put("networkAddress", networkAddress);
        map.put("ids", parentOrSonService.getSonByCurAdmin(SecurityUtil.getUserId()));
        return networkDotService.list(map);
    }

    /**
     * 添加网点
     *
     * @param networkDot
     * @return
     */
    @RequestMapping("addNetworkDot")
    @PreAuthorize("@ps.hasPermission(':netWorkDot:netWorkDotList:add')")
    public ResultUtil addNetworkDot(NetworkDot networkDot) {
        try {
            networkDotService.save(networkDot);
            return ResultUtil.success("添加成功");
        } catch (Exception e) {
            log.error("添加网点失败", e.getMessage());
            return ResultUtil.error("添加网点失败");
        }
    }

    /**
     * 更新网点信息
     *
     * @param networkDot
     * @return
     */
    @RequestMapping("updateNetworkDot")
    @PreAuthorize("@ps.hasPermission(':netWorkDot:netWorkDotList:edit')")
    public ResultUtil updateNetworkDot(NetworkDot networkDot) {
        try {
            networkDotService.updateById(networkDot);
            return ResultUtil.success("更新成功");
        } catch (Exception e) {
            log.error("更新网点数据失败", e.getMessage());
            return ResultUtil.error("更新网点数据失败");
        }

    }

    /**
     * 删除网点
     *
     * @param id
     * @return
     */
    @RequestMapping("deleteNetworkDot")
    @PreAuthorize("@ps.hasPermission(':netWorkDot:netWorkDotList:delete')")
    public ResultUtil deleteNetworkDot(Integer id) {
        try {
//            NetworkDot networkDot=new NetworkDot();
//            networkDot.setId(id);
//            networkDot.setIsDelete(1);
            networkDotService.removeById(id);
            return ResultUtil.success("删除成功");
        } catch (Exception e) {
            log.error("删除网点失败", e.getMessage());
            return ResultUtil.error("删除网点信息失败");
        }
    }
}
