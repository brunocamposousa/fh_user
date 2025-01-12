package com.hack.user.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.hack.user.infrastructure.JwtService;
import com.hack.user.infrastructure.AwsConfig;

import java.util.HashMap;
import java.util.Map;

@Service
public class JwtValidationService  {
    @Autowired
    private JwtService jwtService; 

    @Autowired
    private AwsConfig awsConfig;    

    // Método para decodificar e verificar o JWT, e retornar email em caso de sucesso
    public Map<String, String> validateToken(String token) {
        Map<String, String> response = new HashMap<>();
        String region = awsConfig.getRegion();  
        String userPoolId = awsConfig.getUserPoolId();  

        try {
            // Chama o JwtService para validar o token e obter o decodedJWT
            DecodedJWT decodedJWT = jwtService.decodeAndVerifyJWT(token, region, userPoolId);

            // Extraímos o email do token validado
            String email = decodedJWT.getClaim("email").asString(); 
            
            response.put("message", "Token válido!");
            response.put("email", email);
            
            return response;
        } catch (Exception e) {
            response.put("message", "Token inválido ou expirado.");
            return response;
        }
    }    
}
