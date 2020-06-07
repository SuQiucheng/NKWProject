package com.niuke.async;

import com.alibaba.fastjson.JSONObject;
import com.niuke.utils.JedisAdapter;
import com.niuke.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {
    @Autowired
    JedisAdapter jedisAdapter;

    //将redis作为队列
    //producer需要将事件推入队列中
    public boolean fireEvent(EventModel eventModel){
        try {
            String json = JSONObject.toJSONString(eventModel);
            String key = RedisKeyUtil.getEventQueueKey();
            jedisAdapter.lpush(key,json);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}
