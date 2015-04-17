package surfExtractor.exporter;

import java.io.File;
import java.io.IOException;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.Saver;

public class WekaInstanceExporter extends Exporter {

	protected Saver saver;

	protected Instances instances;

	@Override
	public void export() {
		if (this.saver == null) {
			this.saver = new ArffSaver();
		}

		this.saver.setInstances(instances);

		try {
			// this.saver.setFile(new File(path));
			this.saver.setFile(new File(path));
			this.saver.writeBatch();
			System.out.println(instances.toSummaryString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setInstances(Instances instances) {
		this.instances = instances;
	}

	public void setSaver(Saver saver) {
		this.saver = saver;
	}

}
