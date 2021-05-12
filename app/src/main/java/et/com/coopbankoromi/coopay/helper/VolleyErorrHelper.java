package et.com.coopbankoromi.coopay.helper;

import android.content.Context;

import com.android.volley.TimeoutError;

import et.com.coopbankoromi.coopay.R;

public class VolleyErrorHelper {
    /**
     * Returns appropriate message which is to be displayed to the user
     * against the specified error object.
     *
     * @param error
     * @param context
     * @return
     */

    public static String getMessage(Object error, Context context) {
        if (error instanceof TimeoutError) {
            return context.getResources().getString(R.string.timeout);
        } else if (isServerProblem(error)) {
            return handleServerError(error, context);

        } else if (isNetworkProblem(error)) {
            return context.getResources().getString(R.string.nointernet);
        }
        return context.getResources().getString(R.string.generic_error);

    }

    private static String handleServerError(Object error, Context context) {

        VolleyError er = (VolleyError) error;
        NetworkResponse response = er.networkResponse;
        if (response != null) {
            switch (response.statusCode) {

                case 404:
                case 422:
                case 401:
                    try {
                        // server might return error like this { "error": "Some error occured" }
                        // Use "Gson" to parse the result
                        HashMap<String, String> result = new Gson().fromJson(new String(response.data),
                                new TypeToken<Map<String, String>>() {
                                }.getType());

                        if (result != null && result.containsKey("error")) {
                            return result.get("error");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // invalid request
                    return ((VolleyError) error).getMessage();

                default:
                    return context.getResources().getString(R.string.timeout);
            }
        }

        return context.getResources().getString(R.string.generic_error);
    }

    private static boolean isServerProblem(Object error) {
        return (error instanceof ServerError || error instanceof AuthFailureError);
    }

    private static boolean isNetworkProblem(Object error) {
        return (error instanceof NetworkError || error instanceof NoConnectionError);
    }

