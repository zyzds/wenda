package cn.zyz.wenda.dao;

import cn.zyz.wenda.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDAOTest {
    @Autowired
    private UserDAO userDao;

    @Test
    public void test() {
        User user = new User();
        user.setName("james");
        user.setPassword("123456");
        user.setSalt("ki8");
        user.setHeadUrl("../static/images/img/sprites.auto.915a539c.png");
        userDao.addUser(user);

        user.setPassword("123");
        user.setId(1);
        userDao.updatePassword(user);
        Assert.assertEquals("123", userDao.selectById(1).getPassword());

        userDao.deleteById(1);
        Assert.assertNull(userDao.selectById(1));
    }
}
