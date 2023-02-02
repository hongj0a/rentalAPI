package dejay.rnd.billyG.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import dejay.rnd.billyG.except.ErrCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class RestApiRes<T> {

//  static private Gson gson = new Gson();
//  static private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
  static private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().serializeNulls().create();

  @Expose
  private String timestamp;

  @Expose
  private Integer status;

  @Expose
  private String error;

  @Expose
  private String message;

  @Expose
  private String path;

  @Expose
  private T data;

  @Expose(serialize = false)
  private HttpStatus httpStatus = HttpStatus.OK;

  @Expose(serialize = false)
  private ErrCode errCode = ErrCode.err_success;

  public RestApiRes(T data) {
    this.data = data;
  }

  public RestApiRes(T data, HttpServletRequest req) {
    this.data = data;
    this.init(req);
  }

  public RestApiRes(ErrCode errCode, T data) {
    this.errCode = errCode;
    this.data = data;
  }

  public RestApiRes(HttpStatus httpStatus, ErrCode errCode) {
    this.httpStatus = httpStatus;
    this.errCode = errCode;
  }

  public RestApiRes(HttpStatus httpStatus, ErrCode errCode, HttpServletRequest req) {
    this.httpStatus = httpStatus;
    this.errCode = errCode;
    this.init(req);
  }

  public RestApiRes(HttpStatus httpStatus, ErrCode errCode, T data) {
    this.httpStatus = httpStatus;
    this.errCode = errCode;
    this.data = data;
  }

  public RestApiRes(HttpStatus httpStatus, ErrCode errCode, T data, HttpServletRequest req) {
    this.httpStatus = httpStatus;
    this.errCode = errCode;
    this.data = data;
    this.init(req);
  }

  // -----

  public void init (HttpServletRequest req) {
    this.setPath(req.getServletPath());
    this.setStatus(this.httpStatus.value());
    this.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    this.setError(this.errCode.code());
    this.setMessage(this.errCode.msg());
  }

  static public <T> JsonObject data (RestApiRes<T> o) {
    JsonObject data = JsonParser.parseString(gson.toJson(o)).getAsJsonObject();
    return data;
  }

  // -----

  static public <T> void write (HttpServletRequest req, HttpServletResponse res, RestApiRes<T> o) throws IOException, ServletException {
    o.setPath(req.getServletPath());
    o.setStatus(o.httpStatus.value());
    o.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    o.setError(o.errCode.code());
    o.setMessage(o.errCode.msg());
    // -----
    res.setStatus(o.httpStatus.value());
    res.addHeader("Content-Type", "application/json; charset=UTF-8");
    res.getWriter().write(gson.toJson(o));
    res.getWriter().flush();
    res.getWriter().close();
  }
}
