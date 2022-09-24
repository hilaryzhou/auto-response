package com.hilary.web.intercepor.intercepor;

import com.hilary.web.model.Robot;
import com.hilary.web.utils.ThreadLocalUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(1)
@WebFilter(filterName = "tokenFilter", urlPatterns = "/*")
@Component
public class AppTokenFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String code = req.getHeader("code");
        //如果userId为0，说明当前设备没有登录
        if (code != null && (Integer.parseInt(code) != 0)) {
            Robot robot = new Robot();
            robot.setCode(code);
            ThreadLocalUtils.setrobot(robot);
        }
        //放行
        chain.doFilter(req, resp);
        ThreadLocalUtils.clear();
    }
}
