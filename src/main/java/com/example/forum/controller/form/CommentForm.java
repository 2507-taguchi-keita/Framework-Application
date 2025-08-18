package com.example.forum.controller.form;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class CommentForm {

    private Integer id;
    //投稿のID
    private Integer postId;
    private Integer userId;
    @NotBlank(message = "コメントを入力してください")
    private String comment;
    @AssertTrue(message = "コメントを入力してください")
    public boolean isContentValid() {
        if (comment == null) return false;
        // 半角・全角スペースを削除して判定
        return !comment.replaceAll("\\s+", "").isEmpty();
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime updatedDate;
}

