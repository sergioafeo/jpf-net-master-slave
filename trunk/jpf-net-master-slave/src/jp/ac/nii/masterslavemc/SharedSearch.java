package jp.ac.nii.masterslavemc;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.search.Search;

public abstract class SharedSearch extends Search {

	public SharedSearch(Config config, JVM vm) {
		super(config, vm);
	}
}