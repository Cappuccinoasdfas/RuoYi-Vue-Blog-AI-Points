package com.ruoyi.blog_article.controller;

import com.ruoyi.blog_article.domain.mail.SendCodeRequest;
import com.ruoyi.blog_article.service.IMailServiceService;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class IMailController {

    private final IMailServiceService mailService;
    //  验证码发送接口
    @Anonymous
    @PostMapping("/send-code")
    public AjaxResult sendCode(@Validated @RequestBody SendCodeRequest request) {
        if(mailService.getEmail(request.getEmail())){
            mailService.sendVerificationCode(request.getEmail(), request.getUsername());
            return AjaxResult.success("验证码已发送");
        }else{
            return AjaxResult.error("邮箱已存在");
        }
    }

    /**
     * 测试发送封禁邮件
     *
     */
    @Anonymous
    @PostMapping("/send-blocked")
    public AjaxResult testSendBlocked(@Validated @RequestBody SendCodeRequest request) {
        try {
            mailService.sendBlocked(request.getEmail(), "发送违规内容", request.getUsername());
            return AjaxResult.success("封禁邮件已发送，请检查邮箱");
        } catch (Exception e) {
            return AjaxResult.error("发送失败：" + e.getMessage());
        }
    }

}
