package com.sharecharge.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sharecharge.mall.mybatis.JsonStringArrayTypeHandler;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品
 */
@TableName(value = "goods",autoResultMap = true)
@Data
public class Goods implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(type = IdType.AUTO)
    //("编号")
    private Integer id;
    @NotBlank(message = "{required}")
    //("商品编号")
    private String goodsSn;
    @NotBlank(message = "{required}")
    //("商品名称")
    private String name;
    @NotBlank(message = "{required}")
    //("商家编号")
    private Integer busId;
    //("商品所属类目ID")
    private Integer categoryId;
    //("商品所属品牌id")
    private Integer brandId;
    //("商品宣传图片列表，采用JSON数组格式")
    @TableField(typeHandler= JsonStringArrayTypeHandler.class)
    private String[] gallery;
    //("商品关键字，采用逗号间隔")
    private String keywords;
    //("商品简介")
    private String brief;
    //("是否上架 1上架")
    private Boolean isOnSale;
    private Short sortOrder;
    //("商品页面商品图片")
    private String picUrl;
    //("商品分享海报")
    private String shareUrl;
    //("是否新品首发，如果设置则可以在新品首发页面展示")
    private Boolean isNew;
    //("是否人气推荐，如果设置则可以在人气推荐页面展示")
    private Boolean isHot;
    //("商品单位，例如件、盒")
    private String unit;
    //("专柜价格")
    private BigDecimal counterPrice;
    //("零售价格")
    private BigDecimal retailPrice;
    //("商品详细介绍，是富文本格式")
    private String detail;
    //("是否是輪播提展示")
    private Integer bannerStatus;
    //("创建时间")
    private Date addTime;
    //("更新时间")
    private Date updateTime;
    //("逻辑删除 1删除")
    private Boolean deleted;

    @TableField
    public static final Boolean IS_DELETED = Deleted.IS_DELETED.value();


    @TableField
    public static final Boolean NOT_DELETED = Deleted.NOT_DELETED.value();

    //("销量数量")
    private transient Integer salesSum;
    //("发货地址")
    private transient String formAddress;


    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_goods
     *
     * @mbg.generated
     */

    /**
     * This enum was generated by MyBatis Generator.
     * This enum corresponds to the database table litemall_goods
     *
     * @mbg.generated
     */
    public enum Deleted {
        NOT_DELETED(new Boolean("0"), "未删除"),
        IS_DELETED(new Boolean("1"), "已删除");

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table litemall_goods
         *
         * @mbg.generated
         */
        private final Boolean value;

        /**
         * This field was generated by MyBatis Generator.
         * This field corresponds to the database table litemall_goods
         *
         * @mbg.generated
         */
        private final String name;

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table litemall_goods
         *
         * @mbg.generated
         */
        Deleted(Boolean value, String name) {
            this.value = value;
            this.name = name;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table litemall_goods
         *
         * @mbg.generated
         */
        public Boolean getValue() {
            return this.value;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table litemall_goods
         *
         * @mbg.generated
         */
        public Boolean value() {
            return this.value;
        }

        /**
         * This method was generated by MyBatis Generator.
         * This method corresponds to the database table litemall_goods
         *
         * @mbg.generated
         */
        public String getName() {
            return this.name;
        }
    }
}
