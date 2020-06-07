package com.niuke.controller;

import com.niuke.model.HostHolder;
import com.niuke.model.Message;
import com.niuke.model.User;
import com.niuke.model.ViewObject;
import com.niuke.service.MessageService;
import com.niuke.service.UserService;
import com.niuke.utils.ForumUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.util.resources.cldr.sg.CurrencyNames_sg;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    @Autowired
    HostHolder hostHolder;
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;

    public static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    @GetMapping(value = "/msg/list")
    public String getConversationList(Model model){
        if (hostHolder.getUser() == null){
            return "redirect:/relogin";
        }
        int localUserId = hostHolder.getUser().getId();
        List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
        ArrayList<ViewObject> conversations = new ArrayList<>();
        for (Message message:conversationList){
            ViewObject vo = new ViewObject();
            vo.set("message",message);
            int targetId = (message.getFrom_id() == localUserId ? message.getTo_id() : message.getFrom_id()); // 显示和我互动的用户
            vo.set("user",userService.getUser(targetId));
            vo.set("unread",messageService.getConversationUnreadCount(localUserId,message.getConversation_id()));
            conversations.add(vo);
        }
        model.addAttribute("conversations",conversations);
        return "letter";

    }




    @PostMapping(value = "/msg/addMessage")
    @ResponseBody
    public String addMessage(String toName,String content){
        try {
            if (hostHolder.getUser() == null){
                return ForumUtil.getJsonString(999,"未登录");
            }
            User toUser = userService.selectUserByName(toName);
            if (toUser == null){
                return ForumUtil.getJsonString(1,"用户不存在");
            }
            //发送消息
            int fromId = hostHolder.getUser().getId();
            int toId = toUser.getId();
            Message message = new Message();
            message.setCreated_date(new Date());
            message.setFrom_id(fromId);
            message.setTo_id(toId);
            message.setConversation_id(fromId<toId?String.format("%d_%d",fromId,toId):String.format("%d_%d",toId,fromId));
            message.setContent(content);

            messageService.addMessage(message);
            return ForumUtil.getJsonString(0);
        }
        catch (Exception e){
            logger.error("发送消息失败"+e.getMessage());
            return ForumUtil.getJsonString(1,"发送消息失败");
        }
    }
    @GetMapping(value = "/msg/detail")
    public String getConversationDetail(Model model,String conversationId){
        try {
            //获取消息列表
            List<Message> messageList = messageService.getConversationDetail(conversationId, 0, 10);
            //获取消息及相关用户
            List<ViewObject> messages = new ArrayList<>();
            for (Message message:messageList){
                ViewObject vo = new ViewObject();
                //读过消息后改变状态
                if (hostHolder.getUser().getId() == message.getTo_id()){
                    messageService.updateMessageReadStatus(conversationId,message.getTo_id(),1);
                }

                vo.set("message",message);
                vo.set("user",userService.getUser(message.getFrom_id()));
                messages.add(vo);
            }
            model.addAttribute("messages",messages);
        }
        catch (Exception e){
            logger.error("获取详情失败"+e.getMessage());
        }
        return "letterDetail";
    }
}
