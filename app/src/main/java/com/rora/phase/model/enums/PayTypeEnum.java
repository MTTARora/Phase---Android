package com.rora.phase.model.enums;

public enum PayTypeEnum {
    NONE(0),
    FREE(1),
    LICENSE_REQUIRED(2),
    INSTANCE_PLAY(3);

    public final int id;

    PayTypeEnum(int id) {
        this.id = id;
    }
}
