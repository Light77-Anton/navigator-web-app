package com.example.navigator.controllers;
import com.example.navigator.api.request.RequestForEmployees;
import com.example.navigator.api.response.EmployeeInfoResponse;
import com.example.navigator.api.response.EmployeesListResponse;
import com.example.navigator.api.response.ProfessionsResponse;
import com.example.navigator.service.EmployeeAndEmployerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import java.security.Principal;

@Controller
@RequestMapping("api/search/")
public class SearchController {

    private EmployeeAndEmployerService employeeAndEmployerService;

    SearchController(EmployeeAndEmployerService employeeAndEmployerService) {
        this.employeeAndEmployerService = employeeAndEmployerService;
    }

    @GetMapping("professions")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work') or hasAuthority('user:moderate')")
    public ResponseEntity<ProfessionsResponse> getProfessionList(Principal principal) {

        return ResponseEntity.ok(employeeAndEmployerService.getProfessionsList(principal));
    }

    @GetMapping("employees")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<EmployeesListResponse> getEmployeesOfChosenProfession(
            @RequestBody RequestForEmployees requestForEmployees, Principal principal) {

        return ResponseEntity.ok(employeeAndEmployerService.getEmployeesOfChosenProfession(requestForEmployees, principal));
    }

    @GetMapping("employees/nearest")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<EmployeesListResponse> getTheNearestEmployeeOfChosenProfession(
            @RequestBody RequestForEmployees requestForEmployees, Principal principal) {

        return ResponseEntity.ok(employeeAndEmployerService.getTheNearestEmployeesInfo(requestForEmployees, principal));
    }

    @GetMapping("employees/best")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<EmployeesListResponse> getTheBestEmployees(
            @RequestBody RequestForEmployees requestForEmployees, Principal principal) {

        return ResponseEntity.ok(employeeAndEmployerService.getTheBestEmployees(requestForEmployees, principal));
    }

    @GetMapping("employee/info/{id}")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<EmployeeInfoResponse> getEmployeeInfo(@PathVariable long id, Principal principal) {

        return ResponseEntity.ok(employeeAndEmployerService.getEmployeeInfo(id, principal));
    }
}