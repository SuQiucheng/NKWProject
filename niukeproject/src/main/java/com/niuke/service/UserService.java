package com.niuke.service;

import com.niuke.dao.LoginTicketDao;
import com.niuke.dao.UserDao;
import com.niuke.model.LoginTicket;
import com.niuke.model.User;
import com.niuke.utils.ForumUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.*;

@Service
public class UserService {

    @Autowired
    UserDao userDao;
    @Autowired
    LoginTicketDao loginTicketDao;

    public Map<String,String> register(String userName,String password){
        Map<String,String> map = new HashMap<>();
        //后台的判断
        if (StringUtils.isEmpty(userName)){
            map.put("msg","用户名不能为空");
            return map;
        }
        if (StringUtils.isEmpty(password)){
            map.put("msg","密码不能为空");
        }
        User user = userDao.selectByName(userName);
        if (user != null){
            map.put("msg","用户名已被注册");
            return map;
        }
        user = new User();
        user.setName(userName);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setPassword(ForumUtil.MD5(password+user.getSalt()));
        user.setHead_url(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        userDao.addUser(user);
        user = userDao.selectByName(userName);
        //注册完成下发ticket之后自动登录
        map.put("ticket",addLoginTicket(user.getId()));
        return map;
    }
    public User getUser(int id){
        return userDao.selectById(id);
    }

    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    public Map<String,Object> login(String username,String password){
        Map<String,Object> map = new HashMap<>();
        if(org.springframework.util.StringUtils.isEmpty(username)){
            map.put("msg","用户名不能为空");
            return map;
        }
        if(org.springframework.util.StringUtils.isEmpty(password)){
            map.put("msg","密码不能为空");
            return map;
        }
        User user = userDao.selectByName(username);
        if (user == null){
            map.put("msg","用户名不存在");
            return map;
        }
        if (!ForumUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msg","密码错误");
            return map;
        }
        //登陆成功之后，给客户端的响应中应加入ticket
        map.put("ticket",addLoginTicket(user.getId()));
        return map;
    }
    public String addLoginTicket(int user_id){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user_id);
        Date nowDate = new Date();
        nowDate.setTime(60*60*1000 + nowDate.getTime());
        loginTicket.setExpired(nowDate);
        loginTicket.setStatus(0);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("_",""));
        loginTicketDao.addTicket(loginTicket);
        return loginTicket.getTicket();
    }
    public void logout(String ticket){
        loginTicketDao.updateStatus(ticket,1);
    }
    public User selectUserByName(String name){
        return userDao.selectByName(name);
    }
}
