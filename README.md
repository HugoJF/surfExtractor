# SURF Attributes Extractor

## Current status: DEPRECATED

Rewrite of a old software that was used in the research group I was part of. The main reasons I rewrote it from scratch were: needed cross-platform support (some members used Windows, the majority of them used Linux and some of them even Mac), the old software was completely unsupported, full of errors, slow and hard to work with.

This rewrite achieved very important goals to help researchers dependent on SURF attribute extraction:
- Up to 80% reduction in compute time
- Multi-platform support (avoiding virtualization overhead)
- Improved the workflow between tools (Weka, R, SurfExtractor, etc)
- Ease of use
- Faster update cycles
- GUI capabilities
- More control over SURF parameters (allowing Bash scripting)
- A few other small things such as better logging, easy integration, better maintenance, better dependency control, etc...

I had plans to completely integrate Weka clusterization, importing, exporting libraries to make it more flexible and improve the workflow from SURF Extractor to Weka. 

## What is does
This software runs over an Image Set (at the time, 23 sets with 35 images each), caches each Image in memory (if possible), extracts SURF attributes from each image of each set, clusters the attribute pool using KMeans (saves to disk to avoid re-clustering each re-pass), runs Bag Of Words classifier over the attribute pool and resulting cluster centers, produces a normalized histogram for each image (frequency of each attribute cluster in the image), and export the histogram as a [ARFF](https://www.cs.waikato.ac.nz/ml/weka/arff.html) file to be used in Weka 3.6.6 for further experimentation and classification.

## Used in this project
- Java 1.7
- Maven
- BoofCV 0.17
- Log4j 2.0.2
- ImageJ 1.47
- Weka 3.6.6

## Future

I have no plans on updating this project since it was replaced with a newer Python 3 version re-write that can support modern AI, Computer Vision, and Machine Learning libraries (as Python seems to be the chosen language for researchers while also allowing faster prototyping).

## Usage

<strong> `java -jar "surfExtractor.jar"` </strong> 
`[—imageset.path <path>]` `[—imageset.relation <relation>]` `[—random.seed <seed>]` `[—arff.relation <relation>]` `[—arff.path <path>]` `[—kmeans.iteration <iterations>]` `[—kmeans.kvalue <kvalue>]` `[—cluster.save_path <path>]` `[—cluster.load_path <path>]` `[—surf.radius <radius>]` `[—surf.threshold <threshold>]` `[—surf.ignoreborder <ignoreborder>]` `[—surf.strictrule <strictrule>]` `[—surf.maxfeaturesperscale <mfps>]` `[—surf.numberscalesperoctave <nspo>]` `[—surf.numberofoctaves <octaves>]`

## Startup Parameters

|Parameters | Description |
|-----------|-------------|
|**imageset.path**|Path to Imageset being extracted|
|**imageset.relation**|Name of the Imageset being extracted|
|**random.seed**|Random seed|
|**arff.relation**|Arff relation(name)|
|**arff.path**|Where the final .arff file should be saved|
|**kmeans.iteration**|How many times the clustering algorithm will recalculate the centroids|
|**kmeans.kvalue**|The value of K for k-means algorithm|
|**cluster.save_path**|Where to save clusters information|
|**cluster.load_path**|From where to load cluster information|
|**surf.radius**|SURF algorithm parameter|
|**surf.threshold**|SURF algorithm parameter|
|**surf.ignoreborder**|SURF algorithm parameter|
|**surf.strictrule**|SURF algorithm parameter|
|**surf.maxfeaturesperscale**|SURF algorithm parameter|
|**surf.initialsamplerate**|SURF algorithm parameter|
|**surf.initialsize**|SURF algorithm parameter|
|**surf.numberscalesperoctave**|SURF algorithm parameter|
|**surf.numberofoctaves**|SURF algorithm parameter|
