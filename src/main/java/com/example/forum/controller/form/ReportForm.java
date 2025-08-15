package com.example.forum.controller.form;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ReportForm {

    private int id;
    @NotBlank(message = "投稿内容を入力してください")
    private String content;
    @AssertTrue(message = "投稿内容を入力してください")
    public boolean isContentValid() {
        if (content == null) return false;
        // 半角・全角スペースを削除して判定
        return !content.replaceAll("\\s+", "").isEmpty();
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updatedDate;

    private List<CommentForm> comments;
}

