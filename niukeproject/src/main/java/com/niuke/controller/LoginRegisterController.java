package com.niuke.controller;

import com.niuke.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


//首页的登录 注册功能
@Controller
public class LoginRegisterController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginRegisterController.class);

    @Autowired
    UserService userService;
    //注册
    @PostMapping("/reg")
    public String reg(Model model, String username, String password,
                      @RequestParam(value = "rememberme",defaultValue = "false")boolean rememberme,
                      @RequestParam(value = "next",required = false)String next,
                      HttpServletResponse response){
        try {
            Map<String, String> map = userService.register(username, password);
            if (map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/"); // 在同一应用服务器内共享cookie
                response.addCookie(cookie); // ticket下发到客户端（浏览器）存储
                // 当读取到的next字段不为空的话跳转
                if (!StringUtils.isEmpty(next)){
                    return "redirect:"+next;
                }
                return "redirect:/";

            }
            else {
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }

        }
        catch (Exception e){
            return "login";
        }

    }

    @GetMapping(path = {"/reglogin"})
    public String register(Model model,
                           @RequestParam(value = "next",required = false)String next){
        model.addAttribute("next",next);
        return "login";
    }

    /**
     * 登录
     * @param model
     * @param username
     * @param password
     * @param rememberme
     * @param response
     * @return
     */
    @PostMapping("/login")
    public String login(Model model,String username,String password,
                        @RequestParam(value = "rememberme",defaultValue = "false")boolean rememberme,
                        @RequestParam(value = "next",required = false)String next,
                        HttpServletResponse response){
        try {
            Map<String, Object> map = userService.login(username, password);
            if (map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");//?这个有什么用
                response.addCookie(cookie);
                if (!StringUtils.isEmpty(next)){
                    return "redirect:"+next;
                }
                return "redirect:/";
            }else{
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
        }catch (Exception e){
            return "login";
        }

    }
    @GetMapping("/logout")
    public String logout(@CookieValue("ticket")String ticket){
        userService.logout(ticket);
        return "redirect:/";
    }
}
