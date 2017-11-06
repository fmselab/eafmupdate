package eafmupdate.process;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import eafmupdate.MutatedModel;
import eafmupdate.Util;
import eafmupdate.process.Models;
import eafmupdate.process.Process;
import eafmupdate.process.Processes;
import eafmupdate.process.RepairTag;
import eafmupdate.process.Stats;

/**
 * To generate data for all the experiments in the report.
 * Multiple models and multiple repair processes
 * @author marcoradavelli
 */
public class Experiments {
	
	public static final ExperimentType experimentType = ExperimentType.WITHOUT_MOVE;
	
	public static final boolean useOnlyOneProcess = false;
	
	public static final boolean useOnlyOneModel = false;
	
	public static final boolean SAVE_TEMP = false;
	
	public static final boolean otherExperiment = false;
	
	public static Map<Models,Integer> getModels() {
		if (useOnlyOneModel) {
			Map<Models,Integer> models = new HashMap<>();
			models.put(Models.MOBILE_MEDIA_V5_TO_V6, 1);
			return models;
		} else return Models.getModelsForExperiments();
	}
	
	public static Processes[] getUsefulRepairProcesses() {
		if (useOnlyOneProcess) return new Processes[] {Processes.RANDOM};
		return Processes.getUsefulProcesses();
	}
	
	@Test
	public void experiment() {
		try {
			List<Stats> allRepairs = new ArrayList<>();
			PrintWriter fout = new PrintWriter(new FileWriter("output_"+getExperimentName()+".txt"));

			StringBuilder sb1 = new StringBuilder();
			sb1.append("repair,model,iteration,nmut,mutation,ADQbefore,ADQafter,deltaED,EDbefore,EDafter,EDbeforeInitial,EDafterInitial,COMPbefore,COMPafter,description\n");
			StringBuilder sb2 = new StringBuilder();
			sb2.append("id,repair,model,iteration,numSeededMutations,ADQbefore,ADQafter,ED,EDCheatbefore,EDCheatafter,EDbeforeInitial,EDafterInitial,COMPbefore,COMPafter,mutations,time,avg,max,repaired\n");

			for (Entry<Models,Integer> m : getModels().entrySet()) {
				Models model = m.getKey();
				int iterations = m.getValue();
//				IFeatureModel initialModel = null;
				int numMutations=-1;
//				if (model.experimentType) {
//					initialModel = model.getFM1();
//				} else {
//					numMutations = (int)(Math.random()*11);
//					initialModel = Util.mutateRandomly(new MutatedModel(model.getFM2()), numMutations).model;
//				}
				
				for (Processes repair : getUsefulRepairProcesses()) {
					System.out.println("Model "+model.name() + " with repair "+repair.name());
					int numRepaired = 0;
					
					//if (experimentType==ExperimentType.WITHOUT_MOVE) GenerateMutants.disableMutant(MoveF.instance);
					//else GenerateMutants.enableAllMutants();
					
					for (int i=0; i<iterations; i++) {
						// mutate the model
						MutatedModel mutant = new MutatedModel(model.getMutatedModel(i));
//						if (model.experimentType) {
//							mutant = new MutatedModel(model.getFM1());
//						} else {
//							numMutations = (int)(Math.random()*10);
//							mutant = Util.mutateRandomly(new MutatedModel(model.getFM2()), numMutations);
//						}
//						// repair the model
						Process repairProcess = repair.getRepairProcess(mutant.model, model.getOracle());
						Stats stats = experimentType==ExperimentType.TWO_STEPS ? repairProcess.repair2Steps(mutant.model, model.getOracle()) : repairProcess.repair(mutant.model, model.getOracle());
						
						stats.repairProcess = repair;
						stats.correct = model.getFM2();
						//System.out.println(stats.getStatistics());
						if (SAVE_TEMP) Util.saveTemporary(stats.getRepairedModel(), "output/temp/", model.name()+"_"+i);
						stats.tag = new RepairTag(model,i);
						stats.seededMutationsOfTheInitialModel = numMutations;
						allRepairs.add(stats);
						
						boolean repaired = stats.isRepaired();
						if (repaired) numRepaired++;
						fout.println(i+"\t"+numMutations+"\t"+repaired);
						fout.flush();
					}
					fout.println("Repaired for "+model+": "+numRepaired+" / "+ iterations + " with "+repair.name());
					fout.flush();
				}
				
				for (Stats stats : allRepairs) {
					sb1.append(stats.getMutationStatisticsAsCSV(stats.repairProcess+","+ stats.tag.toString(), true));
				}

				int id=1;
				for (Stats stats : allRepairs) {
					sb2.append((id++) +","+stats.repairProcess+ ","+stats.tag.toString()+","+stats.getOneLineStatisticsAsCSV()+"\n");
				}
				
				allRepairs.clear();
			}
			fout.close();
			
			
			String name = getExperimentName();
			System.out.println("Generate CSV bests...");
			Util.saveTemporary(sb1.toString(), null, name+"_bests.csv"); // the history of the "repaired" model
			System.out.println("Generate CSV bests oneLine...");
			Util.saveTemporary(sb2.toString(), null, name+"_bests_oneLine.csv");  // the synthesis.
			System.out.println("Generate CSV bests oneLine for Charts...");
			generateFix();
			
		} catch (Exception e) {e.printStackTrace();}
	}
	
	private String getExperimentName() {
		return otherExperiment ? "expOther" : (useOnlyOneModel ? "expA" : (useOnlyOneProcess ? "expB" : "expC"));
	}
	
	@Test
	public void generateFix() {
		String name = getExperimentName();
		fixExperiment2ForPlot(name+"_bests.csv", name+"_bests_forPlot.csv", Processes.MAXORDER);
	}
	
	public static void fixExperiment2ForPlot(String pathInput, String pathOutput, int maxIteration) {
		try {
			BufferedReader fin = new BufferedReader(new FileReader("output/"+pathInput));
			PrintWriter fout = new PrintWriter(new FileWriter("output/"+pathOutput));
			String s="";
			int lastMut=-1;
			String left="",right="";
			while ((s=fin.readLine())!=null) {
				String[] st = s.split(",");
				int mut=-1;
				try { mut = Integer.parseInt(st[3]); } catch (Exception e) {}
				if (lastMut!=-1 && mut!=lastMut+1) {
					for (int i=lastMut+1; i<=maxIteration; i++) {
						fout.println(left+i+right);
					}
				}
				lastMut=mut;
				if (st.length>3) {
					left = st[0]+","+st[1]+","+st[2]+",";
					right = ","+",,"+st[6]+",,,"+st[9]+",,"+st[11]+",,"+st[13] +",";
				}
				
				if (st.length>3 && st[3].equals("1")) {
					fout.println(st[0]+","+st[1]+","+st[2]+",0,"+",,"+st[5]+",,,"+st[8]+",,"+st[10]+",,"+st[12] +",");					
				}
				fout.println(s);
				
			}
			fout.close();
			fin.close();
		} catch (Exception e) {e.printStackTrace();}
	}
	
}
