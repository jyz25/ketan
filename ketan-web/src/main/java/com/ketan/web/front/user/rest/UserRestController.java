package com.ketan.web.front.user.rest;

import com.ketan.api.model.vo.ResVo;
import com.ketan.api.model.vo.user.UserRelationReq;
import com.ketan.core.permission.Permission;
import com.ketan.core.permission.UserRole;
import com.ketan.service.user.service.relation.UserRelationServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(path = "user/api")
public class UserRestController {

    @Resource
    private UserRelationServiceImpl userRelationService;


    /**
     * 保存用户关系
     *
     * @param req
     * @return
     * @throws Exception
     */
    @Permission(role = UserRole.LOGIN)
    @PostMapping(path = "saveUserRelation")
    public ResVo<Boolean> saveUserRelation(@RequestBody UserRelationReq req) {
        userRelationService.saveUserRelation(req);
        return ResVo.ok(true);
    }


}
