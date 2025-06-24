package taskaya.backend.commandlinerunner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import taskaya.backend.entity.Skill;
import taskaya.backend.entity.User;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.client.ClientBalance;
import taskaya.backend.entity.freelancer.FreelancerBalance;
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

        client1.setProfilePicture("https://res.cloudinary.com/dhfb7i5h1/image/upload/v1750775363/clients_profile_pictures/62348223-1515-4bfa-b55f-a5eb7ecb3122IMG_7672.png");
        client1.setDescription("Hello! I’m a passionate and organized client who truly values the power of skilled freelancers. I regularly post projects in areas like web and mobile development, UI/UX design, content writing, marketing, and business support — and I’m always on the lookout for creative professionals who are committed to delivering high-quality work.\n" +
                "\n" +
                "As someone who has worked with both individuals and teams, I understand how important it is to create a positive, respectful, and productive working environment. You can expect timely communication, clear project scopes, and constructive feedback throughout the process. I believe in treating freelancers as true partners, not just service providers.\n" +
                "\n" +
                "Here’s what you can expect when working with me:\n" +
                "\n" +
                "✅ Well-defined project goals\n" +
                "✅ Fair budgets and on-time payments\n" +
                "✅ Open, friendly, and professional communication\n" +
                "✅ Flexibility and understanding when needed\n" +
                "✅ Long-term collaboration opportunities for outstanding freelancers\n" +
                "\n" +
                "I’m especially drawn to freelancers who are proactive, detail-oriented, and passionate about what they do. If you love solving problems and taking pride in your work, I’d be happy to connect and build something amazing together.\n");
        ClientBalance clientBalance1 = client1.getBalance();
        clientBalance1.setAvailable(100000.0);
        clientBalance1.setRestricted(7000000.0);

        clientRepository.save(client1);


        client2.setSkills(new HashSet<>(skills2));
        client2.setRate(2);

        ClientBalance clientBalance2 = client2.getBalance();
        clientBalance2.setAvailable(200000.0);
        clientBalance2.setRestricted(1000000.0);

        clientRepository.save(client2);

        client3.setSkills(new HashSet<>(skills3));
        client3.setRate(3.5F);

        ClientBalance clientBalance3 = client3.getBalance();
        clientBalance3.setAvailable(100000.0);
        clientBalance3.setRestricted(700000.0);

        clientRepository.save(client3);
        System.out.println("Client 3 's id :" + client3.getId());
        System.out.println("Client 2 's id :" + client2.getId());
        System.out.println("Client 1 's id :" + client1.getId());

    }
}
