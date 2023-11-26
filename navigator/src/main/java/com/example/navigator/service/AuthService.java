package com.example.navigator.service;
import com.example.navigator.api.request.LoginRequest;
import com.example.navigator.api.request.RegistrationRequest;
import com.example.navigator.api.response.LoginResponse;
import com.example.navigator.api.response.ResultErrorsResponse;
import com.example.navigator.config.SecurityConfig;
import com.example.navigator.model.*;
import com.example.navigator.model.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private SecurityConfig securityConfig;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private EmployeeDataRepository employeeDataRepository;
    @Autowired
    private CaptchaRepository captchaRepository;
    @Autowired
    private ProfessionRepository professionRepository;
    @Autowired
    private ProfessionNameRepository professionNameRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private InProgramMessageRepository inProgramMessageRepository;
    @Autowired
    private EmployerRequestsRepository employerRequestsRepository;

    private final AuthenticationManager authenticationManager;
    private final String PHONE_NUMBER_PATTERN = "^(\\+)?((\\d{2,3}) ?\\d|\\d)(([ -]?\\d)|( ?(\\d{2,3}) ?)){5,12}\\d$";
    private final String NAME_PATTERN_ONE = "[А-ЯЁ][а-яё]+\\s[А-ЯЁ][а-яё]+";
    private final String NAME_PATTERN_TWO = "[А-ЯЁ][а-яё]+[\\s|-]?[А-ЯЁ]?[а-яё]+\\s[А-ЯЁ][а-яё]+";
    private final String NAME_PATTERN_THREE = "[A-Z][a-z]+\\s[A-Z][a-z]+";
    private final String FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final String DEFAULT_LANGUAGE = "English";
    private final String PASSWORDS_ARE_NOT_EQUALS = "PASSWORDS_ARE_NOT_EQUALS";
    private final String ACCOUNT_IS_BANNED_MESSAGE_CODE = "ACCOUNT_IS_BANNED_MESSAGE";
    private final String INCORRECT_ENTERED_CAPTCHA = "Entered code from captcha is incorrect";
    private final String CAPTCHA_IS_NOT_EXIST = "Captcha is not exist, possibly it is expired";
    private final String NAMES_ARE_INCORRECT = "First name and last name are incorrect";
    private final String USER_NOT_FOUND = "USER_NOT_FOUND";
    private final String NOT_EMAIL = "It is not email";
    private final String ACCOUNT_NOT_ACTIVATED = "ACCOUNT_NOT_ACTIVATED";
    private final String TOO_SHORT_PASSWORD = "Password is too short";
    private final String INCORRECT_PHONE = "Phone number is incorrect";
    private final String APP_DOES_NOT_HAVE_LANGUAGE = "App does not have specified language : ";
    private final String PROFESSION_NOT_FOUND = "Such profession is not exist";
    private final String INTERFACE_LANGUAGE_REQUIREMENT = "You must chose interface language";
    private final String COMMUNICATION_LANGUAGE_REQUIREMENT = "You must chose at least 1 communication language";
    private final String SOCIAL_NETWORKS_TEXT_TOO_LONG =
            "Text with social networks links is too long; it must be no more then 30 symbols";
    private final String SPECIAL_EQUIPMENT_TEXT_TOO_LONG =
            "Text about special equipment is too long; it must be no more then 30 symbols";
    private final String EMPLOYEES_WORK_REQUIREMENTS_TEXT_TOO_LONG =
            "Text about work requirements is too long; it must be no more then 30 symbols";
    private final String REGISTRATION_CONFIRMATION_MESSAGE_EMAIL = "REGISTRATION_CONFIRMATION_MESSAGE_EMAIL";

    public AuthService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public LoginResponse getAuthCheckResponse() {
        LoginResponse loginResponse = new LoginResponse();
        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userRepository.findByEmail(username).get();
            loginResponse.setResult(true);
            loginResponse.setUserId(currentUser.getId());
            loginResponse.setRole(currentUser.getRoleString());
            return loginResponse;
        }
        /*
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        if (userRepository.userIsBlocked(currentUser.getId()) == 1) {
            Optional<InProgramMessage> inProgramMessage = inProgramMessageRepository.findByCodeNameAndLanguage
                    (ACCOUNT_IS_BANNED_MESSAGE_CODE, currentUser.getEndonymInterfaceLanguage());
            if (inProgramMessage.isPresent()) {
                loginResponse.setBlockMessage(inProgramMessage.get().getMessage());
            } else {
                loginResponse.setBlockMessage(inProgramMessageRepository.findByCodeNameAndLanguage
                        (ACCOUNT_IS_BANNED_MESSAGE_CODE, DEFAULT_LANGUAGE).get().getMessage());
            }
            loginResponse.setUserId(currentUser.getId());
            return loginResponse;
        }
         */

        return loginResponse;
    }

    public LoginResponse getLoginResponse(LoginRequest loginRequest) {
        LoginResponse loginResponse = new LoginResponse();
        Optional<User> requestedUser = userRepository.findByEmail(loginRequest.getEmail());
        if (requestedUser.isEmpty()) {
            loginResponse.setBlockMessage(checkAndGetMessageInSpecifiedLanguage(USER_NOT_FOUND, DEFAULT_LANGUAGE));
            return loginResponse;
        } else if (!requestedUser.get().isActivated()) {
            loginResponse.setBlockMessage(checkAndGetMessageInSpecifiedLanguage(ACCOUNT_NOT_ACTIVATED, DEFAULT_LANGUAGE));
            return loginResponse;
        } else if(requestedUser.get().isBlocked()) {
            loginResponse.setBlockMessage(checkAndGetMessageInSpecifiedLanguage(ACCOUNT_IS_BANNED_MESSAGE_CODE, DEFAULT_LANGUAGE));
            return loginResponse;
        } else {
            Authentication auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                            loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(auth);
            org.springframework.security.core.userdetails.User user =
                    (org.springframework.security.core.userdetails.User) auth.getPrincipal();
            loginResponse.setResult(true);
            User currentUser = userRepository.findByEmail(user.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException(user.getUsername()));
            loginResponse.setUserId(currentUser.getId());
            loginResponse.setUserRole(currentUser.getRoleString());
            loginResponse.setRole(currentUser.getRoleString());

            return loginResponse;
        }
    }

    public ResultErrorsResponse checkProfileDataForRegistration(RegistrationRequest registrationRequest) {
        List<String> errorsList = new ArrayList<>();
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        Optional<Captcha> captcha = captchaRepository.findBySecretCodeEquals(registrationRequest.getSecretCode());
        if (captcha.isPresent()) {
            if (!captcha.get().getCode().equals(registrationRequest.getCode())) {
                errorsList.add(INCORRECT_ENTERED_CAPTCHA);
                resultErrorsResponse.setErrors(errorsList);
                return resultErrorsResponse;
            }
        } else {
            errorsList.add(CAPTCHA_IS_NOT_EXIST);
            resultErrorsResponse.setErrors(errorsList);
            return resultErrorsResponse;
        }
        if (!registrationRequest.getPassword().equals(registrationRequest.getRepeatedPassword())) {
            errorsList.add(PASSWORDS_ARE_NOT_EQUALS);
        }
        if (!checkName(registrationRequest.getName())) {
            errorsList.add(NAMES_ARE_INCORRECT);
        }
        if (!registrationRequest.getEmail().contains("@")) {
            errorsList.add(NOT_EMAIL);
        }
        if (registrationRequest.getPassword().length() < 6) {
            errorsList.add(TOO_SHORT_PASSWORD);
        }
        if (registrationRequest.getPhone() != null) {
            if (!registrationRequest.getPhone()
                    .matches(PHONE_NUMBER_PATTERN)) {
                errorsList.add(INCORRECT_PHONE);
            }
        }
        if (registrationRequest.getSocialNetworksLinks().length() > 30) {
            errorsList.add(SOCIAL_NETWORKS_TEXT_TOO_LONG);
        }
        if (registrationRequest.getCommunicationLanguage() == null
                || registrationRequest.getCommunicationLanguage().isEmpty()) {
            errorsList.add(COMMUNICATION_LANGUAGE_REQUIREMENT);
        } else if (registrationRequest.getInterfaceLanguage() == null) {
            errorsList.add(INTERFACE_LANGUAGE_REQUIREMENT);
        } else {
            if (languageRepository.findByName(registrationRequest.getCommunicationLanguage()).isEmpty()) {
                errorsList.add(APP_DOES_NOT_HAVE_LANGUAGE + registrationRequest.getCommunicationLanguage());
            }
            if (languageRepository.findByName(registrationRequest.getInterfaceLanguage()).isEmpty()) {
                errorsList.add(APP_DOES_NOT_HAVE_LANGUAGE + registrationRequest.getInterfaceLanguage());
            }
        }
        if (!errorsList.isEmpty()) {
            resultErrorsResponse.setErrors(errorsList);
            return resultErrorsResponse;
        }
        EmployeeData employeeData = null;
        EmployerRequests employerRequests = null;
        User user = new User();
        List<Language> languages = new ArrayList<>();
        languages.add(languageRepository.findByName(registrationRequest.getCommunicationLanguage()).get());
        user.setCommunicationLanguages(languages);
        user.setEndonymInterfaceLanguage(registrationRequest.getInterfaceLanguage());
        user.setEmail(registrationRequest.getEmail());
        user.setEmailHidden(registrationRequest.isEmailHidden());
        user.setName(registrationRequest.getName());
        user.setPhoneHidden(registrationRequest.isPhoneHidden());
        user.setPassword(securityConfig.passwordEncoder().encode(registrationRequest.getPassword()));
        user.setPhone(registrationRequest.getPhone());
        user.setRegTime(LocalDateTime.now());
        user.setBlocked(false);
        user.setActivated(false);
        user.setSocialNetworksLinks(registrationRequest.getSocialNetworksLinks());
        if (registrationRequest.getRole().equals("Employer")) {
            user.setRole("Employer");
            employerRequests = new EmployerRequests();
            employerRequests.setEmployer(user);
            employerRequests.setJobs(new ArrayList<>());
            user.setEmployerRequests(employerRequests);
        } else {
            user.setRole("Employee");
            employeeData = new EmployeeData();
            employeeData.setStatus("INACTIVE");
            employeeData.setDriverLicense(registrationRequest.isDriverLicense());
            employeeData.setAuto(registrationRequest.isAuto());
            employeeData.setEmployee(user);
            user.setEmployeeData(employeeData);
        }
        UserLocation userLocation = new UserLocation();
        userLocation.setLatitude(registrationRequest.getLatitude());
        userLocation.setLongitude(registrationRequest.getLongitude());
        userLocation.setCity(registrationRequest.getCity());
        userLocation.setCountry(registrationRequest.getCountry());
        userLocation.setUser(user);
        user.setUserLocation(userLocation);
        userRepository.save(user);
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("NavigatorApp");
        mail.setTo(registrationRequest.getEmail());
        mail.setSubject("Registration confirmation");
        User registratedUser = userRepository.findByEmail(registrationRequest.getEmail()).get();
        mail.setText(checkAndGetMessageInSpecifiedLanguage(REGISTRATION_CONFIRMATION_MESSAGE_EMAIL + "\n"
                        + "http://localhost:8080/api/auth/account/activate/" + registratedUser.getId(), // cделать ссылку!!!
                registrationRequest.getInterfaceLanguage()));
        javaMailSender.send(mail);
        if (employeeData != null) {
            employeeDataRepository.save(employeeData);
        }
        if (employerRequests != null) {
            employerRequestsRepository.save(employerRequests);
        }
        locationRepository.save(userLocation);
        resultErrorsResponse.setResult(true);

        return resultErrorsResponse;
    }

    private String checkAndGetMessageInSpecifiedLanguage(String codeName, String interfaceLanguage) {
        Optional<InProgramMessage> inProgramMessage = inProgramMessageRepository
                .findByCodeNameAndLanguage(codeName, interfaceLanguage);
        if (inProgramMessage.isPresent()) {
            return inProgramMessage.get().getMessage();
        }
        return inProgramMessageRepository.findByCodeNameAndLanguage(codeName, DEFAULT_LANGUAGE).get().getMessage();
    }

    private boolean checkName(String name) {
        if (name.matches(NAME_PATTERN_ONE) || name.matches(NAME_PATTERN_TWO) || name.matches(NAME_PATTERN_THREE)) {
            return true;
        }

        return false;
    }
}
