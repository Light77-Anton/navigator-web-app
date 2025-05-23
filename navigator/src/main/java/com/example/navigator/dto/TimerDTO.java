package com.example.navigator.dto;
import lombok.Data;

@Data
public class TimerDTO {

    private Long id;
    private String name;
    private String address;
    private String profession;
    private long millisInFuture;
    private Long contactedPersonId;
}
