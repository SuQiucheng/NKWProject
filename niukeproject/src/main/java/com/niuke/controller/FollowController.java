package com.niuke.controller;

import com.niuke.async.EventModel;
import com.niuke.async.EventProducer;
import com.niuke.async.EventType;
import com.niuke.model.*;
import com.niuke.service.CommentService;
import com.niuke.service.FollowService;
import com.niuke.service.QuestionService;
import com.niuke.service.UserService;
import com.niuke.utils.ForumUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FollowController {
    @Autowired
    HostHolder hostHolder;
    @Autowired
    FollowService followService;
    @Autowired
    CommentService commentService;
    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;
    @Autowired
    EventProducer eventProducer;
    //当前用户关注另一个用户
    @PostMapping(value = "/followUser")
    @ResponseBody
    public String followUser(int userId){
        if (hostHolder.getUser()==null){
            return ForumUtil.getJsonString(999);
        }
        if (userId == hostHolder.getUser().getId()){
            return ForumUtil.getJsonString(1);
        }
        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER,userId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER).setEntityOwnerId(userId));

        //返回关注的人数
        return ForumUtil.getJsonString(ret?0:1,String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(),EntityType.ENTITY_USER)));
    }
    @PostMapping(value = "/unfollowUser")
    @ResponseBody
    public String unfollowUser(int userId){
        if (hostHolder.getUser()==null){
            return ForumUtil.getJsonString(999);
        }
        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_USER,userId);
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER).setEntityOwnerId(userId));

        //返回关注的人数
        return ForumUtil.getJsonString(ret?0:1,String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(),EntityType.ENTITY_USER)));
    }
    @PostMapping(value = "/followQuestion")
    @ResponseBody
    public String followQuestion(int questionId){
        if (hostHolder.getUser()==null){
            return ForumUtil.getJsonString(999);
        }
        //判断问题是否存在
        Question q = questionService.selectQuestionById(questionId);
        if (q == null){
            return ForumUtil.getJsonString(1,"问题不存在");
        }

        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION,questionId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(q.getUser_id()));

        //发送用户本人信息用于页面展示
        Map<String,Object> info = new HashMap<>();
        info.put("headUrl",hostHolder.getUser().getHead_url());
        info.put("name",hostHolder.getUser().getName());
        info.put("id",hostHolder.getUser().getId());
        info.put("count",followService.getFollowerCount(EntityType.ENTITY_QUESTION,questionId));
        return ForumUtil.getJsonString(ret?0:1,info);
    }
    @PostMapping(value = "/unfollowQuestion")
    @ResponseBody
    public String unfollowQuestion(int questionId){
        if (hostHolder.getUser()==null){
            return ForumUtil.getJsonString(999);
        }
        //判断问题是否存在
        Question q = questionService.selectQuestionById(questionId);
        if (q == null){
            return ForumUtil.getJsonString(1,"问题不存在");
        }

        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION,questionId);
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(q.getUser_id()));

        //发送用户本人信息用于页面展示
        Map<String,Object> info = new HashMap<>();
        info.put("headUrl",hostHolder.getUser().getHead_url());
        info.put("name",hostHolder.getUser().getName());
        info.put("id",hostHolder.getUser().getId());
        info.put("count",followService.getFollowerCount(EntityType.ENTITY_QUESTION,questionId));
        return ForumUtil.getJsonString(ret?0:1,info);
    }
    @GetMapping(value = "/user/{uid}/followers")
    public String followers(Model model, @PathVariable("uid")int userId){
        List<Integer> followerIds = followService.getFollowers(EntityType.ENTITY_USER, userId, 0, 10);
        if (hostHolder.getUser()!=null){
            model.addAttribute("followers",getUserInfo(hostHolder.getUser().getId(),followerIds));
        }
        else {
            model.addAttribute("followers",getUserInfo(0,followerIds));
        }
        model.addAttribute("followerCount",followService.getFollowerCount(EntityType.ENTITY_USER,userId));
        model.addAttribute("curUser",userService.getUser(userId));
        return "followers";
    }
    @GetMapping(value = "/user/{uid}/followees")
    public String followees(Model model, @PathVariable("uid") int userId) {
        List<Integer> followeeIds = followService.getFollowees(userId, EntityType.ENTITY_USER, 0, 10);

        if (hostHolder.getUser() != null) {
            model.addAttribute("followees", getUserInfo(hostHolder.getUser().getId(), followeeIds));
        } else {
            model.addAttribute("followees", getUserInfo(0, followeeIds));
        }
        model.addAttribute("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followees";
    }

    private List<ViewObject> getUserInfo(int localUserId,List<Integer> userIds){
        List<ViewObject> userInfos = new ArrayList<>();
        for (Integer uid:userIds){
            User user = userService.getUser(uid);
            if (user == null){
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.set("user",user);
            vo.set("commentCount", commentService.getUserCommentCount(uid));
            vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, uid));
            vo.set("followeeCount", followService.getFolloweeCount(uid, EntityType.ENTITY_USER));
            if (localUserId != 0) {
                vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, uid));
            } else {
                vo.set("followed", false);
            }
            userInfos.add(vo);
        }
        return userInfos;
    }

}
