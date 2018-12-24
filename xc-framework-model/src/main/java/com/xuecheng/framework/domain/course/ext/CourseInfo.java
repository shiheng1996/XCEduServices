package com.xuecheng.framework.domain.course.ext;

import com.xuecheng.framework.domain.course.CourseBase;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by admin on 2018/2/10.
 */
@Data
@ToString
public class CourseInfo extends CourseBase implements Serializable {

    //课程图片
    private String pic;

}
