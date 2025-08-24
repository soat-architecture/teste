package dev.com.soat.autorepairshop.application.mapper;

import dev.com.soat.autorepairshop.application.models.input.UserInputDTO;
import dev.com.soat.autorepairshop.application.models.output.UserOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.UserDomain;
import dev.com.soat.autorepairshop.domain.objects.Document;

public class UserApplicationMapper {

    private UserApplicationMapper() {
    }

    public static UserDomain toDomain(final UserInputDTO dto) {
        return UserDomain.create(
            Document.from(dto.document()).unformat(),
            dto.name(),
            dto.email(),
            dto.password(),
            dto.contractedAt(),
            dto.role()
        );
    }

    public static UserOutputDTO toDTO(final UserDomain domain){
        return new UserOutputDTO(
            domain.getIdentifier(),
            domain.getName(),
            domain.getDocument(),
            domain.getEmail(),
            domain.getPassword(),
            domain.getContractedAt(),
            domain.getRole(),
            domain.getStatus(),
            domain.getCreatedAt(),
            domain.getUpdatedAt()
        );
    }
}
