package com.ketan.api.model.exception;

import com.ketan.api.model.vo.Status;
import com.ketan.api.model.vo.constants.StatusEnum;
import lombok.Getter;

/**
 * 业务异常
 */
public class ForumException extends RuntimeException {
    @Getter
    private Status status;

    public ForumException(Status status) {
        this.status = status;
    }

    public ForumException(int code, String msg) {
        this.status = Status.newStatus(code, msg);
    }

    public ForumException(StatusEnum statusEnum, Object... args) {
        this.status = Status.newStatus(statusEnum, args);
    }

}
