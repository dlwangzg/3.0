package com.leadingsoft.bizfuse.base.filestorage;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableAutoConfiguration
@EnableMongoAuditing
@ComponentScan
public class BizfuseStorageConfiguration {
}
