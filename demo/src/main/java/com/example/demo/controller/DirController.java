package com.example.demo.controller;

import com.example.demo.DTO.DirDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @RequestMapping("/sdir")
    public List<DirDTO> sdir(){
        // String path = "/src/video";
        String path = "/src/video";
        File file = new File(path);
        File[] fs = file.listFiles();
        List<DirDTO> list =new ArrayList<>();
        for(File f:fs){
            if(!f.isDirectory()) {
                String s=f.getName();
                DirDTO dirDTO1=new DirDTO(s);
                list.add(dirDTO1);
                System.out.println(s);
            }
        }
        return list;
    }
}
