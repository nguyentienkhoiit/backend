package com.capstone.backend.security.jwt;

import com.capstone.backend.entity.type.MethodType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.capstone.backend.model.CustomError;
import com.capstone.backend.repository.UserRolePermissionRepository;
import com.capstone.backend.utils.Constants;
import com.capstone.backend.utils.MessageException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    JwtService jwtService;
    UserDetailsService userDetailsService;
    UserRolePermissionRepository userRolePermissionRepository;
    MessageException messageException;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String url = request.getRequestURI();
        if (isPermissionApi(url)) {
            filterChain.doFilter(request, response);
            return;
        }
        var data = new Object() {
            final String path = getPath(url);
            final MethodType methodType = MethodType.valueOf(request.getMethod().toUpperCase());
        };
        System.out.println("path: "+data.path);

        final String authHeader = request.getHeader(AUTHORIZATION);
        final String jwt;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            responseToClient(response, messageException.MSG_BEARER_NOT_FOUND);
            return;
        }
        jwt = authHeader.split(" ")[1].trim();
        String[] roles = jwtService.getRolesFromToken(jwt);

        boolean isPermission = Arrays.stream(roles).anyMatch(role -> {
            return (userRolePermissionRepository.needCheckPermission(data.path, data.methodType, role) != null);
        });

        if (!isPermission) {
            responseToClient(response, messageException.MSG_NO_PERMISSION);
            return;
        }

        username = jwtService.extractUsername(jwt);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                Arrays.stream(roles).forEach(role -> {
                    authorities.add(new SimpleGrantedAuthority(role));
                });
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authorities
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            else {
                responseToClient(response, messageException.MSG_TOKEN_INVALID);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    public boolean isPermissionApi(String url) {
        return Arrays.stream(Constants.LIST_PERMIT_ALL)
                .anyMatch(o -> url.contains(o.replace("*", "")));
    }

    public String getPath(String path) {
        int index = path.lastIndexOf("/");
        String uri = path.substring(index);
        Pattern pattern = Pattern.compile("-?\\d+");
        Matcher matcher = pattern.matcher(uri);
        if(matcher.find()) {
            path = path.replace(uri, "");
        }
        return path;
    }

    private void responseToClient(HttpServletResponse response, String message) throws IOException {
        CustomError customError = CustomError.builder()
                .code("403")
                .message(message)
                .build();
        response.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        response.getOutputStream().print(mapper.writeValueAsString(customError));
        response.flushBuffer();
    }
}
