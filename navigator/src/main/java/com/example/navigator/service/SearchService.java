package com.example.navigator.service;
import com.example.navigator.api.request.VacancyRequest;
import com.example.navigator.api.request.LocationsRequest;
import com.example.navigator.api.request.SearchRequest;
import com.example.navigator.api.request.StringRequest;
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
    @Autowired
    private SavedRequestsRepository savedRequestsRepository;

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
    private final String SUCH_VACANCIES_ARE_NOT_EXIST = "SUCH_VACANCIES_ARE_NOT_EXIST";
    private final String TOO_LONG_TEMPLATE_NAME = "TOO_LONG_TEMPLATE_NAME";
    private final String QUOTAS_NUMBER_ERROR = "QUOTAS_NUMBER_ERROR";

    public DistanceResponse calculateDistance(LocationsRequest locationsRequest) {
        DistanceResponse distanceResponse = new DistanceResponse();
        double lat1 = locationsRequest.getLat1();
        double long1 = locationsRequest.getLong1();
        double lat2 = locationsRequest.getLat2();
        double long2 = locationsRequest.getLong2();
        final double R = 6371.0;
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(long1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(long2);
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        distanceResponse.setDistance(R * c);

        return distanceResponse;
    }

    private List<User> excludeSpecifiedEmployeesAndGetList(List<User> employeeList, double usersLat,
                                                           double usersLong, double radius, User employer,
                                                           boolean areLanguagesMatched) {
        List<UserLocation> locationsList = employeeList.stream().map(User::getUserLocation).collect(Collectors.toList());
        for (UserLocation userLocation : locationsList) {
            double lat = userLocation.getLatitude();
            double lon = userLocation.getLongitude();
            double currentRange = AVERAGE_RADIUS_OF_THE_EARTH * (180/Math.PI) * Math.acos(
                    Math.sin(usersLat * (Math.PI/180)) * Math.sin(lat * (Math.PI/180)) +
                            Math.cos(usersLat * (Math.PI/180)) * Math.cos(lat * (Math.PI/180))
                                    * Math.cos(usersLong - lon * (Math.PI/180)));
            if (currentRange > radius) {
                employeeList.remove(userLocation.getUser());
            }
        }
        employer.getBlackList().forEach(employeeList::remove);
        if (areLanguagesMatched) {
            for (Language employersLanguage : employer.getCommunicationLanguages()) {
                for (User employee : employeeList) {
                    boolean areLanguagesMatchedInside = false;
                    for (Language employeeLanguage : employee.getCommunicationLanguages()) {
                        if (employersLanguage.getLanguageEndonym().equals(employeeLanguage.getLanguageEndonym())) {
                            areLanguagesMatchedInside = true;
                            break;
                        }
                    }
                    if (!areLanguagesMatchedInside) {
                        employeeList.remove(employee);
                    }
                }
            }
        }

        return employeeList;
    }

    private List<Vacancy> excludeSpecifiedEmployersAndGetList(List<Vacancy> vacancyList, double usersLat,
                                                              double usersLong, double radius, User employee,
                                                              boolean areLanguagesMatched) {
        List<JobLocation> locationsList = vacancyList.stream().map(Vacancy::getJobLocation).collect(Collectors.toList());
        for (JobLocation jobLocation : locationsList) {
            double lat = jobLocation.getLatitude();
            double lon = jobLocation.getLongitude();
            double currentRange = AVERAGE_RADIUS_OF_THE_EARTH * (180/Math.PI) * Math.acos(
                    Math.sin(usersLat * (Math.PI/180)) * Math.sin(lat * (Math.PI/180)) +
                            Math.cos(usersLat * (Math.PI/180)) * Math.cos(lat * (Math.PI/180))
                                    * Math.cos(usersLong - lon * (Math.PI/180)));
            if (currentRange > radius) {
                vacancyList.remove(jobLocation.getVacancy());
            }
        }
        List<User> employersList = vacancyList.stream().map(v -> v.getEmployerRequests().getEmployer()).collect(Collectors.toList());
        for (User employer : employersList) {
            if (employee.getBlackList().contains(employer)) {
                vacancyList.removeAll(employer.getEmployerRequests().getVacancies());
            }
        }
        if (areLanguagesMatched) {
            for (Language employersLanguage : employee.getCommunicationLanguages()) {
                for (User employer : employersList) {
                    boolean areLanguagesMatchedInside = false;
                    for (Language employeeLanguage : employer.getCommunicationLanguages()) {
                        if (employersLanguage.getLanguageEndonym().equals(employeeLanguage.getLanguageEndonym())) {
                            areLanguagesMatchedInside = true;
                            break;
                        }
                    }
                    if (!areLanguagesMatchedInside) {
                        vacancyList.removeAll(employer.getEmployerRequests().getVacancies());
                    }
                }
            }
        }

        return vacancyList;
    }

    public SearchResponse getVacanciesOfChosenProfession(SearchRequest searchRequest) {
        SearchResponse searchResponse = new SearchResponse();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        String sortType = searchRequest.getSortType();
        int limit = searchRequest.getLimit();
        if (limit <= 0) {
            searchResponse.setError(checkAndGetMessageInSpecifiedLanguage(INCORRECT_LIMIT, user.getEndonymInterfaceLanguage()));
            return searchResponse;
        }
        ProfessionName professionName = professionNameRepository.findByName(searchRequest.getProfessionName()).get();
        boolean isAuto = searchRequest.isAuto();
        boolean areLanguagesMatch = searchRequest.isAreLanguagesMatched();
        double radius = searchRequest.getInRadiusOf();
        UserLocation employeesLocation = user.getUserLocation();
        Pageable page = PageRequest.of(0, limit);
        List<Vacancy> vacanciesList;
        if (sortType.equals("name")) {
            vacanciesList = excludeSpecifiedEmployersAndGetList(
                    vacancyRepository.findAllByProfessionSortedByName(professionName.getProfessionName(), page),
                    employeesLocation.getLatitude(), employeesLocation.getLongitude(), radius, user, areLanguagesMatch);
        } else if (sortType.equals("rating")) {
            vacanciesList = excludeSpecifiedEmployersAndGetList(
                    vacancyRepository.findAllByProfessionSortedByRating(professionName.getProfessionName(), page),
                    employeesLocation.getLatitude(), employeesLocation.getLongitude(), radius, user, areLanguagesMatch);
        } else if (sortType.equals("location")) {
            vacanciesList = excludeSpecifiedEmployersAndGetList(
                    vacancyRepository.findByProfession(professionName.getProfessionName(), page),
                    employeesLocation.getLatitude(), employeesLocation.getLongitude(), radius, user, areLanguagesMatch);
            vacanciesList = new ArrayList<>(getVacanciesSortedByLocation(searchRequest, vacanciesList,
                    employeesLocation.getLatitude(), employeesLocation.getLongitude()).values());
        } else {
            vacanciesList = excludeSpecifiedEmployersAndGetList(
                    vacancyRepository.findByProfession(professionName.getProfessionName(), page),
                    employeesLocation.getLatitude(), employeesLocation.getLongitude(), radius, user, areLanguagesMatch);
        }

        if (vacanciesList.isEmpty()) {
            searchResponse.setError(checkAndGetMessageInSpecifiedLanguage
                    (EMPLOYEES_DOES_NOT_EXIST, user.getEndonymInterfaceLanguage()));
            return searchResponse;
        }
        searchResponse.setVacancyList(vacanciesList);
        searchResponse.setCount(vacanciesList.size());

        return searchResponse;
    }

    public SearchResponse getEmployeesOfChosenProfession(SearchRequest searchRequest) {
        SearchResponse searchResponse = new SearchResponse();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        String sortType = searchRequest.getSortType();
        int limit = searchRequest.getLimit();
        if (limit <= 0) {
            searchResponse.setError(checkAndGetMessageInSpecifiedLanguage(INCORRECT_LIMIT, user.getEndonymInterfaceLanguage()));
            return searchResponse;
        }
        Optional<ProfessionName> professionName = professionNameRepository.findByName(searchRequest.getProfessionName());
        if (professionName.isEmpty()) {
            searchResponse.setError(checkAndGetMessageInSpecifiedLanguage(PROFESSION_NOT_FOUND, user.getEndonymInterfaceLanguage()));
            return searchResponse;
        }
        boolean isAuto = searchRequest.isAuto();
        boolean areLanguagesMatch = searchRequest.isAreLanguagesMatched();
        double radius = searchRequest.getInRadiusOf();
        String additionalLanguage = searchRequest.getAdditionalLanguage();
        UserLocation employersLocation = user.getUserLocation();
        Pageable page = PageRequest.of(0, limit);
        List<User> employeeList;
        //___________________________________________________
        if (searchRequest.isShowTemporarilyInactiveEmployees()) {
            if (additionalLanguage != null) {
                if (sortType.equals("name")) {

                } else if (sortType.equals("rating")) {

                } else if (sortType.equals("location")) {

                } else {

                }
            } else {
                if (sortType.equals("name")) {

                } else if (sortType.equals("rating")) {

                } else if (sortType.equals("location")) {

                } else {

                }
            }
        } else {
            if (additionalLanguage != null) {
                if (sortType.equals("name")) {

                } else if (sortType.equals("rating")) {

                } else if (sortType.equals("location")) {

                } else {

                }
            } else {
                if (sortType.equals("name")) {

                } else if (sortType.equals("rating")) {

                } else if (sortType.equals("location")) {

                } else {

                }
            }
        }
        //______________________________________________________
        if (isAuto) {
            if (sortType.equals("name")) {
                employeeList = excludeSpecifiedEmployeesAndGetList(
                        userRepository.findAllByProfessionAndAutoSortedByName(professionName.getProfession().getId(), page),
                        employersLocation.getLatitude(), employersLocation.getLongitude(), radius, user, areLanguagesMatch);
            } else if (sortType.equals("rating")) {
                employeeList = excludeSpecifiedEmployeesAndGetList(
                        userRepository.findTheBestByProfessionAndAuto(professionName.getProfession().getId(), page),
                        employersLocation.getLatitude(), employersLocation.getLongitude(), radius, user, areLanguagesMatch);
            } else if (sortType.equals("location")) {
                employeeList = excludeSpecifiedEmployeesAndGetList(
                        userRepository.findAllByProfessionAndAuto(professionName.getProfession().getId(), page),
                        employersLocation.getLatitude(), employersLocation.getLongitude(), radius, user, areLanguagesMatch);
                employeeList = new ArrayList<>(getEmployeesSortedByLocation(searchRequest, employeeList,
                        employersLocation.getLatitude(), employersLocation.getLongitude()).values());
            } else {
                employeeList = excludeSpecifiedEmployeesAndGetList(
                        userRepository.findAllByProfessionAndAuto(professionName.getProfession().getId(), page),
                        employersLocation.getLatitude(), employersLocation.getLongitude(), radius, user, areLanguagesMatch);
            }
        } else {
            if (sortType.equals("name")) {
                employeeList = excludeSpecifiedEmployeesAndGetList(
                        userRepository.findAllByProfessionSortedByName(professionName.getProfession().getId(), page),
                        employersLocation.getLatitude(), employersLocation.getLongitude(), radius, user, areLanguagesMatch);
            } else if (sortType.equals("rating")) {
                employeeList = excludeSpecifiedEmployeesAndGetList(
                        userRepository.findTheBestByProfession(professionName.getProfession().getId(), page),
                        employersLocation.getLatitude(), employersLocation.getLongitude(), radius, user, areLanguagesMatch);
            } else if (sortType.equals("location")) {
                employeeList = excludeSpecifiedEmployeesAndGetList(
                        userRepository.findAllByProfession(professionName.getProfession().getId(), page),
                        employersLocation.getLatitude(), employersLocation.getLongitude(), radius, user, areLanguagesMatch);
                employeeList = new ArrayList<>(getEmployeesSortedByLocation(searchRequest, employeeList,
                        employersLocation.getLatitude(), employersLocation.getLongitude()).values());
            } else {
                employeeList = excludeSpecifiedEmployeesAndGetList(
                        userRepository.findAllByProfession(professionName.getProfession().getId(), page),
                        employersLocation.getLatitude(), employersLocation.getLongitude(), radius, user, areLanguagesMatch);
            }
        }
        if (employeeList.isEmpty()) {
            searchResponse.setError(checkAndGetMessageInSpecifiedLanguage
                    (EMPLOYEES_DOES_NOT_EXIST, user.getEndonymInterfaceLanguage()));
            return searchResponse;
        }
        searchResponse.setEmployeeList(employeeList);
        searchResponse.setCount(employeeList.size());

        return searchResponse;
    }

    private TreeMap<Double, User> getEmployeesSortedByLocation(SearchRequest searchRequest, List<User> employeeList,
                                                              double usersLat, double usersLon) {

        TreeMap<Double, User> map = new TreeMap<>();
        HashSet<UserLocation> userLocations = (HashSet<UserLocation>) employeeList.stream().map(User::getUserLocation)
                .collect(Collectors.toSet());
        for (UserLocation loc : userLocations) {
            double lat = loc.getLatitude();
            double lon = loc.getLongitude();
            double currentRange = AVERAGE_RADIUS_OF_THE_EARTH * (180/Math.PI) * Math.acos(
                    Math.sin(usersLat * (Math.PI/180)) * Math.sin(lat * (Math.PI/180)) +
                            Math.cos(usersLat * (Math.PI/180)) * Math.cos(lat * (Math.PI/180))
                                    * Math.cos(usersLon - lon * (Math.PI/180)));
            map.put(currentRange, loc.getUser());
        }

        return map;
    }

    private TreeMap<Double, Vacancy> getVacanciesSortedByLocation(SearchRequest searchRequest, List<Vacancy> vacancyList,
                                                              double usersLat, double usersLon) {

        TreeMap<Double, Vacancy> treeMap = new TreeMap<>();
        HashMap<UserLocation, Vacancy> employersMap = new HashMap<>();
        for (Vacancy vacancy : vacancyList) {
            employersMap.put(vacancy.getEmployerRequests().getEmployer().getUserLocation(), vacancy);
        }
        for (Map.Entry<UserLocation, Vacancy> map : employersMap.entrySet()) {
            double lat = map.getKey().getLatitude();
            double lon = map.getKey().getLongitude();
            double currentRange = AVERAGE_RADIUS_OF_THE_EARTH * (180/Math.PI) * Math.acos(
                    Math.sin(usersLat * (Math.PI/180)) * Math.sin(lat * (Math.PI/180)) +
                            Math.cos(usersLat * (Math.PI/180)) * Math.cos(lat * (Math.PI/180))
                                    * Math.cos(usersLon - lon * (Math.PI/180)));

            treeMap.put(currentRange, map.getValue());
        }

        return treeMap;
    }

    public ResultErrorsResponse saveRequest(SearchRequest searchRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        LastRequest lastRequest = new LastRequest();
        lastRequest.setAuto(lastRequest.isAuto());
        lastRequest.setLimit(lastRequest.getLimit());
        lastRequest.setUser(user);
        lastRequest.setInRadiusOf(lastRequest.getInRadiusOf());
        lastRequest.setAreLanguagesMatched(lastRequest.isAreLanguagesMatched());
        lastRequest.setProfessionName(lastRequest.getProfessionName());
        lastRequest.setSortType(lastRequest.getSortType());
        savedRequestsRepository.save(lastRequest);
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        resultErrorsResponse.setResult(true);

        return resultErrorsResponse;
    }

    public SearchResponse getSavedRequests() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setSavedRequestsList(savedRequestsRepository.findAllByUserId(user.getId()));

        return searchResponse;
    }

    public ResultErrorsResponse deleteVacancyById(long id) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        vacancyRepository.deleteById(id);
        resultErrorsResponse.setResult(true);

        return resultErrorsResponse;
    }

    public VacancyInfoResponse getVacancyById(long id) {
        VacancyInfoResponse vacancyInfoResponse = new VacancyInfoResponse();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        Vacancy vacancy = vacancyRepository.findById(id).get();
        vacancyInfoResponse.setJobAddress(vacancy.getJobLocation().getJobAddress());
        vacancyInfoResponse.setPaymentAndAdditionalInfo(vacancy.getPaymentAndAdditionalInfo());
        vacancyInfoResponse.setLocalDateTime(vacancy.getStartDateTime());
        vacancyInfoResponse.setProfessionName(professionNameRepository.findByProfessionIdAndLanguage
                (vacancy.getProfession().getId(), user.getEndonymInterfaceLanguage()).get().getProfessionName());

        return vacancyInfoResponse;
    }

    public ResultErrorsResponse setVacancy(VacancyRequest vacancyRequest) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        List<String> errors = new ArrayList<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User employer = userRepository.findByEmail(username).get();
        Optional<Profession> profession = professionRepository.findById(vacancyRequest.getProfessionId());
        int quotasNumber = vacancyRequest.getQuotasNumber();
        String templateName = vacancyRequest.getTemplateName();
        boolean isSaveTemplate = vacancyRequest.isSaveTemplate();
        boolean isNecessaryToCloseAllQuotas = vacancyRequest.isRequiredToCloseAllQuotas();
        String jobAddress = vacancyRequest.getJobAddress();
        Double latitude = vacancyRequest.getLatitude();
        Double longitude = vacancyRequest.getLongitude();
        String info = vacancyRequest.getPaymentAndAdditionalInfo();
        LocalDateTime waitingDateTime = vacancyRequest.getWaitingTimestamp();
        LocalDateTime startDateTime = vacancyRequest.getStartTimestamp();
        if (profession.isEmpty()) {
            errors.add(checkAndGetMessageInSpecifiedLanguage(PROFESSION_NOT_FOUND, employer.getEndonymInterfaceLanguage()));
        }
        if (jobAddress == null || jobAddress.length() > 50 || latitude == null || longitude == null) {
            errors.add(checkAndGetMessageInSpecifiedLanguage(INCORRECT_JOB_ADDRESS, employer.getEndonymInterfaceLanguage()));
        }
        if (info != null) {
            if (info.length() > 200) {
                errors.add(checkAndGetMessageInSpecifiedLanguage(TOO_MANY_ADDITIONAL_INFO, employer.getEndonymInterfaceLanguage()));
            }
        }
        if (waitingDateTime == null || startDateTime == null) {
            errors.add(checkAndGetMessageInSpecifiedLanguage(SPECIFICATION_DATE_REQUIREMENT, employer.getEndonymInterfaceLanguage()));
        }
        if (templateName.length() > 20) {
            errors.add(checkAndGetMessageInSpecifiedLanguage(TOO_LONG_TEMPLATE_NAME, employer.getEndonymInterfaceLanguage()));
        }
        if (quotasNumber < 1) {
            errors.add(checkAndGetMessageInSpecifiedLanguage(QUOTAS_NUMBER_ERROR, employer.getEndonymInterfaceLanguage()));
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
        vacancy.setProfession(profession.get());
        JobLocation jobLocation = new JobLocation();
        jobLocation.setVacancy(vacancy);
        jobLocation.setJobAddress(vacancyRequest.getJobAddress());
        jobLocation.setLatitude(latitude);
        jobLocation.setLongitude(longitude);
        vacancy.setJobLocation(jobLocation);
        vacancy.setPaymentAndAdditionalInfo(info);
        vacancy.setEmployerRequests(employer.getEmployerRequests());
        vacancy.setStartDateTime(startDateTime);
        vacancy.setWaitingDateTime(waitingDateTime);
        vacancy.setType("PUBLIC");
        vacancy.setQuotasNumber(quotasNumber);
        vacancy.setNecessaryToCloseAllQuotas(isNecessaryToCloseAllQuotas);
        vacancyRepository.save(vacancy);
        if (isSaveTemplate) {
            Vacancy template = new Vacancy();
            template.setProfession(profession.get());
            jobLocation.setVacancy(template);
            jobLocation.setJobAddress(vacancyRequest.getJobAddress());
            jobLocation.setLatitude(latitude);
            jobLocation.setLongitude(longitude);
            template.setJobLocation(jobLocation);
            template.setPaymentAndAdditionalInfo(info);
            template.setEmployerRequests(employer.getEmployerRequests());
            template.setStartDateTime(null);
            template.setWaitingDateTime(null);
            template.setType("TEMPLATE");
            template.setQuotasNumber(quotasNumber);
            template.setNecessaryToCloseAllQuotas(isNecessaryToCloseAllQuotas);
            vacancyRepository.save(template);
        }
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