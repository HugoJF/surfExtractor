surfExtractor
=============

Parameters
 - imageset.path         -> Path to Imageset being extracted
 - imageset.to_classify  -> ?
 - imageset.relation     -> Name of the Imageset being extracted
 - random.seed           -> Random seed
 - arff.relation         -> Arff relation(name)
 - arff.path             -> Where the final .arff file should be saved
 - kmeans.iteration      -> How many times the clustering algorithm will recalculate the centroids
 - kmeans.kvalue         -> The value of K for k-means algorithm
 - cluster.path          -> Where to save clusters information

Extracts SURF features from images and output them as .arff files for WEKA.

- [ ] documentate how to use, and usable parameters
- [ ] add verification to ImageSet.java ImageClass.java methods if created via empty constructor
- [ ] add configuration for cluster exportation
- [ ] add configuration for cluster importation, ignoring the feature clustering step
- [x] ~~create project for batch extration (work around is using .bat files)~~
- [x] ~~create project for batch Weka experimentation~~
- [ ] use Weka as file exporter
- [ ] force GUI command
- [ ] improve GUI
- [ ] add some kind of framework for easily add new feature extractors (maybe a new side project working with surfExtractor as a library)
- [ ] update current classes for external use (since they're being frequently used as a library now)
- [ ] image enhancement code
- [ ] data normalization, data rescaling options
- [ ] add support for context menu
