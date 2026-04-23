package com.global.auth.common.utils;

public class ValidationConstants {
    // 전사 표준 이메일 정규식
    public static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    
    // 전사 표준 휴대폰 번호 정규식 (010-1234-5678 형식)
    public static final String PHONE_REGEX = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$";

    // 비밀번호 정책: 8자 이상, 영문/숫자/특수문자 포함
    public static final String PASSWORD_POLICY_MSG = "비밀번호는 8자 이상이며, 영문, 숫자, 특수문자를 포함해야 합니다.";
}
