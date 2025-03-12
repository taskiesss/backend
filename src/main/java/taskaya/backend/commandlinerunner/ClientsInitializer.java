package taskaya.backend.commandlinerunner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.User;
import taskaya.backend.entity.client.Client;
import taskaya.backend.repository.SkillRepository;
import taskaya.backend.repository.client.ClientRepository;
import taskaya.backend.services.client.ClientService;

import java.util.HashSet;
import java.util.List;

@Component
public class ClientsInitializer {
    @Autowired
    SkillRepository skillRepository;

    @Autowired
    ClientService clientService;

    @Autowired
    ClientRepository clientRepository;

    public void clientSeed() {

        User clientUser1 = User.builder()
                .email("2000966@eng.asu.edu.eg")
                .role(User.Role.CLIENT)
                .username("client01")
                .password(new BCryptPasswordEncoder().encode("client1@123"))
                .build();

        List<String> skillNames1 = List.of("Java", "Spring Boot", "Spring Security");
        List<Skill> skills1 = skillRepository.findByNameIn(skillNames1);

        User clientUser2 = User.builder()
                .email("2001480@eng.asu.edu.eg")
                .role(User.Role.CLIENT)
                .username("client02")
                .password(new BCryptPasswordEncoder().encode("client2@123"))
                .build();

        List<String> skillNames2 = List.of("AWS", "Google Cloud");
        List<Skill> skills2 = skillRepository.findByNameIn(skillNames2);

        User clientUser3 = User.builder()
                .email("2001024@gmail.com")
                .role(User.Role.CLIENT)
                .username("client03")
                .password(new BCryptPasswordEncoder().encode("client3@123"))
                .build();

        List<String> skillNames3 = List.of("Machine Learning", "Natural Language Processing (NLP)","kjhgfd");
        List<Skill> skills3 = skillRepository.findByNameIn(skillNames3);



        Client client1 = clientService.createClient(clientUser1);
        Client client2 = clientService.createClient(clientUser2);
        Client client3 = clientService.createClient(clientUser3);

        client1.setSkills(new HashSet<>(skills1));
        client1.setRate(3);
        clientRepository.save(client1);


        client2.setSkills(new HashSet<>(skills2));
        client2.setRate(2);
        clientRepository.save(client2);

        client3.setSkills(new HashSet<>(skills3));
        client3.setRate(3.5F);
        clientRepository.save(client3);

    }
}
