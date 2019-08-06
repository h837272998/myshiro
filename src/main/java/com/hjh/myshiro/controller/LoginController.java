package com.hjh.myshiro.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.hjh.myshiro.entity.User;
import com.hjh.myshiro.utils.ResultBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @Description:
 * @Author: HJH
 * @Date: 2019-08-01 16:40
 */
@Slf4j
@Controller
public class LoginController {

    @PostMapping("/login")
    @ResponseBody
    public ResultBean login(@RequestParam("username") String username,
                            @RequestParam("pswd") String pswd,
                            @RequestParam(value = "rememberMe",defaultValue = "false") Boolean rememberMe,
                            @RequestParam("vcode") String vcode,
                            Model model, HttpServletRequest request){

        if (vcode==null||vcode==""){
            return new ResultBean("验证码不能为空...");
        }

        Subject subject = SecurityUtils.getSubject();
        System.out.println("验证sessionId"+subject.getSession().getId());
        LineCaptcha captcha = (LineCaptcha) subject.getSession().getAttribute("captcha");
        if (captcha==null){
            return new ResultBean("验证码过期...");
        }
        if (!captcha.verify(vcode)){
            return new ResultBean("验证码错误...");
        }

        UsernamePasswordToken token = new UsernamePasswordToken(username, pswd,rememberMe);
        try {
            subject.login(token);
            subject.getSession().setAttribute("username",subject.getPrincipal().toString());
            return new ResultBean<>();
        }catch (AuthenticationException e){
            String msg = null;
            if(e.getClass().getName().equals("org.apache.shiro.authc.IncorrectCredentialsException")){
                msg = "验证出错，检测输入。";
            }else{
                msg = e.getMessage();
            }
            return new ResultBean<>(msg);
        }
    }

    @GetMapping("/getCode")
    public void getCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/gif");
        //生成线型验证码
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(100, 50,4,8);
        //输出
        lineCaptcha.write(response.getOutputStream());
        //设置session
        HttpSession session = request.getSession();
        System.out.println("设置验证码session"+session.getId());
        session.setAttribute("captcha",lineCaptcha);
    }

}
