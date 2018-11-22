package com.leadingsoft.bizfuse.cloud.saas.server.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.leadingsoft.bizfuse.cloud.saas.server.convertor.ServerInstanceExcelConvertor;
import com.leadingsoft.bizfuse.cloud.saas.server.convertor.TenantDataSourceExcelConvertor;
import com.leadingsoft.bizfuse.cloud.saas.server.convertor.TenantServerExcelConvertor;
import com.leadingsoft.bizfuse.cloud.saas.server.model.ServerInstance;
import com.leadingsoft.bizfuse.cloud.saas.server.model.TenantDataSourceConfig;
import com.leadingsoft.bizfuse.cloud.saas.server.model.TenantServerConfig;
import com.leadingsoft.bizfuse.cloud.saas.server.repository.ServerInstanceRepository;
import com.leadingsoft.bizfuse.cloud.saas.server.repository.TenantDataSourceConfigRepository;
import com.leadingsoft.bizfuse.cloud.saas.server.repository.TenantServerConfigRepository;
import com.leadingsoft.bizfuse.cloud.saas.server.service.ServerInstanceService;
import com.leadingsoft.bizfuse.cloud.saas.server.service.TenantDataSourceConfigService;
import com.leadingsoft.bizfuse.cloud.saas.server.service.TenantServerConfigService;
import com.leadingsoft.bizfuse.common.web.dto.result.ResultDTO;
import com.leadingsoft.bizfuse.common.web.view.DefaultListDataExcelView;

import io.swagger.annotations.Api;

/**
 * Excel 导入、导出
 *
 * @author liuyg
 */
@Controller
@RequestMapping("/saas/excel")
@Api(tags = {"Excel 导入导出API" })
public class ExcelController {

    @Autowired
    private ServerInstanceExcelConvertor serverInstanceExcelConvertor;
    @Autowired
    private ServerInstanceService serverInstanceService;
    @Autowired
    private ServerInstanceRepository serverInstanceRepository;
    @Autowired
    private TenantDataSourceExcelConvertor tenantDataSourceExcelConvertor;
    @Autowired
    private TenantDataSourceConfigRepository tenantDataSourceConfigRepository;
    @Autowired
    private TenantDataSourceConfigService tenantDataSourceConfigService;
    @Autowired
    private TenantServerExcelConvertor tenantServerExcelConvertor;
    @Autowired
    private TenantServerConfigRepository tenantServerConfigRepository;
    @Autowired
    private TenantServerConfigService tenantServerConfigService;

    @RequestMapping(value = "/serverInstances/upload", method = {RequestMethod.POST })
    @ResponseBody
    public ResultDTO<?> uploadServers(@RequestPart("file") final MultipartFile file) throws IOException {
        final List<ServerInstance> servers =
                this.serverInstanceExcelConvertor.parse(file.getInputStream());
        servers.stream().forEach(server -> {
            final ServerInstance oldServer =
                    this.serverInstanceRepository.findByInternalIPAndPort(server.getInternalIP(), server.getPort());
            if (oldServer == null) {
                this.serverInstanceService.create(server);
            } else {
                oldServer.setPublicIP(server.getPublicIP());
                oldServer.setRemarks(server.getRemarks());
                this.serverInstanceService.update(oldServer);
            }
        });
        return ResultDTO.success();
    }

    @RequestMapping(value = "/serverInstances/download", method = {RequestMethod.GET })
    public ModelAndView downloadServers() throws IOException {
        final List<ServerInstance> servers = this.serverInstanceRepository.findAll();
        final ModelMap modelMap = new ModelMap();

        // 定义下载文件名
        modelMap.addAttribute(DefaultListDataExcelView.FILENAME, "ServerInstances.xls");
        // 数据列表
        modelMap.addAttribute(DefaultListDataExcelView.DATAS, servers);
        // 定义数据要输出到Excel的字段（按顺序输出），对应数据对象中的 field 名
        modelMap.addAttribute(DefaultListDataExcelView.HEADERS,
                Arrays.asList("type", "internalIP", "publicIP", "port", "remarks"));
        // 定义数据列在Excel表头中显示的名字
        modelMap.addAttribute(DefaultListDataExcelView.HEADER_NAMES,
                Arrays.asList("类型*", "内网IP*", "外网IP", "端口号*", "备注"));

        return new ModelAndView("defaultListDataExcelView", modelMap);
    }

