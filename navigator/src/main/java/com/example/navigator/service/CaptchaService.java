package com.example.navigator.service;
import com.example.navigator.api.response.CaptchaResponse;
import com.example.navigator.model.Captcha;
import com.example.navigator.model.repository.CaptchaRepository;
import com.github.cage.Cage;
import com.github.cage.YCage;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Random;

@Service
public class CaptchaService {

    @Autowired
    private CaptchaRepository captchaRepository;

    final private String TITLE = "data:image/png;base64, ";
    final private String FORMAT = "yyyy-MM-dd HH:mm";

    public CaptchaResponse generateAndGetCaptcha() throws Exception {
        CaptchaResponse captchaResponse = new CaptchaResponse();
        char[] availableChars = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder randomSecretCode = new StringBuilder();
        StringBuilder randomCode = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            randomCode.append(availableChars[random.nextInt(availableChars
                    .length)]);
            randomSecretCode.append(availableChars[random.nextInt(availableChars
                    .length)]);
        }
        captchaResponse.setSecret(randomSecretCode.toString());
        Cage cage = new YCage();
        BufferedImage bf = cage.drawImage(randomCode.toString());
        BufferedImage scaledImage = Scalr.resize(bf, Scalr.Mode.FIT_EXACT,100, 35, Scalr.OP_GRAYSCALE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(scaledImage, "png", baos);
        byte[] fileContent = baos.toByteArray();
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        captchaResponse.setImage(TITLE + encodedString);
        Captcha captchaCode = new Captcha();
        captchaCode.setGenerationTime(LocalDateTime.now());
        captchaCode.setCode(randomCode.toString());
        captchaCode.setSecretCode(randomSecretCode.toString());
        captchaRepository.save(captchaCode);

        return captchaResponse;
    }

    public void deleteOldCaptchasFromRepository() {
        List<Captcha> captchas = captchaRepository.findAll();
        for(Captcha captcha : captchas) {
            if (captcha.getGenerationTime().isAfter(LocalDateTime.now().plusHours(1))) {
                captchaRepository.delete(captcha);
            }
        }
    }
}