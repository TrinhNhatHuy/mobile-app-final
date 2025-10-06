package vn.edu.usth.mobilefinal.network;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.StringRequest;


public class NetworkHelper {
    private static NetworkHelper instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private NetworkHelper(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized NetworkHelper getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkHelper(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public void addToRequestQueue(Request request) {
        getRequestQueue().add(request);
    }

    public interface VolleyCallback {
        void onSuccess(String result);
        void onError(String error);
    }

    public void getArtworks(String url, VolleyCallback callback) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> callback.onSuccess(response),
                error -> callback.onError(error.toString()));
        addToRequestQueue(stringRequest);
    }
} //Đây chỉ là tạo một object StringRequest, nó chưa gửi request.
//Bạn chỉ đang khai báo “tôi muốn gửi GET request tới url này và xử lý response bằng callback này”.
//Tức là chưa có dữ liệu artwork nào được lấy cả, chỉ mới định nghĩa request.


//Quản lý RequestQueue (hàng đợi các yêu cầu HTTP).
//Thực hiện các request GET/POST (ở đây là getArtworks dùng GET).
//Trả kết quả về cho Activity hoặc Fragment thông qua callback (onSuccess, onError).
//Sử dụng singleton pattern để chỉ tạo 1 instance cho toàn app, tiết kiệm bộ nhớ.
//NetworkHelper giữ tất cả request trong một queue duy nhất (RequestQueue) cho toàn app.
//Volley sẽ lấy request từ queue, gửi HTTP request đến server, nhận response, rồi gọi callback.
//Bạn không cần lo việc quản lý nhiều request, NetworkHelper + Volley đã làm tất cả.