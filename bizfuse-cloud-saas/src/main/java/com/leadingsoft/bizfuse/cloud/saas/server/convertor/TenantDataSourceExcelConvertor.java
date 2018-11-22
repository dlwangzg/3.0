package com.leadingsoft.bizfuse.cloud.saas.server.convertor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import com.leadingsoft.bizfuse.cloud.saas.server.model.TenantDataSourceConfig;
import com.leadingsoft.bizfuse.common.web.utils.ExcelUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TenantDataSourceExcelConvertor {
    public List<TenantDataSourceConfig> parse(final InputStream in) {
        Workbook workbook = null;
        try {
            workbook = new HSSFWorkbook(in);

            final List<TenantDataSourceConfig> TenantDataSourceConfigs = new ArrayList<TenantDataSourceConfig>();
            final Sheet dcsSheet = workbook.getSheetAt(0);

            int rowNumber = 1;
            Row row = null;
            while (rowNumber <= dcsSheet.getLastRowNum()) {
                row = dcsSheet.getRow(rowNumber++);
                if (row == null) {
                    break;
                }
                TenantDataSourceConfigs.add(this.parse(row));
            }
            return TenantDataSourceConfigs;
        } catch (final Exception e) {
            TenantDataSourceExcelConvertor.log.error("文件数据导入失败", e);
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (final IOException e) {
            }
        }
        return Collections.emptyList();
    }

    private TenantDataSourceConfig parse(final Row row) {
        int columnNumber = 1;
        final String tenantNo = ExcelUtils.getCellStringValue(row, columnNumber++, false);
        final String serverType = ExcelUtils.getCellStringValue(row, columnNumber++, false);
        final String driverClassName = ExcelUtils.getCellStringValue(row, columnNumber++, false);
        final String url = ExcelUtils.getCellStringValue(row, columnNumber++, false);
        final String username = ExcelUtils.getCellStringValue(row, columnNumber++, false);
        final String password = ExcelUtils.getCellStringValue(row, columnNumber++, false);
        final TenantDataSourceConfig config = new TenantDataSourceConfig();
        config.setTenantNo(tenantNo);
        config.setServerType(serverType);
        config.setDriverClassName(driverClassName);
        config.setUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        return config;
    }
}
