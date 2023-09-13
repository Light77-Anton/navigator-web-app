package com.example.navigator.controllers;
import com.example.navigator.api.request.JobRequest;
import com.example.navigator.api.request.RequestForEmployees;
import com.example.navigator.api.response.EmployeeInfoResponse;
import com.example.navigator.api.response.EmployeesListResponse;
import com.example.navigator.api.response.ResultErrorsResponse;
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

    @PostMapping("employer/search/passive")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<ResultErrorsResponse> setPassiveSearch(@RequestBody JobRequest jobRequest) { //

        return ResponseEntity.ok(searchService.setPassiveSearch(jobRequest));
    }

    @GetMapping("employees")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<EmployeesListResponse> getEmployeesOfChosenProfession(
            @RequestBody RequestForEmployees requestForEmployees) {

        return ResponseEntity.ok(searchService.getEmployeesOfChosenProfession(requestForEmployees));
    }

    @GetMapping("employees/nearest")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<EmployeesListResponse> getTheNearestEmployeeOfChosenProfession(
            @RequestBody RequestForEmployees requestForEmployees) {

        return ResponseEntity.ok(searchService.getTheNearestEmployeesInfo(requestForEmployees));
    }

    @GetMapping("employees/best")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<EmployeesListResponse> getTheBestEmployees(
            @RequestBody RequestForEmployees requestForEmployees) {

        return ResponseEntity.ok(searchService.getTheBestEmployees(requestForEmployees));
    }

    @GetMapping("employee/info/{id}")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<EmployeeInfoResponse> getEmployeeInfo(@PathVariable long id) {

        return ResponseEntity.ok(profileService.getEmployeeInfo(id));
    }
}