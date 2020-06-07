package com.niuke.service;

import com.niuke.dao.FeedDao;
import com.niuke.model.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {
    @Autowired
    FeedDao feedDao;

    //拉模式
    public List<Feed> getUserFeeds(int maxId,List<Integer> userIds,int count){
        return feedDao.selectUserFeeds(maxId,userIds,count);
    }

    public boolean addFeed(Feed feed){
        feedDao.addFeed(feed);
        return feed.getId()>0;
    }

    //推模式下会将新鲜事推送给粉丝，即在redis的粉丝拥有的新鲜事表中添加新鲜事的id
    public Feed getById(int id){
        return feedDao.getFeedById(id);
    }
}
