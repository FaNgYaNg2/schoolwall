package com.fy.schoolwall.common.enums;

/**
 * 帖子分类枚举
 */
public enum PostCategory {
    ACADEMIC("学术交流", "academic"),
    CAMPUS_LIFE("校园生活", "campus_life"),
    CLUB_ACTIVITY("社团活动", "club_activity"),
    JOB_INTERN("求职实习", "job_intern"),
    SECONDHAND("二手交易", "secondhand"),
    LOST_FOUND("失物招领", "lost_found"),
    DORMITORY("宿舍生活", "dormitory"),
    DINING("饮食推荐", "dining"),
    STUDY_GROUP("学习小组", "study_group"),
    COURSE_REVIEW("课程评价", "course_review"),
    SCHOLARSHIP("奖学金", "scholarship"),
    COMPETITION("竞赛信息", "competition"),
    VOLUNTEER("志愿活动", "volunteer"),
    SPORTS("体育运动", "sports"),
    ENTERTAINMENT("娱乐休闲", "entertainment"),
    TRAVEL("旅游出行", "travel"),
    EMOTIONAL("情感交流", "emotional"),
    ANONYMOUS("匿名树洞", "anonymous"),
    NOTICE("通知公告", "notice"),
    OTHER("其他", "other");

    private final String displayName;
    private final String code;

    PostCategory(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return code;
    }

    /**
     * 根据code获取枚举
     */
    public static PostCategory fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (PostCategory category : values()) {
            if (category.code.equals(code)) {
                return category;
            }
        }
        return null;
    }

    /**
     * 根据显示名称获取枚举
     */
    public static PostCategory fromDisplayName(String displayName) {
        if (displayName == null) {
            return null;
        }
        for (PostCategory category : values()) {
            if (category.displayName.equals(displayName)) {
                return category;
            }
        }
        return null;
    }

    /**
     * 验证分类是否有效
     */
    public static boolean isValid(String category) {
        return fromCode(category) != null || fromDisplayName(category) != null;
    }
}