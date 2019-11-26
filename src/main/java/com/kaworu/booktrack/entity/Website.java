package com.kaworu.booktrack.entity;

import lombok.Data;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Data
@Table(name = "t_website")
public class Website {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", columnDefinition="bigint COMMENT 'ID'")
    private long id;

    @Column(name="name", columnDefinition="varchar(255) COMMENT '网站名称'")
    private String name;

    @Column(name="host", columnDefinition="varchar(255) COMMENT '主域名'")
    private String host;

    @Column(name="max_out_time", columnDefinition="integer COMMENT '最长超时时间'")
    private int maxOutTime;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name="book_name_xpath", columnDefinition="longtext COMMENT '书名规则'")
    private String bookNameXpath;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name="author_xpath", columnDefinition="longtext COMMENT '作者规则'")
    private String authorXpath;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name="chapter_url_xpath", columnDefinition="longtext COMMENT '章节地址规则'")
    private String chapterUrlXpath;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name="chapter_name_xpath", columnDefinition="longtext COMMENT '章节名称规则'")
    private String chapterNameXpath;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name="chapter_content_xpath", columnDefinition="longtext COMMENT '章节正文规则'")
    private String chapterContentXpath;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name="header", columnDefinition="longtext COMMENT '请求头参数'")
    private String header;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name="content_replace", columnDefinition="longtext COMMENT '内容替换'")
    private String contentReplace;
}
