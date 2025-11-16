package com.cangsg.brick.kt.entry;

import java.util.HashMap;
import java.util.Map;

import com.cangsg.brick.kt.annotation.KtValidateMapBound;
import com.cangsg.brick.kt.annotation.KtValidateNotEmpty;
import com.cangsg.brick.kt.annotation.KtValidateRegex;
import com.cangsg.brick.kt.annotation.KtValidateStringBound;

import lombok.Data;

@Data
public class KtTest {
    @KtValidateNotEmpty(message = "用户名不能为空")
    @KtValidateStringBound(minLength = 5, maxLength = 10, message = "字符串必须5到10个字符")
    private String login;

    @KtValidateNotEmpty(message = "密码不能为空")
    @KtValidateRegex(message = "密码格式异常", regex = "^\\d{3}$")
    private String password;

    @KtValidateNotEmpty(message = "Map不能为空")
    @KtValidateMapBound(message = "Map数量不能为空", minCount = 1, maxCount = 5)
    private Map<String, KtTestItem> map = new HashMap<>();
}
