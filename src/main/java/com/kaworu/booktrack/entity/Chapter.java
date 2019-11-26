package com.kaworu.booktrack.entity;

import lombok.Data;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Data
@Table(name = "t_chapter")
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", columnDefinition="bigint COMMENT 'ID'")
    private long id;

    @Column(name="book_id", columnDefinition="bigint COMMENT '图书ID'")
    private long bookId;

    @Column(name="book_name", columnDefinition="varchar(255) COMMENT '图书名'")
    private String bookName;

    @Column(name="chapter_id", columnDefinition="int COMMENT '章节号'")
    private int chapterId;

    @Column(name="chapter_name", columnDefinition="varchar(255) COMMENT '章节名'")
    private String chapterName;

    @Column(name="url", columnDefinition="varchar(255) COMMENT 'URL'")
    private String url;

    @Column(name="status", columnDefinition="varchar(255) COMMENT '状态'")
    private boolean status;

    @Column(name="content", columnDefinition="mediumtext COMMENT '正文'")
    private String content;

    @Column(name="updateTime", columnDefinition="varchar(255) COMMENT '更新时间'")
    private String updateTime;

    @Column(name="source", columnDefinition="bigint COMMENT '来源'")
    private long source;

    @Transient
    private long serial;
}
