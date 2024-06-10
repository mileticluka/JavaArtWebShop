package hr.algebra.javawebproject.listener;

import hr.algebra.javawebproject.model.Log;
import hr.algebra.javawebproject.repository.ILogRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@AllArgsConstructor
@Component
public class HttpActionFilter implements Filter {

    private final ILogRepository logRepository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (!(servletRequest instanceof HttpServletRequest request)
                || !(servletResponse instanceof HttpServletResponse response)) {
            return;
        }

        String method = request.getMethod();
        String path = request.getServletPath();
        String clientIP = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String id = request.getParameter("id");

        Log log = new Log();
        log.setTime(LocalDateTime.now());

        if (path.contains("create")) {
            if(request.getMethod().equals("POST")) {
                log.setMessage("Create Request sent to '" + path + "' from IP: " + clientIP + " User-Agent: " + userAgent);
                logRepository.save(log);
            }
        } else if (path.contains("edit")) {
            if(request.getMethod().equals("POST"))
            {
                log.setMessage("Edit Request sent to '" + path + "' with id=" + id + " from IP: " + clientIP + " User-Agent: " + userAgent);
                logRepository.save(log);
            }

        } else if (path.contains("delete")) {
            log.setMessage("Delete Request sent to '" + path + "' with id=" + id + " from IP: " + clientIP + " User-Agent: " + userAgent);
            logRepository.save(log);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}