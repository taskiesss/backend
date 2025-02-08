package taskaya.backend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import taskaya.backend.DTO.login.AuthenticationResponseDTO;
import taskaya.backend.DTO.login.LoginDTO;
import taskaya.backend.services.LogInService;

@RestController
@RequestMapping("/login")
public class LogInController {

    @Autowired
    LogInService logInService;

    @PostMapping
    public ResponseEntity<AuthenticationResponseDTO> login(@RequestBody LoginDTO requestDTO){
        return ResponseEntity.ok(logInService.login(requestDTO));
    }
}
