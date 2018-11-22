package com.leadingsoft.bizfuse.quartz;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.leadingsoft.bizfuse.quartz.core.conf.QuartzConfigurationAdapter;

/**
 * Quartz自动配置
 *
 * @author liuyg
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class BizfuseQuartzConfiguration extends QuartzConfigurationAdapter {

    @Value("${spring.datasource.driver-class-name}")
    private String dataSourceDriver;

    /**
     * 数据库建表SQL文件路径
     *
     * @return
     */
    @Override
    protected String getSqlScriptPath() {
        if (this.dataSourceDriver.equals("org.h2.Driver")) {
            return "quartz/tables_h2.sql";
        }
        if (this.dataSourceDriver.equals("com.ibm.db2.jcc.DB2Driver")) {
            return "quartz/tables_db2.sql";
        }
        if (this.dataSourceDriver.equals("com.mysql.jdbc.Driver")) {
            return "quartz/tables_mysql_innodb.sql";
        }
        if (this.dataSourceDriver.equals("org.postgresql.Driver")) {
            return "quartz/tables_postgres.sql";
        }
        if (this.dataSourceDriver.contains("SQLServer")) {
            return "quartz/tables_sqlServer.sql";
        }
        return super.getSqlScriptPath();
    }
}
