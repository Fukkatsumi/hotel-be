package com.netcracker.hotelbe.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@EnableWebSecurity
// TODO delete for security enable
@Order(1)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    CustomUserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(SecurityConstants.AUTH_LOGIN_URL).permitAll()
                .antMatchers(HttpMethod.GET, "/apartmentsClasses/**").permitAll()
                .antMatchers(HttpMethod.GET, "/apartmentPrices/**").permitAll()
                .antMatchers(HttpMethod.GET, "/booking-add-services/**").permitAll()
                .antMatchers(HttpMethod.GET, "/user/**").permitAll()
                .antMatchers(HttpMethod.POST, "/user/**").permitAll()
                .antMatchers(HttpMethod.GET, "/reviews/**").permitAll()
                .antMatchers(HttpMethod.GET, "/apartments/**").hasAnyAuthority("Worker", "Manager", "Administrator")
                .antMatchers("/tasks/**").hasAnyAuthority("Worker", "Manager", "Administrator")
                .antMatchers("/apartmentsClasses/**").hasAnyAuthority("Manager", "Administrator")
                .antMatchers("/apartmentPrices/**").hasAnyAuthority("Manager", "Administrator")
                .antMatchers("/booking-add-services/**").hasAnyAuthority("Manager", "Administrator")
                .antMatchers("/apartments/**").hasAnyAuthority("Manager", "Administrator")
                .antMatchers("/staff/**").hasAnyAuthority("Manager", "Administrator")
                .antMatchers(HttpMethod.PUT, "/bookings/**").hasAnyAuthority("Manager", "Administrator")
                .antMatchers(HttpMethod.PATCH, "/bookings/**").hasAnyAuthority("Manager", "Administrator")
                .anyRequest().authenticated()
                .and()
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtAuthorizationFilter(authenticationManager()))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

}
