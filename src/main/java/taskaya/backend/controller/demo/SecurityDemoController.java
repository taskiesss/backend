package taskaya.backend.controller.demo;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import taskaya.backend.DTO.login.AuthenticationRequestDTO;
import taskaya.backend.DTO.login.AuthenticationResponseDTO;
import taskaya.backend.config.security.JwtService;
import taskaya.backend.entity.User;
import taskaya.backend.repository.UserRepository;

@RestController
    @RequestMapping("/api/demo/security")
public class SecurityDemoController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtService jwtService;


    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticate (
            @RequestBody AuthenticationRequestDTO authenticationRequestDTO
    ){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequestDTO.getUsername(),
                        authenticationRequestDTO.getPassword()
                )
        );

        User user = userRepository.findByUsername(authenticationRequestDTO.getUsername()).
                orElseThrow(()->new UsernameNotFoundException("username not found "));

        String token = jwtService.generateToken(user);

        return  ResponseEntity.ok(
                AuthenticationResponseDTO.builder()
                        .token(token)
                        .build()
        );


    }



    @GetMapping("print-hello")
    public ResponseEntity<AuthenticationResponseDTO> printhello(){
        return ResponseEntity.ok(
                AuthenticationResponseDTO.builder()
                        .token("hello world")
                        .build()
        );
    }

}
