package com.leadingsoft.bizfuse.base.dict.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.codahale.metrics.annotation.Timed;
import com.leadingsoft.bizfuse.base.dict.model.DictionaryCategory;
import com.leadingsoft.bizfuse.base.dict.service.DictionaryCategoryService;
import com.leadingsoft.bizfuse.base.dict.service.DictionaryExcelImportService;
import com.leadingsoft.bizfuse.common.web.dto.result.ResultDTO;

/**
 * 数据字典的管理接口
 *
 * @author sunyx
 */
@Controller
@RequestMapping("/w/dicts")
public class DictionaryExcelController {
    @Autowired
    private DictionaryCategoryService dictionaryCategoryService;
    @Autowired
    private DictionaryExcelImportService dictionaryExcelImportService;

    // curl -c /tmp/localCookie http://localhost:8080/login?username=admin&password=rd1234
    // curl -b /tmp/localCookie -F "file=@/tmp/test.xls" http://localhost:8080/w/dicts/excel/upload.do
    @Timed
    @RequestMapping(value = "/excel/upload", method = {RequestMethod.POST })
    @ResponseBody
    public ResultDTO<?> uploadPolicyFile(@RequestPart("file") final MultipartFile file) throws IOException {
        this.dictionaryExcelImportService.upload(file);

        return ResultDTO.success();
    }

    /// Users/sunyx/workspace/tima_develop/03编码/tima1231/business/business/src/main/resources/excel/dictionary-sample.xls
    // curl -c /tmp/localCookie http://localhost:8080/login?username=admin&password=rd1234
    // curl -b /tmp/localCookie http://localhost:8080/w/dicts/excel/download.do -o /tmp/test1.xls
    @Timed
    @RequestMapping(value = "/excel/download", method = RequestMethod.GET)
    public ModelAndView generateExcel(final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {

        final Iterable<DictionaryCategory> dictionaryCategories = this.dictionaryCategoryService.findAll();

        final ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("filename", "码表文件.xls");
        modelMap.addAttribute("data", dictionaryCategories);

        return new ModelAndView("dictionaryExcelView", modelMap);
    }

}
