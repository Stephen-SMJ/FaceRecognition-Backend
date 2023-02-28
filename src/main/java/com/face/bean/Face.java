package com.face.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName("face")
@Data //不用写set和get方法
public class Face implements Serializable { //当需要把对象打包成数据进行通信传输的时候要继承Serializable
    @TableId(type = IdType.AUTO)
    private Integer fid;  //'主键'
    private String faceBase; //'图片数据 base_64编码'

    @JsonFormat(pattern = "yyyy-mm-dd hh:mm:ss")
    private Date createTime;  //'插入时间'

    private Integer vefNum;  //'验证次数'

    private String faceName; //'人脸名称',

    private String remark; //'人脸备注'

    private Integer faceStatus; //'人脸是否可用，(0==可用，1,不可用)',
    private String updateExtend1; //varchar(300) COMMENT '扩展字段1',
    private String updateExtend2; // varchar(300) COMMENT '扩展字段2',
    private String updateExtend3; // varchar(300) COMMENT '扩展字段3'

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
