package taskaya.backend.services.client;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import taskaya.backend.DTO.clients.ClientPostedJobsResponseDTO;
import taskaya.backend.DTO.clients.ClientProfileResponseDTO;
import taskaya.backend.DTO.clients.ClientWorkDoneResponseDTO;
import taskaya.backend.DTO.freelancers.requests.DescriptionPatchRequestDTO;
import taskaya.backend.DTO.freelancers.requests.SkillsUpdateRequestDTO;
import taskaya.backend.DTO.mappers.ClientPostedJobsResponseMapper;
import taskaya.backend.DTO.mappers.ClientProfileResponseMapper;
import taskaya.backend.DTO.mappers.ClientWorkDoneResponseMapper;
import taskaya.backend.config.Constants;
import taskaya.backend.config.security.JwtService;
import taskaya.backend.entity.User;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.client.ClientBalance;
import taskaya.backend.entity.client.ClientBusiness;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.Job;
import taskaya.backend.exceptions.notFound.NotFoundException;
import taskaya.backend.repository.client.ClientRepository;
import taskaya.backend.repository.work.JobRepository;
import taskaya.backend.services.CloudinaryService;

import java.io.IOException;
import java.util.*;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private ClientPostedJobsResponseMapper clientPostedJobsResponseMapper;

    @Autowired
    CloudinaryService cloudinaryService;


    @Transactional
    public Client createClient(User user){
        user.setRole(User.Role.CLIENT);
        Client client = Client.builder()
                .id(user.getId())
                .user(user)
                .name(user.getUsername())
                .balance(new ClientBalance())
                .clientBusiness(new ClientBusiness())
                .profilePicture(Constants.FIRST_PROFILE_PICTURE)
                .build();

        clientRepository.save(client);
        return client;

    }

    public Client getClientFromJWT(){
        User user = jwtService.getUserFromToken();
        return clientRepository.findByUser(user).orElseThrow(()-> new NotFoundException("client not found"));
    }


    public ClientProfileResponseDTO getClientProfile(String id) {
        if(Objects.equals(id, "my-profile")){
            Client client = getClientFromJWT();
            return ClientProfileResponseMapper.toDTO(client);
        }else {
            Client client = clientRepository.findById(UUID.fromString(id))
                    .orElseThrow(()-> new NotFoundException("client not found"));
            return ClientProfileResponseMapper.toDTO(client);
        }
    }

    public Page<ClientWorkDoneResponseDTO> getClientWorkDone(String id, int page, int size) {
        List<ClientWorkDoneResponseDTO> listDTO = new ArrayList<>();

        //get client
        Client client = clientRepository.findById(UUID.fromString(id))
                .orElseThrow(()-> new NotFoundException("client not found"));

        List<Job> jobs = jobRepository.findByClientAndStatus(client, Job.JobStatus.DONE);
        jobs.sort(Comparator.comparing(Job::getEndedAt).reversed());

        //map to DTO list
        listDTO = ClientWorkDoneResponseMapper.toDTOList(jobs);

        //List to Page
        Pageable pageable = PageRequest.of(page, size);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), listDTO.size());

        List<ClientWorkDoneResponseDTO> paginatedList = listDTO.subList(start, end);

        return new PageImpl<>(paginatedList, pageable, listDTO.size());
    }
    public Page<ClientPostedJobsResponseDTO> getPostedJobs(int page, int size, String search) {

        List<ClientPostedJobsResponseDTO> DTOs = new ArrayList<>();

        Client client = getClientFromJWT();
        List<Job> jobs;

        if (search == null || search.isEmpty()) {
            jobs = jobRepository.findAllByClient(client);
        } else {
            jobs = jobRepository.findByClientAndTitleContainingIgnoreCase(client, search);
        }
        jobs.sort(Comparator.comparing(Job::getTitle).reversed());

        //map to DTO list
        DTOs = clientPostedJobsResponseMapper.toDTOList(jobs);

        //List to Page
        Pageable pageable = PageRequest.of(page, size);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), DTOs.size());

        List<ClientPostedJobsResponseDTO> paginatedList = DTOs.subList(start, end);

        return new PageImpl<>(paginatedList, pageable, DTOs.size());
    }

    public void updateSkills(SkillsUpdateRequestDTO skills) {
        Client client = getClientFromJWT();
        client.setSkills(new HashSet<>(skills.getSkills()));
        clientRepository.save(client);
    }
    @Transactional
    public void updateDesc(DescriptionPatchRequestDTO request) {
        Client client = getClientFromJWT();
        client.setDescription(request.getDescription());
        clientRepository.save(client);
    }

    @Transactional
    public void updateProfilePicture(MultipartFile updatedPicture) throws IOException {
        //get client from token
        Client client = getClientFromJWT();

        List<String> allowedMimeTypes = List.of("image/jpeg", "image/png", "image/gif", "image/webp");
        // Validate file type
        if (updatedPicture.isEmpty() || !allowedMimeTypes.contains(updatedPicture.getContentType())) {
            throw new IllegalArgumentException("Invalid file type. Only JPEG, PNG, GIF, and WEBP images are allowed.");
        }

        //delete old picture
        String currentPicture = client.getProfilePicture();
        if (!(currentPicture.equals(Constants.FIRST_PROFILE_PICTURE)))
            cloudinaryService.deleteFile(currentPicture);

        //store new picture
        String pictureURL = cloudinaryService.uploadFile(updatedPicture, "clients_profile_pictures");
        client.setProfilePicture(pictureURL);
        clientRepository.save(client);
    }

}
