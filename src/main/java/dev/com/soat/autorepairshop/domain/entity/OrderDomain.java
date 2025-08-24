package dev.com.soat.autorepairshop.domain.entity;

import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import dev.com.soat.autorepairshop.domain.objects.Document;
import dev.com.soat.autorepairshop.domain.objects.LicensePlate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false)
@ToString
@Getter
@Setter
public class OrderDomain extends DomainEntity<Long> {

    private final Long identifier;
    private final Document clientDocument;
    private final LicensePlate vehicleLicensePlate;
    private OrderStatusType status;
    private String notes;
    private Long employeeId;
    private Long activeBudgetId;
    private final Long serviceId;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    private OrderDomain(final Long identifier,
                        final String clientDocument,
                        final String vehicleLicensePlate,
                        final OrderStatusType status,
                        final String notes,
                        final Long employeeId,
                        final Long activeBudgetId,
                        final Long serviceId,
                        final LocalDateTime createdAt,
                        final LocalDateTime updatedAt ,
                        final LocalDateTime completedAt) {

        this.identifier = identifier;
        this.clientDocument = Document.from(clientDocument);
        this.vehicleLicensePlate = new LicensePlate(vehicleLicensePlate);
        this.status = status;
        this.notes = notes;
        this.employeeId = employeeId;
        this.activeBudgetId = activeBudgetId;
        this.serviceId = serviceId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.completedAt = completedAt;
    }

    public static OrderDomain create(final String clientDocument,
                                     final String vehicleLicensePlate,
                                     final String notes,
                                     final Long employeeId,
                                     final Long serviceId) {
        return new OrderDomain(
                null,
                clientDocument,
                vehicleLicensePlate,
                OrderStatusType.RECEBIDA,
                notes,
                employeeId,
                null,
                serviceId,
                LocalDateTime.now(),
                null,
                null);
    }

    public static OrderDomain restore(final Long identifier,
                                      final String clientDocument,
                                      final String vehicleLicensePlate,
                                      final OrderStatusType status,
                                      final String notes,
                                      final Long employeeId,
                                      final Long activeBudgetId,
                                      final Long serviceId,
                                      final LocalDateTime createdAt,
                                      final LocalDateTime updatedAt ,
                                      final LocalDateTime completedAt){
        return new OrderDomain(
                identifier,
                clientDocument,
                vehicleLicensePlate,
                status,
                notes,
                employeeId,
                activeBudgetId,
                serviceId,
                createdAt,
                updatedAt,
                completedAt);
    }

    public void changeStatus(OrderStatusType newStatus){
        this.status = newStatus;
    }

    public void addNotes(String notes){
        this.notes += " " + notes;
    }

    public void complete(){
        completedAt = LocalDateTime.now();
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    @Override
    public Long getIdentifier() {
        return identifier;
    }

    public boolean isOrderInInitialStatus() {
        return this.status != OrderStatusType.RECEBIDA;
    }

    public boolean isOrderInNotApproved() {
        return this.status != OrderStatusType.APROVADA;
    }

    public boolean isOrderInNotExecution() {
        return this.status != OrderStatusType.EM_EXECUCAO;
    }

    public boolean isOrderInNotFinished() {
        return this.status != OrderStatusType.FINALIZADA;
    }

    public boolean isOrderNotInDiagnostic() {
        return this.status != OrderStatusType.EM_DIAGNOSTICO;
    }

    public void assignEmployee(final Long employeeId){
        this.updatedAt = LocalDateTime.now();
        this.employeeId = employeeId;
        this.status = OrderStatusType.EM_DIAGNOSTICO;
    }

    public void awaitingApproval(){
        this.updatedAt = LocalDateTime.now();
        this.status = OrderStatusType.AGUARDANDO_APROVACAO;
    }

    public void approval(){
        this.updatedAt = LocalDateTime.now();
        this.status = OrderStatusType.APROVADA;
    }

    public void notApproval(){
        this.updatedAt = LocalDateTime.now();
        this.status = OrderStatusType.REJEITADA;
    }

    public void updateEmployee(final Long employeeId){
        this.updatedAt = LocalDateTime.now();
        this.employeeId = employeeId;
    }

    public void startExecution(){
        this.updatedAt = LocalDateTime.now();
        this.status = OrderStatusType.EM_EXECUCAO;
    }

    public void finish(){
        this.updatedAt = LocalDateTime.now();
        this.completedAt = LocalDateTime.now();
        this.status = OrderStatusType.FINALIZADA;
    }

    public void delivery(){
        this.updatedAt = LocalDateTime.now();
        this.status = OrderStatusType.ENTREGUE;
    }

    public void attributeBudget(final Long activeBudgetId){
        this.updatedAt = LocalDateTime.now();
        this.activeBudgetId = activeBudgetId;
    }
}
