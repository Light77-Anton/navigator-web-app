package com.example.navigator.service;
import com.example.navigator.api.request.DecisionRequest;
import com.example.navigator.api.request.EmployerPassiveSearchRequest;
import com.example.navigator.api.request.JobRequest;
import com.example.navigator.api.response.*;
import com.example.navigator.model.*;
import com.example.navigator.model.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@Service
public class ChatMessageService {

    @Autowired
    private EmployerRequestsRepository employerRequestsRepository;
    @Autowired
    private InProgramMessageRepository inProgramMessageRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private EmployerPassiveSearchDataRepository employerPassiveSearchDataRepository;
    @Autowired
    private ProfessionNameRepository professionNameRepository;

    private final String DEFAULT_LANGUAGE = "English";
    private final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy hh.mm");
    private final String USER_IS_TEMPORARILY_BUSY = "USER_IS_TEMPORARILY_BUSY";
    private final String OFFER_IS_NOT_EXIST = "OFFER_IS_NOT_EXIST";
    private final String USER_NOT_FOUND = "USER_NOT_FOUND";
    private final String PROFESSION_NOT_FOUND = "PROFESSION_NOT_FOUND";
    private final String PROFESSION_SPECIFICATION_REQUIREMENT = "PROFESSION_SPECIFICATION_REQUIREMENT";
    private final String INCORRECT_JOB_ADDRESS = "INCORRECT_JOB_ADDRESS";
    private final String TOO_MANY_ADDITIONAL_INFO = "TOO_MANY_ADDITIONAL_INFO";
    private final String SPECIFICATION_DATE_REQUIREMENT = "SPECIFICATION_DATE_REQUIREMENT";

    /*

    public TerminateJobResponse requestToTerminateJob(Job jobToTerminate, Principal principal) {
        TerminateJobResponse terminateJobResponse = new TerminateJobResponse();
        User user = userRepository.findByEmail(principal.getName()).get();
        terminateJobResponse.setJob(jobToTerminate);
        terminateJobResponse.setSenderName(user.getName());
        terminateJobResponse.setSenderId(user.getId());
        if (user.getRole().equals(Role.EMPLOYEE)) {
            terminateJobResponse.setRecipientId(jobToTerminate.getEmployerRequests().getEmployer().getId());
            terminateJobResponse.setRecipientName(jobToTerminate.getEmployerRequests().getEmployer().getName());
        } else {
            terminateJobResponse.setRecipientId(jobToTerminate.getEmployeeData().getEmployee().getId());
            terminateJobResponse.setRecipientName(jobToTerminate.getEmployeeData().getEmployee().getName());
        }

        return terminateJobResponse;
    }


    public TerminateJobResponse responseToTerminateJob(TerminateJobResponse terminateJobResponse) {
        if (terminateJobResponse.getRecipientAnswer().equals("AGREES")) {
            jobRepository.delete(terminateJobResponse.getJob());
            terminateJobResponse.setJob(null);
        }

        return terminateJobResponse;
    }

     */

    // досрочная отмена работы с любой стороны будет выполнятся без согласия противоположной стороны с возможностью постовить голос и комент

    private String checkAndGetMessageInSpecifiedLanguage(String codeName, String interfaceLanguage) {
        Optional<InProgramMessage> inProgramMessage = inProgramMessageRepository
                .findByCodeNameAndLanguage(codeName, interfaceLanguage);
        if (inProgramMessage.isPresent()) {
            return inProgramMessage.get().getMessage();
        }
        return inProgramMessageRepository.findByCodeNameAndLanguage(codeName, DEFAULT_LANGUAGE).get().getMessage();
    }

