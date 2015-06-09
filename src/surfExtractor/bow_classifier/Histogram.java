package surfExtractor.bow_classifier;

/**
 * @author Hugo
 * 
 */
public class Histogram {
	/**
	 * Array storing the frequency of clusters inside the n-th cluster
	 */
	private double[] histogram;
	/**
	 * This acts as the class name inside the final file
	 */
	private String folderName;

	/**
	 * @param size - Simple fixed size double vector
	 */
	public Histogram(int size) {
		this.histogram = new double[size];
	}

	/**
	 * @return the histogram double vector
	 */
	public double[] getHistogram() {
		return this.histogram;
	}

	/**
	 * @param h - replaces the histogram's double vector
	 */
	public void setHistogram(double[] h) {
		this.histogram = h;
	}

	/**
	 * @param pos - return histogram's value at position pos
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
	 * @param b - normalized or not
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
	 * Normalize histogram so it's components sum equals 1
	 */
	public void normalizeToSum1() {
		int total = 0;
		for (int i = 0; i < this.histogram.length; i++) {
			total += this.histogram[i];
		}
		for (int i = 0; i < this.histogram.length; i++) {
			this.histogram[i] /= total;
		}
	}

	/**
	 * Normalize histogram as if it were a vector (dividing each component by
	 * the length of the vector)
	 */
	public void normalizAsVectorMagnitude() {
		double sum = 0;
		for (int i = 0; i < this.histogram.length; i++) {
			sum += this.histogram[i] * this.histogram[i];
		}
		sum = Math.sqrt(sum);
		for (int i = 0; i < this.histogram.length; i++) {
			this.histogram[i] /= sum;
		}
	}
	
	/**
	 * Normalizes in reference to the max value of an attribute
	 * 
	 * @param maxVal - Array with max values for each attribute
	 */
	public void normalizeFromMaxFeatureVal(double[] maxVal) {
		for(int i = 0; i < this.histogram.length; i++) {
			this.histogram[i] /= maxVal[i];
		}
	}

	/**
	 * @param folderName class name
	 */
	public void setFolderName(String folderName) {
		this.folderName = folderName;

	}

	/**
	 * @return Folder name String
	 */
	public String getFolderName() {
		return folderName;
	}
}
