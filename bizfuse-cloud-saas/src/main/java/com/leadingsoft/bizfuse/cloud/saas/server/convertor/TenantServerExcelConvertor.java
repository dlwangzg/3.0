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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.leadingsoft.bizfuse.cloud.saas.server.model.ServerInstance;
import com.leadingsoft.bizfuse.cloud.saas.server.model.TenantServerConfig;
import com.leadingsoft.bizfuse.cloud.saas.server.repository.ServerInstanceRepository;
import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;
import com.leadingsoft.bizfuse.common.web.utils.ExcelUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TenantServerExcelConvertor {

    @Autowired
    private ServerInstanceRepository serverInstanceRepository;

    public List<TenantServerConfig> parse(final InputStream in) {
        Workbook workbook = null;
        try {
            workbook = new HSSFWorkbook(in);

            final List<TenantServerConfig> TenantServerConfigs = new ArrayList<TenantServerConfig>();
            final Sheet dcsSheet = workbook.getSheetAt(0);

            int rowNumber = 1;
            Row row = null;
            while (rowNumber <= dcsSheet.getLastRowNum()) {
                row = dcsSheet.getRow(rowNumber++);
                if (row == null) {
                    break;
                }
                TenantServerConfigs.add(this.parse(row));
            }
            return TenantServerConfigs;
        } catch (final Exception e) {
            TenantServerExcelConvertor.log.error("文件数据导入失败", e);
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

    private TenantServerConfig parse(final Row row) {
        int columnNumber = 1;
        final String tenantNo = ExcelUtils.getCellStringValue(row, columnNumber++, false);
        final String type = ExcelUtils.getCellStringValue(row, columnNumber++, false);
        final String internalIP = ExcelUtils.getCellStringValue(row, columnNumber++, false);
        final Integer port = ExcelUtils.getCellIntegerValue(row, columnNumber++, false);
        final TenantServerConfig config = new TenantServerConfig();
        config.setTenantNo(tenantNo);
        final ServerInstance server = this.serverInstanceRepository.findByInternalIPAndPort(internalIP, port);
        if (server == null) {
            throw new CustomRuntimeException("server.notfound", String.format("服务器实例 [%s:%s] 不存在. ", internalIP, port));
        }
        if (!server.getType().equals(type)) {
            throw new CustomRuntimeException("server.notfound",
                    String.format("服务器类型 [%s:%s] 不符. ", server.getType(), type));
        }
        config.setTenantNo(tenantNo);
        config.setServerInstance(server);
        return config;
    }
}
