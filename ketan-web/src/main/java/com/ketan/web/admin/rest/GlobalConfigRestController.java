package com.ketan.web.admin.rest;


import com.ketan.api.model.vo.PageVo;
import com.ketan.api.model.vo.ResVo;
import com.ketan.api.model.vo.config.GlobalConfigReq;
import com.ketan.api.model.vo.config.SearchGlobalConfigReq;
import com.ketan.api.model.vo.config.dto.GlobalConfigDTO;
import com.ketan.core.permission.Permission;
import com.ketan.core.permission.UserRole;
import com.ketan.service.config.service.GlobalConfigService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 标签后台
 */
@RestController
@Permission(role = UserRole.LOGIN)
@Api(value = "全局配置管理控制器", tags = "全局配置")
@RequestMapping(path = {"api/admin/global/config/", "admin/global/config/"})
public class GlobalConfigRestController {

    @Autowired
    private GlobalConfigService globalConfigService;

    @Permission(role = UserRole.ADMIN)
    @PostMapping(path = "save")
    public ResVo<String> save(@RequestBody GlobalConfigReq req) {
        globalConfigService.save(req);
        return ResVo.ok();
    }

    @Permission(role = UserRole.ADMIN)
    @GetMapping(path = "delete")
    public ResVo<String> delete(@RequestParam(name = "id") Long id) {
        globalConfigService.delete(id);
        return ResVo.ok();
    }

    @PostMapping(path = "list")
    @Permission(role = UserRole.ADMIN)
    public ResVo<PageVo<GlobalConfigDTO>> list(@RequestBody SearchGlobalConfigReq req) {
        PageVo<GlobalConfigDTO> page = globalConfigService.getList(req);
        return ResVo.ok(page);
    }
}
