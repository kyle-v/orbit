package com.orbit.game.desktop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import orbit.ServerRequest;
import orbit.Weapon;

public class ProfileWindow extends Window{

	private static final long serialVersionUID = 2895673463672850898L;
	private JPanel containerPanel;		//panel to hold button panel and current panel
	private JPanel buttonPanel;
	private JPanel profilePanel;
	private JPanel planetPanel;			//these panels will be the 3 various profile screens
	private JPanel inventoryPanel;

	private Vector<WeaponGui> weaponPanels = new Vector<WeaponGui>();

	private JOrbitButton backButton;
	private final ImageIcon backgroundImage = new ImageIcon("assets/StarBackground.jpg");
	//private final ImageIcon defaultPlanet = new ImageIcon("assets/defaultPlanet_pinkLarge.png");
	ImageIcon planetImage;
	JComboBox jcb;
	JLabel jl, imageLabel;
	JButton jb;
	
	ProfileWindow(Orbit orbit){

		super(orbit);
		planetImage = new ImageIcon("assets/planets/" + orbit.currentUser.planetPath);

		containerPanel = new JPanel(new BorderLayout());		//Initializing
		profilePanel = new JProfilePanel();
		planetPanel = new JPanel();
		inventoryPanel = new JPanel();
		inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.Y_AXIS));
		buttonPanel = new JPanel();

		setupProfilePanel();


		backButton = new JOrbitButton("Back to Lobby");
		buttonPanel.add(Box.createGlue());
		buttonPanel.add(backButton);
		
//		JPanel testPanel = new JPanel();
//		testPanel.add(inventoryPanel);
		containerPanel.add(inventoryPanel, BorderLayout.EAST);
		containerPanel.add(profilePanel, BorderLayout.CENTER);		//add panels to main panel
		containerPanel.add(buttonPanel, BorderLayout.SOUTH);


		add(containerPanel);
		addActionListeners();
		setSize(1024,600);
	}

	private void setupProfilePanel(){
		for(Weapon w : orbit.currentUser.weapons){
			WeaponGui wg;
			if(orbit.currentUser.equippedWeapons.contains(w)){
				wg = new WeaponGui(w, true, this);
			}else{
				wg = new WeaponGui(w, false, this);
			}
			weaponPanels.add(wg);
			inventoryPanel.add(wg);
		}
		
		File file = new File("assets/planets/");  
		File[] files = file.listFiles();  
		String[] pattern = new String[files.length];
		for (int i = 0; i<files.length; i++){
			pattern[i] = files[i].getName();
		}
		jcb = new JComboBox(pattern);
		jcb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				setImage(itemEvent.getItem().toString());
			}
		});
		inventoryPanel.add(jcb);
	}
	
	public void setImage(String imagePath){
		orbit.currentUser.planetPath = imagePath;
		planetImage = new ImageIcon("assets/planets/" + imagePath);
		profilePanel.revalidate();
		profilePanel.repaint();
	}

	private void addActionListeners(){
		backButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.out.println("planet sent: " + orbit.currentUser.planetPath);
				Orbit.sendRequest(new ServerRequest("Update User", orbit.currentUser));
				if(orbit.lobby == null){
					orbit.lobby = new LobbyWindow(orbit);
				}
				orbit.lobby.setVisible(true);
				((LobbyWindow)(orbit.lobby)).startUpdateThread();
				orbit.profile.dispose();
			}
		});
	}
	
	public void checkEquipLimits(){
		if(orbit.currentUser.equippedWeapons.size() >= 3){
			for(WeaponGui wg:  weaponPanels){
				if(!wg.equipped) wg.equipButton.setEnabled(false);
				else wg.equipButton.setEnabled(true);	
			}
		}else if(orbit.currentUser.equippedWeapons.size() <= 1){
			for(WeaponGui wg: weaponPanels){
				if(wg.equipped) wg.equipButton.setEnabled(false);
				else wg.equipButton.setEnabled(true);
			}
		}else{
			for(WeaponGui wg: weaponPanels){
				wg.equipButton.setEnabled(true);
			}
		}
	}


	class JProfilePanel extends JPanel{				//custom Lobby panel with overridden paint component
		private static final long serialVersionUID = 2L;

		JProfilePanel(){					
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		}
		protected void paintComponent(Graphics g){
			g.drawImage(backgroundImage.getImage() ,0,0,null );
			g.drawImage(planetImage.getImage() ,this.getWidth()/2 -110, this.getHeight()/5,null );
		}
	}


}

