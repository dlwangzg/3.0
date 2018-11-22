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

import com.leadingsoft.bizfuse.cloud.saas.server.model.ServerInstance;
import com.leadingsoft.bizfuse.common.web.utils.ExcelUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ServerInstanceExcelConvertor {

    public List<ServerInstance> parse(final InputStream in) {
        Workbook workbook = null;
        try {
            workbook = new HSSFWorkbook(in);

            final List<ServerInstance> serverInstances = new ArrayList<ServerInstance>();
            final Sheet dcsSheet = workbook.getSheetAt(0);

            int rowNumber = 1;
            Row row = null;
            while (rowNumber <= dcsSheet.getLastRowNum()) {
                row = dcsSheet.getRow(rowNumber++);
                if (row == null) {
                    break;
                }
                serverInstances.add(this.parse(row));
            }
            return serverInstances;
        } catch (final Exception e) {
            ServerInstanceExcelConvertor.log.error("文件数据导入失败", e);
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

    private ServerInstance parse(final Row row) {
        int columnNumber = 1;
        final String type = ExcelUtils.getCellStringValue(row, columnNumber++, false);
        final String internalIP = ExcelUtils.getCellStringValue(row, columnNumber++, false);
        final String publicIP = ExcelUtils.getCellStringValue(row, columnNumber++, true);
        final Integer port = ExcelUtils.getCellIntegerValue(row, columnNumber++, false);
        final String remark = ExcelUtils.getCellStringValue(row, columnNumber++, true);
        final ServerInstance instance = new ServerInstance();
        instance.setType(type);
        instance.setInternalIP(internalIP);
        instance.setPublicIP(publicIP);
        instance.setPort(port);
        instance.setRemarks(remark);
        return instance;
    }
}
