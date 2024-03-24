package org.matcris.footyfix.config;

import org.matcris.footyfix.web.filter.FirebaseAuthenticationTokenFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

public class FilterConfig {

    @Bean
    public FilterRegistrationBean<FirebaseAuthenticationTokenFilter> firebaseAuthenticationFilter() {
        FilterRegistrationBean<FirebaseAuthenticationTokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new FirebaseAuthenticationTokenFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }
}
