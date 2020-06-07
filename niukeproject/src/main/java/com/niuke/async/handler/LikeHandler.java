package com.niuke.async.handler;

import com.niuke.async.EventHandler;
import com.niuke.async.EventModel;
import com.niuke.async.EventType;
import com.niuke.model.Message;
import com.niuke.model.User;
import com.niuke.service.MessageService;
import com.niuke.service.UserService;
import com.niuke.utils.ForumUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class LikeHandler implements EventHandler {
    @Autowired
    UserService userService;
    @Autowired
    MessageService messageService;
    @Override
    public void doHandle(EventModel model) {
        //来自系统管理员
        int fromId = ForumUtil.SYSTEMCONTROLLER_USERID;
        int toId = model.getEntityOwnerId();
        Message message = new Message();
        message.setFrom_id(fromId);
        message.setTo_id(toId);
        message.setCreated_date(new Date());
        message.setConversation_id(fromId < toId ? String.format("%d_%d", fromId, toId) : String.format("%d_%d", toId, fromId));
        User user = userService.getUser(model.getActorId());
        message.setContent("用户'" + user.getName() + "'赞了你的问题,http://127.0.0.1:8081/question/" + model.getExts("questionId"));
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
