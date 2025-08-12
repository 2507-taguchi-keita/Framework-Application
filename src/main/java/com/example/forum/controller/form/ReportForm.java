package com.example.forum.controller.form;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReportForm {

    private int id;
    private String content;

    private List<CommentForm> comments;
}

