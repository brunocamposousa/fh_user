package com.hack.user.infrastructure;

import com.hack.user.domain.User;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

@Service
public class AwsCognitoService {
    private final CognitoIdentityProviderClient cognitoClient;
    private final String userPoolId;

    public AwsCognitoService(AwsConfig awsConfig, CognitoIdentityProviderClient cognitoClient) {
        this.cognitoClient = cognitoClient;
        this.userPoolId = awsConfig.getUserPoolId();
    }

    public String registerUser(User user) {
        AdminCreateUserRequest request = AdminCreateUserRequest.builder()
                .userPoolId(userPoolId)
                .username(user.getEmail())
                .temporaryPassword(user.getPassword())
                .userAttributes(
                        software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType.builder()
                                .name("email").value(user.getEmail()).build(),
                        software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType.builder()
                                .name("email_verified").value("true").build()
                )
                .build();

        AdminCreateUserResponse response = cognitoClient.adminCreateUser(request);
        System.out.println("Usuário Cognito criado: " + response.user().username());
        return response.user().username();
    }
}