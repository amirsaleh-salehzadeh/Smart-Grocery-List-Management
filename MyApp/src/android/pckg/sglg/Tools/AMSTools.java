package android.pckg.sglg.Tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.bool;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.pckg.sglg.Algorithms.Apriori.Item;
import android.pckg.sglg.Business.ProductCore;
import android.provider.MediaStore;
import android.util.Log;

public class AMSTools {
	static Bitmap bmImg;

	/**
	 * @param args
	 */
	public static Bitmap resizeBitmap(final Bitmap bitmap, final int width,
			final int height) {
		final int oldWidth = bitmap.getWidth();
		final int oldHeight = bitmap.getHeight();
		final int newWidth = width;
		final int newHeight = height;

		// calculate the scale
		final float scaleWidth = ((float) newWidth) / oldWidth;
		final float scaleHeight = ((float) newHeight) / oldHeight;

		// create a matrix for the manipulation
		final Matrix matrix = new Matrix();
		// resize the Bitmap
		matrix.postScale(scaleWidth, scaleHeight);
		// if you want to rotate the Bitmap

		// recreate the new Bitmap
		final Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				oldWidth, oldHeight, matrix, true);

		return resizedBitmap;
	}

	public static void downloadFile(String fileUrl, String fileName,
			ContentResolver rs) {
		URL myFileUrl = null;
		try {
			myFileUrl = new URL(fileUrl);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			Log.i("im connected", "Download");
			bmImg = BitmapFactory.decodeStream(is);
			saveImage(fileName, rs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static public boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	public static boolean deleteDirectory() {
		String pathn = Environment.getExternalStorageDirectory().toString();
		Log.i("in save()", "after mkdir");
		File path = new File(pathn + "/SGLG/image/");
		if (path.exists()) {
			File[] files = path.listFiles();
			if (files == null) {
				return true;
			}
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	static void saveImage(String name, ContentResolver rs) {
		File filename;
		try {
			String path = Environment.getExternalStorageDirectory().toString();
			Log.i("in save()", "after mkdir");
			new File(path + "/SGLG/image/").mkdirs();
			filename = new File(path + "/SGLG/" + name);
			Log.i("in save()", "after file");
			FileOutputStream out = new FileOutputStream(filename);
			Log.i("in save()", "after outputstream");
			bmImg.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
			Log.i("in save()", "after outputstream closed");
			MediaStore.Images.Media.insertImage(rs, filename.getAbsolutePath(),
					filename.getName(), filename.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String SortMyString(String s) {
		// int[] candidatesArr =
		String[] numberStrs = s.split(",");
		int[] numbers = new int[numberStrs.length];
		for (int i = 0; i < numberStrs.length; i++) {
			numbers[i] = Integer.parseInt(numberStrs[i]);
		}
		Arrays.sort(numbers);
		String res = "";
		for (int i = 0; i < numbers.length; i++) {
			if (res.length() == 0 && !(numbers[i] + "").equals(""))
				res = (numbers[i] + "");
			else if (!(numbers[i] + "").equals(""))
				res = res + "," + (numbers[i] + "");
		}
		return res;
	}

	public static String avoidDupInComma(String sp) {
		HashSet<String> test = new HashSet<String>(Arrays.asList(sp.split(",")));
		String collegeString = "";
		for (String s : test) {
			collegeString += (collegeString == "" ? "" : ",") + s;
		}
		return collegeString;
	}

	public static boolean checkToggleButton(String string) {
		File root = new File(Environment.getExternalStorageDirectory(),
				"LPIMNotes");
		File file = new File(root, "productsPeriod.txt");
		String line = null;
		String p = "";
		BufferedReader buffreader;
		try {
			buffreader = new BufferedReader(new FileReader(file));
			while ((line = buffreader.readLine()) != null) {
				p += line;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] lines = p.split(";");
		for (int i = 0; i < lines.length; i++) {
			String[] q = lines[i].split(",");
			if (q[0].equalsIgnoreCase(string))
				return true;
		}
		return false;
	}

	public static String getTodayDate() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");// dd/MM/yyyy
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return strDate + " 00:00:00";
	}

	public static int getDatabaseVersion() {
		return 94;
	}

	public static String getDatabaseName() {
		return "SGLG";
	}

	private static String getProductPeriod(String string) {
		String res = "";
		File root = new File(Environment.getExternalStorageDirectory(),
				"LPIMNotes");
		File file = new File(root, "productsPeriod.txt");
		String line = null;
		String p = "";
		BufferedReader buffreader;
		try {
			buffreader = new BufferedReader(new FileReader(file));
			while ((line = buffreader.readLine()) != null) {
				p += line;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] lines = p.split(";");
		for (int i = 0; i < lines.length; i++) {
			String[] q = lines[i].split(",");
			if (q[0].equalsIgnoreCase(string))
				return q[1];
		}
		return res;
	}

	public static void dropFromList(String id) {
		File root = new File(Environment.getExternalStorageDirectory(),
				"LPIMNotes");
		File file = new File(root, "productsPeriod.txt");
		String line = null;
		String p = "";
		BufferedReader buffreader;
		try {
			buffreader = new BufferedReader(new FileReader(file));
			while ((line = buffreader.readLine()) != null) {
				if (line.split(",")[0].equalsIgnoreCase(id)) {
					line = "";
					p += line;
				} else {
					p += line + "\n";
				}
			}
			PrintWriter writer = new PrintWriter(file);
			writer.print("");
			writer.print(p);

			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		file = new File(root, "Notification.txt");
		p = "";
		try {
			buffreader = new BufferedReader(new FileReader(file));
			while ((line = buffreader.readLine()) != null) {
				if (line.split(",")[0].equalsIgnoreCase(id)) {
					line = "";
					p += line;
				} else {
					p += line + "\n";
				}
			}
			PrintWriter writer = new PrintWriter(file);
			writer.print("");
			writer.print(p);

			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean androidSynchFile(String x) {
		boolean res = false;
		File root = new File(Environment.getExternalStorageDirectory(),
				"LPIMNotes");
		if (!root.exists()) {
			root.mkdirs();
		}
		File file = new File(root, "productsPeriod.txt");
		String line = "";
		String p = "";
		try {
			BufferedReader buffreader = new BufferedReader(new FileReader(file));
			while ((line = buffreader.readLine()) != null) {
				// if(line.split(",")[0].equalsIgnoreCase(x.split(",")[0]))
				// line = "";
				p += line + "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		FileWriter writer;
		try {
			if (!file.exists())
				file.createNewFile();
			String[] lines = p.split(";");
			boolean validator = true;
			for (int i = 0; i < lines.length; i++) {
				String[] newInput = x.split(",");
				String[] q = lines[i].split(",");
				if (q[0].equalsIgnoreCase(newInput[0])) {
					validator = false;
					return false;
				}
			}
			writer = new FileWriter(file, true);
			if (validator) {
				writer.append(x);
			}
			writer.flush();
			writer.close();
			res = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public static String GetServerAddress() {
		return GetServerIP() + "LPIMWS/REST/WebService/";
	}

	public static String GetServerIP() {
		// return "http://192.168.1.227:8090/";
		 return "http://192.168.43.5:8090/";
//		 return "http://10.122.57.111:8080/";
//		return "http://10.0.0.3:8090/";
	}

	private static void saveLastpurchaseDate(String date) {
		// TODO Auto-generated method stub
		File root = new File(Environment.getExternalStorageDirectory(),
				"LPIMNotes");
		if (!root.exists()) {
			root.mkdirs();
		}
		File file = new File(root, "lastPurchaseDate.txt");
		FileWriter writer;
		try {
			if (!file.exists())
				file.createNewFile();
			writer = new FileWriter(file, false);
			writer.write(date);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// public static String getProductName(String string) {
	// StringBuilder builder = new StringBuilder();
	// HttpClient client = new DefaultHttpClient();
	// try {
	// HttpGet httpGet = new HttpGet(AMSTools.GETIP()
	// + "/LPIMWS/REST/WebService/GetProductName?productName="
	// + string);
	// HttpResponse response = client.execute(httpGet);
	// StatusLine statusLine = response.getStatusLine();
	// int statusCode = statusLine.getStatusCode();
	// if (statusCode == 200) {
	// HttpEntity entity = response.getEntity();
	// InputStream content = entity.getContent();
	// BufferedReader reader = new BufferedReader(
	// new InputStreamReader(content));
	// String line;
	// while ((line = reader.readLine()) != null) {
	// builder.append(line);
	// }
	// } else {
	// Log.e(OrderListActivity.class.toString(),
	// "Failed to download file");
	// }
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// String json = builder.toString();
	// json = json.replace("\"", "");
	// return json;
	// }

	private static String calculateDate(String string, String date) {
		String period = getProductPeriod(string);
		date = date.replace("-", "");
		Date tradeDate;
		try {
			tradeDate = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
					.parse(date);
			Calendar c = Calendar.getInstance();
			c.setTime(tradeDate);
			c.add(Calendar.DATE, Integer.parseInt(period));
			tradeDate = c.getTime();
			date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
					.format(tradeDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(date);
		return date;
	}

	private static void synchNotification(String string, String date) {
		String x = string + "," + date + ";\n";
		File root = new File(Environment.getExternalStorageDirectory(),
				"LPIMNotes");
		if (!root.exists()) {
			root.mkdirs();
		}
		File file = new File(root, "Notification.txt");
		String line = null;
		String p = "";
		try {
			BufferedReader buffreader = new BufferedReader(new FileReader(file));
			while ((line = buffreader.readLine()) != null) {
				p += line + "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		FileWriter writer;
		try {
			if (!file.exists())
				file.createNewFile();
			String[] lines = p.split("\n");
			boolean validator = true;
			for (int i = 0; i < lines.length; i++) {
				String t = lines[i] + "\n";
				if (t.equalsIgnoreCase(x)) {
					validator = false;
				}
			}
			writer = new FileWriter(file, true);
			if (validator) {
				writer.append(x);
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void setShoppingTime(Integer h, Integer m, Integer day) {
		String x = h + ":" + m + ":00," + day;
		File root = new File(Environment.getExternalStorageDirectory(),
				"LPIMNotes");
		if (!root.exists()) {
			root.mkdirs();
		}
		File file = new File(root, "ShoppingTime.txt");
		FileWriter writer;
		try {
			if (!file.exists())
				file.createNewFile();
			writer = new FileWriter(file, false);
			writer.write(x);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getShoppingTime() {
		String time = "";
		File root = new File(Environment.getExternalStorageDirectory(),
				"LPIMNotes");
		if (!root.exists()) {
			root.mkdirs();
		}
		File file = new File(root, "ShoppingTime.txt");
		String line = null;
		try {
			BufferedReader buffreader = new BufferedReader(new FileReader(file));
			while ((line = buffreader.readLine()) != null) {
				time += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return time;
	}

	public static String getAlarmsInfo() {
		File root = new File(Environment.getExternalStorageDirectory(),
				"LPIMNotes");
		if (!root.exists()) {
			root.mkdirs();
		}
		File file = new File(root, "Notification.txt");
		String line = null;
		String p = "";
		try {
			BufferedReader buffreader = new BufferedReader(new FileReader(file));
			while ((line = buffreader.readLine()) != null) {
				// if(line.split(",")[0].equalsIgnoreCase(x.split(",")[0]))
				// line = "";
				if (!p.contains(line.split(",")[1])) {
					p += line.split(",")[1];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return p;
	}

	public static String extractGrocery() {
		String productIds = "";
		String lastPdate = "";
		File root = new File(Environment.getExternalStorageDirectory(),
				"LPIMNotes");
		File file = new File(root, "lastPurchaseDate.txt");
		String line = null;
		try {
			BufferedReader buffreader = new BufferedReader(new FileReader(file));
			while ((line = buffreader.readLine()) != null) {
				lastPdate += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		lastPdate = lastPdate.replace("-", "");
		Date lastpurchasedDate;
		try {
			lastpurchasedDate = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
					.parse(lastPdate);
			Calendar c = new GregorianCalendar();
			c.set(Calendar.HOUR_OF_DAY, 0); // anything 0 - 23
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			Date todayDate = c.getTime();

			file = new File(root, "Notification.txt");
			try {
				BufferedReader buffreader = new BufferedReader(new FileReader(
						file));
				while ((line = buffreader.readLine()) != null) {
					if (line.length() > 3) {
						String dateString = line.split(",")[1].replace("-", "");
						Date datePurchase = new SimpleDateFormat("yyyyMMdd",
								Locale.ENGLISH).parse(dateString);
						if (datePurchase.after(lastpurchasedDate)
								&& datePurchase.before(todayDate)) {
							productIds += line.split(",")[0] + ",";
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return productIds;
	}

	public static ArrayList<Item> sievedMyArray(
			ArrayList<Item> associationeRules4CurrentList) {
		ArrayList<Item> res = new ArrayList<Item>();
		for (int i = 0; i < associationeRules4CurrentList.size(); i++) {
			String[] ps = associationeRules4CurrentList.get(i).getName()
					.split(",");
			for (int j = 0; j < ps.length; j++) {
				if (res.size() == 0) {
					Item item = new Item();
					item.setName(ps[0]);
					item.setConfidence(associationeRules4CurrentList.get(i)
							.getConfidence());
					res.add(item);
				}
				boolean chk = false;
				for (int k = 0; k < res.size(); k++) {
					if (res.get(k).getName().equals(ps[j])) {
						chk = true;
						if (res.get(k).getConfidence() < associationeRules4CurrentList
								.get(i).getConfidence())
							res.get(k).setConfidence(
									associationeRules4CurrentList.get(i)
											.getConfidence());
					}
				}
				if (!chk) {
					Item item = new Item();
					item.setName(ps[j]);
					item.setConfidence(associationeRules4CurrentList.get(i)
							.getConfidence());
					res.add(item);
				}
			}
		}

		return res;
	}

}
