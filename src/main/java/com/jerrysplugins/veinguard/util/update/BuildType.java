package com.jerrysplugins.veinguard.util.update;

public enum BuildType {

    LATEST("", "Latest"),
    BETA("-BETA", "Beta"),
    ALPHA("-ALPHA", "Alpha"),
    PRE_RELEASE("-PRE_RELEASE", "Pre-Release"),
    SNAPSHOT("-SNAPSHOT", "Snapshot"),
    EXPERIMENTAL("-EXPERIMENTAL", "Experimental"),
    UNKNOWN("", "Unknown");

    private final String versionSubString;
    private final String displayName;

    BuildType(String versionSubString, String displayName) {
        this.versionSubString = versionSubString;
        this.displayName = displayName;
    }

    public String getVersionSubString() {
        return this.versionSubString;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public static BuildType fromVersionSubString(String versionSubString) {

        if (versionSubString == null || versionSubString.isBlank()) {
            return LATEST;
        }

        String normalized = versionSubString.toUpperCase();

        if (normalized.contains(EXPERIMENTAL.versionSubString)) {
            return EXPERIMENTAL;
        }

        if (normalized.contains(SNAPSHOT.versionSubString)) {
            return SNAPSHOT;
        }

        if (normalized.contains(PRE_RELEASE.versionSubString)) {
            return PRE_RELEASE;
        }

        if (normalized.contains(ALPHA.versionSubString)) {
            return ALPHA;
        }

        if (normalized.contains(BETA.versionSubString)) {
            return BETA;
        }

        return UNKNOWN;
    }
}