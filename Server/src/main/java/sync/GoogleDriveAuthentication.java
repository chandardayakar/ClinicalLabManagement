package sync;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class GoogleDriveAuthentication {

  private static String clientId = "27693571261-ht1q2pnpvdna5ldmgo1uhmvv3d0t8quj.apps.googleusercontent.com";
  private static String redirect_uri = "urn:ietf:wg:oauth:2.0:oob";
  private static String clientSecret = "sfowNOngK1Fu1vAfE-SwsGXc";
  public static String aceToken;
  public static String refToken;
  public static String CODE = null;
  private static StringBuffer fileList;

  public static String generatePermissionUrl() {

    String response_type = "code";
    String approval_prompt = "force";
    String access_type = "offline";
    String scope = "https://www.googleapis.com/auth/drive";
    String endPoint = "https://accounts.google.com/o/oauth2/auth";

    String finalUrl = endPoint + "?" + "client_id=" + clientId + "&response_type=" + response_type
        + "&approval_prompt=" + approval_prompt + "&access_type=" + access_type + "&scope=" + scope
        + "&redirect_uri=" + redirect_uri;
    return finalUrl;
  }

  public static void populateTokens() {
    readTokens();
    if (aceToken != null) {
      return;
    }
    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        String endPoint = "https://accounts.google.com/o/oauth2/token";
        StringBuffer response = new StringBuffer();
        try {
          String body_string = "grant_type=authorization_code" + "&code=" + CODE + "&client_id=" + clientId + "&client_secret=" + clientSecret + "&redirect_uri=" + redirect_uri;
          URL temp = new URL(endPoint);
          HttpURLConnection con = (HttpURLConnection) temp.openConnection();
          con.setRequestMethod("POST");
          con.setDoOutput(true);
          OutputStream token_stream = con.getOutputStream();
          token_stream.write(body_string.getBytes());
          token_stream.flush();
          token_stream.close();
          BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
          String inputLine;
          while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
          }
          JsonParser parser = new JsonParser();
          JsonObject o = (JsonObject) parser.parse(response.toString());
          aceToken = o.get("access_token").getAsString();
          refToken = o.get("refresh_token").getAsString();
          storeTokens();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    t.start();
    try {
      t.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static void refreshToken() {
    readTokens();
    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        String endPoint = "https://accounts.google.com/o/oauth2/token";
        StringBuffer response = new StringBuffer();
        try {
          String body_string = "grant_type=refresh_token&client_id=" + clientId + "&refresh_token=" + refToken + "&redirect_uri=" + redirect_uri;
          URL temp = new URL(endPoint);
          HttpURLConnection con = (HttpURLConnection) temp.openConnection();
          con.setRequestMethod("POST");
          con.setDoOutput(true);
          OutputStream token_stream = con.getOutputStream();
          token_stream.write(body_string.getBytes());
          token_stream.flush();
          token_stream.close();
          BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
          String inputLine;
          while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
          }
          JsonParser parser = new JsonParser();
          JsonObject o = (JsonObject) parser.parse(response.toString());
          aceToken = o.get("access_token").getAsString();
          storeTokens();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    t.start();
    try {
      t.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static void readTokens() {
    Properties prop = new Properties();
    InputStream in = null;
    try {
      GoogleDriveAuthentication _this = new GoogleDriveAuthentication();
      File syncDir = new File(_this.getClass().getResource("Sync").getFile());
      if (!syncDir.exists()) {
        return;
      }
      String filePath = syncDir.getPath() + "/gtokens.properties";
      File tokens = new File(filePath);
      boolean fileExists = tokens.exists();
      if (!fileExists) {
        return;
      }

      in = new FileInputStream(tokens);
      prop.load(in);
      if (prop != null) {

        aceToken = prop.getProperty("GoogleacessToken");
        refToken = prop.getProperty("GooglerefreshToken");
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static void storeTokens() {
    Properties prop = new Properties();
    OutputStream out = null;

    try {

      GoogleDriveAuthentication _this = new GoogleDriveAuthentication();
      File syncDir = new File(_this.getClass().getResource("Sync").getFile());
      if (!syncDir.exists()) {
        syncDir.mkdir();
      }
      String filePath = syncDir.getPath() + "/gtokens.properties";
      File tokens = new File(filePath);
      if (!tokens.exists()) {
        tokens.createNewFile();
      }

      prop.setProperty("GoogleacessToken", aceToken);
      prop.setProperty("GooglerefreshToken", refToken);

      out = new FileOutputStream(filePath);
      prop.store(out, null);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
