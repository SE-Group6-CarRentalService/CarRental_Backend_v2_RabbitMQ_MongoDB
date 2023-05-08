package at.fhcampuswien.carrental.carrentalservice.config;

import at.fhcampuswien.carrental.carrentalservice.security.JwtAuthenticationEntryPoint;
import at.fhcampuswien.carrental.carrentalservice.security.JwtRequestFilter;
import at.fhcampuswien.carrental.carrentalservice.services.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
//Uncomment for Security init commented for testing purpose
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Uncomment for Security init commented for testing purpose
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().and().csrf().disable()
                // dont authenticate this particular request
                .authorizeHttpRequests().requestMatchers("/v1/Cars/**").permitAll()
                .requestMatchers("/v1/Customers/login", "/v1/Customers/register").permitAll()
                .requestMatchers("/swagger-ui/**","/v3/api-docs/**", "/actuator/**").permitAll().
                // all other requests need to be authenticated
                        anyRequest().authenticated().and().
                        exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Add a filter to validate the tokens with every request
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }





}
