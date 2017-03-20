package algorithm.apriori;

public class Item {

	public String Name;
	public double Support;
	public double Confidence;
	public String Value;
	

	public String getValue() {
		return Value;
	}

	public void setValue(String value) {
		Value = value;
	}

	public double getConfidence() {
		return Confidence;
	}

	public void setConfidence(double confidence) {
		Confidence = confidence;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public double getSupport() {
		return Support;
	}

	public void setSupport(double support) {
		Support = support;
	}

	public int CompareTo(Item other) {
		return Name.compareTo(other.Name);
	}
}
