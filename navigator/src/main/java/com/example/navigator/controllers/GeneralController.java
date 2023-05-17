package com.example.navigator.controllers;
import com.example.navigator.api.request.*;
import com.example.navigator.api.response.AvatarResponse;
import com.example.navigator.api.response.JobListResponse;
import com.example.navigator.api.response.ResultErrorsResponse;
import com.example.navigator.api.response.VoteResponse;
import com.example.navigator.model.User;
import com.example.navigator.service.EmployeeAndEmployerService;
import com.example.navigator.service.ProfileService;
import com.example.navigator.service.SystemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;

@Controller
@RequestMapping("api/")
public class GeneralController {

    private ProfileService profileService;
    private EmployeeAndEmployerService employeeAndEmployerService;
    private SystemService systemService;

    GeneralController(ProfileService profileService, EmployeeAndEmployerService employeeAndEmployerService,
                      SystemService systemService) {
        this.profileService = profileService;
        this.employeeAndEmployerService = employeeAndEmployerService;
        this.systemService = systemService;
    }

    @PutMapping("moderator")
    public ResponseEntity<ResultErrorsResponse> setModerator(Principal principal) { // способ становления модератором под вопросов

        return ResponseEntity.ok(profileService.setModerator(principal));
    }

    @PostMapping("system/code/add")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<ResultErrorsResponse> addMessageCodeName(@RequestParam(value = "code") String codeName,
                                                                   Principal principal) {

        return ResponseEntity.ok(systemService.addMessageCodeName(codeName, principal));
    }

    @PostMapping("system/message/add")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<ResultErrorsResponse> addInProgramMessage(
            @RequestBody InProgramMessageRequest inProgramMessageRequest, Principal principal) {

        return ResponseEntity.ok(systemService.addInProgramMessage(inProgramMessageRequest, principal));
    }

    @PostMapping("language/add")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<ResultErrorsResponse> addLanguage(@RequestParam(value = "language") String language) {

        return ResponseEntity.ok(systemService.addLanguage(language));
    }

    @PutMapping("profile/avatar")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work') or hasAuthority('user:moderate')")
    public ResponseEntity<AvatarResponse> profileAvatar(@RequestParam(value = "avatar") MultipartFile avatar,
                                                        Principal principal) {

        return ResponseEntity.ok(profileService.writeAvatar(avatar, principal));
    }


    @PutMapping("profile")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work') or hasAuthority('user:moderate')")
    public ResponseEntity<ResultErrorsResponse> profile(@RequestBody ProfileRequest profileRequest,
                                                        Principal principal) {

        return ResponseEntity.ok(profileService.checkAndChangeProfile(profileRequest, principal));
    }

    @PostMapping("professions/name/add")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<ResultErrorsResponse> addProfessionInSpecifiedLanguage(@RequestBody ProfessionRequest professionRequest,
                                                                                 Principal principal) {

        return ResponseEntity.ok(systemService.addProfessionInSpecifiedLanguage(professionRequest, principal));
    }

    @PostMapping("professions/new/add")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<ResultErrorsResponse> addNewProfessionId() {

        return ResponseEntity.ok(systemService.addNewProfessionId());
    }

    @PutMapping("block")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<ResultErrorsResponse> blockUser(@RequestBody ModeratorDecision moderatorDecision) {

        return ResponseEntity.ok(profileService.changeUserCondition(moderatorDecision));
    }

    @PutMapping("unblock")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<ResultErrorsResponse> unblockUser(@RequestBody ModeratorDecision moderatorDecision) {

        return ResponseEntity.ok(profileService.changeUserCondition(moderatorDecision));
    }

    @PutMapping("location/{id}")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work') or hasAuthority('user:moderate')")
    public ResponseEntity<ResultErrorsResponse> updateLocation(@PathVariable long id,
                                               @RequestBody LocationRequest locationRequest) {

        return ResponseEntity.ok(employeeAndEmployerService.updateLocation(id, locationRequest));
    }

    @PostMapping("restore")
    public ResponseEntity<ResultErrorsResponse> getRecoveryCode(@RequestParam String email) {

        return ResponseEntity.ok(profileService.checkEmailAndGetCode(email));
    }

    @PostMapping("password")
    public ResponseEntity<ResultErrorsResponse> changePassword(@RequestBody PasswordRequest passwordRequest) {

        return ResponseEntity.ok(profileService.checkAndChangePassword(passwordRequest));
    }

    @PutMapping("user/like")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<VoteResponse> like(@RequestBody VoteRequest voteRequest) {

        return ResponseEntity.ok(employeeAndEmployerService.vote(voteRequest));
    }

    @PutMapping("user/dislike")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<VoteResponse> dislike(@RequestBody VoteRequest voteRequest) {

        return ResponseEntity.ok(employeeAndEmployerService.vote(voteRequest));
    }

    @PostMapping("comment")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<ResultErrorsResponse> comment(@RequestBody CommentRequest commentRequest, Principal principal) {

        return ResponseEntity.ok(profileService.comment(commentRequest, principal));
    }

    @PostMapping("employer/search/passive")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<ResultErrorsResponse> setPassiveSearch(@RequestBody JobRequest jobRequest, Principal principal) {

        return ResponseEntity.ok(employeeAndEmployerService.setPassiveSearch(jobRequest, principal));
    }

    @PostMapping("user/{id}/favorite/{decision}")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<ResultErrorsResponse> favorite(@PathVariable long favoriteId,
                                                         @PathVariable String decision, Principal principal) {

        return ResponseEntity.ok(profileService.changeFavoritesList(favoriteId, decision, principal));
    }

    @PostMapping("user/{id}/blacklist/{decision}")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<ResultErrorsResponse> blackList(@PathVariable long bannedId,
                                                          @PathVariable String decision, Principal principal) {

        return ResponseEntity.ok(profileService.changeBlackList(bannedId, decision, principal));
    }

    @GetMapping("user/sender/get")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<User> getSender(Principal principal) {

        return ResponseEntity.ok(profileService.getSender(principal));
    }

    @GetMapping("user/recipient/{id}/get")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<User> getRecipient(@PathVariable long recipientId) {

        return ResponseEntity.ok(profileService.getRecipient(recipientId));
    }

    @GetMapping("jobs")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<JobListResponse> getJobList(Principal principal) {

        return ResponseEntity.ok(profileService.getJobList(principal));
    }

    @DeleteMapping("jobs/delete")
    public ResponseEntity<ResultErrorsResponse> checkAndDeleteNotConfirmedJobs() { // реализация пока под вопросом

        return ResponseEntity.ok(employeeAndEmployerService.checkAndDeleteNotConfirmedJobs());
    }
}
