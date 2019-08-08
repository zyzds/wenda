package cn.zyz.wenda.model;

import org.springframework.stereotype.Component;

@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<>();

    public User get() {
        return users.get();
    }

    public void add(User user) {
        users.set(user);
    }

    public void remove() {
        users.remove();
    }
}
