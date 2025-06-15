package com.example.navigator.service;
import com.example.navigator.api.request.ChatRequest;
import com.example.navigator.api.request.DecisionRequest;
import com.example.navigator.api.request.EmployerPassiveSearchRequest;
import com.example.navigator.api.request.VacancyRequest;
import com.example.navigator.api.response.*;
import com.example.navigator.dto.TimerDTO;
import com.example.navigator.model.*;
import com.example.navigator.model.repository.*;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private CommentRepository commentRepository;
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
    private final String NO_MORE_QUOTAS = "NO_MORE_QUOTAS";
    private final String USER_DECLINED_OFFER = "USER_DECLINED_OFFER";
    private final String USER_ACCEPTED_OFFER = "USER_ACCEPTED_OFFER";

    public CommentsListResponse getCommentById(long id) {
        CommentsListResponse commentsListResponse = new CommentsListResponse();
        commentsListResponse.setList(List.of(commentRepository.findById(id).get()));

        return commentsListResponse;
    }

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

    public AnswerToOfferResponse answerToOffer(DecisionRequest decisionRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        AnswerToOfferResponse answerToOfferResponse = new AnswerToOfferResponse();
        answerToOfferResponse.setRecipientId(Long.parseLong(decisionRequest.getRecipientId()));
        String decision = decisionRequest.getDecision();
        Vacancy vacancy = vacancyRepository.findById(Long.parseLong(decisionRequest.getVacancyId())).get();
        List<EmployeeData> hiredEmployees = vacancy.getHiredEmployees();;
        if (decision.equals("ACCEPT")) {
            if (user.getRole().equals(Role.EMPLOYEE)) {
                hiredEmployees.add(user.getEmployeeData());
                if (hiredEmployees.size() == vacancy.getQuotasNumber()) {
                    answerToOfferResponse.setResult(true);
                    answerToOfferResponse.setDecision((byte) 1);
                    vacancyRepository.deleteById(vacancy.getId());
                } else if (hiredEmployees.size() > vacancy.getQuotasNumber()) {
                    answerToOfferResponse.setResult(false);
                    answerToOfferResponse.setError(checkAndGetMessageInSpecifiedLanguage(NO_MORE_QUOTAS,
                            user.getEndonymInterfaceLanguage()));
                } else {
                    answerToOfferResponse.setResult(true);
                    answerToOfferResponse.setDecision((byte) 1);
                }
            } else {
                Optional<User> employee = userRepository.findById(Long.parseLong(decisionRequest.getRecipientId()));
                if (employee.isPresent()) {
                    hiredEmployees.add(employee.get().getEmployeeData());
                    if (hiredEmployees.size() == vacancy.getQuotasNumber()) {
                        answerToOfferResponse.setResult(true);
                        answerToOfferResponse.setDecision((byte) 1);
                        vacancyRepository.deleteById(vacancy.getId());
                    } else if (hiredEmployees.size() > vacancy.getQuotasNumber()) {
                        answerToOfferResponse.setResult(false);
                        answerToOfferResponse.setError(checkAndGetMessageInSpecifiedLanguage(NO_MORE_QUOTAS,
                                user.getEndonymInterfaceLanguage()));
                    } else {
                        answerToOfferResponse.setResult(true);
                        answerToOfferResponse.setDecision((byte) 1);
                    }
                } else {
                    answerToOfferResponse.setResult(false);
                    answerToOfferResponse.setError(checkAndGetMessageInSpecifiedLanguage(USER_NOT_FOUND,
                            user.getEndonymInterfaceLanguage()));
                }
            }
        } else {
            answerToOfferResponse.setResult(true);
            answerToOfferResponse.setDecision((byte) 0);
        }

        return answerToOfferResponse;
    }

    public ExtendedUserInfoResponse sendEmployeesOffer(EmployerPassiveSearchRequest employerPassiveSearchRequest) {
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

    public ResultErrorsResponse checkOfferFromEmployer(long employeeId) {
        ResultErrorsResponse resultErrorsResponse = new ResultErrorsResponse();
        List<String> errors = new ArrayList<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User employer = userRepository.findByEmail(username).get();
        Optional<User> employee = userRepository.findById(employeeId);
        if (employee.isEmpty()) {
            errors.add(checkAndGetMessageInSpecifiedLanguage(USER_NOT_FOUND, employer.getEndonymInterfaceLanguage()));
            resultErrorsResponse.setErrors(errors);
            return resultErrorsResponse;
        }
        resultErrorsResponse.setResult(true);

        return resultErrorsResponse;
    }

    public ChatMessageResponse saveNewMessage(AnswerToOfferResponse answerToOfferResponse,
                                              ChatRequest chatRequest, ChatMessage chatMessage,
                                              ExtendedUserInfoResponse extendedUserInfoResponse,
                                              VacancyRequest vacancyRequest, Long employeeId, Long employerId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).get();
        ChatMessageResponse response = new ChatMessageResponse();
        response.setResult(false);
        if (answerToOfferResponse != null) { // входящий ответ на предложение
            Optional<User> recipient = userRepository.findById(answerToOfferResponse.getRecipientId());
            if (recipient.isPresent()) {
                ChatMessage answerToOffer = new ChatMessage();
                Optional<ChatRoom> chatRoom;
                if (chatRoomRepository.findBySenderIdAndRecipientId(user.getId(), recipient.get().getId()).isPresent()) {
                    chatRoom = chatRoomRepository.findBySenderIdAndRecipientId(user.getId(), recipient.get().getId());
                } else if (chatRoomRepository.findBySenderIdAndRecipientId(recipient.get().getId(), user.getId()).isPresent()) {
                    chatRoom = chatRoomRepository.findBySenderIdAndRecipientId(recipient.get().getId(), user.getId());
                } else {
                    response.setResult(false);

                    return response;
                }
                answerToOffer.setChat(chatRoom.get());
                answerToOffer.setTime(LocalDateTime.now());
                answerToOffer.setSender(user);
                answerToOffer.setRecipient(recipient.get());
                answerToOffer.setStatus("RECEIVED");
                if (answerToOfferResponse.getDecision() == 1) {
                    answerToOffer.setMessageType("OFFER ACCEPTANCE");
                    answerToOffer.setContent(checkAndGetMessageInSpecifiedLanguage(USER_ACCEPTED_OFFER,
                            user.getEndonymInterfaceLanguage()));
                } else {
                    answerToOffer.setMessageType("OFFER REFUSING");
                    answerToOffer.setContent(checkAndGetMessageInSpecifiedLanguage(USER_DECLINED_OFFER,
                            user.getEndonymInterfaceLanguage()));
                }
                chatMessageRepository.save(answerToOffer);
                response.setResult(true);
            }
        }
        if (chatMessage != null) { // если это входящее сообщение
            chatMessage.setStatus("RECEIVED");
            chatMessageRepository.save(chatMessage);
            response.setResult(true);
        }
        if (chatRequest != null) { // если это исходящее сообщение
            Optional<User> recipient = userRepository.findById(chatRequest.getRecipientId());
            if (recipient.isPresent()) {
                ChatMessage newMessage = new ChatMessage();
                Optional<ChatRoom> chatRoom = chatRoomRepository.findBySenderIdAndRecipientId(chatRequest.getSenderId(), chatRequest.getRecipientId());
                if (chatRoom.isEmpty()) {
                    ChatRoom newChatRoom = new ChatRoom();
                    newChatRoom.setSenderId(chatRequest.getSenderId());
                    newChatRoom.setRecipientId(chatRequest.getRecipientId());
                    newMessage.setChat(newChatRoom);
                    chatRoomRepository.save(newChatRoom);
                } else {
                    newMessage.setChat(chatRoom.get());
                }
                newMessage.setStatus("SENT");
                newMessage.setTime(LocalDateTime.now());
                newMessage.setSender(user);
                newMessage.setRecipient(recipient.get());
                if (chatRequest.isImage()) {
                    byte[] decodedBytes = Base64.getDecoder().decode(chatRequest.getContent());
                    try {
                        newMessage.setMessageType("IMAGE");
                        newMessage.setContent("image_path_placeholder");
                        chatMessageRepository.save(newMessage);
                        ChatMessage imageChatMessage = chatMessageRepository.findById().get();
                        BufferedImage image = ImageIO.read(new ByteArrayInputStream(decodedBytes));
                        BufferedImage editedImage = Scalr.resize(image, Scalr.Mode.FIT_EXACT,120,120);
                        String pathToImage = "navigator/images/id" + imageChatMessage.getId() + "image.png";
                        imageChatMessage.setContent(pathToImage);
                        chatMessageRepository.save(imageChatMessage);
                        response.setMessage(imageChatMessage);
                        Path path = Paths.get(pathToImage);
                        ImageIO.write(editedImage, "png", path.toFile());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    newMessage.setContent(chatRequest.getContent());
                    chatMessageRepository.save(newMessage);
                    response.setMessage(newMessage);
                }
                response.setResult(true);
            }
        }
        if (vacancyRequest != null && employeeId != null) { // предложение от работодателя рабочему
            Optional<User> employee = userRepository.findById(employeeId);
            if (employee.isEmpty()) {
                response.setResult(false);
            } else {
                Optional<ChatRoom> chatRoom = chatRoomRepository.findBySenderIdAndRecipientId(user.getId(), employeeId);
                ChatMessage jobOffer = new ChatMessage();
                if (chatRoom.isEmpty()) {
                    ChatRoom newChatRoom = new ChatRoom();
                    newChatRoom.setSenderId(user.getId());
                    newChatRoom.setRecipientId(employeeId);
                    chatRoomRepository.save(newChatRoom);
                    jobOffer.setChat(newChatRoom);
                } else {
                    jobOffer.setChat(chatRoom.get());
                }
                jobOffer.setSender(user);
                jobOffer.setRecipient(employee.get());
                jobOffer.setTime(LocalDateTime.now());
                jobOffer.setStatus("SENT");
                jobOffer.setMessageType("OFFER");
                StringBuilder sb = new StringBuilder();
                sb.append(vacancyRequest.getProfessionName());
                sb.append(" - ");
                sb.append(vacancyRequest.getJobAddress());
                sb.append(" - ");
                sb.append(vacancyRequest.getStartTimestamp());
                sb.append(" - ");
                sb.append(vacancyRequest.getPaymentAndAdditionalInfo());
                jobOffer.setContent(sb.toString());
                Optional<Vacancy> vacancy = vacancyRepository.findById(vacancyRequest.getVacancyId());
                if (vacancy.isPresent()) {
                    jobOffer.setVacancy(vacancy.get());
                } else {
                    Vacancy privateVacancy = new Vacancy();
                    privateVacancy.setEmployerRequests(user.getEmployerRequests());
                    privateVacancy.setStartDateTime(LocalDateTime.now());
                    privateVacancy.setPaymentAndAdditionalInfo(vacancyRequest.getPaymentAndAdditionalInfo());
                    privateVacancy.setType("PRIVATE");
                    privateVacancy.setProfession(professionNameRepository.findByName(vacancyRequest.getProfessionName())
                            .get().getProfession());
                    privateVacancy.setWaitingDateTime(vacancyRequest.getWaitingTimestamp());
                    privateVacancy.setQuotasNumber(1);
                    jobOffer.setVacancy(privateVacancy);
                    privateVacancy.setReferencedChatMessage(jobOffer);
                    vacancyRepository.save(privateVacancy);
                }
                chatMessageRepository.save(jobOffer);
                response.setResult(true);
            }
        }
        if (extendedUserInfoResponse != null) { // если работодателю приходит предложение от рабочего
            Optional<ChatRoom> chatRoom = chatRoomRepository.findBySenderIdAndRecipientId(extendedUserInfoResponse.getId(), user.getId());
            if (chatRoom.isPresent()) {
                ChatMessage jobRequest = new ChatMessage();
                jobRequest.setChat(chatRoom.get());
                jobRequest.setSender(userRepository.findById(extendedUserInfoResponse.getId()).get());
                jobRequest.setRecipient(user);
                jobRequest.setTime(LocalDateTime.now());
                jobRequest.setStatus("RECEIVED");
                jobRequest.setMessageType("OFFER");
                Vacancy vacancy = vacancyRepository.findById(extendedUserInfoResponse.getVacancyId()).get();
                StringBuilder sb = new StringBuilder();
                String professionField = null;
                for (ProfessionName professionName : vacancy.getProfession().getProfessionNames()) {
                    if (professionName.getLanguage().getLanguageEndonym().equals(DEFAULT_LANGUAGE)) {
                        professionField = professionName.getProfessionName();
                    }
                    if (professionName.getLanguage().getLanguageEndonym().equals(user.getEndonymInterfaceLanguage())) {
                        professionField = professionName.getProfessionName();
                        break;
                    }
                }
                sb.append(professionField);
                sb.append(" - ");
                sb.append(vacancy.getJobLocation().getJobAddress());
                sb.append(" - ");
                sb.append(vacancy.getStartDateTime());
                sb.append(" - ");
                sb.append(vacancy.getPaymentAndAdditionalInfo());
                jobRequest.setContent(sb.toString());
                jobRequest.setVacancy(vacancy);
                chatMessageRepository.save(jobRequest);
                response.setResult(true);
            }
        }
        if (vacancyRequest != null) { // если рабочему приходит предложение от работодателя
            Optional<ChatRoom> chatRoom = chatRoomRepository.findBySenderIdAndRecipientId(vacancyRequest.getEmployerId(), user.getId());
            if (chatRoom.isPresent()) {
                ChatMessage jobRequest = new ChatMessage();
                jobRequest.setChat(chatRoom.get());
                jobRequest.setSender(userRepository.findById(vacancyRequest.getEmployerId()).get());
                jobRequest.setRecipient(user);
                jobRequest.setTime(LocalDateTime.now());
                jobRequest.setStatus("RECEIVED");
                jobRequest.setMessageType("OFFER");
                Vacancy vacancy = vacancyRepository.findById(vacancyRequest.getVacancyId()).get();
                StringBuilder sb = new StringBuilder();
                String professionField = null;
                for (ProfessionName professionName : vacancy.getProfession().getProfessionNames()) {
                    if (professionName.getLanguage().getLanguageEndonym().equals(DEFAULT_LANGUAGE)) {
                        professionField = professionName.getProfessionName();
                    }
                    if (professionName.getLanguage().getLanguageEndonym().equals(user.getEndonymInterfaceLanguage())) {
                        professionField = professionName.getProfessionName();
                        break;
                    }
                }
                sb.append(professionField);
                sb.append(" - ");
                sb.append(vacancy.getJobLocation().getJobAddress());
                sb.append(" - ");
                sb.append(vacancy.getStartDateTime());
                sb.append(" - ");
                sb.append(vacancy.getPaymentAndAdditionalInfo());
                jobRequest.setContent(sb.toString());
                jobRequest.setVacancy(vacancy);
                chatMessageRepository.save(jobRequest);
                response.setResult(true);
            }
        }
        if (vacancyRequest != null && employerId != null) { // предложение от рабочего
            Optional<User> employer = userRepository.findById(employerId);
            if (employer.isEmpty()) {
                response.setResult(false);
            } else {
                Optional<ChatRoom> chatRoom = chatRoomRepository.findBySenderIdAndRecipientId(user.getId(), employerId);
                ChatMessage jobOffer = new ChatMessage();
                if (chatRoom.isEmpty()) {
                    ChatRoom newChatRoom = new ChatRoom();
                    newChatRoom.setSenderId(user.getId());
                    newChatRoom.setRecipientId(employeeId);
                    chatRoomRepository.save(newChatRoom);
                    jobOffer.setChat(newChatRoom);
                } else {
                    jobOffer.setChat(chatRoom.get());
                }
                jobOffer.setSender(user);
                jobOffer.setRecipient(employer.get());
                jobOffer.setTime(LocalDateTime.now());
                jobOffer.setStatus("SENT");
                jobOffer.setMessageType("OFFER");
                StringBuilder sb = new StringBuilder();
                sb.append(vacancyRequest.getProfessionName());
                sb.append(" - ");
                sb.append(vacancyRequest.getJobAddress());
                sb.append(" - ");
                sb.append(vacancyRequest.getStartTimestamp());
                sb.append(" - ");
                sb.append(vacancyRequest.getPaymentAndAdditionalInfo());
                jobOffer.setContent(sb.toString());
                Optional<Vacancy> vacancy = vacancyRepository.findById(vacancyRequest.getVacancyId());
                if (vacancy.isPresent()) {
                    jobOffer.setVacancy(vacancy.get());
                }
                chatMessageRepository.save(jobOffer);
                response.setResult(true);
            }
        }

        return response;
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
