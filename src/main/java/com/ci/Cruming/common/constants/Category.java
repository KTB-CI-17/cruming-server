package com.ci.Cruming.common.constants;

public enum Category {
    GENERAL,
    PROBLEM;

    public static boolean isProblem(Category category) {
        return category.equals(PROBLEM);
    }

    public static boolean isGeneral(Category category) {
        return category.equals(GENERAL);
    }
}
