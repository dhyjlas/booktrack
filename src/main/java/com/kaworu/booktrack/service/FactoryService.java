package com.kaworu.booktrack.service;

import com.kaworu.booktrack.config.Website;
import com.kaworu.booktrack.utils.SpringUtil;
import org.springframework.stereotype.Service;

@Service
public class FactoryService {
    public BaseCrawlService getBean(String source){
        if(source == null)
            return null;

        Website website = Website.getWebsiteBySource(source);
        if(website == null)
            return null;

        return (BaseCrawlService) SpringUtil.getBean(website.getBeanName());
    }
}
