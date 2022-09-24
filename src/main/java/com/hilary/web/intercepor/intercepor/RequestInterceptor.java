package com.hilary.web.intercepor.intercepor;

import com.alibaba.fastjson.JSON;
import com.hilary.web.model.commons.Response;
import com.hilary.web.utils.IpUtils;
import com.hilary.web.utils.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @Description:
 * @Auther: zhouhuan
 * @Date: 2022/8/8-14:18
 */
@Slf4j
@Component
@Order(1)
public class RequestInterceptor implements HandlerInterceptor {

    private static final String DEFAULT_ERROR = "/error";
    private final ThreadLocal<Long> requestCostThreadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        if (!DEFAULT_ERROR.equals(uri)) {
            String method = request.getMethod();
            Map<String, String[]> parameter = request.getParameterMap();
            String body = RequestUtils.getRequestBodyStr(request);
            String ip = IpUtils.getIpAddr(request);
            long startTime = System.currentTimeMillis();
            requestCostThreadLocal.set(startTime);
            log.info("Before request ip:{}, uri:{}, method:{};parameter:{}, body:{}, start at {}",
                    ip, uri, method, JSON.toJSONString(parameter), body, startTime);

        }
        return true;
    }

    /**
     * 接口成功响应之后的日志
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        String uri = request.getRequestURI();
        String ip = IpUtils.getIpAddr(request);

        if (!DEFAULT_ERROR.equals(uri)) {
            long endTime = System.currentTimeMillis();
            long startTime = requestCostThreadLocal.get();
            String method = request.getMethod();
            log.info("After request ip:{}, uri:{}, method:{}, end at {}, cost {}", ip, uri, method, endTime,
                    endTime - startTime);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        requestCostThreadLocal.remove();
    }

    private void sendErrorResponse(HttpServletResponse response, Response result)
            throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(JSON.toJSONString(result));
    }
}
