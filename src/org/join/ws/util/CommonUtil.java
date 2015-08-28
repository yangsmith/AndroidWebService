package org.join.ws.util;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;
import org.join.ws.WSApplication;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.WindowManager;

/**
 * @brief 閫氱敤宸ュ叿
 * @author join
 */
public class CommonUtil {

    static final class Holder {
        static CommonUtil instance = new CommonUtil();
    }

    public static CommonUtil getSingleton() {
        return Holder.instance;
    }

    private Context mContext;

    private CommonUtil() {
        mContext = WSApplication.getInstance().getBaseContext();
    }

    /**
     * @brief 鍒涘缓鐩綍
     * @param dirPath 鐩綍璺緞
     * @return true: success; false: failure or already existed and not a
     *         directory.
     */
    public boolean makeDirs(String dirPath) {
        File file = new File(dirPath);
        if (file.exists()) {
            if (file.isDirectory()) {
                return true;
            }
            return false;
        }
        return file.mkdirs();
    }

    /**
     * 鑾峰彇鏂囦欢鍚庣紑鍚嶏紝涓嶅甫`.`
     * @param file 鏂囦欢
     * @return 鏂囦欢鍚庣紑
     */
    public String getExtension(File file) {
        String name = file.getName();
        int i = name.lastIndexOf('.');
        int p = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        return i > p ? name.substring(i + 1) : "";
    }

    /**
     * @brief 鍒ゆ柇缃戠粶鏄惁鍙敤
     * @warning need ACCESS_NETWORK_STATE permission
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (null == info) {
            return false;
        }
        return info.isAvailable();
    }

    /**
     * @brief 鑾峰彇褰撳墠IP鍦板潃
     * @return null if network off
     */
    public String getLocalIpAddress() {
        try {
            // 閬嶅巻缃戠粶鎺ュ彛
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                // 閬嶅巻IP鍦板潃
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                        .hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    // 闈炲洖浼犲湴鍧�鏃惰繑鍥�
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @brief 鍒ゆ柇澶栭儴瀛樺偍鏄惁鎸傝浇
     */
    public boolean isExternalStorageMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * @brief 鑾峰彇鏂囦欢绯荤粺璺緞鍐呯殑鍙敤绌洪棿锛屽崟浣峛ytes
     * @param path 鏂囦欢绯荤粺璺緞
     */
    public int getAvailableBytes(String path) {
        StatFs sf = new StatFs(path);
        int blockSize = sf.getBlockSize();
        int availCount = sf.getAvailableBlocks();
        return blockSize * availCount;
    }

    /**
     * @brief 瀛樺偍澶у皬鏍煎紡鍖栦负鍙槄璇荤殑瀛椾覆
     */
    public String readableFileSize(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " "
                + units[digitGroups];
    }

    /**
     * @brief 鍒ゆ柇鏈嶅姟鏄惁杩愯涓�
     * @param servClsName 鏈嶅姟绫诲悕
     * @return 鏄惁杩愯涓�
     */
    public boolean isServiceRunning(String servClsName) {
        ActivityManager mActivityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> mServiceList = mActivityManager
                .getRunningServices(Integer.MAX_VALUE);

        for (RunningServiceInfo servInfo : mServiceList) {
            if (servClsName.equals(servInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @brief 鑾峰彇绐楀彛榛樿鏄剧ず淇℃伅
     */
    public Display getDisplay() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay();
    }

    /**
     * dp -> px
     * @param dipValue dp鍗曚綅鐨勫��
     * @return px鍗曚綅鐨勫��
     */
    public int dp2px(float dipValue) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px - > dp
     * @param pxValue px鍗曚綅鐨勫��
     * @return dp鍗曚綅鐨勫��
     */
    public int px2dp(float pxValue) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /** 
     * sp -> px
     * @param spValue sp鍗曚綅鐨勫��
     * @return px鍗曚綅鐨勫��
     */
    public int sp2px(float spValue) {
        float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * px -> sp
     * @param pxValue px鍗曚綅鐨勫��
     * @return sp鍗曚綅鐨勫��
     */
    public int px2sp(float pxValue) {
        float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /** 骞存湀鏃ユ椂鍒嗙 */
    public final String FORMAT_YMDHMS = "yyyy-MM-dd kk:mm:ss";
    /** 骞存湀鏃� */
    public final String FORMAT_YMD = "yyyy-MM-dd";
    /** 鏃跺垎绉� */
    public final String FORMAT_HMS = "kk:mm:ss";

    /** 鑾峰緱褰撳墠鏃堕棿 */
    public CharSequence currentTime(CharSequence inFormat) {
        return DateFormat.format(inFormat, System.currentTimeMillis());
    }

    /**
     * @brief 妫�鏌ユ湰鍦扮鍙ｆ槸鍚﹁鍗犵敤
     * @param port 绔彛
     * @return true: already in use, false: not.
     */
    public boolean isLocalPortInUse(int port) {
        boolean flag = true;
        try {
            flag = isPortInUse("127.0.0.1", port);
        } catch (Exception e) {
        }
        return flag;
    }

    /**
     * @brief 妫�鏌ヤ富鏈虹鍙ｆ槸鍚﹁鍗犵敤
     * @param host 涓绘満
     * @param port 绔彛
     * @return true: already in use, false: not.
     * @throws UnknownHostException 
     */
    public boolean isPortInUse(String host, int port) throws UnknownHostException {
        boolean flag = false;
        InetAddress theAddress = InetAddress.getByName(host);
        try {
            Socket socket = new Socket(theAddress, port);
            socket.close();
            flag = true;
        } catch (IOException e) {
        }
        return flag;
    }

}
