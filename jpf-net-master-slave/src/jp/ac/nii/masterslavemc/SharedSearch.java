package jp.ac.nii.masterslavemc;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.State;
import gov.nasa.jpf.search.DFSearch;
import gov.nasa.jpf.vm.VM;

public abstract class SharedSearch extends DFSearch {

	@Override
	protected void restoreState(State state) {
		vm.restoreState(state.getVMState());
	}

	@Override
	public boolean supportsRestoreState() {
		return true;
	}

	public SharedSearch(Config config, VM vm) {
		super(config, vm);
	}
}