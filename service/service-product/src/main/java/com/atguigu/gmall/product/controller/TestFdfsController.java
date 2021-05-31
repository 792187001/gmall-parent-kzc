package com.atguigu.gmall.product.controller;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@CrossOrigin
public class TestFdfsController {
    public static void main(String[] args) throws IOException, MyException {
        String path = TestFdfsController.class.getClassLoader().getResource("tracker.conf").getPath();
        //初始化fdfs的全局配置,默认读取配置信息
        ClientGlobal.init(path);//读取配置信息，写配置文件

        //获取tracker连接
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer connection = trackerClient.getConnection();
        //获取一个干活的storage
        StorageClient storageClient = new StorageClient(connection,null);//第二个可以指定一个storage
        //上传文件                                                           ‪C:\Users\kzc\Desktop\aa.jpg
        String[] urls = storageClient.upload_appender_file("C:/Users/kzc/Desktop/aa.jpg", "jpg", null);
        String imgUrl = "";
        for (String url : urls) {
            imgUrl = imgUrl +"/"+ url;
        }
        System.out.println(imgUrl);
    }
}
