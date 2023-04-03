package dejay.rnd.billyG.except;

public enum ErrCode {
  err_success                       ("0000", "성공"),
  // -----
  err_api_authentication            ("1000", "인증 오류"),
  err_param_authentication          ("1100", "파라미터 오류"),

  err_file_not_found                ("1200", "File Not found 오류"),
  err_duplicate_file                ("1201", "중복된 파일이 있습니다."),
  err_exist_lead_town               ("1202", "이미 등록된 대표지역이 있습니다"),
  err_over_towns                    ("1203","이미 등록된 관심지역이 10개입니다."),
  err_not_an_positive_integer       ("1300", "양의정수 오류"),
  err_api_duplicate_nickname        ("1400", "중복 닉네임"),
  err_api_is_exist_user              ("1402", "존재하는 회원"),
  err_api_is_delete_user             ("1405", "탈퇴한 회원"),
  err_api_is_new_user              ("1403", "신규 회원"),
  err_api_is_inconsistency         ("1404", "로그인 한 user가 아님"),
  err_api_is_deleted_post          ("1500", "삭제된 게시물"),
  err_api_is_exist_like            ("1501", "이미 좋아요 한 게시물"),
  err_api_is_not_exist_like          ("1502", "좋아요 정보 없음"),
  err_long_time_no_use_user         ("1401", "1년이상 장기 미이용 고객"),
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
