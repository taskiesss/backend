package taskaya.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import taskaya.backend.DTO.login.NameAndPictureResponseDTO;
import taskaya.backend.services.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/name-and-picture")
    public ResponseEntity<NameAndPictureResponseDTO> getNameAndPicture() {
        return ResponseEntity.ok(userService.nameAndPicture());
    }
}
