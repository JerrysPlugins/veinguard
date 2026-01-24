package com.jerrysplugins.veinguard.core.util;

public enum UpdateType {
    RELEASE(""),
    BETA("-BETA"),
    ALPHA("-ALPHA"),
    PRE_RELEASE("-PRE_RELEASE"),
    SNAPSHOT("-SNAPSHOT"),
    EXPERIMENTAL("-EXPERIMENTAL");

    private final String versionSubString;

    UpdateType(String versionSubString) {
        this.versionSubString = versionSubString;
    }

    public String getVersionSubString() { return this.versionSubString; }
}