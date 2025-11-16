package com.cangsg.brick.kt.entry;

import com.cangsg.brick.kt.annotation.KtValidateCustom;
import com.cangsg.brick.kt.customhandler.KtMyCustomHandler;

import lombok.Data;

@Data
public class KtTest10 {

    private int i1;

    @KtValidateCustom(handleBy = KtMyCustomHandler.class,message = "s1校验规则不正确")
    private String s1;
}
