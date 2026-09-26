package com.campus.book_service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tb_book")
public class Book {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String author;
    private BigDecimal price;
    private Integer stock;
    private Integer version;  // 乐观锁版本号
    private Long sellerId;    // 发布人ID
    private Integer status;   // 1上架 0下架
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}