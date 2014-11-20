surfExtractor
=============

Parameters
<table>
<tbody>
<tr><td><b>imageset.path</b></td><td>Path to Imageset being extracted</td></tr>
<tr><td><b>imageset.to_classify</b></td><td>?</td></tr>
<tr><td><b>imageset.relation</b></td><td>Name of the Imageset being extracted</td></tr>
<tr><td><b>random.seed</b></td><td>Random seed</td></tr>
<tr><td><b>arff.relation</b></td><td>Arff relation(name)</td></tr>
<tr><td><b>arff.path</b></td><td>Where the final .arff file should be saved</td></tr>
<tr><td><b>kmeans.iteration</b></td><td>How many times the clustering algorithm will recalculate the centroids</td></tr>
<tr><td><b>kmeans.kvalue</b></td><td>The value of K for k-means algorithm</td></tr>
<tr><td><b>cluster.path</b></td><td>Where to save clusters information</td></tr>
</tbody>
</table>

Extracts SURF features from images and output them as .arff files for WEKA.

- [ ] documentate how to use, and usable parameters
- [ ] ~~add verification to ImageSet.java ImageClass.java methods if created via empty constructor~~
- [ ] add configuration for cluster exportation
- [ ] add configuration for cluster importation, ignoring the feature clustering step
- [ ] ~~create project for batch extration (work around is using .bat files)~~
- [ ] ~~create project for batch Weka experimentation~~
- [ ] use Weka as file exporter
- [ ] ~~force GUI command~~
- [ ] ~~improve GUI~~
- [ ] add some kind of framework for easily add new feature extractors (maybe a new side project working with surfExtractor as a library)
- [ ] update current classes for external use (since they're being frequently used as a library now)
- [ ] image enhancement code
- [ ] data normalization, data rescaling options
- [ ] ~~add support for context menu~~
