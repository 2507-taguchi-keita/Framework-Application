package com.example.forum.service;

import com.example.forum.controller.form.CommentForm;
import com.example.forum.repository.CommentRepository;
import com.example.forum.repository.ReportRepository;
import com.example.forum.repository.entity.Comment;
import com.example.forum.repository.entity.Report;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    // 投稿ごとにコメントをまとめるために、投稿に紐づくコメントをDBから取得してcommentFormへ変換
    //投稿にはいくつものコメントがついているか分からないので、List型。
    //formクラスを使う理由→htmlと結びついているため
//    List<CommentForm> comments = commentService.findCommentsByPostId(report.getId());
//    取得したコメントリストをform(ReportForm)にセット。
//            form.setComments(comments);

    public List<CommentForm> findAllComment() {
        //ここでRepositryを呼んで、DBから全投稿をとってきて更新日時の降順で並べる
        //Commentテーブルに対応したCommentエンティティのリストをDBからRepositryがとってくる→それをresultsへ代入
        List<Comment> results = commentRepository.findAllByOrderByUpdatedDateDesc();
        return setCommentForm(results);
    }

    // Entity → Form 変換
    private List<CommentForm> setCommentForm(List<Comment> results) {
        //変換後の CommentForm を入れる空のリストを作る new ArrayList<>()で空のリストを作る。
        List<CommentForm> comments = new ArrayList<>();
        //DBから取り出したCommentのリストを一件ずつ取り出して処理する
        for (Comment result : results) {
            //ここでcommentを使うのは、全投稿を表すものとしてcommentsを使い、一件ずつ読み取る処理の中ではcommentを使いたいから
            CommentForm comment = new CommentForm();
            //ここでEntityの全情報をFormに詰め替える
            comment.setId(result.getId());
            comment.setPostId(result.getPostId());
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
        //DB に保存するための Entity（Commentクラス） を入れる箱を作っている
        Comment comment;
        //編集で、既にDBにあるコメントを編集する場合
        if (reqComment.getId() != null) {
            // findByIdでDBから既存コメントを取得
            comment = commentRepository.findById(reqComment.getId()).orElse(new Comment());
        } else {
            // なければ、新しくコメントを生成し作成日時も付与
            comment = new Comment();
            comment.setCreatedDate(LocalDateTime.now());
        }
        //Entityにコメントの内容をセット
        comment.setPostId(reqComment.getPostId());
        comment.setUserId(reqComment.getUserId());
        comment.setComment(reqComment.getComment());
        comment.setUpdatedDate(LocalDateTime.now());
        //更新したら作成日時が空になってしまう場合を想定して
        if (comment.getCreatedDate() == null) {
            comment.setCreatedDate(LocalDateTime.now());
        }
        //Dbに保存する
        commentRepository.save(comment);
    }

    // 特定投稿のコメント一覧取得
    public List<CommentForm> findCommentsByPostId(int postId) {
        //ここでRepositryを呼んで、DBから全投稿をとってきて更新日時の降順で並べる
        //Commentテーブルに対応したCommentエンティティのリストをDBからRepositryがとってくる→それをresultsへ代入
        List<Comment> results = commentRepository.findByPostIdOrderByUpdatedDateDesc(postId);
        return setCommentForm(results);
    }

    // コメント削除　@で、途中で問題が起きたらロールバックされる
    @Transactional
    public void deleteById(Integer id) {
        commentRepository.deleteById(id);
    }

    // コメント編集ボタンを押した時に呼ばれるメソッド（編集画面に表示するためだけの処理）
    public CommentForm findCommentById(Integer id) {
        //CommentテーブルからIDをとってきて、commentに代入している。なければnull。結果はEntityで返ってくる
        Comment comment = commentRepository.findById(id).orElse(null);
        //commentがなければnullを返す
        if (comment == null) return null;
        //Entity(coment)をForm(form)に変換している。
        CommentForm form = new CommentForm();
        form.setId(comment.getId());
        form.setPostId(comment.getPostId());
        form.setUserId(comment.getUserId());
        form.setComment(comment.getComment());
        form.setCreatedDate(comment.getCreatedDate());
        form.setUpdatedDate(comment.getUpdatedDate());
        return form;
    }

    // コメント保存 + レポート更新
    //このメソッドは「コメント単体」だけでなく「そのコメントが付く投稿」も扱う必要があるため、両方引数にしている
    public void saveComment(Comment comment, Report report) {
        // 投稿がまだDBに保存されていない場合は先に保存
        if (report.getId() == null) {
            reportRepository.save(report);
        }
        // コメントに投稿IDを設定
        comment.setPostId(report.getId());
        // コメントを保存
        commentRepository.save(comment);
        // 投稿の更新日時を更新して再保存
        report.setUpdatedDate(LocalDateTime.now()); // ReportのupdatedDateを更新
        reportRepository.save(report);
    }

    // 更新ボタンを押した時の処理で呼び出されている。Entity単位で保存（既存コメントを編集する用）
    //既存のコメントを更新する場合に必要のため、Commentを引数に設定
    public void saveCommentEntity(Comment comment) {
        // コメントのupdatedDateだけ更新して保存
        comment.setUpdatedDate(LocalDateTime.now());
        commentRepository.save(comment);
    }

    // 更新ボタンを押した時の処理で呼び出されている。CommentのEntityをIDで取得
    //コメントを特定するためにこの引数にしている
    public Comment findCommentEntityById(Integer id) {
        return commentRepository.findById(id).orElse(null);
    }

    /*
    引数は そのメソッドが何をするか、どの情報が必要かで決まる
    コメントだけでいい場合 → コメントのEntityだけ
    コメント＋投稿情報が必要な場合 → コメントとReport両方
    特定のコメントを取得するだけ → IDだけ
     */
}