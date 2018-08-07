import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Implements a one or two-level cache on any specified object using LinkedList.
 * Constructors allow specification of the size of each cache level
 * 
 * @author Keener
 *
 * @param <T> the object type for the cache to hold
 */
public class Cache<T> {
	private boolean isTwoLevel;			//whether the cache is a two level cac

	private LinkedList<T> level1;		//level one cache 
	private LinkedList<T> level2;		//level two cache (is null when not used)
	private int size1;					//size of level one cache
	private int size2;					//size of level two cache (when used)


	private int hit1;					//how many times a searched object was found in level 1			
	private int hit2;					//how many times a searched object was found in level 2 (but not in level 1)
	private int globalRef;				//total number of times an object has been requested from the cache
	private int ref1;					//number of times level 1 cache was searched (should be same as globalRef)
	private int ref2;					//number of times level 2 cache was searched

	
	/**
	 * Constructor for level one cache
	 * 
	 * @param size1 - desired storage size for level one 
	 */
	public Cache(int size1){
		isTwoLevel = false; 
		level1 = new LinkedList<T>();
		this.size1 = size1;

		hit1 = 0;
		globalRef = ref1 = 0;

	}
	
	/**
	 * Overloaded constructor for level 2 cache
	 * 
	 * @param size1 - desired storage size for level one
	 * @param size2 - desired storage size for level two
	 */
	
	public Cache(int size1, int size2){
		isTwoLevel = true; 
		level1 = new LinkedList<T>();
		level2 = new LinkedList<T>();
		this.size1 = size1;
		this.size2 = size2;

		hit1 = hit2 =0;
		globalRef = ref1 = ref2 = 0;

	}
	
	/**
	 * Searches the cache for a given object. 
	 * Searches level one first. If found, moves object to front, and moves it to front on 2nd level if it exists
	 * If not found in level 1, searches level 2 if possible. If found in level two, moves it to the front on both levels
	 * If not found on any level, it is added to the front on all levels.
	 * 
	 * @param target - object to be searched for
	 * @return - the object that was found, or the object that was added (if not found)
	 */

	public T getObject(T target){
		globalRef++;								//increase number of references
		ref1++;

		T retVal = null;							//set retVal to null to begin

			
			if (level1.contains(target)){			//if level one has the target
				hit1++;								//increase hits for level 1
				retVal = level1.remove(level1.indexOf(target));	//remove the target, and store it
				
				
				if(isTwoLevel){											//if also two level
					retVal = level2.remove(level2.indexOf(target));		//remove the target, and store it
				}
				
				addObject(target);				//call addObject for this target (will add to front of both levels)
			
			} else if(isTwoLevel){	//if target is not in first level AND a second level exists
				ref2++;				//increase refs to level 2
				
				
				if(level2.contains(target)){		//if the target is in the second level 
					hit2++;							//increase hits to level 2
					retVal = level2.remove(level2.indexOf(target));		//remove target and store it	
					addObject(target);				//call addObject for this target (will add to front of both levels)
				
			}
			
		}
			
			if(retVal == null){		//if all levels have been searched target has not been found
				addObject(target);	//call addObject for this target (will add to front of both levels)
			
				retVal = level1.getFirst();	//store the target now that it is added
			}
			
			return retVal;

	}
	
	/**
	 * Adds a given object to the front of a cache on all available levels.
	 * @param element - the object to be added
	 */
	
	
	public void addObject(T element){
		level1.addFirst(element);		//add the element to the front of the first level
		
		if (level1.size() > size1){
			level1.removeLast();		//if first level is full, remove the last element
		}
			
		if(isTwoLevel){					//if two level	
			level2.addFirst(element);	//add the element to front of second level
			
			if(level2.size() > size2){	//if second level is full, remove last element
				level2.removeLast();
			}
		}
	}
	
	/**
	 * Removes the given object from the cache
	 * 
	 * @param target - the object to be removed
	 * @return the removed argument
	 */

