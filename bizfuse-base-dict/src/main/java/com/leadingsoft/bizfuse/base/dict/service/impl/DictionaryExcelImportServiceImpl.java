package com.leadingsoft.bizfuse.base.dict.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.leadingsoft.bizfuse.base.dict.dto.DictionaryForExcelConvertor;
import com.leadingsoft.bizfuse.base.dict.model.DictionaryCategory;
import com.leadingsoft.bizfuse.base.dict.service.DictionaryCategoryService;
import com.leadingsoft.bizfuse.base.dict.service.DictionaryExcelImportService;

@Service
@Transactional
public class DictionaryExcelImportServiceImpl implements DictionaryExcelImportService {

    @Autowired
    private DictionaryForExcelConvertor dictionaryForExcelConvertor;
    @Autowired
    private DictionaryCategoryService dictionaryCategoryService;

    @Override
    public void upload(final MultipartFile file) throws IOException {

        final List<DictionaryCategory> dictionaryCategories =
                this.dictionaryForExcelConvertor.parse(file.getInputStream());

        this.dictionaryCategoryService.deleteAll();

        for (final DictionaryCategory model : dictionaryCategories) {
            this.dictionaryCategoryService.createCategory(model);
        }

    }
}
