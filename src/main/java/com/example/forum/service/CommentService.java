package com.example.forum.service;

import com.example.forum.controller.form.CommentForm;
import com.example.forum.repository.CommentRepository;
import com.example.forum.repository.ReportRepository;
import com.example.forum.repository.entity.Comment;
import com.example.forum.repository.entity.Report;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReportRepository reportRepository;

    // レコード全件取得（更新日時降順）
    public List<CommentForm> findAllComment() {
        List<Comment> results = commentRepository.findAllByOrderByUpdatedDateDesc();
        return setCommentForm(results);
    }

    // Entity → Form 変換
    private List<CommentForm> setCommentForm(List<Comment> results) {
        List<CommentForm> comments = new ArrayList<>();
        for (Comment result : results) {
            CommentForm comment = new CommentForm();
            comment.setId(result.getId());
            comment.setCommentId(result.getCommentId());
            comment.setUserId(result.getUserId());
            comment.setComment(result.getComment());
            comment.setCreatedDate(result.getCreatedDate());
            comment.setUpdatedDate(result.getUpdatedDate());
            comments.add(comment);
        }
        return comments;
    }

    // コメント追加・編集
    public void saveComment(CommentForm reqComment) {
        Comment comment;
        if (reqComment.getId() != null) {
            // 更新
            comment = commentRepository.findById(reqComment.getId()).orElse(new Comment());
        } else {
            // 新規
            comment = new Comment();
            comment.setCreatedDate(LocalDateTime.now());
        }

        comment.setCommentId(reqComment.getCommentId());
        comment.setUserId(reqComment.getUserId());
        comment.setComment(reqComment.getComment());
        comment.setUpdatedDate(LocalDateTime.now());

        if (comment.getCreatedDate() == null) {
            comment.setCreatedDate(LocalDateTime.now());
        }

        commentRepository.save(comment);
    }

    // 特定投稿のコメント一覧取得
    public List<CommentForm> findCommentsByCommentId(int commentId) {
        List<Comment> results = commentRepository.findByCommentIdOrderByUpdatedDateAsc(commentId);
        return setCommentForm(results);
    }

    // コメント削除
    @Transactional
    public void deleteCommentById(Integer id) {
        commentRepository.deleteCommentById(id);
    }

    // コメント編集画面用取得
    public CommentForm findCommentById(Integer id) {
        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment == null) return null;

        CommentForm form = new CommentForm();
        form.setId(comment.getId());
        form.setCommentId(comment.getCommentId());
        form.setUserId(comment.getUserId());
        form.setComment(comment.getComment());
        form.setCreatedDate(comment.getCreatedDate());
        form.setUpdatedDate(comment.getUpdatedDate());
        return form;
    }

    // 作成日時で検索
    public List<Comment> findByCreatedDateBetweenExcludingZero(LocalDateTime start, LocalDateTime end) {
        return commentRepository.findByCreatedDateBetweenAndCommentIdNot(start, end, 0);
    }

    // コメント保存 + レポート更新
    public void saveComment(Comment comment, Report report) {
        commentRepository.save(comment);
        report.setUpdatedTime(LocalDateTime.now());
        reportRepository.save(report);
    }
}