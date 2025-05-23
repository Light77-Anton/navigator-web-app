package com.example.navigator.controllers;
import com.example.navigator.api.request.LoginRequest;
import com.example.navigator.api.request.RegistrationRequest;
import com.example.navigator.api.response.*;
import com.example.navigator.service.AuthService;
import com.example.navigator.service.CaptchaService;
import com.example.navigator.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api/auth/")
public class AuthController {

    private AuthService authService;
    private CaptchaService captchaService;
    private ProfileService profileService;

    AuthController(AuthService authService, CaptchaService captchaService, ProfileService profileService) {
        this.authService = authService;
        this.captchaService = captchaService;
        this.profileService = profileService;
    }

    @GetMapping("check")
    public ResponseEntity<LoginResponse> authCheck() {

        return ResponseEntity.ok(authService.getAuthCheckResponse());
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        return ResponseEntity.ok(authService.getLoginResponse(loginRequest));
    }

    @GetMapping("logout")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work') or hasAuthority('user:moderate')")
    public ResponseEntity<ResultErrorsResponse> logout() {
        SecurityContextHolder.clearContext();
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        resultErrorsResponse.setResult(true);

        return ResponseEntity.ok(resultErrorsResponse);
    }

    @GetMapping("captcha")
    public ResponseEntity<CaptchaResponse> captcha() throws Exception{
        captchaService.deleteOldCaptchasFromRepository();

        return ResponseEntity.ok(captchaService.generateAndGetCaptcha());
    }

    @PostMapping("registration")
    public ResponseEntity<ResultErrorsResponse> registration(@RequestBody RegistrationRequest registrationRequest) {

        return ResponseEntity.ok(authService.checkProfileDataForRegistration(registrationRequest));
    }

    @DeleteMapping("delete")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work') or hasAuthority('user:moderate')")
    public ResponseEntity<DeleteAccountResponse> deleteAccount() { // по идее сначала должен быть вызван logout

        return ResponseEntity.ok(profileService.deleteAccount());
    }

    @PutMapping("account/activate/{id}")
    public ResponseEntity<StringResponse> activateAccount(@PathVariable("id") Long userId) {

        return ResponseEntity.ok(profileService.activateAccount(userId));
    }
}