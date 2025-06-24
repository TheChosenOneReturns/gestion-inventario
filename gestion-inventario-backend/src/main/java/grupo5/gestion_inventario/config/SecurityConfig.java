package grupo5.gestion_inventario.config;

import grupo5.gestion_inventario.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    // --- MÉTODO CORREGIDO ---
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // <-- CAMBIO CLAVE 1: Deshabilitar CSRF. Fundamental para APIs REST stateless.
                .csrf(csrf -> csrf.disable())

                // <-- CAMBIO CLAVE 2: Aplicar la configuración de CORS a la cadena de filtros.
                .cors(withDefaults()) // Esto le dice a Spring que use el Bean 'corsConfigurationSource'

                .authorizeHttpRequests(auth -> auth
                        // Se mantienen tus reglas, permitiendo acceso a la autenticación y al actuator.
                        .requestMatchers(
                                "/api/auth/**",
                                "/superpanel/login",
                                "/api/clients",
                                "/actuator/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // Se mantiene la política de sesión STATELESS, correcta para JWT.
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // Se mantiene tu filtro JWT, que se ejecutará después de que la autenticación inicial pase.
        http.addFilterBefore(jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Tu configuración de CORS está casi perfecta, solo un pequeño ajuste.
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // ¡IMPORTANTE! Para producción, reemplaza "*" con la URL de tu frontend en CloudFront/S3.
        configuration.setAllowedOrigins(Arrays.asList("https://dojkssjy03yxb.cloudfront.net/login","http://gestion-inventario-frontend-ariel12345.s3-website-us-east-1.amazonaws.com","https://dojkssjy03yxb.cloudfront.net", "http://localhost:5173", "http://localhost:5174", "http://localhost:5176", "http://localhost:5177"));
        
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        // Permite las cabeceras necesarias para la autenticación y el tipo de contenido.
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}