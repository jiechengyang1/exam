package com.jcy.exception;
//错误代码
public enum CommonErrorCode implements ErrorCode {

    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "权限不足"),

    E_100101(100101, "账号密码错误, 或用户被封禁"),
    E_100102(100102, "用户不存在!"),
    E_100103(100103, "用户名已存在!"),
    E_100104(1000104, "验证码输入错误!"),
    E_100105(1000105, "操作用户的模式非法!"),
    E_100106(1000106, "操作考试的模式非法!"),

    E_200001(200001, "token异常"),

    E_300001(300001, "发布新公告异常"),
    E_300002(300002, "修改公告异常"),

    E_400001(400001, "考试不存在"),

    /**
     * 切面类错误
     */
    E_800001(800001, "目标方法返回null"),

    /**
     * 未知错误
     */
    UNKNOWN(999999, "未知错误");


    private final int code;

    private final String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    CommonErrorCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public static CommonErrorCode setErrorCode(int code) {
        for (CommonErrorCode errorCode : CommonErrorCode.values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return null;
    }
}
