package cn.zyz.wenda.controller;

import cn.zyz.wenda.async.EventModel;
import cn.zyz.wenda.async.EventProducer;
import cn.zyz.wenda.async.EventType;
import cn.zyz.wenda.model.Comment;
import cn.zyz.wenda.model.EntityType;
import cn.zyz.wenda.model.HostHolder;
import cn.zyz.wenda.model.ViewObject;
import cn.zyz.wenda.service.*;
import cn.zyz.wenda.util.WendaUtil;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class FollowController {
    private static final Logger logger = LoggerFactory.getLogger(FollowController.class);

    @Autowired
    private FollowService followService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping("/followUser")
    @ResponseBody
    public String followUser(@RequestParam("userId") int userId) {
        if (hostHolder.get() == null) {
            return WendaUtil.getJSONString(999);
        }
        if (hostHolder.get().getId() == userId) {
            return WendaUtil.getJSONString(1, "无法关注自己");
        }
        try {
            if (followService.follow(EntityType.USER.getName(), userId, hostHolder.get().getId()) == 0) {
                return WendaUtil.getJSONString(1, "关注用户失败");
            }
            EventModel model = new EventModel(EventType.FOLLOW);
            model.setEntityOwnerId(userId)
                    .setActorId(hostHolder.get().getId())
                    .setEntityId(userId)
                    .setEntityType(EntityType.USER.getName());
            eventProducer.fireEvent(model);
            return WendaUtil.getJSONString(0, "关注成功");
        } catch (Exception e) {
            logger.error("关注用户失败：" + e.getMessage());
        }
        return WendaUtil.getJSONString(1, "关注用户失败");
    }

    @RequestMapping("/unfollowUser")
    @ResponseBody
    public String unFollowUser(@RequestParam("userId") int userId) {
        if (hostHolder.get() == null) {
            return WendaUtil.getJSONString(999);
        }
        if (hostHolder.get().getId() == userId) {
            return WendaUtil.getJSONString(1, "无法取消关注自己");
        }
        try {
            if (followService.unFollow(EntityType.USER.getName(), userId, hostHolder.get().getId()) == 0) {
                return WendaUtil.getJSONString(1, "取消关注用户失败");
            }
            EventModel model = new EventModel(EventType.UNFOLLOW);
            model.setEntityOwnerId(userId)
                    .setActorId(hostHolder.get().getId())
                    .setEntityId(userId)
                    .setEntityType(EntityType.USER.getName());
            eventProducer.fireEvent(model);
            return WendaUtil.getJSONString(0, "取消关注成功");
        } catch (Exception e) {
            logger.error("取消关注用户失败：" + e.getMessage());
        }
        return WendaUtil.getJSONString(1, "取消关注用户失败");
    }

    @RequestMapping("/followQuestion")
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId) {
        if (hostHolder.get() == null) {
            return WendaUtil.getJSONString(999);
        }
        try {
            if (followService.follow(EntityType.QUESTION.getName(), questionId, hostHolder.get().getId()) == 0) {
                return WendaUtil.getJSONString(1, "关注问题失败");
            }
            EventModel model = new EventModel(EventType.FOLLOW);
            model.setEntityOwnerId(questionService.getQusetionById(questionId).getUserId())
                    .setActorId(hostHolder.get().getId())
                    .setEntityId(questionId)
                    .setEntityType(EntityType.QUESTION.getName())
                    .setExt("questionId", questionId);
            eventProducer.fireEvent(model);
            JSONObject result = new JSONObject();
            result.put("code", 0);
            result.put("id", hostHolder.get().getId());
            result.put("name", hostHolder.get().getName());
            result.put("headUrl", hostHolder.get().getHeadUrl());
            result.put("count", followService.getFollowerCount(EntityType.QUESTION.getName(), questionId));
            return result.toJSONString();
        } catch (Exception e) {
            logger.error("关注问题失败：" + e.getMessage());
        }
        return WendaUtil.getJSONString(1, "关注问题失败");
    }

    @RequestMapping("/unfollowQuestion")
    @ResponseBody
    public String unFollowQuestion(@RequestParam("questionId") int questionId) {
        if (hostHolder.get() == null) {
            return WendaUtil.getJSONString(999);
        }
        try {
            if (followService.unFollow(EntityType.QUESTION.getName(), questionId, hostHolder.get().getId()) == 0) {
                return WendaUtil.getJSONString(1, "取消关注问题失败");
            }
            JSONObject result = new JSONObject();
            result.put("code", 0);
            result.put("id", hostHolder.get().getId());
            result.put("count", followService.getFollowerCount(EntityType.QUESTION.getName(), questionId));
            return result.toJSONString();
        } catch (Exception e) {
            logger.error("取消关注问题失败：" + e.getMessage());
        }
        return WendaUtil.getJSONString(1, "取消关注问题失败");
    }

    @RequestMapping("user/{userId}/followees")
    public String followees(@PathVariable("userId") int userId,
                            @RequestParam(value = "offset", defaultValue = "0") int offset,
                            @RequestParam(value = "limit", defaultValue = "10") int limit,
                            Model model) {
        List<ViewObject> vos = new ArrayList<>();
        List<Integer> list = followService.getFollowee(EntityType.USER.getName(), userId, offset, limit);
        for (int uid : list) {
            ViewObject vo = new ViewObject();
            vo.set("user", userService.getUserById(uid));
            vo.set("followeeCount", followService.getFolloweeCount(EntityType.USER.getName(), uid));
            vo.set("followerCount", followService.getFollowerCount(EntityType.USER.getName(), uid));
            vo.set("commentCount", commentService.getCommentsByUserId(uid).size());
            int likeCount = 0;
            for (Comment comment : commentService.getCommentsByUserId(uid)) {
                likeCount += likeService.getLikeCount(comment.getId(), EntityType.COMMENT.getName());
            }
            vo.set("likeCount", likeCount);
            vo.set("hasFollowed", followService.hasFollowed(EntityType.USER.getName(), uid, hostHolder.get().getId()));
            vos.add(vo);
        }
        model.addAttribute("curUser", userService.getUserById(userId));
        model.addAttribute("followeeCount", followService.getFolloweeCount(EntityType.USER.getName(), userId));
        model.addAttribute("vos", vos);
        return "followees";
    }

    @RequestMapping("user/{userId}/followers")
    public String followers(@PathVariable("userId") int userId,
                            @RequestParam(value = "offset", defaultValue = "0") int offset,
                            @RequestParam(value = "limit", defaultValue = "10") int limit,
                            Model model) {
        List<ViewObject> vos = new ArrayList<>();
        List<Integer> list = followService.getFollower(EntityType.USER.getName(), userId, offset, limit);
        for (int uid : list) {
            ViewObject vo = new ViewObject();
            vo.set("user", userService.getUserById(uid));
            vo.set("followeeCount", followService.getFolloweeCount(EntityType.USER.getName(), uid));
            vo.set("followerCount", followService.getFollowerCount(EntityType.USER.getName(), uid));
            vo.set("commentCount", commentService.getCommentsByUserId(uid).size());
            int likeCount = 0;
            for (Comment comment : commentService.getCommentsByUserId(uid)) {
                likeCount += likeService.getLikeCount(comment.getId(), EntityType.COMMENT.getName());
            }
            vo.set("likeCount", likeCount);
            vo.set("hasFollowed", followService.hasFollowed(EntityType.USER.getName(), uid, hostHolder.get().getId()));
            vos.add(vo);
        }
        model.addAttribute("curUser", userService.getUserById(userId));
        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.USER.getName(), userId));
        model.addAttribute("vos", vos);
        return "followers";
    }
}
