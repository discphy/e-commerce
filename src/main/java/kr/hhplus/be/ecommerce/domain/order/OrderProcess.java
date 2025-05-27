package kr.hhplus.be.ecommerce.domain.order;

import lombok.Getter;

@Getter
public class OrderProcess {

    private final OrderProcessTask task;
    private final OrderProcessStatus status;

    private OrderProcess(OrderProcessTask task, OrderProcessStatus status) {
        this.task = task;
        this.status = status;
    }

    public static OrderProcess of(OrderProcessTask task, OrderProcessStatus status) {
        return new OrderProcess(task, status);
    }

    public static OrderProcess ofPending(OrderProcessTask task) {
        return new OrderProcess(task, OrderProcessStatus.PENDING);
    }

    public static OrderProcess ofFailed(OrderProcessTask task) {
        return new OrderProcess(task, OrderProcessStatus.FAILED);
    }

    public boolean isSuccess(OrderProcessTask task) {
        return task == this.task && isSuccess();
    }

    public boolean isSuccess() {
        return status == OrderProcessStatus.SUCCESS;
    }

    public boolean isFailed() {
        return status == OrderProcessStatus.FAILED;
    }

    public boolean isPending() {
        return status == OrderProcessStatus.PENDING;
    }
}
