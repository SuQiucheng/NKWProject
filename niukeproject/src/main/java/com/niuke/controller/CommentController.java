package com.niuke.controller;

import com.niuke.async.EventModel;
import com.niuke.async.EventProducer;
import com.niuke.async.EventType;
import com.niuke.model.Comment;
import com.niuke.model.EntityType;
import com.niuke.model.HostHolder;
import com.niuke.service.CommentService;
import com.niuke.service.QuestionService;
import com.niuke.utils.ForumUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;

@Controller
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    QuestionService questionService;
    @Autowired
    EventProducer eventProducer;

    @PostMapping(value = {"/addComment"})
    public String addComment(int questionId,String content){
        try {
            //增加question的评论
            Comment comment = new Comment();
            comment.setContent(content);
            if (hostHolder.getUser()!=null){
                comment.setUser_id(hostHolder.getUser().getId());
            }
            else {
                //如果用户没有登陆，就使用默认用户
                comment.setUser_id(ForumUtil.ANONYMOUS_USERID);
            }
            comment.setCreated_date(new Date());
            comment.setEntity_id(questionId);
            comment.setEntity_type(EntityType.ENTITY_QUESTION);
            commentService.addComment(comment);
            //更新question的评论数
            //comment.getEntity_id()得到questionId
            int count = commentService.getCommentCount(comment.getEntity_id(),comment.getEntity_type());
            questionService.updateCommentCount(comment.getEntity_id(),count);
            //推送异步事件
            eventProducer.fireEvent(new EventModel(EventType.COMMENT).setActorId(comment.getUser_id())
                                    .setEntityId(questionId));
        }
        catch (Exception e){
            logger.error("增加评论失败"+e.getMessage());
        }
        return "redirect:/question/"+questionId;
    }
}
