package com.ci.Cruming.common.constants;

public enum Category {
    GENERAL,
    PROBLEM;

    public static boolean isProblem(Category category) {
        return category.equals(PROBLEM);
    }
}
