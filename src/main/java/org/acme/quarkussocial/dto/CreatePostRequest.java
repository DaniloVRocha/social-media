package org.acme.quarkussocial.dto;

import org.acme.quarkussocial.domain.model.domain.User;

import javax.persistence.*;
import java.time.LocalDateTime;


public class CreatePostRequest {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
