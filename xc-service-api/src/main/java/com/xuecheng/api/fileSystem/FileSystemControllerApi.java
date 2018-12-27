package com.xuecheng.api.fileSystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;
@Api(value="文件管理接口",description = "文件管理接口，提供文件的上传下载")
public interface FileSystemControllerApi {
    /**
     * 上传文件
     * @param multipartFile 文件
     * @param filetag 文件标签
     * @param businesskey 业务key
     * @param  metadata   元信息,json格式
     * @return
     */
    @ApiOperation("上传文件")
    public UploadFileResult upload(MultipartFile multipartFile,String filetag,String businesskey, String metadata);

}
