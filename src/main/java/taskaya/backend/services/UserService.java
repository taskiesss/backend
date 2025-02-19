package taskaya.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.login.NameAndPictureResponseDTO;
import taskaya.backend.DTO.mappers.NameAndPictureResponseMapper;
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

    public NameAndPictureResponseDTO nameAndPicture(){
        String username = JwtService.getAuthenticatedUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new RuntimeException("Username not found!"));
        if(user.getRole() == User.Role.FREELANCER || user.getRole() == User.Role.ADMIN ){
            Freelancer freelancer = freelancerRepository.findByUser(user)
                    .orElseThrow(()->new RuntimeException("User not found!"));
            return NameAndPictureResponseMapper.toDTO(freelancer);
        } else if (user.getRole() == User.Role.CLIENT) {
            Client client = clientRepository.findByUser(user)
                    .orElseThrow(()->new RuntimeException("User not found!"));
            return NameAndPictureResponseMapper.toDTO(client);
        }
        else {
            throw new RuntimeException("Role undefined.");
        }
    }
}
