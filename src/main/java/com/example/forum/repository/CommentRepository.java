package com.example.forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.forum.repository.entity.Comment;

import java.util.Date;
import java.util.List;

@Repository
//ReportRepository が JpaRepository を継承しており、findAllメソッドを実行しているため、何か記載する必要はない
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    public List<Comment> findAllByOrderByIdDesc();
    List<Comment> findByContentIdOrderByIdAsc(int contentId);
    void deleteCommentById(Integer id);
    List<Comment> findByCreatedDateBetween(Date start, Date end);
}