	public T removeObject(T target){
		T retVal = null;
		
		if(!level1.contains(target)){		
			if(!isTwoLevel){					//if element is not in any level, throw exception
				throw new NoSuchElementException();
			}
		} else {
			retVal = level1.get(level1.indexOf(target));	//if level one contains the target, remove it and store
			level1.remove(target);
		}
		
		
		if(isTwoLevel){									// if is two level
			if(!level2.contains(target)){				// if does not contain, throw exception
				throw new NoSuchElementException();
			} else {
				retVal = level1.get(level1.indexOf(target));	//if it does exist, remove and store
			}
			level2.remove(target);
		}
		
		return retVal;
	}
	
	/**
	 * Clears the cache (will clear multiple levels if they exist)
	 */
	
	public void clearCache(){
		level1.clear();				//clears level one
		
		if(isTwoLevel){				// if two level cache, clears second level
			level2.clear();
		}
	}
	
	/**
	 * Getter for isTwoLevel
	 * @return - boolean true if cache is two level, false if one level
	 */
	
	public boolean isTwoLevel() {
		boolean retVal = isTwoLevel;
		
		return retVal;
	}

	/**
	 * Getter for level1 cache
	 * @return LinkedList for level1
	 */
	
	public LinkedList<T> getLevel1() {
		LinkedList<T> retVal = level1;
		
		return retVal;
	}

	/**
	 * Getter for level2 cache
	 * @return LinkedList for level2
	 */
	
	public LinkedList<T> getLevel2() {
		LinkedList<T> retVal = level2;
		
		return retVal;
	}

	/**
	 * Getter for storage size of level 1 cache
	 * @return int of storage size
	 */
	
	public int getSize1() {
		
		int retVal = size1;
		return retVal;
	}
	
	/**
	 * Getter for storage size of level 2 cache
	 * @return int of storage size
	 */

	public int getSize2() {
		int retVal = size2;
		
		return retVal;
	}
	
	/**
	 * Getter for total global hits
	 * @return int of global hits
	 */

	public int getGlobalHit() {
		int retVal = hit1 + hit2;
		return retVal;
	}
	
	/**
	 * Getter for hits for level 1
	 * @return int of level 1 hits
	 */

	public int getHit1() {
		int retVal = hit1;
		return retVal;
	}

	
	/**
	 * Getter for hits for level 2
	 * @return int of level 2 hits
	 */
	
	public int getHit2() {
		int retVal = hit2;
		return retVal;
	}

	/**
	 * Getter for global hit ratio
	 * @return double of global hit ratio
	 */
	
	public double getGlobalHitRatio() {
		double hits = (double) getGlobalHit();
		double ref = (double) globalRef;
		
		double retVal = hits/ref;
		return retVal;
	}
	
	/**
	 * Getter for level 1 hit ratio
	 * 
	 * @return double of level 1 hit ratio
	 */

	public double getHitRatio1() {
		double hits = (double) getHit1();
		double ref = (double) getRef1();
		
		double retVal = hits/ref;
		return retVal;
	}
	
	/**
	 * Getter for level 2 hit ratio
	 * 
	 * @return double of level 2 hit ratio
	 */

	public double getHitRatio2() {
		double hits = (double) getHit2();
		double ref = (double) getRef2();
		
		double retVal = hits/ref;
		return retVal;     
	}
	
	/**
	 * Getter for number of global references
	 * @return int of global references
	 */

	public int getGlobalRef() {
		int retVal = globalRef;
		return retVal;
	}
	
	/**
	 * Getter for level 1 references
	 * 
	 * @return int of level 1 references
	 */

	public int getRef1() {
		int retVal = ref1;
		return retVal;
	}
	
	/**
	 * Getter for level 2 references
	 * 
	 * @return int of level 2 references
	 */

	public int getRef2() {
		int retVal = ref2;
		return retVal;
	}
}
