package com.imyuanxiao.uls.controller.api;

import com.imyuanxiao.uls.annotation.Auth;
import com.imyuanxiao.uls.model.entity.Role;
import com.imyuanxiao.uls.model.param.RoleParam;
import com.imyuanxiao.uls.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static com.imyuanxiao.uls.util.CommonUtil.ACTION_SUCCESSFUL;

/**
 * @ClassName RoleController
 * @Description Role Management Interface
 * @Author imyuanxiao
 * @Date 2023/5/4 17:35
 * @Version 1.0
 **/
@Slf4j
@RestController
@RequestMapping("/role")
@Auth(id = 3000, name = "角色管理")
@Api(tags = "Role Management Interface")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping("/add")
    @Auth(id = 1, name = "新增角色")
    @ApiOperation(value = "Add role")
    public String createRole(@RequestBody @Validated(RoleParam.CreateRole.class) RoleParam param) {
        roleService.createRole(param);
        return ACTION_SUCCESSFUL;
    }

    @DeleteMapping("/delete")
    @Auth(id = 2, name = "删除角色")
    @ApiOperation(value = "Delete role")
    public String deleteRole(@RequestBody Long[] ids) {
        roleService.removeRolesByIds(Arrays.asList(ids));
        return ACTION_SUCCESSFUL;
    }

    @PutMapping("/update")
    @Auth(id = 3, name = "编辑角色")
    @ApiOperation(value = "Update role")
    public String updateRole(@RequestBody @Validated(RoleParam.UpdateResources.class) RoleParam param) {
        roleService.updatePermissions(param);
        return ACTION_SUCCESSFUL;
    }

    @GetMapping("/list")
    @ApiOperation(value = "Get all roles")
    public List<Role> getRoleList() {
        return roleService.list();
    }

//    @GetMapping("/page/{current}&{pageSize}")
//    @ApiOperation(value = "Page through role information")
//    public IPage<RolePageVO> getRolePage(@PathVariable("current") int current, @PathVariable("pageSize") int pageSize) {
//        // Set pagination parameters
//        Page<RolePageVO> page = new Page<>();
//        OrderItem orderItem = new OrderItem();
//        orderItem.setColumn("id");
//        page.setCurrent(current).setSize(pageSize).addOrder(orderItem);
//        return roleService.selectPage(page);
//    }

}
