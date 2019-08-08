package cn.zyz.wenda.service;

import cn.zyz.wenda.dao.MessageDAO;
import cn.zyz.wenda.model.Conversation;
import cn.zyz.wenda.model.Message;
import cn.zyz.wenda.util.SensitiveWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageDAO messageDAO;

    public void addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(SensitiveWord.replaceSensitiveWord(message.getContent()));
        messageDAO.addMessage(message);
    }

    public List<Message> getMessages(String conversationId, int offset, int limit) {
        return messageDAO.selectMessages(conversationId, offset, limit);
    }

    public List<Conversation> getConversations(int userId, int offset, int limit) {
        return messageDAO.getConversationList(userId, offset, limit);
    }

    public int getUnReadCount(int userId, String conversationId) {
        return messageDAO.getUnReadCount(userId, conversationId);
    }

    public void readMessage(int userId, String conversationId) {
        messageDAO.updateHasRead(userId, conversationId);
    }
}
