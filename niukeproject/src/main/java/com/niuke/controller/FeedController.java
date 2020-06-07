package com.niuke.controller;

import com.niuke.model.EntityType;
import com.niuke.model.Feed;
import com.niuke.model.HostHolder;
import com.niuke.service.FeedService;
import com.niuke.service.FollowService;
import com.niuke.utils.JedisAdapter;
import com.niuke.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class FeedController {
    @Autowired
    FeedService feedService;
    @Autowired
    FollowService followService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    JedisAdapter jedisAdapter;

    //推模式,需要从自己在redis表中的新鲜事列表中取出
    @GetMapping(value = "/pushfeeds")
    private String getPushFeeds(Model model){
        int localUserId = hostHolder.getUser()!=null?hostHolder.getUser().getId():0;
        List<String> feedIds = jedisAdapter.lrang(RedisKeyUtil.getTimelineKey(localUserId),0,10);
        List<Feed> feeds = new ArrayList<>();
        for (String feedId : feedIds) {
            Feed feed = feedService.getById(Integer.parseInt(feedId));
            if (feed!=null){
                feeds.add(feed);
            }
        }
        model.addAttribute("feeds",feeds);
        return "feeds";
    }
    //拉模式
    @GetMapping(value = "/pullfeeds")
    private String getPullFeeds(Model model){
        int localUserId = hostHolder.getUser()!=null?hostHolder.getUser().getId():0;
        List<Integer> followees = new ArrayList<>();
        if (localUserId!=0){
            followees = followService.getFollowees(localUserId, EntityType.ENTITY_USER,Integer.MAX_VALUE);
        }
        List<Feed> feeds = feedService.getUserFeeds(Integer.MAX_VALUE,followees,10);
        model.addAttribute("feeds",feeds);
        return "feeds";
    }

}
