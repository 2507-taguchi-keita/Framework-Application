package com.example.forum.controller;

import com.example.forum.controller.form.ReportForm;
import com.example.forum.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ForumController {
    @Autowired
    ReportService reportService;

    /*
     * 投稿内容表示処理
     */
    @GetMapping
    public ModelAndView top() {
        ModelAndView mav = new ModelAndView();
        // 投稿を全件取得
        List<ReportForm> contentData = reportService.findAllReport();
        // 画面遷移先を指定
        mav.setViewName("/top");
        // 投稿データオブジェクトを保管
        mav.addObject("contents", contentData);
        return mav;
    }

    /*
     * 新規投稿画面表示
     */
    @GetMapping("/new")
    public ModelAndView newContent() {
        ModelAndView mav = new ModelAndView();
        // form用の空のentityを準備
        ReportForm reportForm = new ReportForm();
        // 画面遷移先を指定
        mav.setViewName("/new");
        // 準備した空のFormを保管
        mav.addObject("formModel", reportForm);
        return mav;
    }

    /*
     * 新規投稿処理
     */
    @PostMapping("/add")
    public ModelAndView addContent(@ModelAttribute("formModel") ReportForm reportForm) {
        // 投稿をテーブルに格納
        reportService.saveReport(reportForm);
        // rootつまり、⑤サーバー側：投稿内容表示機能の処理へリダイレクト
        return new ModelAndView("redirect:/");
    }

    //削除処理(削除ボタンを押した後)
    @DeleteMapping("/delete/{id}")
    //contentだと、同じ内容が入力されている可能性があるため、idを引数にする
    public ModelAndView deleteContent(@PathVariable Integer id){
        //Service層にある投稿削除の処理を呼び出して実行する
        //URLから取得してきたIDはレコードを指定する際に必要なので、引数に指定しReportServiceを呼出
        reportService.deleteReport(id);
        // rootつまり、⑤サーバー側：投稿内容表示機能の処理へリダイレクト
        return new ModelAndView("redirect:/");
    }

    //編集画面表示(編集ボタンを押した後)
    @GetMapping("/edit/{id}")
    //トップ画面から編集画面へ遷移したいので、idを引数として受け取り
    public ModelAndView editContent(@PathVariable Integer id) {
        //空のオブジェクトを生成し、この後でmav.addObjectのように中身を詰めていく
        ModelAndView mav = new ModelAndView();
        // 編集する投稿を取得
        //Serviceへアクセスして、idと投稿内容を変数report(分かりやすい名前ならなんでもいい)へ格納
        ReportForm report = reportService.editReport(id);
        // 編集する投稿をセット
        //「report というオブジェクト（編集したい投稿）」をformModel という名前で HTML に渡している
        mav.addObject("formModel", report);
        // 画面遷移先を指定して戻り値mavを返す
        mav.setViewName("/edit");
        return mav;
    }

    //編集処理(更新ボタンを押下した時)
    @PutMapping("/update/{id}")
    //編集画面から、id および formModel の変数名で入力された投稿内容を受け取る
    public ModelAndView editContent(@PathVariable Integer id, @ModelAttribute("formModel") ReportForm report){
        // UrlParameterのidを更新するentityにセット
        report.setId(id);
        // 編集した投稿を更新
        reportService.saveReport(report);
        // rootつまり、⑤サーバー側：投稿内容表示機能の処理へリダイレクト
        return new ModelAndView("redirect:/");
    }
}

