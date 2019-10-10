
package jp.riken.brain.ni.helper;

import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;


/**
 * A helper application of Samurai Graph application.
 * This application uninstall the old version and install the new version
 * in automatic upgrade of Samurai Graph.
 */
public class UpgradeHelper
{

	/**
	 * The main method.
	 * @param args parameters from a command line
	 */
	public static void main( String[] args )
	{
		boolean status = true;
		if( args.length != 3 ) {
			final String msgBadArgs = "Wrong parameters!";
			JOptionPane.showMessageDialog( null, msgBadArgs );
			status = false;
		} else {
			status = execute( args[0], args[1], args[2] );
		}
		System.exit( ( status ) ? 0 : 1 );
	}

	/**
	 * Execute uninstaller and installer.
	 * @param pathOld A pathname string of the old version
	 * @param pathInst A pathname string of the installer
	 * @param pathNew A pathname string of the new version
	 * @return true:success, false:failure
	 */
	private static boolean execute(
		final String pathOld,
		final String pathInst,
		final String pathNew )
	{
		final String separator = System.getProperty("file.separator");
		final String pathUninst = pathOld + separator + "Uninstall.exe";
		final String msgFailed = "Upgrade is failed for some reason.";
		final String msgFailedExe = "Failed to start the latest version.";
		final String msgSuccess = "Successfully upgraded to the latest version.";
		// final String msgSuccessInst = "Successfully installed the latest version.";
		// final String msgSuccessUninst = "Successfully uninstalled the old version.";
		final String cmdInst = pathInst + " /S /D=" + pathNew;
		final String cmdUninst = pathUninst + " /S _?=" + pathOld;
		final String cmdExe = pathNew + separator + "samurai-graph.exe";

		// check whether installer exists
		File inst = new File( pathInst );
		if( inst.exists() == false ) {
			// installer not found
			JOptionPane.showMessageDialog( null, msgFailed );
			return false;
		}
		inst.deleteOnExit();	// set to be deleted on exit


		// check whether uninstaller exists
		File uninst = new File( pathUninst );
		if( uninst.exists() == false ) {
			// uninstaller not found
			JOptionPane.showMessageDialog( null, msgFailed );
			return false;
		}


		// execute the uninstaller
		try {
			Process p = Runtime.getRuntime().exec( cmdUninst );
			try {
				p.waitFor();
			} catch( InterruptedException ex ) {
			}
			if ( p.exitValue() != 0 ) {
				// aborted to uninstall
				JOptionPane.showMessageDialog( null, msgFailed );
				return false;
			}
		} catch( IOException ex ) {
			// failed to uninstall the old version
			JOptionPane.showMessageDialog( null, msgFailed );
			return false;
		}
		// JOptionPane.showMessageDialog( null, msgSuccessUninst );
		

		// execute installer
		try {
			Process p = Runtime.getRuntime().exec( cmdInst );
			try {
				p.waitFor();
			} catch( InterruptedException ex ) {
			}
			if ( p.exitValue() != 0 ) {
				// aborted to install process
				JOptionPane.showMessageDialog( null, msgFailed );
				return false;
			}
		} catch( IOException ex ) {
			// failed to install the latest version
			JOptionPane.showMessageDialog( null, msgFailed );
			return false;
		}
		// JOptionPane.showMessageDialog( null, msgSuccessInst );
		JOptionPane.showMessageDialog( null, msgSuccess );

		// execute the new version
		try {
			Runtime.getRuntime().exec( cmdExe, null, new File( pathNew ) );
		} catch( IOException ex ) {
			JOptionPane.showMessageDialog( null, msgFailedExe );
			return false;
		}
		return true;
	}
}
