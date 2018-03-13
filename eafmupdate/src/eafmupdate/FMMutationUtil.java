package eafmupdate;

import java.io.IOException;

import org.sat4j.specs.TimeoutException;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import eafmupdate.model.Oracle;
import fmautorepair.mutationoperators.FMMutation;
import splar.core.fm.FeatureModelException;
import splar.core.fm.configuration.ConfigurationEngineException;

public class FMMutationUtil {
	private static double getActualAdequacy(FMMutation m, Oracle oracle) throws TimeoutException, IOException, UnsupportedModelException, FeatureModelException, ConfigurationEngineException { return Util.getAdequacy(oracle, m.getFirst()); }
	private static double getPreviousAdequacy(FMMutation m, Oracle oracle) throws TimeoutException, IOException, UnsupportedModelException, FeatureModelException, ConfigurationEngineException { return Util.getAdequacy(oracle, m.getPreviousModel()); }
	public static double getDeltaAdequacy(FMMutation m, Oracle oracle) throws TimeoutException, IOException, FeatureModelException, ConfigurationEngineException, UnsupportedModelException { return getActualAdequacy(m, oracle)-getPreviousAdequacy(m, oracle); }
	
	private static double getActualCompactness(FMMutation m) { return Util.getCompactness(m.getFirst()); }
	private static double getPreviousCompactness(FMMutation m) { return Util.getCompactness(m.getPreviousModel()); }

	/** @return some statistics in CSV format:
	 * mutationName; ADQbefore;ADQafter; deltaED;EDbefore;EDafter
	 * the editDistance is w.r.t. the target model 
	 * @throws UnsupportedModelException 
	 * @throws ConfigurationEngineException 
	 * @throws FeatureModelException 
	 * @throws IOException 
	 * @throws TimeoutException */
	public static String getStatistics(FMMutation m, Oracle oracle, IFeatureModel target, IFeatureModel initial) throws TimeoutException, IOException, FeatureModelException, ConfigurationEngineException, UnsupportedModelException {
		return m.getMutationClass().getSimpleName()
				+","+getPreviousAdequacy(m, oracle)
				+","+getActualAdequacy(m, oracle)
				+","+getPreviousCompactness(m)
				+","+getActualCompactness(m)
				;
	}
	
	public static String getStatisticsWithoutCompactness(FMMutation m, Oracle oracle, IFeatureModel target, IFeatureModel initial) throws TimeoutException, IOException, FeatureModelException, ConfigurationEngineException, UnsupportedModelException {
		return m.getMutationClass().getSimpleName()
				+","+getPreviousAdequacy(m, oracle)
				+","+getActualAdequacy(m, oracle)
				;
	}

}
