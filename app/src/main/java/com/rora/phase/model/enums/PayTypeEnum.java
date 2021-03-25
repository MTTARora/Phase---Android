package com.rora.phase.model.enums;

public enum PayTypeEnum {
    FREE(1),
    LICENSE_REQUIRED(2),
    INSTANCE_PLAY(3);

    public final int id;

    PayTypeEnum(int id) {
        this.id = id;
    }
}
