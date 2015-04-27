package orbit;

import java.io.Serializable;

public class Trade implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient User seller;
	private transient User buyer;
	
	public String sellerName;
	public String buyerName;
	
	private Weapon offeredWeapon;
	private int moneyRequested;
	
	
	
	
	public Trade(User seller, User buyer, Weapon offeredWeapon,
			int moneyRequested) {
		this.seller = seller;
		this.buyer = buyer;
		this.offeredWeapon = offeredWeapon;
		this.moneyRequested = moneyRequested;
		this.sellerName = seller.getUsername();
		this.buyerName = buyer.getUsername();
	}
	
	
	/**
	 * This method either accepts or denies a trade and transfers the funds and items between users
	 * 
	 * @param accepted whether the buyer accepts the offered trade, or rejects it
	 * @return True if the action was completed successfully, false if the attempted action is not permitted
	 */
	public boolean acceptTrade(boolean accepted){
		
		if(accepted){
			if(buyer.getMoney() < moneyRequested){
				// Buyer does not have the money requested
				System.out.println("Trade cannot be accepted, " + buyer.getUsername() + " does not have enough microns.");
				return false;
			}else{
				System.out.println("Trade was accepted by " + buyer.getUsername() + ".");

				// Transfer weapon
				seller.removeWeapon(offeredWeapon);
				buyer.addWeapon(offeredWeapon);
				
				// Transfer money
				buyer.withdrawMoney(moneyRequested);
				seller.addMoney(moneyRequested);
				
				return true;
			}
		}else{
			System.out.println("Trade was rejected");
			return true;
		}
	}



	public Weapon getOfferedWeapon() {
		return offeredWeapon;
	}


	public int getMoneyRequested() {
		return moneyRequested;
	}
	
}