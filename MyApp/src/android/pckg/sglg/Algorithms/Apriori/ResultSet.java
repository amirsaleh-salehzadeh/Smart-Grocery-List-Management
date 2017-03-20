package android.pckg.sglg.Algorithms.Apriori;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultSet {
	public ArrayList<Rule> StrongRules;

	public ArrayList<String> MaximalItemSets;

	public HashMap<String, HashMap<String, Double>> ClosedItemSets;

	public ItemsDictionary FrequentItems;
	
	public ItemsDictionary FrequentItemsHM;
}
