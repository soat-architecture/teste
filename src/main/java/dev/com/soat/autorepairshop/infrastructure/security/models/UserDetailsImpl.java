package dev.com.soat.autorepairshop.infrastructure.security.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.com.soat.autorepairshop.application.models.output.UserOutputDTO;
import dev.com.soat.autorepairshop.domain.enums.UserStatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

@AllArgsConstructor
@Getter
public class UserDetailsImpl implements UserDetails {

    private Long identifier;
    private String email;
    @JsonIgnore
    private String password;
    private String status;
    private Collection<GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
        return Objects.equals(this.status, UserStatusType.ACTIVE.getName());
    }

    public static UserDetailsImpl build(final UserOutputDTO user) {
        final var authorityList = createAuthorityList(user.role());
        return new UserDetailsImpl(
                user.identifier(),
                user.email(),
                user.password(),
                user.status(),
                authorityList
        );
    }
}