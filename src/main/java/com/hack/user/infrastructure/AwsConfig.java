package com.hack.user.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.regions.Region;

@Configuration 
public class AwsConfig {

    @Value("${aws.cognito.userPoolId}")
    private String userPoolId;

    @Value("${aws.cognito.appClientId}")
    private String appClientId;    

    @Value("${aws.cognito.appClientSecret}")
    private String appClientSecret;    

    @Value("${aws.region}")
    private String region;

    public String getUserPoolId() {
        return userPoolId;
    }

    public String getAppClientId() {
        return appClientId;
    }    

    public String getAppClientSecret() {
        return appClientSecret;
    }    

    public String getRegion() {
        return region;
    }

    // Bean para CognitoIdentityProviderClient
    @Bean
    public CognitoIdentityProviderClient cognitoIdentityProviderClient() {
        return CognitoIdentityProviderClient.builder()
                .region(Region.of(region))  // Usando a região configurada no application.properties
                .build();
    }

    // Bean para SnsClient
    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder()
                .region(Region.of(region))  // Usando a mesma região
                .build();
    }
}
