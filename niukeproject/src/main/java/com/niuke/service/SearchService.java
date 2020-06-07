package com.niuke.service;

import com.niuke.model.Question;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {
    public static final String QUESTION_TITLE_FIELD="question_title";
    public static final String QUESTION_CONTENT_FIELD="question_content";
    public static final String QUESTION_TITLE_CONTENT_FIELD="question_title_content";
    //在application.properties中配置
    @Autowired
    SolrClient solrClient;
    @Autowired
    QuestionService questionService;
    public List<Question> searchQuestion(String keyword,int offset,int count,
                                        String hlPre,String hlPos) throws Exception{
        List<Question> questionList = new ArrayList<>();
        SolrQuery query = new SolrQuery(keyword);
        //设置查询域
        query.set("df", QUESTION_TITLE_CONTENT_FIELD);
        query.setRows(count);
        query.setStart(offset);
        query.setHighlight(true);
        query.setHighlightSimplePre(hlPre);
        query.setHighlightSimplePost(hlPos);
        //定义高亮的字段
        query.set("hl.fl",QUESTION_TITLE_FIELD+','+QUESTION_CONTENT_FIELD);
        QueryResponse response = solrClient.query(query);
        for (Map.Entry<String,Map<String,List<String>>> entry:response.getHighlighting().entrySet()){
            Question question = new Question();
            question = questionService.selectQuestionById(Integer.parseInt(entry.getKey()));
            if (entry.getValue().containsKey(QUESTION_TITLE_FIELD)){
                List<String> titleList = entry.getValue().get(QUESTION_TITLE_FIELD);
                if (titleList.size()>0){
                    question.setTitle(titleList.get(0));
                }
            }
            if (entry.getValue().containsKey(QUESTION_CONTENT_FIELD)){
                List<String> contentList = entry.getValue().get(QUESTION_CONTENT_FIELD);
                if (contentList.size()>0){
                    question.setContent(contentList.get(0));
                }
            }
            questionList.add(question);
        }
        return questionList;
    }
    public boolean indexQuestion(int qid,String title,String content) throws IOException, SolrServerException {
        SolrInputDocument doc = new SolrInputDocument();
        doc.setField("id",qid);
        doc.setField(QUESTION_TITLE_FIELD,title);
        doc.setField(QUESTION_CONTENT_FIELD,content);
        UpdateResponse updateResponse = solrClient.add(doc, 1000);
        return updateResponse!=null && updateResponse.getStatus()==0;
    }
}
