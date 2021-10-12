package com.zjb.rpcdemo.message;

public class RpcResponseMessage extends Message {
    /**
     * 返回值
     */
    private Object returnValue;
    /**
     * 异常值
     */
    private Throwable exceptionValue;

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }
    
    
    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public void setExceptionValue(Throwable exceptionValue) {
        this.exceptionValue = exceptionValue;
    }
    
     public Object getReturnValue() {
        return returnValue;
    }

    public Throwable getExceptionValue() {
        return exceptionValue;
    }
}