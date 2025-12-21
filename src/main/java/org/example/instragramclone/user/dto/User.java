package org.example.instragramclone.user.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import jakarta.persistence.*;
import org.example.instragramclone.entities.ForgotPassword;
import org.example.instragramclone.common.AuthProvider;
import org.example.instragramclone.common.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.example.instragramclone.common.AuthProvider.LOCAL;

@Data
@EqualsAndHashCode(exclude = {"forgotPassword", "follower", "following"})
@ToString(exclude = {"forgotPassword", "follower", "following"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "_users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true )
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true, name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, length = 20, columnDefinition = "varchar(20) default 'LOCAL'")
    @Builder.Default
    private AuthProvider provider = LOCAL;

    @Column(name = "provider_id", columnDefinition = "TEXT")
    private String providerId;

    @OneToOne(mappedBy = "user")
    private ForgotPassword forgotPassword;
    // Follow feature
    @ManyToMany(mappedBy = "following")
    private Set<User> follower = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "user_following",
        joinColumns = @JoinColumn(name = "follower_id", columnDefinition = "uuid"),
        inverseJoinColumns = @JoinColumn(name = "following_id", columnDefinition = "uuid")
    )
    private Set<User> following = new HashSet<>();




    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;



    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }



    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.provider == null) {
            this.provider = LOCAL;
        }
    }

}
