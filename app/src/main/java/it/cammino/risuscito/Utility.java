package it.cammino.risuscito;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;

import it.cammino.utilities.systembartint.SystemBarTintManager;

public class Utility {
	
	//Costanti per le impostazioni
	public static final String SCREEN_ON = "sempre_acceso";
	public static final String SHOW_SECONDA = "mostra_seconda_lettura";
	public static final String SAVE_LOCATION = "memoria_salvataggio_scelta";
	public static final String DEFAULT_INDEX = "indice_predefinito"; 
	
	public static final int DISMISS = 1;
	public static final int RENAME_CONFERMA = 2;
	public static final int AGGIUNGI_CONFERMA = 3;
	public static final int SAVE_LIST_OK = 4;
	public static final int SAVE_LIST_KO = 5;
	public static final int ALPHA_LISTAPERS_OK = 6;
	public static final int ALPHA_LISTAPRED_OK = 7;
	public static final int ARG_LISTAPERS_OK = 8;
	public static final int ARG_LISTAPRED_OK = 9;
	public static final int NUM_LISTAPERS_OK = 10;
	public static final int NUM_LISTAPRED_OK = 11;
	public static final int SAL_LISTAPERS_OK = 12;
	public static final int SAL_LISTAPRED_OK = 13;
	public static final int EUCAR_RESET_OK = 14;
	public static final int PAROLA_RESET_OK = 15;
	public static final int PERS_RESET_OK = 16;
	public static final int VELOCE_LISTAPERS_OK = 17;
	public static final int VELOCE_LISTAPRED_OK = 18;
	public static final int AVANZATA_LISTAPERS_OK = 19;
	public static final int AVANZATA_LISTAPRED_OK = 20;
	public static final int PREFERENCE_DEFINDEX_OK = 21;
	public static final int PREFERENCE_SAVELOC_OK = 22;
	public static final int ADD_LIST_OK = 23;
	public static final int DOWNLOAD_CANCEL = 24;
	public static final int DOWNLOAD_OK = 25;
	public static final int DOWNLOAD_LINK = 26;
	public static final int DELETE_MP3_OK = 27;
	public static final int DELETE_LINK_OK = 28;
	public static final int DELETE_ONLY_LINK_OK = 29;
	public static final int SAVE_TAB_OK = 30;
	public static final int DISMISS_EXIT = 31;
    public static final int DISMISS_RENAME = 32;
    public static final int DISMISS_ADD = 33;

    //Costanti per il passaggio dati alla pagina di visualizzazione canto in fullscreen
    public static final String URL_CANTO = "urlCanto";
    public static final String SPEED_VALUE = "speedValue";
    public static final String SCROLL_PLAYING = "scrollPlaying";
    public static final String ID_CANTO = "idCanto";
    public static final String TAG_TRANSIZIONE = "fullscreen";
    public static final String TRANS_PAGINA_RENDER = "paginarender";
	
    @SuppressLint("NewApi")
	public static void setAccessibilityIgnore(View view) {
        view.setClickable(false);
        view.setFocusable(false);
        view.setContentDescription("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        }
    }
	
    //metodo che restituisce la stringa di input senza la pagina all'inizio
    public static String truncatePage(String input) {
    	
    	int length = input.length();
    	int start;
    	
    	for (start = 0; start < length; start++) {
    		
    		if (input.charAt(start) == ')') {
    			start += 2;
    			break;
    		}
    	}
    	
    	return input.substring(start);
    }
    
    //metodo che duplica tutti gli apici presenti nella stringa
    public static String duplicaApostrofi(String input) {
    	
    	String result = input;
    	int massimo  = result.length() - 1;
    	char apice = '\'';
    	
    	for (int i = 0; i <= massimo; i++) {
    		if (result.charAt(i) == apice ) {
    			result = result.substring(0, i+1) + apice + result.substring(i+1);
    			massimo++;
    			i++;
    		}
    	}
    	
    	return result;
    }
    
    public static String intToString(int num, int digits) {
//        assert digits > 0 : "Invalid number of digits";
        if (BuildConfig.DEBUG && !(digits > 0))
        		throw new AssertionError("Campo digits non valido");

        // create variable length array of zeros
        char[] zeros = new char[digits];
        Arrays.fill(zeros, '0');
        // format number as String
        DecimalFormat df = new DecimalFormat(String.valueOf(zeros));

        return df.format(num);
    }
    
	public static boolean isOnline(Activity activity) {
	    ConnectivityManager cm =
	        (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	
	/* Checks if external storage is available to at least read */
	public static boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	/* Filtra il link di input per tenere solo il nome del file */
	public static String filterMediaLink(String link) {
	    if (link.length() == 0)
	    	return link;
	    else {
	    	int start = link.indexOf(".com/");
	    	return link.substring(start + 5);
	    }
	}
	
	public static String retrieveMediaFileLink(Context activity, String link) {
		
		if (isExternalStorageReadable()) {
			File[] fileArray = ContextCompat.getExternalFilesDirs(activity, null);
			File fileExt = new File(fileArray[0], filterMediaLink(link));
			if (fileExt.exists()) {
//				Log.i("FILE esterno:", fileExt.getAbsolutePath());
				return fileExt.getAbsolutePath();
			}
//			Log.i("FILE esterno:", "NON TROVATO");
		}
		
		File fileInt = new File(activity.getFilesDir(), filterMediaLink(link));
		if (fileInt.exists()) {
//			Log.i("FILE interno:", fileInt.getAbsolutePath());
			return fileInt.getAbsolutePath();
		}
//		Log.i("FILE INTERNO:", "NON TROVATO");
		return "";
	}
	
    public static void setupTransparentTints(Activity context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT
        		|| Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH)
        	return;
        SystemBarTintManager tintManager = new SystemBarTintManager(context);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.theme_primary_dark);
    }

    @SuppressWarnings("ResourceType")
    public static void blockOrientation(Activity activity) {
        // Copied from Android docs, since we don't have these values in Froyo 2.2
        int SCREEN_ORIENTATION_REVERSE_LANDSCAPE = 8;
        int SCREEN_ORIENTATION_REVERSE_PORTRAIT = 9;

        Display display = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        switch(activity.getResources().getConfiguration().orientation)
        {
            case Configuration.ORIENTATION_LANDSCAPE:
                if(rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90)
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                else
                    activity.setRequestedOrientation(SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                if(rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_270)
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                else
                    activity.setRequestedOrientation(SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        }
    }
	
}
