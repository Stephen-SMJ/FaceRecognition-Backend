package com.face.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.models.auth.In;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@TableName("face_vef_log")
public class FaceVefLog {
    @TableId(type = IdType.AUTO)
    private Integer lid; // int primary key auto_increment COMMENT '主键',

    @JsonFormat(pattern = "yyyy-mm-dd hh:mm:ss")
    private Date vefTime; // datetime COMMENT '验证时间',
    private Integer vefCode; // int COMMENT '返回code',
    private String vefMsg; // varchar(200) COMMENT '返回的消息',
    private String loginName; // varchar(100) COMMENT '验证人'

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
