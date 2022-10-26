package org.powbot.dax.api.utils;

import com.google.gson.JsonObject;
import org.powbot.dax.api.models.ServerResponse;
import org.powbot.mobile.service.DaxProxyService;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

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
