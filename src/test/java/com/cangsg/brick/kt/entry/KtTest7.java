package com.cangsg.brick.kt.entry;

import java.time.LocalDateTime;

import com.cangsg.brick.kt.annotation.KtValidateAfterDate;
import com.cangsg.brick.kt.annotation.KtValidateNotEmpty;

import lombok.Data;

@Data
public class KtTest7 {
    @KtValidateNotEmpty(message = "开始时间不能为空")
    private LocalDateTime startDate;

    @KtValidateNotEmpty(message = "结束时间不能为空")
    @KtValidateAfterDate(message = "开始时间必须小于结束时间", propertyName = "startDate")
    private LocalDateTime endDate;
}
