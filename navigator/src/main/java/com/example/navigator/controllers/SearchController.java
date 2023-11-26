package com.example.navigator.controllers;
import com.example.navigator.api.request.JobRequest;
import com.example.navigator.api.request.SearchRequest;
import com.example.navigator.api.response.EmployeeInfoResponse;
import com.example.navigator.api.response.EmployeesListResponse;
import com.example.navigator.api.response.ResultErrorsResponse;
import com.example.navigator.api.response.VacanciesListResponse;
import com.example.navigator.service.ProfileService;
import com.example.navigator.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api/search/")
public class SearchController {

    private SearchService searchService;
    private ProfileService profileService;

    SearchController(SearchService searchService, ProfileService profileService) {
        this.searchService = searchService;
        this.profileService = profileService;
    }

    @GetMapping("vacancies")
    @PreAuthorize("hasAuthority('user:work')")
    public ResponseEntity<VacanciesListResponse> getVacanciesByProfession(SearchRequest searchRequest) {

        return ResponseEntity.ok(searchService.getVacanciesByProfession(searchRequest));
    }

    @GetMapping("vacancies/name")
    @PreAuthorize("hasAuthority('user:work')")
    public ResponseEntity<VacanciesListResponse> getVacanciesByProfessionSortedByName(SearchRequest searchRequest) {

        return ResponseEntity.ok(searchService.getVacanciesByProfession(searchRequest));
    }

    @GetMapping("vacancies/location")
    @PreAuthorize("hasAuthority('user:work')")
    public ResponseEntity<VacanciesListResponse> getVacanciesByProfessionSortedByLocation(SearchRequest searchRequest) {

        return ResponseEntity.ok(searchService.getVacanciesByProfession(searchRequest));
    }

    @GetMapping("vacancies/rating")
    @PreAuthorize("hasAuthority('user:work')")
    public ResponseEntity<VacanciesListResponse> getVacanciesByProfessionSortedByRating(SearchRequest searchRequest) {

        return ResponseEntity.ok(searchService.getVacanciesByProfession(searchRequest));
    }

    @PostMapping("employer/search/passive")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<ResultErrorsResponse> setPassiveSearch(@RequestBody JobRequest jobRequest) {

        return ResponseEntity.ok(searchService.setPassiveSearch(jobRequest));
    }

    @GetMapping("employees")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<EmployeesListResponse> getEmployeesOfChosenProfession(
            @RequestBody SearchRequest searchRequest) {

        return ResponseEntity.ok(searchService.getEmployeesOfChosenProfession(searchRequest));
    }

    @GetMapping("employees/name")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<EmployeesListResponse> getEmployeesOfChosenProfessionSortedByName(
            @RequestBody SearchRequest searchRequest) {

        return ResponseEntity.ok(searchService.getEmployeesOfChosenProfession(searchRequest));
    }

    @GetMapping("employees/nearest")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<EmployeesListResponse> getEmployeesOfChosenProfessionSortedByLocation(
            @RequestBody SearchRequest searchRequest) {

        return ResponseEntity.ok(searchService.getEmployeesOfChosenProfessionSortedByLocation(searchRequest));
    }

    @GetMapping("employees/best")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<EmployeesListResponse> getEmployeesOfChosenProfessionSortedByRating(
            @RequestBody SearchRequest searchRequest) {

        return ResponseEntity.ok(searchService.getEmployeesOfChosenProfessionSortByRating(searchRequest));
    }

    @GetMapping("employee/info/{id}")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<EmployeeInfoResponse> getEmployeeInfo(@PathVariable long id) {

        return ResponseEntity.ok(profileService.getEmployeeInfo(id));
    }
}