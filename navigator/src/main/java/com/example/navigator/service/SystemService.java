package com.example.navigator.service;
import com.example.navigator.api.request.*;
import com.example.navigator.api.response.*;
import com.example.navigator.dto.TimerDTO;
import com.example.navigator.model.*;
import com.example.navigator.model.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    private VacancyRepository vacancyRepository;
    @Autowired
    private RequestForCompanySettingRepository requestForCompanySettingRepository;
    @Autowired
    private InfoAboutVacancyFromEmployerRepository infoAboutVacancyFromEmployerRepository;

    private final String DEFAULT_LANGUAGE = "English";
    private final String LANGUAGE_IS_NOT_EXIST = "LANGUAGE_IS_NOT_EXIST";
    private final String CODE_NAME_IS_NOT_EXIST = "CODE_NAME_IS_NOT_EXIST";
    private final String IN_PROGRAM_MESSAGE_EXISTS_ALREADY = "IN_PROGRAM_MESSAGE_EXISTS_ALREADY";
    private final String CODE_NAME_EXISTS_ALREADY = "CODE_NAME_EXISTS_ALREADY";
    private final String PROFESSION_ALREADY_EXISTS = "PROFESSION_ALREADY_EXISTS";
    private final String APP_DOES_NOT_HAVE_LANGUAGE = "APP_DOES_NOT_HAVE_LANGUAGE";
    private final String PROFESSION_NOT_FOUND = "PROFESSION_NOT_FOUND";

    public ResultErrorsResponse makeRequestForCompanySetting(StringRequest stringRequest) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        RequestForCompanySetting requestForCompanySetting = new RequestForCompanySetting();
        requestForCompanySetting.setEmployerRequests(user.getEmployerRequests());
        requestForCompanySetting.setRequestInfo(stringRequest.getString());
        requestForCompanySettingRepository.save(requestForCompanySetting);
        resultErrorsResponse.setResult(true);

        return resultErrorsResponse;
    }

    public IdResponse getLanguageIdByName(String name) {
        IdResponse idResponse = new IdResponse();
        Optional<Language> language = languageRepository.findByName(name);
        if (language.isEmpty()) {

            return idResponse;
        }
        idResponse.setResult(true);
        idResponse.setId(language.get().getId());

        return idResponse;
    }

    public TimersListResponse getTimersList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        TimersListResponse timersListResponse = new TimersListResponse();
        List<TimerDTO> dtoList = new ArrayList<>();
        TimerDTO timerDTO;
        if (user.getRole() == Role.EMPLOYER) {
            for (Vacancy vacancy : user.getEmployerRequests().getVacancies()) {
                for (EmployeeData employeeData : vacancy.getHiredEmployees()) {
                    timerDTO = new TimerDTO();
                    timerDTO.setId(vacancy.getId());
                    timerDTO.setName(employeeData.getEmployee().getName());
                    timerDTO.setAddress(vacancy.getJobLocation().getJobAddress());
                    timerDTO.setProfession(getProfessionNameByIdAndLanguage(vacancy.getProfession().getId()).getString());
                    timerDTO.setContactedPersonId(employeeData.getId());
                    timerDTO.setMillisInFuture(ZonedDateTime.of(vacancy.getStartDateTime(), ZoneId.systemDefault()).toInstant().toEpochMilli());
                    dtoList.add(timerDTO);
                }
            }
        } else {
            EmployeeData data = user.getEmployeeData();
            for (Vacancy vacancy : data.getAcceptedVacancies()) {
                timerDTO = new TimerDTO();
                timerDTO.setId(vacancy.getId());
                timerDTO.setName(vacancy.getEmployerRequests().getEmployer().getName());
                timerDTO.setAddress(vacancy.getJobLocation().getJobAddress());
                timerDTO.setProfession(getProfessionNameByIdAndLanguage(vacancy.getProfession().getId()).getString());
                timerDTO.setContactedPersonId(vacancy.getEmployerRequests().getId());
                timerDTO.setMillisInFuture(ZonedDateTime.of(vacancy.getStartDateTime(), ZoneId.systemDefault()).toInstant().toEpochMilli());
                dtoList.add(timerDTO);
            }
        }
        timersListResponse.setList(dtoList);

        return timersListResponse;
    }

    public StringResponse getAdditionalInfoAboutVacancyInSpecifiedLanguage(ProfessionToUserRequest professionToUserRequest) {
        StringResponse stringResponse = new StringResponse();
        long vacancyId = professionToUserRequest.getId();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        for (Language language : user.getCommunicationLanguages()) {
            Optional<InfoAboutVacancyFromEmployer> infoAboutVacancyFromEmployer = infoAboutVacancyFromEmployerRepository.
                    findByVacancyIdAndLanguage(vacancyId, language.getLanguageEndonym());
            if (infoAboutVacancyFromEmployer.isPresent()) {
                stringResponse.setString(infoAboutVacancyFromEmployer.get().getText());
                return stringResponse;
            }
        }

        return null;
    }

    public StringResponse getProfessionNameByIdAndLanguage(long professionId) {
        StringResponse stringResponse = new StringResponse();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        Optional<ProfessionName> professionName = professionNameRepository.findByProfessionIdAndLanguage(professionId,
                user.getEndonymInterfaceLanguage());
        if (professionName.isPresent()) {
            stringResponse.setString(professionName.get().getProfessionName());
        } else {
            stringResponse.setString(professionNameRepository.findByProfessionIdAndLanguage(professionId,
                    DEFAULT_LANGUAGE).get().getProfessionName());
        }

        return stringResponse;
    }

    public IdResponse getProfessionIdByName(String name) {
        IdResponse idResponse = new IdResponse();
        Optional<ProfessionName> professionName = professionNameRepository.findByName(name);
        if (professionName.isEmpty()) {
            return idResponse;
        }
        Profession profession = professionName.get().getProfession();
        idResponse.setId(profession.getId());

        return idResponse;
    }

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

    public ProfessionNamesListResponse getProfessionsNamesInSpecifiedLanguage() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        ProfessionNamesListResponse professionNamesListResponse = new ProfessionNamesListResponse();
        professionNamesListResponse.setList(new ArrayList<>(professionNameRepository.findAllBySpecifiedLanguage(user.getEndonymInterfaceLanguage())));

        return professionNamesListResponse;
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