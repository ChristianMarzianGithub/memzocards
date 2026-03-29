package com.memzocards.main.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration 
class SecurityConfig {

    @Bean
    fun userDetailsService(): InMemoryUserDetailsManager {
        // withDefaultPasswordEncoder is fine for local testing/H2
        val user = User.withDefaultPasswordEncoder()
            .username("user")
            .password("password")
            .roles("USER")
            .build()
        return InMemoryUserDetailsManager(user)
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/h2-console/**").permitAll()
                    .anyRequest().authenticated()
            }
            .csrf { csrf -> 
                csrf.ignoringRequestMatchers("/h2-console/**") 
            }
            .headers { headers -> 
                headers.frameOptions { frame -> frame.sameOrigin() } 
            }
        
        return http.build()
    }
}