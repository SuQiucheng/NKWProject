package com.niuke.dao;

import com.niuke.NiukeApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NiukeApplication.class)
public class LoginTicketDaoTest {
    @Autowired
    LoginTicketDao loginTicketDao;

    @Test
    public void testLoginTicket(){
        String ticket = "4fd724f8-87fe-43a7-8503-4d638d13558e";
        System.out.println(loginTicketDao.selectByTicket(ticket));
    }
}
