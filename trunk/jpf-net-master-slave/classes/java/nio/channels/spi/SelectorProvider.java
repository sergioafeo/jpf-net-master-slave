package java.nio.channels.spi;


public class SelectorProvider {

	private static SelectorProvider defaultProvider;
	
	protected SelectorProvider() {
		
	}
	
	public static SelectorProvider provider(){
		if (defaultProvider == null) {
			defaultProvider = new SelectorProvider();
		}
		return defaultProvider;
	}

	public AbstractSelector openSelector() {
		// TODO Auto-generated method stub
		return new AbstractSelector();
	}

	
//	public AbstractSelector openSelector(){
//		System.out.println("mugu");
//		return (AbstractSelector) new Selector();
//	}
//	
//	
	
}
