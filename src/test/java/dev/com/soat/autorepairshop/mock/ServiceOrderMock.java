package dev.com.soat.autorepairshop.mock;

import dev.com.soat.autorepairshop.application.models.input.OrderInputDTO;
import dev.com.soat.autorepairshop.application.models.output.OrderOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;

import java.time.LocalDateTime;

public class ServiceOrderMock {
    public static final Long DEFAULT_IDENTIFIER = 1L;
    public static final String DEFAULT_DOCUMENT = "71019345012";
    public static final String DEFAULT_LICENSE_PLATE = "ABC1D34";
    public static final OrderStatusType DEFAULT_ORDER_STATUS = OrderStatusType.EM_EXECUCAO;
    public static final String DEFAULT_NOTES = "";
    public static final Long DEFAULT_EMPLOYEE_ID = 1L;
    public static final Long DEFAULT_ACTIVE_BUDGET_ID = 1L;
    public static final Long DEFAULT_SERVICE_ID = 1L;
    public static final LocalDateTime DEFAULT_CREATED_AT = LocalDateTime.now();


    public static OrderDomain buildDomain(){
        return OrderDomain.restore(
                DEFAULT_IDENTIFIER,
                DEFAULT_DOCUMENT,
                DEFAULT_LICENSE_PLATE,
                DEFAULT_ORDER_STATUS,
                DEFAULT_NOTES,
                DEFAULT_EMPLOYEE_ID,
                DEFAULT_ACTIVE_BUDGET_ID,
                DEFAULT_SERVICE_ID,
                DEFAULT_CREATED_AT,
                null,
                null
        );
    }
    public static OrderDomain buildDomain(final OrderStatusType status){
        return OrderDomain.restore(
                DEFAULT_IDENTIFIER,
                DEFAULT_DOCUMENT,
                DEFAULT_LICENSE_PLATE,
                status,
                DEFAULT_NOTES,
                DEFAULT_EMPLOYEE_ID,
                DEFAULT_ACTIVE_BUDGET_ID,
                DEFAULT_SERVICE_ID,
                DEFAULT_CREATED_AT,
                null,
                null
        );
    }

    public static OrderInputDTO buildInputDTO(){
        return new OrderInputDTO(
                DEFAULT_DOCUMENT,
                DEFAULT_LICENSE_PLATE,
                DEFAULT_EMPLOYEE_ID,
                DEFAULT_SERVICE_ID,
                DEFAULT_NOTES
        );
    }

    public static OrderOutputDTO buildOutputDTO(){
        return new OrderOutputDTO(
                DEFAULT_IDENTIFIER,
                DEFAULT_DOCUMENT,
                DEFAULT_LICENSE_PLATE,
                DEFAULT_ORDER_STATUS.getDescription(),
                DEFAULT_EMPLOYEE_ID,
                DEFAULT_ACTIVE_BUDGET_ID,
                DEFAULT_NOTES,
                DEFAULT_CREATED_AT,
                null,
                null
        );
    }
}
