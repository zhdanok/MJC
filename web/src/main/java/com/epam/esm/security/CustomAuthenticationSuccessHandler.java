package com.epam.esm.security;

import com.epam.esm.dto.UserDto;
import com.epam.esm.entity.UserProfile;
import com.epam.esm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        UserProfile userProfile = userService.getUserProfileByLogin(authentication.getName());
        Integer userId = null;
        if (userProfile == null) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            UserDto newUser = UserDto.builder()
                    .name(String.valueOf(oAuth2User.getAttributes().get("name")))
                    .login(oAuth2User.getName())
                    .build();
            userId = userService.saveUser(newUser);
        }
        httpServletResponse.sendRedirect(String.format("/users/%d", userId));
    }
}
