package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AliOssUtilConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AliOssUtil AliOss(AliOssProperties properties){
        log.info("构建AliOssUtil类....");
        return new AliOssUtil(properties.getEndpoint(),properties.getAccessKeyId(), properties.getAccessKeySecret(),properties.getBucketName());
    }
}
