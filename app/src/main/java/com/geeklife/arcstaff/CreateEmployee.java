package com.geeklife.arcstaff;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateEmployee extends AppCompatActivity {

    EditText fName, lName, bUnit, hOffice, emailAdd, countryCode, contact;

    ListView buList, offList;
    // required lists
    String buItems[] = {"Defence", "Nuclear", "Oil & Gas", "Operations", "Rail", "Resourcing"};
    String buCodes[] = {"DBU", "NBU", "OBU", "OPS", "RBU", "RMG"};
    String countryItems[] = {"United Kingdom (+44)", "Australia (+61)", "Singapore (+51)"};
    String officeItems[] = {"Edinburgh", "Glasgow", "Manchester", "Bristol", "Taunton", "London",
            "Singapore", "Perth", "Sydney"};
    String officeCodes[] = {"EDI", "GLA", "MAN", "BRI", "TAU", "LON", "SIN", "PER", "SYD"};

    JSONObject employee;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.new_user );

        final InputMethodManager imm = ( InputMethodManager ) getSystemService( INPUT_METHOD_SERVICE );
        final ArrayAdapter busAdapter = new ArrayAdapter<>
                ( this, R.layout.lv_rows, buItems );
        final ArrayAdapter offAdapter = new ArrayAdapter<>
                ( this, R.layout.lv_rows, officeItems );

        final Button enter = findViewById( R.id.btn_enter );

        fName = findViewById( R.id.first_name );
        lName = findViewById( R.id.last_name );
        bUnit = findViewById( R.id.bus_unit );
        hOffice = findViewById( R.id.home_office );
        emailAdd = findViewById( R.id.email );
        countryCode = findViewById( R.id.country_code );
        contact = findViewById( R.id.contact );


        employee = new JSONObject();

        View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange( View v, boolean hasFocus ) {

                switch ( v.getId() ) {

                    // Business Unit
                    case R.id.bus_unit:
                        if ( hasFocus ) {
                            buList = findViewById( R.id.list_view );

                            buList.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick
                                        ( AdapterView<?> adapterView, View view, int pos, long len ) {

                                    bUnit.setText( new StringBuilder()
                                            .append( buItems[pos] )
                                            .append( " " )
                                            .append( getString( R.string.open_parenthesis ) )
                                            .append( buCodes[pos] )
                                            .append( getString( R.string.close_parenthesis ) )
                                            .toString() );
                                    toggleVisibility();
                                }
                            } );

                            Log.i( "FORM", "BU has focus" );
                            imm.hideSoftInputFromWindow( bUnit.getWindowToken(), 0 );
                            buList.setAdapter( busAdapter );
                            toggleVisibility();
                        }
                        break;

                    case R.id.home_office:
                        if ( hasFocus ) {
                            offList = findViewById( R.id.list_view );

                            offList.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick
                                        ( AdapterView<?> adapterView, View view, int pos, long len ) {
                                    Log.i( "OFFICE", "setting item" );
                                    hOffice.setText( new StringBuilder()
                                            .append( officeItems[pos] )
                                            .append( " " )
                                            .append( getString( R.string.open_parenthesis ) )
                                            .append( officeCodes[pos] )
                                            .append( getString( R.string.close_parenthesis ) )
                                            .toString() );
                                    toggleVisibility();
                                }
                            } );

                            Log.i( "FORM", "OFFICE has focus" );
                            imm.hideSoftInputFromWindow( hOffice.getWindowToken(), 0 );
                            offList.setAdapter( offAdapter );
                            toggleVisibility();
                        }
                        break;

                }

            }
        };


        enter.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {

                imm.hideSoftInputFromWindow( enter.getWindowToken(), 0 );

                // build JSON Object

                try {
                    employee.put( "first_name", fName.getText().toString() );
                    employee.put( "last_name", lName.getText().toString() );
                    employee.put( "bus_unit", bUnit.getText().toString() );
                    employee.put( "home_office", hOffice.getText().toString() );
                    employee.put( "email", emailAdd.getText().toString() );
                    employee.put( "contact", contact.getText().toString() );

                } catch ( JSONException e ) {
                    e.printStackTrace();
                }

                if ( checkInputText() ) {
                    new AsyncCreateEmp().execute();
                }

            }
        } );

        bUnit.setOnFocusChangeListener( focusListener );
        hOffice.setOnFocusChangeListener( focusListener );

    }

    private class AsyncCreateEmp extends AsyncTask<Void, Void, Void> {
        HttpURLConnection connection;
        URL url = null;
        ProgressBar progressBar = findViewById( R.id.progressbar );

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //set a process dialogue on UI thread
            progressBar.setVisibility( View.VISIBLE );

        }

        @Override
        protected Void doInBackground( Void... voids ) {
            return null;
        }

        @Override
        protected void onPostExecute( Void v ) {
            super.onPostExecute( v );
            progressBar.setVisibility( View.GONE );
            Toast.makeText( CreateEmployee.this, "DONE", Toast.LENGTH_SHORT ).show();
        }
    }

    private ArrayAdapter setArrayAdapter( String arrayToUse[] ) {

        ArrayAdapter mAdapter = new ArrayAdapter<>
                ( this, R.layout.lv_rows, arrayToUse );
        buList.setAdapter( mAdapter );

        return mAdapter;
    }

    private boolean checkInputText() {
        Boolean isValid = true;
        EditText editTexts[] = {fName, lName, bUnit, hOffice, emailAdd, countryCode, contact};

        for ( EditText et : editTexts ) {

            if ( et == fName || et == lName ) {
                String REGEX = "[A-Za-z]";
                String input = et.getText().toString();
                Pattern p = Pattern.compile( REGEX );
                Matcher m = p.matcher( input );
                int compare = findMatches( m, input ) - input.length();


                if ( compare == 0 && input.length() > 0 ) {
                    et.setBackgroundResource( R.drawable.et_valid );
                } else {
                    et.setBackgroundResource( R.drawable.et_error );
                    if ( isValid ) {
                        isValid = false;
                    }
                }
            }
            // just check not null
            if ( et == bUnit || et == hOffice || et == countryCode ) {
                if ( et.getText().length() > 0 ) {
                    et.setBackgroundResource( R.drawable.et_valid );
                }
            }

            if ( et == contact ) {
                String REGEX = "[0-9]";
                String input = et.getText().toString();
                Pattern p = Pattern.compile( REGEX );
                Matcher m = p.matcher( input );
                int compare = findMatches( m, input ) - input.length();

                if ( compare == 0 && input.length() > 0 ) {
                    et.setBackgroundResource( R.drawable.et_valid );
                } else {
                    et.setBackgroundResource( R.drawable.et_error );
                    if ( isValid ) {
                        isValid = false;
                    }
                }
            }

            if ( et == emailAdd ) {
                String arc = "@consultarc.com";
                String emailToTest = et.getText().toString();

                if ( isValidEmail( emailToTest ) ) {
                    Log.i( "EMAILTEST", "OK" );
                    et.setBackgroundResource( R.drawable.et_valid );
                } else {
                    Log.i( "EMAILTEST", "NOT OK" );
                    et.setBackgroundResource( R.drawable.et_error );
                    if ( isValid ) {
                        isValid = false;

                    }
                }
            }
        }


        return isValid;
    }

    private boolean isValidEmail( String email ) {
        Boolean ok = true;

        String arc = "@consultarc.com";
        String REGEX = "^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$";
        ok = email.length() > 15;

        if ( ok ) {
            ok = email.substring( email.length() - arc.length() ).equals( arc );
        }

        Pattern p = Pattern.compile( REGEX );
        Matcher m = p.matcher( email );

        if ( ok ) {
            ok = m.matches();
        }

        // dummy
        return ok;  // use ok to add multiple tests

    }

    private int findMatches( Matcher m, String s ) {
        int count = 0;

        while ( m.find() ) {
            count++;
        }
        return count;
    }

    private void toggleVisibility() {
        ConstraintLayout form = findViewById( R.id.new_user_form );
        Log.i( "FORM", Integer.toString( form.getChildCount() ) );

        for ( int i = 0; i < form.getChildCount(); i++ ) {
            View v = form.getChildAt( i );

            if ( v.getVisibility() == View.GONE ) {
                v.setVisibility( View.VISIBLE );
            } else {
                v.setVisibility( View.GONE );
            }
        }

    }
}
