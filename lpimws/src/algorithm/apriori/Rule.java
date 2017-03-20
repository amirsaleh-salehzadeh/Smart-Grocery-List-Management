package algorithm.apriori;

public class Rule implements Comparable<Rule> {
	String combination, remaining, X, Y;
    Double confidence; 
    
	public Rule(String combination, String remaining, double confidence) {
		 this.combination = combination;
         this.remaining = remaining;
         this.confidence = confidence;
	}

	public String getCombination() {
		return combination;
	}

	public void setCombination(String combination) {
		this.combination = combination;
	}

	public String getRemaining() {
		return remaining;
	}

	public void setRemaining(String remaining) {
		this.remaining = remaining;
	}

	public String getX() {
		return combination;
	}

	public void setX(String x) {
		X = x;
	}

	public String getY() {
		return remaining;
	}

	public void setY(String y) {
		Y = y;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	public int compareTo(Rule o) {
		return getX().compareTo(o.getX());
	}


}
