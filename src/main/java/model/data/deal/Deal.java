package model.data.deal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.advertisement.AbstractAdvertisement;
import model.data.item.Item;
import model.data.user.User;

import org.jdom2.Element;

import util.StringToElement;
import util.secure.AVProtocol.Proof;

/**
 * This class can be instantiated for contains an agreement.
 * This class extends AbstractAdvertisement and can be used like an advertisement.
 * @author Michael Dubuis
 *
 */
public class Deal extends AbstractAdvertisement {
	private static final String[] stringState = {"draft", "waiting", "signed", "to sign"};
	
	private String title;			// Title of deal
	private int state = 0;			// State of deal (draft at start)
	private ArrayList<String> signatories = new ArrayList<String>();
	private ArrayList<Item> items = new ArrayList<Item>();
	private HashMap<String, String> rules = new HashMap<String, String>();
	private ArrayList<Claus> clauses = new ArrayList<Claus>();
	private HashMap<String, Proof> proofs = new HashMap<String, Proof>(); // TODO Change when change protocol Sarah
	// TODO add proofs of signature and signatures
	
	///////////////////////////////////////////////// CONSTRUCTORS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	/**
	 * Constructor for new Deal
	 * @param title
	 * @param user
	 */
	public Deal(String title, User user){
		super();
		setState(0);
		addSignatory(user);
		setKeys();
	}
	
	public Deal(String XML){
		super(XML);
		setKeys();
	}
	
	public Deal(Element i) {
		super(i);
	}
	
