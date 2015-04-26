package com.orbit.game.desktop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import javax.swing.SwingConstants;

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
	private final ImageIcon backgroundImage = new ImageIcon("bin/StarBackground.jpg");
	//private final ImageIcon defaultPlanet = new ImageIcon("bin/defaultPlanet_pinkLarge.png");
	ImageIcon planetImage;
	JComboBox jcb;
	JLabel jl, imageLabel;
	JButton jb;
	
	ProfileWindow(Orbit orbit){

		super(orbit);
		planetImage = new ImageIcon("bin/planets/" + orbit.currentUser.planetPath);

		containerPanel = new JPanel(new BorderLayout());		//Initializing
		profilePanel = new JProfilePanel();
		planetPanel = new JPanel();
		inventoryPanel = new JPanel();
		inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.Y_AXIS));
		buttonPanel = new JPanel();

		setupPanels();


		backButton = new JOrbitButton("Back to Lobby");
		buttonPanel.add(Box.createGlue());
		buttonPanel.add(backButton);
		
//		JPanel testPanel = new JPanel();
//		testPanel.add(inventoryPanel);
		containerPanel.add(inventoryPanel, BorderLayout.EAST);
		containerPanel.add(planetPanel, BorderLayout.WEST);

		containerPanel.add(profilePanel, BorderLayout.CENTER);		//add panels to main panel
		containerPanel.add(buttonPanel, BorderLayout.SOUTH);


		add(containerPanel);
		addActionListeners();
		setSize(1024,600);
	}

	private void setupPanels(){
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
		
		File file = new File("bin/planets/");  
		File[] files = file.listFiles();  
		String[] pattern = new String[files.length];
		for (int i = 0; i<files.length; i++){
			pattern[i] = files[i].getName();
		}
		jcb = new JComboBox<String>(pattern);
		jcb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				setImage(itemEvent.getItem().toString());
			}
		});
		planetPanel.add(jcb);
	}
	
	public void setImage(String imagePath){
		orbit.currentUser.planetPath = imagePath;
		planetImage = new ImageIcon("bin/planets/" + imagePath);
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
	

	public void checkMoney(){
		for(WeaponGui wg: weaponPanels){
			if(orbit.currentUser.getMoney() < wg.weapon.damageUpgradeCost) wg.upgradeDamage.setEnabled(false);
			else wg.upgradeDamage.setEnabled(true);
			if(orbit.currentUser.getMoney() < wg.weapon.speedUpgradeCost) wg.upgradeSpeed.setEnabled(false);
			else wg.upgradeDamage.setEnabled(true);
		}

	}


	class JProfilePanel extends JPanel{				//custom Lobby panel with overridden paint component
		private static final long serialVersionUID = 2L;

		JProfilePanel(){					
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
		System.out.println("WeaponGUi created for " + weapon.getName() + "  " + weapon.getWeaponImage());
		this.weapon = weapon;
		this.equipped = equipped;
		this.pw = profile;
		//setPreferredSize(new Dimension(400, 50));
		//this.setMinimumSize(new Dimension(400, 50));
		ImageIcon weaponImage = new ImageIcon("bin/weapons/"+weapon.getWeaponImage());
		JLabel weaponLabel = new JLabel(weapon.getName(), weaponImage, SwingConstants.TRAILING);
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(new GridBagLayout());
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		add(weaponLabel, gbc);
		
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		damageLabel = new JLabel("Damage: " + weapon.getDamage());
		add(damageLabel, gbc);
		
		gbc.gridx++;
		upgradeDamage = new JButton("Upgrade +" + weapon.damageUpgradeAmt +" $" + weapon.damageUpgradeCost);
		add(upgradeDamage, gbc);
		
		gbc.gridy++;
		gbc.gridx = 0;
		speedLabel = new JLabel("Speed: " + weapon.getSpeed());
		add(speedLabel, gbc);
		
		gbc.gridx++;
		upgradeSpeed = new JButton("Upgrade +" + weapon.speedUpgradeAmt +" - $" + weapon.speedUpgradeAmt);
		add(upgradeSpeed, gbc);

		gbc.gridy++;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		equipButton = new JButton("Equip");
		if(equipped) {
			equipButton.setText("Unequip");
		}else{
			equipButton.setText("Equip");
		}
		if(pw.orbit.currentUser.getMoney() < weapon.damageUpgradeCost) upgradeDamage.setEnabled(false);
		if(pw.orbit.currentUser.getMoney() < weapon.speedUpgradeCost) upgradeSpeed.setEnabled(false);

		add(equipButton, gbc);
		pw.checkEquipLimits();
		pw.checkMoney();


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
					damageLabel.setText("Damage: " + weapon.getDamage());

					if(pw.orbit.currentUser.getMoney() < weapon.damageUpgradeCost) upgradeDamage.setEnabled(false);
				
				}
				pw.checkMoney();

			}
		});

		upgradeSpeed.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(pw.orbit.currentUser.getMoney() > weapon.speedUpgradeCost){
					pw.orbit.currentUser.addMoney(-weapon.speedUpgradeCost);
					weapon.upgradeSpeed();
					upgradeSpeed.setText("Upgrade +" + weapon.speedUpgradeAmt +" - $" + weapon.speedUpgradeAmt);
					if(pw.orbit.currentUser.getMoney() < weapon.speedUpgradeCost) upgradeSpeed.setEnabled(false);
				
					speedLabel.setText("Speed: " + weapon.getSpeed());

				}
				pw.checkMoney();

				
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
