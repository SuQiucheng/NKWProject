package com.niuke.service;

import com.niuke.utils.JedisAdapter;
import com.niuke.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class FollowService {
    @Autowired
    JedisAdapter jedisAdapter;
    //用户关注了某个实体，可以关注问题，关注用户，关注评论等实体
    public boolean follow(int userId,int entityType,int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        Date date = new Date();
        //jedis事务处理方法
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        //粉丝+1,zadd增加key score value
        tx.zadd(followerKey,date.getTime(),String.valueOf(userId));
        //关注+1
        tx.zadd(followeeKey,date.getTime(),String.valueOf(entityId));

        List<Object> ret = jedisAdapter.exec(tx,jedis);
        return ret.size() == 2 && (Long) ret.get(0)>0 &&(Long)ret.get(1)>0;
    }
    //取消关注
    public boolean unfollow(int userId,int entityType,int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        //粉丝数减去1
        tx.zrem(followerKey,String.valueOf(userId));
        //关注者减去1
        tx.zrem(followeeKey,String.valueOf(userId));
        List<Object> ret = jedisAdapter.exec(tx, jedis);
        return ret.size()==2 && (Long)ret.get(0)>0 && (Long)ret.get(1)>0;
    }

    public List<Integer> getFollowers(int entityType,int entityId,int count){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        return getFollowers(entityType,entityId,0,count);
    }
    public List<Integer> getFollowers(int entityType,int entityId,int offset,int count){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        Set<String> set = jedisAdapter.zrevrange(followerKey, offset, count);
        return getIdsFromSet(set);
    }

    public List<Integer> getFollowees(int userId,int entityType,int count){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return getFollowees(userId,entityType,0,count);
    }
    public List<Integer> getFollowees(int userId,int entityType,int offset,int count){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey,offset,count));
    }
    //因为存在redis内的是字符串，可以通过这个函数转化为ing类型
    private List<Integer> getIdsFromSet(Set<String> idSet){
        List<Integer> ids = new ArrayList<>();
        for (String str : idSet) {
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }
    public long getFollowerCount(int entityType,int entityId){

        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        return jedisAdapter.zcard(followerKey);
    }
    public long getFolloweeCount(int userId,int entityType){

        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return jedisAdapter.zcard(followeeKey);
    }
    //判断用户是否关注了某个实体
    public boolean isFollower(int userId,int entityType,int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        return jedisAdapter.zscore(followerKey,String.valueOf(userId))!=null;
    }
}
