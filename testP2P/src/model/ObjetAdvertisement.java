package model;

import net.jxta.document.AdvertisementFactory;
import net.jxta.document.Element;
import net.jxta.document.MimeMediaType;
import net.jxta.id.ID;

public class ObjetAdvertisement extends AbstractAdvertisement<Objet>{

	public ObjetAdvertisement(Element root) {
		super(root);
	}
	
	public ObjetAdvertisement(Objet obj) {
		super();
		if(obj == null) return;
		putValue("titre", obj.getTitre());
		putValue("resume", obj.getResume());
		putValue("desc", obj.getDesc());
		putValue("img", obj.getImg());
		putValue("proposition", Boolean.toString(obj.isProposition()));
		putValue("souhait", Boolean.toString(obj.isSouhait()));
		putValue("troc", Boolean.toString(obj.isTroc()));
		putValue("vente", Boolean.toString(obj.isVente()));
		putValue("date", Long.toString(obj.getDate()));
	}

	@Override
	protected void setKeys() {
		this.addKey("titre", true);
		this.addKey("resume", false);
		this.addKey("desc", false);
		this.addKey("img", false);
		this.addKey("date", false);
		this.addKey("proposition", false);
		this.addKey("souhait", false);
		this.addKey("troc", false);
		this.addKey("vente", false);
		
		// TODO
	}

	@Override
	public ID getID() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static String getAdvertisementType() {
		return "jxta:" + ObjetAdvertisement.class.getName();
	}
	
	public static void register() {
		ObjetAdvertisement adv = new ObjetAdvertisement((Objet)null);
		AdvertisementFactory.registerAdvertisementInstance(ObjetAdvertisement.getAdvertisementType(),
                										   new AdvertisementInstaciator(adv.getClass(), ObjetAdvertisement.getAdvertisementType()));
	}
	
	public static void main(String[] args) {
		Objet obj = new Objet(true, false, true, false, "des patates", "des patates bien fraiches", "<h1>la description</h1>", null, null);
		obj.setDate(System.currentTimeMillis());
		ObjetAdvertisement adv = new ObjetAdvertisement(obj);
		obj = adv.toClass();
		System.out.println(obj.getDesc());
	}

	@Override
	public String getAdvType() {
		return ObjetAdvertisement.getAdvertisementType();
	}

	@Override
	public Objet toClass() {
		Objet obj = new Objet(Boolean.parseBoolean(this.getValue("proposition")), 
				  Boolean.parseBoolean(this.getValue("souhait")), 
				  Boolean.parseBoolean(this.getValue("troc")), 
				  Boolean.parseBoolean(this.getValue("vente")), 
				  this.getValue("titre"), this.getValue("resume"), 
				  this.getValue("desc"), this.getValue("img"), null);
		return obj;
	}

}
