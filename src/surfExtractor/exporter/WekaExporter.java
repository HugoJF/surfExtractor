package surfExtractor.exporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import surfExtractor.bow_classifier.Bow;
import surfExtractor.bow_classifier.Histogram;
import surfExtractor.image_set.ImageClass;
import surfExtractor.image_set.ImageSet;
import weka.associations.gsp.Element;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.Saver;

public class WekaExporter extends Exporter {

	protected Saver saver;

	protected Instances instances;
	
	public WekaExporter(ImageSet is, Bow bow) {
		this.imageSet = is;
		this.bow = bow;
	}

	public void setSaver(Saver s) {
		this.saver = s;
	}

	@Override
	public void export() {
		FastVector attributes = new FastVector();
		for (int i = 0; i < bow.getClusterNum(); i++) {
			attributes.addElement(new Attribute("A" + i));
		}

		FastVector classValues = new FastVector();
		for (ImageClass ic : imageSet.getImageClasses()) {
			classValues.addElement(ic.getClassName());
		}
		attributes.addElement(new Attribute("class", classValues));

		instances = new Instances(imageSet.getRelation(), attributes, 1);

		for (int i = 0; i < bow.getHistograms().size(); i++) {
			Histogram hh = bow.getHistograms().get(i);
			hh.normalize();
			double[] attributeValues = new double[hh.getSize() + 1];
			for (int j = 0; j < hh.getSize(); j++) {
				attributeValues[j] = hh.getValue(j);
			}
			attributeValues[attributeValues.length - 1] = 0D;
			Instance instance = new Instance(1, attributeValues);
			instance.setValue((Attribute) attributes.elementAt(attributes.size() - 1), hh.getFolderName());
			instances.add(instance);
		}

		if (this.saver == null) {
			this.saver = new ArffSaver();
		}

		this.saver.setInstances(instances);

		if (path != "") {

			try {
				// this.saver.setFile(new File(path));
				this.saver.setFile(new File(path));
				this.saver.writeBatch();
				System.out.println(instances.toSummaryString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	public Instances getInstances() {
		return this.instances;
	}

}
