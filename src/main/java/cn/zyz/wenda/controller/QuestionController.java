package cn.zyz.wenda.controller;

import cn.zyz.wenda.async.EventModel;
import cn.zyz.wenda.async.EventProducer;
import cn.zyz.wenda.async.EventType;
import cn.zyz.wenda.model.*;
import cn.zyz.wenda.service.*;
import cn.zyz.wenda.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/question")
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @Autowired
    private FollowService followService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(Question question) {
        if (hostHolder.get() != null) {
            try {
                question.setUserId(hostHolder.get().getId());
                question.setCreateDate(new Timestamp(System.currentTimeMillis()));
                int questionId = questionService.addQuestion(question);

                EventModel model = new EventModel();
                model.setActorId(hostHolder.get().getId());
                model.setEntityType(EntityType.QUESTION.getName());
                model.setEntityId(questionId);
                model.setEventType(EventType.ADD_QUESTION);
                model.setEntityOwnerId(hostHolder.get().getId());
                eventProducer.fireEvent(model);

                return WendaUtil.getJSONString(0);
            } catch (Exception e) {
                logger.error("问题添加异常：" + e.getMessage());
                return WendaUtil.getJSONString(1, "失败");
            }
        }
        return WendaUtil.getJSONString(999);
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.GET})
    public String getQuestion(@PathVariable("id") int id, Model model) {
        Question question = questionService.getQusetionById(id);
        List<ViewObject> comments = new ArrayList<>();
        for (Comment comment : commentService.getComments(id, EntityType.QUESTION.getName(), 0, 10)) {
            ViewObject object = new ViewObject();
            object.set("comment", comment);
            object.set("user", userService.getUserById(comment.getUserId()));
            object.set("likeCount", likeService.getLikeCount(comment.getId(), EntityType.COMMENT.getName()));
            if (hostHolder.get() != null) {
                object.set("status", likeService.status(hostHolder.get().getId(), comment.getId(), EntityType.COMMENT.getName()));
            } else {
                object.set("status", 0);
            }
            comments.add(object);
        }
        if (hostHolder.get() != null) {
            model.addAttribute("hasFollowed",
                    followService.hasFollowed(EntityType.QUESTION.getName(), id, hostHolder.get().getId()) ? 1 : 0);
        }
        model.addAttribute("question", question);
        model.addAttribute("user", userService.getUserById(question.getUserId()));
        model.addAttribute("comments", comments);
        List<User> users = new ArrayList<>();
        for (int uid : followService.getFollower(EntityType.QUESTION.getName(), id, 0, 10)) {
            users.add(userService.getUserById(uid));
        }
        model.addAttribute("followers", users);
        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.QUESTION.getName(), id));
        return "detail";
    }
}
