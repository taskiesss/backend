package taskaya.backend.services.client;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import taskaya.backend.DTO.clients.ClientPostedJobsResponseDTO;
import taskaya.backend.DTO.clients.ClientProfileResponseDTO;
import taskaya.backend.DTO.clients.ClientWorkDoneResponseDTO;
import taskaya.backend.DTO.mappers.ClientPostedJobsResponseMapper;
import taskaya.backend.DTO.mappers.ClientProfileResponseMapper;
import taskaya.backend.DTO.mappers.ClientWorkDoneResponseMapper;
import taskaya.backend.config.Constants;
import taskaya.backend.config.security.JwtService;
import taskaya.backend.entity.User;
import taskaya.backend.entity.client.Client;
import taskaya.backend.entity.client.ClientBalance;
import taskaya.backend.entity.client.ClientBusiness;
import taskaya.backend.entity.work.Job;
import taskaya.backend.exceptions.notFound.NotFoundException;
import taskaya.backend.repository.client.ClientRepository;
import taskaya.backend.repository.work.JobRepository;

import java.util.*;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private JobRepository jobRepository;

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
            jobs.sort(Comparator.comparing(Job::getTitle).reversed());

            //map to DTO list
            DTOs = ClientPostedJobsResponseMapper.toDTOList(jobs);

            //List to Page
            Pageable pageable = PageRequest.of(page, size);

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), DTOs.size());

            List<ClientPostedJobsResponseDTO> paginatedList = DTOs.subList(start, end);

            return new PageImpl<>(paginatedList, pageable, DTOs.size());

        } else {
            jobs = jobRepository.findByClientAndTitleContainingIgnoreCase(client, search);
            jobs.sort(Comparator.comparing(Job::getTitle).reversed());

            //map to DTO list
            DTOs = ClientPostedJobsResponseMapper.toDTOList(jobs);

            //List to Page
            Pageable pageable = PageRequest.of(page, size);

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), DTOs.size());

            List<ClientPostedJobsResponseDTO> paginatedList = DTOs.subList(start, end);

            return new PageImpl<>(paginatedList, pageable, DTOs.size());
        }

    }
}
