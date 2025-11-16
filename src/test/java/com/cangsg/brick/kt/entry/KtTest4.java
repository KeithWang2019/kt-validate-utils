package com.cangsg.brick.kt.entry;

import com.cangsg.brick.kt.annotation.KtValidateOptions;

import lombok.Data;

@Data
public class KtTest4 {
    @KtValidateOptions(message = "只能选择给定类型", options = { "t1", "t2", "t3" })
    private String type;
}
