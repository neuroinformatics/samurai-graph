package jp.riken.brain.ni.samuraigraph.platform.macosx;

import jp.riken.brain.ni.samuraigraph.application.SGDrawingServer;

import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

public class SGMacOSXAdapter extends ApplicationAdapter
{
    // eawt application
    private static SGMacOSXAdapter	 mAdapter;
    private static com.apple.eawt.Application mApp;

	private SGMacOSXAdapter()
	{
	}

	/**
	 * register macos x application menu handler hooks to exsisting java app handlers
	 * @param ds - main application instance
	 */
	public static void registerMacOSXApplication( )
	{
		if (mApp == null) {
			mApp = new com.apple.eawt.Application();
		}			
		
		if (mAdapter == null) {
			mAdapter = new SGMacOSXAdapter( );
		}
		mApp.addApplicationListener( mAdapter );
		// disable preferecnes
//		enablePrefs(false);
	}
 
	/** Another static entry point for EAWT functionality.  Enables the 
	 *  "Preferences..." menu item in the application menu.
	 * 
	 */
//	public static void enablePrefs(boolean enabled) {
//		if ( mApp == null) {
//			 mApp = new com.apple.eawt.Application();
//		}
//		mApp.setEnabledPreferencesMenu(enabled);
//	}
	
	/**
	 * 
	 */
//	public void handlePreferences( ApplicationEvent e )
//	{
//		if ( mDs != null)
//		{
//		    mDs.preferences();
//			e.setHandled(true);
//		} else {
//			throw new IllegalStateException("handlePreferences: SGDrawingWindow instance detached from listener");
//		}
//	}

	/**
	 * 
	 */
	public void handleAbout( ApplicationEvent e )
	{
		if ( SGDrawingServer.aboutHandler() == false ) {
			throw new IllegalStateException("handlePreferences: SGDrawingServer instance detached from listener");
		}
		e.setHandled( true );
	}
	
	/**
	 * 
	 */
	public void handleQuit( ApplicationEvent e )
	{
		/*	
		/	You MUST setHandled(false) if you want to delay or cancel the quit.
		/	This is important for cross-platform development -- have a universal quit
		/	routine that chooses whether or not to quit, so the functionality is identical
		/	on all platforms.  This example simply cancels the AppleEvent-based quit and
		/	defers to that universal method.
		*/
	    e.setHandled( false );
	    if ( SGDrawingServer.quitHandler() == false ) {
	        throw new IllegalStateException("handleQuit: SGDrawingServer instance detached from listener");
	    }
	}
	
	
	/**
	 * 
	 */
	public void handleOpenFile( ApplicationEvent e )
	{
	    String fname = e.getFilename();
	    if ( SGDrawingServer.openFileHandler( fname ) == false ) {
	        throw new IllegalStateException("handleOpenFile: SGDrawingServer instance detached from listener");
	    }
	    e.setHandled( true );
	}
}
