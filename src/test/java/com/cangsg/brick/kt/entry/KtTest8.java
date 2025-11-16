package com.cangsg.brick.kt.entry;

import com.cangsg.brick.kt.annotation.KtValidateIntegerBound;

import lombok.Data;

@Data
public class KtTest8 {
    @KtValidateIntegerBound(message = "数字范围3-7", min = 3, max = 7)
    private int i1;

    @KtValidateIntegerBound(message = "数字范围10-12", min = 10, max = 12)
    private Integer i2;
}
