package com.cms.security.exceptions;

import com.cms.models.constants.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        String jsonResponse = String.format(
                "{" +
                        "\"success\":false," +
                        "\"status_code\":%d," +
                        "\"error_code\":\"%s\"," +
                        "\"message\":\"%s\"," +
                        "\"errors\":null," +
                        "\"timestamp\":\"%s\"" +
                        "}",
                HttpServletResponse.SC_FORBIDDEN,
                ErrorCode.ACCESS_DENIED.getCode(),
                "Bạn không có quyền truy cập tài nguyên này",
                Instant.now().toString()
        );

        PrintWriter writer = response.getWriter();
        writer.write(jsonResponse);
        writer.flush();
    }
}