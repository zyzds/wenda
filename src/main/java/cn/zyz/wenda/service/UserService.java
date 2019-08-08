package cn.zyz.wenda.service;

import cn.zyz.wenda.dao.LoginTicketDAO;
import cn.zyz.wenda.dao.UserDAO;
import cn.zyz.wenda.model.LoginTicket;
import cn.zyz.wenda.model.User;
import cn.zyz.wenda.util.SensitiveWord;
import cn.zyz.wenda.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserDAO userDao;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public Map<String, String> register(User user) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isEmpty(user.getPassword())) {
            map.put("msg", "密码为空！");
            return map;
        }
        if (SensitiveWord.hasSensitiveWord(user.getName())) {
            map.put("msg", "用户名包含敏感词汇");
            return map;
        }
        if (!user.getName().matches("^[0-9a-zA-Z\\u4e00-\\u9fa5]{2,10}$")) {
            map.put("msg", "用户名不合法");
            return map;
        }
        if (userDao.selectByName(user.getName()) != null) {
            map.put("msg", "用户名已经被使用！");
            return map;
        }
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        user.setPassword(WendaUtil.MD5(user.getPassword() + user.getSalt()));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        userDao.addUser(user);

        LoginTicket ticket = addLoginTicket(userDao.selectByName(user.getName()).getId());
        map.put("ticket", ticket.getTicket());
        return map;
    }

    public Map<String, String> login(String name, String password) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isEmpty(name)) {
            map.put("msg", "用户名为空！");
            return map;
        }
        if (StringUtils.isEmpty(password)) {
            map.put("msg", "密码为空！");
            return map;
        }
        User user = userDao.selectByName(name);
        if (user == null || !user.getPassword().equals(WendaUtil.MD5(password + user.getSalt()))) {
            map.put("msg", "用户不存在或密码错误！");
            return map;
        }

        LoginTicket ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket.getTicket());
        return map;
    }

    public void logout(String ticket) {
        loginTicketDAO.updateTicket(ticket, 1);
    }

    public User getUserById(int id) {
        return userDao.selectById(id);
    }

    public User getUserByName(String name) {
        return userDao.selectByName(name);
    }

    public LoginTicket addLoginTicket(int userId) {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        loginTicket.setExpired(new Timestamp(1000 * 3600 * 24 * 100L + System.currentTimeMillis()));
        loginTicket.setStatus(0);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDAO.addTicket(loginTicket);
        return loginTicket;
    }

}
