package com.example.navigator.service;
import com.example.navigator.api.request.JobRequest;
import com.example.navigator.api.request.RequestForEmployees;
import com.example.navigator.api.response.*;
import com.example.navigator.model.*;
import com.example.navigator.model.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private VacancyRepository vacancyRepository;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private EmployerRequestsRepository employerRequestsRepository;
    @Autowired
    private EmployeeDataRepository employeeDataRepository;
    @Autowired
    private ProfessionRepository professionRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InProgramMessageRepository inProgramMessageRepository;
    @Autowired
    private ProfessionNameRepository professionNameRepository;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private ProfessionToUserRepository professionToUserRepository;
    @Autowired
    private VoteRepository voteRepository;

    private static final double AVERAGE_RADIUS_OF_THE_EARTH = 6371.0;
    private final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final String DEFAULT_LANGUAGE = "English";
    private final String PROFESSION_ALREADY_EXISTS = "PROFESSION_ALREADY_EXISTS";
    private final String APP_DOES_NOT_HAVE_LANGUAGE = "APP_DOES_NOT_HAVE_LANGUAGE";
    private final String PROFESSION_NOT_FOUND = "PROFESSION_NOT_FOUND";
    private final String PROFESSION_SPECIFICATION_REQUIREMENT = "PROFESSION_SPECIFICATION_REQUIREMENT";
    private final String INCORRECT_JOB_ADDRESS = "INCORRECT_JOB_ADDRESS";
    private final String TOO_MANY_ADDITIONAL_INFO = "TOO_MANY_ADDITIONAL_INFO";
    private final String SPECIFICATION_DATE_REQUIREMENT = "SPECIFICATION_DATE_REQUIREMENT";
    private final String PASSIVE_SEARCH_EXISTS_ALREADY = "PASSIVE_SEARCH_EXISTS_ALREADY";
    private final String INCORRECT_LIMIT = "INCORRECT_LIMIT";
    private final String EMPLOYEES_DOES_NOT_EXIST = "EMPLOYEES_DOES_NOT_EXIST";
    private final String NO_INFO_EMPLOYEE = "NO_INFO_EMPLOYEE";
    private final String EMPLOYEE_ACTIVE_SINCE = "EMPLOYEE_ACTIVE_SINCE";
    private final String ERROR_HAS_OCCURRED = "ERROR_HAS_OCCURRED";
    private final String USER_NOT_FOUND = "USER_NOT_FOUND";
    private final String OFFER_IS_NOT_EXIST = "OFFER_IS_NOT_EXIST";
    private final String USER_IS_TEMPORARILY_BUSY = "USER_IS_TEMPORARILY_BUSY";

    private HashSet<User> getEmployeeListInDesignatedRadius(double usersLat, double usersLong,
                                                                        HashSet<Location> locationsSet, double radius) {
        HashSet<User> usersSet = new HashSet<>();
        for (Location location : locationsSet) {
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            double currentRange = AVERAGE_RADIUS_OF_THE_EARTH * (180/Math.PI) * Math.acos(
                    Math.sin(usersLat * (Math.PI/180)) * Math.sin(lat * (Math.PI/180)) +
                            Math.cos(usersLat * (Math.PI/180)) * Math.cos(lat * (Math.PI/180))
                                    * Math.cos(usersLong - lon * (Math.PI/180)));
            if (currentRange >= radius) {
                usersSet.add(location.getUser());
            }
        }

        return  usersSet;
    }

    public EmployeesListResponse getEmployeesOfChosenProfession(RequestForEmployees requestForEmployees) {
        EmployeesListResponse employeesListResponse = new EmployeesListResponse();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        int limit = requestForEmployees.getLimit();
        if (limit <= 0) {
            employeesListResponse.setError(checkAndGetMessageInSpecifiedLanguage(INCORRECT_LIMIT, user.getEndonymInterfaceLanguage()));
            return employeesListResponse;
        }
        ProfessionName professionName = professionNameRepository.findByName(requestForEmployees.getProfessionName()).get();
        List<String> communicationLanguages = requestForEmployees.getCommunicationLanguages();
        boolean isAuto = requestForEmployees.isAuto();
        boolean areLanguagesMatch = requestForEmployees.isAreLanguagesMatched();
        double radius = requestForEmployees.getInRadiusOf();
        Pageable page = PageRequest.of(0, limit);
        List<User> employeeList;
        HashSet<User> employeeSet = new HashSet<>();
        if (isAuto) {
            if (areLanguagesMatch) {
                for (String language : communicationLanguages) {
                    employeeSet.addAll(userRepository
                            .findAllByProfessionLanguageAndAuto(professionName.getProfession().getId(), language));
                }
            } else {
                employeeSet = userRepository
                        .findAllByProfessionAndAuto(professionName.getProfession().getId(), page);
            }
        } else {
            if (areLanguagesMatch) {
                for (String language : communicationLanguages) {
                    employeeSet = userRepository
                            .findAllByProfessionAndLanguage(professionName.getProfession().getId(), language);
                }
            } else {
                employeeSet = userRepository
                        .findAllByProfession(professionName.getProfession().getId(), page);
            }
        }
        user.getBlackList().forEach(employeeSet::remove);
        HashSet<Location> locationsSet = (HashSet<Location>) employeeSet.stream().map(User::getLocation).collect(Collectors.toSet());
        employeeList = new ArrayList<>(new ArrayList<>(getEmployeeListInDesignatedRadius
                (user.getLocation().getLatitude(), user.getLocation().getLongitude(),
                        locationsSet, radius)));
        if (employeeList.isEmpty()) {
            employeesListResponse.setError(checkAndGetMessageInSpecifiedLanguage
                    (EMPLOYEES_DOES_NOT_EXIST, user.getEndonymInterfaceLanguage()));
            return employeesListResponse;
        }
        user.setLastRequest(professionName + "-" + limit);
        employeesListResponse.setEmployeeList(employeeList);

        return employeesListResponse;
    }

    public EmployeesListResponse getEmployeesOfChosenProfessionSortedByName(RequestForEmployees requestForEmployees) {
        EmployeesListResponse employeesListResponse = new EmployeesListResponse();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        int limit = requestForEmployees.getLimit();
        if (limit <= 0) {
            employeesListResponse.setError(checkAndGetMessageInSpecifiedLanguage(INCORRECT_LIMIT, user.getEndonymInterfaceLanguage()));
            return employeesListResponse;
        }
        ProfessionName professionName = professionNameRepository.findByName(requestForEmployees.getProfessionName()).get();
        List<String> communicationLanguages = requestForEmployees.getCommunicationLanguages();
        boolean isAuto = requestForEmployees.isAuto();
        boolean areLanguagesMatch = requestForEmployees.isAreLanguagesMatched();
        double radius = requestForEmployees.getInRadiusOf();
        Pageable page = PageRequest.of(0, limit);
        List<User> employeeList;
        HashSet<User> employeeSet = new HashSet<>();
        if (isAuto) {
            if (areLanguagesMatch) {
                for (String language : communicationLanguages) {
                    employeeSet.addAll(userRepository
                            .findAllByProfessionLanguageAndAutoSortedByName(professionName.getProfession().getId(), language));
                }
            } else {
                employeeSet = userRepository
                        .findAllByProfessionAndAutoSortedByName(professionName.getProfession().getId(), page);
            }
        } else {
            if (areLanguagesMatch) {
                for (String language : communicationLanguages) {
                    employeeSet = userRepository
                            .findAllByProfessionAndLanguageSortedByName(professionName.getProfession().getId(), language);
                }
            } else {
                employeeSet = userRepository
                        .findAllByProfessionSortedByName(professionName.getProfession().getId(), page);
            }
        }
        user.getBlackList().forEach(employeeSet::remove);
        HashSet<Location> locationsSet = (HashSet<Location>) employeeSet.stream().map(User::getLocation).collect(Collectors.toSet());
        employeeList = new ArrayList<>(new ArrayList<>(getEmployeeListInDesignatedRadius
                (user.getLocation().getLatitude(), user.getLocation().getLongitude(),
                        locationsSet, radius)));
        if (employeeList.isEmpty()) {
            employeesListResponse.setError(checkAndGetMessageInSpecifiedLanguage
                    (EMPLOYEES_DOES_NOT_EXIST, user.getEndonymInterfaceLanguage()));
            return employeesListResponse;
        }
        user.setLastRequest(professionName + "-" + limit);
        employeesListResponse.setEmployeeList(employeeList);

        return employeesListResponse;
    }

    public EmployeesListResponse getEmployeesOfChosenProfessionSortedByLocation(RequestForEmployees requestForEmployees) {
        EmployeesListResponse employeesListResponse = new EmployeesListResponse();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        int limit = requestForEmployees.getLimit();
        if (limit <= 0) {
            employeesListResponse.setError(checkAndGetMessageInSpecifiedLanguage(INCORRECT_LIMIT, user.getEndonymInterfaceLanguage()));
            return employeesListResponse;
        }
        ProfessionName professionName = professionNameRepository.findByName(requestForEmployees.getProfessionName()).get();
        List<String> communicationLanguages = requestForEmployees.getCommunicationLanguages();
        boolean isAuto = requestForEmployees.isAuto();
        boolean areLanguagesMatch = requestForEmployees.isAreLanguagesMatched();
        double radius = requestForEmployees.getInRadiusOf();
        Pageable page = PageRequest.of(0, limit);
        List<User> employeeList;
        HashSet<User> employeeSet = new HashSet<>();
        if (isAuto) {
            if (areLanguagesMatch) {
                for (String language : communicationLanguages) {
                    employeeSet.addAll(userRepository
                            .findAllByProfessionLanguageAndAuto(professionName.getProfession().getId(), language));
                }
            } else {
                employeeSet = userRepository
                        .findAllByProfessionAndAuto(professionName.getProfession().getId(), page);
            }
        } else {
            if (areLanguagesMatch) {
                for (String language : communicationLanguages) {
                    employeeSet = userRepository
                            .findAllByProfessionAndLanguage(professionName.getProfession().getId(), language);
                }
            } else {
                employeeSet = userRepository
                        .findAllByProfession(professionName.getProfession().getId(), page);
            }
        }
        user.getBlackList().forEach(employeeSet::remove);
        HashSet<Location> locationsSet = (HashSet<Location>) employeeSet.stream().map(User::getLocation).collect(Collectors.toSet());
        employeeList = new ArrayList<>(new ArrayList<>(getEmployeeListInDesignatedRadius
                (user.getLocation().getLatitude(), user.getLocation().getLongitude(),
                        locationsSet, radius)));
        double usersLat = requestForEmployees.getJobAddressLat();
        double usersLon = requestForEmployees.getJobAddressLon();
        List<Location> locations = employeeList.stream().map(User::getLocation).collect(Collectors.toList());
        if (employeeList.isEmpty()) {
            employeesListResponse.setError(checkAndGetMessageInSpecifiedLanguage
                    (EMPLOYEES_DOES_NOT_EXIST, user.getEndonymInterfaceLanguage()));
            return employeesListResponse;
        }
        HashMap<User, Double> map = new HashMap<>(limit);
        for (Location loc : locations) {
            double lat = loc.getLatitude();
            double lon = loc.getLongitude();
            double currentRange = AVERAGE_RADIUS_OF_THE_EARTH * (180/Math.PI) * Math.acos(
                    Math.sin(usersLat * (Math.PI/180)) * Math.sin(lat * (Math.PI/180)) +
                            Math.cos(usersLat * (Math.PI/180)) * Math.cos(lat * (Math.PI/180))
                                    * Math.cos(usersLon - lon * (Math.PI/180)));
            if (map.size() < requestForEmployees.getLimit()) {
                map.put(loc.getUser(), currentRange);
                continue;
            }
            double theLongestValue = map.values().stream().max(Double::compare).get();
            if (currentRange < theLongestValue) {
                for (Map.Entry<User, Double> entry : map.entrySet()) {
                    if (entry.getValue() == theLongestValue) {
                        map.remove(entry.getKey());
                        map.put(loc.getUser(), currentRange);
                    }
                }
            }
        }
        user.setLastRequest(professionName + "-" + limit);
        employeeList = new ArrayList<>(map.keySet());
        employeesListResponse.setEmployeeList(employeeList);

        return employeesListResponse;
    }

    public EmployeesListResponse getEmployeesOfChosenProfessionSortByRating(RequestForEmployees requestForEmployees) {
        EmployeesListResponse employeesListResponse = new EmployeesListResponse();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        int limit = requestForEmployees.getLimit();
        if (limit <= 0) {
            employeesListResponse.setError(checkAndGetMessageInSpecifiedLanguage(INCORRECT_LIMIT, user.getEndonymInterfaceLanguage()));
            return employeesListResponse;
        }
        ProfessionName professionName = professionNameRepository.findByName(requestForEmployees.getProfessionName()).get();
        List<String> communicationLanguages = requestForEmployees.getCommunicationLanguages();
        boolean isAuto = requestForEmployees.isAuto();
        boolean areLanguagesMatch = requestForEmployees.isAreLanguagesMatched();
        double radius = requestForEmployees.getInRadiusOf();
        Pageable page = PageRequest.of(0, limit);
        List<User> employeeList;
        HashSet<User> employeeSet = new HashSet<>();
        if (isAuto) {
            if (areLanguagesMatch) {
                for (String language : communicationLanguages) {
                    employeeSet.addAll(userRepository
                            .findTheBestByProfessionLanguageAndAuto(professionName.getProfession().getId(), language));
                }
            } else {
                employeeSet = userRepository
                        .findTheBestByProfessionAndAuto(professionName.getProfession().getId(), page);
            }
        } else {
            if (areLanguagesMatch) {
                for (String language : communicationLanguages) {
                    employeeSet = userRepository
                            .findTheBestByProfessionAndLanguage(professionName.getProfession().getId(), language);
                }
            } else {
                employeeSet = userRepository
                        .findTheBestByProfession(professionName.getProfession().getId(), page);
            }
        }
        user.getBlackList().forEach(employeeSet::remove);
        HashSet<Location> locationsSet = (HashSet<Location>) employeeSet.stream().map(User::getLocation).collect(Collectors.toSet());
        employeeList = new ArrayList<>(new ArrayList<>(getEmployeeListInDesignatedRadius
                (user.getLocation().getLatitude(), user.getLocation().getLongitude(),
                        locationsSet, radius)));
        if (employeeList.isEmpty()) {
            employeesListResponse.setError(checkAndGetMessageInSpecifiedLanguage
                    (EMPLOYEES_DOES_NOT_EXIST, user.getEndonymInterfaceLanguage()));
            return employeesListResponse;
        }
        user.setLastRequest(professionName + "-" + limit);
        employeesListResponse.setEmployeeList(employeeList);

        return employeesListResponse;
    }

    public ResultErrorsResponse setPassiveSearch(JobRequest jobRequest) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        List<String> errors = new ArrayList<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User employer = userRepository.findByEmail(username).get();
        List<String> professionsNames = jobRequest.getProfessions();
        String jobAddress = jobRequest.getJobAddress();
        String info = jobRequest.getPaymentAndAdditionalInfo();
        Long timestamp = jobRequest.getTimestamp();
        Long lowestBorderTimestamp = jobRequest.getLowestBorderTimestamp();
        Long highestBorderTimestamp = jobRequest.getHighestBorderTimestamp();
        List<Profession> professions = new ArrayList<>();
        if (!professionsNames.isEmpty()) {
            for (String professionName : professionsNames) {
                Optional<ProfessionName> profession = professionNameRepository.findByName(professionName);
                if (profession.isEmpty()) {
                    errors.add(checkAndGetMessageInSpecifiedLanguage(PROFESSION_NOT_FOUND, employer.getEndonymInterfaceLanguage()));
                } else {
                    professions.add(profession.get().getProfession());
                }
            }
        } else {
            errors.add(checkAndGetMessageInSpecifiedLanguage(PROFESSION_SPECIFICATION_REQUIREMENT, employer.getEndonymInterfaceLanguage()));
        }
        if (jobAddress == null || jobAddress.length() > 50) {
            errors.add(checkAndGetMessageInSpecifiedLanguage(INCORRECT_JOB_ADDRESS, employer.getEndonymInterfaceLanguage()));
        }
        if (info != null) {
            if (info.length() > 50) {
                errors.add(checkAndGetMessageInSpecifiedLanguage(TOO_MANY_ADDITIONAL_INFO, employer.getEndonymInterfaceLanguage()));
            }
        }
        if (timestamp == null && (lowestBorderTimestamp == null && highestBorderTimestamp == null)) {
            errors.add(checkAndGetMessageInSpecifiedLanguage(SPECIFICATION_DATE_REQUIREMENT, employer.getEndonymInterfaceLanguage()));
        }
        if (!errors.isEmpty()) {
            resultErrorsResponse.setErrors(errors);
            return resultErrorsResponse;
        }
        if (employer.getEmployerRequests() == null) {
            EmployerRequests employerRequests = new EmployerRequests();
            employerRequests.setEmployer(employer);
            employerRequestsRepository.save(employerRequests);
        }
        Vacancy vacancy = new Vacancy();
        vacancy.setJobAddress(jobAddress);
        vacancy.setProfessions(professions);
        vacancy.setPaymentAndAdditionalInfo(info);
        vacancy.setEmployerRequests(employer.getEmployerRequests());
        if (timestamp != null) {
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), TimeZone.getDefault().toZoneId());
            vacancy.setDesignatedDateTime(dateTime);
        } else {
            LocalDateTime startDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), TimeZone.getDefault().toZoneId());
            vacancy.setStartDateTime(startDateTime);
            LocalDateTime endDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), TimeZone.getDefault().toZoneId());
            vacancy.setEndDateTime(endDateTime);
        }
        vacancyRepository.save(vacancy);
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


}