package dev.com.soat.autorepairshop.domain.gateway;

public interface PartInventoryGateway {
    Integer getOnHand(Long partId);
    void decrease(Long partId, int quantity);
    void increase(Long partId, int quantity); // <-- novo
}
