package com.example.forum.service;

import com.example.forum.controller.form.CommentForm;
import com.example.forum.controller.form.ReportForm;
import com.example.forum.repository.ReportRepository;
import com.example.forum.repository.entity.Comment;
import com.example.forum.repository.entity.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import com.example.forum.repository.CommentRepository;

@Service
public class ReportService {
    @Autowired
    ReportRepository reportRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    CommentService commentService;
    /*
     * レコード全件取得処理
     */
    public List<ReportForm> findAllReport() {
        List<Report> results = reportRepository.findAllByOrderByIdDesc();
        List<ReportForm> reports = setReportForm(results);
        for (ReportForm reportForm : reports) {
            List<CommentForm> commentList = commentService.findCommentsByContentId(reportForm.getId());
            reportForm.setComments(commentList);
        }
        return reports;
    }

    /*
     * DBから取得したデータをFormに設定
     */
    //setReportFormメソッドでEntity→Formに詰め直して、Controllerに戻しています
    private List<ReportForm> setReportForm(List<Report> results) {
        List<ReportForm> reports = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            ReportForm report = new ReportForm();
            Report result = results.get(i);
            report.setId(result.getId());
            report.setContent(result.getContent());
            reports.add(report);
        }
        return reports;
    }

    /*
     * レコード追加・編集機能
     */
    public void saveReport(ReportForm reqReport) {
        Report saveReport = setReportEntity(reqReport);
        //saveメソッドは新規登録（insert）、更新（update）の両方が使える→更新のメソッドは不要
        reportRepository.save(saveReport);
    }

    /*
     * リクエストから取得した情報をEntityに設定
     */
    private Report setReportEntity(ReportForm reqReport) {
        Report report = new Report();
        report.setId(reqReport.getId());
        report.setContent(reqReport.getContent());
        return report;
    }

    //削除機能
    public void deleteReport(Integer id) {
        //deleteById=引数に該当するレコードを削除
        reportRepository.deleteById(id);
    }

    /*
     * レコードを1件取得
     */
    public ReportForm editReport(Integer id) {
        //Report オブジェクトをたくさん入れるための空のリストを作っている
        List<Report> results = new ArrayList<>();
        //findById = Id が一致するレコードを取得するような処理.
        // Id が合致しないときは null を返したいので、orElse(null)を使う
        //reportRepository~~の結果をReport型に変換して追加する処理
        results.add((Report) reportRepository.findById(id).orElse(null));
        //results（Report型のリスト）を setReportForm() メソッドに渡している
        List<ReportForm> reports = setReportForm(results);
        return reports.get(0);
    }
}
