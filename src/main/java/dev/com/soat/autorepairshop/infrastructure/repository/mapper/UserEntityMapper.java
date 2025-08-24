package dev.com.soat.autorepairshop.infrastructure.repository.mapper;

import dev.com.soat.autorepairshop.domain.entity.UserDomain;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.RoleEntity;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.UserEntity;

public class UserEntityMapper {

    private UserEntityMapper() {

    }

    public static UserDomain toDomain(final UserEntity entity){
        return UserDomain.restore(
                entity.getUserId(),
                entity.getName(),
                entity.getDocument(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getContractedAt(),
                entity.getRole().getName(),
                entity.getStatus().getName(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static UserEntity toEntity(final UserDomain domain,
                                      final RoleEntity role){
        return new UserEntity(
                domain.getIdentifier(),
                domain.getName(),
                domain.getEmail(),
                domain.getPassword(),
                domain.getDocument(),
                domain.getStatusType(),
                domain.getContractedAt(),
                role
        );
    }
}
