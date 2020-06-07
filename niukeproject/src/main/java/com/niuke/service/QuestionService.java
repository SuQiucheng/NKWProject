package com.niuke.service;

import com.niuke.dao.QuestionDao;
import com.niuke.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class QuestionService
{
    @Autowired
    QuestionDao questionDao;
    @Autowired
    SensitiveService sensitiveService;
    public List<Question> selectLatestQuestions(int userId,int offset,int limit){
       return questionDao.selectLatestQuestions(userId,offset,limit);
    }

    public List<Question> selectQuestions(int offset,int limit){
        return questionDao.selectQuestions(0,10);
    }

    /**
     * 添加问题
     * @param question
     * @return
     */
    public int addQuestion(Question question){
        //内容过滤，防止用户输入的内容破坏网页结构
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));
        question.setTitle(sensitiveService.filter(question.getTitle()));
        questionDao.addQuestion(question);
        List<Question> questionList = selectLatestQuestions(question.getUser_id(), 0, 1);
        //添加问题成功则返回问题的Id，否则返回0.
        return questionList.get(0).getId();
    }

    public Question selectQuestionById(int id) {
        return questionDao.selectQuestionById(id);
    }

    /**
     * 更新评论的数量
     * @param id
     * @param comment_count
     * @return
     */
    public int updateCommentCount(int id,int comment_count){
        return questionDao.updateCommentCount(id,comment_count);
    }

    }
