package dev.com.soat.autorepairshop.domain.entity;

import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.Month;

class OrderDomainTest {

    @Test
    void testCreateOrderDomain() {
        OrderDomain result = OrderDomain.create("45997418000153", "ABC-1234", "Teste",
                32L, 10L);

        Assertions.assertNull(result.getIdentifier());
        Assertions.assertEquals("45997418000153", result.getClientDocument().getValue());
        Assertions.assertEquals("ABC-1234", result.getVehicleLicensePlate().getValue());
        Assertions.assertEquals(OrderStatusType.RECEBIDA, result.getStatus());
        Assertions.assertEquals("Teste", result.getNotes());
        Assertions.assertEquals(32L, result.getEmployeeId());
        Assertions.assertEquals(10L, result.getServiceId());
        Assertions.assertNotNull(result.getCreatedAt());
        Assertions.assertNull(result.getUpdatedAt());
        Assertions.assertNull(result.getCompletedAt());
    }

    @Test
    void testRestoreOrderDomain() {
        LocalDateTime createdAt = LocalDateTime.of(2025, Month.JULY, 1, 0, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2025, Month.JULY, 26, 0, 0);

        OrderDomain result = OrderDomain.restore(12L,"45997418000153", "ABC-1234",
                OrderStatusType.EM_DIAGNOSTICO, "Teste", 32L, 10L,10L, createdAt, updatedAt, null);

        Assertions.assertEquals(12L, result.getIdentifier());
        Assertions.assertEquals("45997418000153", result.getClientDocument().getValue());
        Assertions.assertEquals("ABC-1234", result.getVehicleLicensePlate().getValue());
        Assertions.assertEquals(OrderStatusType.EM_DIAGNOSTICO, result.getStatus());
        Assertions.assertEquals("Teste", result.getNotes());
        Assertions.assertEquals(32L, result.getEmployeeId());
        Assertions.assertEquals(10L, result.getServiceId());
        Assertions.assertEquals(createdAt, result.getCreatedAt());
        Assertions.assertEquals(updatedAt, result.getUpdatedAt());
        Assertions.assertNull(result.getCompletedAt());
    }

    @Test
    void testChangeStatus(){
        OrderDomain result = OrderDomain.create("45997418000153", "ABC-1234", "Teste",
                32L, 10L);

        result.changeStatus(OrderStatusType.EM_DIAGNOSTICO);

        Assertions.assertEquals(OrderStatusType.EM_DIAGNOSTICO, result.getStatus());
    }

    @Test
    void testAddNotes(){
        OrderDomain result = OrderDomain.create("45997418000153", "ABC-1234", "Teste",
                32L, 10L);

        result.addNotes("feito com sucesso!");

        Assertions.assertEquals("Teste feito com sucesso!", result.getNotes());
    }

    @Test
    void testComplete(){
        OrderDomain result = OrderDomain.create("45997418000153", "ABC-1234", "Teste",
                32L, 10L);

        result.complete();

        Assertions.assertNotNull(result.getCompletedAt());
    }

    @Test
    void testIsOrderInInitialStatus_WhenStatusIsRecebida_ShouldReturnFalse() {
        OrderDomain order = OrderDomain.create("45997418000153", "ABC-1234", "Teste", 32L, 10L);

        boolean result = order.isOrderInInitialStatus();

        Assertions.assertFalse(result);
    }

    @Test
    void testIsOrderInInitialStatus_WhenStatusIsNotRecebida_ShouldReturnTrue() {
        OrderDomain order = OrderDomain.create("45997418000153", "ABC-1234", "Teste", 32L, 10L);
        order.changeStatus(OrderStatusType.EM_DIAGNOSTICO);

        boolean result = order.isOrderInInitialStatus();

        Assertions.assertTrue(result);
    }

    @Test
    void testIsOrderInNotApproved_WhenStatusIsAprovada_ShouldReturnFalse() {
        OrderDomain order = OrderDomain.create("45997418000153", "ABC-1234", "Teste", 32L, 10L);
        order.changeStatus(OrderStatusType.APROVADA);

        boolean result = order.isOrderInNotApproved();

        Assertions.assertFalse(result);
    }

    @Test
    void testIsOrderInNotApproved_WhenStatusIsNotAprovada_ShouldReturnTrue() {
        OrderDomain order = OrderDomain.create("45997418000153", "ABC-1234", "Teste", 32L, 10L);

        boolean result = order.isOrderInNotApproved();

        Assertions.assertTrue(result);
    }

    @Test
    void testIsOrderInNotExecution_WhenStatusIsEmExecucao_ShouldReturnFalse() {
        OrderDomain order = OrderDomain.create("45997418000153", "ABC-1234", "Teste", 32L, 10L);
        order.changeStatus(OrderStatusType.EM_EXECUCAO);

        boolean result = order.isOrderInNotExecution();

        Assertions.assertFalse(result);
    }

    @Test
    void testIsOrderInNotExecution_WhenStatusIsNotEmExecucao_ShouldReturnTrue() {
        OrderDomain order = OrderDomain.create("45997418000153", "ABC-1234", "Teste", 32L, 10L);

        boolean result = order.isOrderInNotExecution();

        Assertions.assertTrue(result);
    }

    @Test
    void testIsOrderInNotFinished_WhenStatusIsFinalizada_ShouldReturnFalse() {
        OrderDomain order = OrderDomain.create("45997418000153", "ABC-1234", "Teste", 32L, 10L);
        order.changeStatus(OrderStatusType.FINALIZADA);

        boolean result = order.isOrderInNotFinished();

        Assertions.assertFalse(result);
    }

