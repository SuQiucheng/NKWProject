package com.niuke.utils;

public class RedisKeyUtil {
    public static String SPLIT = ":";
    public static String BIZ_LIKE = "LIKE";
    public static String BIZ_DISLIKE = "DISLIKE";
    public static String BIZ_EVENTQUEUE = "EVENT_QUEUE";

    // 粉丝
    private static String BIZ_FOLLOWER = "FOLLOWER";
    // 关注对象
    private static String BIZ_FOLLOWEE = "FOLLOWEE";
    private static String BIZ_TIMELINE = "TIMELINE";

    public static String getLikeKey(int entityType,int entityId){
        return BIZ_LIKE+SPLIT+entityType+SPLIT+entityId;
    }
    public static String getDisLikeKey(int entityType,int entityId){
        return BIZ_DISLIKE+SPLIT+entityType+SPLIT+entityId;
    }
    //获得消息队列的key
    public static String getEventQueueKey(){
        return BIZ_EVENTQUEUE;
    }
    //获得粉丝的key
    public static String getFollowerKey(int entityType,int entityId){
        return BIZ_FOLLOWER+SPLIT+entityType+SPLIT+entityId;
    }
    //获得关注对象的key
    public static String getFolloweeKey(int userId,int entityType){
        return BIZ_FOLLOWEE+SPLIT+userId+SPLIT+entityType;
    }

    public static String getTimelineKey(int userId){
        return BIZ_TIMELINE+SPLIT+userId;
    }
}
