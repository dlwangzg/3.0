package com.leadingsoft.bizfuse.quartz.core.managment;

import java.util.HashMap;
import java.util.Map;

import com.leadingsoft.bizfuse.quartz.core.managment.ClusteredJobsManager.JobInfo;

public class JobMappingHolder {

    private static final Map<String, JobInfo> jobIdInfoMapping = new HashMap<String, JobInfo>();

    public static Map<String, JobInfo> getJobMapping() {
        return JobMappingHolder.jobIdInfoMapping;
    }

    public static boolean containsJob(final String key) {
        return JobMappingHolder.jobIdInfoMapping.containsKey(key);
    }
}
