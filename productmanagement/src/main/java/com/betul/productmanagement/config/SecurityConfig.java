package com.betul.productmanagement.config;

import com.betul.productmanagement.security.JwtAuthenticationFilter;
import com.betul.productmanagement.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return customUserDetailsService;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Static kaynaklar (JS, CSS, images vs.)
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

                        // HTML dosyaları
                        .requestMatchers("/index.html").permitAll()
                        .requestMatchers("/login.html").permitAll()
                        .requestMatchers("/login").permitAll() // Login endpoint'ine erişime izin ver
                        .requestMatchers("/register.html").permitAll()
                        .requestMatchers("/register").permitAll() // Register endpoint'ine erişime izin ver
                        .requestMatchers("/products.html").permitAll()
                        .requestMatchers("/products").permitAll()
                        .requestMatchers("/cart.html").permitAll()
                        .requestMatchers("/cart").permitAll()
                        .requestMatchers("/purchase.html").permitAll()
                        .requestMatchers("/purchase").permitAll()
                        .requestMatchers("/myOrders.html").permitAll()
                        .requestMatchers("/myOrders").permitAll()

                        // API endpointleri
                        .requestMatchers("/api/auth/**").permitAll()
                        // Products API
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/products/**").hasRole("ADMIN")
                        // Categories API
                        .requestMatchers(HttpMethod.POST, "/api/categories").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/categories").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        // Cart API
                        .requestMatchers("/api/cart/**").hasAnyRole("USER", "ADMIN") // Sepet işlemleri için kullanıcı/admin yetkisi
                        // Orders API
                        .requestMatchers("/api/orders/**").hasAnyRole("USER", "ADMIN")

                        // H2 Console
                        .requestMatchers("/h2-console/**").permitAll()

                        //Geri kalan her şey girişli kullanıcı için
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        //configuration.setAllowedOrigins(List.of("http://localhost:8082")); // frontend host portunu belirt
        configuration.setAllowCredentials(true); // cookie/JWT iletimini sağlar
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
