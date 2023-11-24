package com.example.navigator.controllers;
import com.example.navigator.api.request.JobRequest;
import com.example.navigator.api.request.RequestForEmployees;
import com.example.navigator.api.response.EmployeeInfoResponse;
import com.example.navigator.api.response.EmployeesListResponse;
import com.example.navigator.api.response.ResultErrorsResponse;
import com.example.navigator.api.response.VacanciesList;
import com.example.navigator.service.ProfileService;
import com.example.navigator.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
@RequestMapping("api/search/")
public class SearchController {

    private SearchService searchService;
    private ProfileService profileService;

    SearchController(SearchService searchService, ProfileService profileService) {
        this.searchService = searchService;
        this.profileService = profileService;
    }

    @GetMapping("vacancies/{profession}")
    @PreAuthorize("hasAuthority('user:work')")
    public ResponseEntity<VacanciesList> getVacanciesByProfession(@PathVariable String profession) {

        return ResponseEntity.ok()
    }

    @PostMapping("employer/search/passive")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<ResultErrorsResponse> setPassiveSearch(@RequestBody JobRequest jobRequest) {

        return ResponseEntity.ok(searchService.setPassiveSearch(jobRequest));
    }

    @GetMapping("employees")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<EmployeesListResponse> getEmployeesOfChosenProfession(
            @RequestBody RequestForEmployees requestForEmployees) {

        return ResponseEntity.ok(searchService.getEmployeesOfChosenProfession(requestForEmployees));
    }

    @GetMapping("employees/name")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<EmployeesListResponse> getEmployeesOfChosenProfessionSortedByName(
            @RequestBody RequestForEmployees requestForEmployees) {

        return ResponseEntity.ok(searchService.getEmployeesOfChosenProfession(requestForEmployees));
    }

    @GetMapping("employees/nearest")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<EmployeesListResponse> getEmployeesOfChosenProfessionSortedByLocation(
            @RequestBody RequestForEmployees requestForEmployees) {

        return ResponseEntity.ok(searchService.getEmployeesOfChosenProfessionSortedByLocation(requestForEmployees));
    }

    @GetMapping("employees/best")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<EmployeesListResponse> getEmployeesOfChosenProfessionSortedByRating(
            @RequestBody RequestForEmployees requestForEmployees) {

        return ResponseEntity.ok(searchService.getEmployeesOfChosenProfessionSortByRating(requestForEmployees));
    }

    @GetMapping("employee/info/{id}")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<EmployeeInfoResponse> getEmployeeInfo(@PathVariable long id) {

        return ResponseEntity.ok(profileService.getEmployeeInfo(id));
    }
}