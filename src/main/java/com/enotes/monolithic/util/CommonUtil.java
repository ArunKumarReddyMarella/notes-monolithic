package com.enotes.monolithic.util;

import com.enotes.monolithic.config.security.CustomUserDetails;
import com.enotes.monolithic.entity.User;
import com.enotes.monolithic.handler.GenericResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

public class CommonUtil {

    private static final String SUCCESS = "success";
    private static final String FAILED = "failed";

    private CommonUtil() {
    }

    public static ResponseEntity<?> createBuildResponse(Object data, HttpStatus status) {

        GenericResponse response = GenericResponse.builder().responseStatus(status).status(SUCCESS).message(SUCCESS)
                .data(data).build();
        return response.create();
    }

    public static ResponseEntity<?> createBuildResponseMessage(String message, HttpStatus status) {

        GenericResponse response = GenericResponse.builder().responseStatus(status).status(SUCCESS).message(message)
                .build();
        return response.create();
    }

    public static ResponseEntity<?> createErrorResponse(Object data, HttpStatus status) {

        GenericResponse response = GenericResponse.builder().responseStatus(status).status(FAILED).message(FAILED)
                .data(data).build();
        return response.create();
    }

    public static ResponseEntity<?> createErrorResponseMessage(String message, HttpStatus status) {

        GenericResponse response = GenericResponse.builder().responseStatus(status).status(FAILED).message(message)
                .build();
        return response.create();
    }

    public static String getContentType(String originalFileName) {
        String extension = FilenameUtils.getExtension(originalFileName); // java_programing.pdf

        switch (extension) {
            case "pdf":
                return "application/pdf";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheettml.sheet";
            case "txt":
                return "text/plan";
            case "png":
                return "image/png";
            case "jpeg":
                return "image/jpeg";
            default:
                return "application/octet-stream";
        }
    }

    public static String getUrl(HttpServletRequest request) {
        String apiUrl = request.getRequestURL().toString(); // http:localhost:8080/api/v1/auth
        apiUrl = apiUrl.replace(request.getServletPath(), ""); // http:localhost:8080
        return apiUrl;
    }

    public static User getLoggedInUser() {
        CustomUserDetails loggedInUser = (CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return loggedInUser.getUser();
    }

}
