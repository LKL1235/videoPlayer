package com.example.demo.controller;

import com.example.demo.DTO.DirDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author:25445
 * @date:2022/12/4 20:10
 **/
@RestController
public class DirController {

    @Autowired
    private DirDTO dirDTO;
    @RequestMapping("/sdir/{fdir}")
    public List<DirDTO> sdir(@PathVariable String fdir){
        // String path = "/src/video";
        // String path = "/src/video"+fdir;
        System.out.println(fdir);
        String path = "/src/video/"+fdir;
        File file = new File(path);
        File[] fs = file.listFiles();
        List<DirDTO> list =new ArrayList<>();
        for(File f:fs){
            if(!f.isDirectory()) {
                String s=f.getName();
                DirDTO dirDTO1=new DirDTO(s);
                list.add(dirDTO1);
            }
        }
        return list;
    }
    @RequestMapping("/sdir")
    public List<DirDTO> sdir1(){
        // String path = "/src/video";
        // String path = "/src/video"+fdir;
        String path = "/src/video";
        File file = new File(path);
        File[] fs = file.listFiles();
        List<DirDTO> list =new ArrayList<>();
        for(File f:fs){
            if(!f.isDirectory()) {
                String s=f.getName();
                DirDTO dirDTO1=new DirDTO(s);
                list.add(dirDTO1);
            }
        }
        return list;
    }

    @RequestMapping("/ldir")
    public List<DirDTO> ldir(){
        String path = "/src/video";		//要遍历的路径
        File file = new File(path);		//获取其file对象
        File[] fs = file.listFiles();	//遍历path下的文件和目录，放在File数组中
        List<DirDTO> list=new ArrayList<>();
        for(File f:fs){					//遍历File[]数组
            if(f.isDirectory())		//若非目录(即文件)，则打印
            {
                String s = f.getName();
                DirDTO dirDTO1=new DirDTO(s);
                list.add(dirDTO1);
                System.out.println(s);
            }
        }
        return list;
    }
}
