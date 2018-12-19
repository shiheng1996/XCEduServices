package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常捕获类
 */
@RestControllerAdvice
public class ExceptionCatch {
    private final  static Logger LOGGER= LoggerFactory.getLogger(ExceptionCatch.class);
    //使用EXCEPTIONS存放异常类型和错误代码的映射  ImmutableMap的特点是一旦被创建就不可改变,并且线程安全
   private static ImmutableMap<Class<? extends Throwable>,ResultCode>  EXCEPTIONS;

   //使用builder来构建一个异常类型和错误代码的异常
   protected static ImmutableMap.Builder<Class<? extends Throwable>,ResultCode> builder=ImmutableMap.builder();

    @ExceptionHandler(Exception.class)
    public ResponseResult exception(Exception e){
        LOGGER.error("catch exception: {}\r\nexception:",e.getMessage(),e);
        if (EXCEPTIONS==null){
            EXCEPTIONS= builder.build();
        }
        final ResultCode resultCode=EXCEPTIONS.get(e.getClass());
        final ResponseResult responseResult;
        //从EXCEPTIONS中找异常类型所对应的错误代码，如果找到了将错误代码响应给用户，如果找不到给用户响应99999异常
        if (resultCode!=null){
            responseResult=new ResponseResult(resultCode);
        }else {
            responseResult=new ResponseResult(CommonCode.SERVER_ERROR);
        }
        return responseResult;
    }

    /**
     * 捕获自定义异常  如果excetionHandler中没有传入自定义异常则表示捕获所有异常
     * @param e
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public ResponseResult customException(CustomException e){
        LOGGER.error("catch exception : {}/r/nexception: ",e.getMessage(),e);
        return new ResponseResult(e.getResultCode());
    }
  static {
        //在这里加入一些基础的异常类型判断  加入非法参数异常
      builder.put(HttpMessageNotReadableException.class,CommonCode.INVALID_PARAM);

  }


}
