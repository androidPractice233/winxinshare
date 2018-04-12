package com.scut.weixinshare.model;
/** 
* @author hts
* @version date：2017年10月16日 下午9:36:21 
* 
*/
public class ResultBean<T> {
private Integer status=200;

//    error_msg 错误信息，若status为200时，为正常，500时为服务器错误
private String msg;

//    content 返回体报文的出参，使用泛型兼容不同的类型
private T data;

public Integer getStatus() {
    return status;
}

public void setStatus(Integer code) {
    this.status = code;
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
            "status=" + status +
            ", msg='" + msg + '\'' +
            ", data=" + data +
            '}';
}
}
