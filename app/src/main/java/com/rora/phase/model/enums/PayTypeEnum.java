package com.rora.phase.model.enums;

public enum PayTypeEnum {
    FREE(1),
    FREE_WITH_OWN_ACCOUNT(2),
    LICENSE_REQUIRED(3),
    INSTANCE_PLAY(4);

    public final int id;

    PayTypeEnum(int id) {
        this.id = id;
    }
}
