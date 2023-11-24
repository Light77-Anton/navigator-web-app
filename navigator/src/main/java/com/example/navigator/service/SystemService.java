package com.example.navigator.service;
import com.example.navigator.api.request.InProgramMessageRequest;
import com.example.navigator.api.request.LocationRequest;
import com.example.navigator.api.request.ProfessionRequest;
import com.example.navigator.api.request.TextListInSpecifiedLanguageRequest;
import com.example.navigator.api.response.ProfessionsResponse;
import com.example.navigator.api.response.ResultErrorsResponse;
import com.example.navigator.api.response.StringResponse;
import com.example.navigator.api.response.TextListResponse;
import com.example.navigator.model.*;
import com.example.navigator.model.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class SystemService {

    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private InProgramMessageRepository inProgramMessageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageCodeNameRepository messageCodeNameRepository;
    @Autowired
    private ProfessionNameRepository professionNameRepository;
    @Autowired
    private ProfessionRepository professionRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private JobRepository jobRepository;

    private final String DEFAULT_LANGUAGE = "English";
    private final String LANGUAGE_IS_NOT_EXIST = "LANGUAGE_IS_NOT_EXIST";
    private final String CODE_NAME_IS_NOT_EXIST = "CODE_NAME_IS_NOT_EXIST";
    private final String IN_PROGRAM_MESSAGE_EXISTS_ALREADY = "IN_PROGRAM_MESSAGE_EXISTS_ALREADY";
    private final String CODE_NAME_EXISTS_ALREADY = "CODE_NAME_EXISTS_ALREADY";
    private final String PROFESSION_ALREADY_EXISTS = "PROFESSION_ALREADY_EXISTS";
    private final String APP_DOES_NOT_HAVE_LANGUAGE = "APP_DOES_NOT_HAVE_LANGUAGE";
    private final String PROFESSION_NOT_FOUND = "PROFESSION_NOT_FOUND";

    public ProfessionsResponse getProfessionsList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        ProfessionsResponse professionsResponse = new ProfessionsResponse();
        professionsResponse.setList(professionNameRepository.findAllBySpecifiedLanguage(user.getEndonymInterfaceLanguage())
                .stream().map(ProfessionName::getProfessionName).collect(Collectors.toList()));

        return professionsResponse;
    }

    //@Scheduled(fixedRate = 150000) // под вопросом,возможно это можно будет реализовать на фронте
    public ResultErrorsResponse checkAndDeleteNotConfirmedJobs() {
        jobRepository.deleteAllNotConfirmedJobsByExpirationTime(System.currentTimeMillis());
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        resultErrorsResponse.setResult(true);

        return resultErrorsResponse;
    }


    public ResultErrorsResponse updateLocation(LocationRequest locationRequest) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        resultErrorsResponse.setResult(true);
        locationRepository.updateLocation(locationRequest.getLatitude(), locationRequest.getLongitude(), locationRequest.getId());

        return resultErrorsResponse;
    }

    public TextListResponse getLanguagesList() {
        TextListResponse textListResponse = new TextListResponse();
        textListResponse.setList(languageRepository.findAll().stream()
                .map(Language::getLanguageEndonym).collect(Collectors.toList()));

        return textListResponse;
    }

    public HashMap<String, String> checkAndGetTextListInSpecifiedLanguage(TextListInSpecifiedLanguageRequest textList) {
        HashMap<String, String> map = new HashMap<>();
        String interfaceLanguage = textList.getLanguage();
        for (String codeName : textList.getCodeNameList()) {
            Optional<InProgramMessage> inProgramMessage = inProgramMessageRepository
                    .findByCodeNameAndLanguage(codeName, interfaceLanguage);
            if (inProgramMessage.isPresent()) {
                map.put(codeName, inProgramMessage.get().getMessage());
            } else {
                map.put(codeName,
                        inProgramMessageRepository.findByCodeNameAndLanguage(codeName, DEFAULT_LANGUAGE).get().getMessage());
            }
        }

        return map;
    }

    public ResultErrorsResponse addNewProfessionId() {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        Profession profession = new Profession();
        professionRepository.save(profession);
        resultErrorsResponse.setResult(true);

        return resultErrorsResponse;
    }

    public ResultErrorsResponse addProfessionInSpecifiedLanguage(ProfessionRequest professionRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        List<String> errors = new ArrayList<>();
        Optional<Profession> profession = professionRepository.findById(professionRequest.getProfessionId());
        Optional<Language> language = languageRepository.findByName(professionRequest.getLanguage());
        if (language.isEmpty()) {
            errors.add(checkAndGetMessageInSpecifiedLanguage(APP_DOES_NOT_HAVE_LANGUAGE, user.getEndonymInterfaceLanguage()));
        }
        if (profession.isEmpty()) {
            errors.add(checkAndGetMessageInSpecifiedLanguage(PROFESSION_NOT_FOUND, user.getEndonymInterfaceLanguage()));
        }
        if (professionNameRepository.findByNameAndLanguage(professionRequest.getProfession(),
                professionRequest.getLanguage()).isPresent()) {
            errors.add(checkAndGetMessageInSpecifiedLanguage(PROFESSION_ALREADY_EXISTS, user.getEndonymInterfaceLanguage()));
        }
        if (!errors.isEmpty()) {
            resultErrorsResponse.setErrors(errors);
            return resultErrorsResponse;
        }
        ProfessionName professionName = new ProfessionName();
        professionName.setLanguage(language.get());
        professionName.setProfessionName(professionName.getProfessionName());
        professionName.setProfession(profession.get());
        professionNameRepository.save(professionName);
        resultErrorsResponse.setResult(true);

        return resultErrorsResponse;
    }

    public ResultErrorsResponse addLanguage(String language) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        resultErrorsResponse.setResult(true);
        if (languageRepository.findByName(language).isPresent()) {
            return resultErrorsResponse;
        }
        Language newLanguage = new Language();
        newLanguage.setLanguageEndonym(language);
        languageRepository.save(newLanguage);

        return resultErrorsResponse;
    }

    public ResultErrorsResponse addMessageCodeName(String codeName) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        List<String> errors = new ArrayList<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        if (messageCodeNameRepository.findByName(codeName).isPresent()) {
            errors.add(checkAndGetMessageInSpecifiedLanguage(CODE_NAME_EXISTS_ALREADY, user.getEndonymInterfaceLanguage()));
            resultErrorsResponse.setErrors(errors);
            return resultErrorsResponse;
        }
        MessagesCodeName messagesCodeName = new MessagesCodeName();
        messagesCodeName.setCodeName(codeName);
        messageCodeNameRepository.save(messagesCodeName);
        resultErrorsResponse.setResult(true);

        return resultErrorsResponse;
    }

    public ResultErrorsResponse addInProgramMessage(InProgramMessageRequest inProgramMessageRequest) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        List<String> errors = new ArrayList<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        String language = inProgramMessageRequest.getLanguage();
        String message = inProgramMessageRequest.getMessage();
        String codeName = inProgramMessageRequest.getCodeName();
        if (languageRepository.findByName(language).isEmpty()) {
            errors.add(checkAndGetMessageInSpecifiedLanguage(LANGUAGE_IS_NOT_EXIST, user.getEndonymInterfaceLanguage()));
            resultErrorsResponse.setErrors(errors);
            return resultErrorsResponse;
        }
        if (messageCodeNameRepository.findByName(codeName).isEmpty()) {
            errors.add(checkAndGetMessageInSpecifiedLanguage(CODE_NAME_IS_NOT_EXIST, user.getEndonymInterfaceLanguage()));
            resultErrorsResponse.setErrors(errors);
            return resultErrorsResponse;
        }
        if (inProgramMessageRepository.findByCodeNameAndLanguage(codeName, language).isPresent()) {
            errors.add(checkAndGetMessageInSpecifiedLanguage(IN_PROGRAM_MESSAGE_EXISTS_ALREADY, user.getEndonymInterfaceLanguage()));
            resultErrorsResponse.setErrors(errors);
            return resultErrorsResponse;
        }
        InProgramMessage inProgramMessage = new InProgramMessage();
        inProgramMessage.setLanguage(languageRepository.findByName(language).get());
        inProgramMessage.setMessagesCodeName(messageCodeNameRepository.findByName(codeName).get());
        inProgramMessage.setMessage(message);
        inProgramMessageRepository.save(inProgramMessage);
        resultErrorsResponse.setResult(true);

        return resultErrorsResponse;
    }

    public String checkAndGetMessageInSpecifiedLanguage(String codeName, String interfaceLanguage) {
        Optional<InProgramMessage> inProgramMessage = inProgramMessageRepository
                .findByCodeNameAndLanguage(codeName, interfaceLanguage);
        if (inProgramMessage.isPresent()) {
            return inProgramMessage.get().getMessage();
        }

        return inProgramMessageRepository.findByCodeNameAndLanguage(codeName, DEFAULT_LANGUAGE).get().getMessage();
    }

    public StringResponse checkAndGetSingleMessageInSpecifiedLanguage(String codeName, String interfaceLanguage) {
        StringResponse stringResponse = new StringResponse();
        Optional<InProgramMessage> inProgramMessage = inProgramMessageRepository
                .findByCodeNameAndLanguage(codeName, interfaceLanguage);
        if (inProgramMessage.isPresent()) {
            stringResponse.setString(inProgramMessage.get().getMessage());
            return stringResponse;
        }
        stringResponse.setString(inProgramMessageRepository.findByCodeNameAndLanguage(codeName, DEFAULT_LANGUAGE).get()
                .getMessage());

        return stringResponse;
    }
}