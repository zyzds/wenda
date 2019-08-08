package cn.zyz.wenda.interception;

import cn.zyz.wenda.dao.LoginTicketDAO;
import cn.zyz.wenda.dao.UserDAO;
import cn.zyz.wenda.model.HostHolder;
import cn.zyz.wenda.model.LoginTicket;
import cn.zyz.wenda.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class PassportInterception implements HandlerInterceptor {
    @Autowired
    private LoginTicketDAO loginTicketDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = null;
        if (request.getCookies() == null) {
            return true;
        }
        for (Cookie cookie : request.getCookies()) {
            if ("ticket".equals(cookie.getName())) {
                ticket = cookie.getValue();
                break;
            }
        }
        LoginTicket loginTicket = loginTicketDAO.selectByTicket(ticket);
        if (loginTicket == null || loginTicket.getStatus() != 0) {
            return true;
        }
        if (loginTicket.getExpired().before(new Date())) {
            loginTicketDAO.updateTicket(ticket, 1);
            return true;
        }
        User user = userDAO.selectById(loginTicket.getUserId());
        hostHolder.add(user);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            modelAndView.addObject("user", hostHolder.get());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.remove();
    }
}
