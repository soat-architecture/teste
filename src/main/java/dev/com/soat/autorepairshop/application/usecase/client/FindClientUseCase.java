package dev.com.soat.autorepairshop.application.usecase.client;

import dev.com.soat.autorepairshop.application.mapper.ApplicationClientMapper;
import dev.com.soat.autorepairshop.application.models.input.ClientInputDTO;
import dev.com.soat.autorepairshop.application.utils.ClientValidationUtils;
import dev.com.soat.autorepairshop.domain.entity.ClientDomain;
import dev.com.soat.autorepairshop.domain.objects.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class FindClientUseCase {

    private final ClientValidationUtils clientValidationUtils;

    public ClientInputDTO execute(String doc) {
        var document = Document.from(doc);

        log.info("c=FindClientUseCase m=execute s=start document={}", document.mask());

        ClientDomain client = clientValidationUtils.validateClientExistenceByDocument(document.unformat());

        log.info("c=FindClientUseCase m=execute s=end document={}", document.mask());
        return ApplicationClientMapper.map(client);
    }

}
