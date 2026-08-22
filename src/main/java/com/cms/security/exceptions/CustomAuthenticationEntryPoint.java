package com.cms.security.exceptions;

import com.cms.models.constants.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String jsonResponse = String.format(
                "{" +
                        "\"success\":false," +
                        "\"status_code\":%d," +
                        "\"error_code\":\"%s\"," +
                        "\"message\":\"%s\"," +
                        "\"errors\":null," +
                        "\"timestamp\":\"%s\"" +
                        "}",
                HttpServletResponse.SC_UNAUTHORIZED,
                ErrorCode.BAD_CREDENTIALS.getCode(),
                "Yêu cầu xác thực tài khoản",
                Instant.now().toString()
        );

        PrintWriter writer = response.getWriter();
        writer.write(jsonResponse);
        writer.flush();
    }
}