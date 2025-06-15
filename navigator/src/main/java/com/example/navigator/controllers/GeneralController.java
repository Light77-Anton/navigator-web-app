package com.example.navigator.controllers;
import com.example.navigator.api.request.*;
import com.example.navigator.api.response.*;
import com.example.navigator.model.User;
import com.example.navigator.service.SearchService;
import com.example.navigator.service.ProfileService;
import com.example.navigator.service.SystemService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("api/")
public class GeneralController {

    private ProfileService profileService;
    private SearchService searchService;
    private SystemService systemService;

    GeneralController(ProfileService profileService, SearchService searchService,
                      SystemService systemService) {
        this.profileService = profileService;
        this.searchService = searchService;
        this.systemService = systemService;
    }

    @GetMapping("templates/get")
    public ResponseEntity<VacancyListResponse> getTemplatesList() {

        return ResponseEntity.ok(profileService.getTemplatesList());
    }

    @GetMapping("system/message/get")
    public ResponseEntity<StringResponse> getMessageInSpecifiedLanguage
            (@RequestBody InProgramMessageRequest inProgramMessageRequest) {

        return ResponseEntity.ok(systemService.checkAndGetSingleMessageInSpecifiedLanguage(inProgramMessageRequest.getCodeName()
                , inProgramMessageRequest.getLanguage()));
    }

    @GetMapping("languages/list/get")
    public ResponseEntity<TextListResponse> getLanguagesList() {

        return ResponseEntity.ok(systemService.getLanguagesList());
    }

    @GetMapping("vacancy/info/get")
    @PreAuthorize("hasAuthority('user:work')")
    public ResponseEntity<StringResponse> getAdditionalInfoAboutVacancyInSpecifiedLanguage
            (@RequestBody ProfessionToUserRequest professionToUserRequest) {

        return ResponseEntity.ok(systemService.getAdditionalInfoAboutVacancyInSpecifiedLanguage(professionToUserRequest));
    }

    @GetMapping("profession/{id}/name/get")
    @PreAuthorize("hasAuthority('user:work')")
    public ResponseEntity<StringResponse> getProfessionNameInSpecifiedLanguage(@PathVariable long id) {

        return ResponseEntity.ok(systemService.getProfessionNameByIdAndLanguage(id));
    }

    @GetMapping("professions/names/list/get")
    public ResponseEntity<ProfessionNamesListResponse> getProfessionsNamesInSpecifiedLanguage() {

        return ResponseEntity.ok(systemService.getProfessionsNamesInSpecifiedLanguage());
    }

    @GetMapping("profession/get/by/name/{profession}")
    public ResponseEntity<IdResponse> getProfessionIdByName(@PathVariable String professionName) {

        return ResponseEntity.ok(systemService.getProfessionIdByName(professionName));
    }

    @GetMapping("info/from/employee/get")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<StringResponse> getInfoFromEmployeeInEmployersLanguage
            (@RequestBody ProfessionToUserRequest professionToUserRequest) {

        return ResponseEntity.ok(profileService.getInfoFromEmployeeInEmployersLanguage(professionToUserRequest));
    }

    @GetMapping("professions/to/employee/get")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<StringResponse> getProfessionsToUserInEmployersLanguage
            (@RequestBody long id) {

        return ResponseEntity.ok(profileService.getProfessionsToUserInEmployersLanguage(id));
    }

    @GetMapping("profession/to/user/get")
    @PreAuthorize("hasAuthority('user:work')")
    public ResponseEntity<ProfessionToUserResponse> getProfessionToUser
            (@RequestBody ProfessionToUserRequest professionToUserRequest) {

        return ResponseEntity.ok(profileService.getProfessionToUser(professionToUserRequest));
    }

    @PostMapping("profession/to/user/post")
    @PreAuthorize("hasAuthority('user:work')")
    public ResponseEntity<ResultErrorsResponse> postProfessionToUser
            (@RequestBody ProfessionToUserRequest professionToUserRequest) {

        return ResponseEntity.ok(profileService.postProfessionToUser(professionToUserRequest));
    }

    @DeleteMapping("professions/to/user/clear")
    @PreAuthorize("hasAuthority('user:work')")
    public ResponseEntity<ResultErrorsResponse> clearProfessionsToUser () {

        return ResponseEntity.ok(profileService.clearProfessionsToUser());
    }

