package com.leadingsoft.bizfuse.quartz.controller;

import java.util.List;

import javax.annotation.Resource;

import org.quartz.JobKey;
import org.quartz.Trigger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.leadingsoft.bizfuse.quartz.bean.TriggerBean;
import com.leadingsoft.bizfuse.quartz.bean.TriggerSearchBean;
import com.leadingsoft.bizfuse.quartz.convertor.JobConvertor;
import com.leadingsoft.bizfuse.quartz.convertor.TriggerConvertor;
import com.leadingsoft.bizfuse.quartz.dto.JobDetailDTO;
import com.leadingsoft.bizfuse.quartz.dto.ResultDTO;
import com.leadingsoft.bizfuse.quartz.dto.TriggerDTO;
import com.leadingsoft.bizfuse.quartz.service.JobManagerService;

@RestController
@RequestMapping("/api")
public class JobsManagementController {

    @Resource
    private JobManagerService jobManagerService;
    @Resource
    private JobConvertor jobConvertor;
    @Resource
    private TriggerConvertor triggerConvertor;

    @RequestMapping(value = "/jobs", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<List<JobDetailDTO>> getJobs(
            @RequestParam(required = false) final String search,
            final Pageable pageable) {
        final Page<JobKey> pageModels = this.jobManagerService.getJobs(search, pageable);
        return this.jobConvertor.toResultDTO(pageModels);
    }

    @RequestMapping(value = "/jobs/{jobName}/pause", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<Void> pauseJob(@PathVariable final String jobName) {
        this.jobManagerService.pauseJob(jobName);
        return ResultDTO.success();
    }

    @RequestMapping(value = "/jobs/{jobName}/resume", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<Void> resumeJob(@PathVariable final String jobName) {
        this.jobManagerService.resumeJob(jobName);
        return ResultDTO.success();
    }

    @RequestMapping(value = "/jobs/{jobName}/destroy", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<Void> detroy(@PathVariable final String jobName) {
        this.jobManagerService.deleteJob(jobName);
        return ResultDTO.success();
    }

    @RequestMapping(value = "/jobs/{jobName}/triggers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<List<TriggerDTO>> getTraggers(@PathVariable final String jobName,
            @RequestParam(required = false) final String name,
            @RequestParam(required = false) final String state,
            final Pageable pageable) {
        final TriggerSearchBean search = new TriggerSearchBean();
        search.setJobName(jobName);
        search.setTriggerName(name);
        search.setTriggerState(state);
        final Page<Trigger> pageModels =
                this.jobManagerService.getTriggers(search, pageable);
        return this.triggerConvertor.toResultDTO(pageModels);
    }

    @RequestMapping(value = "/triggers/{triggerName}/pause", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<Void> pauseTrigger(@PathVariable final String triggerName) {
        this.jobManagerService.pauseTrigger(triggerName);
        return ResultDTO.success();
    }

    @RequestMapping(value = "/triggers/{triggerName}/resume", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<Void> resumeTrigger(@PathVariable final String triggerName) {
        this.jobManagerService.resumeTrigger(triggerName);
        return ResultDTO.success();
    }

    @RequestMapping(value = "/triggers", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<Void> updateTrigger(@RequestBody final TriggerBean triggerBean) {
        this.jobManagerService.updateTrigger(triggerBean);
        return ResultDTO.success();
    }

    @RequestMapping(value = "/triggers", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<Void> addTragger(@RequestBody final TriggerBean triggerBean) {
        this.jobManagerService.addTrigger(triggerBean);
        return ResultDTO.success();
    }

    @RequestMapping(value = "/triggers/{triggerName}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResultDTO<Void> destroyTrigger(@PathVariable final String triggerName) {
        this.jobManagerService.deleteTrigger(triggerName);
        return ResultDTO.success();
    }
}
