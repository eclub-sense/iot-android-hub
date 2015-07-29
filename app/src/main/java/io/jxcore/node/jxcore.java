// License information is available from LICENSE file

package io.jxcore.node;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class jxcore {

  public enum JXType {
    RT_Int32(1), RT_Double(2), RT_Boolean(3), RT_String(4), RT_Object(5), RT_Buffer(
        6), RT_Undefined(7), RT_Null(8), RT_Error(9), RT_Function(10), RT_Unsupported(11);

    int val;

    private JXType(int n) {
      val = n;
    }

    public static JXType fromInt(int n) {
      switch (n) {
      case 1:
        return RT_Int32;
      case 2:
        return RT_Double;
      case 3:
        return RT_Boolean;
      case 4:
        return RT_String;
      case 5:
        return RT_Object;
      case 6:
        return RT_Buffer;
      case 7:
        return RT_Undefined;
      case 8:
        return RT_Null;
      case 9:
        return RT_Error;
      default:
        return RT_Unsupported;
      }
    }
  }

  static {
    System.loadLibrary("jxcore");
  }

  public native void setNativeContext(final Context context,
      final AssetManager assetManager);

  public native int loopOnce();

  public native void startEngine();

  public native void prepareEngine(String home, String fileTree);

  public native void stopEngine();

  public native void defineMainFile(String content);

  public native long evalEngine(String script);

  public native int getType(long id);

  public native double getDouble(long id);

  public native String getString(long id);

  public native byte[] getBuffer(long id);

  public native int getInt32(long id);

  public native int getBoolean(long id);

  public native String convertToString(long id);

  public native long callCBString(String event_name, String param, int is_json);

  public native long callCBArray(String event_name, Object[] arr, int size);

  public static String LOGTAG = "JX-Cordova";
  public static Activity activity = null;
  public static jxcore addon;

  static Map<String, JXcoreCallback> java_callbacks;
  public static Handler handler = null;
  public static boolean app_paused = false;

  public class CoreRunable implements Runnable {
    @Override
    public void run() {
      // TODO Auto-generated method stub
    }

    public String callback_id_;
    public Object[] params_;
    public String str_param_;

    public CoreRunable(String callback_id, String str_param) {
      callback_id_ = callback_id;
      str_param_ = str_param;
    }

    public CoreRunable(String callback_id, Object[] params) {
      callback_id_ = callback_id;
      params_ = params;
    }
  }

  public static void callback(long is_error) {
    Log.e(LOGTAG, "WTF?");
  }

  public static void Prepare() {
    java_callbacks = new HashMap<>();
    JXcoreExtension.LoadExtensions();
    JXMobile.Initialize();
  }

  public interface JXcoreCallback {
    public void Receiver(ArrayList<Object> params, String callbackId);
  }

  public static void javaCall(ArrayList<Object> params) {
    if (params.size() < 2 || params.get(0).getClass() != String.class
        || params.get(params.size() - 1).getClass() != String.class) {
      Log.e(LOGTAG, "JavaCall recevied an unknown call");
      return;
    }

    String receiver = params.remove(0).toString();
    String callId = params.remove(params.size() - 1).toString();

    if (!java_callbacks.containsKey(receiver)) {
      Log.e(LOGTAG, "JavaCall recevied a call for unknown method " + receiver);
      return;
    }

    java_callbacks.get(receiver).Receiver(params, callId);
  }

  public static void RegisterMethod(String name, JXcoreCallback callback) {
    java_callbacks.put(name, callback);
  }

  private static void callJSMethod(String id, Object[] args) {
    long ret = addon.callCBArray(id, args, args.length);
    int tp = addon.getType(ret);
    JXType ret_tp = JXType.fromInt(tp);

    if (ret_tp == JXType.RT_Object || ret_tp == JXType.RT_String
        || ret_tp == JXType.RT_Error) {
      Log.e(LOGTAG, "jxcore.CallJSMethod :" + addon.getString(ret));
    }
  }

  private static void callJSMethod(String id, String args) {
    long ret = addon.callCBString(id, args, 1);
    int tp = addon.getType(ret);
    JXType ret_tp = JXType.fromInt(tp);

    if (ret_tp == JXType.RT_Object || ret_tp == JXType.RT_String
        || ret_tp == JXType.RT_Error) {
      Log.e(LOGTAG, "jxcore.CallJSMethod :" + addon.getString(ret));
    }
  }

  public static boolean CallJSMethod(String id, Object[] args) {
    if (jxcore.handler == null) {
      Log.e(LOGTAG, "JXcore wasn't initialized yet");
      return false;
    }

    if (Looper.myLooper() != Looper.getMainLooper()) {
      activity.runOnUiThread(jxcore.addon.new CoreRunable(id, args) {
        @Override
        public void run() {
          callJSMethod(callback_id_, params_);
        }
      });
    } else {
      callJSMethod(id, args);
    }

    return true;
  }

  public static boolean CallJSMethod(String id, String json) {
    if (jxcore.handler == null) {
      Log.e(LOGTAG, "JXcore wasn't initialized yet");
      return false;
    }

    if (Looper.myLooper() != Looper.getMainLooper()) {
      activity.runOnUiThread(jxcore.addon.new CoreRunable(id, json) {
        @Override
        public void run() {
          callJSMethod(callback_id_, str_param_);
        }
      });
    } else {
      callJSMethod(id, json);
    }

    return true;
  }

  private static void startProgress() {
    // TODO replace activity with that base context, or at lease
    // TODO make it a sane value
    addon.Initialize(activity.getBaseContext().getFilesDir().getAbsolutePath());

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int active = addon.loopOnce();
            final int wait_long = app_paused ? 50 : 5;
            if (active == 0)
                handler.postDelayed(this, wait_long);
            else
                handler.postDelayed(this, 1);
        }
    };

    if (handler != null) {
        handler.getLooper().quit();
    }

    handler = new Handler(activity.getMainLooper());
    handler.postDelayed(runnable, 5);
  }



    public void onStart(boolean multitasking) {
    //super.onPause(multitasking);
    jxcore.CallJSMethod("JXcore_Device_OnPause", "{}");
    app_paused = true;
  }

  public void onStop(boolean multitasking) {
    //super.onResume(multitasking);
    jxcore.CallJSMethod("JXcore_Device_OnResume", "{}");
    app_paused = false;
  }

  private void Initialize(String home) {
    // assets.list is terribly slow, below trick is literally 100 times faster
    StringBuilder assets = new StringBuilder();
    assets.append("{");
    boolean first_entry = true;
    try {
      ZipFile zf = new ZipFile(
          activity.getBaseContext().getApplicationInfo().sourceDir);
      try {
        for (Enumeration<? extends ZipEntry> e = zf.entries(); e
            .hasMoreElements();) {
          ZipEntry ze = e.nextElement();
          String name = ze.getName();
          if (name.startsWith("assets/www/jxcore/")) {
            if (first_entry)
              first_entry = false;
            else
              assets.append(",");
            int size = FileManager.aproxFileSize(name.substring(7));
            assets.append("\"" + name.substring(18) + "\":" + size);
          }
        }
      } finally {
        zf.close();
      }
    } catch (Exception e) {
    }
    assets.append("}");

    prepareEngine(home + "/www/jxcore", assets.toString());

    //String mainFile = FileManager.readFile("jxcore_cordova.js");

    String data = "process.setPaths = function(){ process.cwd = function() { return '" + home
        + "/www/jxcore';};\n" 
        + "process.userPath ='" + activity.getBaseContext().getCacheDir().toString() + "';\n"
        + "};"
        //+ mainFile
            ;
    
    defineMainFile(data);

    startEngine();
  }
}