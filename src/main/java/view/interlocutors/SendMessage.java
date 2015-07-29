package view.interlocutors;

import java.util.ArrayList;

import model.Application;
import model.data.user.User;
import model.network.search.Search;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import util.VARIABLES;
import controller.MessageController;

public class SendMessage extends AbstractInterlocutor {

	public SendMessage() {
		super();
	}

	@Override
	public void run() {
		if(!isInitialized()) return;
		try {
			JSONObject c = getJSON(content);
			String subject = c.getString("subject");
			String message = c.getString("message");
			String receiver = c.getString("receiver");
			
			JSONObject data = new JSONObject();
			JSONObject content = new JSONObject();
			
			Search<User> search = new Search<User>(Application.getInstance().getNetwork(), User.class.getSimpleName(), "publicKey", true);
			search.search(receiver, VARIABLES.MaxTimeSearch, VARIABLES.ReplicationsAccount);
			
			ArrayList<User> users = search.getResults();
			if(users.isEmpty()) {
				data.put("query", "messageNotSent");
				content.put("feedback", "Account not found on network !");
				data.put("content", content);	
				com.sendText(data.toString());
			}
			long moreRecent = 0L;
			for(User u : users){
				if(moreRecent < u.getLastUpdated())
					moreRecent = u.getLastUpdated();
				else
					users.remove(u);
			}
			for(User u : users){
				if(moreRecent > u.getLastUpdated())
					users.remove(u);
			}
			User u = users.get(0);
			
			MessageController messageController = new MessageController(subject, message, u.getKeys());
			if(messageController.send()){
				data.put("query", "messageSent");
				content.put("feedback", "Message Send to "+u.getNick()+" !");
				data.put("content", content);
				com.sendText(data.toString());
			} else {
				data.put("query", "messageNotSent");
				content.put("feedback", "Message not send !");
				data.put("content", content);	
				com.sendText(data.toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			this.reset();
		}
	}

}