    @GetMapping("system/text/get")
    public ResponseEntity<MapTextResponse> checkAndGetTextListInSpecifiedLanguage
            (@RequestBody TextListInSpecifiedLanguageRequest textList) {
        MapTextResponse mapTextResponse = new MapTextResponse();
        mapTextResponse.setMap(systemService.checkAndGetTextListInSpecifiedLanguage(textList));

        return ResponseEntity.ok(mapTextResponse);
    }

    @GetMapping("language/get")
    public ResponseEntity<StringResponse> getUsersInterfaceLanguage() {

        return ResponseEntity.ok(profileService.getUsersInterfaceLanguage());
    }

    @PutMapping("moderator")
    public ResponseEntity<ResultErrorsResponse> setModerator() { // способ становления модератором под вопросов

        return ResponseEntity.ok(profileService.setModerator());
    }

    @PostMapping("system/code/add")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<ResultErrorsResponse> addMessageCodeName(@RequestParam(value = "code") String codeName) {

        return ResponseEntity.ok(systemService.addMessageCodeName(codeName));
    }

    @PostMapping("system/message/add")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<ResultErrorsResponse> addInProgramMessage(
            @RequestBody InProgramMessageRequest inProgramMessageRequest) {

        return ResponseEntity.ok(systemService.addInProgramMessage(inProgramMessageRequest));
    }

    @PostMapping("language/add")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<ResultErrorsResponse> addLanguage(@RequestParam(value = "language") String language) {

        return ResponseEntity.ok(systemService.addLanguage(language));
    }

    @PutMapping("profile/avatar")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work') or hasAuthority('user:moderate')")
    public ResponseEntity<AvatarResponse> profileAvatar(@RequestPart(value = "avatar") MultipartFile avatar) {

        return ResponseEntity.ok(profileService.writeAvatar(avatar));
    }

    @PutMapping("profile")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work') or hasAuthority('user:moderate')")
    public ResponseEntity<ResultErrorsResponse> profile(@RequestBody ProfileRequest profileRequest) {

        return ResponseEntity.ok(profileService.checkAndChangeProfile(profileRequest));
    }

    @PutMapping("status")
    @PreAuthorize("hasAuthority('user:work')")
    public ResponseEntity<ResultErrorsResponse> employeeStatus(@RequestBody StatusRequest statusRequest) {

        return ResponseEntity.ok(profileService.employeeStatus(statusRequest));
    }

    @PutMapping("status/check")
    @PreAuthorize("hasAuthority('user:work')")
    public ResponseEntity<ResultErrorsResponse> checkEmployeeStatus() {

        return ResponseEntity.ok(profileService.checkEmployeeStatus());
    }

