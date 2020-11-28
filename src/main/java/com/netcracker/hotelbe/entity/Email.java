package com.netcracker.hotelbe.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Email {
    private Long id;
    private String subject;
    private String text;
}
