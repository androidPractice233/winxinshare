package com.scut.weixinshare.Exception;

import java.io.IOException;

/**
 * Created by skyluo on 2018/4/12.
 */

public class CryptException extends IOException{
    public String returnMessage;

    @Override
    public String getMessage() {
        return returnMessage;

    }

    public CryptException(String returnMessage){
        this.returnMessage = returnMessage;
    }

}
