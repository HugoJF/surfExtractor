package bowClassifier;

/**
 * @author Hugo
 * 
 */
public class Histogram {
	private double[] histogram;
	private String folderName;

	/**
	 * @param size
	 *            - Simple fixed size double vector
	 */
	public Histogram(int size) {
		this.histogram = new double[size];
		this.histogram = new double[size];
	}

	/**
	 * @return the histogram double vector
	 */
	public double[] getHistogram() {
		return this.histogram;
	}

	/**
	 * @param h
	 *            - replaces the histogram's double vector
	 */
	public void setHistogram(double[] h) {
		this.histogram = h;
	}

	/**
	 * @param pos
	 *            - return histogram's value at position pos
	 * @return
	 */
	public double getValue(int pos) {
		return this.histogram[pos];
	}

	/**
	 * Set value of the vector in position pos
	 * 
	 * @param pos
	 * @param val
	 */
	public void setValue(int pos, double val) {
		this.histogram[pos] = val;
	}

	/**
	 * @return histogram length
	 */
	public int getSize() {
		return histogram.length;
	}

	/**
	 * @param b
	 *            - normalized or not
	 * @return
	 */
	public String toString() {
		String s = "";
		for (int i = 0; i < this.histogram.length; i++) {
			s += this.histogram[i];
			s += ", ";
		}

		s += this.folderName;

		return s;
	}

	/**
	 * Normalize histogram
	 */
	public void normalize() {
		int total = 0;
		for (int i = 0; i < this.histogram.length; i++) {
			total += this.histogram[i];
		}
		for (int i = 0; i < this.histogram.length; i++) {
			this.histogram[i] /= total;
		}
	}

	/**
	 * @param folderName class name
	 */
	public void setFolderName(String folderName) {
		this.folderName = folderName;

	}
}