    public AnswerToOfferResponse answerToOffer(DecisionRequest decisionRequest, Principal principal) {
        AnswerToOfferResponse answerToOfferResponse = new AnswerToOfferResponse();
        User user = userRepository.findByEmail(principal.getName()).get();
        String decision = decisionRequest.getDecision();
        Job job = jobRepository.findById(decisionRequest.getNotConfirmedJobId()).get();
        if (decision.equals("ACCEPT")) {
            if (user.getRole().equals(Role.EMPLOYEE)) {
                job.setStatus("IN PROCESS");
                job.setExpirationTime(null);
                if (job.getDesignatedDateTime() != null) {
                    LocalDate date = LocalDate.parse(job.getDesignatedDateTime().format(FORMAT));
                    user.getEmployeeData().setStatus("Employee will be active "+ date.plusDays(1));
                } else {
                    LocalDate endDate = LocalDate.parse(job.getEndDateTime().format(FORMAT));
                    user.getEmployeeData().setStatus("Employee will be active " + endDate.plusDays(1));
                }
                job.getEmployeeData().setStatus("INACTIVE");
                userRepository.save(user);
                jobRepository.save(job);
                answerToOfferResponse.setResult(true);
                answerToOfferResponse.setRecipientId(job.getEmployerRequests().getEmployer().getId());
                return answerToOfferResponse;
            } else {
                job.setStatus("IN PROCESS");
                job.setExpirationTime(null);
                if (job.getDesignatedDateTime() != null) {
                    LocalDate date = LocalDate.parse(job.getDesignatedDateTime().format(FORMAT));
                    job.getEmployeeData().setStatus("Employee will be active since " + date.plusDays(1));
                } else {
                    LocalDate endDate = LocalDate.parse(job.getEndDateTime().format(FORMAT));
                    job.getEmployeeData().setStatus("Employee will be active " + endDate.plusDays(1));
                }
                job.getEmployeeData().setStatus("INACTIVE");
                userRepository.save(user);
                jobRepository.save(job);
                answerToOfferResponse.setResult(true);
                answerToOfferResponse.setRecipientId(job.getEmployerRequests().getEmployer().getId());
                employerPassiveSearchDataRepository.delete(employerPassiveSearchDataRepository
                        .findById(decisionRequest.getPassiveSearchId()).get());
                return answerToOfferResponse;
            }
        }
        return answerToOfferResponse;
    }

    public EmployeeInfoResponse sendEmployeesOffer (EmployerPassiveSearchRequest employerPassiveSearchRequest, Principal principal) {
        EmployeeInfoResponse employeeInfoResponse = new EmployeeInfoResponse();
        User employee = userRepository.findByEmail(principal.getName()).get();
        for (Job job : employee.getEmployeeData().getJobs()) {
            if (job.getStatus().equals("NOT CONFIRMED")) {
                employeeInfoResponse.setError(checkAndGetMessageInSpecifiedLanguage(USER_IS_TEMPORARILY_BUSY, employee.getInterfaceLanguage()));
            }
        }
        List<Profession> professions = employee.getEmployeeData().getProfessions();
        String name = employee.getName();
        String email = employee.getEmail();
        double ranking = employee.getRanking();
        String phone = employee.getPhone();
        String avatar = employee.getAvatar();
        Optional<EmployerPassiveSearchData> employerPassiveSearchData = employerPassiveSearchDataRepository
                .findById(employerPassiveSearchRequest.getId());
        if (employerPassiveSearchData.isEmpty()) {
            employeeInfoResponse.setError(checkAndGetMessageInSpecifiedLanguage(OFFER_IS_NOT_EXIST, employee.getInterfaceLanguage()));
            return employeeInfoResponse;
        }
        String jobAddress = employerPassiveSearchData.get().getJobAddress();
        String additionalInfo = employerPassiveSearchData.get().getPaymentAndAdditionalInfo();
        LocalDateTime designatedDateTime = employerPassiveSearchData.get().getDesignatedDateTime();
        LocalDateTime startDateTime = employerPassiveSearchData.get().getStartDateTime();
        LocalDateTime endDateTime = employerPassiveSearchData.get().getEndDateTime();
        Job job = new Job();
        if (designatedDateTime != null) {
            job.setDesignatedDateTime(designatedDateTime);
        } else {
            job.setStartDateTime(startDateTime);
            job.setEndDateTime(endDateTime);
        }
        job.setProfessions(professions);
        job.setJobAddress(jobAddress);
        job.setPaymentAndAdditionalInfo(additionalInfo);
        job.setExpirationTime(employerPassiveSearchRequest.getExpirationTime());
        job.setEmployerRequests(employerPassiveSearchData.get().getEmployerRequests());
        job.setStatus("NOT CONFIRMED");
        job.setEmployeeData(employee.getEmployeeData());
        jobRepository.save(job);
        employeeInfoResponse.setName(name);
        employeeInfoResponse.setEmail(email);
        employeeInfoResponse.setRanking(ranking);
        employeeInfoResponse.setPhone(phone);
        employeeInfoResponse.setAvatar(avatar);
        employeeInfoResponse.setToEmployerId(employerPassiveSearchData.get().getEmployerRequests().getEmployer().getId());

        return employeeInfoResponse;
    }

