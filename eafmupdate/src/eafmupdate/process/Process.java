package eafmupdate.process;

import java.io.IOException;

import org.sat4j.specs.TimeoutException;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import eafmupdate.GenerateMutants;
import eafmupdate.model.Oracle;
import splar.core.fm.FeatureModelException;
import splar.core.fm.configuration.ConfigurationEngineException;

public abstract class Process {
	
	//@Deprecated
	//public abstract RepairStats repair(IFeatureModel fmodel, IFeatureModel correct) throws TimeoutException, IOException, FeatureModelException, ConfigurationEngineException, UnsupportedModelException;
	
	public abstract Stats repair(IFeatureModel fmodel, Oracle oracle) throws TimeoutException, IOException, FeatureModelException, ConfigurationEngineException, UnsupportedModelException;

	/** Performs two consecutive repairs: one without using MoveF, the second using it */
	public Stats repair2Steps(IFeatureModel fm, Oracle oracle) throws TimeoutException, IOException, FeatureModelException, ConfigurationEngineException, UnsupportedModelException {
		//GenerateMutants.disableMutant(MoveF.instance); // se l'attributo fosse private, non funziona!! Perché la JVM lo esegue dopo
		//System.out.println(GenerateMutants.mutatorsToExclude);
		Stats s = repair(fm, oracle);
		if (!s.isRepaired()) {
			System.out.println("not repaired "+s.getOneLineStatisticsAsCSV());
			Stats s2 = repair(s.repaired.model, oracle);
			s.append(s2);
			System.out.println("It was not repaired");
		}
		return s;
	}
	
	/** Performs repair without using MoveF */
	public Stats repairWithoutMove(IFeatureModel fm, Oracle oracle) throws TimeoutException, IOException, FeatureModelException, ConfigurationEngineException, UnsupportedModelException {
		//GenerateMutants.disableMutant(MoveF.instance); // se l'attributo fosse private, non funziona!! Perché la JVM lo esegue dopo
		//System.out.println(GenerateMutants.mutatorsToExclude);
		Stats s = repair(fm, oracle);
		return s;
	}
}
