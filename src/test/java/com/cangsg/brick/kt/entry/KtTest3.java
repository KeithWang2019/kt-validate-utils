package com.cangsg.brick.kt.entry;

import com.cangsg.brick.kt.annotation.KtValidateArrayBound;
import com.cangsg.brick.kt.annotation.KtValidateNotEmpty;

import lombok.Data;

@Data
public class KtTest3 {

    @KtValidateNotEmpty(message = "列表不能为空")
    @KtValidateArrayBound(message = "列表数量不能为空", minCount = 1, maxCount = 2)
    private String[] items;

    @KtValidateNotEmpty(message = "列表2不能为空")
    @KtValidateArrayBound(message = "列表2数量不能为空", minCount = 1, maxCount = 2)
    private KtTest2Item[] items2;
}
