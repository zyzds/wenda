package cn.zyz.wenda.dao;

import cn.zyz.wenda.model.Question;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QuestionDAOTest {
    @Autowired
    private QuestionDAO questionDao;

    @Test
    public void test() {
        Question question = new Question();
        question.setTitle("title");
        question.setContent("content");
        question.setUserId(1);
        question.setCreateDate(new Timestamp(System.currentTimeMillis()));
        questionDao.addQuestion(question);

        List<Question> questions = questionDao.selectLatestQuestions(0, 0, 3);
        for (Question q : questions) {
            System.out.println(q.getId());
        }
    }
}
