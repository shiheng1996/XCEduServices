package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CmsConfigService  {
    @Autowired
    private CmsConfigRepository cmsConfigRepository;

    //根据id查询cmsconfig
    public CmsConfig  getCmsConfig(String id){
        if (id!=null){
            Optional<CmsConfig> byId = cmsConfigRepository.findById(id);
            if (byId.isPresent()){
                CmsConfig cmsConfig = byId.get();
                return cmsConfig;
            }

        }
        return null;
    }

}
