package taskaya.backend.config.security;

import io.jsonwebtoken.Claims;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import taskaya.backend.entity.User;
import taskaya.backend.entity.community.Community;
import taskaya.backend.entity.community.CommunityMember;
import taskaya.backend.entity.freelancer.Freelancer;
import taskaya.backend.entity.work.Contract;
import taskaya.backend.entity.work.Proposal;
import taskaya.backend.entity.work.WorkerEntity;
import taskaya.backend.repository.UserRepository;
import taskaya.backend.repository.client.ClientRepository;
import taskaya.backend.repository.community.CommunityMemberRepository;
import taskaya.backend.repository.community.CommunityRepository;
import taskaya.backend.repository.freelancer.FreelancerRepository;
import taskaya.backend.repository.work.ContractRepository;
import taskaya.backend.repository.work.ProposalRepository;
import taskaya.backend.services.community.CommunityMemberService;
import taskaya.backend.services.freelancer.FreelancerService;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String SECRET_KEY = "a7b3c9e1d5f2803b4a65791208ef567b8c901234defa5b67c8d90123456789ab";

    @Autowired
    CommunityRepository communityRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    FreelancerRepository freelancerRepository;

    @Autowired
    CommunityMemberRepository communityMemberRepository;

    @Autowired
    ContractRepository contractRepository;

    @Autowired
    ProposalRepository proposalRepository;

    public String extractUsername(String token) {
        return extractClaim(token , Claims-> Claims.getSubject()) ;
    }



    public <T> T extractClaim(String token , Function<Claims,T> claimsResolver){
        final Claims claims =extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken (
            Map<String , Object> extraClaims,       //ay haga 3aizin nezawedha
            UserDetails userDetails
    ){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+ 1000*60*30))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails);
    }

    public boolean isTokenValid(String token , UserDetails userDetails){
        final String username =extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    private Claims extractAllClaims (String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static String getAuthenticatedUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    public boolean isCommunityAdmin(String communityId) {
        return communityRepository.isAdmin(UUID.fromString(communityId) , getUserFromToken().getId());
    }

    public User getUserFromToken(){
        return userRepository.findByUsername(getAuthenticatedUsername())
                .orElseThrow(()->new AccessDeniedException("security failed"));
    }

    public boolean isCommunityMember(String communityId){
        return communityMemberRepository.isMember(UUID.fromString(communityId) , getUserFromToken().getId());
    }

    public boolean isNotCommunityMember(String communityId){
        return !communityMemberRepository.isMember(UUID.fromString(communityId) , getUserFromToken().getId());
    }

    public boolean isClient(){
        return getUserFromToken().getRole() == User.Role.CLIENT;
    }

    public boolean isFreelancer(){
        return getUserFromToken().getRole() == User.Role.FREELANCER;
    }

    public boolean contractDetailsAuth(String contractId){
        if(isClient()){
            return isClientContractOwner(contractId);
        }else{
            Contract contract = contractRepository.findById(UUID.fromString(contractId)).orElseThrow();
            User user = getUserFromToken();
            WorkerEntity contractWorkerEntity = contract.getWorkerEntity();
            if(contractWorkerEntity.getType() == WorkerEntity.WorkerType.FREELANCER){
                Freelancer freelancer = freelancerRepository.findByUser(user)
                        .orElseThrow(()->new AccessDeniedException("security failed"));
                return contractWorkerEntity.getId().equals(freelancer.getWorkerEntity().getId());
            }
            else{
                Community community = communityRepository.findByWorkerEntity(contractWorkerEntity)
                        .orElseThrow(()->new AccessDeniedException("security failed"));
                return isCommunityMember(community.getUuid().toString());
            }
        }
    }
    public boolean ProposalDetailsAuth(String proposalId){

        if(isClient()){
            return isClientProposalOwner(proposalId);
        }else{
            Proposal proposal = proposalRepository.findById(UUID.fromString(proposalId)).orElseThrow();

            User user = getUserFromToken();
            WorkerEntity proposalWorkerEntity = proposal.getWorkerEntity();
            if(proposalWorkerEntity.getType() == WorkerEntity.WorkerType.FREELANCER){
                Freelancer freelancer = freelancerRepository.findByUser(user)
                        .orElseThrow(()->new AccessDeniedException("security failed"));
                return proposalWorkerEntity.getId().equals(freelancer.getWorkerEntity().getId());
            }
            else{
                Community community = communityRepository.findByWorkerEntity(proposalWorkerEntity)
                        .orElseThrow(()->new AccessDeniedException("security failed"));
                return isCommunityMember(community.getUuid().toString());
            }
        }
    }

    public boolean isClientContractOwner(String contractId){
        Contract contract = contractRepository.findById(UUID.fromString(contractId)).orElseThrow();
            UUID clientId = clientRepository.findByUser(getUserFromToken())
                    .orElseThrow(() -> new AccessDeniedException("security failed")).getId();
            return clientId.equals(contract.getClient().getId());
    }

    public boolean isClientProposalOwner(String proposalId){
        Proposal proposal = proposalRepository.findById(UUID.fromString(proposalId)).orElseThrow();
        UUID clientId = clientRepository.findByUser(getUserFromToken())
                .orElseThrow(() -> new AccessDeniedException("security failed")).getId();
        return clientId.equals(proposal.getClient().getId());
    }


    public boolean fileSubmissionAuth(String contractId){
        User user = getUserFromToken();
        Contract contract = contractRepository.findById(UUID.fromString(contractId)).orElseThrow();

        WorkerEntity contractWorkerEntity = contract.getWorkerEntity();
        if(contractWorkerEntity.getType() == WorkerEntity.WorkerType.FREELANCER){
            Freelancer freelancer = freelancerRepository.findByUser(user)
                    .orElseThrow(()->new AccessDeniedException("security failed"));
            return contractWorkerEntity.getId().equals(freelancer.getWorkerEntity().getId());
        }
        else{
            Community community = communityRepository.findByWorkerEntity(contractWorkerEntity)
                    .orElseThrow(()->new AccessDeniedException("security failed"));
            return isCommunityMember(community.getUuid().toString());
        }
    }

    public  boolean isCommunityAdminOrFreelancerForContract(String contractId){
        User user = getUserFromToken();
        Contract contract = contractRepository.findById(UUID.fromString(contractId)).orElseThrow();
        WorkerEntity contractWorkerEntity = contract.getWorkerEntity();

        if (contractWorkerEntity.getType() == WorkerEntity.WorkerType.FREELANCER){
            Freelancer freelancer = freelancerRepository.findByUser(user)
                    .orElseThrow(()->new AccessDeniedException("security failed"));
            return contractWorkerEntity.getId().equals(freelancer.getWorkerEntity().getId());
        }else {
            Community community = communityRepository.findByWorkerEntity(contractWorkerEntity)
                    .orElseThrow(()->new AccessDeniedException("security failed"));
            return isCommunityAdmin(community.getUuid().toString());
        }

    }

}
