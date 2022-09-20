package com.ruoyi.framework.security.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class SwaggerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().matches("^/owon(/\\w*)*|^/general(/\\w*)*")){
            LoginUser loginUser = getLoginUser(request.getHeader("Authorization"));
            if(loginUser == null || !loginUser.getUser().isAdmin()) {
                response.setContentType("application/json");
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.getWriter().print(JsonMapper.builder().build()
                        .writeValueAsString(AjaxResult.error("没有权限")));
                return;
            }
        }
        filterChain.doFilter(request,response);
    }

    private LoginUser getLoginUser(String token) {
        Environment environment = SpringUtils.getBean(Environment.class);
        String secret = environment.getProperty("token.secret");
        if (StringUtils.isNotEmpty(token)){
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(secret)
                        .parseClaimsJws(token)
                        .getBody();
                // 解析对应的权限以及用户信息
                String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
                String userKey = Constants.LOGIN_TOKEN_KEY + uuid;
                RedisCache redisCache = SpringUtils.getBean(RedisCache.class);
                LoginUser user = redisCache.getCacheObject(userKey);
                return user;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
}
