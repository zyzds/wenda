package cn.zyz.wenda.configuration;

import cn.zyz.wenda.interception.LoginRequestInception;
import cn.zyz.wenda.interception.PassportInterception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WendaWebConfiguration implements WebMvcConfigurer {
    @Autowired
    private PassportInterception interception;

    @Autowired
    private LoginRequestInception loginRequestInception;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interception);
        registry.addInterceptor(loginRequestInception).addPathPatterns("/user/**", "/msg/**");
    }
}
