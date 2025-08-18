package com.example.forum.controller;

import ch.qos.logback.core.model.Model;
import com.example.forum.controller.form.CommentForm;
import com.example.forum.controller.form.ReportForm;
import com.example.forum.repository.ReportRepository;
import com.example.forum.repository.entity.Comment;
import com.example.forum.repository.entity.Report;
import com.example.forum.service.CommentService;
import com.example.forum.service.ReportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class ForumController {
    @Autowired
    ReportService reportService;
    @Autowired
    CommentService commentService;
    /*
     * 投稿内容表示処理
     * ユーザーが何をするのか、クリック毎に処理を分けるイメージ
     */
    @GetMapping
    public ModelAndView top() {
        //「どの画面に飛ばすかが途中で変わる」ような処理があるときは setViewName
        ModelAndView mav = new ModelAndView("/top");
        // 投稿を全件取得し、Serviceへ処理を任せる　　Serviceは、「何を、どうするか」を決める場所→DBに追加したり、DBから取得したりする場所
        List<ReportForm> contentData = reportService.findAllReport();
        List<CommentForm> commentData = commentService.findAllComment();
        // 投稿データオブジェクトを保管　mavに代入したものをtopで表示するように
        mav.addObject("contents", contentData);
        mav.addObject("comments", commentData);
        //topページを表示させ、投稿一覧(content)とコメント入力用フォーム(CommentForm)を画面に渡す
        mav.addObject("commentForm", new CommentForm());
        return mav;
    }

    /*
     * 新規投稿画面表示
     */
    @GetMapping("/new")
    public ModelAndView newContent() {
        //どの画面に飛ばすかを指定している
        ModelAndView mav = new ModelAndView("/new");
        // Html用の空のentityを準備→この時点ではまだ何も入力していないため、何も入力されていない箱があるとエラーになる。
        //エラーにならずに存在させるために、ここでReportForm(ユーザーが入力する情報が入ったクラスに)空の箱を作る。
        ReportForm reportForm = new ReportForm();
        // 準備した空のFormをformModelという名前で渡す
        mav.addObject("formModel", reportForm);
        return mav;
    }

    /*
     * 新規投稿処理+エラー処理
     */
    @PostMapping("/add")
    //画面で入力された値がReportFormに自動で入る。"formModel"という名前で画面に返す時にも利用できる
    //Validated=バリデーションの結果をresultに入れる。
    public ModelAndView addContent(@ModelAttribute("formModel") @Validated ReportForm reportForm, BindingResult result) {
        if(result.hasErrors()){
            ModelAndView mav = new ModelAndView("/new");
            mav.addObject("formModel", reportForm);
            return mav;
        }
        Report report = new Report();
        report.setContent(reportForm.getContent());
        // 投稿をテーブルに格納 saveReportの名前は自由。分かりやすい名前を
        reportService.saveReport(reportForm);
        // rootつまり、⑤サーバー側：投稿内容表示機能の処理へリダイレクト
        //保存処理が終わったら、もう一度トップページにアクセスしなおすように指定している。
        //ここでのnewは、mavというクラスを返すために必要なオブジェクトを作っている
        return new ModelAndView("redirect:/");
    }

    //削除処理(削除ボタンを押した後)
    @DeleteMapping("/delete/{id}")
    //contentだと、同じ内容が入力されている可能性があるため、idを引数にする
    public ModelAndView deleteContent(@PathVariable Integer id) {
        //Service層に投稿削除の処理をしてもらう
        //URLから取得してきたIDはレコードを指定する際に必要なので、引数に指定しReportServiceを呼出
        reportService.deleteReport(id);
        // rootつまり、⑤サーバー側：投稿内容表示機能の処理へリダイレクト
        return new ModelAndView("redirect:/");
    }

    //編集画面表示(編集ボタンを押した後)
    @GetMapping("/edit/{id}")
    //トップ画面から編集画面へ遷移したいので、idを引数として受け取り
    public ModelAndView editContent(@PathVariable Integer id){
        //空のオブジェクトを生成し、この後でmav.addObjectのように中身を詰めていく
        ModelAndView mav = new ModelAndView();
        // Service は DB から投稿内容を取ってきて ReportForm に入れる→そして、reportへ入る
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
    public ModelAndView editContent(@PathVariable Integer id, @ModelAttribute("formModel") @Validated ReportForm report, BindingResult result) {
        if(result.hasErrors()){
            ModelAndView mav = new ModelAndView("/edit");
            mav.addObject("formModel", report);
            return mav;
        }
        // UrlParameterのidを更新するentityにセット
        report.setId(id);
        // 編集した投稿を更新
        reportService.saveReport(report);
        // rootつまり、⑤サーバー側：投稿内容表示機能の処理へリダイレクト
        return new ModelAndView("redirect:/");
    }

    //投稿へのコメント機能を追加(返信ボタンを押した後)
    @PostMapping("/comment/{postId}")
    //Integer postIdで、どの投稿に追加するのかを指定。HTMLのcommentFormに入力された内容がCommentFormに自動で入る。で、resultにバリデーション結果が入る。
    //HttpSessionは、ページをまたいでも一時的に情報を保持しておきたい時に使う
    public ModelAndView commentContent(@PathVariable Integer postId, @ModelAttribute("commentForm") @Validated CommentForm commentForm,
            BindingResult result, HttpSession session) {
        if (result.hasErrors()) {
            //CommentFormにあるpostIdに値を詰める作業が必要
            reportService.findReportById(postId);
            //コメント対象の投稿 ID をセッションに保存→トップ画面に戻った時にどの投稿にコメントしていたのかを覚えておくため
            session.setAttribute("postId", postId);
            ModelAndView mav = new ModelAndView("/top");
            //トップ画面を再表示する際に投稿一覧(contents)と、ユーザーが入力したコメント(commentForm)を表示するためにセットしておく
            mav.addObject("contents", reportService.findAllReport());
            mav.addObject("comments", commentService.findAllComment());
            mav.addObject("commentForm", commentForm);
            return mav;
        }
        //エラーが無かった場合は、一時的に保持していたものは不要になるため、削除する
        session.removeAttribute("postId");
        // 投稿取得→ReportテーブルからIdを取得。ここでのpostIdは、Reportテーブルのidと同じ役割
        Report report = reportService.findReportById(postId);
        //新しいコメントの箱を作り(CommentテーブルのCommentカラムにデータを入れる準備をしている)、どの投稿に対するコメントなのか(postId)、コメント内容、ユーザーIDをセットする(ログイン機能は無いので固定で)
        Comment comment = new Comment();
        comment.setPostId(postId);
        //Commentはユーザーが操作している情報なので、自動で入力されたcommentFormからとってくる必要がある
        comment.setComment(commentForm.getComment());
        // ユーザーIDは固定値で代用（例: 1）
        comment.setUserId(1);
        //Serviceにコメントを保存してもらう→コメント単体だけではどの投稿に紐づけるか分からないため、二つの引数を渡している
        //postIDでは、投稿とコメント自体は連携出来ていない。そのため、両方を引数で渡す必要がある。
        commentService.saveComment(comment, report);
        return new ModelAndView("redirect:/");
    }

    //削除処理(削除ボタンを押した後)
    @DeleteMapping("/comment/delete/{id}")
    //contentだと、同じ内容が入力されている可能性があるため、idを引数にする
    public ModelAndView deleteComment(@PathVariable Integer id) {
        //Service層にある投稿削除の処理を呼び出して実行する
        //URLから取得してきたIDはレコードを指定する際に必要なので、引数に指定しReportServiceを呼出
        commentService.deleteById(id);
        // rootつまり、⑤サーバー側：投稿内容表示機能の処理へリダイレクト
        //同じ画面を表示するには、Controllerに指示する必要がある。
        return new ModelAndView("redirect:/");
    }

    //返信コメントの編集画面表示処理（コメントの編集ボタンを押した時、画面を遷移させる）
    @GetMapping("/comment/edit/{id}")
    //引数にコメント自体のIDを渡し、どのコメントを編集するかを指示
    public ModelAndView editCommentForm(@PathVariable Integer id) {
        //どの画面を表示するかを指定して、ModelAndViewオブジェクトを作る
        ModelAndView mav = new ModelAndView("/comment_edit");
        //自動的に入力されるCommentFormの変数commentに、サービスから取得したコメント情報を代入
        CommentForm comment = commentService.findCommentById(id);
        //サービスから取得したcommentをcommentFormに入れて、画面へ表示させる→編集前のコメントが画面に表示される
        mav.addObject("commentForm", comment);
        return mav;
    }

    //返信コメントの表示処理（更新ボタンを押した時、画面を遷移させて編集後のメッセージを表示させる）
    @PutMapping("/comment/update/{id}")
    //どのコメントを更新するかを指定するコメント自体のIDと、更新後のコメント内容(commentForm)を引数にする。
    public ModelAndView updateComment(@PathVariable Integer id, @ModelAttribute("commentForm") @Validated CommentForm commentForm, BindingResult result) {
        if(result.hasErrors()){
            ModelAndView mav = new ModelAndView("/comment_edit");
            mav.addObject("commentForm", commentForm);
            return mav;
        }
        // 既存のコメントをDBから取得　変数は分かりやすくつけた名前
        Comment existingComment = commentService.findCommentEntityById(id);
        //存在しないコメントを更新しようとするとエラーになるため、コメントが存在しなければトップ画面へ
        if (existingComment == null) {
            return new ModelAndView("redirect:/"); // 存在しなければトップへリダイレクト
        }
        // 更新内容をセット（createdDateはそのまま保持）Commentでユーザーが入力した新しいメッセージに更新。Updateddateで更新日時を現在へ
        //ここでは変更したいものをセットする。→既に存在するものを上書きしたいので、setを使用
        existingComment.setComment(commentForm.getComment());
        existingComment.setUpdatedDate(LocalDateTime.now());
        // コメントを保存してもらうようにサービスへ処理を依頼
        commentService.saveCommentEntity(existingComment);
        //トップ画面に戻して画面を更新
        return new ModelAndView("redirect:/");
    }

    /*
     * 日付で投稿を絞り込み
     */
    @GetMapping("/date")
    //ユーザーが選択した日付をRequestPalamで取得する。
    //DateTimeFormatで、文字列で渡ってきた値を日付型にし、「文字列の形式が yyyy-MM-dd ですよ」と指定してくれる
    public ModelAndView filterByDate(@RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        ModelAndView mav = new ModelAndView("/top");
        mav.addObject("commentForm", new CommentForm());
        //日付が入力されていない場合はエラーを返し、フィルターしていない全投稿を表示させる
        if (startDate == null || endDate == null) {
            mav.addObject("dateError", "開始日・終了日を入力してください");
            mav.addObject("contents", reportService.findAllReport());
            return mav;
        }
        //日付をLocalDateTimeに変換する。00:00~23:59:59に。
        //日付のみであれば、同日に投稿したコメントを比較できないため。
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        //絞り込んで返ってくる値が一つとは限らないので、List型を使う
        //ReportForm は Controller 側で扱いやすいように DB から取った投稿情報を詰めた「箱」
        List<ReportForm> filteredReports = reportService.findByCreatedDateBetween(startDateTime, endDateTime);
        //絞り込みした後の投稿と、画面に戻すときに検索日付も表示できるように startDate と endDate も渡す
        mav.addObject("contents", filteredReports);
        mav.addObject("startDate", startDate);
        mav.addObject("endDate", endDate);
        return mav;
    }
}

