package com.ketan.api.model.exception;

import com.ketan.api.model.vo.constants.StatusEnum;

public class ExceptionUtil {

    public static ForumException of(StatusEnum status, Object... args) {
        return new ForumException(status, args);
    }

}
