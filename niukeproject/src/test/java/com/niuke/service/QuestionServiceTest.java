package com.niuke.service;

import com.niuke.NiukeApplication;
import com.niuke.model.Question;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NiukeApplication.class)
public class QuestionServiceTest {
    @Autowired
    QuestionService questionService;

    @Test
    public void questionService(){
//        Question question = new Question();
//        question.setComment_count(1);
//        question.setContent("Content");
//        question.setCreated_date(new Date());
//        question.setTitle("12345678");
//        question.setUser_id(1);
//        System.out.println(questionService.addQuestion(question));
        System.out.println(questionService.selectLatestQuestions(1,0,5));
        System.out.println(questionService.selectQuestionById(1));
        questionService.updateCommentCount(6923,2);
    }
}
