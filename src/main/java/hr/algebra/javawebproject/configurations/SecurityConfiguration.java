package hr.algebra.javawebproject.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain web(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login/*").permitAll()

                        .requestMatchers("/category/*").permitAll()
                        .requestMatchers("/artist/*").permitAll()
                        .requestMatchers("/artpiece/*").permitAll()
                        .requestMatchers("/cart/*").permitAll()

                        .requestMatchers("/*/create").hasRole("ADMIN")
                        .requestMatchers("/*/update").hasRole("ADMIN")
                        .requestMatchers("/*/delete").hasRole("ADMIN")

                        .requestMatchers("/transaction/add-transaction-cash").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/transaction/add-transaction-paypal").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/transaction/execute-payment").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/transaction/success").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/transaction/details").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/transaction/get").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.defaultSuccessUrl("/artpiece/get"))
                .logout(LogoutConfigurer::permitAll)
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository()) // Set up CSRF token repository
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HttpSessionCsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setSessionAttributeName("_csrf");
        return repository;
    }
}
