package com.alness.gamesheroes.common;

import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseServer {
    private String message;
    private HttpStatus code;
    private Boolean status;
    private Map<String, Object> data;

    public ResponseServer(String message, HttpStatus code, Boolean status) {
        this.message = message;
        this.code = code;
        this.status = status;
    }
}


    

