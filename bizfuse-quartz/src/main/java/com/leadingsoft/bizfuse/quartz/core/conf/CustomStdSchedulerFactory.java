package com.leadingsoft.bizfuse.quartz.core.conf;

import java.util.Properties;

import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class CustomStdSchedulerFactory extends StdSchedulerFactory {

    @Override
    public void initialize(final Properties props) throws SchedulerException {
        props.put(StdSchedulerFactory.PROP_JOB_STORE_CLASS, CustomDataSourceJobStore.class.getName());
        super.initialize(props);
    }
}
