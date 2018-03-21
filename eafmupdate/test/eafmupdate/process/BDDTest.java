package eafmupdate.process;

import java.io.IOException;

import org.junit.Test;
import org.sat4j.specs.TimeoutException;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.editing.NodeCreator;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import eafmupdate.CompareOracleMutantBDD;
import eafmupdate.GenerateUpdateRequest;
import eafmupdate.Util;
import eafmupdate.model.Oracle;
import net.sf.javabdd.BDD;
import splar.core.fm.FeatureModelException;
import splar.core.fm.configuration.ConfigurationEngineException;

public class BDDTest {
	
	@Test
	public void bddTest() {
		try {
			ModelsPair m = ModelsPair.EXAMPLE;
			Oracle o = m.getOracle();
			BDD bdd = CompareOracleMutantBDD.getBDD(o);
			
			BDD bdd2 = o.f2bdd.nodeToBDD(NodeCreator.createNodes(m.getFM2()));
			System.out.println(bdd.satCount());
			bdd = bdd.and(bdd2.not());
			System.out.println(bdd.satCount());
			bdd = CompareOracleMutantBDD.getBDD(o);
			System.out.println(bdd.satCount());
		} catch (Exception e) {e.printStackTrace();}
	}
	
	@Test
	public void adqTest() {
		try {
			for (ModelsPair m : ModelsPair.getModelForExperiments()) {
				System.out.println(m+": "+Util.getAdequacy(m.getFM2(), m.getFM1())+" "+Util.getAdequacy(m.getOracle(), m.getFM1()));
			}
		} catch (IOException | FeatureModelException | ConfigurationEngineException | TimeoutException | UnsupportedModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void URTest() throws TimeoutException, IOException, FeatureModelException, ConfigurationEngineException {
//		Oracle oracle = Models.REGISTER.getOracle();
//		IFeatureModel fm = Models.REGISTER.getFM1();
		
		//Oracle oracle = Models.EXAMPLE.getOracle();
		//IFeatureModel fm = Models.EXAMPLE.getFM1();
		//Oracle oracle = Models.PPU1.getOracle();
		//IFeatureModel fm = Models.PPU1.getFM1();
		
		Oracle oracle = ModelsPair.EXAMPLE_XOR.getOracle();
		IFeatureModel fm = ModelsPair.EXAMPLE_XOR.getFM1();
		
		System.out.println(GenerateUpdateRequest.computeProductsToAddOrRemove(oracle, fm, true) + "\n"
				+ GenerateUpdateRequest.computeProductsToAddOrRemove(oracle, fm, false) + "\nTo Add: "
				+ GenerateUpdateRequest.generateProductsToAdd(oracle.oracleFM, fm) + "\nToRemove: "
				+ GenerateUpdateRequest.generateProductsToRemove(oracle.oracleFM, fm));
		System.out.println("FR: "+(1-Util.getAdequacy(oracle, fm)));
	}
	
	@Test
	public void PPUTest() throws TimeoutException, IOException, FeatureModelException, ConfigurationEngineException, UnsupportedModelException {
		IFeatureModel fm1 = ModelsPair.PPU1_2.getFM2();
		IFeatureModel fm2 = ModelsPair.PPU1_2.getFM1();
		System.out.println(GenerateUpdateRequest.computeProductsToAddOrRemove(fm1,fm2, true) + "\n"
				+ GenerateUpdateRequest.computeProductsToAddOrRemove(fm1,fm2, false) + "\nTo Add: "
				+ GenerateUpdateRequest.generateProductsToAdd(fm1,fm2) + "\nToRemove: "
				+ GenerateUpdateRequest.generateProductsToRemove(fm1,fm2));
		System.out.println("FR: "+(1-Util.getAdequacy(fm1,fm2)));
	}
}
