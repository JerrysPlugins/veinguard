package com.jerrysplugins.veinguard.util.update;

public enum VersionStatus {
    UP_TO_DATE,   // Current version matches latest
    BEHIND,       // Current version is older than latest
    AHEAD         // Current version is newer than latest (developmental)
}