    @RequestMapping(value = "/tenantDataSources/upload", method = {RequestMethod.POST })
    @ResponseBody
    public ResultDTO<?> uploadTenantDataSources(@RequestPart("file") final MultipartFile file) throws IOException {
        final List<TenantDataSourceConfig> configs =
                this.tenantDataSourceExcelConvertor.parse(file.getInputStream());
        configs.stream().forEach(dataSource -> {
            final List<TenantDataSourceConfig> oldDataSources =
                    this.tenantDataSourceConfigRepository.findByServerTypeAndTenantNoIn(dataSource.getServerType(),
                            Arrays.asList(dataSource.getTenantNo()));
            if (oldDataSources.isEmpty()) {
                this.tenantDataSourceConfigService.create(dataSource);
            } else {
                final TenantDataSourceConfig oldDataSource = oldDataSources.get(0);
                oldDataSource.setDriverClassName(dataSource.getDriverClassName());
                oldDataSource.setUrl(dataSource.getUrl());
                oldDataSource.setUsername(dataSource.getUsername());
                oldDataSource.setPassword(dataSource.getPassword());
                this.tenantDataSourceConfigService.update(oldDataSource);
            }
        });
        return ResultDTO.success();
    }

    @RequestMapping(value = "/tenantDataSources/download", method = {RequestMethod.GET })
    public ModelAndView downloadTenantDataSources(@RequestParam(required = false) final String tenantNo)
            throws IOException {
        List<TenantDataSourceConfig> dataSources = null;
        if (tenantNo != null) {
            dataSources = this.tenantDataSourceConfigRepository.findByTenantNo(tenantNo);
        } else {
            dataSources = this.tenantDataSourceConfigRepository.findByOrderByTenantNoAsc();
        }
        final ModelMap modelMap = new ModelMap();

        // 定义下载文件名
        modelMap.addAttribute(DefaultListDataExcelView.FILENAME, "TenantDataSourceConfigs.xls");
        // 数据列表
        modelMap.addAttribute(DefaultListDataExcelView.DATAS, dataSources);
        // 定义数据要输出到Excel的字段（按顺序输出），对应数据对象中的 field 名
        modelMap.addAttribute(DefaultListDataExcelView.HEADERS,
                Arrays.asList("tenantNo", "serverType", "driverClassName", "url", "username", "password"));
        // 定义数据列在Excel表头中显示的名字
        modelMap.addAttribute(DefaultListDataExcelView.HEADER_NAMES,
                Arrays.asList("租户编码*", "服务类型*", "驱动类*", "URL*", "用户名*", "密码*"));

        return new ModelAndView("defaultListDataExcelView", modelMap);
    }

    @RequestMapping(value = "/tenantServers/upload", method = {RequestMethod.POST })
    @ResponseBody
    public ResultDTO<?> uploadTenantServers(@RequestPart("file") final MultipartFile file) throws IOException {
        final List<TenantServerConfig> servers = this.tenantServerExcelConvertor.parse(file.getInputStream());
        servers.stream().forEach(server -> {
            final TenantServerConfig oldServer =
                    this.tenantServerConfigRepository.findByTenantNoAndServerInstance(server.getTenantNo(),
                            server.getServerInstance());
            if (oldServer == null) {
                this.tenantServerConfigService.create(server);
            } else {
                // 已经存在，不变更
            }
        });
        return ResultDTO.success();
    }

    @RequestMapping(value = "/tenantServers/download", method = {RequestMethod.GET })
    public ModelAndView downloadTenantServers(@RequestParam(required = false) final String tenantNo)
            throws IOException {
        List<TenantServerConfig> configs = null;
        if (tenantNo != null) {
            configs = this.tenantServerConfigRepository.findByTenantNo(tenantNo);
        } else {
            configs = this.tenantServerConfigRepository.findByOrderByTenantNoAsc();
        }
        final ModelMap modelMap = new ModelMap();

        // 定义下载文件名
        modelMap.addAttribute(DefaultListDataExcelView.FILENAME, "TenantServerConfigs.xls");
        // 数据列表
        modelMap.addAttribute(DefaultListDataExcelView.DATAS, configs);
        // 定义数据要输出到Excel的字段（按顺序输出），对应数据对象中的 field 名
        modelMap.addAttribute(DefaultListDataExcelView.HEADERS,
                Arrays.asList("tenantNo", "serverInstance.type", "serverInstance.internalIP", "serverInstance.port"));
        // 定义数据列在Excel表头中显示的名字
        modelMap.addAttribute(DefaultListDataExcelView.HEADER_NAMES,
                Arrays.asList("租户编码*", "服务类型*", "服务器内网IP*", "服务端口*"));

        return new ModelAndView("defaultListDataExcelView", modelMap);
    }
}
