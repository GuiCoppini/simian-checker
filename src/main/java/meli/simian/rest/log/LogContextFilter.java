package meli.simian.rest.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class LogContextFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String httpMethod = req.getMethod();
        String endpoint = req.getRequestURI();
        log.info("m={} path={}", httpMethod, endpoint);

        chain.doFilter(request, response);

        long totalTime = System.currentTimeMillis() - startTime;
        log.info("m={} path={}, timeElapsed={}ms, status={}",
                httpMethod, endpoint, totalTime, res.getStatus());
    }
}