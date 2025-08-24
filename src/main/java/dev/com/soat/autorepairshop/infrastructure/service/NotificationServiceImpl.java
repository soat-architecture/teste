package dev.com.soat.autorepairshop.infrastructure.service;

import dev.com.soat.autorepairshop.domain.entity.BudgetAggregateRoot;
import dev.com.soat.autorepairshop.domain.enums.EmailMessageType;
import dev.com.soat.autorepairshop.domain.enums.NotificationStatusType;
import dev.com.soat.autorepairshop.domain.service.NotificationService;
import dev.com.soat.autorepairshop.infrastructure.adapter.EmailAdapter;
import dev.com.soat.autorepairshop.infrastructure.adapter.TemplateEmailAdapter;
import dev.com.soat.autorepairshop.infrastructure.repository.NotificationOrderRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.mapper.NotificationOrderEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    @Value("${company.email}")
    private String companyEmail;
    private final EmailAdapter emailAdapter;
    private final NotificationOrderRepository notificationOrderRepository;
    private final TemplateEmailAdapter templateEmailAdapter;

    @Override
    public void sendBudgetNotification(final BudgetAggregateRoot budgetAggregateRoot) {
        final var emailMessageType = EmailMessageType.BUDGET_EMAIL_TITLE;
        final var message = templateEmailAdapter.buildBudgetMessage(emailMessageType.getTitle(), budgetAggregateRoot);
        try {
            log.info("c=NotificationServiceImpl m=sendBudgetNotification s=Start to={}", budgetAggregateRoot.getClient().getEmail());

            emailAdapter.send(
                    companyEmail,
                    budgetAggregateRoot.getClient().getEmail(),
                    emailMessageType.getSubject(),
                    message,
                    emailMessageType.getIsHtml()
            );

            final var entity = NotificationOrderEntityMapper.create(
                    budgetAggregateRoot.getBudget().getServiceOrderId(),
                    budgetAggregateRoot.getClient().getIdentifier(),
                    emailMessageType.getChannel(),
                    message,
                    NotificationStatusType.DELIVERED
            );

            notificationOrderRepository.save(entity);
            log.info("c=NotificationServiceImpl m=sendBudgetNotification s=Done to={}", budgetAggregateRoot.getClient().getEmail());
        } catch (Exception exception){
            log.error("c=NotificationServiceImpl m=sendBudgetNotification s=Error to={} message={}", budgetAggregateRoot.getClient().getEmail(), exception.getMessage());
            final var entity = NotificationOrderEntityMapper.create(
                    budgetAggregateRoot.getBudget().getServiceOrderId(),
                    budgetAggregateRoot.getClient().getIdentifier(),
                    emailMessageType.getChannel(),
                    message,
                    NotificationStatusType.FAILED
            );

            notificationOrderRepository.save(entity);
            throw exception;
        }
    }
}
