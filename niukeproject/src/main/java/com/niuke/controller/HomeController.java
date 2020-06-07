package com.niuke.controller;

import com.niuke.model.*;
import com.niuke.service.CommentService;
import com.niuke.service.FollowService;
import com.niuke.service.QuestionService;
import com.niuke.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    FollowService followService;

    @GetMapping(path = {"/","/index"})
    public String home(Model model){
        model.addAttribute("vos",getQuestion(0,10));
        return "index";
    }

    //得到最新的问题（不会根据用户id）
    private List<ViewObject> getQuestion(int offset,int limit){
//        List<Question> questionList = questionService.selectLatestQuestions(userId,offset,limit);
        List<Question> questionList = questionService.selectQuestions(offset,limit);
        List<ViewObject> vos = new ArrayList<>();
        for (Question question:questionList){
            ViewObject vo = new ViewObject();
            vo.set("question",question);
            //问题关注的数量
            vo.set("followCount",1);
            vo.set("user", userService.getUser(question.getUser_id()));
            vos.add(vo);
        }
        return vos;
    }
    //得到最新的问题（根据用户id）
    private List<ViewObject> getQuestion(int userId,int offset,int limit){
        List<Question> questionList = questionService.selectLatestQuestions(userId,offset,limit);
        List<ViewObject> vos = new ArrayList<>();
        for (Question question:questionList){
            ViewObject vo = new ViewObject();
            vo.set("question",question);
            //问题关注的数量
            vo.set("followCount",1);
            vo.set("user", userService.getUser(question.getUser_id()));
            vos.add(vo);
        }
        return vos;
    }
    //进入个人主页
    @GetMapping(value = "/user/{userId}")
    public String userIndex(Model model, @PathVariable("userId")int userId){
        model.addAttribute("vos",getQuestion(userId,0,10));
        User user = userService.getUser(userId);
        ViewObject vo = new ViewObject();
        vo.set("user",user);
        vo.set("commentCount", commentService.getUserCommentCount(userId));
        vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));

        if (hostHolder.getUser() != null) {
            vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), userId, EntityType.ENTITY_USER));
        } else {
            vo.set("followed", false);
        }
        model.addAttribute("profileUser", vo);
        model.addAttribute("vos", getQuestion(userId, 0, 10));
        return "profile";
    }
}
