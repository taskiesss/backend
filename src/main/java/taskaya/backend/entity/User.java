package taskaya.backend.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true ,length = 30)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;


    @Column(name = "new_notification", nullable = false)
    @Builder.Default
    private Integer newNotifications = 0;

    // UserDetails methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" +role.name()));
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Customize logic if needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Customize logic if needed
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Customize logic if needed
    }

    @Override
    public boolean isEnabled() {
        return true; // Customize logic if needed
    }


    public enum Role {
        ADMIN, FREELANCER, CLIENT
    }
}