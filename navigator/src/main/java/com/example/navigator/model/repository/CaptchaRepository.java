package com.example.navigator.model.repository;

import com.example.navigator.model.Captcha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaptchaRepository extends JpaRepository<Captcha, Long> {

    @Query(value = "SELECT c FROM com.example.navigator.model.Captcha AS c WHERE c.secretCode = :secret")
    Optional<Captcha> findBySecretCodeEquals(String secret);
}
