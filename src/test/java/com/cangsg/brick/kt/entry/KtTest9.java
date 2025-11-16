package com.cangsg.brick.kt.entry;

import com.cangsg.brick.kt.annotation.KtValidateFloatBound;

import lombok.Data;

@Data
public class KtTest9 {
    @KtValidateFloatBound(message = "数字范围1.5-5.5", min = 1.5f, max = 5.5f)
    private float f1;

    @KtValidateFloatBound(message = "数字范围6.0-8.5", min = 6.0f, max = 8.5f)
    private float f2;
}