	@SuppressWarnings("rawtypes")
	public Deal(net.jxta.document.Element e) {
		super(e);
	}
	//////////////////////////////////////////////////// GETTERS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	public String getTitle(){
		return title;
	}
	public ArrayList<String> getSignatories(){
		return signatories;
	}
	public ArrayList<Item> getItems(){
		return items;
	}
	public String getRecipientOf(Item item){
		String itemKey = getItemKey(item);
		return rules.containsKey(itemKey)?rules.get(itemKey):null;
	}
	public ArrayList<Item> getItemsReceivedBy(String publicKey){
		ArrayList<Item> is = new ArrayList<Item>();
		for (Item item : items) {
			String itemKey = getItemKey(item);
			if(rules.containsKey(itemKey) && rules.get(itemKey).equals(publicKey))
				is.add(item);
		}
		return is;
	}
	public String getStateStringFormat(){
		if(state < 0 || state > stringState.length -1){
			printError("getStateStringFormat","Unknown state !");
			return "unknown state";
		}
		return stringState[state];
	}
	public int getState(){
		if(state < 0 || state >= stringState.length){
			printError("getState","Unknown state !");
			return -1;
		}
		return state;
	}
	public boolean isDraft(){
		return state == 0;
	}
	public boolean isWaiting(){
		return state == 1;
	}
	public boolean isSigned(){
		return state == 2;
	}
	public boolean isToSign(){
		return state == 3;
	}
	public ArrayList<Claus> getClauses(){
		return clauses;
	}
	///////////////////////////////////////////////// STATIC GETTERS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	public static String[] getPossiblesStates(){
		return stringState;
	}
	public static String getPossibleState(int i){
		if(i < 0 || i > stringState.length -1){
			printError("getPossibleState","Unknown state !");
			return "unknown state";
		}
		return stringState[i];
	}
	//////////////////////////////////////////////////// SETTERS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	public void setTitle(String title){
		if(!isDraft()){
			printError("setTitle", "This Deal isn't a draft");
			return;
		}
		this.title = title;
	}
	private void setState(int state){
		if(state < 0 || state >= stringState.length){
			printError("setState", "Unknown state !");
			return;
		}
		this.state = state;
	}
	public void draft(){
		this.state = 0;
		// TODO Maybe delete proofs received
	}
	public boolean addSignatory(String publicKey){
		if(!isDraft())
			return printError("addSignatory", "This Deal isn't a draft");
		if(publicKey == null || publicKey.isEmpty())
			return printError("addSignatory", "publicKey Empty !");
		return signatories.add(publicKey);
	}
	public boolean addSignatory(User user){
		if(!isDraft())
			return printError("addSignatory", "This Deal isn't a draft");
		if(user == null)
			return printError("addSignatory", "User empty");
		if(user.getKeys() == null || user.getKeys().getPublicKey() == null)
			return printError("addSignatory", "User have to got PublicKey !");
		return addSignatory(user.getKeys().getPublicKey().toString(16));
	}
	public boolean addItem(Item item){
		if(!isDraft())
			return printError("addItem", "This Deal isn't a draft");
		if(item == null)
			return printError("addItem", "Item empty");
		if(item.getOwner() == null || item.getOwner().isEmpty())
			return printError("addItem", "Item "+item.getTitle()+" doesn't have Owner");
		String owner = item.getOwner();
		if(signatories.contains(owner))
			return signatories.add(owner) && items.add(item);
		return items.add(item);
	}
	public boolean addTransferRule(Item item, String publicKey){
		if(!isDraft())
			return printError("addTransferRule", "This Deal isn't a draft");
		if(item == null || publicKey == null || publicKey.isEmpty())
			return printError("addTransferRule", "Item and PublicKey haven't to be empty");
		if(!signatories.contains(publicKey))
			if(!addSignatory(publicKey))
				return printError("addTransferRule", "Impossible to add publicKey");
		if(!items.contains(item))
			if(!addItem(item))
				return printError("addTransferRule", "Impossible to add Item");
		String itemKey = getItemKey(item);
		if(rules.containsKey(itemKey)){
			printError("addTransferRule", "Rule for Item "+item.getTitle()+" deleted");
			rules.remove(itemKey);
		}
		return rules.put(itemKey, publicKey) != null;
	}
	public boolean addTransferRule(Item item, User user){
		if(!isDraft())
			return printError("addTransferRule", "This Deal isn't a draft");
		if(user == null)
			return printError("addTransferRule", "User empty");
		if(user.getKeys() == null || user.getKeys().getPublicKey() == null)
			return printError("addTransferRule", "User haven't publicKey");
		return addTransferRule(item, user.getKeys().getPublicKey().toString(16));
	}
	private void addTransferRule(String itemKey, String receiver){
		if(!(itemKey.isEmpty() || receiver.isEmpty()))
			rules.put(itemKey, receiver);
	}
	public boolean addClaus(Claus layout){
		if(layout == null)
			return printError("addLayout", "Layout empty");
		return clauses.add(layout);
	}
	//////////////////////////////////////////////////// REMOVER \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	public boolean removeSignatory(String publicKey){
		if(publicKey == null || publicKey.isEmpty())
			return printError("removeSignatory", "publicKey empty");
		for (Item item : items) {
			if(item.getOwner().equals(publicKey)){
				String itemKey = getItemKey(item);
				rules.remove(itemKey);
				items.remove(item);
			}
		}
		for(Iterator<String> itemKey = rules.keySet().iterator(); itemKey.hasNext();){
			if(rules.get(itemKey).equals(publicKey))
				rules.remove(itemKey);
		}
		signatories.remove(publicKey);
		return true;
	}
	public boolean removeSignatory(User user){
		if(user == null)
			return printError("removeSignatory", "User Empty");
		if(user.getKeys() == null || user.getKeys().getPublicKey() == null)
			return printError("removeSignatory", "User have to got a public Key");
		String publicKey = user.getKeys().getPublicKey().toString(16);
		return removeSignatory(publicKey);
	}
	public boolean removeItem(Item item){
		if(item == null)
			return printError("removeItem", "Item empty");
		if(item.getOwner() == null || item.getOwner().isEmpty())
			return printError("removeItem", "Item haven't owner");
		if(item.getTitle() == null || item.getTitle().isEmpty())
			return printError("removeItem", "Item haven't title");
		String itemKey = getItemKey(item);
		return rules.remove(itemKey)!=null && items.remove(item);
	}
	public boolean removeRule(Item item, String publicKey){
		if(item == null)
			return printError("removeRule", "Item empty");
		if(item.getOwner() == null || item.getOwner().isEmpty())
			return printError("removeRule", "Item haven't owner");
		if(item.getTitle() == null || item.getTitle().isEmpty())
			return printError("removeRule", "Item haven't title");
		if(publicKey == null || publicKey.isEmpty())
			return printError("removeRule", "publicKey null or empty");
		String itemKey = getItemKey(item);
		return rules.remove(itemKey)!=null;
	}
	public boolean removeRule(Item item, User user){
		if(user == null)
			return printError("removeRule", "User empty");
		if(user.getKeys() == null || user.getKeys().getPublicKey() == null)
			return printError("removeRule", "User haven't public Key");
		String publicKey = user.getKeys().getPublicKey().toString(16);
		return removeRule(item, publicKey);
	}
	public boolean removeClaus(Claus claus){
		if(claus == null)
			return printError("removeClaus", "Claus empty");
		return clauses.remove(claus);
	}
	//////////////////////////////////////////////////// OTHERS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	private static String getItemKey(Item item){
		return item.getTitle()+":"+item.getOwner();			// TODO CHANGE IF ITEM CHANGE
	}
	//////////////////////////////////////////////////// PRINTER \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	private static boolean printError(String method, String error){
		System.err.println(Deal.class.getName()+"."+method+" : "+error);
		return false;
	}
	
	public String toPrint(){
		return "";
	}
	////////////////////////////////////////////////////// XML \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	private String signatoriesXML(){
		StringBuffer s = new StringBuffer();
		for (String string : signatories) {
			s.append("<signatory>");
			s.append(string);
			s.append("</signatory>");
		}
		return s.toString();
	}
	private String itemsXML(){
		StringBuffer s = new StringBuffer();
		for(Item i: items) {
			s.append(i); 
		}
		return s.toString();
	}
	private String rulesXML(){
		StringBuffer s = new StringBuffer();
		for(Iterator<String> itemKey = rules.keySet().iterator(); itemKey.hasNext();){
			s.append("<rule>");
			s.append("<itemKey>");
			s.append(itemKey);
			s.append("</itemKey>");
			s.append("<receiver>");
			s.append(rules.get(itemKey));
			s.append("</receiver>");
			s.append("</rule>");
		}
		return s.toString();
	}
	private String clausesXML(){
		StringBuffer s = new StringBuffer();
		for(Claus l: clauses) {
			s.append(l); 
		}
		return s.toString();
	}
	private void loadSignatories(Element e){
		Element root = StringToElement.getElementFromString(e.getValue(), e.getName());
		for(Element s: root.getChildren("signatory")) {
			addSignatory(s.getText());
		}
	}
	private void loadItems(Element e){
		Element root = StringToElement.getElementFromString(e.getValue(), e.getName());
		for(Element i: root.getChildren()) {
			addItem(new Item(i));
		}
	}
	private void loadRules(Element e){
		Element root = StringToElement.getElementFromString(e.getValue(), e.getName());
		for (Element r : root.getChildren("rule")) {
			Element itemKeyElement = r.getChild("itemKey");
			Element receiverElement = r.getChild("receiver");
			if(itemKeyElement == null || receiverElement == null)
				continue;
			String itemKey = itemKeyElement.getText();
			String receiver = receiverElement.getText();
			addTransferRule(itemKey, receiver);
		}
	}
	private void loadClauses(Element e){
		Element root = StringToElement.getElementFromString(e.getValue(), e.getName());
		for(Element i: root.getChildren()) {
			switch(i.getChild("Class").getText()){
			// TODO add case for all clauses
			case "model.data.deal.ClausVAT":
				addClaus(new ClausVAT(i));
				break;
			}
		}
	}
	///////////////////////////////////////////////// ADVERTISEMENT \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	@Override
	protected String getAdvertisementName() {
		return Deal.class.getSimpleName();
	}
	@Override
	protected void setKeys() {
		this.addKey("title", 			false);
		this.addKey("state", 			false);
		this.addKey("signatories", 		false);
		this.addKey("items", 			false);
		this.addKey("rules", 			false);
		this.addKey("clauses", 			false);
	}
	@Override
	protected void putValues() {
		this.addValue("title", 			getTitle());
		this.addValue("state", 			String.valueOf(this.getState()));
		this.addValue("signatories", 	this.signatoriesXML());
		this.addValue("items", 			this.itemsXML());
		this.addValue("rules", 			this.rulesXML());
		this.addValue("clauses", 		this.clausesXML());
	}
	@Override
	protected boolean handleElement(Element e) {
		String val = e.getText();
		switch(e.getName()) {
		case "title":					setTitle(val); break;
		case "state":					setState(Integer.parseInt(val)); break;
		case "signatories":				loadSignatories(e); break;
		case "items": 					loadItems(e); break;
		case "rules":	 				loadRules(e); break;
		case "clauses":					loadClauses(e); break;
		default: return false;
		}
		return true;
	}
}