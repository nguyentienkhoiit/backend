package com.capstone.backend.utils;

import org.springframework.stereotype.Service;

@Service
public class Constants {
    public static final String HOST = "http://localhost:8080";
    public static final String HOST_SERVER = "D:/SEP490/backend/resource";
    public static final String API_VERSION = "/api/v1";
    public static final Long DEFAULT_PAGE_SIZE = 9L;
    public static final Long DEFAULT_PAGE_INDEX = 1L;
    public static final Long TOTAL_POINT_DEFAULT = 10L;
    public static final Long DEFAULT_VALUE = 1L;
    public static final Long EMAIL_WAITING_EXPIRATION = 15L;
    public static final Long POINT_RESOURCE = 0L;
    public static final Long DEFAULT_TAG_PAGE_SIZE = 25L;
    public static String[] LIST_PERMIT_ALL = new String[]{
            "/api/v1/auth/**", "/api/v1/register/**", "/v2/api-docs", "/v3/api-docs",
            "/v3/api-docs/**", "/swagger-resources", "/swagger-resources/**",
            "/configuration/ui", "/configuration/security", "/swagger-ui/**",
            "/webjars/**", "/swagger-ui.html", "/api/v1/resource/view/**"
    };
    public static String CREATOR_RESOURCE_PERMISSION = "CDRUV";
    public static String CREATOR_RESOURCE_PERMISSION_MESSAGE = "Owner";
    public static String SHARED_RESOURCE_PERMISSION = "DV";
    public static String SHARED_RESOURCE_PERMISSION_MESSAGE = "View permission";
    public static final String ROLE_GUEST_NAME = "GUEST";

}