    @Test
    void testIsOrderInNotFinished_WhenStatusIsDiagnostic_ShouldReturnFalse() {
        OrderDomain order = OrderDomain.create("45997418000153", "ABC-1234", "Teste", 32L, 10L);
        order.changeStatus(OrderStatusType.EM_DIAGNOSTICO);

        boolean result = order.isOrderNotInDiagnostic();

        Assertions.assertFalse(result);
    }

    @Test
    void testIsOrderInNotFinished_WhenStatusIsNotFinalizada_ShouldReturnTrue() {
        OrderDomain order = OrderDomain.create("45997418000153", "ABC-1234", "Teste", 32L, 10L);

        boolean result = order.isOrderInNotFinished();

        Assertions.assertTrue(result);
    }

    @Test
    void testAssignEmployee() {
        OrderDomain order = OrderDomain.create("45997418000153", "ABC-1234", "Teste", 32L, 10L);
        LocalDateTime beforeAssign = LocalDateTime.now();

        order.assignEmployee(50L);

        Assertions.assertEquals(50L, order.getEmployeeId());
        Assertions.assertEquals(OrderStatusType.EM_DIAGNOSTICO, order.getStatus());
        Assertions.assertNotNull(order.getUpdatedAt());
        Assertions.assertTrue(order.getUpdatedAt().isAfter(beforeAssign) || order.getUpdatedAt().isEqual(beforeAssign));
    }

    @Test
    void testUpdateEmployee() {
        OrderDomain order = OrderDomain.create("45997418000153", "ABC-1234", "Teste", 32L, 10L);
        LocalDateTime beforeUpdate = LocalDateTime.now();

        order.updateEmployee(60L);

        Assertions.assertEquals(60L, order.getEmployeeId());
        Assertions.assertNotNull(order.getUpdatedAt());
        Assertions.assertTrue(order.getUpdatedAt().isAfter(beforeUpdate) || order.getUpdatedAt().isEqual(beforeUpdate));
    }

    @Test
    void testStartExecution() {
        OrderDomain order = OrderDomain.create("45997418000153", "ABC-1234", "Teste", 32L, 10L);
        LocalDateTime beforeStart = LocalDateTime.now();

        order.startExecution();

        Assertions.assertEquals(OrderStatusType.EM_EXECUCAO, order.getStatus());
        Assertions.assertNotNull(order.getUpdatedAt());
        Assertions.assertTrue(order.getUpdatedAt().isAfter(beforeStart) || order.getUpdatedAt().isEqual(beforeStart));
    }

    @Test
    void testFinish() {
        OrderDomain order = OrderDomain.create("45997418000153", "ABC-1234", "Teste", 32L, 10L);
        LocalDateTime beforeFinish = LocalDateTime.now();

        order.finish();

        Assertions.assertEquals(OrderStatusType.FINALIZADA, order.getStatus());
        Assertions.assertNotNull(order.getUpdatedAt());
        Assertions.assertTrue(order.getUpdatedAt().isAfter(beforeFinish) || order.getUpdatedAt().isEqual(beforeFinish));
    }

    @Test
    void testDelivery() {
        OrderDomain order = OrderDomain.create("45997418000153", "ABC-1234", "Teste", 32L, 10L);
        LocalDateTime beforeDelivery = LocalDateTime.now();

        order.delivery();

        Assertions.assertEquals(OrderStatusType.ENTREGUE, order.getStatus());
        Assertions.assertNotNull(order.getUpdatedAt());
        Assertions.assertTrue(order.getUpdatedAt().isAfter(beforeDelivery) || order.getUpdatedAt().isEqual(beforeDelivery));
    }

    @Test
    void testAddNotesToExistingNotes() {
        OrderDomain order = OrderDomain.create("45997418000153", "ABC-1234", "Diagnóstico inicial",
                32L, 10L);

        order.addNotes("Peça substituída");
        order.addNotes("Teste realizado");

        Assertions.assertEquals("Diagnóstico inicial Peça substituída Teste realizado", order.getNotes());
    }

    @Test
    void testCompleteOrderFlow() {
        // Criar ordem
        OrderDomain order = OrderDomain.create("45997418000153", "ABC-1234", "Reparo do motor", 32L, 10L);
        Assertions.assertEquals(OrderStatusType.RECEBIDA, order.getStatus());

        // Atribuir funcionário (vai para EM_DIAGNOSTICO)
        order.assignEmployee(50L);
        Assertions.assertEquals(OrderStatusType.EM_DIAGNOSTICO, order.getStatus());
        Assertions.assertEquals(50L, order.getEmployeeId());

        // Iniciar execução
        order.startExecution();
        Assertions.assertEquals(OrderStatusType.EM_EXECUCAO, order.getStatus());

        // Adicionar notas durante execução
        order.addNotes("Peça trocada com sucesso");

        order.notApproval();
        Assertions.assertEquals(OrderStatusType.REJEITADA, order.getStatus());

        // Adicionar notas durante execução
        order.approval();
        Assertions.assertEquals(OrderStatusType.APROVADA, order.getStatus());

        // Finalizar serviço
        order.finish();
        Assertions.assertEquals(OrderStatusType.FINALIZADA, order.getStatus());

        // Entregar
        order.delivery();
        Assertions.assertEquals(OrderStatusType.ENTREGUE, order.getStatus());

        // Completar (marcar data de conclusão)
        order.complete();
        Assertions.assertNotNull(order.getCompletedAt());

        // Verificar notas finais
        Assertions.assertEquals("Reparo do motor Peça trocada com sucesso", order.getNotes());
    }
}