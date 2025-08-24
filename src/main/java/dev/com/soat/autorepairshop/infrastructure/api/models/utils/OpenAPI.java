package dev.com.soat.autorepairshop.infrastructure.api.models.utils;


/**
 * Centraliza os nomes das Tags usadas na documentação do OpenAPI (Swagger).
 * Usar uma classe final com construtor privado impede que ela seja instanciada.
 */
public final class OpenAPI {

    private OpenAPI() {

    }

    public static final String AUTH_TAG = "Auth - v1";
    public static final String USERS_TAG = "Users - v1";
    public static final String PARTS_TAG = "Parts - v1";
    public static final String VEHICLES_TAG = "Vehicle - v1";
    public static final String SERVICES_TAG = "Services - v1";
    public static final String SERVICE_ORDERS_TAG = "Service Orders - v1";
    public static final String INVENTORY_TAG = "Inventory - v1";
    public static final String CLIENTS_TAG = "Clients - v1";
    public static final String BUDGETS_TAG = "Budgets - v1";
    public static final String NOTIFICATION_TAG = "Notification - v1";

    public static final String SECURITY_SCHEMA_NAME = "Authentication Schema";
    public static final String SECURITY_TOKEN_NAME = "token";
}