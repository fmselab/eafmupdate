package eafmupdate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.sat4j.specs.TimeoutException;

import de.ovgu.featureide.fm.core.ExtensionManager.NoSuchExtensionException;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.editing.Comparison;
import de.ovgu.featureide.fm.core.editing.ModelComparator;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import de.ovgu.featureide.fm.core.io.xml.XmlFeatureModelFormat;
import fmmutation.mutationoperators.FMMutation;
import fmmutation.mutationoperators.FMMutator;
import fmmutation.mutationoperators.features.AlToAnd;
import fmmutation.mutationoperators.features.AlToAndOpt;
import fmmutation.mutationoperators.features.AlToOr;
import fmmutation.mutationoperators.features.AndToAl;
import fmmutation.mutationoperators.features.AndToOr;
import fmmutation.mutationoperators.features.ManToOpt;
import fmmutation.mutationoperators.features.OptToMan;
import fmmutation.mutationoperators.features.OrToAl;
import fmmutation.mutationoperators.features.OrToAnd;
import fmmutation.mutationoperators.features.OrToAndOpt;
import fmmutation.utils.CollectionsUtil;
import fmmutation.utils.Utils;
import fmupdate.models.ExampleTaker;
import splar.core.fm.FeatureModelException;
import splar.core.fm.configuration.ConfigurationEngineException;

public class GenerateMutants {
	// Original one
//	static FMMutator[] mutators = new FMMutator[]{AlternativeToAnd.instance, AlternativeToAndOpt.instance, AltToOr.instance,
//													AndToAlternative.instance, AndToOr.instance, MandToOpt.instance,
//													/*MissingFeature.instance,*/ OptionalToMandatory.instance, OrToAltenative.instance,
//													OrToAnd.instance, OrToAndOpt.instance};
	
	
	FMMutator getRandomMutator(List<FMMutator> fmMutators) {
		//List<FMMutator> fmMutators = getFmMutators();
		FMMutator m = fmMutators .get(rnd.nextInt(fmMutators.size()));
		return m;
	}
	
	public static GenerateMutants instance = new GenerateMutants();	
	protected GenerateMutants() {}
	
	/**
	 * 
	 * @return a mutator radomnly selected among the mutators from getFmMutators (it can be extended)
	 */
	public final FMMutator getRandomMutator() {
		return getRandomMutator(getFmMutators());
	}
	
	public List<FMMutator> getFmMutators(){
		return Arrays.asList( 
		//MissingFeature.instance,   // not possible to remove features
		//ConstraintRemover.instance,
		AlToAnd.instance,
		AlToOr.instance, 
		AlToAndOpt.instance,
		OrToAl.instance, 
		OrToAnd.instance, 
		OrToAndOpt.instance,
		AndToOr.instance, 
		ManToOpt.instance,
		OptToMan.instance, 
		AndToAl.instance
		//NegationMutant.instance,
		//LogicAndToOr.instance,
		//LogicOrToAnd.instance,
		//ConstraintSubstitute.instance,
		//LiteralChanger.instance,
		//ConstraintAdder.instance, 
		//IffToImplies.instance, 
		//ImpliesToIff.instance,
		//RequiresToExcludes.instance,
		//ExcludesToRequires.instance,
		//MoveF.instance,    //XXX for lighter version, removed support for move operation
		// ExcludesAsCNFToRequires.instance,
		// RequiresAsCnfToExcludes.instance,
	);
				};
	
	/*private static FMMutator[] getFMMutators(Neighbors neighbors) {
		return new FMMutator[] { 
				RemoveFeature.instance, 
				//ConstraintRemover.instance,
				AltToAnd.instance,
				AltToOr.instance, 
				AltToAndOpt.instance,
				OrToAlt.instance, 
				OrToAnd.instance, 
				OrToAndOpt.instance,
				AndToOr.instance, 
				MandToOpt.instance,
				OptToMand.instance, 
				AndToAlt.instance,
				//NegationMutant.instance,
				//LogicAndToOr.instance,
				//LogicOrToAnd.instance,
				//ConstraintSubstitute.instance,
				//LiteralChanger.instance,
				//ConstraintAdder.instance, 
				//IffToImplies.instance, 
				//ImpliesToIff.instance,
				//RequiresToExcludes.instance,
				//ExcludesToRequires.instance,
				MoveFeature.instance,
		// ExcludesAsCNFToRequires.instance,
		// RequiresAsCnfToExcludes.instance,
		};
	}*/
		
	
	public Random rnd = new Random();

