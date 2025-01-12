package com.hack.user.application;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hack.user.infrastructure.AwsConfig;
import com.hack.user.infrastructure.SecretHashUtil; 

import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

@Service
public class LoginUserService {

    @Autowired
    private CognitoIdentityProviderClient cognitoClient;

    @Autowired
    private AwsConfig awsConfig;

    // Método para autenticar e gerar token
    public AuthenticationResultType authenticateUser(String email, String password) throws Exception {
        try {
            String userPoolId = awsConfig.getUserPoolId();  
            String clientId = awsConfig.getAppClientId();  
            String clientSecret = awsConfig.getAppClientSecret(); 

            // Gerando o SECRET_HASH 
            String secretHash = SecretHashUtil.generateSecretHash(email, clientId, clientSecret);

            // Autenticação do usuário com Cognito
            AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                    .userPoolId(userPoolId)
                    .clientId(clientId) 
                    .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                    .authParameters(Map.of(
                            "USERNAME", email,
                            "PASSWORD", password,
                            "SECRET_HASH", secretHash 
                    ))
                    .build();

            AdminInitiateAuthResponse authResponse = cognitoClient.adminInitiateAuth(authRequest);
            return authResponse.authenticationResult(); // Retorna o AuthenticationResult com tokens
        } catch (Exception e) {
            throw new Exception("Falha ao autenticar o usuário: " + e.getMessage(), e);
        }
    }    
}
