package io.github.gotonode.gem.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, proxyTargetClass = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        // TODO: Disable this for production!
        httpSecurity.csrf().disable();

        httpSecurity.headers().frameOptions().sameOrigin();

        httpSecurity.authorizeRequests()
                .antMatchers("/h2-console", "/h2-console/**").hasAuthority("ADMIN")
                .antMatchers(HttpMethod.GET,"/").permitAll()
                .antMatchers(HttpMethod.GET,"/debug", "/debug/**").hasAuthority("ADMIN")
                .antMatchers(HttpMethod.POST,"/debug", "/debug/**").hasAuthority("ADMIN")
                .antMatchers(HttpMethod.POST, "/toggle", "/toggle/**").permitAll()
                .antMatchers(HttpMethod.GET, "/fetch").permitAll()
                .antMatchers(HttpMethod.GET, "/done").permitAll()
                .antMatchers(HttpMethod.GET, "/css", "/css/**").permitAll()
                .antMatchers(HttpMethod.GET, "/img", "/img/**").permitAll()
                .antMatchers(HttpMethod.GET, "/js", "/js/**").permitAll()
                .anyRequest().authenticated().and()
                .formLogin().permitAll().and()
                .logout().logoutSuccessUrl("/").permitAll();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {

        UserDetails user = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("admin")
                .authorities("ADMIN")
                .build();

        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(user);

        return manager;
    }
}
