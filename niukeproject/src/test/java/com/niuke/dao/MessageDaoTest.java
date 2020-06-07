package com.niuke.dao;

import com.niuke.NiukeApplication;
import com.niuke.model.Message;
import com.niuke.service.MessageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NiukeApplication.class)
public class MessageDaoTest {
    @Autowired
    MessageDao messageDao;
    @Autowired
    MessageService messageService;
    @Test
    public void messageDaoTest(){
//        Message message = new Message();
//        message.setContent("2");
//        message.setFrom_id(1);
//        message.setTo_id(2);
//        message.setConversation_id("1_2");
//        message.setCreated_date(new Date());
//        message.setHas_read(0);
//        System.out.println(messageService.addMessage(message));
//        System.out.println(messageService.getConversationList(1,0,10));
        System.out.println(messageDao.getConversationUnreadCount(10,"9_10"));
//        List<Message> conversationList = messageDao.getConversationList(1, 0, 10);
//        System.out.println(conversationList);
//        System.out.println(messageDao.addMessage(message));
    }
}