class WeaponGui extends JPanel{
	private static final long serialVersionUID = 1L;
	Weapon weapon;
	JLabel damageLabel;
	JLabel speedLabel;
	JButton upgradeDamage;
	JButton upgradeSpeed;
	JButton equipButton;
	boolean equipped;

	ProfileWindow pw;
	public WeaponGui (Weapon weapon, boolean equipped, ProfileWindow profile){
		System.out.println("WeaponGUi created for " + weapon.getName());
		this.weapon = weapon;
		this.equipped = equipped;
		this.pw = profile;
		setPreferredSize(new Dimension(200, 70));
		this.setMinimumSize(new Dimension(200, 70));
		ImageIcon weaponImage = new ImageIcon(weapon.getWeaponImage());
		JLabel weaponLabel = new JLabel(weapon.getName(), weaponImage, JLabel.EAST);
		add(weaponLabel);
		damageLabel = new JLabel("Damage: " + weapon.getDamage());
		add(damageLabel);
		upgradeDamage = new JButton("Upgrade +" + weapon.damageUpgradeAmt +" $" + weapon.damageUpgradeCost);
		add(upgradeDamage);

		speedLabel = new JLabel("Speed: " + weapon.getSpeed());
		add(speedLabel);
		upgradeSpeed = new JButton("Upgrade +" + weapon.speedUpgradeAmt +" - $" + weapon.speedUpgradeAmt);
		add(upgradeSpeed);

		equipButton = new JButton("Equip");
		if(equipped) {
			equipButton.setText("Unequip");
		}else{
			equipButton.setText("Equip");
		}
		if(pw.orbit.currentUser.getMoney() < weapon.damageUpgradeCost) upgradeDamage.setEnabled(false);
		if(pw.orbit.currentUser.getMoney() < weapon.speedUpgradeCost) upgradeSpeed.setEnabled(false);

		add(equipButton);
		pw.checkEquipLimits();

		addActionListeners();
		repaint();
	}

	private void addActionListeners(){
		upgradeDamage.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(pw.orbit.currentUser.getMoney() > weapon.damageUpgradeCost){
					pw.orbit.currentUser.addMoney(-weapon.damageUpgradeCost);
					weapon.upgradeDamage();
					upgradeDamage.setText("Upgrade +" + weapon.damageUpgradeAmt +" $" + weapon.damageUpgradeCost);
					if(pw.orbit.currentUser.getMoney() < weapon.damageUpgradeCost) upgradeDamage.setEnabled(false);
				}
			}
		});

		upgradeSpeed.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(pw.orbit.currentUser.getMoney() > weapon.speedUpgradeCost){
					pw.orbit.currentUser.addMoney(-weapon.speedUpgradeCost);
					weapon.upgradeSpeed();
					upgradeSpeed.setText("Upgrade +" + weapon.speedUpgradeAmt +" - $" + weapon.speedUpgradeAmt);
					if(pw.orbit.currentUser.getMoney() < weapon.speedUpgradeCost) upgradeSpeed.setEnabled(false);
				}
			}
		});

		equipButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(equipped){
					//UNEQUIP
					pw.orbit.currentUser.equippedWeapons.remove(weapon);
					equipped = false;
					equipButton.setText("Equip");
				}else{
					//EQUIP
					pw.orbit.currentUser.equippedWeapons.add(weapon);
					equipped = true;
					equipButton.setText("Unequip");
				}
				pw.checkEquipLimits();

			}
		});
	}
}
