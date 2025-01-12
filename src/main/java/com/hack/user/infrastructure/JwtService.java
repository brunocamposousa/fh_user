package com.hack.user.infrastructure;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.TimeUnit;

@Service
public class JwtService {

    // URL base para JWKS do Cognito
    private String getJwksUrl(String region, String userPoolId) {
        return String.format("https://cognito-idp.%s.amazonaws.com/%s/.well-known/jwks.json", region, userPoolId);
    }

    // Decodifica e verifica o JWT
    public DecodedJWT decodeAndVerifyJWT(String token, String region, String userPoolId) throws Exception {
        try {
            // Extrai o "kid" do header do token para identificar a chave correta
            DecodedJWT decodedJWT = JWT.decode(token);
            String kid = decodedJWT.getKeyId();

            // Configura o JwkProvider com base na URL do JWKS
            String jwksUrl = getJwksUrl(region, userPoolId);
            JwkProvider jwkProvider = new JwkProviderBuilder(new URI(jwksUrl).toURL())
                    .cached(10, 24, TimeUnit.HOURS) // Cache para melhorar desempenho
                    .rateLimited(10, 1, TimeUnit.MINUTES) // Limite de chamadas para evitar bloqueio
                    .build();

            // Busca o JWK correspondente pelo "kid"
            Jwk jwk = jwkProvider.get(kid);

            // Obtém a chave pública
            RSAPublicKey publicKey = (RSAPublicKey) jwk.getPublicKey();

            // Configura o algoritmo de verificação usando a chave pública
            JWTVerifier verifier = JWT.require(Algorithm.RSA256(publicKey, null))
                    .withIssuer(String.format("https://cognito-idp.%s.amazonaws.com/%s", region, userPoolId)) // Valida o issuer
                    .build();

            // Verifica o token e retorna o JWT decodificado
            return verifier.verify(token);

        } catch (JwkException e) {
            throw new Exception("Erro ao buscar JWK: " + e.getMessage(), e);
        } catch (JWTVerificationException e) {
            throw new Exception("Erro ao verificar JWT: " + e.getMessage(), e);
        }
    }
}