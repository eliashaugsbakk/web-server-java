package no.eliashaugsbakk.uploader;

import no.eliashaugsbakk.uploader.controller.CliController;
import no.eliashaugsbakk.uploader.exception.UploaderException;

import java.io.IOException;

public class Main {
  static void main(String[] args) {

    try {
      CliController controller = new CliController();
      controller.execute(args);

    } catch (UploaderException e) {
      System.err.println("\n[!] CONFIGURATION ERROR: " + e.getMessage());
      System.exit(1);

    } catch (IOException e) {
      System.err.println("\n[X] FILE SYSTEM ERROR");
      System.err.println("    Could not read file: " + e.getMessage());
      System.exit(1);

    } catch (Exception e) {
      System.err.println("\n[X] CRITICAL ERROR: " + e.getLocalizedMessage());
      System.exit(1);
    }
  }
}
