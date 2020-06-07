package com.niuke.service;

import com.niuke.NiukeApplication;
import com.niuke.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NiukeApplication.class)
public class testUserService {

    @Autowired
    UserService userService;
    @Test
    public void testService(){
        User user = new User();
        user.setHead_url("1234567890");
        user.setPassword("我要的是什么呀");
        user.setName("二十二画生");
        user.setSalt("123456");
        userService.register("李达生","1234567890");
    }
}
