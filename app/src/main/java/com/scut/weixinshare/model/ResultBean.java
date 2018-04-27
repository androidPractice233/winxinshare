package com.scut.weixinshare.model;
/** 
* @author hts
* @version date：2017年10月16日 下午9:36:21 
* 
*/
public class ResultBean<T> {
private Integer code=200;

//    error_msg 错误信息，若status为200时，为正常，500时为服务器错误
private String msg;

//    content 返回体报文的出参，使用泛型兼容不同的类型
private T data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
    return msg;
}

public void setMsg(String msg) {
    this.msg = msg;
}

public T getData(Object object) {
    return data;
}

public void setData(T data) {
    this.data = data;
}

public T getData() {
    return data;
}

@Override
public String toString() {
    return "Result{" +
            "data=" + data +
            ", msg='" + msg + '\'' +
            ", data=" + data +
            '}';
}
}
