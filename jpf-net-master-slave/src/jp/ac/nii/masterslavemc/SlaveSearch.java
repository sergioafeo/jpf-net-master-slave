package jp.ac.nii.masterslavemc;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.State;
import gov.nasa.jpf.jvm.JVM;

public class SlaveSearch extends SharedSearch {
	public enum SearchCommand  {
		EMPTY,
		SEARCH,
		FINISH
	}
	
	public SlaveSearch(Config config, JVM vm) {
		super(config, vm);
	}

	@Override
	public void search() {
		SearchParamBundle params;
		boolean done = false;
		comm.notifyReadyToSearch();
		notifySearchStarted();
		while (!done)
			try {
				params = comm.getSearchParams();
				switch (params.getCommand()) {
				case SEARCH:
					System.out.println("I was told to search!!");
					comm.notifySearchFinished(null);
					break;
				case FINISH:
					done = true;
					System.out.println("Slave search terminated!!");
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		notifySearchFinished();
	}
}
