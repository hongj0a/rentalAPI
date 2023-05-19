package dejay.rnd.billyG.except;


public class AppException extends RuntimeException {
  private ErrCode errCode;
  private Object data = null;

  public AppException(ErrCode errCode) {
    this.errCode = errCode;
  }

  public AppException(ErrCode errCode, Object data) {
    this.errCode = errCode;
    this.data = data;
  }

  public ErrCode getErrCode () {
    return errCode;
  }
  public Boolean isDataAvailable () { return null != data;  }
  public Object getData () {
    return data;
  }
}
