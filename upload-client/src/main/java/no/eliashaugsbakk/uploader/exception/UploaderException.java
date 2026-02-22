package no.eliashaugsbakk.uploader.exception;

public class UploaderException extends RuntimeException{

  public UploaderException(String message) {
    super(message);
  }

  public UploaderException(String message, Throwable cause) {
    super(message, cause);
  }
}
