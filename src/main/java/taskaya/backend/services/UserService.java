package taskaya.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.login.NameAndPictureNavBarResponseDTO;
import taskaya.backend.DTO.login.NameAndPictureResponseDTO;
import taskaya.backend.DTO.mappers.NameAndPictureNavBarResponseMapper;
import taskaya.backend.DTO.mappers.NameAndPictureResponseMapper;
import taskaya.backend.DTO.payments.responses.NameAndTotalBalanceMapper;
import taskaya.backend.DTO.payments.responses.NameAndTotalBalanceResponseDTO;
import taskaya.backend.config.security.JwtService;
import taskaya.backend.entity.User;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.repository.UserRepository;
import taskaya.backend.repository.client.ClientRepository;
import taskaya.backend.repository.freelancer.FreelancerRepository;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    FreelancerRepository freelancerRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    JwtService jwtService;
    public NameAndPictureNavBarResponseDTO nameAndPicture(){
        String username = JwtService.getAuthenticatedUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("Username not found!"));
        if(user.getRole() == User.Role.FREELANCER  ){
            Freelancer freelancer = freelancerRepository.findByUser(user)
                    .orElseThrow(()->new RuntimeException("User not found!"));
            return NameAndPictureNavBarResponseMapper.toDTO(freelancer, user.getNewNotifications());
        } else if (user.getRole() == User.Role.CLIENT) {
            Client client = clientRepository.findByUser(user)
                    .orElseThrow(()->new RuntimeException("User not found!"));
            return NameAndPictureNavBarResponseMapper.toDTO(client, user.getNewNotifications());
        }
        else {
            throw new RuntimeException("Role undefined.");
        }
    }


    public NameAndTotalBalanceResponseDTO getUserNameAndBalance(){
        User user = jwtService.getUserFromToken();
        if (user.getRole().equals(User.Role.FREELANCER)){
            return NameAndTotalBalanceMapper.toDTO(freelancerRepository.findByUser(user).get());
        }else {
            return NameAndTotalBalanceMapper.toDTO(clientRepository.findByUser(user).get());
        }
    }
}
