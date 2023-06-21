package net.lenni0451.spm.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.lenni0451.spm.PluginManager;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadUtils {

    private static final JsonParser jsonParser = new JsonParser();

    /**
     * Open a connection to an url
     *
     * @param url The url to open
     * @return The opened connection or null if the response code is not 200
     * @throws IOException when the connection could not be opened
     */
    public static HttpURLConnection openConnection(final String url) throws IOException {
        HttpsURLConnection.setFollowRedirects(true);
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setDoInput(true);
        connection.setRequestProperty("user-agent", PluginManager.getInstance().getConfig().getString("UserAgent"));

        if (connection.getResponseCode() != 200) {
            connection.disconnect();
            return null;
        }

        return connection;
    }

    /**
     * Read the bytes from a connection and write them to an output stream
     *
     * @param connection The connection to read from
     * @param os         The output stream to write to
     * @throws IOException when the connection could not be read or the output stream could not be written to
     */
    public static void readWriteBytes(final HttpURLConnection connection, final OutputStream os) throws IOException {
        InputStream is = connection.getInputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) os.write(buffer, 0, length);
        is.close();
    }

    /**
     * Read the bytes from a connection and return them
     *
     * @param connection The connection to read from
     * @return The bytes read from the connection
     * @throws IOException when the connection could not be read
     */
    public static byte[] readBytes(final HttpURLConnection connection) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        readWriteBytes(connection, baos);
        return baos.toByteArray();
    }

    /**
     * Read a string from a connection and return it
     *
     * @param connection The connection to read from
     * @return The string read from the connection
     * @throws IOException when the connection could not be read
     */
    public static String readString(final HttpURLConnection connection) throws IOException {
        StringBuilder responseBuilder = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = br.readLine()) != null) responseBuilder.append(line);
        br.close();
        return responseBuilder.toString();
    }

    /**
     * Get the information about a plugin on spigotmc<br>
     * Please check spiget documentation for more information
     *
     * @param pluginId The id of the plugin
     * @return JsonObject containing all the information
     * @throws IOException when the plugin was not found
     */
    public static JsonObject getSpigotMcPluginInfo(final int pluginId) throws IOException {
        HttpURLConnection connection = openConnection("https://api.spiget.org/v2/resources/" + pluginId);
        if (connection == null) return null;

        String response = readString(connection);
        return jsonParser.parse(response).getAsJsonObject();
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
        HttpURLConnection connection = openConnection("https://api.spiget.org/v2/resources/" + pluginId + "/download");
        if (connection == null) return false;

        FileOutputStream fos = new FileOutputStream(file);
        readWriteBytes(connection, fos);
        fos.close();
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
        HttpURLConnection connection = openConnection(url);
        if (connection == null || connection.getContentLength() <= 0) throw new IOException();

        FileOutputStream fos = new FileOutputStream(file);
        readWriteBytes(connection, fos);
        fos.close();
    }

    /**
     * Download a file from a direkt link
     *
     * @param url THe URL of the file
     * @return The bytes of the file
     * @throws IOException When the URL is invalid
     */
    public static byte[] download(final String url) throws IOException {
        HttpURLConnection connection = openConnection(url);
        if (connection == null || connection.getContentLength() <= 0) throw new IOException();

        return readBytes(connection);
    }

    /**
     * Get the newest plugin manager version from github
     *
     * @return String The newest version of PluginManager
     * @throws IOException When the github url could not be accessed
     */
    public static String getNewestVersion() throws IOException {
        HttpURLConnection connection = openConnection("https://api.github.com/repos/Lenni0451/SpigotPluginManager/releases/latest");
        if (connection == null) throw new IOException();

        String response = readString(connection);
        JsonObject jsonObject = jsonParser.parse(response).getAsJsonObject();
        return jsonObject.get("tag_name").getAsString();
    }

}
