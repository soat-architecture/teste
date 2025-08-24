package dev.com.soat.autorepairshop.mock;

import dev.com.soat.autorepairshop.domain.entity.ServiceDomain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ServiceMock {
    public static final Long DEFAULT_IDENTIFIER = 1L;
    public static final String DEFAULT_NAME = "NAME";
    public static final String DEFAULT_DESCRIPTION = "DESCRIPTION";
    public static final BigDecimal DEFAULT_BASE_PRICE = BigDecimal.ONE;
    public static final LocalDateTime DEFAULT_CREATED_AT = LocalDateTime.now();


    public static ServiceDomain buildDomain(){
        return new ServiceDomain(
                DEFAULT_IDENTIFIER,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_BASE_PRICE,
                DEFAULT_CREATED_AT,
                null
        );
    }
}
