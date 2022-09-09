package com.sharecharge.web.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sharecharge.biz.entity.Command;
import com.sharecharge.biz.service.CommandService;
import com.sharecharge.core.constant.ExceptionConstant;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.core.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("sys/command")
@RequiredArgsConstructor
public class CommandController {

    final CommandService commandService;

    /**
     * 添加指令表
     *
     * @param
     * @return
     */
    @RequestMapping("addCommand")
    @PreAuthorize("@ps.hasPermission(':sys:command:addCommand')")
    public ResultUtil addCommand(Command command) {
        return ResultUtil.success(commandService.save(command));
    }

    /**
     * 删除指令表
     *
     * @param
     * @return
     */
    @RequestMapping("deleteCommand")
    @PreAuthorize("@ps.hasPermission(':sys:command:deleteCommand')")
    public ResultUtil deleteCommand(Integer commandId) {
        try {
//            Command commandMy = new Command();
//            commandMy.setCommandId(commandId);
//            commandMy.setIsDelete(1);
            commandService.removeById(commandId);
            return ResultUtil.success("删除成功!");
        } catch (Exception e) {
            log.error("删除指令表错误: " + e.getMessage());
            return ResultUtil.error("删除失败!");
        }
    }

    /**
     * 更新指令表
     *
     * @param
     * @return
     */
    @RequestMapping("updateCommand")
    @PreAuthorize("@ps.hasPermission(':sys:command:updateCommand')")
    public ResultUtil updateCommand(Command command) {
        return ResultUtil.success(commandService.updateById(command));
    }

    /**
     * 查询指令表列表
     *
     * @param
     * @return
     */
    @RequestMapping("findCommandList")
    @PreAuthorize("@ps.hasPermission(':sys:command:findCommandList')")
    public ResultUtil findCommandList(@RequestParam("page") Integer page,
                                      @RequestParam("limit") Integer limit,
                                      Command command) {
        try {
            Page<Command> commandPage=new Page<>();
            commandPage.setCurrent(page);
            commandPage.setSize(limit);
            IPage<Command> page1 = commandService.page(commandPage,new QueryWrapper<Command>().lambda()
                    .eq(!StringUtils.isEmpty(command.getCommand()),Command::getCommand,command.getCommand())
                    .eq(!StringUtils.isEmpty(command.getCommandName()),Command::getCommandName,command.getCommandName())
            );
            ResultUtil resultUtil = new ResultUtil();
            resultUtil.setData(page1.getRecords());
            resultUtil.setCount(page1.getTotal());
            resultUtil.setMsg("查询成功!");
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            return resultUtil;
        } catch (Exception e) {
            log.error("查询指令表错误: " + e.getMessage());
            return ResultUtil.error("查询失败!");
        }
    }


    /**
     * 查询所有指令列表
     * 不分页
     *
     * @param
     * @return
     */
    @RequestMapping("findDeviceCommand")
    public ResultUtil findDeviceCommand() {
        try {
            ResultUtil resultUtil = new ResultUtil();
            resultUtil.setData(commandService.list());
            resultUtil.setCode(ExceptionConstant.SUCCESS_HTTPREUQEST);
            return resultUtil;
        } catch (Exception e) {
            log.error("查询指令表错误: " + e.getMessage());
            return ResultUtil.error("查询失败!");
        }
    }


}
