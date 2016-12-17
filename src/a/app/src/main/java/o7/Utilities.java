package o7;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.JsonReader;
import android.util.Pair;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

class HeightAlignedImageView extends ImageView {
    public HeightAlignedImageView(Context context) { super(context); }
    public HeightAlignedImageView(Context context, AttributeSet attrs) { super(context, attrs); }
    public HeightAlignedImageView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        if (w != h) {
            setMeasuredDimension(h, h);
        }
    }
}

// Not exactly the win32 HRESULT but similar concept that can program execution result
// is represented by a integer code and optionally a string. Negative value means failure,
// non-negative values means success.
class HResult {
    private int code;
    private String message;

    static HResult S_OK = new HResult(0, null);
    static HResult E_NOTIMPL = new HResult((int)0x80004001, "E_NOTIMPL");
    static HResult E_FAIL    = new HResult((int)0x80004005, "E_FAIL");

    HResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    HResult(Exception ex) {
        this.code = E_FAIL.code;
        this.message = ex.toString();
    }

    boolean isSucceeded() { return this.code >= 0; }

    boolean isFailed() { return this.code < 0; }

    @Override
    public String toString() {
        if (null != this.message) {
            return this.message;
        }

        if (this.code < 0) {
            return String.format("FAILED (hr = 0x%X)", this.code);
        }

        return "";
    }
}

class Helpers {
    /**
     * Query json object from network. Can't be called directly from UI thread.
     */
    static Pair<HResult,JSONObject> queryJsonObjectFromNetwork(String urlString) {
        try {
            URL url = new URL(urlString);
            String response = readUrlResponseString(url);
            JSONTokener tokener = new JSONTokener(response);
            JSONObject jsonObject = (JSONObject)tokener.nextValue();
            return new Pair<>(HResult.S_OK, jsonObject);
        } catch (Exception ex) {
            return new Pair<>(new HResult(ex), null);
        }
    } // end of GetJsonFromUrl function

    static String readUrlResponseString(URL url)
    {
        try (
                InputStream is = url.openStream();
                BufferedInputStream reader = new BufferedInputStream(is)) {

            byte[] buffer = new byte[1024];
            StringBuilder sb = new StringBuilder();
            int count;
            while((count = reader.read(buffer)) > 0) {
                String s = new String(buffer, 0, count);
                sb.append(s);
            }
            return sb.toString();
        }
        catch (Exception ex)
        {
            return ex.toString();
        }
    }
} // end of Helpers class
