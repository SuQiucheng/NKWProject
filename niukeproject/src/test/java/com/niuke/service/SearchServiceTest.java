package com.niuke.service;

import com.niuke.NiukeApplication;
import com.niuke.model.Question;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = NiukeApplication.class)
public class SearchServiceTest {
    @Autowired
    SearchService searchService;
    @Test
    public void searchServiceTest() throws Exception {
        List<Question> questionList = searchService.searchQuestion("上网", 0, 10, "<em>", "</em>");
        for (Question question:questionList){
            System.out.println(question);
        }

    }

}