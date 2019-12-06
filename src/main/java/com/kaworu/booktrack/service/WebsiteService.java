package com.kaworu.booktrack.service;

import com.alibaba.fastjson.JSON;
import com.kaworu.booktrack.entity.TestDTO;
import com.kaworu.booktrack.entity.Website;
import com.kaworu.booktrack.exception.BusinessException;
import com.kaworu.booktrack.repository.WebsiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class WebsiteService {
    @Autowired
    private WebsiteRepository websiteRepository;

    @Autowired
    private XPathCrawlService xPathCrawlService;

    /**
     * 获取站点列表
     * @param page
     * @param size
     * @param sort
     * @param direction
     * @param query
     * @return
     */
    public Page<Website> list(int page, int size, String sort, String direction, String query){
        Sort sortDate = new Sort("desc".equals(direction) ? Sort.Direction.DESC : Sort.Direction.ASC, sort);
        Pageable pageable = PageRequest.of(page, size, sortDate);

        Specification<Website> example = (Specification<Website>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            //站点名称
            if(!StringUtils.isEmpty(query)){
                Predicate predicate = criteriaBuilder.like(root.get("name").as(String.class), "%" + query + "%");
                predicates.add(predicate);
            }
            //站点地址
            if(!StringUtils.isEmpty(query)){
                Predicate predicate = criteriaBuilder.like(root.get("host").as(String.class), "%" + query + "%");
                predicates.add(predicate);
            }

            if (predicates.size() == 0) {
                return null;
            }

            Predicate[] predicateArr = new Predicate[predicates.size()];
            predicateArr = predicates.toArray(predicateArr);

            return criteriaBuilder.or(predicateArr);
        };

        return websiteRepository.findAll(example, pageable);
    }

    /**
     * 获取所有站点
     * @return
     */
    public List<Website> getWebsites(){
        return websiteRepository.findAll();
    }

    public Website findById(long id){
        return websiteRepository.findById(id).orElse(null);
    }

    /**
     * 通过url分析来源
     * @param url
     * @return
     * @throws MalformedURLException
     */
    public Website analysisUrl(String url) throws MalformedURLException {
        URL urlObject = new URL(url);
        String host = urlObject.getHost();
        System.out.println(host);

        List<Website> websiteList = websiteRepository.findAll();
        for(Website website : websiteList){
            String webhost = new URL(website.getHost()).getHost();
            if(webhost.equals(host)){
                return website;
            }
        }

        throw new BusinessException("未找到匹配的域名");
    }

    /**
     * 保存站点
     * @param website
     */
    public void save(Website website){
        if(website.getId() > 0){
            Website website0 = websiteRepository.findById(website.getId()).orElse(null);
            if(website0 == null){
                throw new BusinessException("找不到该ID");
            }
            website0.setName(website.getName());
            website0.setHost(website.getHost());
            website0.setHeader(website.getHeader());
            website0.setBookNameXpath(website.getBookNameXpath());
            website0.setAuthorXpath(website.getAuthorXpath());
            website0.setChapterNameXpath(website.getChapterNameXpath());
            website0.setChapterUrlXpath(website.getChapterUrlXpath());
            website0.setChapterContentXpath(website.getChapterContentXpath());
            website0.setHeader(website.getHeader());
            website0.setContentReplace(website.getContentReplace());
            website0.setMaxOutTime(website.getMaxOutTime());
            website = website0;
        }

        if(website.getMaxOutTime() < 1){
            website.setMaxOutTime(10000);
        }
        websiteRepository.save(website);
    }

    /**
     * 删除站点
     * @param id
     */
    public void deleteById(long id){
        websiteRepository.deleteById(id);
    }

    /**
     * 规则测试
     * @param website
     * @return
     */
    public TestDTO test(Website website){
        TestDTO testDTO = new TestDTO();
        try {
            testDTO.setBookName(xPathCrawlService.parseToListForString(website.getCatalogAddress(), website, website.getBookNameXpath()));
        } catch (Exception e) {
            testDTO.setBookName("解析错误");
            e.printStackTrace();
        }
        try {
            testDTO.setAuthor(xPathCrawlService.parseToListForString(website.getCatalogAddress(), website, website.getAuthorXpath()));
        } catch (Exception e) {
            testDTO.setAuthor("解析错误");
            e.printStackTrace();
        }
        try {
            testDTO.setChapterName(xPathCrawlService.parseToListForString(website.getCatalogAddress(), website, website.getChapterNameXpath()));
        } catch (Exception e) {
            testDTO.setChapterName("解析错误");
            e.printStackTrace();
        }
        try {
            testDTO.setChapterUrl(xPathCrawlService.parseToListForString(website.getCatalogAddress(), website, website.getChapterUrlXpath()));
        } catch (Exception e) {
            testDTO.setChapterUrl("解析错误");
            e.printStackTrace();
        }
        try {
            String chapterContent = xPathCrawlService.parseToListForString(website.getContentAddress(), website, website.getChapterContentXpath());
            String contentReplace = website.getContentReplace();
            if(!StringUtils.isEmpty(contentReplace)) {
                if (!StringUtils.isEmpty(contentReplace)) {
                    Map<String, String> map = (Map<String, String>) JSON.parse(contentReplace);
                    for (String key : map.keySet()) {
                        chapterContent = chapterContent.replaceAll(key, map.get(key));
                    }
                }
            }
            testDTO.setChapterContent(chapterContent);
        } catch (Exception e) {
            testDTO.setChapterContent("解析错误");
            e.printStackTrace();
        }

        return testDTO;
    }
}
