package hr.algebra.paola.config;


import hr.algebra.paola.auth.AppDetailsService;
import hr.algebra.paola.auth.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
public class SecurityConfig {
    
    private final JwtFilter jwtFilter;
    private final AppDetailsService appDetailsService;

    public SecurityConfig(JwtFilter jwtFilter, AppDetailsService appDetailsService) {
        this.jwtFilter = jwtFilter;
        this.appDetailsService = appDetailsService;
    }

    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(f -> f.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**","/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("READ_ONLY","FULL_ACCESS","MANAGER_ACCESS")
                        .requestMatchers(HttpMethod.POST,"api/**").hasRole("FULL_ACCESS")
                        .requestMatchers(HttpMethod.PUT,"api/**").hasRole("FULL_ACCESS")
                        .requestMatchers(HttpMethod.DELETE,"api/**").hasAnyRole("FULL_ACCESS","MANAGER_ACCESS")
                        //adding menager role
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) ->{
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.getWriter().write("error");
                        })
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();

    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(appDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }



}
