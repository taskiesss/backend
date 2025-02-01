package taskaya.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.login.AuthenticationRequestDTO;
import taskaya.backend.DTO.login.AuthenticationResponseDTO;
import taskaya.backend.DTO.login.LoginDTO;
import taskaya.backend.config.security.JwtService;
import taskaya.backend.entity.User;
import taskaya.backend.exceptions.login.EmailNotFoundException;
import taskaya.backend.exceptions.login.WrongPasswordException;
import taskaya.backend.repository.UserRepository;

@Service
public class LogInService {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtService jwtService;

    @Autowired
    PasswordEncoder passwordEncoder;


    public AuthenticationResponseDTO login (LoginDTO requestDTO){
        //fetch el user
        User user  = userRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(()->new EmailNotFoundException("mail is not found"));

        //validate password
        if(passwordEncoder.matches(requestDTO.getPassword(),user.getPassword())){

            //generate token
            return authenticate(
                AuthenticationRequestDTO.builder()
                        .username(user.getUsername())
                        .password(requestDTO.getPassword())
                        .build()
            );

        }else
            throw new WrongPasswordException("wrong password");

    }


    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO requestDTO) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDTO.getUsername(),
                        requestDTO.getPassword()
                )
        );

        User user = userRepository.findByUsername(requestDTO.getUsername()).
                orElseThrow(() -> new UsernameNotFoundException("username not found "));

        String token = jwtService.generateToken(user);

        return AuthenticationResponseDTO.builder()
                .token(token)
                .build();
    }
}

