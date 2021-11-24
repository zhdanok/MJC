package com.epam.esm.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        httpServletResponse.getWriter().print(String.format("{ \n \t \"errorMessage\": %s \n \t \"errorCode\": %d \n}", e.getLocalizedMessage(), HttpStatus.FORBIDDEN.value()));
        httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
    }
}
