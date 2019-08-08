package cn.zyz.wenda.service;

import cn.zyz.wenda.dao.CommentDAO;
import cn.zyz.wenda.dao.QuestionDAO;
import cn.zyz.wenda.model.Comment;
import cn.zyz.wenda.model.Question;
import cn.zyz.wenda.util.SensitiveWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentDAO commentDAO;

    @Autowired
    private QuestionDAO questionDAO;

    public int addComment(Comment comment) {
        String content = comment.getContent();
        content = HtmlUtils.htmlEscape(content);
        content = SensitiveWord.replaceSensitiveWord(content);
        comment.setContent(content);

        Question question = questionDAO.selectQuestionById(comment.getEntityId());
        questionDAO.updateCommentCount(question.getCommentCount() + 1, question.getId());

        commentDAO.addComment(comment);
        return comment.getId();
    }

    public List<Comment> getComments(int entityId, String entityType,
                                     int offset, int limit) {
        return commentDAO.selectComment(entityId, entityType, offset, limit);
    }

    public int getCommentCount(int entityId, String entityType) {
        return commentDAO.getCommentCount(entityId, entityType);
    }

    public void deleteComment(int id) {
        commentDAO.deleteById(id);
    }

    public Comment getCommentById(int id) {
        return commentDAO.getCommentById(id);
    }

    public List<Comment> getCommentsByUserId(int userId) {
        return commentDAO.getCommentsByUserId(userId);
    }
}
