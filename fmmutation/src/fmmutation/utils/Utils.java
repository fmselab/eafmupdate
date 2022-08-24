package fmmutation.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sat4j.specs.TimeoutException;

import de.ovgu.featureide.fm.core.ConstraintAttribute;
import de.ovgu.featureide.fm.core.FeatureModelAnalyzer;
import de.ovgu.featureide.fm.core.analysis.cnf.formula.FMAnalyzerCreator;
import de.ovgu.featureide.fm.core.base.IConstraint;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.impl.DefaultFeatureModelFactory;
import de.ovgu.featureide.fm.core.base.impl.FMFactoryManager;
import de.ovgu.featureide.fm.core.explanations.fm.impl.composite.CompositeRedundantConstraintExplanationCreator;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import de.ovgu.featureide.fm.core.io.manager.FileHandler;
import de.ovgu.featureide.fm.core.io.sxfm.SXFMFormat;
import de.ovgu.featureide.fm.core.io.xml.XmlFeatureModelFormat;
import splar.core.fm.FeatureModelException;
import splar.core.fm.FeatureModelStatistics;
import splar.core.fm.XMLFeatureModel;

/** fmautorepair.utils to read models */
public class Utils {

	static public IFeatureModel readSPLOTModel(String path) throws FileNotFoundException, UnsupportedModelException {
		// see
		// https://github.com/FeatureIDE/FeatureIDE/blob/develop/tests/de.ovgu.featureide.fm.core-test/src/de/ovgu/featureide/fm/core/io/sxfm/Experiment_ConvertSPLOTmodels.java
		// read the same SPLOT file using the FeatureiDE reader
		final IFeatureModel fm_original = DefaultFeatureModelFactory.getInstance().create();
		SXFMFormat format = new SXFMFormat();
//		final ProblemList problems = 
		FileHandler.load(new File(path).toPath(), fm_original, format);
		return fm_original;
	}

	static public IFeatureModel readSGUIDSL(String path) throws FileNotFoundException, UnsupportedModelException {
		final IFeatureModel fm_original = DefaultFeatureModelFactory.getInstance().create();
		XmlFeatureModelFormat format = new XmlFeatureModelFormat();
		FileHandler.load(new File(path).toPath(), fm_original, format);
		return fm_original;

	}

	static public splar.core.fm.FeatureModel getSplotModel(String featureModelPath) throws FeatureModelException {
		// Create feature model object from an XML file (SXFM format - see
		// www.splot-research.org for details)
		// If an identifier is not provided for a feature use the feature name
		// as id
		splar.core.fm.FeatureModel IFeatureModel = new XMLFeatureModel(featureModelPath,
				XMLFeatureModel.USE_VARIABLE_NAME_AS_ID);
		// load feature model from
		IFeatureModel.loadModel();
		// Now, let's print some statistics about the feature model
		FeatureModelStatistics stats = new FeatureModelStatistics(IFeatureModel);
		stats.update();

		// stats.dump();
		return IFeatureModel;
	}

	public static Set<String> getFeatureNames(IFeatureModel fm) {
		Set<String> fnames = new HashSet<>();
		for (IFeature a : fm.getFeatures())
			fnames.add(a.getName());
		return fnames;
	}

	/**
	 * If the candidate has no dead features, no redundant constraints
	 * 
	 * @param candidate the candidate mutated model
	 * @return if the mutation is valid
	 */
	public static boolean isOk(IFeatureModel candidate) {
		FeatureModelAnalyzer analyzer = new FeatureModelAnalyzer(candidate);
		List<IConstraint> redConstraints = analyzer.getRedundantConstraints(null);
		if (!redConstraints.isEmpty()) {
			return false;
		}
		if (analyzer.getDeadFeatures(null).size() > 0) {
			// System.out.println("Dead features:
			// "+candidate.model.getAnalyser().getDeadFeatures().size());
			return false;
		}
//			for (IConstraint ctr : candidate.getConstraints()) {
//				if (ctr.getConstraintAttribute()==ConstraintAttribute.REDUNDANT) {
//					return false;
//				}
//			}
		return true;
	}

}
