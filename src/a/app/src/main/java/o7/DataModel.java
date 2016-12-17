package o7;

import android.util.Pair;

import org.json.JSONObject;

class Esi {

    // Main class for one EVE character.
    class Character {
        private int id = 0;
        private String name = "";

        int getId() { return this.id; }
        String getName() { return this.name; }

        Character(int id) {
            this.id = id;
        }
    }

    Pair<HResult,Character[]> QueryCharacters(String pattern) {

        // query list of character IDs that match the pattern.
        String url = "https://esi.tech.ccp.is/latest/search/?search=" + pattern + "&categories=character&language=en-us&strict=false&datasource=tranquility";
        Pair<HResult, JSONObject> response = Helpers.queryJsonObjectFromNetwork(url);
        if (response.first.isFailed()) {
            return new Pair<>(response.first, null);
        }

        // TODO: Query basic character info for each ID.
        return new Pair<>(HResult.E_NOTIMPL, null);
    }
}