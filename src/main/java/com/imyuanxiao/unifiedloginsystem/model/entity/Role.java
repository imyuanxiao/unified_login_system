package com.imyuanxiao.unifiedloginsystem.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 
 * @TableName role
 */
@TableName(value ="role")
@Data
@Accessors(chain = true)
public class Role implements Serializable {
    /**
     * role ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * role name
     */
    @TableField(value = "role_name")
    private String roleName;

    /**
     * 
     */
    @TableField(value = "description")
    private String description;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}