package com.jcedenon.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

// Security class 8
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorLogin {

    private String message;

    private Date timestamp;
}
