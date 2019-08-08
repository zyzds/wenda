package cn.zyz.wenda.controller;

import cn.zyz.wenda.model.EntityType;
import cn.zyz.wenda.model.Question;
import cn.zyz.wenda.model.User;
import cn.zyz.wenda.model.ViewObject;
import cn.zyz.wenda.service.FollowService;
import cn.zyz.wenda.service.QuestionService;
import cn.zyz.wenda.service.SearchService;
import cn.zyz.wenda.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private SearchService searchService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private FollowService followService;

    @RequestMapping(value = "/search")
    public String search(@RequestParam("keyword") String keyword,
                         @RequestParam(value = "offset", defaultValue = "0") int offset,
                         @RequestParam(value = "limit", defaultValue = "10") int limit,
                         Model model) {
        try {
            List<Question> questions = searchService.queryQuestion(keyword, offset, limit, "<em>", "</em>");
            List<ViewObject> vos = new ArrayList<>();
            for (Question question : questions) {
                Question q = questionService.getQusetionById(question.getId());
                if (question.getTitle() != null) {
                    q.setTitle(question.getTitle());
                }
                if (question.getContent() != null) {
                    q.setContent(question.getContent());
                }
                User user = userService.getUserById(q.getUserId());
                ViewObject vo = new ViewObject();
                vo.set("question", q);
                vo.set("user", user);
                vo.set("followerCount", followService.getFollowerCount(EntityType.QUESTION.getName(), q.getId()));
                vos.add(vo);
            }
            model.addAttribute("vos", vos);
            model.addAttribute("keyword", keyword);
        } catch (Exception e) {
            logger.error("查询异常：" + e.getMessage());
        }
        return "result";
    }
}
