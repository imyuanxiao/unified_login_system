package com.imyuanxiao.uls.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imyuanxiao.uls.enums.ResultCode;
import com.imyuanxiao.uls.exception.ApiException;
import com.imyuanxiao.uls.mapper.PermissionMapper;
import com.imyuanxiao.uls.model.entity.Role;
import com.imyuanxiao.uls.model.param.RoleParam;
import com.imyuanxiao.uls.service.RoleService;
import com.imyuanxiao.uls.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
* @author Administrator
* @description 针对表【role】的数据库操作Service实现
* @createDate 2023-05-26 18:00:54
*/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
    implements RoleService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public Set<Long> getIdsByUserId(Long userId) {
        return baseMapper.selectIdsByUserId(userId);
    }

    @Override
    public Set<Role> getRolesByUserId(Long userId) {
        Set<Long> roleIds = baseMapper.selectIdsByUserId(userId);
        Set<Role> roleList = new HashSet<>();
        for(Long roleId: roleIds){
            roleList.add(this.getById(roleId));
        }
        return roleList;
    }

    @Override
    public void removeByUserId(Serializable userId) {
        baseMapper.deleteByUserId(userId);
    }

    @Override
    public void insertRolesByUserId(Long userId, Collection<Long> roleIds) {
        baseMapper.insertRolesByUserId(userId, roleIds);
    }

//    @Override
//    public IPage<RolePageVO> selectPage(Page<RolePageVO> page) {
//        QueryWrapper<RolePageVO> queryWrapper = new QueryWrapper<>();
//        // 获取分页列表
//        IPage<RolePageVO> pages = baseMapper.selectPage(page, queryWrapper);
//        // 再查询各角色的权限
//        for (RolePageVO vo : pages.getRecords()) {
//            vo.setPermissionIds(permissionMapper.selectIdsByRoleId(vo.getId()));
//        }
//        return pages;
//    }

    @Override
    public void updatePermissions(RoleParam param) {
        // 先删除原有角色对应的权限数据
        permissionMapper.deleteByRoleId(param.getId());
        // 如果新的权限ID为空就代表删除所有权限，不用后面新增流程了
        if (CollectionUtil.isEmpty(param.getPermissionIds())) {
            return;
        }
        // 新增权限ID
        permissionMapper.insertPermissionsByRoleId(param.getId(), param.getPermissionIds());
    }

    @Override
    public void createRole(RoleParam param) {
        if (lambdaQuery().eq(Role::getRoleName, param.getRoleName()).one() != null) {
            throw new ApiException(ResultCode.FAILED, "角色名重复");
        }
        // 新增角色
        Role role = new Role().setRoleName(param.getRoleName());
        save(role);
        if (CollectionUtil.isEmpty(param.getPermissionIds())) {
            return;
        }
        // 再新增权限数据
        permissionMapper.insertPermissionsByRoleId(role.getId(), param.getPermissionIds());
    }

    public boolean removeRolesByIds(Collection<?> idList) {
        if (CollectionUtil.isEmpty(idList)) {
            return false;
        }
        // 删除角色下所属的权限
        for (Object roleId : idList) {
            permissionMapper.deleteByRoleId((Long)roleId);
        }
        // 删除角色
        baseMapper.deleteBatchIds(idList);
        return true;
    }

}




