package android.pckg.sglg.Algorithms.Apriori;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import android.pckg.sglg.Business.ListCore;
import android.pckg.sglg.Tools.AMSTools;
import android.pckg.sglg.Tools.NVL;

public class Apriori {
	ItemsDictionary allFrequentItems = new ItemsDictionary(
			new ArrayList<Item>());
	final HashMap<String, Item> hashMap = new HashMap<String, Item>();
	ArrayList<Rule> strongRules = new ArrayList<Rule>();

	private static double round(double value) {
		if (2 < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, 2);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	public ResultSet ProcessTransaction(double minSupport,
			double minConfidence, Iterable<String> items, String[] transactions) {
		ArrayList<Item> frequentItems = GetL1FrequentItems(minSupport, items,
				transactions);
		minSupport = this.round(minSupport);
		minConfidence = this.round(minConfidence);
		allFrequentItems = new ItemsDictionary(frequentItems);
		hashMap.putAll(allFrequentItems.fillHashmap(frequentItems));
		HashMap<String, Double> candidates = new HashMap<String, Double>();
		double transactionsCount = transactions.length;
		int i = 1;
		do {
			candidates = GenerateCandidates(frequentItems, transactions,
					minSupport);
			frequentItems = GetFrequentItems(candidates, minSupport,
					transactionsCount);
			allFrequentItems.concatAll(frequentItems);
			hashMap.putAll(allFrequentItems.fillHashmap(frequentItems));
			if (i >= 3)
				break;
			i++;
		} while (candidates.size() != 0);

		HashSet<Rule> rules = GenerateRules();
		strongRules = GetStrongRules(minConfidence, rules, allFrequentItems);
		// HashMap<String, HashMap<String, Double>> closedItemSets =
		// GetClosedItemSets();
		// ArrayList<String> maximalItemSets =
		// GetMaximalItemSets(closedItemSets);

		ResultSet rs = new ResultSet();
		// rs.ClosedItemSets = closedItemSets;
		rs.FrequentItems = allFrequentItems;
		rs.StrongRules = strongRules;
		// rs.MaximalItemSets = maximalItemSets;
		return rs;
	}

	public ArrayList<Item> GetFrequentItemsTransaction(Iterable<String> items) {
		String[] p = new String[] { "" };
		ArrayList<Item> frequentItems = GetL1FrequentItems(0, items, p);
		allFrequentItems = new ItemsDictionary(frequentItems);
		hashMap.putAll(allFrequentItems.fillHashmap(frequentItems));
		HashMap<String, Double> candidates = new HashMap<String, Double>();
		double transactionsCount = p.length;
		int i = 1;
		do {
			candidates = GenerateCandidates(frequentItems, p, -1);
			frequentItems = GetFrequentItems(candidates, 0, transactionsCount);
			allFrequentItems.concatAll(frequentItems);
			hashMap.putAll(allFrequentItems.fillHashmap(frequentItems));
			if (i >= 3)
				break;
			i++;
		} while (candidates.size() != 0);
		frequentItems.clear();
		for (Entry<String, Item> entry : hashMap.entrySet()) {
			String key = entry.getKey();
			frequentItems.add(entry.getValue());
			// do something with key and/or tab
		}
		return frequentItems;
	}

	private ArrayList<String> GetMaximalItemSets(
			HashMap<String, HashMap<String, Double>> closedItemSets) {
		ArrayList<String> maximalItemSets = new ArrayList<String>();

		for (Entry<String, HashMap<String, Double>> item : closedItemSets
				.entrySet()) {
			HashMap<String, Double> parents = item.getValue();

			if (parents.size() == 0) {
				maximalItemSets.add(item.getKey());
			}
		}

		return maximalItemSets;
	}

	private HashMap<String, HashMap<String, Double>> GetClosedItemSets() {
		HashMap<String, HashMap<String, Double>> closedItemSets = new HashMap<String, HashMap<String, Double>>();
		int i = 0;

		for (Entry<String, Item> item : hashMap.entrySet()) {
			HashMap<String, Double> parents = GetItemParents(item.getValue()
					.getName(), ++i);

			if (CheckIsClosed(item.getKey(), parents)) {
				closedItemSets.put(item.getValue().getName(), parents);
			}
		}

		return closedItemSets;
	}

	private HashMap<String, Double> GetItemParents(String child, int index) {
		HashMap<String, Double> parents = new HashMap<String, Double>();

		for (Entry<String, Item> item : hashMap.entrySet()) {
			String parent = item.getValue().getName();

			if (parent.length() == child.length() + 1) {
				if (CheckIsSubset(child, parent)) {
					parents.put(parent, hashMap.get(parent).getSupport());
				}
			}
		}

		return parents;
	}

	private boolean CheckIsClosed(String child, HashMap<String, Double> parents) {
		for (String parent : parents.keySet()) {
			if (hashMap.get(child).getSupport() == hashMap.get(parent)
					.getSupport()) {
				return false;
			}
		}

		return true;
	}

	private ArrayList<Rule> GetStrongRules(double minConfidence,
			HashSet<Rule> rules, ItemsDictionary allFrequentItems) {
		for (Rule rule : rules) {
			String xy = AMSTools.SortMyString(rule.getX() + "," + rule.getY());
			AddStrongRule(rule, xy, minConfidence);
		}
		Collections.sort(strongRules, new Comparator<Rule>() {
			public int compare(Rule o1, Rule o2) {
				return Double.compare(o1.getConfidence(), o2.getConfidence());
			}

		});
		return strongRules;
	}

	private void AddStrongRule(Rule rule, String XY, double minConfidence) {
		double confidence = GetConfidence(rule.getX(), XY);

		if (confidence >= minConfidence) {
			Rule newRule = new Rule(rule.getX(), rule.getY(), confidence);
			if (this.chkAvailin(newRule))
				strongRules.add(newRule);
		}

		confidence = GetConfidence(rule.getY(), XY);

		if (confidence >= minConfidence) {
			Rule newRule = new Rule(rule.getY(), rule.getX(), confidence);
			if (this.chkAvailin(newRule))
				strongRules.add(newRule);
		}

	}

	public boolean chkAvailin(Rule rule) {
		for (int i = 0; i < strongRules.size(); i++) {
			Rule newrule = new Rule(strongRules.get(i).combination, strongRules
					.get(i).getRemaining(), strongRules.get(i).getConfidence());
			if (newrule.getCombination()
					.equalsIgnoreCase(rule.getCombination())
					&& newrule.getRemaining().equalsIgnoreCase(
							rule.getRemaining())
					&& newrule.getConfidence() == rule.getConfidence())
				return false;
		}
		return true;
	}

	private double GetConfidence(String X, String XY) {
		if (hashMap.get(XY) == null || hashMap.get(X) == null)
			return 0;
		double supportX = hashMap.get(X).getSupport();
		double supportXY = hashMap.get(XY).getSupport();
		return supportXY / supportX;
	}

	private HashSet<Rule> GenerateRules() {
		HashSet<Rule> rulesList = new HashSet<Rule>();
		for (Entry<String, Item> entry : hashMap.entrySet()) {
			if (entry.getValue().getName().split(",").length > 1) {
				Iterable<String> subsetsList = GenerateSubsets(entry.getValue()
						.getName());

				for (String subset : subsetsList) {
					String remaining = GetRemaining(subset, entry.getValue()
							.getName());
					Rule rule = new Rule(subset, remaining, 0);

					if (!rulesList.contains(rule)) {
						rulesList.add(rule);
					}
				}
			}

		}

		return rulesList;
	}

	private Iterable<String> GenerateSubsets(String item) {
		Iterable<String> allSubsets = new ArrayList<String>();
		int subsetLength = item.split(",").length / 2;

		for (int i = 1; i <= subsetLength; i++) {
			ArrayList<String> subsets = new ArrayList<String>();
			GenerateSubsetsRecursive(item, i,
					new String[item.split(",").length], subsets, 0, 0);
			allSubsets = subsets;
		}

		return allSubsets;
	}

	private void GenerateSubsetsRecursive(String item, int subsetLength,
			String[] temp, ArrayList<String> subsets, int q, int r) {
		if (q == subsetLength) {
			String sb = "";
			for (int i = 0; i < subsetLength; i++) {
				if (sb.equalsIgnoreCase(""))
					sb = temp[i];
				else
					sb = sb + "," + temp[i];
			}

			subsets.add(sb);
		} else {
			for (int i = r; i < item.split(",").length; i++) {
				temp[q] = item.split(",")[i];
				GenerateSubsetsRecursive(item, subsetLength, temp, subsets,
						q + 1, i + 1);
			}
		}

	}

	private String GetRemaining(String child, String parent) {
		int index = 0;
		String res = "";
		for (int i = 0; i < child.split(",").length; i++) {
			for (int j = 0; j < parent.split(",").length; j++) {
				if (child.split(",")[i].equalsIgnoreCase(parent.split(",")[j])) {
					parent = parent.replaceAll(parent.split(",")[j], "");
				}
			}
		}
		do {
			parent = parent.replaceAll(",,", ",");
		} while (parent.indexOf(",,") > 0);
		if (",".equals("" + parent.charAt(0)))
			parent = parent.substring(1);
		if (",".equalsIgnoreCase("" + parent.charAt(parent.length() - 1)))
			parent = parent.substring(0, parent.length() - 1);
		if (",".equals("" + parent.charAt(0)))
			parent = parent.substring(1);
		return parent;
	}

	private ArrayList<Item> GetFrequentItems(
			HashMap<String, Double> candidates, double minSupport,
			double transactionsCount) {
		ArrayList<Item> frequentItems = new ArrayList<Item>();
		for (Entry<String, Double> entry : candidates.entrySet()) {

			if (entry.getValue() / transactionsCount >= minSupport
					|| minSupport == 0) {
				Item newitem = new Item();
				newitem.setName(entry.getKey());
				newitem.setSupport(entry.getValue());
				frequentItems.add(newitem);
			}
		}

		return frequentItems;
	}

	private HashMap<String, Double> GenerateCandidates(
			ArrayList<Item> frequentItems, String[] transactions,
			double minSupport) {
		HashMap<String, Double> candidates = new HashMap<String, Double>();
		int transcount = transactions.length;
		for (int i = 0; i < frequentItems.size() - 1; i++) {
			String firstItem = frequentItems.get(i).getName();
			for (int j = i + 1; j < frequentItems.size(); j++) {
				String secondItem = frequentItems.get(j).getName();
				String generatedCandidate = AMSTools
						.SortMyString(GenerateCandidate(firstItem, secondItem));
				if (generatedCandidate != null
						|| !generatedCandidate.equalsIgnoreCase("")) {
					double support = GetSupport(generatedCandidate,
							transactions);
					if (minSupport == -1)
						candidates.put(generatedCandidate, support);
					if (((support * 100) / transcount) * 0.01 >= minSupport)
						if (!candidates.containsKey(generatedCandidate))
							candidates.put(generatedCandidate, support);
				}
			}
		}

		return candidates;
	}

	private String GenerateCandidate(String firstItem, String secondItem) {
		int length = firstItem.split(",").length;
		String[] firstItemArr = firstItem.split(",");
		String[] secondItemArr = secondItem.split(",");
		String candidate = "";
		if (length == 1) {
			return firstItem + "," + secondItem;
		} else {
			for (int i = 0; i < firstItemArr.length; i++) {
				for (int j = 0; j < secondItemArr.length; j++) {
					if (!firstItemArr[i].equalsIgnoreCase(secondItemArr[j]))
						if (candidate != "") {
							boolean chk1 = false;
							boolean chk2 = false;
							for (int k = 0; k < candidate.split(",").length; k++) {
								if (firstItemArr[i]
										.equals(candidate.split(",")[k]))
									chk1 = true;
								if (secondItemArr[j].equals(candidate
										.split(",")[k]))
									chk2 = true;
							}
							if (!chk1)
								candidate = candidate + "," + firstItemArr[i];
							if (!chk2)
								candidate = candidate + "," + secondItemArr[j];
						} else
							candidate = firstItemArr[i] + ","
									+ secondItemArr[j];
				}
			}
			return AMSTools.SortMyString(candidate);
		}
		// String firstSubString = firstItem
		// .substring(
		// 0,
		// firstItem.length()
		// - (firstItem.split(",")[firstItem.split(",").length - 1]
		// .length()+1));
		// String secondSubString = secondItem
		// .substring(
		// 0,
		// secondItem.length()
		// - (secondItem.split(",")[secondItem.split(",").length - 1]
		// .length()+1));
		// // secondItem.substring(0, length - 1);
		//
		// if (firstSubString.equalsIgnoreCase(secondSubString)) {
		// return firstItem + ","+secondSubString;
		// }
		//
		// return "";
		// }
	}

	private ArrayList<Item> GetL1FrequentItems(double minSupport,
			Iterable<String> items, String[] transactions) {
		ArrayList<Item> frequentItemsL1 = new ArrayList<Item>();
		double transactionsCount = transactions.length;
		for (String item : items) {
			double support = GetSupport(item, transactions);
			if (((support * 100) / transactionsCount) * 0.01 >= minSupport) {
				Item newitem = new Item();
				newitem.setName(item);
				newitem.setSupport(support);
				frequentItemsL1.add(newitem);
			}
		}
		Collections.sort(frequentItemsL1, new Comparator<Item>() {

			public int compare(Item o1, Item o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return frequentItemsL1;
	}

	private double GetSupport(String item, String[] transactions) {
		double support = 0;

		for (String transaction : transactions) {
			if (CheckIsSubset(item, transaction)) {
				support++;
			}
		}

		return support;
	}

	private boolean CheckIsSubset(String item, String transaction) {
		for (String c : item.split(",")) {
			if (!transaction.contains(c + "")) {
				return false;
			}
		}

		return true;
	}

	public static void main(String[] args) {
		Apriori ap = new Apriori();
		ArrayList<String> itemslist = new ArrayList<String>();
		itemslist.add("I1");
		itemslist.add("I2");
		itemslist.add("I3");
		itemslist.add("I4");
		itemslist.add("I5");
		Iterable<String> b = itemslist;
		ResultSet rs = ap.ProcessTransaction(0.2, 0.7, b, new String[] {
				"I1,I2,I5", "I2,I4", "I2,I3", "I1,I2,I4", "I1,I3", "I2,I3",
				"I1,I3", "I1,I2,I3,I5", "I1,I2,I3" });
	}

}
