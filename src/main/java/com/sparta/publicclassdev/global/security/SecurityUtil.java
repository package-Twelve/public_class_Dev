package com.sparta.publicclassdev.global.security;

import com.sparta.publicclassdev.domain.users.entity.RoleEnum;
import com.sparta.publicclassdev.domain.users.entity.Users;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {

    public static void setAdminRole() {
        Users users = Users.builder()
            .name("admin")
            .email("admin@email.com")
            .password("password")
            .role(RoleEnum.ADMIN)
            .build();

        UserDetails userDetails = new UserDetailsImpl(users);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
