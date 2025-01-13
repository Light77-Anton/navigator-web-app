package com.example.navigator.controllers;
import com.example.navigator.api.request.ChatRequest;
import com.example.navigator.api.request.DecisionRequest;
import com.example.navigator.api.request.VacancyRequest;
import com.example.navigator.api.response.*;
import com.example.navigator.model.ChatMessage;
import com.example.navigator.service.ChatMessageService;
import com.example.navigator.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("api/chat/")
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ChatMessageService chatMessageService;
    @Autowired
    private SearchService searchService;
    private final String URL = "/queue/reply";

    @MessageMapping("message/get")
    @SendTo("queue/{id}")
    public ResponseEntity<> getIncomingMessage(@Payload Object messageObject, @PathVariable long id) {
        if (messageObject instanceof ChatMessage) { // переписка

        } else if (messageObject instanceof ExtendedUserInfoResponse) { // предложение от рабочего

        } else if (messageObject instanceof VacancyRequest) { // предложение от рабочетодателя

        } else if () {

        }
    }

    @MessageMapping("response/job/terminate")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<TerminateJobResponse> responseToTerminateJob(@Payload TerminateJobResponse terminateJobResponse) {
        TerminateJobResponse recipientAnswer = chatMessageService
                .responseToTerminateJob(terminateJobResponse);
        messagingTemplate.convertAndSendToUser(recipientAnswer.getSenderId().toString(), URL, recipientAnswer);

        return ResponseEntity.ok(recipientAnswer);
    }

    @MessageMapping("request/job/terminate")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<TerminateJobResponse> requestToTerminateJob(@Payload Job jobToTerminate, Principal principal) {
        TerminateJobResponse terminateJobResponse = chatMessageService
                .requestToTerminateJob(jobToTerminate, principal);
        messagingTemplate.convertAndSendToUser(terminateJobResponse.getRecipientId().toString(), URL, terminateJobResponse);

        return ResponseEntity.ok(terminateJobResponse);
    }

    @MessageMapping("offer/decision")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<AnswerToOfferResponse> giveDecisionToOffer(@RequestBody DecisionRequest decision) {
        AnswerToOfferResponse answerToOfferResponse = chatMessageService.answerToOffer(decision);
        if (answerToOfferResponse.getError().isEmpty()) {
            messagingTemplate.convertAndSendToUser(answerToOfferResponse.getRecipientId().toString(), URL, answerToOfferResponse);
        }

        return ResponseEntity.ok(answerToOfferResponse);
    }

    @MessageMapping("employer/offer/{userId}")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<ResultErrorsResponse> sendOfferFromEmployer(@DestinationVariable("userId") String userId,
                                                                      @RequestBody VacancyRequest vacancyRequest) {
        ResultErrorsResponse resultErrorsResponse = chatMessageService.checkOfferFromEmployer(vacancyRequest);
        if (resultErrorsResponse.isResult()) {
            Map<String, Object> header = new HashMap<>();
            header.put("payload_type", "vacancy_request");
            messagingTemplate.convertAndSendToUser(userId, URL, vacancyRequest, header);
        }

        return ResponseEntity.ok(resultErrorsResponse);
    }

    @MessageMapping("employee/offer/{userId}")
    @PreAuthorize("hasAuthority('user:work')")
    public ResponseEntity<ExtendedUserInfoResponse> sendEmployeesOffer(@DestinationVariable("userId") String userId) {
        ExtendedUserInfoResponse extendedUserInfoResponse = chatMessageService
                .sendEmployeesOffer(employerPassiveSearchRequest);
        if (extendedUserInfoResponse.getError() == null) {
            messagingTemplate.convertAndSendToUser
                    (extendedUserInfoResponse.getToEmployerId().toString(), URL, extendedUserInfoResponse);
        }

        return ResponseEntity.ok(extendedUserInfoResponse);
    }

    @MessageMapping("message/{userId}")
    @PreAuthorize("hasAuthority('user:work') or hasAuthority('user:hire') or hasAuthority('user:moderate')")
    public ResponseEntity<ChatMessageResponse> processMessage(@DestinationVariable("userId") String userId,
                                                              @RequestBody ChatRequest chatRequest) {
        ChatMessageResponse response = chatMessageService.saveNewMessage(chatRequest);
        Map<String, Object> header = new HashMap<>();
        header.put("payload_type", "chat_message");
        messagingTemplate.convertAndSendToUser(userId, URL, response.getMessage(), header);

        return ResponseEntity.ok(response);
    }

    @GetMapping("messages/{senderId}/{recipientId}/count")
    @PreAuthorize("hasAuthority('user:work') or hasAuthority('user:hire')")
    public ResponseEntity<ChatMessageResponse> countNewMessages(@PathVariable long senderId, @PathVariable long recipientId) {

        return ResponseEntity.ok(chatMessageService.countNewMessages(senderId, recipientId));
    }

    @GetMapping("messages/find")
    @PreAuthorize("hasAuthority('user:work') or hasAuthority('user:hire')")
    public ResponseEntity<ChatMessageResponse> findChatMessages(@RequestBody ChatRequest chatRequest) {

        return ResponseEntity.ok(chatMessageService.findAllMessages(chatRequest));
    }

    @GetMapping("messages/{id}")
    @PreAuthorize("hasAuthority('user:work') or hasAuthority('user:hire')")
    public ResponseEntity<ChatMessageResponse> findMessage(@PathVariable long id) {

        return ResponseEntity.ok(chatMessageService.findById(id));
    }

    @GetMapping("open")
    @PreAuthorize("hasAuthority('user:work') or hasAuthority('user:hire')")
    public ResponseEntity<ResultErrorsResponse> openChat(@RequestBody ChatRequest chatRequest) {

        return ResponseEntity.ok(chatMessageService.openChat(chatRequest));
    }
}