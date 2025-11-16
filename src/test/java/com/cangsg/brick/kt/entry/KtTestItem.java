package com.cangsg.brick.kt.entry;

import java.util.List;

import com.cangsg.brick.kt.annotation.KtValidateNotEmpty;
import com.cangsg.brick.kt.annotation.KtValidateStringBound;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KtTestItem {
    @KtValidateNotEmpty(message = "用户名不能为空")
    @KtValidateStringBound(message = "用户名大于4个字符，小于50个字符", minLength = 4, maxLength = 50)
    private String name;
}
