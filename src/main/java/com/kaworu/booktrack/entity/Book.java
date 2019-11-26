package com.kaworu.booktrack.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "t_book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", columnDefinition="bigint COMMENT 'ID'")
    private long id;

    @Column(name="book_name", columnDefinition="varchar(255) COMMENT '书名'")
    private String bookName;

    @Column(name="author", columnDefinition="varchar(255) COMMENT '作者'")
    private String author;

    @Column(name="url", columnDefinition="varchar(255) COMMENT '地址'")
    private String url;

    @Column(name="source", columnDefinition="bigint COMMENT '来源'")
    private long source;

    @Column(name="create_time", columnDefinition="varchar(255) COMMENT '创建时间'")
    private String createTime;

    @Column(name="update_time", columnDefinition="varchar(255) COMMENT '更新时间'")
    private String updateTime;

    @Column(name="chapters", columnDefinition="int COMMENT '章节数'")
    private int chapters;
}
