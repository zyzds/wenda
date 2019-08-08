package cn.zyz.wenda.controller;

import cn.zyz.wenda.async.EventModel;
import cn.zyz.wenda.async.EventProducer;
import cn.zyz.wenda.async.EventType;
import cn.zyz.wenda.model.Comment;
import cn.zyz.wenda.model.EntityType;
import cn.zyz.wenda.model.HostHolder;
import cn.zyz.wenda.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;

@Controller
@RequestMapping("/comment")
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content) {
        try {
            Comment comment = new Comment();
            comment.setUserId(hostHolder.get().getId());
            comment.setCreateDate(new Timestamp(System.currentTimeMillis()));
            comment.setEntityId(questionId);
            comment.setEntityType(EntityType.QUESTION.getName());
            comment.setContent(content);
            int commentId = commentService.addComment(comment);

            EventModel model = new EventModel();
            model.setActorId(hostHolder.get().getId());
            model.setEntityType(EntityType.COMMENT.getName());
            model.setEntityId(commentId);
            model.setEventType(EventType.COMMENT);
            model.setEntityOwnerId(hostHolder.get().getId());
            eventProducer.fireEvent(model);
        } catch (Exception e) {
            logger.error("回复添加异常：" + e.getMessage());
        }
        return "redirect:/question/" + questionId;
    }
}
