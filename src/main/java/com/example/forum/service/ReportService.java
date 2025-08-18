package com.example.forum.service;

import com.example.forum.controller.form.CommentForm;
import com.example.forum.controller.form.ReportForm;
import com.example.forum.repository.ReportRepository;
import com.example.forum.repository.entity.Comment;
import com.example.forum.repository.entity.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.example.forum.repository.CommentRepository;

@Service
public class ReportService {
    @Autowired
    ReportRepository reportRepository;
    @Autowired
    CommentService commentService;

    /*
     * 投稿一覧取得（更新日時降順）　戻り値はList<ReportForm>なので、投稿の全部を取得している
     */
    public List<ReportForm> findAllReport() {
        //ここでRepositryを呼んで、DBから全投稿をとってきて更新日時の降順で並べる
        //Reportテーブルに対応したReportエンティティのリストをDBからRepositryがとってくる→それをresultsへ代入
        List<Report> results = reportRepository.findAllByOrderByUpdatedDateDesc();
        //DBのReportはEntityのため、そのまま渡さずにFormリストに変換してControllerへ返す
        return convertToFormList(results);
    }

    /*
     * ↑の処理の続き＝画面表示用にデータを変換する　　Entity → Form に変換 & コメントをセット
     */
    private List<ReportForm> convertToFormList(List<Report> results) {
        //変換後の ReportForm を入れる空のリストを作る new ArrayList<>()で空のリストを作る。
        List<ReportForm> forms = new ArrayList<>();
        //DBから取り出したReportのリストを一件ずつ取り出して処理する
        for (Report report : results) {
            //もし投稿が存在しないなら、このループはスキップして先に進める処理
            if (report == null) continue;
            //ここでformを使うのは、全投稿を表すものとしてformsを使い、一件ずつ読み取る処理の中ではformを使いたいから
            ReportForm form = new ReportForm();
            //DBから取得したID,投稿内容、日時をReportFormへセット
            form.setId(report.getId());
            form.setContent(report.getContent());
            form.setCreatedDate(report.getCreatedDate());
            form.setUpdatedDate(report.getUpdatedDate());
            //完成したReportFormをform(List<ReportForm>)へ追加。
            forms.add(form);
        }
        return forms;
    }

    /*
     * 投稿追加・更新　　reqReportはユーザーが考えたもの
     */
    public void saveReport(ReportForm reqReport) {
        //DB に保存するための Entity（Reportクラス） を入れる箱を作っている
        Report report;
        //ReportFormにIDがあるなら更新し、なければ作る。
        if (reqReport.getId() != null) {
            // 既存投稿なら DB から取得して更新し、DBに存在しなければ例外を投げる
            //-> new IllegalArgumentException("投稿が存在しません"))→ラムダ式　引数の例外を返す処理
            report = reportRepository.findById(reqReport.getId()).orElseThrow(() -> new IllegalArgumentException("投稿が存在しません"));
        } else {
            // 新規投稿の場合、Entityクラスの空のオブジェクトを作る。
            report = new Report();
            //作成した時間をセットする
            report.setCreatedDate(LocalDateTime.now());
        }
        //入力された本文をreportに入れる
        report.setContent(reqReport.getContent());
        report.setUpdatedDate(LocalDateTime.now());
        //DBにReportを保存する。新規なら追加し、既存なら更新する
        reportRepository.save(report);
    }

    /*
     * 投稿削除
     */
    public void deleteReport(Integer id) {
        reportRepository.deleteById(id);
    }

    /*
     * 投稿1件取得（編集用）して、Form(画面表示用のhtmlと関わるクラス)に変換する
     */
    public ReportForm editReport(Integer id) {
        //Report reportは、DBのテーブルに対応しているクラス名にする必要があるため
        Report report = reportRepository.findById(id).orElse(null);
        //DBにそのIDの投稿がなければnullを返す
        if (report == null) return null;
        //上記のRepositryからとってきた情報をFormにセットする→Entity は DB 用で、バリデーションや入力用の補助がないことが多いため
        ReportForm form = new ReportForm();
        form.setId(report.getId());
        form.setContent(report.getContent());
        form.setCreatedDate(report.getCreatedDate());
        form.setUpdatedDate(report.getUpdatedDate());
        // コメントもセット（画面内で投稿とコメントをまとめて表示できるようにするため
//        form.setComments(commentService.findCommentsByPostId(report.getId()));
        return form;
    }

    /*
     * 日付で投稿を絞り込み(指定された開始日時〜終了日時の間に作成された投稿だけを取得して、フォーム用のオブジェクトリストに変換する)
     */
    public List<ReportForm> findByCreatedDateBetween(LocalDateTime start, LocalDateTime end) {
        //複数の投稿が対象になるため、List型にして、DBからstart,endのEntityを取得する
        List<Report> reports = reportRepository.findByCreatedDateBetween(start, end);
        //EntityからFormに変換する　List<Report>からList<ReportForm>へ
        //reports という名前は絞り込みメソッド内の変数名
        //convertToFormList 内ではそれを results という名前で受け取るだけ
        return convertToFormList(reports);
    }

    /*
     * 投稿へのコメント機能を追加する処理から呼ばれている　　　コメントに紐づく投稿を取得するためのメソッド
     */
    //reportRepository.findById(id) … DB の Report テーブルから指定した ID の投稿を取得
    public Report findReportById(Integer id) {
        return reportRepository.findById(id).orElse(null);
    }
}
