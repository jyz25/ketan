package com.ketan.web.front.notice.view;


import com.ketan.api.model.context.ReqInfoContext;
import com.ketan.api.model.enums.NotifyTypeEnum;
import com.ketan.api.model.vo.PageParam;
import com.ketan.core.permission.Permission;
import com.ketan.core.permission.UserRole;
import com.ketan.service.notify.service.NotifyService;
import com.ketan.web.front.notice.vo.NoticeResVo;
import com.ketan.web.global.BaseViewController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * 消息通知
 **/
@Controller
@Permission(role = UserRole.LOGIN)
@RequestMapping(path = "notice")
public class NoticeViewController extends BaseViewController {
    @Autowired
    private NotifyService notifyService;

    @RequestMapping({"/{type}", "/"})
    public String list(@PathVariable(name = "type", required = false) String type, Model model) {
        Long loginUserId = ReqInfoContext.getReqInfo().getUserId();
        Map<String, Integer> map = notifyService.queryUnreadCounts(loginUserId);

        NotifyTypeEnum typeEnum = type == null ? null : NotifyTypeEnum.typeOf(type);
        if (typeEnum == null) {
            // 若没有指定查询的消息类别，则找一个存在消息未读数的进行展示
            typeEnum = map.entrySet().stream().filter(s -> s.getValue() > 0)
                    .map(s -> NotifyTypeEnum.typeOf(s.getKey()))
                    .findAny()
                    .orElse(NotifyTypeEnum.COMMENT);
        }

        NoticeResVo vo = new NoticeResVo();
        vo.setList(notifyService.queryUserNotices(loginUserId, typeEnum, PageParam.newPageInstance()));

        vo.setSelectType(typeEnum.name().toLowerCase());
        vo.setUnreadCountMap(notifyService.queryUnreadCounts(loginUserId));
        model.addAttribute("vo", vo);
        return "views/notice/index";
    }
}
