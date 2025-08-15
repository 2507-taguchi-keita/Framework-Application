package com.example.forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.forum.repository.entity.Comment;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
//ReportRepository が JpaRepository を継承しており、findAllメソッドを実行しているため、何か記載する必要はない
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    public List<Comment> findAllByOrderByUpdatedDateDesc();
    void deleteCommentById(Integer id);
    List<Comment> findByCommentIdOrderByUpdatedDateDesc(int commentId);
    List<Comment> findByCreatedDateBetweenAndCommentIdNot(LocalDateTime start, LocalDateTime end, int commentId);

}