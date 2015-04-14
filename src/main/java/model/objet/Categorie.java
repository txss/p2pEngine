package model.objet;

import util.*;

public class Categorie {

	public static enum CATEGORY {
		Appliances,
		Baby,
		Beauty,
		Books,
		ClothingAccessories,
		Clothing,
		Computers,
		FoodAndBeverages,
		Health,
		Home,
		Industrial,
		Jewelry,
		Music,
		MusicalInstruments,
		Office,
		OtherProducts,
		Photography,
		Shoes,
		Sports,
		Tools,
		Toys,
		Vehicles,
		Video,
		VideoGames,
		OtherServices,
		PersonalCaring,
		Teaching,
		Transport
	}; 
	
	private String choix;
	
	public Categorie(Categorie.CATEGORY cat){
		this.choix = Langues.getString("Categorie.categorie."+cat.toString()) ;
	}
	
	public Categorie(String choix){
		boolean temp = false;
		
		for (int i = 0; i < CATEGORY.values().length; i++) {
			if(CATEGORY.values()[i].toString().toLowerCase().equals(choix.toLowerCase())){
				temp = true;
				this.choix = Langues.getString("Categorie.categorie."+choix);
				break;
			}
		}
		if(!temp)
			this.choix = Langues.getString("Categorie.categorie.OtherProducts");
		
	}
	
	public String getChoix() {
		return choix;
	}
	
	public void setChoix(String choix) {
		this.choix = choix;
	}
	
	public static void main(String[] args){
		Categorie test = new Categorie(Categorie.CATEGORY.Baby);
		System.out.println("test 1 : "+test.getChoix());
		
		test = new Categorie("Baby");
		System.out.println("test 2 : "+test.getChoix());

		test = new Categorie("trucs");
		System.out.println("test 3 : "+test.getChoix());
	}
	
}
