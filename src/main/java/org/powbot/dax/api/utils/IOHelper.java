package org.powbot.dax.api.utils;

import com.google.gson.JsonObject;
import org.powbot.dax.api.models.ServerResponse;
import org.powbot.mobile.service.DaxProxyService;

import java.io.IOException;
import java.net.HttpURLConnection;

public class IOHelper {

    public static ServerResponse get(String endpoint) throws IOException {
        String resp = DaxProxyService.INSTANCE.executeGetRequest(endpoint);
        if (resp == null) {
            return new ServerResponse(false, -1, null);
        }

        return new ServerResponse(true, HttpURLConnection.HTTP_OK, resp);
    }

    public static ServerResponse post(JsonObject jsonObject, String endpoint) throws IOException {
        String resp = DaxProxyService.INSTANCE.executePostRequest(endpoint, jsonObject.toString());

        if (resp == null) {
            return new ServerResponse(false, -1, null);
        }

        return new ServerResponse(true, HttpURLConnection.HTTP_OK, resp);
    }

}
