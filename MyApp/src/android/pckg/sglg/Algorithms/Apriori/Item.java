package android.pckg.sglg.Algorithms.Apriori;

public class Item {

	public String Name;
	public double Support;
	public double Confidence;
	public String value;
	

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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
