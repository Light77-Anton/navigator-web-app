package com.example.navigator.service;
import com.example.navigator.api.request.CommentRequest;
import com.example.navigator.api.request.ModeratorDecision;
import com.example.navigator.api.request.PasswordRequest;
import com.example.navigator.api.request.ProfileRequest;
import com.example.navigator.api.response.AvatarResponse;
import com.example.navigator.api.response.DeleteAccountResponse;
import com.example.navigator.api.response.JobListResponse;
import com.example.navigator.api.response.ResultErrorsResponse;
import com.example.navigator.config.SecurityConfig;
import com.example.navigator.model.*;
import com.example.navigator.model.repository.*;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ProfileService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private ProfessionRepository professionRepository;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private EmployeeDataRepository employeeDataRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CaptchaRepository captchaRepository;
    @Autowired
    private FavoriteToUserRepository favoriteToUserRepository;
    @Autowired
    private BannedToUserRepository bannedToUserRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private InProgramMessageRepository inProgramMessageRepository;
    @Autowired
    private EmployerRequestsRepository employerRequestsRepository;
    @Autowired
    private ProfessionNameRepository professionNameRepository;
    @Autowired
    private ProfessionToUserRepository professionToUserRepository;
    @Autowired
    private SecurityConfig securityConfig;

    private static final long UPLOAD_LIMIT = 5242880;
    private static final String CHANGE_PASSWORD = "/login/change-password/";
    private final String PHONE_NUMBER_PATTERN = "^(\\+)?((\\d{2,3}) ?\\d|\\d)(([ -]?\\d)|( ?(\\d{2,3}) ?)){5,12}\\d$";
    private final String NAME_PATTERN_ONE = "[А-ЯЁ][а-яё]+\\s[А-ЯЁ][а-яё]+";
    private final String NAME_PATTERN_TWO = "[А-ЯЁ][а-яё]+[\\s|-]?[А-ЯЁ]?[а-яё]+\\s[А-ЯЁ][а-яё]+";
    private final String NAME_PATTERN_THREE = "[A-Z][a-z]+\\s[A-Z][a-z]+";
    private final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String DEFAULT_LANGUAGE = "English";
    private final String ACCOUNT_DELETION_REQUIREMENT = "ACCOUNT_DELETION_REQUIREMENT";
    private final String AVATAR_SIZE = "AVATAR_SIZE";
    private final String AVATAR_FORMAT = "AVATAR_FORMAT";
    private final String NAMES_ARE_INCORRECT = "NAMES_ARE_INCORRECT";
    private final String NOT_EMAIL = "NOT_EMAIL";
    private final String TOO_SHORT_PASSWORD = "TOO_SHORT_PASSWORD";
    private final String INCORRECT_PHONE = "INCORRECT_PHONE";
    private final String APP_DOES_NOT_HAVE_LANGUAGE = "APP_DOES_NOT_HAVE_LANGUAGE";
    private final String PROFESSION_NOT_FOUND = "PROFESSION_NOT_FOUND";
    private final String COMMUNICATION_LANGUAGE_REQUIREMENT = "COMMUNICATION_LANGUAGE_REQUIREMENT";
    private final String INTERFACE_LANGUAGE_REQUIREMENT = "INTERFACE_LANGUAGE_REQUIREMENT";
    private final String SOMETHING_IS_WRONG = "SOMETHING_IS_WRONG";
    private final String MODERATOR_SETTING_REQUIREMENT = "MODERATOR_SETTING_REQUIREMENT";
    private final String SOCIAL_NETWORKS_TEXT_TOO_LONG = "SOCIAL_NETWORKS_TEXT_TOO_LONG";
    private final String SPECIAL_EQUIPMENT_TEXT_TOO_LONG = "SPECIAL_EQUIPMENT_TEXT_TOO_LONG";
    private final String EMPLOYEES_WORK_REQUIREMENTS_TEXT_TOO_LONG = "EMPLOYEES_WORK_REQUIREMENTS_TEXT_TOO_LONG";


    public ResultErrorsResponse setModerator(Principal principal) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        List<String> errors = new ArrayList<>();
        User user = userRepository.findByEmail(principal.getName()).get();
        if (user.getRole().equals(Role.EMPLOYEE)) {
            if (user.getEmployeeData().getJobs() != null) {
                errors.add(checkAndGetMessageInSpecifiedLanguage(MODERATOR_SETTING_REQUIREMENT, user.getInterfaceLanguage()));
                resultErrorsResponse.setErrors(errors);
                return resultErrorsResponse;
            }
            professionToUserRepository.deleteAllByEmployeeId(user.getId());
            user.setEmployeeData(null);
        }
        if (user.getRole().equals(Role.EMPLOYER)) {
            if (user.getEmployerRequests().getEmployerPassiveSearchData() != null || !user.getEmployerRequests().getJobs().isEmpty()) {
                errors.add(checkAndGetMessageInSpecifiedLanguage(MODERATOR_SETTING_REQUIREMENT, user.getInterfaceLanguage()));
                resultErrorsResponse.setErrors(errors);
                return resultErrorsResponse;
            }
            user.setEmployerRequests(null);
        }
        user.setRole("Moderator");
        userRepository.save(user);
        resultErrorsResponse.setResult(true);

        return resultErrorsResponse;
    }

    public JobListResponse getJobList(Principal principal) {
        JobListResponse jobListResponse = new JobListResponse();
        User user = userRepository.findByEmail(principal.getName()).get();
        List<Job> jobs = user.getEmployerRequests().getJobs();
        jobListResponse.setJobCount(jobs.size());
        jobListResponse.setJobs(jobs);

        return jobListResponse;
    }

    public User getRecipient(long recipientId) {

        return userRepository.findById(recipientId).get();
    }

    public User getSender(Principal principal) {

        return userRepository.findByEmail(principal.getName()).get();
    }

    public ResultErrorsResponse comment(CommentRequest commentRequest, Principal principal) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        User user = userRepository.findByEmail(principal.getName()).get();
        Optional<Comment> comment = commentRepository.findByToIdAndFromId(user.getId(), commentRequest.getUserId());
        resultErrorsResponse.setResult(true);
        if (comment.isPresent()) {
            Comment currentComment = comment.get();
            currentComment.setContent(commentRequest.getContent());
            commentRepository.save(currentComment);

            return resultErrorsResponse;
        }
        Comment newComment = new Comment();
        newComment.setContent(commentRequest.getContent());
        newComment.setFromId(user.getId());
        newComment.setToId(commentRequest.getUserId());
        CommentId commentId = new CommentId();
        commentId.setFromId(user.getId());
        commentId.setToId(commentRequest.getUserId());
        newComment.setId(commentId);
        commentRepository.save(newComment);

        return resultErrorsResponse;
    }

    public ResultErrorsResponse changeFavoritesList(long favoriteId, String decision, Principal principal) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        User user = userRepository.findByEmail(principal.getName()).get();
        if (decision.equals("ADD") && userRepository.findById(favoriteId).isPresent()) {
            FavoriteToUser favoriteToUser = new FavoriteToUser();
            favoriteToUser.setUserId(user.getId());
            favoriteToUser.setFavoriteId(favoriteId);
            favoriteToUserRepository.save(favoriteToUser);
            resultErrorsResponse.setResult(true);
        } else if (decision.equals("REMOVE") && userRepository.findById(favoriteId).isPresent()) {
            favoriteToUserRepository.delete(user.getId(), favoriteId);
            resultErrorsResponse.setResult(true);
        } else {
            List<String> errors = List.of(checkAndGetMessageInSpecifiedLanguage(SOMETHING_IS_WRONG, user.getInterfaceLanguage()));
            resultErrorsResponse.setErrors(errors);
        }

        return resultErrorsResponse;
    }

    public ResultErrorsResponse changeBlackList(long bannedId, String decision, Principal principal) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        User user = userRepository.findByEmail(principal.getName()).get();
        if (decision.equals("ADD") && userRepository.findById(bannedId).isPresent()) {
            BannedToUser bannedToUser = new BannedToUser();
            bannedToUser.setUserId(user.getId());
            bannedToUser.setBannedId(bannedId);
            bannedToUserRepository.save(bannedToUser);
            resultErrorsResponse.setResult(true);
        } else if (decision.equals("REMOVE") && userRepository.findById(bannedId).isPresent()) {
            bannedToUserRepository.delete(user.getId(), bannedId);
            resultErrorsResponse.setResult(true);
        } else {
            List<String> errors = List.of(checkAndGetMessageInSpecifiedLanguage(SOMETHING_IS_WRONG, user.getInterfaceLanguage()));
            resultErrorsResponse.setErrors(errors);
        }

        return resultErrorsResponse;
    }

    public DeleteAccountResponse deleteAccount(Principal principal) {
        DeleteAccountResponse deleteAccountResponse = new DeleteAccountResponse();
        Optional<User> user = userRepository.findByEmail(principal.getName());
        if (user.isPresent()) {
            deleteAccountResponse.setId(user.get().getId());
            if (user.get().getRole().equals(Role.EMPLOYEE)) {
                if (user.get().getEmployeeData().getJobs()!= null) {
                    deleteAccountResponse.setError(checkAndGetMessageInSpecifiedLanguage
                            (ACCOUNT_DELETION_REQUIREMENT, user.get().getInterfaceLanguage()));
                    return deleteAccountResponse;
                }
                employeeDataRepository.delete(user.get().getEmployeeData());
            }
            if (user.get().getRole().equals(Role.EMPLOYER)) {
                if (user.get().getEmployerRequests().getJobs() != null &&
                        user.get().getEmployerRequests().getEmployerPassiveSearchData() != null) {
                    deleteAccountResponse.setError(checkAndGetMessageInSpecifiedLanguage
                            (ACCOUNT_DELETION_REQUIREMENT, user.get().getInterfaceLanguage()));
                    return deleteAccountResponse;
                }
                employerRequestsRepository.delete(user.get().getEmployerRequests());
            }
            locationRepository.delete(user.get().getLocation());
            userRepository.delete(user.get());
            deleteAccountResponse.setResult(true);
            return deleteAccountResponse;
        }

        return deleteAccountResponse;
    }

    public ResultErrorsResponse checkAndChangePassword(PasswordRequest passwordRequest) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        List<String> errors = new ArrayList<>();
        if (passwordRequest.getPassword() == null || passwordRequest.getCode() == null ||
                passwordRequest.getCaptcha() == null || passwordRequest.getCaptchaSecret() == null) {
            errors.add("You need to enter code from the picture,recovery code and your new password");
            resultErrorsResponse.setErrors(errors);
            return resultErrorsResponse;
        }
        if (passwordRequest.getPassword().length() < 6) {
            errors.add("Password is too short");
            resultErrorsResponse.setErrors(errors);
            return resultErrorsResponse;
        }
        Optional<Captcha> captchaCode = captchaRepository
                .findBySecretCodeEquals(passwordRequest.getCaptchaSecret());
        if (captchaCode.isPresent()) {
            if (captchaCode.get().getCode().equals(passwordRequest.getCaptcha())) {
                Optional<User> user = userRepository.findByCode(passwordRequest.getCode());
                if (user.isPresent()) {
                    userRepository.findByCodeAndUpdatePassword(passwordRequest.getCode(),
                            securityConfig.passwordEncoder().encode(passwordRequest.getPassword()));
                } else {
                    errors.add("Such recovery code is not found");
                }
            } else {
                errors.add("Entered code from captcha is incorrect");
            }
        } else {
            errors.add("Secret code is incorrect");
        }
        if (!errors.isEmpty()) {
            resultErrorsResponse.setErrors(errors);
            return resultErrorsResponse;
        }
        resultErrorsResponse.setResult(true);

        return resultErrorsResponse;
    }

    public ResultErrorsResponse checkEmailAndGetCode(String email) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        List<String> errors = new ArrayList<>();
        if (email == null) {
            errors.add("you need to write email where you want to send code");
            resultErrorsResponse.setErrors(errors);
            return resultErrorsResponse;
        }
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            errors.add("user with such email is not found");
            resultErrorsResponse.setErrors(errors);
            return resultErrorsResponse;
        }
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("NavigatorApp");
        mail.setTo(email);
        mail.setSubject("Request for recovery code");
        char[] availableChars = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder hash = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 45; i++) {
            hash.append(availableChars[random.nextInt(availableChars
                    .length)]);
        }
        mail.setText(CHANGE_PASSWORD + hash.toString());
        javaMailSender.send(mail);
        userRepository.addRestoreCode(user.get().getId(), hash.toString());
        resultErrorsResponse.setResult(true);

        return resultErrorsResponse;
    }

    public ResultErrorsResponse changeUserCondition(ModeratorDecision moderatorDecision) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        userRepository.changeUserCondition(moderatorDecision.getId(), moderatorDecision.getDecision());
        resultErrorsResponse.setResult(true);

        return resultErrorsResponse;
    }

    public ResultErrorsResponse checkAndChangeProfile(ProfileRequest profileRequest, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).get();
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        List<String> errorsList = new ArrayList<>();
        String name = profileRequest.getName();
        String email = profileRequest.getEmail();
        String phone = profileRequest.getPhone();
        String password = profileRequest.getPassword();
        if (!checkName(name)) {
            errorsList.add(checkAndGetMessageInSpecifiedLanguage(NAMES_ARE_INCORRECT, user.getInterfaceLanguage()));
        }
        if (!email.contains("@")) {
            errorsList.add(checkAndGetMessageInSpecifiedLanguage(NOT_EMAIL, user.getInterfaceLanguage()));
        }
        if (!phone.matches(PHONE_NUMBER_PATTERN)) {
            errorsList.add(checkAndGetMessageInSpecifiedLanguage(INCORRECT_PHONE, user.getInterfaceLanguage()));
        }
        if (password.length() < 6) {
            errorsList.add(checkAndGetMessageInSpecifiedLanguage(TOO_SHORT_PASSWORD, user.getInterfaceLanguage()));
        }
        if (profileRequest.getSocialNetworksLinks().length() > 30) {
            errorsList.add(SOCIAL_NETWORKS_TEXT_TOO_LONG);
        }
        if (profileRequest.getCommunicationLanguages() == null) {
            errorsList.add(checkAndGetMessageInSpecifiedLanguage
                    (COMMUNICATION_LANGUAGE_REQUIREMENT, user.getInterfaceLanguage()));
        } else {
            for (String languageName : profileRequest.getCommunicationLanguages()) {
                if (languageRepository.findByName(languageName).isEmpty()) {
                    errorsList.add(checkAndGetMessageInSpecifiedLanguage
                            (COMMUNICATION_LANGUAGE_REQUIREMENT, user.getInterfaceLanguage()) + languageName);
                }
            }
        }
        if (profileRequest.getInterfaceLanguage() == null) {
            errorsList.add(checkAndGetMessageInSpecifiedLanguage
                    (INTERFACE_LANGUAGE_REQUIREMENT, user.getInterfaceLanguage()));
        } else {
            if (languageRepository.findByName(profileRequest.getInterfaceLanguage()).isEmpty()) {
                errorsList.add(checkAndGetMessageInSpecifiedLanguage
                        (APP_DOES_NOT_HAVE_LANGUAGE, user.getInterfaceLanguage()) + profileRequest.getInterfaceLanguage());
            }
        }
        if (!errorsList.isEmpty()) {
            resultErrorsResponse.setErrors(errorsList);
            return resultErrorsResponse;
        }
        List<Language> languages = new ArrayList<>();
        for (String lang : profileRequest.getCommunicationLanguages()) {
            languages.add(languageRepository.findByName(lang).get());
        }
        user.setCommunicationLanguages(languages);
        user.setInterfaceLanguage(profileRequest.getInterfaceLanguage());
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(securityConfig.passwordEncoder().encode(password));
        if (user.getRole().equals(Role.EMPLOYEE)) {
            String status = profileRequest.getStatus();
            if (status.equals("Employee will be active since ") || status.equals("Employee will be active until ")) {
                LocalDateTime ldt = LocalDateTime.ofInstant
                        (Instant.ofEpochMilli(profileRequest.getStartActivityDateTimestamp()),
                                TimeZone.getDefault().toZoneId());
                user.getEmployeeData().setStatus(status + ldt.format(FORMAT));
            } else {
                user.getEmployeeData().setStatus(status);
            }
            List<Profession> professions = new ArrayList<>();
            if (profileRequest.getProfessionsAndExtendedInfo() != null) {
                for (String professionAndExtendedInfo : profileRequest.getProfessionsAndExtendedInfo()) {
                    String[] array = professionAndExtendedInfo.split(":", 2);
                    if (professionNameRepository.findByName(array[0]).isEmpty()) {
                        errorsList.add(checkAndGetMessageInSpecifiedLanguage
                                (PROFESSION_NOT_FOUND, user.getInterfaceLanguage()) + profileRequest.getInterfaceLanguage());
                        resultErrorsResponse.setErrors(errorsList);
                        return resultErrorsResponse;
                    }
                    Profession profession = professionNameRepository.findByName(array[0]).get().getProfession();
                    professions.add(profession);
                    if (array[1] != null) {
                        if (!array[1].matches("\\s+")) {
                            professionToUserRepository.setExtendedInfoByEmployeeAndProfessionId(user.getId(),
                                    profession.getId(), array[1]);
                        }
                    }
                }
            }
            if (profileRequest.getEmployeesWorkRequirements() != null) {
                if (profileRequest.getEmployeesWorkRequirements().length() > 30) {
                    errorsList.add(checkAndGetMessageInSpecifiedLanguage
                            (EMPLOYEES_WORK_REQUIREMENTS_TEXT_TOO_LONG, user.getInterfaceLanguage()) + profileRequest.getInterfaceLanguage());
                    resultErrorsResponse.setErrors(errorsList);
                    return resultErrorsResponse;
                }
            }
            if (profileRequest.getSpecialEquipment() != null) {
                if (profileRequest.getSpecialEquipment().length() > 30) {
                    errorsList.add(checkAndGetMessageInSpecifiedLanguage
                            (SPECIAL_EQUIPMENT_TEXT_TOO_LONG, user.getInterfaceLanguage()) + profileRequest.getInterfaceLanguage());
                    resultErrorsResponse.setErrors(errorsList);
                    return resultErrorsResponse;
                }
            }
            user.getEmployeeData().setProfessions(professions);
            user.getEmployeeData().setEmployeesWorkRequirements(profileRequest.getEmployeesWorkRequirements());
            user.getEmployeeData().setDriverLicense(profileRequest.isDriverLicense());
            user.getEmployeeData().setAuto(profileRequest.isAuto());
        }
        userRepository.save(user);
        resultErrorsResponse.setResult(true);

        return resultErrorsResponse;
    }

    public AvatarResponse writeAvatar(MultipartFile avatar, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).get();
        AvatarResponse avatarResponse = new AvatarResponse();
        List<String> errorsList = new ArrayList<>();
        if (avatar.isEmpty()) {
            return avatarResponse;
        }
        if (avatar.getSize() > UPLOAD_LIMIT) {
           errorsList.add(checkAndGetMessageInSpecifiedLanguage(AVATAR_SIZE, user.getInterfaceLanguage()));
        }
        if (!avatar.getOriginalFilename().endsWith("jpg") && !avatar.getOriginalFilename().endsWith("png")) {
            errorsList.add(checkAndGetMessageInSpecifiedLanguage(AVATAR_FORMAT, user.getInterfaceLanguage()));
        }
        if (!errorsList.isEmpty()) {
            avatarResponse.setErrors(errorsList);
            return avatarResponse;
        }
        String extension = FilenameUtils.getExtension(avatar.getOriginalFilename());
        try {
            BufferedImage bufferedImage = ImageIO.read(avatar.getInputStream());
            BufferedImage editedImage = Scalr.resize(bufferedImage, Scalr.Mode.FIT_EXACT,36,36);
            String pathToImage = "navigator/avatars/id" + user.getId() + "avatar." + extension;
            Path path = Paths.get(pathToImage);
            if (userRepository.findAvatarPath(user.getId()).isPresent()) {
                Files.deleteIfExists(Paths.get(userRepository.findAvatarPath(user.getId()).get()));
            }
            ImageIO.write(editedImage, extension, path.toFile());
            user.setAvatar(pathToImage);
            userRepository.save(user);
            avatarResponse.setPathToFile(pathToImage);
            avatarResponse.setResult(true);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return avatarResponse;
    }

    private boolean checkName(String name) {
        if (name.matches(NAME_PATTERN_ONE) || name.matches(NAME_PATTERN_TWO) || name.matches(NAME_PATTERN_THREE)) {
            return true;
        }

        return false;
    }

    private String checkAndGetMessageInSpecifiedLanguage(String codeName, String interfaceLanguage) {
        Optional<InProgramMessage> inProgramMessage = inProgramMessageRepository
                .findByCodeNameAndLanguage(codeName, interfaceLanguage);
        if (inProgramMessage.isPresent()) {
            return inProgramMessage.get().getMessage();
        }
        return inProgramMessageRepository.findByCodeNameAndLanguage(codeName, DEFAULT_LANGUAGE).get().getMessage();
    }
}
