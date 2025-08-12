package com.example.forum.service;

import com.example.forum.controller.form.CommentForm;
import com.example.forum.controller.form.ReportForm;
import com.example.forum.repository.CommentRepository;
import com.example.forum.repository.entity.Comment;
import com.example.forum.repository.entity.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    CommentRepository commentRepository;

    /*
     * レコード全件取得処理
     */
    public List<CommentForm> findAllComment() {
        List<Comment> results = commentRepository.findAllByOrderByIdDesc();
        List<CommentForm> comments = setCommentForm(results);
        return comments;
    }

    /*
     * DBから取得したデータをFormに設定
     */
    private List<CommentForm> setCommentForm(List<Comment> results) {
        List<CommentForm> comments = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            CommentForm comment = new CommentForm();
            Comment result = results.get(i);
            comment.setId(result.getId());
            comment.setContentId(result.getContentId());
            comment.setUserId(result.getUserId());
            comment.setContent(result.getContent());
            comment.setCreatedDate(result.getCreatedDate());
            comment.setUpdatedDate(result.getUpdatedDate());
            comments.add(comment);
        }
        return comments;
    }

    /*
     * レコード追加・編集機能
     */
    public void saveComment(CommentForm reqComment) {
        Comment saveComment = setCommentEntity(reqComment);
        //saveメソッドは新規登録（insert）、更新（update）の両方が使える→更新のメソッドは不要
        commentRepository.save(saveComment);
    }

    /*
     * リクエストから取得した情報をEntityに設定
     */
    private Comment setCommentEntity(CommentForm reqComment) {
        Comment comment = new Comment();
        comment.setId(reqComment.getId());
        comment.setContentId(reqComment.getContentId());
        comment.setUserId(reqComment.getUserId());
        comment.setContent(reqComment.getContent());
        comment.setCreatedDate(reqComment.getCreatedDate());
        comment.setUpdatedDate(reqComment.getUpdatedDate());
        return comment;
    }
}