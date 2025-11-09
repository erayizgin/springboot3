package com.pluralsight.ecommerce.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.pluralsight.ecommerce.common.LoginResponse;
import com.pluralsight.ecommerce.dto.GoogleOauthRequest;
import com.pluralsight.ecommerce.dto.user.LoginUserDto;
import com.pluralsight.ecommerce.dto.user.RegisterUserDto;
import com.pluralsight.ecommerce.model.User;
import com.pluralsight.ecommerce.service.AuthenticationService;
import com.pluralsight.ecommerce.service.GoogleOAuthService;
import com.pluralsight.ecommerce.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;
    private final GoogleOAuthService googleOAuthService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, GoogleOAuthService googleOAuthService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.googleOAuthService = googleOAuthService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(null, authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/login/oauth/google")
    public ResponseEntity<LoginResponse> googleAuthenticate(@RequestBody GoogleOauthRequest oauthreq) throws Exception {
        GoogleTokenResponse tokenResponse = googleOAuthService.exchangeCodeForTokens(oauthreq.getCode());

        String idTokenString = tokenResponse.getIdToken();

        GoogleIdToken.Payload payload = googleOAuthService.verifyToken(idTokenString);

        System.out.println("User ID (sub): " + payload.getSubject());
        System.out.println("Email: " + payload.getEmail());
        System.out.println("Email Verified: " + payload.getEmailVerified());
        System.out.println("Name: " + payload.get("name"));
        System.out.println("Picture: " + payload.get("picture"));


        User user = null;
        try {
            user = authenticationService.findByEmail(payload.getEmail());
        } catch (Exception e) {
            user = authenticationService.createUser(payload);
        }

        String jwtToken = jwtService.generateToken(null, user);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }

}


















