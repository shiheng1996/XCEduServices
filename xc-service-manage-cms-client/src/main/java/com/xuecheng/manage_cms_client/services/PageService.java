package com.xuecheng.manage_cms_client.services;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Optional;
@Component
public class PageService {

    private static  final Logger LOGGER = LoggerFactory.getLogger(PageService.class);
    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    CmsSiteRepository cmsSiteRepository;

    /**
     * 根据id查询页面
     * @param id
     * @return
     */
    public CmsPage findPageById(String id){
        Optional<CmsPage> cmsPage = cmsPageRepository.findById(id);
        if (cmsPage.isPresent()){
            return cmsPage.get();
        }else {
            return null;
        }

    }

    /**
     * 将页面从GridFS下载到服务器物理路径
     * @param cmsPage
     */
    public void savePageToServerPath(CmsPage cmsPage) {
        //1.从gridFS中查询html文件,返回输入流
        InputStream inputStream= getFileById(cmsPage.getHtmlFileId());
        if(inputStream == null){
            LOGGER.error("getFileById InputStream is null ,htmlFileId:{}",cmsPage.getHtmlFileId());
            return ;
        }
        //2.生成新的页面物理路径
       String  PhysicalPath = getPhysicalPath(cmsPage);
        //3.将html文件保存到服务器物理路径上
        downloadPageToServerPath1(inputStream,PhysicalPath);
    }



    //1.从gridFS中查询html文件
    private InputStream getFileById(String htmlFileId) {
        //1.1获取文件对象
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(htmlFileId)));
        //1.2打开下载流
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getId());
        //1.3定义GridFsResource
        GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
        try {
            //1.4返回输入流
            return gridFsResource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
    //2.生成新的页面物理路径
    private String getPhysicalPath(CmsPage cmsPage) {
        Optional<CmsSite> optionalCmsSite = cmsSiteRepository.findById(cmsPage.getSiteId());
        if (optionalCmsSite.isPresent()){
            //2.1站点物理路径
            String sitePhysicalPath = optionalCmsSite.get().getSitePhysicalPath();
            //2.2页面的物理路径
            String pagePhysicalPath = cmsPage.getPagePhysicalPath();
            //2.3页面名称
            String pageName = cmsPage.getPageName();
            //2.4新的物理路径
            String pagePath=sitePhysicalPath+pagePhysicalPath+pageName;
            return pagePath;
        }
        return null;
    }
    //3.将html文件保存到服务器物理路径上
    private void downloadPageToServerPath1(InputStream inputStream, String physicalPath) {
        //3.1创建新的页面物理路径文件对象
        File file = new File(physicalPath);
        FileOutputStream outputStream=null;
        try {
            //3.2创建输出流
             outputStream = new FileOutputStream(file);
            //3.3将输入流中的内容,输出到指定路径
            IOUtils.copy(inputStream,outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