    public JobResponse sendEmployerOffer(JobRequest jobRequest, Principal principal) {
        JobResponse jobResponse = new JobResponse();
        List<String> errors = new ArrayList<>();
        User employer = userRepository.findByEmail(principal.getName()).get();
        Optional<User> employee = userRepository.findById(jobRequest.getUserId());
        List<String> professionsNames = jobRequest.getProfessions();
        String jobAddress = jobRequest.getJobAddress();
        String info = jobRequest.getPaymentAndAdditionalInfo();
        Long timestamp = jobRequest.getTimestamp();
        Long lowestBorderTimestamp = jobRequest.getLowestBorderTimestamp();
        Long highestBorderTimestamp = jobRequest.getHighestBorderTimestamp();
        if (employee.isEmpty()) {
            errors.add(checkAndGetMessageInSpecifiedLanguage(USER_NOT_FOUND, employer.getInterfaceLanguage()));
        }
        for (Job job : employee.get().getEmployeeData().getJobs()) {
            if (job.getStatus().equals("NOT CONFIRMED")) {
                errors.add(checkAndGetMessageInSpecifiedLanguage(USER_IS_TEMPORARILY_BUSY, employer.getInterfaceLanguage()));
            }
        }
        List<Profession> professions = new ArrayList<>();
        if (!professionsNames.isEmpty()) {
            for (String professionName : professionsNames) {
                Optional<ProfessionName> profession = professionNameRepository.findByName(professionName);
                if (profession.isEmpty()) {
                    errors.add(checkAndGetMessageInSpecifiedLanguage(PROFESSION_NOT_FOUND, employer.getInterfaceLanguage()));
                } else {
                    professions.add(profession.get().getProfession());
                }
            }
        } else {
            errors.add(checkAndGetMessageInSpecifiedLanguage(PROFESSION_SPECIFICATION_REQUIREMENT, employer.getInterfaceLanguage()));
        }
        if (jobAddress == null || jobAddress.length() > 50) {
            errors.add(checkAndGetMessageInSpecifiedLanguage(INCORRECT_JOB_ADDRESS, employer.getInterfaceLanguage()));
        }
        if (info != null) {
            if (info.length() > 50) {
                errors.add(checkAndGetMessageInSpecifiedLanguage(TOO_MANY_ADDITIONAL_INFO, employer.getInterfaceLanguage()));
            }
        }
        if (timestamp == null && (lowestBorderTimestamp == null && highestBorderTimestamp == null)) {
            errors.add(checkAndGetMessageInSpecifiedLanguage(SPECIFICATION_DATE_REQUIREMENT, employer.getInterfaceLanguage()));
        }
        if (!errors.isEmpty()) {
            jobResponse.setErrors(errors);
            return jobResponse;
        }
        if (employer.getEmployerRequests() == null) {
            EmployerRequests employerRequests = new EmployerRequests();
            employerRequests.setEmployer(employer);
            employerRequestsRepository.save(employerRequests);
        }
        Job job = new Job();
        job.setJobAddress(jobAddress);
        job.setProfessions(professions);
        job.setPaymentAndAdditionalInfo(info);
        job.setEmployeeData(employee.get().getEmployeeData());
        job.setEmployerRequests(employer.getEmployerRequests());
        if (timestamp != null) {
            LocalDateTime designatedDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), TimeZone.getDefault().toZoneId());
            job.setDesignatedDateTime(designatedDateTime);
        } else {
            LocalDateTime startDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(lowestBorderTimestamp), TimeZone.getDefault().toZoneId());
            job.setStartDateTime(startDateTime);
            LocalDateTime endDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(highestBorderTimestamp), TimeZone.getDefault().toZoneId());
            job.setEndDateTime(endDateTime);
        }
        job.setStatus("NOT CONFIRMED");
        job.setExpirationTime(jobRequest.getExpirationTime());
        jobRepository.save(job);
        jobResponse.setResult(true);
        jobResponse.setJob(job);

        return jobResponse;
    }

    public void createChat(ChatMessage chatMessage) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setSenderId(chatMessage.getSender().getId());
        chatRoom.setRecipientId(chatMessage.getRecipient().getId());
        chatRoomRepository.save(chatRoom);
    }

    public ChatRoom getChat(long senderId, long recipientId) {

        return chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId).get();
    }

    public ChatMessageResponse saveNewMessage(ChatMessage chatMessage, ChatRoom chatRoom) {
        ChatMessageResponse chatMessageResponse = new ChatMessageResponse();
        if (chatMessage.getChat() == null) {
            chatMessage.setChat(chatRoom);
        }
        chatMessage.setStatus("RECEIVED");
        chatMessage.setTime(LocalDateTime.now());
        chatMessageRepository.save(chatMessage);
        chatMessageResponse.setResult(true);
        chatMessageResponse.setMessage(chatMessage);

        return chatMessageResponse;
    }

    public ChatMessageResponse countNewMessages(long senderId, long recipientId) {
        ChatMessageResponse chatMessageResponse = new ChatMessageResponse();
        chatMessageResponse.setMessageCount(chatMessageRepository.countNewMessages(senderId, recipientId));

        return chatMessageResponse;
    }

    public ChatMessageResponse findAllMessages(long senderId, long recipientId) {
        ChatMessageResponse chatMessageResponse = new ChatMessageResponse();
        Optional<ChatRoom> chatRoom = chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId);
        if (chatRoom.isEmpty()) {
            ChatRoom newChatRoom = new ChatRoom(senderId, recipientId);
            chatRoomRepository.save(newChatRoom);
            chatMessageResponse.setResult(true);

            return chatMessageResponse;
        }
        List<ChatMessage> chatMessages = chatMessageRepository.findAllBySenderIdAndRecipientId(senderId, recipientId);
        if (chatMessages.isEmpty()) {
            chatMessageResponse.setResult(true);
           return chatMessageResponse;
        }
        for (ChatMessage m : chatMessages) {
            m.setStatus("DELIVERED");
            chatMessageRepository.save(m);
        }
        chatMessageResponse.setMessages(chatMessages);

        return chatMessageResponse;
    }

    public ChatMessageResponse findById(long id) {
        ChatMessageResponse chatMessageResponse = new ChatMessageResponse();
        Optional<ChatMessage> chatMessage = chatMessageRepository.findById(id);
        if (chatMessage.isEmpty()) {
            chatMessageResponse.setResult(false);
            return chatMessageResponse;
        }
        ChatMessage m = chatMessage.get();
        m.setStatus("DELIVERED");
        chatMessageResponse.setMessage(m);

        return chatMessageResponse;
    }
}
