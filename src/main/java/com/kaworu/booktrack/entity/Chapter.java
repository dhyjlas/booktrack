package com.kaworu.booktrack.entity;

import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Table(name = "t_chapter")
public class Chapter {
    private long id;
    private long bookId;
    private String bookName;
    private int chapterId;
    private String chapterName;
    private String url;
    private boolean status;
    private String content;
    private String updateTime;
    private String source;

    private long serial;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", columnDefinition="bigint COMMENT 'ID'")
    public long getId() {
        return id;
    }

    @Column(name="book_id", columnDefinition="bigint COMMENT '图书ID'")
    public long getBookId() {
        return bookId;
    }

    @Column(name="book_name", columnDefinition="varchar(255) COMMENT '图书名'")
    public String getBookName() {
        return bookName;
    }

    @Column(name="chapter_id", columnDefinition="int COMMENT '章节号'")
    public int getChapterId() {
        return chapterId;
    }

    @Column(name="chapter_name", columnDefinition="varchar(255) COMMENT '章节名'")
    public String getChapterName() {
        return chapterName;
    }

    @Column(name="url", columnDefinition="varchar(255) COMMENT 'URL'")
    public String getUrl() {
        return url;
    }

    @Column(name="status", columnDefinition="varchar(255) COMMENT '状态'")
    public boolean isStatus() {
        return status;
    }

    @Column(name="content", columnDefinition="mediumtext COMMENT '正文'")
    public String getContent() {
        return content;
    }

    @Column(name="updateTime", columnDefinition="varchar(255) COMMENT '更新时间'")
    public String getUpdateTime() {
        return updateTime;
    }

    @Column(name="source", columnDefinition="varchar(255) COMMENT '来源'")
    public String getSource() {
        return source;
    }

    @Transient
    public long getSerial() {
        return serial;
    }
}
