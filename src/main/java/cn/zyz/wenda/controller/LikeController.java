package cn.zyz.wenda.controller;

import cn.zyz.wenda.async.EventModel;
import cn.zyz.wenda.async.EventProducer;
import cn.zyz.wenda.async.EventType;
import cn.zyz.wenda.model.EntityType;
import cn.zyz.wenda.model.HostHolder;
import cn.zyz.wenda.service.CommentService;
import cn.zyz.wenda.service.LikeService;
import cn.zyz.wenda.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {
    private static final Logger logger = LoggerFactory.getLogger(LikeController.class);

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping("/like")
    @ResponseBody
    public String like(@RequestParam("commentId") int commentId) {
        if (hostHolder.get() == null) {
            return WendaUtil.getJSONString(999);
        }
        try {
            if (commentService.getCommentById(commentId) == null) {
                return WendaUtil.getJSONString(1, "回复不存在");
            }
            likeService.like(hostHolder.get().getId(), commentId, EntityType.COMMENT.getName());
            long count = likeService.getLikeCount(commentId, EntityType.COMMENT.getName());

            EventModel model = new EventModel(EventType.LIKE);
            model.setActorId(hostHolder.get().getId())
                    .setEntityId(commentId)
                    .setEntityOwnerId(commentService.getCommentById(commentId).getUserId())
                    .setEntityType(EntityType.COMMENT.getName())
                    .setExt("questionId", commentService.getCommentById(commentId).getEntityId());
            eventProducer.fireEvent(model);
            return WendaUtil.getJSONString(0, String.valueOf(count));
        } catch (Exception e) {
            logger.error("赞同失败：" + e.getMessage());
            return WendaUtil.getJSONString(1, "失败");
        }
    }

    @RequestMapping("/dislike")
    @ResponseBody
    public String dislike(@RequestParam("commentId") int commentId) {
        if (hostHolder.get() == null) {
            return WendaUtil.getJSONString(999);
        }
        try {
            likeService.dislike(hostHolder.get().getId(), commentId, EntityType.COMMENT.getName());
            long count = likeService.getLikeCount(commentId, EntityType.COMMENT.getName());
            return WendaUtil.getJSONString(0, String.valueOf(count));
        } catch (Exception e) {
            logger.error("不赞同失败：" + e.getMessage());
            return WendaUtil.getJSONString(1, "失败");
        }
    }
}
