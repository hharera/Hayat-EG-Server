package com.harera.hayat.service.user.auth;

import static com.harera.hayat.util.RegexUtils.isEmail;
import static com.harera.hayat.util.RegexUtils.isPhoneNumber;
import static com.harera.hayat.util.RegexUtils.isUsername;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.harera.hayat.exception.SignupException;
import com.harera.hayat.model.user.FirebaseUser;
import com.harera.hayat.model.user.User;
import com.harera.hayat.model.user.auth.FirebaseOauthToken;
import com.harera.hayat.model.user.auth.LoginRequest;
import com.harera.hayat.model.user.auth.LoginResponse;
import com.harera.hayat.model.user.auth.LogoutRequest;
import com.harera.hayat.model.user.auth.OAuthLoginRequest;
import com.harera.hayat.model.user.auth.SignupRequest;
import com.harera.hayat.model.user.auth.SignupResponse;
import com.harera.hayat.repository.UserRepository;
import com.harera.hayat.repository.user.auth.TokenRepository;
import com.harera.hayat.service.firebase.FirebaseService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthValidation authValidation;
    private final TokenRepository tokenRepository;
    private final FirebaseService firebaseService;
    private final ModelMapper modelMapper;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, JwtService jwtService,
                    PasswordEncoder passwordEncoder, AuthValidation authValidation,
                    TokenRepository tokenRepository, FirebaseService firebaseService,
                    ModelMapper modelMapper, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authValidation = authValidation;
        this.tokenRepository = tokenRepository;
        this.firebaseService = firebaseService;
        this.modelMapper = modelMapper;
        this.jwtUtils = jwtUtils;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        authValidation.validateLogin(loginRequest);

        long userId = getUserId(loginRequest.getSubject());
        User user = userRepository.findById(userId).orElseThrow(
                        () -> new UsernameNotFoundException("User not found"));

        if (!Objects.equals(user.getDeviceToken(), loginRequest.getDeviceToken())) {
            user.setDeviceToken(loginRequest.getDeviceToken());
            userRepository.save(user);
        }

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtService.generateToken(user));
        loginResponse.setRefreshToken(jwtService.generateRefreshToken(user));

        return loginResponse;
    }

    public LoginResponse login(OAuthLoginRequest oAuthLoginRequest) {
        authValidation.validate(oAuthLoginRequest);
        FirebaseToken firebaseToken = firebaseService
                        .getFirebaseToken(oAuthLoginRequest.getFirebaseToken());
        UserRecord userRecord = firebaseService.getUser(firebaseToken.getUid());
        User user = userRepository.findByUid(userRecord.getUid()).orElseThrow(
                        () -> new UsernameNotFoundException("User not found"));
        return new LoginResponse(jwtService.generateToken(user),
                        jwtService.generateRefreshToken(user));
    }

    public SignupResponse signup(SignupRequest signupRequest) {
        authValidation.validate(signupRequest);
        FirebaseUser firebaseUser = firebaseService.createUser(signupRequest);
        if (firebaseUser == null) {
            throw new SignupException("User creation failed");
        }

        User user = modelMapper.map(signupRequest, User.class);
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setUid(firebaseUser.getUid());
        user.setUsername(firebaseUser.getUid());

        userRepository.save(user);
        return modelMapper.map(user, SignupResponse.class);
    }

    private long getUserId(String subject) {
        Optional<User> user = Optional.empty();
        if (isPhoneNumber(subject)) {
            user = userRepository.findByMobile(subject);
        } else if (isEmail(subject)) {
            user = userRepository.findByEmail(subject);
        } else if (isUsername(subject)) {
            user = userRepository.findByUsername(subject);
        }
        if (user.isPresent()) {
            return user.get().getId();
        }
        return 0;
    }

    public UserDetails loadUserByUsername(String username)
                    throws UsernameNotFoundException {
        try {
            long userId = Integer.parseInt(username);
            return userRepository.findById(userId).orElse(null);
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found");
        }
    }

    public LoginResponse refresh(String refreshToken) {
        String usernameOrMobile = jwtUtils.extractUserSubject(refreshToken);
        final User user = (User) loadUserByUsername(usernameOrMobile);
        jwtUtils.validateRefreshToken(user, refreshToken);
        LoginResponse authResponse = new LoginResponse();
        authResponse.setToken(jwtService.generateToken(user));
        authResponse.setRefreshToken(jwtService.generateRefreshToken(user));
        return authResponse;
    }

    public FirebaseOauthToken generateFirebaseToken(LoginRequest loginRequest) {
        authValidation.validateLogin(loginRequest);
        long userId = getUserId(loginRequest.getSubject());
        User user = userRepository.findById(userId).orElseThrow(
                        () -> new UsernameNotFoundException("User not found"));
        String s = firebaseService.generateToken(user.getUid());
        return new FirebaseOauthToken(s);
    }

    public void logout(LogoutRequest logoutRequest) {
        String usernameOrMobile = jwtUtils.extractUserSubject(logoutRequest.getToken());
        final User user = (User) loadUserByUsername(usernameOrMobile);
        if (StringUtils.isNotEmpty(user.getDeviceToken())) {
            user.setDeviceToken(null);
            userRepository.save(user);
        }
        jwtUtils.validateToken(user, logoutRequest.getToken());
        tokenRepository.removeUserToken(logoutRequest.getToken());
        if (StringUtils.isNotEmpty(logoutRequest.getRefreshToken())) {
            jwtUtils.validateRefreshToken(user, logoutRequest.getRefreshToken());
            tokenRepository.removeUserRefreshToken(logoutRequest.getRefreshToken());
        }
    }
}
