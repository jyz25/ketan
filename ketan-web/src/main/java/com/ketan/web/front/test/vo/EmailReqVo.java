package com.ketan.web.front.test.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 邮件发送验证
 */
@Data
public class EmailReqVo implements Serializable {
    private static final long serialVersionUID = -8560585303684975482L;

    private String to;

    private String title;

    private String content;

}
