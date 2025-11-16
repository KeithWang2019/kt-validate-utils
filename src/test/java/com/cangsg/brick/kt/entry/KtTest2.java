package com.cangsg.brick.kt.entry;

import lombok.Data;

import java.util.List;

import com.cangsg.brick.kt.annotation.KtValidateListBound;
import com.cangsg.brick.kt.annotation.KtValidateNotEmpty;

@Data
public class KtTest2 {

    @KtValidateNotEmpty(message = "列表不能为空")
    @KtValidateListBound(message = "列表数量不能为空", minCount = 1, maxCount = 2)
    private List<String> items;

    @KtValidateNotEmpty(message = "列表2不能为空")
    @KtValidateListBound(message = "列表2数量不能为空", minCount = 1, maxCount = 2)
    private List<KtTest2Item> items2;
}
