package com.kiss.kissnest.filter;

import com.kiss.kissnest.util.CodeUtil;
import entity.Guest;
import filter.GuestFilter;
import filter.InnerFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import utils.GuestUtil;
import utils.ThreadLocalUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@WebFilter(filterName = "nestFilter", urlPatterns = "/*")
public class NestFilter implements Filter {

    private CodeUtil codeUtil;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext servletContext = filterConfig.getServletContext();
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        codeUtil = context.getBean(CodeUtil.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        ResponseWrapper responseWrapper = new ResponseWrapper(httpServletResponse);
        InnerFilterChain preFilterChain = new InnerFilterChain();

        if (!httpServletRequest.getRequestURI().contains("/code/login")) {
            GuestFilter userInfoFilter = new GuestFilter();
            preFilterChain.addFilter(userInfoFilter);
        }

        preFilterChain.doFilter(httpServletRequest, httpServletResponse, preFilterChain);

//        Guest operator = new Guest();
//        operator.setId(120);
//        operator.setName("小钱");
//        operator.setName("xiaoqian");
//        GuestUtil.setGuest(operator);

        chain.doFilter(httpServletRequest, responseWrapper);
        InnerFilterChain suffixFilterChain = new InnerFilterChain();
        suffixFilterChain.addFilter(new ResponseFilter(responseWrapper, codeUtil));
        suffixFilterChain.doFilter(httpServletRequest, httpServletResponse, suffixFilterChain);
        ThreadLocalUtil.remove();
    }

    @Override
    public void destroy() {

    }
}
