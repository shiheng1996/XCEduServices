package com.xuecheng.manage_cms;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GridFsTest {

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
   private  GridFsTemplate gridFsTemplate;

    /**
     * 向mongodb中存储模板文件
     */
    @Test
    public void testGridFsTest(){
        //要存储的文件
        File file = new File("D://index_banner.ftl");
        try {
            //输入流
            FileInputStream inputStream = new FileInputStream(file);
            //向mongodb的GridFs存储模板,返回值是fs.files的id
            ObjectId objectId = gridFsTemplate.store(inputStream, "index_banner.ftl");
            System.out.println(objectId.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void query() throws IOException {
        String fileid="5bf548b1b0a66d21f47a599c";
        //根据id查询文件
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileid)));
        //打开流下载对象
        GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //获取流对象
        GridFsResource gridFsResource = new GridFsResource(gridFSFile,downloadStream);
        //读取流中的数据
        String s = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
        System.out.println(s);
    }

}
