package surfExtractor.user_interface;

import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Random;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.BoxLayout;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.JProgressBar;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicScrollBarUI;

import surfExtractor.misc.Configuration;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class UserInterface {

	public static JFrame frmSurfextractor;

	public static JTabbedPane tabbedPane;

	public static JPanel advanced;
	public static JPanel basics;

	public static JFileChooser jFileChooserLoad;
	public static JFileChooser jFileChooserSave;

	public static JButton imagesetButton;
	public static JButton arffDestinationBtn;
	public static JButton randSeedButton;
	public static JButton extractButton;

	public static JProgressBar featureExtractionProgress;
	public static JProgressBar featureClusteringProgress;
	public static JProgressBar histogramCreationProgress;

	public static JSpinner kmeanskSpinner;
	public static JSpinner kmeansIterSpinner;
	public static JSpinner randSeedSpinner;

	public static JLabel randSeedLabel;
	public static JLabel arffDestinationLabel;
	public static JLabel imagesetLabel;
	public static JLabel kmeanskLabel;
	public static JLabel kmeansIter;
	public static JLabel lblFeatureExtraction;
	public static JLabel lblFeatureClustering;
	public static JLabel lblHistogramCreation;

	public static boolean done;

	public static File imagesetPath;
	public static File arffDestinationPath;

	/**
	 * Launch the application.
	 */
	public static void start() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UserInterface.initialize();
					UserInterface.frmSurfextractor.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public static void initialize() {

		jFileChooserLoad = new JFileChooser("C:");
		jFileChooserLoad.setFileSelectionMode(jFileChooserLoad.DIRECTORIES_ONLY);
		jFileChooserLoad.setMultiSelectionEnabled(false);

		jFileChooserSave = new JFileChooser("C:");
		jFileChooserSave.setFileSelectionMode(jFileChooserLoad.FILES_ONLY);
		jFileChooserSave.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "Arff files";
			}

			@Override
			public boolean accept(File arg0) {
				return arg0.getAbsolutePath().endsWith(".arff");
			}
		});
		frmSurfextractor = new JFrame();
		frmSurfextractor.setResizable(false);
		frmSurfextractor.setTitle("surfExtractor 0.1");
		frmSurfextractor.setBounds(100, 100, 315, 290);
		frmSurfextractor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSurfextractor.getContentPane().setLayout(new BoxLayout(frmSurfextractor.getContentPane(), BoxLayout.X_AXIS));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setToolTipText("");
		frmSurfextractor.getContentPane().add(tabbedPane);

		basics = new JPanel();
		basics.setLayout(null);
		tabbedPane.addTab("Basics", null, basics, null);

		imagesetButton = new JButton("Select dataset");
		imagesetButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int result = UserInterface.jFileChooserLoad.showOpenDialog(null);
				if (result == UserInterface.jFileChooserLoad.APPROVE_OPTION) {
					File f = UserInterface.jFileChooserLoad.getSelectedFile();
					if (f != null) {
						UserInterface.imagesetPath = UserInterface.jFileChooserLoad.getSelectedFile();
						UserInterface.imagesetLabel.setText(UserInterface.jFileChooserLoad.getSelectedFile().getAbsolutePath());
					}
				}
			}
		});
		imagesetButton.setBounds(10, 11, 284, 23);
		basics.add(imagesetButton);

		imagesetLabel = new JLabel("...");
		imagesetLabel.setHorizontalAlignment(SwingConstants.LEFT);
		imagesetLabel.setBounds(10, 45, 280, 14);
		basics.add(imagesetLabel);

		arffDestinationBtn = new JButton(".arff destination");
		arffDestinationBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int result = UserInterface.jFileChooserSave.showSaveDialog(null);
				if (result == UserInterface.jFileChooserSave.APPROVE_OPTION) {
					File f = UserInterface.jFileChooserSave.getSelectedFile();
					if (f != null) {
						UserInterface.arffDestinationPath = UserInterface.jFileChooserSave.getSelectedFile();
						String path = UserInterface.jFileChooserSave.getSelectedFile().getAbsolutePath();
						if (!path.endsWith(".arff")) {
							path += ".arff";
						}
						UserInterface.arffDestinationLabel.setText(path);
					}
				}
			}
		});
		arffDestinationBtn.setBounds(10, 70, 284, 23);
		basics.add(arffDestinationBtn);

		arffDestinationLabel = new JLabel("...");
		arffDestinationLabel.setHorizontalAlignment(SwingConstants.LEFT);
		arffDestinationLabel.setBounds(10, 104, 280, 14);
		basics.add(arffDestinationLabel);

		lblFeatureExtraction = new JLabel("Feature extraction");
		lblFeatureExtraction.setHorizontalAlignment(SwingConstants.CENTER);
		lblFeatureExtraction.setBounds(10, 129, 124, 14);
		basics.add(lblFeatureExtraction);

		lblFeatureClustering = new JLabel("Feature clustering");
		lblFeatureClustering.setHorizontalAlignment(SwingConstants.CENTER);
		lblFeatureClustering.setBounds(10, 154, 124, 14);
		basics.add(lblFeatureClustering);

		lblHistogramCreation = new JLabel("Histogram creation");
		lblHistogramCreation.setHorizontalAlignment(SwingConstants.CENTER);
		lblHistogramCreation.setBounds(10, 179, 124, 14);
		basics.add(lblHistogramCreation);

		featureExtractionProgress = new JProgressBar();
		featureExtractionProgress.setBounds(144, 129, 146, 14);
		basics.add(featureExtractionProgress);

		featureClusteringProgress = new JProgressBar();
		featureClusteringProgress.setBounds(144, 154, 146, 14);
		basics.add(featureClusteringProgress);

		histogramCreationProgress = new JProgressBar();
		histogramCreationProgress.setBounds(144, 179, 146, 14);
		basics.add(histogramCreationProgress);

		extractButton = new JButton("Extract");
		extractButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (UserInterface.extract()) {
					UserInterface.done = true;
					UserInterface.extractButton.setEnabled(false);
					UserInterface.extractButton.setText("Extracting");
				}
			}
		});
		extractButton.setBounds(10, 204, 280, 23);
		basics.add(extractButton);

		advanced = new JPanel();
		tabbedPane.addTab("Advanced", null, advanced, null);
		advanced.setLayout(null);

		kmeanskSpinner = new JSpinner();
		kmeanskSpinner.setBounds(10, 11, 59, 20);
		advanced.add(kmeanskSpinner);

		kmeanskLabel = new JLabel("k-means k value");
		kmeanskLabel.setBounds(79, 14, 86, 14);
		advanced.add(kmeanskLabel);

		kmeansIterSpinner = new JSpinner();
		kmeansIterSpinner.setBounds(10, 42, 59, 20);
		advanced.add(kmeansIterSpinner);

		kmeansIter = new JLabel("k-means iterations");
		kmeansIter.setBounds(79, 45, 100, 14);
		advanced.add(kmeansIter);

		randSeedSpinner = new JSpinner();
		randSeedSpinner.setBounds(10, 73, 59, 20);
		advanced.add(randSeedSpinner);

		randSeedButton = new JButton("Randomize");
		randSeedButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				randSeedSpinner.setValue(new Random().nextInt(99999));
			}
		});
		randSeedButton.setBounds(153, 72, 86, 23);
		advanced.add(randSeedButton);

		randSeedLabel = new JLabel("Random seed");
		randSeedLabel.setBounds(79, 76, 86, 14);
		advanced.add(randSeedLabel);

	}

	/**
	 * Holds the code while user is selecting parameters
	 */
	public static void hold() {
		while (!UserInterface.done) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	public static void setConfiguration() {
		Configuration.addConfiguration("kmeans.kvalue", String.valueOf(UserInterface.kmeanskSpinner.getValue()));
		Configuration.addConfiguration("kmeans.iteration", String.valueOf(UserInterface.kmeansIterSpinner.getValue()));

		Configuration.addConfiguration("imageset.path", UserInterface.imagesetPath.getAbsolutePath());
		Configuration.addConfiguration("arff.path", UserInterface.arffDestinationPath.getAbsolutePath());
	}

	public static boolean extract() {
		if (UserInterface.imagesetPath == null) {
			JOptionPane.showMessageDialog(null, "Please select a imageset");
			return false;
		}
		if (UserInterface.arffDestinationPath == null) {
			JOptionPane.showMessageDialog(null, "Please select a destination for the final file");
			return false;
		}
		if (Integer.valueOf(String.valueOf(UserInterface.kmeansIterSpinner.getValue())) <= 0) {
			JOptionPane.showMessageDialog(null, "K-Means Interations must be greater than 0");
			return false;
		}
		if (Integer.valueOf(String.valueOf(UserInterface.kmeanskSpinner.getValue())) <= 0) {
			JOptionPane.showMessageDialog(null, "K-Means K-value must be greater than 0");
			return false;
		}
		return true;
	}
	
	public static void done() {
		JOptionPane.showMessageDialog(null, "Extraction completed");
		UserInterface.extractButton.setText("Done!");
	}
}
