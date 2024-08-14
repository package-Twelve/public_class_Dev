package com.sparta.publicclassdev.domain.users.entity;

public enum RankEnum {
    BRONZE(0, 499),
    SILVER(500, 999),
    GOLD(1000, Integer.MAX_VALUE);

    private final int minPoints;
    private final int maxPoints;

    RankEnum(int minPoints, int maxPoints) {
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
    }

    public int getMinPoints() {
        return minPoints;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public static RankEnum getRankByPoints(int points) {
        for (RankEnum rank : RankEnum.values()) {
            if (points >= rank.getMinPoints() && points <= rank.getMaxPoints()) {
                return rank;
            }
        }
        throw new IllegalArgumentException("Invalid points: " + points);
    }
}
