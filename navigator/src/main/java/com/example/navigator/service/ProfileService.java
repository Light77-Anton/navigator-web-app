package com.example.navigator.service;
import com.example.navigator.api.request.*;
import com.example.navigator.api.response.*;
import com.example.navigator.config.SecurityConfig;
import com.example.navigator.model.*;
import com.example.navigator.model.repository.*;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private LanguageToUserRepository languageToUserRepository;
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
    private VoteRepository voteRepository;
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
    private final String REGISTRATION_CONFIRMATION_MESSAGE_LOGIN = "REGISTRATION_CONFIRMATION_MESSAGE_LOGIN";
    private final String USER_NOT_FOUND = "USER_NOT_FOUND";
    private final String NO_INFO_EMPLOYEE = "NO_INFO_EMPLOYEE";
    private final String PROFESSIONS = "PROFESSIONS";

    public StringResponse getInfoFromEmployeeInEmployersLanguage(ProfessionToUserRequest professionToUserRequest) {
        StringResponse stringResponse = new StringResponse();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        EmployeeData employeeData = employeeDataRepository.findById(professionToUserRequest.getId()).get();
        for (InfoFromEmployee infoFromEmployee : employeeData.getInfoFromEmployee()) {
            for (Language employerLang : user.getCommunicationLanguages()) {
                if (infoFromEmployee.getLanguage().getId() == employerLang.getId()) {
                    stringResponse.setString(infoFromEmployee.getText());
                    return stringResponse;
                }
            }
        }

        return null;
    }

    public StringResponse getProfessionsToUserInEmployersLanguage(ProfessionToUserRequest professionToUserRequest) {
        StringResponse stringResponse = new StringResponse();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        List<ProfessionToUser> professionsToUser = professionToUserRepository.findAllByEmployeeId
                (professionToUserRequest.getId());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(checkAndGetMessageInSpecifiedLanguage(PROFESSIONS, user.getEndonymInterfaceLanguage()) + ": ");
        for (ProfessionToUser ptu : professionsToUser) {
            Optional<ProfessionName> professionName = professionNameRepository.
                    findByProfessionIdAndLanguage(ptu.getProfession().getId(), user.getEndonymInterfaceLanguage());
            stringResponse.setString(professionName.get().getProfessionName() + ",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        return stringResponse;
    }

    public ProfessionToUserResponse getProfessionToUser(ProfessionToUserRequest professionToUserRequest) {
        ProfessionToUserResponse professionToUserResponse = new ProfessionToUserResponse();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        Optional<ProfessionToUser> professionToUser = professionToUserRepository
                .findByEmployeeAndProfessionId(user.getId(), professionToUserRequest.getId());
        professionToUserResponse.setProfession(professionToUser.get().getProfession());
        professionToUserResponse.setEmployee(user.getEmployeeData());
        professionToUserResponse.setExtendedInfoFromEmployee(professionToUser.get().getExtendedInfoFromEmployee());

        return professionToUserResponse;
    }

    public ResultErrorsResponse postProfessionToUser(ProfessionToUserRequest professionToUserRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        List<ProfessionToUser> professionToUserList = professionToUserRepository.findAllByEmployeeId(user.getEmployeeData().getId());
        for (ProfessionToUser professionToUser : professionToUserList) {
            if (professionToUser.getProfession().getId() == professionToUserRequest.getId()) {
                professionToUserRepository.delete(professionToUser);
            }
        }
        ProfessionToUser professionToUser = new ProfessionToUser();
        professionToUser.setEmployee(employeeDataRepository.findById(user.getEmployeeData().getId()).get());
        professionToUser.setProfession(professionRepository.findById(professionToUserRequest.getId()).get());
        if (professionToUserRequest.getAdditionalInfo() != null) {
            professionToUser.setExtendedInfoFromEmployee(professionToUserRequest.getAdditionalInfo());
        }
        professionToUserRepository.save(professionToUser);
        resultErrorsResponse.setResult(true);

        return resultErrorsResponse;
    }

    public ResultErrorsResponse deleteProfessionToUser(ProfessionToUserRequest professionToUserRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        professionToUserRepository.deleteByEmployeeAndProfessionId(user.getEmployeeData().getId(),
                professionToUserRequest.getId());
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        resultErrorsResponse.setResult(true);

        return  resultErrorsResponse;
    }

    public UserInfoResponse getUserInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setId(user.getId());
        userInfoResponse.setAvatar(user.getAvatar());
        userInfoResponse.setBlocked(user.isBlocked());
        userInfoResponse.setEmail(user.getEmail());
        userInfoResponse.setUserLocation(user.getUserLocation());
        userInfoResponse.setRole(user.getRoleString());
        userInfoResponse.setName(user.getName());
        ArrayList<String> list = new ArrayList<>();
        for (Language lang : user.getCommunicationLanguages()) {
            list.add(lang.getLanguageEndonym());
        }
        userInfoResponse.setCommunicationLanguages(list);
        userInfoResponse.setPhone(user.getPhone());
        userInfoResponse.setLimitOfTheSearch(user.getLimitForTheSearch());
        userInfoResponse.setAreLanguagesMatched(user.isAreLanguagesMatched());
        userInfoResponse.setEmployeeData(user.getEmployeeData());
        userInfoResponse.setEmployerRequests(user.getEmployerRequests());
        userInfoResponse.setRanking(user.getRanking());
        userInfoResponse.setBlackList(user.getBlackList());
        userInfoResponse.setEndonymInterfaceLanguage(user.getEndonymInterfaceLanguage());
        userInfoResponse.setFavorites(user.getFavorites());
        userInfoResponse.setLastRequest(user.getLastRequest());
        userInfoResponse.setVotes(user.getVotes());
        userInfoResponse.setRegTime(user.getRegTime());
        userInfoResponse.setSocialNetworksLinks(user.getSocialNetworksLinks());
        userInfoResponse.setUserLocation(user.getUserLocation());

        return userInfoResponse;
    }

    public ExtendedUserInfoResponse getEmployeeInfo(StringRequest stringRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User employer = userRepository.findByEmail(username).get();
        ExtendedUserInfoResponse extendedUserInfoResponse = new ExtendedUserInfoResponse();
        Optional<User> employee = userRepository.findById((Long.parseLong(stringRequest.getString())));
        if (employee.isEmpty() || !employee.get().getRole().equals(Role.EMPLOYEE)) {
            extendedUserInfoResponse.setError(checkAndGetMessageInSpecifiedLanguage
                    (NO_INFO_EMPLOYEE, employer.getEndonymInterfaceLanguage()));
            return extendedUserInfoResponse;
        }
        User user = employee.get();
        extendedUserInfoResponse.setRating(user.getRanking());
        extendedUserInfoResponse.setStatus(user.getEmployeeData().getStatus());
        extendedUserInfoResponse.setName(user.getName());
        extendedUserInfoResponse.setEmail(user.getEmail());
        extendedUserInfoResponse.setPhone(user.getPhone());
        extendedUserInfoResponse.setAvatar(user.getAvatar());

        HashMap<String, String> professionsAndInfoMap = new HashMap();
        List<ProfessionToUser> employeesProfessionsList = professionToUserRepository.findAllByEmployeeId(user.getId());
        for (ProfessionToUser ptu : employeesProfessionsList) {
            if (ptu.getExtendedInfoFromEmployee() != null) {
                if (!ptu.getExtendedInfoFromEmployee().matches("\\s+")) {

                    professionsAndInfoMap.put(, );
                }
            }
        }
        extendedUserInfoResponse.setExtendedInfoFromEmployeeAboutProfessions(listWithExtendedInfo);

        return extendedUserInfoResponse;
    }

    public StringResponse activateAccount(Long userId) {
        User user = userRepository.findById(userId).get();
        StringResponse stringResponse = new StringResponse();
        user.setActivated(true);
        stringResponse.setString(checkAndGetMessageInSpecifiedLanguage(REGISTRATION_CONFIRMATION_MESSAGE_LOGIN
                , user.getEndonymInterfaceLanguage()));

        return stringResponse;
    }

    public ResultErrorsResponse setModerator() {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        List<String> errors = new ArrayList<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        if (user.getRole().equals(Role.EMPLOYEE)) {
            if (user.getEmployeeData().getJobs() != null) {
                errors.add(checkAndGetMessageInSpecifiedLanguage(MODERATOR_SETTING_REQUIREMENT, user.getEndonymInterfaceLanguage()));
                resultErrorsResponse.setErrors(errors);
                return resultErrorsResponse;
            }
            professionToUserRepository.deleteAllByEmployeeId(user.getId());
            user.setEmployeeData(null);
        }
        if (user.getRole().equals(Role.EMPLOYER)) {
            if (user.getEmployerRequests().getEmployerPassiveSearchData() != null || !user.getEmployerRequests().getJobs().isEmpty()) {
                errors.add(checkAndGetMessageInSpecifiedLanguage(MODERATOR_SETTING_REQUIREMENT, user.getEndonymInterfaceLanguage()));
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

    public JobListResponse getJobList() {
        JobListResponse jobListResponse = new JobListResponse();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        List<Job> jobs = user.getEmployerRequests().getJobs();
        jobListResponse.setJobCount(jobs.size());
        jobListResponse.setJobs(jobs);

        return jobListResponse;
    }

    public User getRecipient(long recipientId) {

        return userRepository.findById(recipientId).get();
    }

    public User getSender() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(username).get();
    }

    public VoteResponse vote(VoteRequest voteRequest) {
        VoteResponse voteResponse = new VoteResponse();
        Optional<User> user = userRepository.findById(voteRequest.getUserId());
        voteResponse.setValue(voteRequest.getValue());
        if (user.isEmpty()) {
            return voteResponse;
        }
        voteResponse.setUserId(voteRequest.getUserId());
        Vote newVote = new Vote();
        newVote.setValue(voteRequest.getValue());
        newVote.setUser(userRepository.findById(voteRequest.getUserId()).get());
        voteRepository.save(newVote);
        List<Byte> values = new ArrayList<>();
        for (Vote vote : voteRepository.findAllByUserId(voteRequest.getUserId())) {
            values.add(vote.getValue());
        }
        OptionalDouble avg = values.stream().mapToDouble(Byte::doubleValue).average();
        user.get().setRanking(avg.getAsDouble());
        userRepository.save(user.get());

        return voteResponse;
    }

    public ResultErrorsResponse comment(CommentRequest commentRequest) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
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

    public ResultErrorsResponse changeFavoritesList(long favoriteId, String decision) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
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
            List<String> errors = List.of(checkAndGetMessageInSpecifiedLanguage(SOMETHING_IS_WRONG, user.getEndonymInterfaceLanguage()));
            resultErrorsResponse.setErrors(errors);
        }

        return resultErrorsResponse;
    }

    public ResultErrorsResponse changeBlackList(long bannedId, String decision) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
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
            List<String> errors = List.of(checkAndGetMessageInSpecifiedLanguage(SOMETHING_IS_WRONG, user.getEndonymInterfaceLanguage()));
            resultErrorsResponse.setErrors(errors);
        }

        return resultErrorsResponse;
    }

    public DeleteAccountResponse deleteAccount() {
        DeleteAccountResponse deleteAccountResponse = new DeleteAccountResponse();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isPresent()) {
            deleteAccountResponse.setId(user.get().getId());
            if (user.get().getRole().equals(Role.EMPLOYEE)) {
                if (user.get().getEmployeeData().getJobs()!= null) {
                    deleteAccountResponse.setError(checkAndGetMessageInSpecifiedLanguage
                            (ACCOUNT_DELETION_REQUIREMENT, user.get().getEndonymInterfaceLanguage()));
                    return deleteAccountResponse;
                }
                employeeDataRepository.delete(user.get().getEmployeeData());
            }
            if (user.get().getRole().equals(Role.EMPLOYER)) {
                if (user.get().getEmployerRequests().getJobs() != null &&
                        user.get().getEmployerRequests().getEmployerPassiveSearchData() != null) {
                    deleteAccountResponse.setError(checkAndGetMessageInSpecifiedLanguage
                            (ACCOUNT_DELETION_REQUIREMENT, user.get().getEndonymInterfaceLanguage()));
                    return deleteAccountResponse;
                }
                employerRequestsRepository.delete(user.get().getEmployerRequests());
            }
            locationRepository.delete(user.get().getUserLocation());
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

    public ResultErrorsResponse checkEmailAndGetCode(StringRequest email) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        List<String> errors = new ArrayList<>();
        if (email == null) {
            errors.add("you need to write email where you want to send code");
            resultErrorsResponse.setErrors(errors);
            return resultErrorsResponse;
        }
        Optional<User> user = userRepository.findByEmail(email.getString());
        if (user.isEmpty()) {
            errors.add("user with such email is not found");
            resultErrorsResponse.setErrors(errors);
            return resultErrorsResponse;
        }
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("NavigatorApp");
        mail.setTo(email.getString());
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

    public ResultErrorsResponse checkAndChangeProfile(ProfileRequest profileRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        List<String> errorsList = new ArrayList<>();
        String name = profileRequest.getName();
        String phone = profileRequest.getPhone();
        String password = profileRequest.getPassword();
        String firmName = profileRequest.getFirmName();
        if (!checkName(name)) {
            errorsList.add(checkAndGetMessageInSpecifiedLanguage(NAMES_ARE_INCORRECT, user.getEndonymInterfaceLanguage()));
        }
        if (!phone.matches(PHONE_NUMBER_PATTERN)) {
            errorsList.add(checkAndGetMessageInSpecifiedLanguage(INCORRECT_PHONE, user.getEndonymInterfaceLanguage()));
        }
        if (password.length() < 6) {
            errorsList.add(checkAndGetMessageInSpecifiedLanguage(TOO_SHORT_PASSWORD, user.getEndonymInterfaceLanguage()));
        }
        if (profileRequest.getSocialNetworksLinks().length() > 30) {
            errorsList.add(SOCIAL_NETWORKS_TEXT_TOO_LONG);
        }
        if (profileRequest.getCommunicationLanguages() == null) {
            errorsList.add(checkAndGetMessageInSpecifiedLanguage
                    (COMMUNICATION_LANGUAGE_REQUIREMENT, user.getEndonymInterfaceLanguage()));
        } else {
            for (String languageName : profileRequest.getCommunicationLanguages()) {
                if (languageRepository.findByName(languageName).isEmpty()) {
                    errorsList.add(checkAndGetMessageInSpecifiedLanguage
                            (COMMUNICATION_LANGUAGE_REQUIREMENT, user.getEndonymInterfaceLanguage()) + languageName);
                }
            }
        }
        if (profileRequest.getInterfaceLanguage() == null) {
            errorsList.add(checkAndGetMessageInSpecifiedLanguage
                    (INTERFACE_LANGUAGE_REQUIREMENT, user.getEndonymInterfaceLanguage()));
        } else {
            if (languageRepository.findByName(profileRequest.getInterfaceLanguage()).isEmpty()) {
                errorsList.add(checkAndGetMessageInSpecifiedLanguage
                        (APP_DOES_NOT_HAVE_LANGUAGE, user.getEndonymInterfaceLanguage()) + profileRequest.getInterfaceLanguage());
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
        user.setEndonymInterfaceLanguage(profileRequest.getInterfaceLanguage());
        user.setName(name);
        user.setPhone(phone);
        user.setPassword(securityConfig.passwordEncoder().encode(password));
        user.setPhoneHidden(profileRequest.isPhoneHidden());
        user.setEmailHidden(profileRequest.isEmailHidden());
        user.setLimitForTheSearch(profileRequest.getLimit());
        user.setAreLanguagesMatched(profileRequest.isAreLanguagesMatched());
        if (user.getRole().equals(Role.EMPLOYEE)) {
            if (profileRequest.getEmployeesWorkRequirements() != null) {
                if (profileRequest.getEmployeesWorkRequirements().length() > 30) {
                    errorsList.add(checkAndGetMessageInSpecifiedLanguage
                            (EMPLOYEES_WORK_REQUIREMENTS_TEXT_TOO_LONG, user.getEndonymInterfaceLanguage()) + profileRequest.getInterfaceLanguage());
                    resultErrorsResponse.setErrors(errorsList);
                    return resultErrorsResponse;
                }
            }
            user.getEmployeeData().setEmployeesWorkRequirements(profileRequest.getEmployeesWorkRequirements());
            user.getEmployeeData().setDriverLicense(profileRequest.isDriverLicense());
            user.getEmployeeData().setAuto(profileRequest.isAuto());
        }
        if (user.getRole().equals(Role.EMPLOYER)) {
            if (firmName != null) {
                user.getEmployerRequests().setFirmName(firmName);
            }
        }
        userRepository.save(user);
        resultErrorsResponse.setResult(true);

        return resultErrorsResponse;
    }

    public AvatarResponse writeAvatar(MultipartFile avatar) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        AvatarResponse avatarResponse = new AvatarResponse();
        List<String> errorsList = new ArrayList<>();
        if (avatar.isEmpty()) {
            return avatarResponse;
        }
        if (avatar.getSize() > UPLOAD_LIMIT) {
           errorsList.add(checkAndGetMessageInSpecifiedLanguage(AVATAR_SIZE, user.getEndonymInterfaceLanguage()));
        }
        if (!avatar.getOriginalFilename().endsWith("jpg") && !avatar.getOriginalFilename().endsWith("png")) {
            errorsList.add(checkAndGetMessageInSpecifiedLanguage(AVATAR_FORMAT, user.getEndonymInterfaceLanguage()));
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

    public ResultErrorsResponse checkEmployeeStatus() {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        EmployeeData employeeData = user.getEmployeeData();
        if (employeeData.getStatus().equals("custom")) {
            long currentTimestamp = System.currentTimeMillis();
            if (currentTimestamp >= employeeData.getActiveStatusStartDate()) {
                employeeData.setStatus("active");
                employeeData.setActiveStatusStartDate(null);
                employeeDataRepository.save(employeeData);
            }
        }
        if (!employeeData.getStatus().equals("custom")) {
            resultErrorsResponse.setResult(true);
        }

        return resultErrorsResponse;
    }

    public ResultErrorsResponse employeeStatus(StatusRequest statusRequest) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        EmployeeData employeeData = user.getEmployeeData();
        if (statusRequest.getStatus().equals("active")) {
            employeeData.setStatus("active");
        } else if (statusRequest.getStatus().equals("inactive")) {
            employeeData.setStatus("inactive");
        } else {
            employeeData.setStatus("custom");
            employeeData.setActiveStatusStartDate(statusRequest.getTimestamp());
        }
        employeeDataRepository.save(employeeData);
        resultErrorsResponse.setResult(true);

        return resultErrorsResponse;
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

    public StringResponse getUsersInterfaceLanguage() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        StringResponse stringResponse = new StringResponse();
        stringResponse.setString(user.getEndonymInterfaceLanguage());

        return stringResponse;
    }
}