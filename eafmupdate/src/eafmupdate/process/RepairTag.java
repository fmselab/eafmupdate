package eafmupdate.process;

/**
 * a tag for a repair set of mutations.
 * A tag can be a characteristic in common, such as the same model or the same iteration
 * Used for Experiment1 and in RepairStats
 * @author marcoradavelli
 *
 */
public class RepairTag {

	private String tag;
	
	private ModelsPair model;
	private int iteration;
	
	public RepairTag(ModelsPair model, int iteration) {
		this.model=model;
		this.iteration=iteration;
	}
	
	RepairTag(String tag) {
		this.tag=tag;
	}
	
	@Override
	public String toString() {
		return tag!=null ? tag : (model.name()+","+iteration);
	}
}
