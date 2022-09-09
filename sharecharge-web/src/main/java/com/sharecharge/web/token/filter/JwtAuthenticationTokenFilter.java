package com.sharecharge.web.token.filter;

import com.sharecharge.core.constant.ExceptionConstant;
import com.sharecharge.core.exception.ServiceException;
import com.sharecharge.core.util.ResultUtil;
import com.sharecharge.core.util.StringUtils;
import com.sharecharge.security.config.JwtConfig;
import com.sharecharge.security.model.LoginUser;
import com.sharecharge.system.entity.DbAdminUser;
import com.sharecharge.system.entity.DbMenu;
import com.sharecharge.system.service.DbAdminUserService;
import com.sharecharge.system.service.DbMenuService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * token过滤器 验证token有效性
 *
 * @author
 */
@Slf4j
@Component
public class JwtAuthenticationTokenFilter  extends OncePerRequestFilter {//BasicAuthenticationFilter   OncePerRequestFilter

    @Autowired
    DbAdminUserService dbAdminUserService;
    @Autowired
    DbMenuService dbMenuService;

    /**
     * {bcrypt} 加密的特征码
     */
    private static final String BCRYPT = "{bcrypt}";

//    public JwtAuthenticationTokenFilter(AuthenticationManager authenticationManager) {
//        super(authenticationManager);
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取请求头中JWT的Token
        String tokenHeader = request.getHeader(JwtConfig.tokenHeader);
        if (!StringUtils.isEmpty(tokenHeader)&& tokenHeader.startsWith(JwtConfig.tokenPrefix)) {
            try {
                // 截取JWT前缀
                String token = tokenHeader.replace(JwtConfig.tokenPrefix, "");

                SecurityContextHolder.getContext().setAuthentication(getAuthentication(token));

            } catch (ExpiredJwtException e){
                log.info("Token过期");
                //getWriter() has already been called for this response
                //throw new ServiceException("Token过期",ExceptionConstant.TOKEN_EXCEPTIONCODE);
                response.setStatus(200);
                Map<String, Object> resultMap = new HashMap<String, Object>();
                resultMap.put("code", ExceptionConstant.TOKEN_EXCEPTIONCODE);
                resultMap.put("msg","登录信息过期 请重新登录");
                ResultUtil.responseJson(response,resultMap);
                return;
            } catch (Exception e) {
                log.info("Token无效");
                //throw new ServiceException("Token无效",ExceptionConstant.TOKEN_EXCEPTIONCODE);
                response.setStatus(200);
                Map<String, Object> resultMap = new HashMap<String, Object>();
                resultMap.put("code",ExceptionConstant.TOKEN_EXCEPTIONCODE);
                resultMap.put("msg","Token无效");
                ResultUtil.responseJson(response,resultMap);
                return;
            }
        }
        filterChain.doFilter(request, response);

    }

    public Authentication getAuthentication(String token) {
        // 解析JWT
        Claims claims = Jwts.parser()
                .setSigningKey(JwtConfig.secret)
                .parseClaimsJws(token)
                .getBody();

        String userId = claims.getId();

//        List<GrantedAuthority> authorities = AuthorityUtils
//                .commaSeparatedStringToAuthorityList((String) claims.get("authorities"));

        DbAdminUser dbAdminUser = dbAdminUserService.getById(Integer.valueOf(userId));

        List<DbMenu> menuByRoleId = dbMenuService.findMenuByRoleId(dbAdminUser.getRoleId());
        Set<String> dbAuthsSet = new HashSet<>();
        for (DbMenu dbMenu:menuByRoleId){
            dbAuthsSet.add(dbMenu.getPerms());
        }
        Collection<? extends GrantedAuthority> authorities = AuthorityUtils
                .createAuthorityList(dbAuthsSet.toArray(new String[0]));

        LoginUser loginUser = new LoginUser
                (
                        dbAdminUser.getId(),dbAdminUser.getParentId(),dbAdminUser.getAdminPhone(),
                        null, dbAdminUser.getRoleId(),dbAdminUser,
                        dbAdminUser.getAdminName(),dbAdminUser.getAdminPassword(),
                        true, true, true, true,
                        authorities
                );
        //User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(loginUser, "", authorities);
    }
}
