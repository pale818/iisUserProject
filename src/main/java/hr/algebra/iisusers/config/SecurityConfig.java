package hr.algebra.iisusers.config;

import hr.algebra.iisusers.auth.AppUserDetailsService;
import hr.algebra.iisusers.auth.JwtFilter;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final AppUserDetailsService userDetailsService;

    public SecurityConfig(JwtFilter jwtFilter, AppUserDetailsService userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // safe because auth uses Bearer tokens, not browser cookies
                .headers(headers -> headers.frameOptions(f -> f.sameOrigin())) // allows H2 console iframe
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // no HttpSession
                .authorizeHttpRequests(auth -> auth
                        // public endpoints — no token required
                        .requestMatchers("/auth/**", "/h2-console/**", "/ws/**",
                                "/api/proxy/**", "/graphiql/**", "/", "/index.html").permitAll()
                        // any authenticated user can read
                        .requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("READ_ONLY", "FULL_ACCESS")
                        // GraphQL queries — both roles allowed
                        .requestMatchers(HttpMethod.POST, "/graphql").hasAnyRole("READ_ONLY", "FULL_ACCESS")
                        // write operations — admin only
                        .requestMatchers(HttpMethod.POST, "/api/**").hasRole("FULL_ACCESS")
                        .requestMatchers(HttpMethod.PUT, "/api/**").hasRole("FULL_ACCESS")
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("FULL_ACCESS")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        // Returns JSON 403 instead of the default HTML error page
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Access denied: insufficient role\"}");
                        })
                )
                .authenticationProvider(authenticationProvider())
                // JwtFilter runs before Spring Security's default username/password filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public GrpcAuthenticationReader grpcAuthenticationReader() {
        return (call, headers) -> null;
    }
}
