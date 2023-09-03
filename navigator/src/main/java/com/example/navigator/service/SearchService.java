package com.example.navigator.service;
import com.example.navigator.api.request.JobRequest;
import com.example.navigator.api.request.RequestForEmployees;
import com.example.navigator.api.request.VoteRequest;
import com.example.navigator.api.response.*;
import com.example.navigator.model.*;
import com.example.navigator.model.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private EmployerPassiveSearchDataRepository employerPassiveSearchDataRepository;
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

    public EmployeesListResponse getEmployeesOfChosenProfession(RequestForEmployees requestForEmployees, Principal principal) {
        EmployeesListResponse employeesListResponse = new EmployeesListResponse();
        User user = userRepository.findByEmail(principal.getName()).get();
        int limit = requestForEmployees.getLimit();
        if (limit <= 0) {
            employeesListResponse.setError(checkAndGetMessageInSpecifiedLanguage(INCORRECT_LIMIT, user.getInterfaceLanguage()));
            return employeesListResponse;
        }
        ProfessionName professionName = professionNameRepository.findByName(requestForEmployees.getProfessionName()).get();
        String country = requestForEmployees.getCountry();
        String city = requestForEmployees.getCity();
        String language = requestForEmployees.getLanguageName();
        byte isAuto = requestForEmployees.getIsAuto();
        byte areLanguagesMatch = requestForEmployees.getAreLanguagesMatch();
        int usersCountWithoutBlackList;
        Pageable page = PageRequest.of(0, limit);
        List<Long> idList;
        List<User> employeeList;
        if (city != null) {
            if (isAuto == 1) {
                if (areLanguagesMatch == 1) {
                    employeeList = userRepository.findAllByProfessionLanguageAutoCountryAndCity(
                            professionName.getProfession().getId(), country, city, language, page);
                    usersCountWithoutBlackList = employeeList.size();
                    employeeList.removeAll(user.getBlackList());
                    if (employeeList.size() != usersCountWithoutBlackList) {
                        limit = limit + (usersCountWithoutBlackList - employeeList.size());
                        page = PageRequest.of(0, limit);
                        employeeList = userRepository.findAllByProfessionLanguageAutoCountryAndCity(
                                professionName.getProfession().getId(), country, city, language, page);
                        employeeList.removeAll(user.getBlackList());
                    }
                } else {
                    employeeList = userRepository.findAllByProfessionAutoCountryAndCity(
                            professionName.getProfession().getId(), country, city, page);
                    usersCountWithoutBlackList = employeeList.size();
                    employeeList.removeAll(user.getBlackList());
                    if (employeeList.size() != usersCountWithoutBlackList) {
                        limit = limit + (usersCountWithoutBlackList - employeeList.size());
                        page = PageRequest.of(0, limit);
                        employeeList = userRepository.findAllByProfessionAutoCountryAndCity(
                                professionName.getProfession().getId(), country, city, page);
                        employeeList.removeAll(user.getBlackList());
                    }
                }
            } else {
                if (areLanguagesMatch == 1) {
                    employeeList = userRepository.findAllByProfessionLanguageCountryAndCity(
                            professionName.getProfession().getId(), country, city, language, page);
                    usersCountWithoutBlackList = employeeList.size();
                    employeeList.removeAll(user.getBlackList());
                    if (employeeList.size() != usersCountWithoutBlackList) {
                        limit = limit + (usersCountWithoutBlackList - employeeList.size());
                        page = PageRequest.of(0, limit);
                        employeeList = userRepository.findAllByProfessionLanguageCountryAndCity(
                                professionName.getProfession().getId(), country, city, language, page);
                        employeeList.removeAll(user.getBlackList());
                    }
                } else {
                    employeeList = userRepository.findAllByProfessionCountryAndCity(
                            professionName.getProfession().getId(), country, city, page);
                    usersCountWithoutBlackList = employeeList.size();
                    employeeList.removeAll(user.getBlackList());
                    if (employeeList.size() != usersCountWithoutBlackList) {
                        limit = limit + (usersCountWithoutBlackList - employeeList.size());
                        page = PageRequest.of(0, limit);
                        employeeList = userRepository.findAllByProfessionCountryAndCity(
                                professionName.getProfession().getId(), country, city, page);
                        employeeList.removeAll(user.getBlackList());
                    }
                }
            }
            idList = new ArrayList<>(employeeList.stream().map(User::getId).collect(Collectors.toList()));
            if (idList.isEmpty()) {
                employeesListResponse.setError(checkAndGetMessageInSpecifiedLanguage
                        (EMPLOYEES_DOES_NOT_EXIST, user.getInterfaceLanguage()));
                return employeesListResponse;
            }
            user.setLastRequest(professionName + "-" + limit + "-" + country + "-" + city);
            idList.removeAll(user.getBlackList().stream().map(User::getId).collect(Collectors.toList()));
            employeesListResponse.setEmployeesIdList(idList);
            return employeesListResponse;
        }
        if (isAuto == 1) {
            if (areLanguagesMatch == 1) {
                employeeList = userRepository
                        .findAllByProfessionLanguageAutoAndCountry(professionName.getProfession().getId(), country, language, page);
                usersCountWithoutBlackList = employeeList.size();
                employeeList.removeAll(user.getBlackList());
                if (employeeList.size() != usersCountWithoutBlackList) {
                    limit = limit + (usersCountWithoutBlackList - employeeList.size());
                    page = PageRequest.of(0, limit);
                    employeeList = userRepository
                            .findAllByProfessionAutoAndCountry(professionName.getProfession().getId(), country, page);
                    employeeList.removeAll(user.getBlackList());
                }
            } else {
                employeeList = userRepository
                        .findAllByProfessionAutoAndCountry(professionName.getProfession().getId(), country, page);
                usersCountWithoutBlackList = employeeList.size();
                employeeList.removeAll(user.getBlackList());
                if (employeeList.size() != usersCountWithoutBlackList) {
                    limit = limit + (usersCountWithoutBlackList - employeeList.size());
                    page = PageRequest.of(0, limit);
                    employeeList = userRepository
                            .findAllByProfessionAutoAndCountry(professionName.getProfession().getId(), country, page);
                    employeeList.removeAll(user.getBlackList());
                }
            }
        } else {
            if (areLanguagesMatch == 1) {
                employeeList = userRepository
                        .findAllByProfessionLanguageAndCountry(professionName.getProfession().getId(), country, language, page);
                usersCountWithoutBlackList = employeeList.size();
                employeeList.removeAll(user.getBlackList());
                if (employeeList.size() != usersCountWithoutBlackList) {
                    limit = limit + (usersCountWithoutBlackList - employeeList.size());
                    page = PageRequest.of(0, limit);
                    employeeList = userRepository
                            .findAllByProfessionAndCountry(professionName.getProfession().getId(), country, page);
                    employeeList.removeAll(user.getBlackList());
                }
            } else {
                employeeList = userRepository
                        .findAllByProfessionAndCountry(professionName.getProfession().getId(), language, page);
                usersCountWithoutBlackList = employeeList.size();
                employeeList.removeAll(user.getBlackList());
                if (employeeList.size() != usersCountWithoutBlackList) {
                    limit = limit + (usersCountWithoutBlackList - employeeList.size());
                    page = PageRequest.of(0, limit);
                    employeeList = userRepository
                            .findAllByProfessionAndCountry(professionName.getProfession().getId(), country, page);
                    employeeList.removeAll(user.getBlackList());
                }
            }
        }
        idList = new ArrayList<>(employeeList.stream().map(User::getId).collect(Collectors.toList()));
        if (idList.isEmpty()) {
            employeesListResponse.setError(checkAndGetMessageInSpecifiedLanguage
                    (EMPLOYEES_DOES_NOT_EXIST, user.getInterfaceLanguage()));
            return employeesListResponse;
        }
        user.setLastRequest(professionName + "-" + limit + "-" + country);
        employeesListResponse.setEmployeesIdList(idList);

        return employeesListResponse;
    }

    public EmployeesListResponse getTheNearestEmployeesInfo(RequestForEmployees requestForEmployees, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).get();
        EmployeesListResponse employeesListResponse = new EmployeesListResponse();
        int limit = requestForEmployees.getLimit();
        String language = requestForEmployees.getLanguageName();
        byte isAuto = requestForEmployees.getIsAuto();
        byte areLanguagesMatch = requestForEmployees.getAreLanguagesMatch();
        ProfessionName professionName = professionNameRepository.findByName(requestForEmployees.getProfessionName()).get();
        if (limit <= 0) {
            employeesListResponse.setError
                    (checkAndGetMessageInSpecifiedLanguage(INCORRECT_LIMIT, user.getInterfaceLanguage()));
            return employeesListResponse;
        }
        double jobAddressLat = requestForEmployees.getJobAddressLat();
        double jobAddressLon = requestForEmployees.getJobAddressLon();
        List<User> employeeList;
        if (isAuto == 1) {
            if (areLanguagesMatch == 1) {
                employeeList = userRepository.findAllByProfessionAndLanguage(professionName.getProfession().getId(), language);
            } else {
                employeeList = userRepository.findAllByProfession(professionName.getProfession().getId());
            }
        } else {
            if (areLanguagesMatch == 1) {
                employeeList = userRepository.findAllByProfessionLanguageAndAuto(professionName.getProfession().getId(), language);
            } else {
                employeeList = userRepository.findAllByProfessionAndAuto(professionName.getProfession().getId());
            }
        }
        employeeList.removeAll(user.getBlackList());
        List<Location> locations = employeeList.stream().map(User::getLocation).collect(Collectors.toList());
        if (locations.isEmpty()) {
            employeesListResponse.setError(checkAndGetMessageInSpecifiedLanguage
                    (EMPLOYEES_DOES_NOT_EXIST, user.getInterfaceLanguage()));
            return employeesListResponse;
        }
        HashMap<Location, Double> map = new HashMap<>(limit);
        for (Location loc : locations) {
            double lat = loc.getLatitude();
            double lon = loc.getLongitude();
            double currentRange = AVERAGE_RADIUS_OF_THE_EARTH * (180/Math.PI) * Math.acos(
                    Math.sin(jobAddressLat * (Math.PI/180)) * Math.sin(lat * (Math.PI/180)) +
                            Math.cos(jobAddressLat * (Math.PI/180)) * Math.cos(lat * (Math.PI/180))
                                    * Math.cos(jobAddressLon - lon * (Math.PI/180)));
            if (map.size() < requestForEmployees.getLimit()) {
                map.put(loc, currentRange);
                continue;
            }
            double theLongestValue = map.values().stream().max(Double::compare).get();
            if (currentRange < theLongestValue) {
                for (Map.Entry<Location, Double> entry : map.entrySet()) {
                    if (entry.getValue() == theLongestValue) {
                        map.remove(entry.getKey());
                        map.put(loc, currentRange);
                    }
                }
            }
        }
        user.setLastRequest(professionName + "-" + limit);
        List<User> nearesEmployeeList = new ArrayList<>(map.keySet().stream().map(Location::getUser).collect(Collectors.toList()));
        List<Long> idList = new ArrayList<>(nearesEmployeeList.stream().map(User::getId).collect(Collectors.toList()));
        idList.removeAll(user.getBlackList().stream().map(User::getId).collect(Collectors.toList()));
        employeesListResponse.setEmployeesIdList(idList);

        return employeesListResponse;
    }

    public EmployeesListResponse getTheBestEmployees(RequestForEmployees requestForEmployees, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).get();
        int limit = requestForEmployees.getLimit();
        ProfessionName professionName = professionNameRepository.findByName(requestForEmployees.getProfessionName()).get();
        String country = requestForEmployees.getCountry();
        String city = requestForEmployees.getCity();
        String language = requestForEmployees.getLanguageName();
        byte isAuto = requestForEmployees.getIsAuto();
        byte areLanguagesMatch = requestForEmployees.getAreLanguagesMatch();
        EmployeesListResponse employeesListResponse = new EmployeesListResponse();
        int usersCountWithoutBlackList;
        if (limit <= 0) {
            employeesListResponse.setError(checkAndGetMessageInSpecifiedLanguage(INCORRECT_LIMIT, user.getInterfaceLanguage()));
            return employeesListResponse;
        }
        Pageable page = PageRequest.of(0, limit);
        List<Long> idList;
        List<User> employeeList;
        if (city != null) {
            if (isAuto == 1) {
                if (areLanguagesMatch == 1) {
                    employeeList = userRepository.findTheBestByProfessionCountryLanguageAutoAndCity(
                            professionName.getProfession().getId(), country, city, language, page);
                    usersCountWithoutBlackList = employeeList.size();
                    employeeList.removeAll(user.getBlackList());
                    if (employeeList.size() != usersCountWithoutBlackList) {
                        limit = limit + (usersCountWithoutBlackList - employeeList.size());
                        page = PageRequest.of(0, limit);
                        employeeList = userRepository.findTheBestByProfessionCountryLanguageAutoAndCity(
                                professionName.getProfession().getId(), country, city, language, page);
                        employeeList.removeAll(user.getBlackList());
                    }
                } else {
                    employeeList = userRepository.findTheBestByProfessionCountryAutoAndCity(
                            professionName.getProfession().getId(), country, city, page);
                    usersCountWithoutBlackList = employeeList.size();
                    employeeList.removeAll(user.getBlackList());
                    if (employeeList.size() != usersCountWithoutBlackList) {
                        limit = limit + (usersCountWithoutBlackList - employeeList.size());
                        page = PageRequest.of(0, limit);
                        employeeList = userRepository.findTheBestByProfessionCountryAutoAndCity(
                                professionName.getProfession().getId(), country, city, page);
                        employeeList.removeAll(user.getBlackList());
                    }
                }
            } else {
                if (areLanguagesMatch == 1) {
                    employeeList = userRepository.findTheBestByProfessionCountryLanguageAndCity(
                            professionName.getProfession().getId(), country, city, language, page);
                    usersCountWithoutBlackList = employeeList.size();
                    employeeList.removeAll(user.getBlackList());
                    if (employeeList.size() != usersCountWithoutBlackList) {
                        limit = limit + (usersCountWithoutBlackList - employeeList.size());
                        page = PageRequest.of(0, limit);
                        employeeList = userRepository.findTheBestByProfessionCountryLanguageAndCity(
                                professionName.getProfession().getId(), country, city, language, page);
                        employeeList.removeAll(user.getBlackList());
                    }
                } else {
                    employeeList = userRepository.findTheBestByProfessionCountryAndCity(
                            professionName.getProfession().getId(), country, city, page);
                    usersCountWithoutBlackList = employeeList.size();
                    employeeList.removeAll(user.getBlackList());
                    if (employeeList.size() != usersCountWithoutBlackList) {
                        limit = limit + (usersCountWithoutBlackList - employeeList.size());
                        page = PageRequest.of(0, limit);
                        employeeList = userRepository.findTheBestByProfessionCountryAndCity(
                                professionName.getProfession().getId(), country, city, page);
                        employeeList.removeAll(user.getBlackList());
                    }
                }
            }
            idList = new ArrayList<>(employeeList.stream().map(User::getId).collect(Collectors.toList()));
            if (idList.isEmpty()) {
                employeesListResponse.setError(checkAndGetMessageInSpecifiedLanguage
                        (EMPLOYEES_DOES_NOT_EXIST, user.getInterfaceLanguage()));
                return employeesListResponse;
            }
            user.setLastRequest(professionName + "-" + limit + "-" + country + "-" + city);
            idList.removeAll(user.getBlackList().stream().map(User::getId).collect(Collectors.toList()));
            employeesListResponse.setEmployeesIdList(idList);
            return employeesListResponse;
        }
        if (isAuto == 1) {
            if (areLanguagesMatch == 1) {
                employeeList = userRepository.findTheBestByProfessionLanguageAutoAndCountry(
                        professionName.getProfession().getId(), country, language, page);
                usersCountWithoutBlackList = employeeList.size();
                employeeList.removeAll(user.getBlackList());
                if (employeeList.size() != usersCountWithoutBlackList) {
                    limit = limit + (usersCountWithoutBlackList - employeeList.size());
                    page = PageRequest.of(0, limit);
                    employeeList = userRepository.findTheBestByProfessionLanguageAutoAndCountry(
                            professionName.getProfession().getId(), country, language, page);
                    employeeList.removeAll(user.getBlackList());
                }
            } else {
                employeeList = userRepository.findTheBestByProfessionAutoAndCountry(
                        professionName.getProfession().getId(), country, page);
                usersCountWithoutBlackList = employeeList.size();
                employeeList.removeAll(user.getBlackList());
                if (employeeList.size() != usersCountWithoutBlackList) {
                    limit = limit + (usersCountWithoutBlackList - employeeList.size());
                    page = PageRequest.of(0, limit);
                    employeeList = userRepository.findTheBestByProfessionAutoAndCountry(
                            professionName.getProfession().getId(), country, page);
                    employeeList.removeAll(user.getBlackList());
                }
            }
        } else {
            if (areLanguagesMatch == 1) {
                employeeList = userRepository.findTheBestByProfessionLanguageAndCountry(
                        professionName.getProfession().getId(), country, language, page);
                usersCountWithoutBlackList = employeeList.size();
                employeeList.removeAll(user.getBlackList());
                if (employeeList.size() != usersCountWithoutBlackList) {
                    limit = limit + (usersCountWithoutBlackList - employeeList.size());
                    page = PageRequest.of(0, limit);
                    employeeList = userRepository.findTheBestByProfessionLanguageAndCountry(
                            professionName.getProfession().getId(), country, language, page);
                    employeeList.removeAll(user.getBlackList());
                }
            } else {
                employeeList = userRepository.findTheBestByProfessionAndCountry(
                        professionName.getProfession().getId(), country, page);
                usersCountWithoutBlackList = employeeList.size();
                employeeList.removeAll(user.getBlackList());
                if (employeeList.size() != usersCountWithoutBlackList) {
                    limit = limit + (usersCountWithoutBlackList - employeeList.size());
                    page = PageRequest.of(0, limit);
                    employeeList = userRepository.findTheBestByProfessionAndCountry(
                            professionName.getProfession().getId(), country, page);
                    employeeList.removeAll(user.getBlackList());
                }
            }
        }
        idList = new ArrayList<>(employeeList.stream().map(User::getId).collect(Collectors.toList()));
        if (idList.isEmpty()) {
            employeesListResponse.setError(checkAndGetMessageInSpecifiedLanguage
                    (EMPLOYEES_DOES_NOT_EXIST, user.getInterfaceLanguage()));
            return employeesListResponse;
        }
        user.setLastRequest(professionName + "-" + limit + "-" + country);
        employeesListResponse.setEmployeesIdList(idList);

        return employeesListResponse;
    }

    public ResultErrorsResponse setPassiveSearch(JobRequest jobRequest, Principal principal) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        List<String> errors = new ArrayList<>();
        User employer = userRepository.findByEmail(principal.getName()).get();
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
                    errors.add(checkAndGetMessageInSpecifiedLanguage(PROFESSION_NOT_FOUND, employer.getInterfaceLanguage()));
                } else {
                    professions.add(profession.get().getProfession());
                }
            }
        } else {
            errors.add(checkAndGetMessageInSpecifiedLanguage(PROFESSION_SPECIFICATION_REQUIREMENT, employer.getInterfaceLanguage()));
        }
        if (jobAddress == null || jobAddress.length() > 50) {
            errors.add(checkAndGetMessageInSpecifiedLanguage(INCORRECT_JOB_ADDRESS, employer.getInterfaceLanguage()));
        }
        if (info != null) {
            if (info.length() > 50) {
                errors.add(checkAndGetMessageInSpecifiedLanguage(TOO_MANY_ADDITIONAL_INFO, employer.getInterfaceLanguage()));
            }
        }
        if (timestamp == null && (lowestBorderTimestamp == null && highestBorderTimestamp == null)) {
            errors.add(checkAndGetMessageInSpecifiedLanguage(SPECIFICATION_DATE_REQUIREMENT, employer.getInterfaceLanguage()));
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
        EmployerPassiveSearchData employerPassiveSearchData = new EmployerPassiveSearchData();
        employerPassiveSearchData.setJobAddress(jobAddress);
        employerPassiveSearchData.setProfessions(professions);
        employerPassiveSearchData.setPaymentAndAdditionalInfo(info);
        employerPassiveSearchData.setEmployerRequests(employer.getEmployerRequests());
        if (timestamp != null) {
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), TimeZone.getDefault().toZoneId());
            employerPassiveSearchData.setDesignatedDateTime(dateTime);
        } else {
            LocalDateTime startDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), TimeZone.getDefault().toZoneId());
            employerPassiveSearchData.setStartDateTime(startDateTime);
            LocalDateTime endDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), TimeZone.getDefault().toZoneId());
            employerPassiveSearchData.setEndDateTime(endDateTime);
        }
        employerPassiveSearchDataRepository.save(employerPassiveSearchData);
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