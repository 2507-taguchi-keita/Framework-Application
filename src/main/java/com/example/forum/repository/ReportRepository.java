package com.example.forum.repository;

import com.example.forum.repository.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
//ReportRepository が JpaRepository を継承しており、findAllメソッドを実行しているため、何か記載する必要はない
public interface ReportRepository extends JpaRepository<Report, Integer> {
    public List<Report> findAllByOrderByIdDesc();
    List<Report> findByCreatedDateBetween(Date start, Date end);
}

