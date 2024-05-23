package com.example.navigator.controllers;
import com.example.navigator.api.request.*;
import com.example.navigator.api.response.*;
import com.example.navigator.model.User;
import com.example.navigator.service.SearchService;
import com.example.navigator.service.ProfileService;
import com.example.navigator.service.SystemService;
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

    @GetMapping("profession/name/get")
    @PreAuthorize("hasAuthority('user:work')")
    public ResponseEntity<StringResponse> getProfessionNameInSpecifiedLanguage
            (@RequestBody ProfessionToUserRequest professionToUserRequest) {

        return ResponseEntity.ok(systemService.getProfessionNameByIdAndLanguage(professionToUserRequest));
    }

    @GetMapping("professions/names/list/get")
    public ResponseEntity<TextListResponse> getProfessionsNamesInSpecifiedLanguage() {

        return ResponseEntity.ok(systemService.getProfessionsNamesInSpecifiedLanguage());
    }

    @GetMapping("profession/get/by/name")
    public ResponseEntity<IdResponse> getProfessionIdByName(@RequestBody StringRequest stringRequest) {

        return ResponseEntity.ok(systemService.getProfessionIdByName(stringRequest));
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
            (@RequestBody ProfessionToUserRequest professionToUserRequest) {

        return ResponseEntity.ok(profileService.getProfessionsToUserInEmployersLanguage(professionToUserRequest));
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

    @DeleteMapping("profession/to/user/delete")
    @PreAuthorize("hasAuthority('user:work')")
    public ResponseEntity<ResultErrorsResponse> deleteProfessionToUser
            (@RequestBody ProfessionToUserRequest professionToUserRequest) {

        return ResponseEntity.ok(profileService.deleteProfessionToUser(professionToUserRequest));
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
    @PreAuthorize("hasAuthority('user:work')")
    public ResponseEntity<ResultErrorsResponse> updateLocation(@RequestBody LocationRequest locationRequest) {

        return ResponseEntity.ok(systemService.updateLocation(locationRequest));
    }

    @PostMapping("restore")
    public ResponseEntity<ResultErrorsResponse> getRecoveryCode(@RequestBody StringRequest email) {

        return ResponseEntity.ok(profileService.checkEmailAndGetCode(email));
    }

    @PostMapping("password")
    public ResponseEntity<ResultErrorsResponse> changePassword(@RequestBody PasswordRequest passwordRequest) {

        return ResponseEntity.ok(profileService.checkAndChangePassword(passwordRequest));
    }

    @PutMapping("user/like")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<VoteResponse> like(@RequestBody VoteRequest voteRequest) {

        return ResponseEntity.ok(profileService.vote(voteRequest));
    }

    @PutMapping("user/dislike")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<VoteResponse> dislike(@RequestBody VoteRequest voteRequest) {

        return ResponseEntity.ok(profileService.vote(voteRequest));
    }

    @PostMapping("comment")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<ResultErrorsResponse> comment(@RequestBody CommentRequest commentRequest) {

        return ResponseEntity.ok(profileService.comment(commentRequest));
    }

    @PostMapping("user/{id}/favorite/{decision}")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<ResultErrorsResponse> favorite(@PathVariable long favoriteId, @PathVariable String decision) {

        return ResponseEntity.ok(profileService.changeFavoritesList(favoriteId, decision));
    }

    @PostMapping("user/{id}/blacklist/{decision}")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<ResultErrorsResponse> blackList(@PathVariable long bannedId, @PathVariable String decision) {

        return ResponseEntity.ok(profileService.changeBlackList(bannedId, decision));
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
    public ResponseEntity<UserInfoResponse> getUser() {

        return ResponseEntity.ok(profileService.getUserInfo());
    }
}