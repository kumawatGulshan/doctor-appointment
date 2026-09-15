package com.gulshan.doctor.appointment.Entity;


import com.gulshan.doctor.appointment.Entity.Enums.Roles;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false)
    private String email;
    private String password;
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Roles> roles;

    @CreationTimestamp
    private LocalDateTime createdAt;

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//
//        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
//        roles.forEach(
//                role -> {
//                    Set<SimpleGrantedAuthority> permissions = PermissionMapping.getAuthoritiesForRoles(role);
//                    authorities.addAll(permissions);
//                    authorities.add(new SimpleGrantedAuthority("ROLE_"+role.name()));
//                }
//        );
//        return authorities;
//    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

       return roles.stream().map(
                role -> new SimpleGrantedAuthority("ROLE_"+role.name()))
               .collect(Collectors.toSet());
    }


    @Override
    public @Nullable String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
