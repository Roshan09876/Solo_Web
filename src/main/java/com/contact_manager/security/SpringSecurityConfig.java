//package com.contact_manager.security;
//
//
//import com.contact_manager.config.UserDetailsServiceImpl;
//import com.contact_manager.config.PasswordEncoderUtil;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SpringSecurityConfig {
//
//    private final UserDetailsServiceImpl UserDetailsService;
//
//    public SpringSecurityConfig(UserDetailsServiceImpl userDetailsService) {
//        this.UserDetailsService = userDetailsService;
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider(){
//        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
//        authenticationProvider.setUserDetailsService(UserDetailsService);
//        authenticationProvider.setPasswordEncoder(PasswordEncoderUtil.getInstance());
//        return authenticationProvider;
//    }
//
//
//    @Bean
//    protected SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity
//                .authorizeHttpRequests()
//                .requestMatchers("/admin/**")
//                .hasRole("ADMIN")
//                .requestMatchers("/user/**")
//                .hasRole("USER")
//                .requestMatchers("/")
//                .permitAll()
//                .and()
//                .formLogin()
//                .and()
//                .csrf()
//                .disable();
//
//
//        return httpSecurity.build();
//    }
////    @Bean
////    public WebSecurityCustomizer webSecurityCustomizer(){
////        return (web) -> web.ignoring().anyRequest();
////    }
//}