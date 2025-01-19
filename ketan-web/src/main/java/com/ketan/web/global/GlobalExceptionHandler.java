package com.ketan.web.global;

import com.ketan.api.model.exception.ForumAdviceException;
import com.ketan.api.model.vo.ResVo;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 主要作用是用于定义一个全局的控制器增强器，
 * 用于处理控制器（Controller）中抛出的异常，
 * 并将处理结果以 JSON 等格式返回给客户端。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ForumAdviceException.class)
    public ResVo<String> handleForumAdviceException(ForumAdviceException e) {
        return ResVo.fail(e.getStatus());
    }
}
