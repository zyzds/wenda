package cn.zyz.wenda.service;

import cn.zyz.wenda.dao.QuestionDAO;
import cn.zyz.wenda.model.Question;
import cn.zyz.wenda.util.SensitiveWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private QuestionDAO questionDao;

    public List<Question> getLatestQuestinos(int userId, int offset, int limit) {
        return questionDao.selectLatestQuestions(userId, offset, limit);
    }

    public Question getQusetionById(int id) {
        return questionDao.selectQuestionById(id);
    }

    public int addQuestion(Question question) {
        String title = question.getTitle();
        String content = question.getContent();
        title = HtmlUtils.htmlEscape(title);
        content = HtmlUtils.htmlEscape(content);
        title = SensitiveWord.replaceSensitiveWord(title);
        content = SensitiveWord.replaceSensitiveWord(content);
        question.setTitle(title);
        question.setContent(content);

        questionDao.addQuestion(question);
        return question.getId();
    }
}
