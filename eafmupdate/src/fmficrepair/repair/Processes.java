package fmficrepair.repair;

import org.uncommons.watchmaker.framework.selection.RankSelection;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.selection.TruncationSelection;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import fmficrepair.Oracle;
import fmficrepair.Util;
//import fmficrepair.repair.ProcessEvolutionary;
import usingwatchmaker.FeatureModelEvaluator;

/**
 * The enum of different repair configurations of the repair processes, to be used.
 * @author marcoradavelli
 */
public enum Processes {
	RANDOM {
		@Override
		public Process getRepairProcess(IFeatureModel model, Oracle oracle) {
			int nrand = MULT* oracle.getFeatureNames().size();
			return new ProcessRandom(nrand, nrand / 20, STEADY_STEPS);
		}
	},
/*	TRUNCATE_ADQ__ED_ONLY_IF_TIED_5PERCENT {
		@Override
		public Process getRepairProcess(IFeatureModel model, Oracle oracle) {
			//int nrand = MULT* oracle.getFeatureNames().size();
			int nrand = getPopulationSize(model);
			return new ProcessEvolutionary(nrand, nrand / 20, 20, -1, -1);
		}
	},*/
//	WATCHMAKER1 {
//		@Override
//		public RepairProcess getRepairProcess(IFeatureModel model, Oracle oracle) {
//			int nrand = MULT * oracle.getFeatureNames().size();
//			return new RepairWatchmaker(nrand, nrand / 20, 20, -1, -1, new FeatureModelEvaluator(oracle, model, FitnessType.ADQ_ED_COMP), new TruncationSelection(.2));
//		}
//	},
//	WATCHMAKER2 {
//		@Override
//		public RepairProcess getRepairProcess(IFeatureModel model, Oracle oracle) {
//			int nrand = MULT * oracle.getFeatureNames().size();
//			return new RepairWatchmaker(nrand, nrand / 20, 20, -1, -1, new FeatureModelEvaluator(oracle, model, FitnessType.ADQ_ED_COMP), new RankSelection());
//		}
//	},
	RANK_ELITISM_5PERCENTO_ADQ { // the old watchmaker3
		@Override
		public Process getRepairProcess(IFeatureModel model, Oracle oracle) {
			int nrand = getPopulationSize(model);
			return new ProcessWatchmaker(nrand, nrand / 20, MAXORDER, -1, -1, new FeatureModelEvaluator(oracle, model), new RankSelection());
		}
	},
	RANK_ADQ { // the old watchmaker3
		@Override
		public Process getRepairProcess(IFeatureModel model, Oracle oracle) {
			int nrand = getPopulationSize(model);
			return new ProcessWatchmaker(nrand, 0, MAXORDER, STEADY_STEPS, -1, new FeatureModelEvaluator(oracle, model), new RankSelection());
		}
	},
	RANK_ADQ_ED_COMP { // the old watchmaker3
		@Override
		public Process getRepairProcess(IFeatureModel model, Oracle oracle) {
			int nrand = getPopulationSize(model);
			return new ProcessWatchmaker(nrand, 0, MAXORDER, -1, -1, new FeatureModelEvaluator(oracle, model), new RankSelection());
		}
	},
	ROULETTE_WHEEL_ADQ {
		@Override
		public Process getRepairProcess(IFeatureModel model, Oracle oracle) {
			int nrand = getPopulationSize(model);
			return new ProcessWatchmaker(nrand, 0, MAXORDER, STEADY_STEPS, -1, new FeatureModelEvaluator(oracle, model), new RouletteWheelSelection());
		}
	},
	ROULETTE_WHEEL_ADQ_ED_COMP {
		@Override
		public Process getRepairProcess(IFeatureModel model, Oracle oracle) {
			int nrand = getPopulationSize(model);
			return new ProcessWatchmaker(nrand, 0, MAXORDER, -1, -1, new FeatureModelEvaluator(oracle, model), new RouletteWheelSelection());
		}
	},
	TRUNCATION_1PERCENT {
		@Override
		public Process getRepairProcess(IFeatureModel model, Oracle oracle) {
			int nrand = getPopulationSize(model);
			return new ProcessWatchmaker(nrand, 0, MAXORDER, STEADY_STEPS, -1, new FeatureModelEvaluator(oracle, model), new TruncationSelection(0.01));
		}
	},
	TRUNCATION_2PERCENT {
		@Override
		public Process getRepairProcess(IFeatureModel model, Oracle oracle) {
			int nrand = getPopulationSize(model);
			return new ProcessWatchmaker(nrand, 0, MAXORDER, STEADY_STEPS, -1, new FeatureModelEvaluator(oracle, model), new TruncationSelection(Math.max(0.02,  0.5 / (double)nrand + 0.01)) );
		}
	},
	TRUNCATION_5PERCENT {
		@Override
		public Process getRepairProcess(IFeatureModel model, Oracle oracle) {
			int nrand = getPopulationSize(model);
			return new ProcessWatchmaker(nrand, 0, MAXORDER, STEADY_STEPS, -1, new FeatureModelEvaluator(oracle, model), new TruncationSelection(0.05));
		}
	},
	TRUNCATION_20PERCENT {
		@Override
		public Process getRepairProcess(IFeatureModel model, Oracle oracle) {
			int nrand = getPopulationSize(model);
			return new ProcessWatchmaker(nrand, 0, MAXORDER, STEADY_STEPS, -1, new FeatureModelEvaluator(oracle, model), new TruncationSelection(0.2));
		}
	},
	TRUNCATION_10PERCENT {
		@Override
		public Process getRepairProcess(IFeatureModel model, Oracle oracle) {
			int nrand = getPopulationSize(model);
			return new ProcessWatchmaker(nrand, 0, MAXORDER, STEADY_STEPS, -1, new FeatureModelEvaluator(oracle, model), new TruncationSelection(0.1));
		}
	},
	TRUNCATION_ADQ_30PERCENT {
		@Override
		public Process getRepairProcess(IFeatureModel model, Oracle oracle) {
			int nrand = getPopulationSize(model);
			return new ProcessWatchmaker(nrand, 0, MAXORDER, -1, -1, new FeatureModelEvaluator(oracle, model), new TruncationSelection(0.3));
		}
	},
	TRUNCATION_ADQ_ED_5PERCENT {
		@Override
		public Process getRepairProcess(IFeatureModel model, Oracle oracle) {
			int nrand = getPopulationSize(model);
			return new ProcessWatchmaker(nrand, 0, MAXORDER, -1, -1, new FeatureModelEvaluator(oracle, model), new TruncationSelection(0.05));
		}
	},
	TRUNC_ED_C_5PERCENT_STRONG {
		@Override
		public Process getRepairProcess(IFeatureModel model, Oracle oracle) {
			int nrand = getPopulationSize(model);
			return new ProcessWatchmaker(nrand, 0, MAXORDER, -1, -1, new FeatureModelEvaluator(oracle, model), new TruncationSelection(0.05));
		}
	},
	TRUNC_ED_C_5PERCENT_LIGHT {
		@Override
		public Process getRepairProcess(IFeatureModel model, Oracle oracle) {
			int nrand = getPopulationSize(model);
			return new ProcessWatchmaker(nrand, 0, MAXORDER, -1, -1, new FeatureModelEvaluator(oracle, model), new TruncationSelection(0.05));
		}
	},
	;
	
	public static final int MULT = 5;
	public static final int MAXORDER = 25; // FIXME: to use in instances
	public static final int STEADY_STEPS = 15;
	
	public Process getRepairProcess(IFeatureModel model, Oracle oracle) {
		return new ProcessRandom(100, MAXORDER, STEADY_STEPS);
	}
	
	public static Processes[] getUsefulProcesses() {
		return new Processes[] {
			//TRUNCATE_ADQ__ED_ONLY_IF_TIED_5PERCENT,
//			TRUNCATION_1PERCENT,   // it doesn't work if the population is too small
			TRUNCATION_2PERCENT,
			TRUNCATION_5PERCENT,
			TRUNCATION_10PERCENT,
			ROULETTE_WHEEL_ADQ,
			RANK_ADQ,
			RANDOM,
			//TRUNCATION_20PERCENT,
			//TRUNC_ED_C_5PERCENT_LIGHT
		};
	}
	
	private static int getPopulationSize(IFeatureModel model) {
		return MULT * Util.getFeatureNames(model).size();
		//return 100;
		//return Math.min(100, Math.max(200, MULT * oracle.getFeatureNames().size()));
	}
}
