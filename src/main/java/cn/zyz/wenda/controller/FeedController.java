package cn.zyz.wenda.controller;

import cn.zyz.wenda.model.EntityType;
import cn.zyz.wenda.model.Feed;
import cn.zyz.wenda.model.HostHolder;
import cn.zyz.wenda.service.FeedService;
import cn.zyz.wenda.service.FollowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class FeedController {
    private static final Logger logger = LoggerFactory.getLogger(FeedController.class);

    @Autowired
    private FeedService feedService;

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping("/pullFeeds")
    public String getPullFeeds(@RequestParam("userId") int userId,
                               @RequestParam(value = "offset", defaultValue = "0") int offset,
                               @RequestParam(value = "limit", defaultValue = "10") int limit, Model model) {
        try {
            if (hostHolder.get() != null) {
                List<Feed> feeds = feedService.getFeeds(followService.getFollowee(EntityType.USER.getName(), userId, 0, Integer.MAX_VALUE), offset, limit);
                model.addAttribute("feeds", feeds);
                return "feeds";
            }
        } catch (Exception e) {
            logger.error("获取pullFeeds失败：" + e.getMessage());
        }
        return "feeds";
    }

    @RequestMapping("/pushFeeds")
    public String getPushFeeds(@RequestParam("userId") int userId,
                               @RequestParam(value = "offset", defaultValue = "0") int offset,
                               @RequestParam(value = "limit", defaultValue = "10") int limit, Model model) {
        try {
            if (hostHolder.get() != null) {
                List<Feed> feeds = feedService.getPushFeeds(userId, offset, limit);
                model.addAttribute("feeds", feeds);
                return "feeds";
            }
        } catch (Exception e) {
            logger.error("获取pushFeeds失败：" + e.getMessage());
        }
        return "feeds";
    }
}
