package com.niuke.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.niuke.async.EventHandler;
import com.niuke.async.EventModel;
import com.niuke.async.EventType;
import com.niuke.model.EntityType;
import com.niuke.model.Feed;
import com.niuke.model.Question;
import com.niuke.model.User;
import com.niuke.service.FeedService;
import com.niuke.service.FollowService;
import com.niuke.service.QuestionService;
import com.niuke.service.UserService;
import com.niuke.utils.JedisAdapter;
import com.niuke.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.List;

@Component
public class FeedHandler implements EventHandler {
    @Autowired
    FollowService followService;
    @Autowired
    UserService userService;
    @Autowired
    FeedService feedService;
    @Autowired
    JedisAdapter jedisAdapter;
    @Autowired
    QuestionService questionService;
    public String buildFeedData(EventModel model){
        Map<String,String> map = new HashMap<>();
        User actor = userService.getUser(model.getActorId());
        if (actor==null){
            return null;
        }
        map.put("userId",String.valueOf(actor.getId()));
        map.put("userHead",actor.getHead_url());
        map.put("userName",actor.getName());
        if (model.getType()== EventType.COMMENT || (model.getType()==EventType.FOLLOW&&model.getEntityType()== EntityType.ENTITY_QUESTION)){
            Question question = questionService.selectQuestionById(model.getEntityId());
            if (question == null){
                return null;
            }
            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
    }
    @Override
    public void doHandle(EventModel model) {
        Feed feed = new Feed();
        feed.setCreated_date(new Date());
        feed.setType(model.getType().getValue());
        feed.setUser_id(model.getActorId());
        feed.setData(buildFeedData(model));
        if (feed.getData()==null){
            return;
        }
        feedService.addFeed(feed);
        //获得所有的粉丝
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER,model.getEntityId(),Integer.MAX_VALUE);
        //userId=0,表示这是一个未登录用户
        followers.add(0);
        for (Integer follower : followers) {
            String timelineKey = RedisKeyUtil.getTimelineKey(follower);
            jedisAdapter.lpush(timelineKey,String.valueOf(feed.getId()));
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(new EventType[]{EventType.COMMENT, EventType.FOLLOW});
    }
}
