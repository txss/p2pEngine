package view;

import javax.swing.JPanel;
import javax.swing.BoxLayout;

import java.awt.BorderLayout;

import javax.swing.JLayeredPane;
import javax.swing.JTabbedPane;

import java.awt.FlowLayout;

import javax.swing.JButton;

import java.awt.Font;
import java.awt.CardLayout;
import java.awt.GridLayout;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Component;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import model.Advertisable;
import model.Objet;
import model.ObjetsManagement;
import model.SearchListener;
import model.User;
import net.miginfocom.swing.MigLayout;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import com.jgoodies.forms.factories.FormFactory;

import javax.swing.JRadioButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JCheckBox;

public class MainPanel extends JPanel implements SearchListener{
	private JTextField rechercheB;
	private APanel annonceContainer;
	private APanel rechercheContainer;

	/**
	 * Create the panel.
	 */
	public MainPanel() {
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Tahoma", Font.PLAIN, 20));
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab(Messages.getString("MainPanel.tabMesAnnonces.text"), null, panel_2, null);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.Y_AXIS));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		annonceContainer = new APanel();
		annonceContainer.setPreferredSize(new Dimension(0, 0));
		scrollPane_1.setViewportView(annonceContainer);
		annonceContainer.setLayout(new BoxLayout(annonceContainer, BoxLayout.Y_AXIS));
		
		getUserObjet(annonceContainer);
		
		
		panel_2.add(scrollPane_1);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab(Messages.getString("MainPanel.tabRecherche.text"), null, panel, null);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_4 = new JPanel();
		panel_4.setBounds(0, 0, 445, 26);
		panel.add(panel_4, BorderLayout.NORTH);
		
		JPanel panel_5 = new JPanel();
		GroupLayout gl_panel_4 = new GroupLayout(panel_4);
		gl_panel_4.setHorizontalGroup(
			gl_panel_4.createParallelGroup(Alignment.LEADING)
				.addComponent(panel_5, GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
		);
		gl_panel_4.setVerticalGroup(
			gl_panel_4.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_4.createSequentialGroup()
					.addComponent(panel_5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel_5.setLayout(new BoxLayout(panel_5, BoxLayout.X_AXIS));
		
		rechercheB = new JTextField();
		panel_5.add(rechercheB);
		rechercheB.setColumns(10);
		
		JButton btnRechercher = new JButton(Messages.getString("MainPanel.btnRecherche.text"));
		panel_5.add(btnRechercher);
		
		final JCheckBox rdbtnTroc = new JCheckBox(Messages.getString("MainPanel.rbtnTroc.text"));
		rdbtnTroc.setSelected(true);
		panel_5.add(rdbtnTroc);
		
		final JCheckBox rdbtnVente = new JCheckBox(Messages.getString("MainPanel.rbtnVente.text"));
		rdbtnVente.setSelected(true);
		panel_5.add(rdbtnVente);
		panel_4.setLayout(gl_panel_4);
		
		btnRechercher.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.SearchController validator = new controller.SearchController(rechercheB.getText(), rdbtnTroc.isSelected(), rdbtnVente.isSelected(), getThis());
				if(validator.validate()) {
					rechercheContainer.removeAll();
					validator.process();
				}
			}
		
			});
		
		JPanel panel_3 = new JPanel();
		panel_3.setBounds(0, 26, 445, 234);
		panel.add(panel_3);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.Y_AXIS));
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel_3.add(scrollPane);
		
		
		rechercheContainer = new APanel();
		scrollPane.setViewportView(rechercheContainer);
		rechercheContainer.setPreferredSize(new Dimension(0,0));
		rechercheContainer.setLayout(new BoxLayout(rechercheContainer, BoxLayout.Y_AXIS));
		
		
		
		
		setLayout(new CardLayout(0, 0));
		add(tabbedPane, "name_111035141476854");

	}
	
	@Override
	public void revalidate() {
		if(annonceContainer != null) getUserObjet(annonceContainer);
		super.revalidate();
	}
	
	public MainPanel getThis() {
		return this;
	}
	
	private void getUserObjet(APanel panel) {
		User user = Application.getInstance().getUsers().getConnectedUser();
		panel.removeAll();
		
		if(user == null) panel.add(new JLabel("Veuillez vous connecter pour voir vos objets"));
		else if(user.getObjets().size() == 0) panel.add(new JLabel("Aucune annonce n'a \u00E9t\u00E9 ajout\u00E9e pour l'instant"));
		else {
			ObjetsManagement objets = user.getObjets();
			for(int i = 0; i < objets.size(); i++) {
				panel.add(new AnnoncePanel(objets.get(i), i));
			}
		}
	}

	@Override
	public void searchEvent(Advertisable adv) {
		Objet obj = (Objet) adv;
		rechercheContainer.add(new AnnonceRecherchePanel(obj, 0));
	}
}
