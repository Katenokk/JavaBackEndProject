package com.pethealth.finalproject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

public class TokenAuthenticationService {

    public static String createToken(String username, String role) {
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        // Adding user details and roles to the token
        String access_token = JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .withIssuer("http://localhost:8080")
                .withClaim("roles", Collections.singletonList(role))
                .sign(algorithm);

        return "Bearer " + access_token;
    }
}
