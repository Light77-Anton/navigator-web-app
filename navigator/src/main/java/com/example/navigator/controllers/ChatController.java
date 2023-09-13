package com.example.navigator.controllers;
import com.example.navigator.api.request.DecisionRequest;
import com.example.navigator.api.request.EmployerPassiveSearchRequest;
import com.example.navigator.api.request.JobRequest;
import com.example.navigator.api.response.*;
import com.example.navigator.model.ChatMessage;
import com.example.navigator.model.ChatNotification;
import com.example.navigator.model.Job;
import com.example.navigator.service.ChatMessageService;
import com.example.navigator.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.security.Principal;

@Controller
@RequestMapping("api/chat/")
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ChatMessageService chatMessageService;
    @Autowired
    private SearchService searchService;
    private final String URL = "/queue/messages";

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

    @MessageMapping("offer/answer")
    @PreAuthorize("hasAuthority('user:hire') or hasAuthority('user:work')")
    public ResponseEntity<AnswerToOfferResponse> giveAnswerToOffer(@Payload DecisionRequest decision, Principal principal) {
        AnswerToOfferResponse answerToOfferResponse = chatMessageService.answerToOffer(decision, principal);
        if (answerToOfferResponse.getError().isEmpty()) {
            messagingTemplate.convertAndSendToUser(answerToOfferResponse.getRecipientId().toString(), URL, answerToOfferResponse);
        }

        return ResponseEntity.ok(answerToOfferResponse);
    }

    @MessageMapping("employer/offer")
    @PreAuthorize("hasAuthority('user:hire')")
    public ResponseEntity<JobResponse> sendEmployersOffer(@Payload JobRequest jobRequest) {
        JobResponse jobResponse = chatMessageService.sendEmployerOffer(jobRequest);
        if (jobResponse.isResult()) {
            messagingTemplate.convertAndSendToUser(jobRequest.getUserId().toString(), URL, jobResponse.getJob());
        }

        return ResponseEntity.ok(jobResponse);
    }

    @MessageMapping("employee/offer")
    @PreAuthorize("hasAuthority('user:work')")
    public ResponseEntity<EmployeeInfoResponse> sendEmployeesOffer(
            @Payload EmployerPassiveSearchRequest employerPassiveSearchRequest) {
        EmployeeInfoResponse employeeInfoResponse = chatMessageService
                .sendEmployeesOffer(employerPassiveSearchRequest);
        if (employeeInfoResponse.getError() == null) {
            messagingTemplate.convertAndSendToUser
                    (employeeInfoResponse.getToEmployerId().toString(), URL, employeeInfoResponse);
        }

        return ResponseEntity.ok(employeeInfoResponse);
    }

    @MessageMapping("message")
    @PreAuthorize("hasAuthority('user:work') or hasAuthority('user:hire')")
    public void processMessage(@Payload ChatMessage chatMessage) {
        if (chatMessage.getChat() == null) {
            chatMessageService.createChat(chatMessage);
        }
        ChatMessageResponse response = chatMessageService.saveNewMessage(chatMessage,
                chatMessageService.getChat(chatMessage.getSender().getId(), chatMessage.getRecipient().getId()));
        ChatNotification chatNotification = new ChatNotification(response.getMessage().getId(),
                response.getMessage().getSender());

        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipient().getId().toString(),URL, chatNotification);
    }

    @GetMapping("messages/{senderId}/{recipientId}/count")
    @PreAuthorize("hasAuthority('user:work') or hasAuthority('user:hire')")
    public ResponseEntity<ChatMessageResponse> countNewMessages(@PathVariable long senderId, @PathVariable long recipientId) {

        return ResponseEntity.ok(chatMessageService.countNewMessages(senderId, recipientId));
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    @PreAuthorize("hasAuthority('user:work') or hasAuthority('user:hire')")
    public ResponseEntity<ChatMessageResponse> findChatMessages(@PathVariable long senderId, @PathVariable long recipientId) {

        return ResponseEntity.ok(chatMessageService.findAllMessages(senderId, recipientId));
    }

    @GetMapping("/messages/{id}")
    @PreAuthorize("hasAuthority('user:work') or hasAuthority('user:hire')")
    public ResponseEntity<ChatMessageResponse> findMessage(@PathVariable long id) {

        return ResponseEntity.ok(chatMessageService.findById(id));
    }
}