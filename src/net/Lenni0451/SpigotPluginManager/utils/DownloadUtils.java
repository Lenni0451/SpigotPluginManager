package net.Lenni0451.SpigotPluginManager.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.Lenni0451.SpigotPluginManager.PluginManager;

public class DownloadUtils {
	
//	private static final Gson gson = new GsonBuilder().create();
	private static final JsonParser jsonParser = new JsonParser();
	
	public static JsonObject getSpigotMcPluginInfo(final int pluginId) throws IOException {
		URL apiUrl = new URL("https://api.spiget.org/v2/resources/" + pluginId);
		HttpsURLConnection connection = (HttpsURLConnection) apiUrl.openConnection();
		connection.setDoInput(true);
		connection.setRequestProperty("user-agent", PluginManager.getInstance().getConfig().getString("UserAgent"));
		
		if(connection.getResponseCode() != 200) {
			return null;
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder responseBuilder = new StringBuilder();
		String line;
		while((line = br.readLine()) != null) {
			responseBuilder.append(line);
		}
		br.close();
		
		return jsonParser.parse(responseBuilder.toString()).getAsJsonObject();
	}
	
	public static boolean downloadSpigotMcPlugin(final int pluginId, final File file) throws IOException {
		HttpsURLConnection.setFollowRedirects(true);
		URL apiUrl = new URL("http://aqua.api.spiget.org/v2/resources/" + pluginId + "/download");
		HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
		connection.setDoInput(true);
		connection.setRequestProperty("user-agent", PluginManager.getInstance().getConfig().getString("UserAgent"));
		
		if(connection.getResponseCode() != 200) {
			System.out.println(connection.getResponseCode());
			return false;
		}
		
		BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
		FileOutputStream fos = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		int length;
		while((length = bis.read(buffer)) != -1) {
			fos.write(buffer, 0, length);
		}
		fos.close();
		bis.close();
		return true;
	}
	
	public static void downloadPlugin(final String url, final File file) throws IOException {
		URL downloadUrl = new URL(url);
		URLConnection connection = downloadUrl.openConnection();
		connection.setDoInput(true);
		connection.setRequestProperty("user-agent", PluginManager.getInstance().getConfig().getString("UserAgent"));
		
		if(connection.getContentLength() <= 0) {
			throw new IOException();
		}
		
		BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
		FileOutputStream fos = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		int length;
		while((length = bis.read(buffer)) != -1) {
			fos.write(buffer, 0, length);
		}
		fos.close();
		bis.close();
	}

	public static JsonArray getSpigotMcPluginList(final int count, final int page) throws IOException {
		URL apiUrl = new URL("https://api.spiget.org/v2/resources?size=" + count + "&page=" + page);
		HttpsURLConnection connection = (HttpsURLConnection) apiUrl.openConnection();
		connection.setDoInput(true);
		connection.setRequestProperty("user-agent", PluginManager.getInstance().getConfig().getString("UserAgent"));
		
		if(connection.getResponseCode() != 200) {
			return null;
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder responseBuilder = new StringBuilder();
		String line;
		while((line = br.readLine()) != null) {
			responseBuilder.append(line);
		}
		br.close();
		
		return jsonParser.parse(responseBuilder.toString()).getAsJsonArray();
	}
	
	public static String getNewestVersion() throws IOException {
		//https://github.com/Lenni0451/SpigotPluginManager/releases/tag/1.0
		URL url = new URL("https://github.com/Lenni0451/SpigotPluginManager/releases/latest");
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setDoInput(true);
		connection.setRequestProperty("user-agent", PluginManager.getInstance().getConfig().getString("UserAgent"));
		
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder responseBuilder = new StringBuilder();
		String line;
		while((line = br.readLine()) != null) {
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