    @PostMapping("professions/name/add")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<ResultErrorsResponse> addProfessionInSpecifiedLanguage(@RequestBody ProfessionRequest professionRequest) {

        return ResponseEntity.ok(systemService.addProfessionInSpecifiedLanguage(professionRequest));
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

    @PutMapping("location")
    @PreAuthorize("hasAuthority('user:work') or hasAuthority('user:hire')")
    public ResponseEntity<ResultErrorsResponse> updateLocation(@RequestBody LocationRequest locationRequest) {

        return ResponseEntity.ok(systemService.updateLocation(locationRequest));
    }

    @PostMapping("restore")
    public ResponseEntity<ResultErrorsResponse> getRecoveryCode(@RequestBody StringRequest email) {

        return ResponseEntity.ok(profileService.checkEmailAndGetCode(email));
    }

    @PostMapping("profile/password/change")
    public ResponseEntity<ResultErrorsResponse> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {

        return ResponseEntity.ok(profileService.checkAndChangePassword(changePasswordRequest));
    }

    @PutMapping("vote")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<VoteResponse> vote(@RequestBody VoteRequest voteRequest) {

        return ResponseEntity.ok(profileService.vote(voteRequest));
    }

    @PostMapping("comment")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<ResultErrorsResponse> comment(@RequestBody CommentRequest commentRequest) {

        return ResponseEntity.ok(profileService.comment(commentRequest));
    }

    @PostMapping("reply/{id}")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work') or hasAuthority('user:moderate')")
    public ResponseEntity<ResultErrorsResponse> reply(@PathVariable("id") long id, @RequestBody CommentRequest commentRequest) {

        return ResponseEntity.ok(profileService.reply(id, commentRequest));
    }

    @GetMapping("get/{id}/comments/{sort}")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work') or hasAuthority('user:moderate')")
    public ResponseEntity<CommentsListResponse> getCommentsListByUserId(@PathVariable("id") long id,
                                                                        @PathVariable("sort") Byte sort) {

        return ResponseEntity.ok(profileService.getCommentsListByRecipientId(id, sort));
    }

    @PostMapping("user/{id}/favorite/{decision}")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<ResultErrorsResponse> favorite(@PathVariable long id, @PathVariable String decision) {

        return ResponseEntity.ok(profileService.changeFavoritesList(id, decision));
    }

    @PostMapping("user/{id}/blacklist/{decision}")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<ResultErrorsResponse> blackList(@PathVariable long id, @PathVariable String decision) {

        return ResponseEntity.ok(profileService.changeBlackList(id, decision));
    }

    @GetMapping("user/{id}/relationship/status")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<RelationshipStatusResponse> getRelationshipStatus(@PathVariable("id") long id) {

        return ResponseEntity.ok(profileService.getRelationshipStatus(id));
    }

    @GetMapping("user/sender/get")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<User> getSender() {

        return ResponseEntity.ok(profileService.getSender());
    }

    @GetMapping("user/recipient/{id}/get")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<User> getRecipient(@PathVariable long recipientId) {

        return ResponseEntity.ok(profileService.getRecipient(recipientId));
    }

    @GetMapping("jobs")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<JobListResponse> getJobList() {

        return ResponseEntity.ok(profileService.getJobList());
    }

    @DeleteMapping("jobs/delete")
    public ResponseEntity<ResultErrorsResponse> checkAndDeleteNotConfirmedJobs() { // реализация пока под вопросом

        return ResponseEntity.ok(systemService.checkAndDeleteNotConfirmedJobs());
    }

    @GetMapping("professions")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work') or hasAuthority('user:moderate')")
    public ResponseEntity<ProfessionsResponse> getProfessionList() {

        return ResponseEntity.ok(systemService.getProfessionsList());
    }

    @GetMapping("user/get")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work') or hasAuthority('user:moderate')")
    public ResponseEntity<UserInfoResponse> getUserInfo() {

        return ResponseEntity.ok(profileService.getUserInfo());
    }

    @GetMapping("user/avatar/{path}/get")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work') or hasAuthority('user:moderate')")
    public ResponseEntity<Resource> getUserAvatar(@PathVariable("path") String path) {

        return ResponseEntity.ok();
    }

    @PutMapping("user/display/change")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work') or hasAuthority('user:moderate')")
    public ResponseEntity<ResultErrorsResponse> changeWorkDisplay() {

        return ResponseEntity.ok(profileService.changeWorkDisplay());
    }

    @PutMapping("user/employee/work/info/change")
    @PreAuthorize("hasAuthority('user:work')")
    public ResponseEntity<ResultErrorsResponse> changeInfoFromEmployeeForEmployers(@RequestBody StringRequest stringRequest) {

        return ResponseEntity.ok(profileService.changeInfoFromEmployeeForEmployers(stringRequest));
    }

    @GetMapping("user/timers/list/get")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<TimersListResponse> getTimersList() {

        return ResponseEntity.ok(systemService.getTimersList());
    }

    @GetMapping("language/get/by/name")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<IdResponse> getLanguageIdByName(String name) {

        return ResponseEntity.ok(systemService.getLanguageIdByName(name));
    }

    @GetMapping("employee/{employeeId}/to/employer/{employerId}/votes/count/get")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<IdResponse> getAvailableVotesCount(@PathVariable("employeeId") long employeeId,
                                         @PathVariable("employerId") long employerId) {

        return ResponseEntity.ok(profileService.getAvailableVotesCount(employeeId, employerId));
    }

    @GetMapping("get/average/vote/from/sender/{senderId}/to/recipient/{recipientId}")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<IdResponse> getAverageVoteFromSenderToRecipient(@PathVariable("senderId") long senderId,
                                                                          @PathVariable("recipientId") long recipientId) {

        return ResponseEntity.ok(profileService.getAverageVoteFromSenderToRecipient(senderId, recipientId));
    }
}