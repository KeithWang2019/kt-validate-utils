package com.cangsg.brick.kt.entry;

import java.util.Date;

import com.cangsg.brick.kt.annotation.KtValidateAfterDate;
import com.cangsg.brick.kt.annotation.KtValidateNotEmpty;

import lombok.Data;

@Data
public class KtTest5 {
    @KtValidateNotEmpty(message = "开始时间不能为空")
    private Date startDate;

    @KtValidateNotEmpty(message = "结束时间不能为空")
    @KtValidateAfterDate(message = "开始时间必须小于结束时间", propertyName = "startDate")
    private Date endDate;
}
