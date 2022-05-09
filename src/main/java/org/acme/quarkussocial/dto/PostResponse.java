package org.acme.quarkussocial.dto;

import org.acme.quarkussocial.domain.model.domain.Post;

import java.time.LocalDateTime;

public class PostResponse {

    private String text;

    private LocalDateTime dateTime;

    public static PostResponse fromEntity(Post post){
        PostResponse response = new PostResponse();
        response.setText(post.getText());
        response.setDateTime(post.getDateTime());
        return response;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
