package com.example.forum.service;

import com.example.forum.controller.form.CommentForm;
import com.example.forum.controller.form.ReportForm;
import com.example.forum.repository.CommentRepository;
import com.example.forum.repository.entity.Comment;
import com.example.forum.repository.entity.Report;
import jakarta.transaction.Transactional;
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
        Comment comment;
        if (reqComment.getId() != null) {
            // 更新の場合はDBから元のエンティティを取得する
            comment = commentRepository.findById(reqComment.getId()).orElse(new Comment());
            // 作成日時はDBの値をそのまま使う
        } else {
            // 新規の場合は新規エンティティ作成
            comment = new Comment();
            comment.setCreatedDate(new Date()); // 新規作成日時をセット
        }

        comment.setContentId(reqComment.getContentId());
        comment.setUserId(reqComment.getUserId());
        comment.setContent(reqComment.getContent());
        comment.setUpdatedDate(new Date()); // 更新日時は今

        // もし更新でcreatedDateがまだnullならセット
        if (comment.getCreatedDate() == null) {
            comment.setCreatedDate(new Date());
        }

        commentRepository.save(comment);
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

    public List<CommentForm> findCommentsByContentId(int contentId) {
        // CommentRepositoryにcontentIdで取得するメソッドが必要（下で補足）
        List<Comment> results = commentRepository.findByContentIdOrderByIdAsc(contentId);
        return setCommentForm(results);
    }

    @Transactional
    public void deleteCommentById(Integer id) {
        commentRepository.deleteCommentById(id);
    }

    public CommentForm findCommentById(Integer id) {
        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment == null) {
            return null; // エラーハンドリングは必要に応じて
        }
        CommentForm form = new CommentForm();
        form.setId(comment.getId());
        form.setContentId(comment.getContentId());
        form.setUserId(comment.getUserId());
        form.setContent(comment.getContent());
        form.setCreatedDate(comment.getCreatedDate());
        form.setUpdatedDate(comment.getUpdatedDate());
        return form;
    }

    public List<Comment> findByCreatedDateBetween(Date start, Date end) {
        return commentRepository.findByCreatedDateBetween(start, end);
    }
}