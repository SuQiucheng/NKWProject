package com.niuke.controller;

import com.niuke.async.EventModel;
import com.niuke.async.EventProducer;
import com.niuke.async.EventType;
import com.niuke.model.Comment;
import com.niuke.model.EntityType;
import com.niuke.model.HostHolder;
import com.niuke.service.CommentService;
import com.niuke.service.LikeService;
import com.niuke.utils.ForumUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {
    @Autowired
    LikeService likeService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    EventProducer eventProducer;

    @PostMapping(value = "/like")
    @ResponseBody
    public String like(int commentId){
        if (hostHolder.getUser() == null){
            return ForumUtil.getJsonString(999);
        }
        Comment comment = commentService.getCommentById(commentId);

        //异步队列发送私信给被赞人
        eventProducer.fireEvent(new EventModel(EventType.LIKE)
                .setActorId(hostHolder.getUser().getId()).setEntityId(commentId)
                .setEntityType(EntityType.ENTITY_COMMENT)
                .setEntityOwnerId(comment.getUser_id())
                .setExts("questionId",String.valueOf(comment.getEntity_id())));
        //返回前端点赞数
        long likeCount = likeService.like(hostHolder.getUser().getId(),EntityType.ENTITY_COMMENT,commentId);
        return ForumUtil.getJsonString(0,String.valueOf(likeCount));
    }
    @PostMapping(value = "/dislike")
    @ResponseBody
    public String dislike(int commentId){
        if (hostHolder.getUser() == null){
            return ForumUtil.getJsonString(999);
        }
        Comment comment = commentService.getCommentById(commentId);
        long likeCount = likeService.dislike(hostHolder.getUser().getId(),EntityType.ENTITY_COMMENT,commentId);
        return ForumUtil.getJsonString(0,String.valueOf(likeCount));
    }


}
