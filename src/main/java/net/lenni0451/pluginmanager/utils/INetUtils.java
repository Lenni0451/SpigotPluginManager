package net.lenni0451.pluginmanager.utils;

import net.lenni0451.pluginmanager.Main;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class INetUtils {

    private static final String USER_AGENT = "PluginManager v" + Main.getInstance().getDescription().getVersion() + " by " + Main.getInstance().getDescription().getAuthors().get(0);

    public static HttpURLConnection openConnection(final String url) throws IOException {
        URLConnection con = new URL(url).openConnection();
        con.setRequestProperty("User-Agent", USER_AGENT);
        return (HttpURLConnection) con;
    }

    public static String getContent(final String url) throws IOException {
        HttpURLConnection con = openConnection(url);
        con.setDoInput(true);
        con.connect();

        try {
            if (con.getResponseCode() / 100 != 2) throw new IOException("Response code is not 2xx: " + con.getResponseCode() + " " + con.getResponseMessage());
            InputStream is = con.getInputStream();
            StringBuilder out = new StringBuilder();
            byte[] buf = new byte[4096];
            int len;
            while ((len = is.read(buf)) != -1) out.append(new String(buf, 0, len));
            is.close();
            return out.toString();
        } finally {
            con.disconnect();
        }
    }

}
