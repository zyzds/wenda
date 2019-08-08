package cn.zyz.wenda.controller;

import cn.zyz.wenda.model.*;
import cn.zyz.wenda.service.MessageService;
import cn.zyz.wenda.service.UserService;
import cn.zyz.wenda.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/msg")
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(value = "/addMessage", method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,
                             @RequestParam("content") String content) {
        try {
            Message message = new Message();
            message.setContent(content);
            message.setCreateDate(new Timestamp(System.currentTimeMillis()));
            message.setFromId(hostHolder.get().getId());
            if (userService.getUserByName(toName) == null) {
                return WendaUtil.getJSONString(1, "收信人不存在");
            }
            message.setToId(userService.getUserByName(toName).getId());
            messageService.addMessage(message);
            return WendaUtil.getJSONString(0);
        } catch (Exception e) {
            logger.error("添加私信失败：" + e.getMessage());
            return WendaUtil.getJSONString(1, "失败");
        }
    }

    @RequestMapping(value = "/list", method = {RequestMethod.GET})
    public String toLetter(Model model,
                           @RequestParam(value = "offset", defaultValue = "0") int offset,
                           @RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<Conversation> conversations = messageService.getConversations(hostHolder.get().getId(), offset, limit);
        List<ViewObject> vos = new ArrayList<>();
        for (Conversation conversation : conversations) {
            ViewObject vo = new ViewObject();
            User targetUser;
            if (conversation.getLatestMessage().getFromId() == hostHolder.get().getId()) {
                targetUser = userService.getUserById(conversation.getLatestMessage().getToId());
            } else {
                targetUser = userService.getUserById(conversation.getLatestMessage().getFromId());
            }
            vo.set("targetUser", targetUser);
            vo.set("conversation", conversation);
            vo.set("unReadCount", messageService.getUnReadCount(hostHolder.get().getId(), conversation.getConversationId()));
            vos.add(vo);
        }
        model.addAttribute("vos", vos);
        return "letter";
    }

    @RequestMapping(value = "/{conversationId}", method = {RequestMethod.GET})
    public String toLetterDetail(Model model,
                                 @PathVariable("conversationId") String conversationId,
                                 @RequestParam(value = "offset", defaultValue = "0") int offset,
                                 @RequestParam(value = "limit", defaultValue = "10") int limit) {
        messageService.readMessage(hostHolder.get().getId(), conversationId);
        List<Message> messages = messageService.getMessages(conversationId, offset, limit);
        List<ViewObject> vos = new ArrayList<>();
        for (Message message : messages) {
            ViewObject vo = new ViewObject();
            User targetUser;
            if (message.getFromId() == hostHolder.get().getId()) {
                targetUser = userService.getUserById(message.getToId());
            } else {
                targetUser = userService.getUserById(message.getFromId());
            }
            vo.set("targetUser", targetUser);
            vo.set("message", message);
            vos.add(vo);
        }
        model.addAttribute("vos", vos);
        return "letterDetail";
    }
}
