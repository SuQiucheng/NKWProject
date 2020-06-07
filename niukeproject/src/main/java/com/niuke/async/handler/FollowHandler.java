package com.niuke.async.handler;

import com.niuke.async.EventHandler;
import com.niuke.async.EventModel;
import com.niuke.async.EventType;
import com.niuke.model.EntityType;
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
public class FollowHandler implements EventHandler {
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel model) {
        int fromId = ForumUtil.SYSTEMCONTROLLER_USERID;
        int toId = model.getEntityOwnerId();
        Message message = new Message();
        message.setFrom_id(fromId);
        message.setTo_id(toId);
        message.setCreated_date(new Date());
        message.setConversation_id(fromId<toId?String.format("%d_%d", fromId, toId):String.format("%d_%d", toId, fromId));
        User user = userService.getUser(model.getActorId());
        if (model.getEntityType() == EntityType.ENTITY_QUESTION){
            message.setContent("用户'" + user.getName() + "'关注了你的问题,http://127.0.0.1:8081/question/" + model.getEntityId());

        } else if (model.getEntityType() == EntityType.ENTITY_USER) {
            message.setContent("用户'" + user.getName() + "'关注了你,http://127.0.0.1:8081/user/" + model.getActorId());
        }
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
