package kr.hhplus.be.ecommerce.domain.order;

import java.util.Arrays;
import java.util.List;

public class OrderProcesses {

    private final List<OrderProcess> processes;

    private OrderProcesses(List<OrderProcess> processes) {
        this.processes = processes;
    }

    public static OrderProcesses of(List<OrderProcess> processes) {
        return new OrderProcesses(processes);
    }

    public static OrderProcesses ofFailed() {
        return OrderProcesses.of(Arrays.stream(OrderProcessTask.values())
            .map(OrderProcess::ofFailed)
            .toList());
    }

    public boolean isSuccess(OrderProcessTask task) {
        return processes.stream().anyMatch(process -> process.isSuccess(task));
    }

    public boolean existPending() {
        return processes.stream().anyMatch(OrderProcess::isPending);
    }

    public boolean existFailed() {
        return processes.stream().anyMatch(OrderProcess::isFailed);
    }
}
