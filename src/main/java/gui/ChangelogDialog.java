package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import configuration.Configuration;
import gui.components.BTButton;
import gui.components.BTGridBagConstraints;
import gui.components.BTMainFrame;
import gui.shortcut.ShortcutFactory;
import gui.template.ReleaseView;
import pojo.Release;
import pojo.ReleaseManager;
import utils.Token;
import version.VersionControl;

public class ChangelogDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public ChangelogDialog( final BTMainFrame owner ) {
		super( owner, ModalityType.APPLICATION_MODAL );
		this.setAlwaysOnTop( owner.isAlwaysOnTop() );
		this.addKeyListener( ShortcutFactory.createDisposeWindowShortcut( this ) );
		this.add( this.createComponents() );
		this.showDialog();
	}

	private JPanel createComponents() {
		javax.swing.JPanel container = new JPanel( new BorderLayout() );
		container.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ));
		container.add( this.getTextPane(), BorderLayout.CENTER );
		container.add( this.getBottomPane(), BorderLayout.SOUTH );
		
		return container;
	}
	
	private JPanel getTextPane() {
		JPanel textFieldContainer = new JPanel( new BorderLayout() );
		
		final JScrollPane textWrapper = new JScrollPane( this.getReleaseContainer() );
		textWrapper.getVerticalScrollBar().setUnitIncrement( 12 );
		textWrapper.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		SwingUtilities.invokeLater(new Runnable() {
			public void run() { 
				textWrapper.getVerticalScrollBar().setValue(0);
			}
		});
		
		textFieldContainer.add( textWrapper );
		
		return textFieldContainer;
	}
	
	private JPanel getReleaseContainer() {
		BTGridBagConstraints layout = new BTGridBagConstraints.Builder().weighty( 0d ).insets( new Insets( 0, 10, 0, 10 ) ).build();
		
		JPanel container = new JPanel( new GridBagLayout() );

		for ( Release release : ReleaseManager.getInstance().getSortedReleases() ) {
			container.add( new ReleaseView.Builder().version( release.getVersion().toString() ).description( release.getDescription() ).features( release.getFeatures() ).fixes( release.getFixes() ).build(), layout );
		}
		
		// Correção para não haver espaço entre os releaseview, caso soma das alturas seja menor que o tamanho da tela
		container.add( new JLabel(), new BTGridBagConstraints.Builder().anchor( BTGridBagConstraints.SOUTH ).insets( new Insets( 10, 0, 0, 0 ) ).build() );
		
		return container;
	}
	
	private JPanel getBottomPane() {
		javax.swing.JPanel bottomPane = new JPanel( new BorderLayout() );
		bottomPane.setBorder( BorderFactory.createEmptyBorder( 10, 0, 0, 0 ) );
		bottomPane.add( this.getOkButton() );
		return bottomPane;
	}
	
	private BTButton getOkButton() {
		BTButton okButton = new BTButton( Token.OK );
		okButton.setPreferredSize( new Dimension( 100, 30) );
		okButton.setFocusable( true );
		okButton.addKeyListener( ShortcutFactory.createDisposeWindowShortcut( this ) );
		okButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
		});
		return okButton;
	}
	
	private void showDialog() {
		this.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing( final WindowEvent windowEvent ) {
		    	closeDialog();
		    }
		});
		
		this.setTitle( Token.CHANGELOG );
		this.setResizable( false );
		this.setSize( 500, 500 );
		this.setLocationRelativeTo( this.getOwner() );
		this.setVisible( true );
	}
	
	private void closeDialog() {
		Configuration.getInstance().setVersion( VersionControl.getInstance().getCurrentVersion().toString() );
		this.dispose();
	}
}
