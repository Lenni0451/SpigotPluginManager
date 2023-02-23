package net.lenni0451.pluginmanager.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GithubUtils {

    private static final DateTimeFormatter GITHUB_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneId.of("UTC"));

    public static JSONObject getLatestRelease(final String author, final String repo) throws IOException {
        String url = "https://api.github.com/repos/" + author + "/" + repo + "/releases/latest";
        return new JSONObject(new JSONTokener(INetUtils.getContent(url)));
    }

    public static List<Asset> getAssets(final JSONObject release) {
        List<Asset> assets = new ArrayList<>();
        if (!release.has("assets")) return assets;
        JSONArray assetsJson = release.getJSONArray("assets");
        for (Object assetObject : assetsJson) {
            JSONObject asset = (JSONObject) assetObject;
            assets.add(new Asset(asset.getString("name"), asset.getString("browser_download_url"), Instant.from(GITHUB_FORMAT.parse(asset.getString("updated_at")))));
        }
        return assets;
    }


    public static class Asset {
        private final String url;
        private final String name;
        private final Instant timestamp;

        private Asset(final String name, final String url, final Instant timestamp) {
            this.name = name;
            this.url = url;
            this.timestamp = timestamp;
        }

        public String getName() {
            return this.name;
        }

        public String getUrl() {
            return this.url;
        }

        public Instant getTimestamp() {
            return this.timestamp;
        }
    }

}
