package it.cammino.risuscito;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;

public class FavouritesActivity extends Fragment {

  	private DatabaseCanti listaCanti;
  	private String[] titoli;
  	private String cantoDaCanc;
    private View rootView;

  	private LUtils mLUtils;
	  	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.activity_favourites, container, false);
		((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_activity_favourites);
		
		//crea un istanza dell'oggetto DatabaseCanti
		listaCanti = new DatabaseCanti(getActivity());
		
		mLUtils = LUtils.getInstance(getActivity());
		
		return rootView;
	}

    @Override
    public void onResume() {
    	updateFavouritesList();
    	super.onResume();
    }
    
	@Override
	public void onDestroy() {
		if (listaCanti != null)
			listaCanti.close();
		super.onDestroy();
	}
	
    private void startSubActivity(Bundle bundle, View view) {
    	Intent intent = new Intent(getActivity(), PaginaRenderActivity.class);
    	intent.putExtras(bundle);
    	mLUtils.startActivityWithTransition(intent, view, Utility.TRANS_PAGINA_RENDER);
   	}
	
    private void updateFavouritesList() {
    	
    	// crea un manipolatore per il Database in modalità READ
		SQLiteDatabase db = listaCanti.getReadableDatabase();
		
		// lancia la ricerca dei preferiti
		String query = "SELECT titolo, color" +
				"		FROM ELENCO" +
				"		WHERE favourite = 1" +
				"		ORDER BY TITOLO ASC";
		Cursor lista = db.rawQuery(query, null);
		
		//recupera il numero di record trovati
		int total = lista.getCount();
		
		//nel caso sia presente almeno un preferito, viene nascosto il testo di nessun canto presente
		TextView noResults = (TextView) rootView.findViewById(R.id.no_favourites);
		TextView hintRemove = (TextView) rootView.findViewById(R.id.hint_remove);
		if (total > 0) {
			noResults.setVisibility(View.GONE);
			hintRemove.setVisibility(View.VISIBLE);
		}
		else	{
			noResults.setVisibility(View.VISIBLE);
			hintRemove.setVisibility(View.GONE);
		}
		
		// crea un array e ci memorizza i titoli estratti
		titoli = new String[lista.getCount()];		                      
		lista.moveToFirst();    		    		
		for (int i = 0; i < total; i++) {
			
			titoli[i] = lista.getString(1) + lista.getString(0);
			lista.moveToNext();
		}
		
		// chiude il cursore
		lista.close();

		// crea un oggetto di tipo ListView
        ListView lv = (ListView) rootView.findViewById(R.id.favouritesList);
		lv.setAdapter(new SongRowAdapter());
		
		// setta l'azione al click su ogni voce dell'elenco
		lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // recupera il titolo della voce cliccata
                String cantoCliccato = ((TextView) view.findViewById(R.id.text_title))
                        .getText().toString();
                cantoCliccato = Utility.duplicaApostrofi(cantoCliccato);

                // crea un manipolatore per il DB in modalità READ
                SQLiteDatabase db = listaCanti.getReadableDatabase();

                // esegue la query per il recupero del nome del file della pagina da visualizzare
                String query = "SELECT source, _id" +
                        "  FROM ELENCO" +
                        "  WHERE titolo =  '" + cantoCliccato + "'";
                Cursor cursor = db.rawQuery(query, null);

                // recupera il nome del file
                cursor.moveToFirst();
                String pagina = cursor.getString(0);
                int idCanto = cursor.getInt(1);

                // chiude il cursore
                cursor.close();
                db.close();

                // crea un bundle e ci mette il parametro "pagina", contente il nome del file della pagina da visualizzare
                Bundle bundle = new Bundle();
                bundle.putString("pagina", pagina);
                bundle.putInt("idCanto", idCanto);

                // lancia l'activity che visualizza il canto passando il parametro creato
                startSubActivity(bundle, view);

            }
        });
		
		// setta l'azione al click prolungato  su ogni voce dell'elenco
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                cantoDaCanc = ((TextView) view.findViewById(R.id.text_title)).getText().toString();
                cantoDaCanc = Utility.duplicaApostrofi(cantoDaCanc);
                SnackbarManager.show(
                        Snackbar.with(getActivity())
                                .text(getString(R.string.favorite_remove))
                                .actionLabel(getString(R.string.snackbar_remove))
                                .actionListener(new ActionClickListener() {
                                    @Override
                                    public void onActionClicked(Snackbar snackbar) {
                                        SQLiteDatabase db = listaCanti.getReadableDatabase();
                                        String sql = "UPDATE ELENCO" +
                                                "  SET favourite = 0" +
                                                "  WHERE titolo =  '" + cantoDaCanc + "'";
                                        db.execSQL(sql);
                                        db.close();
                                        updateFavouritesList();
                                    }
                                })
                                .actionColor(getResources().getColor(R.color.theme_accent))
                        , getActivity());
                return true;
            }
        });
		
    }
    
    private class SongRowAdapter extends ArrayAdapter<String> {
    	
    	SongRowAdapter() {
    		super(getActivity(), R.layout.row_item, R.id.text_title, titoli);
    	}
    	
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		
    		View row=super.getView(position, convertView, parent);
    		
    		TextView canto = (TextView) row.findViewById(R.id.text_title);
    		String cantoCliccato = canto.getText().toString();
    		String colore = cantoCliccato.substring(0, 7);
    		
    		((TextView) row.findViewById(R.id.text_title))
    			.setText(cantoCliccato.substring(7));
		        		
    		TextView textPage = (TextView) row.findViewById(R.id.text_page);
    		textPage.setVisibility(View.GONE);
    		LinearLayout fullRow = (LinearLayout) row.findViewById(R.id.full_row);
    		fullRow.setBackgroundColor(Color.parseColor(colore));
    		
    		return(row);
    	}
    }
    
}
