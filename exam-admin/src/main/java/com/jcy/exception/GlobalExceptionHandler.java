package com.jcy.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Objects;
import java.util.Set;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    // BindException（@Validated @Valid 仅对于表单提交有效，对于以json格式提交将会失效）

    /**
     * 参数校验异常处理
     * == 对于标记了@RequestBody的出现校验异常都会经此方法处理 ==
     *
     * @param ex 异常拦截
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestErrorResponse handle(MethodArgumentNotValidException ex) {
        String field = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getField();
        String message = ex.getBindingResult().getFieldError().getDefaultMessage();
        return new RestErrorResponse(HttpStatus.BAD_REQUEST.toString(), field + message);
    }

    /**
     * jsr规范中的验证异常
     * == 对于@RequestParams出现的校验异常经此方法处理 ==
     *
     * @param ex 异常拦截
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public RestErrorResponse handle(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();

        StringBuilder message = new StringBuilder();
        for (ConstraintViolation<?> constraint : violations) {
            message.append(constraint.getMessage())
                    .append(";");
//            break;
        }
        return new RestErrorResponse(HttpStatus.BAD_REQUEST.toString(), message.toString());
    }


    // 捕获Exception异常
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse processException(HttpServletRequest request,
                                              HttpServletResponse response,
                                              Exception e) {
        // 解析异常信息
        // 如果是系统自定义异常，直接取出errCode和errMessage
        if (e instanceof BusinessException) {
            LOGGER.info(e.getMessage(), e);
            // 解析系统自定义异常信息
            BusinessException businessException = (BusinessException) e;
            ErrorCode errorCode = businessException.getErrorCode();
            // 错误代码
            int code = errorCode.getCode();
            // 错误信息
            String desc = errorCode.getDesc();
            return new RestErrorResponse(String.valueOf(code), desc);
        }

        LOGGER.error("系统未知异常：", e);
        // 统一定义为99999系统未知错误
        return new RestErrorResponse(String.valueOf(CommonErrorCode.UNKNOWN.getCode()), e.getMessage() != null ? e.getMessage() : CommonErrorCode.UNKNOWN.getDesc());
    }
}
