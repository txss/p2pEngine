package controller.interlocutors;

import java.io.IOException;

import javax.websocket.Session;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import controller.ManagerBridge;

public class RemoveItemFavorites extends AbstractInterlocutor {

	public RemoveItemFavorites() {
	}

	@Override
	public void sender(String msg, Session session) throws JSONException,
			IOException {
		JSONObject c = getJSON(msg);
		String itemKey = c.getString("itemKey");
		ManagerBridge.removeFavoriteItem(itemKey);
		JSONObject data = new JSONObject();
		data.put("query", "itemFavoritesRemoved");
		JSONObject content = new JSONObject();
		content.put("itemKey", itemKey);
		data.put("content", content);
		session.getBasicRemote().sendText(data.toString());
	}

}
