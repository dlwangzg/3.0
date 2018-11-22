package com.leadingsoft.bizfuse.base.filestorage.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文件服务器互ping接口
 *
 * @author shangxq
 */
@RestController
public class InfoController {

    /**
     * 取得服务器状态
     *
     * @return boolean (true表示服务器有效活跃，超时则服务器无效）
     */
    @RequestMapping(method = RequestMethod.GET,value = "/f/info")
    public boolean isAlive() {
        return true;
    }
}
