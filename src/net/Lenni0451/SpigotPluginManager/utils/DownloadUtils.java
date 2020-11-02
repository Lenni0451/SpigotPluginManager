package net.Lenni0451.SpigotPluginManager.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.Lenni0451.SpigotPluginManager.PluginManager;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class DownloadUtils {

    //	private static final Gson gson = new GsonBuilder().create();
    private static final JsonParser jsonParser = new JsonParser();

    /**
     * Get the information about a plugin on spigotmc<br>
     * Please check spiget documentation for more information
     *
     * @param pluginId The id of the plugin
     * @return JsonObject containing all the information
     * @throws IOException when the plugin was not found
     */
    public static JsonObject getSpigotMcPluginInfo(final int pluginId) throws IOException {
        URL apiUrl = new URL("https://api.spiget.org/v2/resources/" + pluginId);
        HttpsURLConnection connection = (HttpsURLConnection) apiUrl.openConnection();
        connection.setDoInput(true);
        connection.setRequestProperty("user-agent", PluginManager.getInstance().getConfig().getString("UserAgent"));

        if (connection.getResponseCode() != 200) {
            return null;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            responseBuilder.append(line);
        }
        br.close();

        return jsonParser.parse(responseBuilder.toString()).getAsJsonObject();
    }

    /**
     * Download a plugin from the spiget api
     *
     * @param pluginId The plugin id
     * @param file     where to save the plugin
     * @return boolean if the plugin was found
     * @throws IOException when the plugin could not be saved
     */
    public static boolean downloadSpigotMcPlugin(final int pluginId, final File file) throws IOException {
        HttpsURLConnection.setFollowRedirects(true);
        URL apiUrl = new URL("http://aqua.api.spiget.org/v2/resources/" + pluginId + "/download");
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        connection.setDoInput(true);
        connection.setRequestProperty("user-agent", PluginManager.getInstance().getConfig().getString("UserAgent"));

        if (connection.getResponseCode() != 200) {
            return false;
        }

        BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
        FileOutputStream fos = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = bis.read(buffer)) != -1) {
            fos.write(buffer, 0, length);
        }
        fos.close();
        bis.close();
        return true;
    }

    /**
     * Download a plugin from a direct link
     *
     * @param url  The URL of the plugin
     * @param file where to save the plugin
     * @throws IOException when the url is invalid/the plugin could not be found
     */
    public static void downloadPlugin(final String url, final File file) throws IOException {
        URL downloadUrl = new URL(url);
        URLConnection connection = downloadUrl.openConnection();
        connection.setDoInput(true);
        connection.setRequestProperty("user-agent", PluginManager.getInstance().getConfig().getString("UserAgent"));

        if (connection.getContentLength() <= 0) {
            throw new IOException();
        }

        BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
        FileOutputStream fos = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = bis.read(buffer)) != -1) {
            fos.write(buffer, 0, length);
        }
        fos.close();
        bis.close();
    }

    /**
     * Get the newest plugin manager version from github
     *
     * @return String The newest version of PluginManager
     * @throws IOException When the github url could not be accessed
     */
    public static String getNewestVersion() throws IOException {
        URL url = new URL("https://github.com/Lenni0451/SpigotPluginManager/releases/latest");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setRequestProperty("user-agent", PluginManager.getInstance().getConfig().getString("UserAgent"));

        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            responseBuilder.append(line);
        }
        br.close();

        String urlBase = "https://github.com/Lenni0451/SpigotPluginManager/releases/tag/";
        String source = responseBuilder.toString();
        String version = source.substring(source.indexOf(urlBase) + urlBase.length());
        version = version.substring(0, version.indexOf("&"));

        return version;
    }

}
