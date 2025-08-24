package dev.com.soat.autorepairshop.mock;

import dev.com.soat.autorepairshop.domain.entity.PartDomain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PartMock {
    public static final Long DEFAULT_IDENTIFIER = 1L;
    public static final String DEFAULT_NAME = "NAME";
    public static final String DEFAULT_SKU = "SKU";
    public static final String DEFAULT_DESCRIPTION = "DESCRIPTION";
    public static final String DEFAULT_BRAND = "BRAND";
    public static final BigDecimal DEFAULT_SELLING_PRICE = BigDecimal.ONE;
    public static final BigDecimal DEFAULT_BUY_PRICE = BigDecimal.ONE;
    public static final Boolean DEFAULT_ACTIVE = true;
    public static final LocalDateTime DEFAULT_CREATED_AT = LocalDateTime.now();


    public static PartDomain buildDomain(){
        return new PartDomain(
                DEFAULT_IDENTIFIER,
                DEFAULT_NAME,
                DEFAULT_SKU,
                DEFAULT_DESCRIPTION,
                DEFAULT_BRAND,
                DEFAULT_SELLING_PRICE,
                DEFAULT_BUY_PRICE,
                DEFAULT_ACTIVE,
                DEFAULT_CREATED_AT,
                null
        );
    }
}
