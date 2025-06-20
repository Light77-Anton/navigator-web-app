package com.example.navigator.controllers;
import com.example.navigator.api.request.VacancyRequest;
import com.example.navigator.api.request.LocationsRequest;
import com.example.navigator.api.request.SearchRequest;
import com.example.navigator.api.response.*;
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

    @GetMapping("distance")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<DistanceResponse> getMeasuredDistance(@RequestBody LocationsRequest locationsRequest) {

        return ResponseEntity.ok(searchService.calculateDistance(locationsRequest));
    }

    @GetMapping("vacancies")
    @PreAuthorize("hasAuthority('user:work')")
    public ResponseEntity<SearchResponse> getVacanciesByProfession(@RequestBody SearchRequest searchRequest) {

        return ResponseEntity.ok(searchService.getVacanciesOfChosenProfession(searchRequest));
    }

    @PostMapping("employer/vacancy/set")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<ResultErrorsResponse> setVacancy(@RequestBody VacancyRequest vacancyRequest) {

        return ResponseEntity.ok(searchService.setVacancy(vacancyRequest));
    }

    @GetMapping("vacancy/{id}/get")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<VacancyInfoResponse> getVacancyById(@PathVariable("id") long id) {

        return ResponseEntity.ok(searchService.getVacancyById(id));
    }

    @DeleteMapping("vacancy/{id}/delete")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<ResultErrorsResponse> deleteVacancyById(@PathVariable("id") long id) {

        return ResponseEntity.ok(searchService.deleteVacancyById(id));
    }

    @GetMapping("employees")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<SearchResponse> getEmployeesOfChosenProfession(@RequestBody SearchRequest searchRequest) {

        return ResponseEntity.ok(searchService.getEmployeesOfChosenProfession(searchRequest));
    }

    @GetMapping("employee/{id}/info")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<ExtendedUserInfoResponse> getEmployeeInfo(@PathVariable("id") long id) {

        return ResponseEntity.ok(profileService.getEmployeeInfo(id));
    }

    @GetMapping("employer/{id}/info")
    @PreAuthorize("hasAuthority('user:work')")
    public ResponseEntity<ExtendedUserInfoResponse> getEmployerInfo(@PathVariable long id) {

        return ResponseEntity.ok(profileService.getEmployerInfo(id));
    }

    @GetMapping("saved/requests/get")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<SearchResponse> getSavedRequests() {

        return ResponseEntity.ok(searchService.getSavedRequests());
    }

    @GetMapping("save/request")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<ResultErrorsResponse> saveRequest(@RequestBody SearchRequest searchRequest) {

        return ResponseEntity.ok(searchService.saveRequest(searchRequest));
    }
}