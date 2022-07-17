package com.polovyi.ivan.tutorials.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import io.awspring.cloud.messaging.endpoint.NotificationStatusHandlerMethodArgumentResolver;
import io.awspring.cloud.messaging.endpoint.config.NotificationHandlerMethodArgumentResolverFactoryBean;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class AWSConfig implements WebMvcConfigurer {

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.access-key}")
    private String awsAccessKey;

    @Value("${aws.secret-key}")
    private String awsSecretKey;

    @Value("${aws.sns-endpoint}")
    private String snsEndpoint;

    @Bean
    public AWSCredentials credentials() {
        return new BasicAWSCredentials(awsAccessKey, awsSecretKey);
    }

    @Bean
    public AmazonSNS amazonSNS() {
        return AmazonSNSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials()))
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(snsEndpoint, awsRegion))
                .build();
    }

    @Bean
    public NotificationStatusHandlerMethodArgumentResolver notificationStatusHandlerMethodArgumentResolver() {
        return new NotificationStatusHandlerMethodArgumentResolver(amazonSNS());
    }

    @Bean
    public NotificationHandlerMethodArgumentResolverFactoryBean notificationHandlerMethodArgumentResolverFactoryBean() {
        return new NotificationHandlerMethodArgumentResolverFactoryBean(amazonSNS());
    }

    @Override
    @SneakyThrows
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(notificationStatusHandlerMethodArgumentResolver());
        resolvers.add(notificationHandlerMethodArgumentResolverFactoryBean().getObject());
    }
}
