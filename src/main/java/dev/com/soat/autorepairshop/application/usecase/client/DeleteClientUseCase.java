package dev.com.soat.autorepairshop.application.usecase.client;

import dev.com.soat.autorepairshop.domain.gateway.ClientGateway;
import dev.com.soat.autorepairshop.domain.objects.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeleteClientUseCase {

    private final ClientGateway gateway;

    public void execute(String doc) {
        var document = Document.from(doc);

        log.info("c=DeleteClientUseCase m=execute s=start document={}", document.mask());

        this.gateway.delete(document.unformat());

        log.info("c=DeleteClientUseCase m=execute s=end document={}", document.mask());
    }

}
