package fmautorepair.utils;

import java.io.File;
import java.io.FileNotFoundException;

import de.ovgu.featureide.fm.core.ExtensionManager.NoSuchExtensionException;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.impl.FMFactoryManager;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import de.ovgu.featureide.fm.core.io.manager.FileHandler;
import de.ovgu.featureide.fm.core.io.sxfm.SXFMFormat;
import de.ovgu.featureide.fm.core.io.xml.XmlFeatureModelFormat;
import splar.core.fm.FeatureModelException;
import splar.core.fm.FeatureModelStatistics;
import splar.core.fm.XMLFeatureModel;

/** fmautorepair.utils to read models */
public class Utils{

	/** read models from the model project 
	 * @throws NoSuchExtensionException */
	
	static public IFeatureModel readModel(String path) throws FileNotFoundException, UnsupportedModelException, NoSuchExtensionException {
		IFeatureModel fm_original = FMFactoryManager.getDefaultFactory().createFeatureModel();		
		XmlFeatureModelFormat format = new XmlFeatureModelFormat();
		FileHandler.load(new File(path).toPath(), fm_original, format);
		return fm_original;
	}

	static public IFeatureModel readSPLOTModel(String path) throws FileNotFoundException, UnsupportedModelException {
		// see https://github.com/FeatureIDE/FeatureIDE/blob/develop/tests/de.ovgu.featureide.fm.core-test/src/de/ovgu/featureide/fm/core/io/sxfm/Experiment_ConvertSPLOTmodels.java
		// read the same SPLOT file using the FeatureiDE reader
		IFeatureModel fm_original = FMFactoryManager.getDefaultFactory().createFeatureModel();		
		SXFMFormat format = new SXFMFormat();
//		final ProblemList problems = 
				FileHandler.load(new File(path).toPath(), fm_original, format);
		return fm_original;
	}

	static public IFeatureModel readSGUIDSL(String path) throws FileNotFoundException, UnsupportedModelException {
		IFeatureModel fm_original = FMFactoryManager.getDefaultFactory().createFeatureModel();		
		XmlFeatureModelFormat format = new XmlFeatureModelFormat();
		FileHandler.load(new File(path).toPath(), fm_original, format);
		return fm_original;

	}

	static public splar.core.fm.FeatureModel getSplotModel(String featureModelPath) throws FeatureModelException {
		// Create feature model object from an XML file (SXFM format - see
		// www.splot-research.org for details)
		// If an identifier is not provided for a feature use the feature name
		// as id
		splar.core.fm.FeatureModel featureModel = new XMLFeatureModel(featureModelPath,
				XMLFeatureModel.USE_VARIABLE_NAME_AS_ID);
		// load feature model from
		featureModel.loadModel();
		// Now, let's print some statistics about the feature model
		FeatureModelStatistics stats = new FeatureModelStatistics(featureModel);
		stats.update();

		// stats.dump();
		return featureModel;
	}

}
