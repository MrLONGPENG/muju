package com.lveqia.cloud.zuul.config.filter;

import com.lveqia.cloud.common.objeck.info.UserInfo;
import com.lveqia.cloud.common.util.AuthUtil;
import com.lveqia.cloud.zuul.config.RedisCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.annotation.Nullable;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Component
public class AuthTokenFilter extends OncePerRequestFilter {


    private final RedisCacheManager redisCacheManager;
    private static Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Autowired
    public AuthTokenFilter(RedisCacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }


    @Override
    protected void doFilterInternal(@Nullable HttpServletRequest request, @Nullable HttpServletResponse response
            , @Nullable  FilterChain filterChain) throws ServletException, IOException {
        if(request == null || response == null || filterChain == null ) return;
        if(response.getStatus() == HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
            logger.error("AuthTokenFilter has 500 error");
            return;
        }
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
        } else {
            UserInfo userInfo = AuthUtil.getUserInfo(request);
            if (userInfo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                String key = AuthUtil.getKey(userInfo);
                String redisToken = redisCacheManager.getToken(key);
                if (!AuthUtil.isTokenExpired(userInfo) && redisToken != null && redisToken.equals(userInfo.getToken())){
                    redisCacheManager.expireAt(key, AuthUtil.generateExpirationDate(AuthUtil.EXPIRATION_REDIS));
                    logger.debug("Token 有效， username " + userInfo.getUsername() + ", setting security context");
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userInfo, null, getAuthorities(userInfo.getRoleInfo()));
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(request, response);
        }
    }

    private Collection<? extends GrantedAuthority> getAuthorities(List<?> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Object role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        return authorities;
    }
}
