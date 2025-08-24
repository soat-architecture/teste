package dev.com.soat.autorepairshop.infrastructure.security.service;

import dev.com.soat.autorepairshop.application.usecase.user.FindUserByEmailUseCase;
import dev.com.soat.autorepairshop.infrastructure.security.models.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final FindUserByEmailUseCase findUserByEmailUseCase;

    @Override
    public UserDetails loadUserByUsername(final String email){
        final var user = findUserByEmailUseCase.execute(email);
        return UserDetailsImpl.build(user);
    }
}
