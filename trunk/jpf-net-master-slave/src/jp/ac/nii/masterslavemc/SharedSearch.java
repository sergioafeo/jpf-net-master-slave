package jp.ac.nii.masterslavemc;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.search.DFSearch;

public abstract class SharedSearch extends DFSearch {

	public SharedSearch(Config config, JVM vm) {
		super(config, vm);
	}
}