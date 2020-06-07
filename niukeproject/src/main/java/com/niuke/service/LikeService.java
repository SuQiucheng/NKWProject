package com.niuke.service;

import com.niuke.utils.JedisAdapter;
import com.niuke.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    JedisAdapter jedisAdapter;

    //判断问题当前喜欢的人数
    public long getLikeCount(int entityType,int entityId){
        String likeKey = RedisKeyUtil.getLikeKey(entityType,entityId);
        return jedisAdapter.scard(likeKey);
    }
    //判断当前用户喜欢或者不喜欢的状态
    //1 表示喜欢
    //0 表示不喜欢也不讨厌
    //-1 表示讨厌
    public int getLikeStatus(int userId,int entityType,int entityId){
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        if (jedisAdapter.sismember(likeKey,String.valueOf(userId))){
           return 1;
        }
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType,entityId);
        if (jedisAdapter.sismember(disLikeKey,String.valueOf(userId))){
            return -1;
        }
        return 0;
    }
    //添加喜欢
    public long like(int userId,int entityType,int entityId){
        String likeKey = RedisKeyUtil.getLikeKey(entityType,entityId);
        jedisAdapter.sadd(likeKey, String.valueOf(userId));
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType,entityId);
        if (jedisAdapter.sismember(disLikeKey,String.valueOf(userId))){
            jedisAdapter.srem(disLikeKey,String.valueOf(userId));
        }
        return jedisAdapter.scard(likeKey);
    }
    //添加不喜欢
    public long dislike(int userId,int entityType,int entityId){
        String dislikeKey = RedisKeyUtil.getDisLikeKey(entityType,entityId);
        jedisAdapter.sadd(dislikeKey, String.valueOf(userId));
        String likeKey = RedisKeyUtil.getLikeKey(entityType,entityId);
        if (jedisAdapter.sismember(likeKey,String.valueOf(userId))){
            jedisAdapter.srem(likeKey,String.valueOf(userId));
        }
        return jedisAdapter.scard(likeKey);
    }
}
