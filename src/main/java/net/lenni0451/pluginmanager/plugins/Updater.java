package net.lenni0451.pluginmanager.plugins;

import com.vdurmont.semver4j.Semver;
import net.lenni0451.pluginmanager.utils.GithubUtils;
import org.json.JSONObject;

import java.io.IOException;

public class Updater {

    public UpdateState isUpdateAvailable(final String currentVersion, final String author, final String repo) throws IOException {
        JSONObject latestRelease = GithubUtils.getLatestRelease(author, repo);
        if (!latestRelease.has("tag_name")) return UpdateState.NO_TAG;
        String tagName = latestRelease.getString("tag_name");
        try {
            Semver latestVersion = new Semver(tagName);
            Semver current = new Semver(currentVersion);
            if (current.isGreaterThan(latestVersion)) return UpdateState.UP_TO_DATE;
            return UpdateState.UPDATE_AVAILABLE;
        } catch (Throwable t) {
            if (currentVersion.equals(tagName)) return UpdateState.UP_TO_DATE;
            return UpdateState.UPDATE_AVAILABLE;
        }
    }


    public enum UpdateState {
        UP_TO_DATE,
        UPDATE_AVAILABLE,
        NO_TAG,
    }

}
