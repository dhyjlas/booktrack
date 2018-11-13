package com.kaworu.booktrack.config;

import lombok.Getter;

@Getter
public enum Website {
    BIQUKAN("笔趣看", "www.biqukan.com", "1", "biqukanService"),
    BIQUGE("笔趣阁", "www.biquge.com.tw", "2", "biqugeService");

    private final String siteName;
    private final String host;
    private final String source;
    private final String beanName;

    Website(String siteName, String host, String source, String beanName){
        this.siteName = siteName;
        this.host = host;
        this.source = source;
        this.beanName = beanName;
    }

    /**
     * 通过域名获取
     * @param host
     * @return
     */
    public static Website getWebsiteByHost(String host){
        if(host == null)
            return null;

        for(Website temp:Website.values()){
            if(temp.getHost().equals(host)){
                return temp;
            }
        }
        return null;
    }

    /**
     * 通过来源号获取
     * @param source
     * @return
     */
    public static Website getWebsiteBySource(String source){
        if(source == null)
            return null;

        for(Website temp:Website.values()){
            if(temp.getSource().equals(source)){
                return temp;
            }
        }
        return null;
    }
}
