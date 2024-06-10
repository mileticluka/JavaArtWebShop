package hr.algebra.javawebproject.listener;

import hr.algebra.javawebproject.model.Log;
import hr.algebra.javawebproject.repository.ILogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class LoginEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final ILogRepository logRepository;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attr.getRequest();

        String ipAddress = request.getRemoteAddr();
        UserDetails userDetails = (UserDetails) event.getAuthentication().getPrincipal();

        Log log = new Log();
        log.setTime(LocalDateTime.now());
        log.setMessage("User: '" + userDetails.getUsername() + "' logged in from IP Address: " + ipAddress);

        logRepository.save(log);
    }
}