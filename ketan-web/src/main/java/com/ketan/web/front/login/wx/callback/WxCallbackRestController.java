package com.ketan.web.front.login.wx.callback;


import com.ketan.api.model.vo.user.wx.BaseWxMsgResVo;
import com.ketan.api.model.vo.user.wx.WxTxtMsgReqVo;
import com.ketan.api.model.vo.user.wx.WxTxtMsgResVo;
import com.ketan.service.user.service.LoginService;
import com.ketan.web.front.login.wx.helper.WxAckHelper;
import com.ketan.web.front.login.wx.helper.WxLoginHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 微信公众号登录相关
 */
@RequestMapping(path = "wx")
@RestController
public class WxCallbackRestController {
    @Autowired
    private LoginService sessionService;
    @Autowired
    private WxLoginHelper qrLoginHelper;
    @Autowired
    private WxAckHelper wxHelper;

    /**
     * 微信的公众号接入 token 验证，即返回echostr的参数值
     *
     * @param request
     * @return
     */
    @GetMapping(path = "callback")
    public String check(HttpServletRequest request) {
        String echoStr = request.getParameter("echostr");
        if (StringUtils.isNoneEmpty(echoStr)) {
            // 缺省安全认证，实际生产需要验证请求是否来自微信服务器
            // 可参考：https://zhuanlan.zhihu.com/p/31157348
            return echoStr;
        }
        return "";
    }

    /**
     * fixme: 需要做防刷校验
     * 微信的响应返回
     * 本地测试访问: curl -X POST 'http://localhost:8080/wx/callback' -H 'content-type:application/xml' -d '<xml><URL><![CDATA[https://hhui.top]]></URL><ToUserName><![CDATA[一灰灰blog]]></ToUserName><FromUserName><![CDATA[demoUser1234]]></FromUserName><CreateTime>1655700579</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[login]]></Content><MsgId>11111111</MsgId></xml>' -i
     *
     * @param msg
     * @return
     */
    @PostMapping(path = "callback",
            consumes = {"application/xml", "text/xml"},
            produces = "application/xml;charset=utf-8")
    public BaseWxMsgResVo callBack(@RequestBody WxTxtMsgReqVo msg) {
        String content = msg.getContent();
        // 关注事件和扫码事件触发
        if ("subscribe".equals(msg.getEvent()) || "scan".equalsIgnoreCase(msg.getEvent())) {
            String key = msg.getEventKey();
            if (StringUtils.isNotBlank(key) || key.startsWith("qrscene_")) {
                // 带参数的二维码，扫描、关注事件拿到之后，直接登录，省却输入验证码这一步
                // fixme 带参数二维码需要 微信认证，个人公众号无权限
                String code = key.substring("qrscene_".length());
                sessionService.autoRegisterWxUserInfo(msg.getFromUserName());
                qrLoginHelper.login(code);
                WxTxtMsgResVo res = new WxTxtMsgResVo();
                res.setContent("登录成功");
                fillResVo(res, msg);
                return res;
            }
        }

        // 默认的消息返回，个人公众号登录使用
        BaseWxMsgResVo res = wxHelper.buildResponseBody(msg.getEvent(), content, msg.getFromUserName());
        fillResVo(res, msg);
        return res;
    }

    private void fillResVo(BaseWxMsgResVo res, WxTxtMsgReqVo msg) {
        res.setFromUserName(msg.getToUserName());
        res.setToUserName(msg.getFromUserName());
        res.setCreateTime(System.currentTimeMillis() / 1000);
    }
}
