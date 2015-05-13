package surfExtractor.exporter;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import surfExtractor.bow_classifier.Bow;
import surfExtractor.bow_classifier.Histogram;
import surfExtractor.image_set.ImageClass;
import surfExtractor.image_set.ImageSet;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class InstanceGenerator extends Exporter{

	private Instances instances;
	
	private ImageSet imageSet;
	
	private Bow bow;

	protected Logger LOGGER = Logger.getLogger(Exporter.class);
	
	public InstanceGenerator(ImageSet is, Bow bow) {
		this.imageSet = is;
		this.bow = bow;
	}
	
	@Override
	public void export() {
		//Generate attribute name list
		FastVector attributes = new FastVector();
		for (int i = 0; i < bow.getClusterNum(); i++) {
			attributes.addElement(new Attribute("A" + i));
		}

		//Generate class values list
		FastVector classValues = new FastVector();
		for (ImageClass ic : imageSet.getImageClasses()) {
			classValues.addElement(ic.getClassName());
		}
		//Add the class as a new Attribute with it's possible nominal values
		attributes.addElement(new Attribute("class", classValues));

		//Declare Instances passing ImageSet name, all attributes and the weight
		instances = new Instances(imageSet.getRelation(), attributes, 1);

		//Iterate over histograms
		for (int i = 0; i < bow.getHistograms().size(); i++) {
			//Save single histogram to variable and normalize it
			Histogram hh = bow.getHistograms().get(i);
			hh.normalize();
			//Create an array to hold the attribute values
			double[] attributeValues = new double[attributes.size()];
			//For every histogram value add to the attribute
			for (int j = 0; j < hh.getSize(); j++) {
				attributeValues[j] = hh.getValue(j);
			}
			//Reset last value of the array (class)
			attributeValues[attributeValues.length - 1] = 0D;
			//Create the final instance
			Instance instance = new Instance(1, attributeValues);
			//Set class value using proper method to add nominal value
			instance.setValue((Attribute) attributes.elementAt(attributes.size() - 1), hh.getFolderName());
			
			//Add instance to the pack
			instances.add(instance);
		}
	}

	public Instances getInstances() {
		
		return this.instances;
	}
}
