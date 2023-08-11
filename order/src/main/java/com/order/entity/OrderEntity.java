package com.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author ti
 * @since 2023-06-10
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("springboot_order")
public class OrderEntity extends Model<OrderEntity> {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 订单号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 商品id
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 商品数量
     */
    @TableField("count")
    private Integer count;

    /**
     * 订单金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    private Integer isDeleted;

    public static final String ID = "id";

    public static final String ORDER_NO = "order_no";

    public static final String USER_ID = "user_id";

    public static final String PRODUCT_ID = "product_id";

    public static final String COUNT = "count";

    public static final String AMOUNT = "amount";

    public static final String STATUS = "status";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

    public static final String IS_DELETED = "is_deleted";

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
