package taskaya.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taskaya.backend.DTO.login.NameAndPictureResponseDTO;
import taskaya.backend.DTO.payments.requests.PaymentSearchRequestDTO;
import taskaya.backend.config.security.JwtService;
import taskaya.backend.services.PaymentService;
import taskaya.backend.services.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    JwtService jwtService;

    @GetMapping("/name-and-picture")
    public ResponseEntity<NameAndPictureResponseDTO> getNameAndPicture() {
        return ResponseEntity.ok(userService.nameAndPicture());
    }

    @PostMapping("/finance/transactions")
    public ResponseEntity<?> getTransactions(@RequestBody PaymentSearchRequestDTO requestDTO) {

        return ResponseEntity.ok(
                paymentService.findPayments(
                        jwtService.getUserFromToken(),
                        requestDTO.getStartDate(),
                        requestDTO.getEndDate(),
                        requestDTO.getType(),
                        requestDTO.getPage(),
                        requestDTO.getSize()
                )
        );
    }

}
