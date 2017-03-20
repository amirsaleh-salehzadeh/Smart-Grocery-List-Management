package algorithm.apriori;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemsDictionary {
	ArrayList<Item> frequentItems;
	public HashMap<String, Item> allFrequentItems = new HashMap<String, Item>();

	public ItemsDictionary(ArrayList<Item> frequentItems) {
		allFrequentItems = this.concatAll(frequentItems);
	}

	public HashMap<String, Item> concatAll(ArrayList<Item> frequentItems2) {
		for (Item item : frequentItems2) {
			allFrequentItems.put(item.getName(), item);
		}
		return allFrequentItems;
	}

	public Map<? extends String, ? extends Item> fillHashmap(
			ArrayList<Item> frequentItems2) {
		HashMap<String, Item> rs = new HashMap<String, Item>();
		for (int i = 0; i < frequentItems2.size(); i++) {
			rs.put(frequentItems2.get(i).getName(), frequentItems2.get(i));
		}
		return rs;
	}

}
