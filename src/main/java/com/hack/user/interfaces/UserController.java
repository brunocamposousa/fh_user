package com.hack.user.interfaces;

import com.hack.user.application.RegisterUserService;

import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;

import com.hack.user.application.JwtValidationService;
import com.hack.user.application.LoginUserService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private RegisterUserService registerUserService;

    @Autowired
    private LoginUserService loginUserService;    

    @Autowired
    private JwtValidationService jwtValidationService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody UserDto userDto) {
        try {
            boolean success = registerUserService.registerUser(userDto);
            Map<String, String> response = new HashMap<>();
            
            if (success) {
                response.put("message", "Usuário registrado com sucesso!");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Erro ao registrar usuário");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (IllegalArgumentException e) {
            // Capturando erro de validação de email
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            // Capturando erros de chamadas ao Cognito ou SNS
            Map<String, String> response = new HashMap<>();
            response.put("message", "Erro interno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            // Chama o serviço para autenticar o usuário e obter os tokens
            AuthenticationResultType result = loginUserService.authenticateUser(email, password);
            
            // Adicionando um header específico
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Criando a resposta com os tokens
            Map<String, String> response = new HashMap<>();
            //response.put("AccessToken", result.accessToken());
            //response.put("IdToken", result.idToken());
            //response.put("RefreshToken", result.refreshToken());
            response.put("token", result.idToken());
            
            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    // Endpoint para validar o token e retornar o email
    @GetMapping("/validateToken")
    public ResponseEntity<Map<String, String>> validateToken(@RequestParam String token) {
        Map<String, String> response = jwtValidationService.validateToken(token);
        
        if (response.containsKey("email")) {
            // Se o email foi extraído com sucesso, retorna a resposta com o email
            return ResponseEntity.ok(response);
        } else {
            // Caso contrário, a resposta com erro será retornada
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }  
}
