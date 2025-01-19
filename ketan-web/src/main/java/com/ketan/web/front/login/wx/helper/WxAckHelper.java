package com.ketan.web.front.login.wx.helper;

import com.ketan.api.model.vo.user.wx.BaseWxMsgResVo;
import com.ketan.api.model.vo.user.wx.WxImgTxtItemVo;
import com.ketan.api.model.vo.user.wx.WxImgTxtMsgResVo;
import com.ketan.api.model.vo.user.wx.WxTxtMsgResVo;
import com.ketan.core.util.CodeGenerateUtil;
import com.ketan.service.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


@Slf4j
@Component
public class WxAckHelper {
    @Autowired
    private LoginService sessionService;
    @Autowired
    private WxLoginHelper qrLoginHelper;


    /**
     * 返回自动响应的文本
     *
     * @return
     */
    public BaseWxMsgResVo buildResponseBody(String eventType, String content, String fromUser) {
        // 返回的文本消息
        String textRes = null;
        // 返回的是图文消息
        List<WxImgTxtItemVo> imgTxtList = null;
        if ("subscribe".equalsIgnoreCase(eventType)) {
            // 订阅
            textRes = "感谢您的关注，祝您每天都带点好运哦！！！。\n" +
                    "\n";
        }
        // 微信公众号登录
        else if (CodeGenerateUtil.isVerifyCode(content)) {
            sessionService.autoRegisterWxUserInfo(fromUser);
            if (qrLoginHelper.login(content)) {
                textRes = "登录成功，开始愉快的玩耍吧！";
            } else {
                textRes = "验证码过期了，刷新登录页面重试一下吧";
            }
        } else {
            textRes = "/:? 咦？您发送的信息我看不懂ε=(´ο｀*)))唉\n" +
                    "\n" +
                    "[自动回复]";
        }

        if (textRes != null) {
            WxTxtMsgResVo vo = new WxTxtMsgResVo();
            vo.setContent(textRes);
            return vo;
        } else {
            WxImgTxtMsgResVo vo = new WxImgTxtMsgResVo();
            vo.setArticles(imgTxtList);
            vo.setArticleCount(imgTxtList.size());
            return vo;
        }
    }
}
