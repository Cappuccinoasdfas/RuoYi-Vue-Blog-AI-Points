package com.ruoyi.blog_aiservices.controller;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

@RestController
@RequestMapping("/runAway")
public class RunAwayController {

    @PostMapping("/run")
    @Anonymous
    public AjaxResult run() {

        AjaxResult ajax = AjaxResult.success();
        try {
            //获取当前登录用户名
            String username = SecurityUtils.getUsername();
            if(!"小猫".equals( username)){
                return ajax.error("你配关我电脑吗");
            }
            // 执行Windows睡眠命令

            Runtime.getRuntime().exec("rundll32.exe powrprof.dll,SetSuspendState 0,1,0");
            return ajax.success("执行成功");
        } catch (IOException e) {
            e.printStackTrace();
            return ajax.error("执行失败");
        }
    }
}