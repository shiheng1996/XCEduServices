package com.xuecheng.manage_cms_client.mq;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms_client.services.PageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ConsumerPostPage {
    private static final Logger LOGGER= LoggerFactory.getLogger(ConsumerPostPage.class);

   @Autowired
    private PageService pageService;
    @RabbitListener(queues = {"${xuecheng.mq.queue}"})
    public void postPage(Map map){
        String pageId = (String) map.get("pageId");
        CmsPage cmsPage = pageService.findPageById(pageId);
        if (cmsPage==null){
            LOGGER.error("receive postpage msg,cmsPage is null,pageId:{}",pageId);
            return;
        }else {
            //将页面从GridFS下载到服务器物理路径
            pageService.savePageToServerPath(cmsPage);
        }
    }

}
