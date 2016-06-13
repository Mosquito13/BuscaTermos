package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import configuration.language.Language;
import configuration.language.Languages;
import gui.components.BTButton;
import gui.components.BTComparisonPane;
import gui.components.BTFilterPanel;
import gui.components.BTLabelTextField;
import gui.components.BTLanguagePanel;
import gui.components.BTSplitPane;
import gui.components.BTTextField;
import gui.context.menu.FieldContextMenu;
import parser.LanguageFileParser;
import utils.IconManager;
import utils.Token;

public class LanguagePanel extends BTLanguagePanel implements Runnable {

	private static final long serialVersionUID = 1L;

	private final Language language;
	
	private BTTextField filterField;
	
	private BTSplitPane splitPane;
	
	private Grid grid;
	
	private BTComparisonPane comparisonPane;
	
	public LanguagePanel( final Language language ) {
		this.language = language;

		// FIXME demora pra carregar e app fica meio travado
//		SwingUtilities.invokeLater( this );
		
		// FIXME gambiarra pra corrigir item acima
		if ( language.getId().equals( Languages.BRAZIL ) ) {
			this.add( new BTFilterPanel( this.getFilterField(), this.getClearFilterButton() ), BorderLayout.NORTH );
			this.add( this.getSplitPane() );
		} else {
			new Thread( this ).start();
		}
	}
	
	@Override
	public void run() {
		this.add( new BTFilterPanel( this.getFilterField(), this.getClearFilterButton() ), BorderLayout.NORTH );
		this.add( this.getSplitPane() );
	}

	public BTTextField getFilterField() {
		if ( this.filterField == null ) {
			this.filterField = new BTTextField();
			this.filterField.setComponentPopupMenu( new FieldContextMenu() );
			this.filterField.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void removeUpdate(DocumentEvent e) {
					this.filterGrid();
				}
				
				@Override
				public void insertUpdate(DocumentEvent e) {
					this.filterGrid();
				}
				
				@Override
				public void changedUpdate(DocumentEvent e) {
					this.filterGrid();
				}
				
				private void filterGrid() {
					getGrid().find( filterField.getText() );
				}
			});
		}
		return this.filterField;
	}

	private BTButton getClearFilterButton() {
		final BTButton clearFilterButton = new BTButton( Token.CLEAR, IconManager.getInstance().getIcon( "icons/clear.png" ) );
		clearFilterButton.setPreferredSize( new Dimension( 20, 20 ) );
		clearFilterButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getFilterField().setText( "" );
			}
		});
		return clearFilterButton;
	}

	private BTSplitPane getSplitPane() {
		if ( this.splitPane == null ) {
			this.splitPane = new BTSplitPane( this.getGrid(), this.getLanguageComparisonPane() );
		}
		return splitPane;
	}
	
	private Grid getGrid() {
		if ( this.grid == null ) {
			this.grid = new Grid( this.getLanguage().getId() );
			this.grid.setData( LanguageFileParser.getTerms( this.getLanguage().getFileName() ) );
			this.grid.addColumn( new GridColumn.Builder().width( 50 ).resizable( false ).build() );
			this.grid.addColumn( new GridColumn.Builder().width( 800 ).build() );
			this.grid = this.grid.output();
		}
		return this.grid;
	}
	
	private BTComparisonPane getLanguageComparisonPane() {
		if ( this.comparisonPane == null ) {
			this.comparisonPane = new BTComparisonPane() {
				private static final long serialVersionUID = 1L;
				@Override
				public Dimension getPreferredSize() {
					return new Dimension(this.getWidth(), getSplitPane().isExpanded() ? (int) super.getPreferredSize().getHeight() : 0);
				}
			};
		}
		return this.comparisonPane;
	}
	
	private Language getLanguage() {
		return this.language;
	}
	
	public List<BTLabelTextField> getComparisonFields() {
		return this.getLanguageComparisonPane().getFields();
	}
	
	public List<Object> getGridData() {
		return this.getGrid().getData();
	}
	
}