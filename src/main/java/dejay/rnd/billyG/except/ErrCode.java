package dejay.rnd.billyG.except;

public enum ErrCode {
  err_success                       ("0000", "성공"),
  // -----
  err_api_authentication            ("1000", "인증 오류"),
  err_param_authentication          ("1100", "파라미터 오류"),
  err_file_not_found                ("1200", "File Not found 오류"),
  err_duplicate_file                ("1201", "중복된 파일이 있습니다."),
  err_not_an_positive_integer       ("1300", "양의정수 오류"),
  err_api_unsubscribed_user         ("2000", "가입되지 않은 사용자입니다."),
  err_api_incorrect_password        ("2001", "비밀번호 오류"),
  err_api_not_found_token           ("2002", "유효하지 않은 토큰"),
  err_api_expired_token             ("2003", "만료된 토큰"),
  err_unknown_exception             ("9999", "Unhandled Exception");

  private String code;
  private String msg;

  ErrCode(final String code, final String msg) {
    this.code = code;
    this.msg = msg;
  }

  public String code () { return code; }
  public String msg () { return msg; }

}
