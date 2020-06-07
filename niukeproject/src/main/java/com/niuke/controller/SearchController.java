package com.niuke.controller;

import com.niuke.async.EventProducer;
import com.niuke.model.EntityType;
import com.niuke.model.Question;
import com.niuke.model.ViewObject;
import com.niuke.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {
    public static final Logger logger = LoggerFactory.getLogger(SearchController.class);
    @Autowired
    SearchService searchService;
    @Autowired
    FollowService followService;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    QuestionService questionService;
    @Autowired
    EventProducer eventProducer;
    @RequestMapping(path = {"/search"},method = {RequestMethod.GET})
    public String search(Model model,
                         @RequestParam("q")String keyword,
                         @RequestParam(value = "offset",defaultValue = "0")String offset,
                         @RequestParam(value = "count",defaultValue = "10")String count){
        try {
            List<Question> questionList = searchService.searchQuestion(keyword, Integer.parseInt(offset), Integer.parseInt(count), "<em>", "</em>");
            List<ViewObject> viewObjects = new ArrayList<>();
            for (Question question:questionList){
                ViewObject vo = new ViewObject();
                vo.set("question",question);
                vo.set("followCount",followService.getFolloweeCount(question.getUser_id(), EntityType.ENTITY_QUESTION));
                vo.set("user",userService.getUser(question.getUser_id()));
                viewObjects.add(vo);
            }
            model.addAttribute("vos",viewObjects);
            model.addAttribute("keyword",keyword);
        } catch (Exception e) {
            logger.error("查询错误"+e.getMessage());
        }
        return "result";
    }
}
