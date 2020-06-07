package com.niuke.dao;

import com.niuke.NiukeApplication;
import com.niuke.model.Question;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = NiukeApplication.class)
public class QuestionDaoTest {
    @Autowired
    QuestionDao questionDao;
    @Test
    public void testQuestionDao(){
//        System.out.println(questionDao);
//        Question question = new Question();
//        question.setComment_count(1);
//        question.setContent("Content");
//        question.setCreated_date(new Date());
//        question.setTitle("社会就是这样");
//        question.setUser_id(1);
//        System.out.println(questionDao.addQuestion(question));
//        Question question1 = questionDao.selectQuestionById(1000);
//        System.out.println(question1);
        List<Question> lists= questionDao.selectQuestions(0,5);
        for (Question question2:lists){
            System.out.println(question2);
        }

    }

}
