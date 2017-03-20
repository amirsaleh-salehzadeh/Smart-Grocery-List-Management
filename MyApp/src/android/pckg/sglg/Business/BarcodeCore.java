package android.pckg.sglg.Business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.text.html.HTMLDocument.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.pckg.sglg.R;
import android.pckg.sglg.Activities.ListActivity;
import android.pckg.sglg.Common.Product;
import android.pckg.sglg.Common.User;
import android.pckg.sglg.Tools.AMSTools;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

//------------> http://hmkcode.com/android-simple-sqlite-database-tutorial/
public class BarcodeCore {
	
}