	private static void init() {
		org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);
	}

	/*public static Set<MyFMMutation> generateMutantsWithSet(IFeatureModel fmodel, int depth, double percSelect) {
		assert depth > 0: "depth must be greater than 0 - depth = " + depth;
		Set<MyFMMutation> currMutants = new HashSet<MyFMMutation>();
		for(FMMutator mutator: mutators) {
			List<FMMutation> res = CollectionsUtil.listFromIterator(mutator.mutate(fmodel));
			for(FMMutation r: res) {
				currMutants.add(new MyFMMutation(r));
			}
		}

		int toRemove = (int)(currMutants.size()*(1 - percSelect));
		for(int i = 0; i < toRemove; i++) {
			currMutants.remove(rnd.nextInt(currMutants.size()));
		}
		Set<MyFMMutation> mutants = new HashSet<MyFMMutation>();
		if(depth == 1) {
			mutants.addAll(currMutants);
		}
		else {
			for(MyFMMutation currMutant: currMutants) {
				IFeatureModel fmMut = currMutant.getMutation();
				mutants.addAll(generateMutantsWithSet(fmMut, depth - 1, percSelect));
			}
		}
		return mutants;
	}*/

	List<FMMutation> generateMutantsNoSet(IFeatureModel fmodel, int depth, double percSelect) {
		assert depth > 0: "depth must be greater than 0 - depth = " + depth;
		assert percSelect > 0 && percSelect <= 1: "percSelect not allowed: " + percSelect;
		//System.err.println("depth = " + depth);
		List<FMMutation> mutants = new ArrayList<FMMutation>();
		for(FMMutator mutator: instance.getFmMutators()) {
			List<FMMutation> currMutants = CollectionsUtil.listFromIterator(mutator.mutate(fmodel));
			
			
			int toRemove = (int)(currMutants.size()*(1 - percSelect));
			for(int i = 0; i < toRemove; i++) {
				currMutants.remove(rnd.nextInt(currMutants.size()));
			}
			
			/*if(currMutants.size() > 2 ) {
				List<FMMutation> selected = new ArrayList<FMMutation>();
				for(int i = 0; i < 2; i++) {
					selected.add(currMutants.get(rnd.nextInt(currMutants.size())));
				}
				currMutants = selected;
			}*/
			
			
			if(depth == 1) {
				mutants.addAll(currMutants);
			}
			else {
				for(FMMutation currMutant: currMutants) {
					IFeatureModel fmMut = currMutant.getFirst();
					mutants.addAll(generateMutantsNoSet(fmMut, depth - 1, percSelect));
				}
			}
		}
		return mutants;
	}

	private static void saveMutatedModel(IFeatureModel IFeatureModel, String name, boolean description) throws IOException {
		if(description) {
			for(String comment: IFeatureModel.getProperty().getComments()) {
				if(comment.contains(" from ")) {
					name = name + "_" + comment.replace("mutation ", "").replaceAll(" from ", "_").replaceAll(" ", "");
				}
			}
		}
		//SXFMWriter writer = new SXFMWriter(mutatedModel);
		//System.out.println(writer.writeToString());
		FileWriter fw = new FileWriter(new File(name + ".xml"));
		fw.write(new XmlFeatureModelFormat().write(IFeatureModel));
		fw.close();
	}

	private static void saveMutatedModel(FMMutation mutation, String name, boolean description) throws IOException {
		saveMutatedModel(mutation.getFirst(), name, description);
	}

	public static void removeEquivalents(String model, String folderMutants) throws FileNotFoundException, UnsupportedModelException, NoSuchExtensionException {
		IFeatureModel fm = Utils.readSPLOTModel(model);
		File dir = new File(folderMutants);
		assert dir.isDirectory();
		ModelComparator comparator = new ModelComparator(1000000);
		int counter = 0;
		for(File file: dir.listFiles()) {
			IFeatureModel mutant = ExampleTaker.readExample(file.getAbsolutePath());
			Comparison comparison = comparator.compare(fm, mutant);
			if(comparison == Comparison.REFACTORING) {
				counter++;
				file.delete();
			}
		}
		System.out.println(counter);
	}

	public static void removeEquivalents(String folderMutants) throws FileNotFoundException, UnsupportedModelException, NoSuchExtensionException {
		File dir = new File(folderMutants);
		assert dir.isDirectory();
		ModelComparator comparator = new ModelComparator(1000000);
		int counter = 0;
		for(File file: dir.listFiles()) {
			if(!file.exists()) continue; 
			IFeatureModel mutant = ExampleTaker.readExample(file.getAbsolutePath());
			File sameDir = new File(folderMutants);
			for(File file2: sameDir.listFiles()) {
				if(!file2.getName().equals(file.getName())) {
					IFeatureModel mutant2 = ExampleTaker.readExample(file2.getAbsolutePath());
					Comparison comparison = comparator.compare(mutant, mutant2);
					if(comparison == Comparison.REFACTORING) {
						counter++;
						file2.delete();
					}
				}
			}
		}
		System.out.println(counter);
	}

	public void generateMutantsFromPreviousSet(String path, double percSelect, int depth) throws UnsupportedModelException, IOException, NoSuchExtensionException {
		init();
		File dir = new File(path);
		assert dir.isDirectory();
		ModelComparator comparator = new ModelComparator(1000000);
		for(File file: dir.listFiles()) {
			if(!file.exists()) continue;
			String fileName = file.getName().replaceAll("depth" + (depth - 1), "depth" + depth);
			fileName = fileName.substring(0, fileName.length() - 4);
			IFeatureModel fmmodel = ExampleTaker.readExample(file.getAbsolutePath());
			List<FMMutation> currMutants = new ArrayList<FMMutation>();
			for(FMMutator mutator: instance.getFmMutators()) {
				currMutants.addAll(CollectionsUtil.listFromIterator(mutator.mutate(fmmodel)));						
			}
			/*int toRemove = (int)(currMutants.size()*(1 - percSelect));
			for(int i = 0; i < toRemove; i++) {
				currMutants.remove(rnd.nextInt(currMutants.size()));
			}
			for(FMMutation m: currMutants) {*/
			List<FMMutation> selectedMutants = new ArrayList<FMMutation>();
			int toSelect = (int)(currMutants.size()*percSelect);
			//System.out.println(toSelect);
			for(int i = 0; i < toSelect; i++) {
				FMMutation mut = currMutants.get(rnd.nextInt(currMutants.size()));
				if(comparator.compare(fmmodel, mut.getFirst()) != Comparison.REFACTORING) {
					selectedMutants.add(mut);
				}
				else {
					i--;
				}
			}
			for(FMMutation m: selectedMutants) {
				//saveMutatedModel(m, fmmodel.get);
				String lastComment = "";
				for (String s : m.getFirst().getProperty().getComments()) lastComment=s;
				String name = fileName + "_" + lastComment.replace("mutation ", "").replaceAll(" from ", "_").replaceAll(" ", "") + ".xml";
				//System.out.println(writer.writeToString());
				FileWriter fw = new FileWriter(new File(name));
				fw.write(new XmlFeatureModelFormat().write(m.getFirst()));
				fw.close();
			}
		}
	}

	public void mutate(String modelPath, int depth, double percMutants) throws IOException, UnsupportedModelException {
		init();
		IFeatureModel fm = Utils.readSPLOTModel(modelPath);
		List<FMMutation> mutants = generateMutantsNoSet(fm, depth, percMutants);
		System.out.println("all mutants = " + mutants.size());
		
		//per eliminare gli equivalenti
		/*Set<MyFMMutation> mutantsNoEquiv = new HashSet<MyFMMutation>();
		MyFMMutation orig = new MyFMMutation(fm);
		mutantsNoEquiv.add(orig);
		for(FMMutation m: mutants) {
			mutantsNoEquiv.add(new MyFMMutation(m));
		}
		mutantsNoEquiv.remove(orig);
		System.out.println(mutantsNoEquiv.size());*/

		String modelName = new File(modelPath).getName();
		modelName = modelName.substring(0, modelName.length() - 4);
		for(FMMutation m: mutants) {
			saveMutatedModel(m, modelName + "_depth" + depth, true);
		}
	}

	List<IFeatureModel> generateNmutants(IFeatureModel fmodel, int numMutants, List<IFeatureModel> mutants) {
		FMMutator[] fmMutators = instance.getFmMutators().toArray(new FMMutator[0]);
		numMutants--;
		if(numMutants >= 0) {
			List<FMMutation> currMutants;
			do {
				FMMutator mutator = fmMutators[rnd.nextInt(fmMutators.length)];
				currMutants = CollectionsUtil.listFromIterator(mutator.mutate(fmodel));
			}
			while(currMutants.size() == 0);
			IFeatureModel mutant = currMutants.get(rnd.nextInt(currMutants.size())).getFirst();
			mutants.add(mutant);
			return generateNmutants(mutant, numMutants, mutants);
		}
		else {
			return mutants;
		}
	}
	
	public void generateNmutants(String modelPath, int numMutants) throws IOException, UnsupportedModelException, NoSuchExtensionException {
		init();
		IFeatureModel fm = ExampleTaker.readExample(modelPath);
		List<IFeatureModel> mutants = new ArrayList<>();
		generateNmutants(fm, numMutants, mutants);

		String modelName = new File(modelPath).getName();
		modelName = modelName.substring(0, modelName.length() - 4);
		for(int i = 0; i < numMutants; i++) {
			saveMutatedModel(mutants.get(i), modelName + "_" + (i + 1), false);
		}
	}

	public static void getAdequacy(String oraclePath, String mutantsPath) throws TimeoutException, IOException, FeatureModelException, ConfigurationEngineException, UnsupportedModelException, NoSuchExtensionException {
		IFeatureModel o = ExampleTaker.readExample(oraclePath);
		for(File f: new File(mutantsPath).listFiles()) {
			IFeatureModel m = ExampleTaker.readExample(f.getAbsolutePath());
			System.out.println(CompareOracleMutantBDD.getConformance(o, m).percConfsJudgedCorrectly());
		}
	}
	
	public static void main(String[] args) throws UnsupportedModelException, IOException, TimeoutException, FeatureModelException, ConfigurationEngineException, NoSuchExtensionException {
		//mutate("splotmodels_new/model_20091225_1547989376.xml", 5, 0.0001);
		//removeEquivalents("splotmodels_new/model_20091225_1547989376.xml", "mutants_model_20091225_1547989376/depth4/");
		//removeEquivalents("mutants_model_20091225_1547989376/depth4/");
		
		//generateMutantsFromPreviousSet("mutants_model_20091225_1547989376/depth4/", 0.1, 5);
		//removeEquivalents("mutants_model_20091225_1547989376/depth5/");
		//generateNmutants("splotmodels_new/featureIDE/REAL-FM-14.xml", 20);
		
		String model = "stack_fm";
		getAdequacy("splotmodels_new/featureIDE/" + model + ".xml", "splotmodels_new/featureIDE/" + model + "/random/");
	}

}

class MyFMMutation {
	private IFeatureModel mutation;

	public MyFMMutation(IFeatureModel m) {
		mutation = m;
	}

	public MyFMMutation(FMMutation m) {
		this(m.getFirst());
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof MyFMMutation) {
			IFeatureModel fm = mutation;
			IFeatureModel fm2 = ((MyFMMutation)o).mutation;
			ModelComparator comparator = new ModelComparator(1000000);
			Comparison comparison = comparator.compare(fm, fm2);
			return comparison == Comparison.REFACTORING;
		}
		return false;
	}

	@Override
	public int hashCode(){
		return mutation.hashCode();
	}

	public IFeatureModel getMutation() {
		return mutation;
	}
}