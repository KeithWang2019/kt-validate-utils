package com.cangsg.brick.kt.customhandler;

import com.cangsg.brick.kt.annotation.KtValidateCustom;
import com.cangsg.brick.kt.entry.KtCustomValidator;
import com.cangsg.brick.kt.entry.KtValidateResult;

public class KtMyCustomHandler extends KtCustomValidator<String> {

    @Override
    public KtValidateResult<?> call(String val, KtValidateCustom annotation) throws RuntimeException {
        int i1 = this.getPreconditionValue(0, "i1");
        setOneself(val + i1);
        return KtValidateResult.build(annotation.message(), this.getFieldPath());
    }

}
