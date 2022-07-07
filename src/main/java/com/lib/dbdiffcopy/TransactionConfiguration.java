package com.lib.dbdiffcopy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * Configuration Example
 */

//@EnableTransactionManagement
//@Configuration
public class TransactionConfiguration {

//    @Bean(name = "sourceTransactionManager")
//    public PlatformTransactionManager sourceTransactionManager() {
//        return new DataSourceTransactionManager();
//    }
//
//
//    @Bean(name = "destinationTransactionManager")
//    public PlatformTransactionManager destinationTransactionManager() {
//        return new DataSourceTransactionManager();
//    }
}
