package gui.shortcut;

import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

class Shortcut extends AbstractShortcut {

	private final int key;
	
	private final String action;
	
	private final HashMap<Integer, Boolean> modifier;
	
	private final ArrayList<Integer> availableModifier;
	
	static class Builder {

		private final int key;

		private final String action;
		
		private int[] modifier;
		
		public Builder( final int key, final String action ) {
			this.key = key;
			this.action = action;
		}
		
		public Builder modifier( final int... modifier ) {
			this.modifier = modifier;
			return this;
		}
		
		public Shortcut build() {
			return new Shortcut( this );
		}
	}
	
	private Shortcut( final Shortcut.Builder builder ){
		this.key = builder.key;
		this.action = builder.action;
		
		this.modifier = new HashMap<>();
		this.availableModifier = new ArrayList<>();
		
		this.initModifier();
		
		for ( final Integer modifier : builder.modifier ) {
			if ( !this.availableModifier.contains( modifier ) ) {
				System.err.println( "Não é possível adicionar " + modifier + " como modificador. São aceitos apenas: " + this.availableModifier );
			}
			this.modifier.put( modifier, true );
		}
	}
	
	private void initModifier() {
		this.availableModifier.add( KeyEvent.CTRL_MASK );
		this.availableModifier.add( KeyEvent.SHIFT_MASK );
		
		for ( final Integer modifier : this.availableModifier ) {
			this.modifier.put( modifier, false );
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if ( !this.isModifierPressed( e.getModifiers() ) ) {
			return;
		}

		if ( e.getKeyCode() == this.key ) {
			this.fireAction();
		}
	}

	private boolean isModifierPressed( final int eventModifiers ) {
		for ( final Integer modifier : this.availableModifier ) {
			if ( this.modifier.get( modifier ) && (eventModifiers & modifier) == 0 ) {
				return false;
			}
			
			if ( !this.modifier.get( modifier ) && (eventModifiers & modifier) != 0 ) {
				return false;
			}
		}
		
		return true;
	}

	private void fireAction() {
		try {
			final Method method = this.getClass().getMethod( this.action );
			method.invoke( this );
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
}