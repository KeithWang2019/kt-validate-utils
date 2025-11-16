package com.cangsg.brick.kt.entry;

import com.cangsg.brick.kt.annotation.KtValidateRegex;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KtTest2Item {
    @KtValidateRegex(message = "名称必须3个数字",regex = "^\\d{3}$")
    private String name;
}
