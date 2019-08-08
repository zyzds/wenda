package cn.zyz.wenda.controller;

import cn.zyz.wenda.model.User;
import cn.zyz.wenda.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/reg", method = {RequestMethod.POST})
    public String reg(User user, Model model, HttpServletResponse response,
                      @RequestParam(value = "next", required = false) String next) {
        try {
            Map<String, String> map = userService.register(user);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket"));
                cookie.setMaxAge(100);
                response.addCookie(cookie);
                if (!StringUtils.isEmpty(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/index";
            }
            model.addAttribute("msg", map.get("msg"));
            return "reglogin";
        } catch (Exception e) {
            logger.error("注册异常：" + e.getMessage());
            return "reglogin";
        }
    }

    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    public String login(Model model,
                        HttpServletResponse response,
                        @RequestParam("name") String name,
                        @RequestParam("password") String password,
                        @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                        @RequestParam(value = "next", required = false) String next) {
        try {
            Map<String, String> map = userService.login(name, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket"));
                cookie.setMaxAge((rememberme ? 365 : 100) * 24 * 3600);
                response.addCookie(cookie);
                if (!StringUtils.isEmpty(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/index";
            }
            model.addAttribute("msg", map.get("msg"));
            return "reglogin";
        } catch (Exception e) {
            logger.error("登录异常：" + e.getMessage());
            return "reglogin";
        }
    }

    @RequestMapping(value = "/logout", method = {RequestMethod.GET})
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/index";
    }

    @RequestMapping("/reglogin")
    public String reglogin() {
        return "reglogin";
    }
}
