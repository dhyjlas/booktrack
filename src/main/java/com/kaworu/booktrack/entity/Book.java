package com.kaworu.booktrack.entity;

import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Table(name = "t_book")
public class Book {
    private long id;
    private String bookName;
    private String author;
    private String url;
    private String source;
    private String createTime;
    private String updateTime;
    private int chapters;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", columnDefinition="bigint COMMENT 'ID'")
    public long getId() {
        return id;
    }

    @Column(name="book_name", columnDefinition="varchar(255) COMMENT '书名'")
    public String getBookName() {
        return bookName;
    }

    @Column(name="author", columnDefinition="varchar(255) COMMENT '作者'")
    public String getAuthor() {
        return author;
    }

    @Column(name="url", columnDefinition="varchar(255) COMMENT '地址'")
    public String getUrl() {
        return url;
    }

    @Column(name="source", columnDefinition="varchar(255) COMMENT '来源'")
    public String getSource() {
        return source;
    }

    @Column(name="create_time", columnDefinition="varchar(255) COMMENT '创建时间'")
    public String getCreateTime() {
        return createTime;
    }

    @Column(name="update_time", columnDefinition="varchar(255) COMMENT '更新时间'")
    public String getUpdateTime() {
        return updateTime;
    }

    @Column(name="chapters", columnDefinition="int COMMENT '章节数'")
    public int getChapters() {
        return chapters;
    }
}
