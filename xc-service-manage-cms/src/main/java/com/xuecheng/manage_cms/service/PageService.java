package com.xuecheng.manage_cms.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PageService {
    @Autowired
    private CmsPageRepository cmsPageRepository;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFSBucket gridFSBucket;
    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;
    @Autowired
    private CmsConfigRepository cmsConfigRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 分页条件查询
     * @param page
     * @param size
     * @param queryPageRequest
     * @return
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest){
        if(queryPageRequest==null){
            queryPageRequest=new QueryPageRequest();
        }
        if (page<0){
            page=1;
        }
        if (size<0){
            size=20;
        }
        //1自定义条件查询
        //1.1 创建条件匹配对象
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        //1.2封装查询条件
        CmsPage cmsPage = new CmsPage();
        if (StringUtils.isNoneEmpty(queryPageRequest.getPageAliase())){
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteId())){
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        if (StringUtils.isNotEmpty(queryPageRequest.getTemplateId())){
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }
        //1.3 创建条件对象
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        //2 设置分页 page为当前页 size为每页显示条数
        page=page-1;
        Pageable pageable = PageRequest.of(page, size);
        //3 调用dao查询,封装查询结果集
        Page<CmsPage> cmsPages = cmsPageRepository.findAll(example,pageable);
        QueryResult<CmsPage> queryResult = new QueryResult<>();
        queryResult.setTotal(cmsPages.getTotalElements());
        queryResult.setList(cmsPages.getContent());
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }
    /**
     * 增加新页面(还有验证是否相同)
     */
    public CmsPageResult add(CmsPage cmsPage) {
            CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
            if(cmsPage1!=null){
                ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
            }
            if (cmsPage1 == null) {//如果mongodb中没有相同的数据
                cmsPage.setPageId(null);
                cmsPageRepository.save(cmsPage);
                return new CmsPageResult(CommonCode.SUCCESS, cmsPage);
            }

            return new CmsPageResult(CommonCode.FAIL, null);
    }

    /**
     * 根据id查询页面
     * @param id
     * @return
     */
    public CmsPage findById(String id){
        if (id!=null){
            Optional<CmsPage> cmsPage = cmsPageRepository.findById(id);
            if (cmsPage.isPresent()){
                return  cmsPage.get();
            }
        }
            return null;
    }

    /**
     * 修改页面
     * @param id
     * @param cmsPage
     * @return
     */
    public CmsPageResult update(String id,CmsPage cmsPage){
           if (cmsPage!=null){
               CmsPage one = this.findById(id);
               if (one!=null) {
                   //更新模板id
                   one.setTemplateId(cmsPage.getTemplateId());
                   //更新所属站点
                   one.setSiteId(cmsPage.getSiteId());
                   //更新页面别名
                   one.setPageAliase(cmsPage.getPageAliase());
                   //更新页面名称
                   one.setPageName(cmsPage.getPageName());
                   //更新访问路径
                   one.setPageWebPath(cmsPage.getPageWebPath());
                   //更新物理路径
                   one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
                   //更新dataUrl
                   one.setDataUrl(cmsPage.getDataUrl());
                   cmsPageRepository.save(one);
                   return new CmsPageResult(CommonCode.SUCCESS,one);
               }
           }
           return  new CmsPageResult(CommonCode.FAIL,null);
    };

    /**
     * 删除页面
     * @param id
     * @return
     */
    public ResponseResult delete(String id){
        CmsPage cmsPage = this.findById(id);
        if (cmsPage!=null){
            cmsPageRepository.deleteById(id);
            return  new ResponseResult(CommonCode.SUCCESS);
        }
        return  new ResponseResult(CommonCode.FAIL);
    };

    /**
     * 发布页面
     * @param pageId
     * @return
     */
    public ResponseResult postPage(String pageId) {
        //1.执行页面静态化
        String pageHtml = this.getPageHtml(pageId);
        //2.将静态化页面存储到GridFs中
        CmsPage cmsPage = this.findById(pageId);
        if (cmsPage==null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        this.saveHtml(cmsPage,pageHtml);
        //3.向MQ发消息
        sendPostPage(cmsPage);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 1.页面静态化
     * @param pageId
     */
    public String getPageHtml(String pageId){
        //1.1根据页面id获取页面
        Optional<CmsPage> optionalCmsPage = cmsPageRepository.findById(pageId);
        if (!optionalCmsPage.isPresent()){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        CmsPage cmsPage = optionalCmsPage.get();
        //1.2获取静态页面的数据
        Map model=getModel(cmsPage);
        //1.3获取生成静态页面的模板
        String template = getTemplate(cmsPage);
        //1.4生成静态页面
        String html=generateHtml(model,template);
        if(StringUtils.isEmpty(html)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        return html;
    }
    //1.2获取静态页面的数据
    private Map getModel(CmsPage cmsPage) {
        if (StringUtils.isEmpty(cmsPage.getDataUrl())){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        //2.1使用restTemplate请求页面中的dataurl
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(cmsPage.getDataUrl(), Map.class);
        if (forEntity.getBody()==null){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        //2.2返回生成静态页面的数据
        return forEntity.getBody();
    }

    //1.3获取静态页面的模板
    private  String  getTemplate(CmsPage cmsPage)  {
        if (StringUtils.isEmpty(cmsPage.getTemplateId())){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        //3.1根据页面的模板id获取模板对象
        Optional<CmsTemplate> optionalTemplate = cmsTemplateRepository.findById(cmsPage.getTemplateId());
        if (!optionalTemplate.isPresent()){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        CmsTemplate cmsTemplate = optionalTemplate.get();
        //3.2获取模板文件id
        String templateFileId = cmsTemplate.getTemplateFileId();
        //3.3获取模板文件内容
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
        //3.4打开下载流对象
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getId());
        //3.5创建GridFsResource
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        //3.6将模板内容转为字符串
        try {
            String s  = IOUtils.toString(gridFSDownloadStream, "utf-8");
            return s;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    //1.4生成静态页面
    private String generateHtml(Map model, String template) {
        //生成配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        //创建模板加载器
        StringTemplateLoader templateLoader = new StringTemplateLoader();
        //设置模板
        templateLoader.putTemplate("template",template);
        //配置模板加载器
        configuration.setTemplateLoader(templateLoader);
        //获取模板
        try {
            Template template1 = configuration.getTemplate("template");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template1, model);
            return html;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 2.将静态化页面存储到GridFs中
     * @param cmsPage
     * @param pageHtml
     */
    private void saveHtml(CmsPage cmsPage, String pageHtml) {
        try {
            //2.1将静态页面内容放入输入流中
            InputStream inputStream = IOUtils.toInputStream(pageHtml, "utf-8");
            //2.2将输入流数据存入GridFS  objectId为fs.files中的ID
            String objectId = String.valueOf(gridFsTemplate.store(inputStream, cmsPage.getPageName()));
            //2.3设置cmapage的htmlFileId并保存
            cmsPage.setHtmlFileId(objectId);
            cmsPageRepository.save(cmsPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 3.向MQ发消息
     * @param cmsPage
     */
    private void sendPostPage(CmsPage cmsPage) {
        //3.1设置消息格式
        HashMap<String, String> map = new HashMap<>();
        map.put("pageId",cmsPage.getPageId());
        //3.2发送消息 设置交换机 routingKey为当前页面的站点id  消息内容为map
        rabbitTemplate.convertSendAndReceive(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE, cmsPage.getSiteId(),map);

    }
}
