package cn.zyz.wenda.controller;

import cn.zyz.wenda.model.*;
import cn.zyz.wenda.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    private QuestionService questionService;
    private UserService userService;
    private CommentService commentService;
    private LikeService likeService;
    private FollowService followService;
    private HostHolder hostHolder;

    @Autowired
    public HomeController(QuestionService questionService, UserService userService, CommentService commentService, LikeService likeService, FollowService followService, HostHolder hostHolder) {
        this.questionService = questionService;
        this.userService = userService;
        this.commentService = commentService;
        this.likeService = likeService;
        this.followService = followService;
        this.hostHolder = hostHolder;
    }

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET})
    public String index(Model model,
                        @RequestParam(value = "offset", defaultValue = "0") int offset,
                        @RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<Question> questions = questionService.getLatestQuestinos(0, offset, limit);
        List<ViewObject> vos = getViewObjects(questions);
        model.addAttribute("vos", vos);
        return "index";
    }

    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET})
    public String userIndex(Model model,
                            @PathVariable(value = "userId") int userId,
                            @RequestParam(value = "offset", defaultValue = "0") int offset,
                            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<Question> questions = questionService.getLatestQuestinos(userId, offset, limit);
        List<ViewObject> vos = getViewObjects(questions);
        model.addAttribute("vos", vos);
        model.addAttribute("tuser", userService.getUserById(userId));
        model.addAttribute("followeeCount", followService.getFolloweeCount(EntityType.USER.getName(), userId));
        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.USER.getName(), userId));
        model.addAttribute("commentCount", commentService.getCommentsByUserId(userId).size());
        int likeCount = 0;
        for (Comment comment : commentService.getCommentsByUserId(userId)) {
            likeCount += likeService.getLikeCount(comment.getId(), EntityType.COMMENT.getName());
        }
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("hasFollowed", followService.hasFollowed(EntityType.USER.getName(), userId, hostHolder.get().getId()));
        return "profile";
    }

    private List<ViewObject> getViewObjects(List<Question> questions) {
        List<ViewObject> vos = new ArrayList<>();
        for (Question question : questions) {
            User user = userService.getUserById(question.getUserId());
            ViewObject vo = new ViewObject();
            vo.set("question", question);
            vo.set("user", user);
            vo.set("followerCount", followService.getFollowerCount(EntityType.QUESTION.getName(), question.getId()));
            vos.add(vo);
        }
        return vos;
    }
}
