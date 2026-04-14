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
                .csrf(csrf -> csrf.disable())
                // H2 console uses frames — keep same-origin frames allowed
                .headers(headers -> headers.frameOptions(f -> f.sameOrigin()))
                // Stateless — JWT, no server session
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public: login, refresh, H2 console, WSDL, XML/JSON validation, GUI
                        .requestMatchers("/auth/**", "/h2-console/**", "/ws/**",
                                "/api/xml/**", "/api/json/**", "/api/proxy/**",
                                "/graphiql/**", "/", "/index.html").permitAll()
                        // GET endpoints: both roles can read
                        .requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("READ_ONLY", "FULL_ACCESS")
                        // GraphQL (always POST): both roles can query/mutate
                        .requestMatchers(HttpMethod.POST, "/graphql").hasAnyRole("READ_ONLY", "FULL_ACCESS")
                        // Write endpoints: only FULL_ACCESS
                        .requestMatchers(HttpMethod.POST, "/api/**").hasRole("FULL_ACCESS")
                        .requestMatchers(HttpMethod.PUT, "/api/**").hasRole("FULL_ACCESS")
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("FULL_ACCESS")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Access denied: insufficient role\"}");
                        })
                )
                .authenticationProvider(authenticationProvider())
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

    // gRPC does not use JWT auth in this project — return null (anonymous) so the
    // gRPC security autoconfiguration is satisfied without blocking any calls.
    @Bean
    public GrpcAuthenticationReader grpcAuthenticationReader() {
        return (call, headers) -> null;
    }
}