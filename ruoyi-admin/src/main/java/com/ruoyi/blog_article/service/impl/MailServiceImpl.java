package com.ruoyi.blog_article.service.impl;
import com.ruoyi.blog_article.domain.mail.MailBan;
import com.ruoyi.blog_article.domain.mail.MailContext;
import com.ruoyi.blog_article.mapper.EmailMapper;
import com.ruoyi.blog_article.service.IMailServiceService;
import com.ruoyi.blog_article.domain.mail.MailVerifyCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 *专门的类，用于存储邮件模板的属性
 * @appName 是应用名称，默认为"咖啡馆"
 * @codeExpireMinutes 是验证码有效期（分钟数），默认为2分钟
 */
@Data
@Component
@ConfigurationProperties(prefix = "mail.template")
 class MailTemplateProperties{
    private String appName = "咖啡馆";
    private int codeExpireMinutes = 2;
}

/**
 * 邮件服务实现类
 * 提供邮件发送功能，包括普通邮件和验证码邮件的发送
 *
 * @author ruoyi
 * @date 2026-04-13
 */

@Slf4j  // Lombok注解：自动生成日志记录器
@Service // Spring注解：标识这是一个服务层Bean，会被组件扫描自动注册
@RequiredArgsConstructor // Lombok注解：为final字段自动生成构造函数，实现依赖注入
public class MailServiceImpl implements IMailServiceService {

    /**
     * JavaMail发送器，用于执行实际的邮件发送操作
     * 由Spring自动注入
     */
    private final JavaMailSender mailSender;

    /**
     * Thymeleaf模板引擎，用于渲染HTML邮件模板
     * 由Spring自动注入
     */
    private final TemplateEngine templateEngine;

    /**
     * Verification实体类，用于构建对象
     * 由Spring自动注入
     */
    private final MailTemplateProperties mailTemplateProperties;
    /**
     * 发件人邮箱地址
     * 从配置文件中的 spring.mail.username 属性读取
     */
    @Value("${spring.mail.username}")
    private String from;
    /**
     * Redis方法
     * redis属性继承，用来缓存邮件验证码
     */
    private final RedisTemplate redisTemplate;
    /**
     * 查询方法
     * 用来查询用户邮箱是否存在
     */
    private final EmailMapper emailMapper;
    /**
     * 发送邮件（通用方法）
     *
     * @param context 邮件上下文对象，包含收件人、主题、模板名称和模板变量等信息
     * @throws RuntimeException 当邮件发送失败时抛出运行时异常
     */
    @Override
    public void send(MailContext context) {
        try {
            // 创建MIME邮件消息对象
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            // 创建MIME消息助手，支持附件（true）并设置编码为UTF-8
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // 设置发件人
            helper.setFrom(from);
            // 设置收件人
            helper.setTo(context.getTo());
            // 设置邮件主题
            helper.setSubject(context.getSubject());

            // 使用Thymeleaf模板引擎渲染HTML内容
            // 模板路径为: mail/ + 模板名称 (例如: mail/verification-code.html)
            String htmlContent = templateEngine.process(
                    "mail/" + context.getTemplateName(),
                    buildContext(context.getVariables())
            );
            // 设置邮件正文为HTML格式（第二个参数true表示启用HTML）
            helper.setText(htmlContent, true);
            // 执行邮件发送
            mailSender.send(mimeMessage);

            // 记录成功日志
            log.info("邮件发送成功: to={}, subject={}", context.getTo(), context.getSubject());

        } catch (MessagingException e) {
            // 邮件发送失败，记录错误日志并抛出运行时异常
            log.error("邮件发送失败: to={}", context.getTo(), e);
            throw new RuntimeException("邮件发送失败", e);
        }
    }

    /**
     * 发送验证码邮件
     * 专门用于发送用户注册、找回密码等场景的验证码邮件
     *
     * @param to       收件人邮箱地址
     * @param username 收件人用户名（用于邮件中的个性化问候）
     */
    // 异步执行
    @Async
    @Override
    public void sendVerificationCode(String to, String username) {
        //判断当前用户是否注册拦截

        log.info("=== sendVerificationCode 开始执行，线程: {} ===", Thread.currentThread().getName());
        String code = String.format("%04d", new Random().nextInt(1000000));
        String key = "code="+ username;
        // 缓存验证码
        redisTemplate.opsForValue().set(key,code,2, TimeUnit.MINUTES);

        MailVerifyCode variables = MailVerifyCode.of(
                code,
                username,
                mailTemplateProperties.getAppName(),
                mailTemplateProperties.getCodeExpireMinutes());

        // 构建邮件上下文
        MailContext context = MailContext.builder()
                .to(to)                                          // 设置收件人
                .subject(variables.getSubject())                // 设置邮件主题
                .templateName("verification-code")              // 设置模板名称为 verification-code
                .variables(variables)                           // 设置模板变量
                .build();

        // 调用通用发送方法发送邮件
        send(context);
    }
    /**
     * 发送违规提示
     * 专门用于告诉用户违规提示和封禁时间
     *
     * @param to       收件人邮箱地址
     * @param username 收件人用户名（用于邮件中的个性化问候）
     * @param description 违规描述
     */
    @Async // 异步执行
    @Override
    public void sendBlocked(String to,String description, String username) {
        log.info("=== sendVerificationCode 开始执行，线程: {} ===", Thread.currentThread().getName());
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 封禁7天（举例）
        LocalDateTime unblockTime = now.plusDays(7);
        // 构建封禁信息对象
        MailBan blocked = MailBan.of(
                username,                           // 用户名
                mailTemplateProperties.getAppName(), // 应用名称
                description,                        // 违规描述
                now,                                // 违规时间
                unblockTime                         // 解封时间
        );

        // 构建邮件上下文
        MailContext context = MailContext.builder()
                .to(to)
                .subject(blocked.getSubject())  // 需要在 Blocked 类中添加 getSubject 方法
                .templateName("blocked")         // 封禁邮件模板
                .variables(blocked)              // 模板变量
                .build();

        // 发送邮件
        send(context);
    }
    /**
     * 查询用户邮箱
     * 查询用户是否注册过这个邮箱号，如果有值返回false，没有值返回true
     * 控制器先进过这里在决定要不要生成验证码
     * @param to 邮箱地址
     * @return 返回false表示用户已注册过这个邮箱，true表示用户没有注册过这个邮箱
     */
    @Override
    public Boolean getEmail(String to) {
        log.info("getEmail 被调用, to={}, 时间={}", to, System.currentTimeMillis());
        if(emailMapper.countByEmail(to) > 0){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 构建Thymeleaf模板上下文
     * 将变量对象转换为Thymeleaf可以识别的Context对象
     *
     * @param variables 模板变量对象（可以是普通Object或VerificationCodeVariables）
     * @return Thymeleaf上下文对象，用于模板渲染
     */
    private Context buildContext(Object variables) {
        Context context = new Context();
        //验证码处理
        if (variables instanceof MailVerifyCode v) {
            v.toMap().forEach(context::setVariable);
            context.setVariable("variables", v);
        }
        // 对违禁处理
        else if (variables instanceof MailBan b) {
            b.toMap().forEach(context::setVariable);
            context.setVariable("variables", b);
        }
//        else {
//            // 普通对象处理
//            context.setVariable("variables", variables);
//        }

        return context;
    }


}