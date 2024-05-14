package com.example.navigator.service;
import com.example.navigator.api.request.ChatRequest;
import com.example.navigator.api.request.DecisionRequest;
import com.example.navigator.api.request.EmployerPassiveSearchRequest;
import com.example.navigator.api.request.VacancyRequest;
import com.example.navigator.api.response.*;
import com.example.navigator.model.*;
import com.example.navigator.model.repository.*;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    private EmployeeToEmployerRepository employeeToEmployerRepository;
    @Autowired
    private VacancyRepository vacancyRepository;
    @Autowired
    private ProfessionNameRepository professionNameRepository;

    private static final long UPLOAD_LIMIT = 5242880;
    private final String DEFAULT_LANGUAGE = "English";
    private final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy hh.mm");
    private final String USER_IS_TEMPORARILY_BUSY = "USER_IS_TEMPORARILY_BUSY";
    private final String IMAGE_SIZE = "IMAGE_SIZE";
    private final String IMAGE_FORMAT = "IMAGE_FORMAT";
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
                vacancyRepository.delete(vacancyRepository
                        .findById(decisionRequest.getPassiveSearchId()).get());
                return answerToOfferResponse;
            }
        }
        return answerToOfferResponse;
    }

    public ExtendedUserInfoResponse sendEmployeesOffer (EmployerPassiveSearchRequest employerPassiveSearchRequest) {
        ExtendedUserInfoResponse extendedUserInfoResponse = new ExtendedUserInfoResponse();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User employee = userRepository.findByEmail(username).get();
        for (Job job : employee.getEmployeeData().getJobs()) {
            if (job.getStatus().equals("NOT CONFIRMED")) {
                extendedUserInfoResponse.setError(checkAndGetMessageInSpecifiedLanguage(USER_IS_TEMPORARILY_BUSY, employee.getEndonymInterfaceLanguage()));
            }
        }
        List<Profession> professions = employee.getEmployeeData().getProfessions();
        String name = employee.getName();
        String email = employee.getEmail();
        double ranking = employee.getRanking();
        String phone = employee.getPhone();
        String avatar = employee.getAvatar();
        Optional<Vacancy> employerPassiveSearchData = vacancyRepository
                .findById(employerPassiveSearchRequest.getId());
        if (employerPassiveSearchData.isEmpty()) {
            extendedUserInfoResponse.setError(checkAndGetMessageInSpecifiedLanguage(OFFER_IS_NOT_EXIST, employee.getEndonymInterfaceLanguage()));
            return extendedUserInfoResponse;
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
        extendedUserInfoResponse.setName(name);
        extendedUserInfoResponse.setEmail(email);
        extendedUserInfoResponse.setRanking(ranking);
        extendedUserInfoResponse.setPhone(phone);
        extendedUserInfoResponse.setAvatar(avatar);
        extendedUserInfoResponse.setToEmployerId(employerPassiveSearchData.get().getEmployerRequests().getEmployer().getId());

        return extendedUserInfoResponse;
    }

    public ResultErrorsResponse checkOfferFromEmployer(VacancyRequest vacancyRequest) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        List<String> errors = new ArrayList<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User employer = userRepository.findByEmail(username).get();
        Optional<User> employee = userRepository.findById(vacancyRequest.getRecipientId());
        if (employee.isEmpty()) {
            errors.add(checkAndGetMessageInSpecifiedLanguage(USER_NOT_FOUND, employer.getEndonymInterfaceLanguage()));
            resultErrorsResponse.setErrors(errors);
            return resultErrorsResponse;
        }
        resultErrorsResponse.setResult(true);
        EmployeeToEmployer employeeToEmployer = new EmployeeToEmployer();
        employeeToEmployer.setEmployerId(employer.getId());
        employeeToEmployer.setEmployeeId(employee.get().getId());
        employeeToEmployerRepository.save(employeeToEmployer);

        return resultErrorsResponse;
    }

    public ChatMessageResponse saveNewMessage(ChatRequest chatRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        ChatMessageResponse chatMessageResponse = new ChatMessageResponse();
        chatMessageResponse.setResult(true);
        ChatMessage chatMessage = new ChatMessage();
        Optional<ChatRoom> chatRoom = chatRoomRepository.findBySenderIdAndRecipientId(chatRequest.getSenderId(), chatRequest.getRecipientId());
        if (chatRoom.isEmpty()) {
            ChatRoom newChatRoom = new ChatRoom();
            newChatRoom.setSenderId(chatRequest.getSenderId());
            newChatRoom.setRecipientId(chatRequest.getRecipientId());
            chatMessage.setChat(newChatRoom);
            chatRoomRepository.save(newChatRoom);
        } else {
            chatMessage.setChat(chatRoom.get());
        }
        chatMessage.setStatus("RECEIVED");
        chatMessage.setTime(LocalDateTime.now());
        chatMessage.setImage(chatMessage.isImage());
        chatMessage.setSender(user);
        chatMessage.setRecipient(userRepository.findById(chatRequest.getRecipientId()).get());
        if (chatMessage.isImage()) {
            byte[] decodedBytes = Base64.getDecoder().decode(chatRequest.getContent());
            try {
                chatMessageRepository.save(chatMessage);
                ChatMessage savedChatMessage = chatMessageRepository.getLastImageForPathSetting().get();
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(decodedBytes));
                BufferedImage editedImage = Scalr.resize(image, Scalr.Mode.FIT_EXACT,120,120);
                String pathToImage = "navigator/images/id" + savedChatMessage.getId() + "image.png";
                savedChatMessage.setContent(pathToImage);
                chatMessageRepository.save(savedChatMessage);
                Path path = Paths.get(pathToImage);
                ImageIO.write(editedImage, "png", path.toFile());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            chatMessage.setContent(chatRequest.getContent());
            chatMessageRepository.save(chatMessage);
        }
        chatMessageResponse.setMessage(chatMessage);

        return chatMessageResponse;
    }

    public ChatMessageResponse countNewMessages(long senderId, long recipientId) {
        ChatMessageResponse chatMessageResponse = new ChatMessageResponse();
        chatMessageResponse.setMessageCount(chatMessageRepository.countNewMessages(senderId, recipientId));

        return chatMessageResponse;
    }

    public ChatMessageResponse findAllMessages(ChatRequest chatRequest) {
        ChatMessageResponse chatMessageResponse = new ChatMessageResponse();
        long senderId = chatRequest.getSenderId();
        long recipientId = chatRequest.getRecipientId();
        Optional<ChatRoom> chatRoom = chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId);
        if (chatRoom.isEmpty()) {
            ChatRoom newChatRoom = new ChatRoom(senderId, recipientId);
            chatRoomRepository.save(newChatRoom);
            chatMessageResponse.setResult(true);

            return chatMessageResponse;
        }
        TreeSet<ChatMessage> chatMessages = chatMessageRepository.findAllBySenderIdAndRecipientId(senderId, recipientId);
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

    public AvatarResponse writeImage(MultipartFile image) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        AvatarResponse avatarResponse = new AvatarResponse();
        List<String> errorsList = new ArrayList<>();
        if (image.isEmpty()) {
            return avatarResponse;
        }
        if (image.getSize() > UPLOAD_LIMIT) {
            errorsList.add(checkAndGetMessageInSpecifiedLanguage(IMAGE_SIZE, user.getEndonymInterfaceLanguage()));
        }
        if (!image.getOriginalFilename().endsWith("jpg") && !image.getOriginalFilename().endsWith("png")) {
            errorsList.add(checkAndGetMessageInSpecifiedLanguage(IMAGE_FORMAT, user.getEndonymInterfaceLanguage()));
        }
        if (!errorsList.isEmpty()) {
            avatarResponse.setErrors(errorsList);
            return avatarResponse;
        }
        String extension = FilenameUtils.getExtension(image.getOriginalFilename());
        try {
            BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
            BufferedImage editedImage = Scalr.resize(bufferedImage, Scalr.Mode.FIT_EXACT,36,36);
            String pathToImage = "navigator/images/id" + user.getId() + "image." + extension;
            Path path = Paths.get(pathToImage);
            ImageIO.write(editedImage, extension, path.toFile());

            avatarResponse.setPathToFile(pathToImage);
            avatarResponse.setResult(true);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return avatarResponse;
    }

    public ResultErrorsResponse openChat(ChatRequest chatRequest) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        User sender = userRepository.findById(chatRequest.getSenderId()).get();
        User recipient = userRepository.findById(chatRequest.getRecipientId()).get();
        Optional<EmployeeToEmployer> employeeToEmployer;
        EmployeeToEmployer firstContact = new EmployeeToEmployer();
        if (sender.getEmployeeData() != null && sender.getEmployerRequests() == null &&
                recipient.getEmployeeData() == null && recipient.getEmployerRequests() != null) {
            employeeToEmployer = employeeToEmployerRepository.findByEmployeeAndEmployerId(sender.getId(), recipient.getId());
            resultErrorsResponse.setResult(true);
            if (employeeToEmployer.isEmpty()) {
                resultErrorsResponse.setResult(false);
                firstContact.setEmployeeId(sender.getId());
                firstContact.setEmployerId(recipient.getId());
                employeeToEmployerRepository.save(firstContact);
            }
        } else if (sender.getEmployeeData() == null && sender.getEmployerRequests() != null &&
                recipient.getEmployeeData() != null && recipient.getEmployerRequests() == null) {
            employeeToEmployer = employeeToEmployerRepository.findByEmployeeAndEmployerId(recipient.getId(), sender.getId());
            resultErrorsResponse.setResult(true);
            if (employeeToEmployer.isEmpty()) {
                resultErrorsResponse.setResult(false);
                firstContact.setEmployeeId(recipient.getId());
                firstContact.setEmployerId(sender.getId());
                employeeToEmployerRepository.save(firstContact);
            }
        } else {
            List<String> errors = new ArrayList<>();
            errors.add(checkAndGetMessageInSpecifiedLanguage(USER_NOT_FOUND, sender.getEndonymInterfaceLanguage()));
            resultErrorsResponse.setErrors(errors);
        }

        return resultErrorsResponse;
    }
}
