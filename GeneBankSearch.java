/**********************************
* CS321                           *
* BTree P4                        *
* GeneBankSearch takes in a BTree *
* file and a sequence to search   *
* for within the BTree            *
***********************************/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.zip.ZipFile;

public class GeneBankSearch{
	
	//private variables
	private static byte isCache;
	private static int cacheSize;
	private static byte debugLvl;
//	private static Cache cache;
	
	public static void main(String[] args){
		if (args.length < 3){		//check for too many or not enough arguments
			System.out.println("ERROR: not enough arguments.");
			System.out.println("Usage: GeneBankSeach <0/1(no/with Cache)> <btree file> <query file> [<cache size>] [<debug level>");
		} else if (args.length > 5){
			System.out.println("ERROR: too many arguments.");
			System.out.println("Usage: GeneBankSeach <0/1(no/with Cache)> <btree file> <query file> [<cache size>] [<debug level>");
		}
		else{ //try to parse information passed in
			try{
				search(args);
			}
			catch (NumberFormatException e){
				System.out.println("Error: 1 or more args is not a number: " + e.toString());
			} catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
	}
	
	

	public static void search(String[] args) throws NumberFormatException, ClassNotFoundException, IOException {
		
		//"Usage: GeneBankSeach <0/1(no/with Cache)> <btree file> <query file> [<cache size>] [<debug level>"
		
		isCache =  Byte.parseByte(args[0]);
		
		if (args.length > 3){
			cacheSize = Integer.parseInt(args[3]);
		}
		if (args.length > 4){
			debugLvl = Byte.parseByte(args[4]);
		}

//		cache = new Cache<Sequence>(cacheSize);
		
		//get Tree from binary file  ??how to do that?
		//The B-Tree should be stored as a binary data file on the disk (and not as a text file). If
		//the name of the GeneBank file is xyz.gbk, the sequence length is k, the BTree degree
		//is t, then the name of the btree file should be xyz.gbk.btree.data.k.t.

		int sequenceLength = 0;
        ZipFile treeFile = new ZipFile(args[1]);

        ObjectInputStream rootInputStream = new ObjectInputStream(treeFile.getInputStream(treeFile.getEntry("n")));
        //noinspection unchecked
        ArrayList<BTree.Key<Sequence>> rootKeys = ((BTree.Node)rootInputStream.readObject()).keys;
        if(rootKeys.size() <= 0) {
            throw new NoSuchElementException("Tree contains no keys");
        }

        // Read the sequence length from the first key in the root node
        sequenceLength = rootKeys.get(0).key.length;

		File queryFile = new File(args[2]);
		Scanner queryScanner = null;
        queryScanner = new Scanner(queryFile);

        System.out.println("Searching tree");
		while(queryScanner.hasNextLine()) {
			Sequence query = new Sequence(BTreeUtil.convertStringToLong(queryScanner.nextLine()), sequenceLength);
			try {
				BTree.Key<Sequence> result = BTree.search(treeFile, query);
				if(result.count > 0)
				System.out.println(result);
			} catch (IOException e) {
				// This happens if the zip file is closed or otherwise can't be read
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// This happens if there was an error deserializing the tree
				e.printStackTrace();
			}
		}
}
	
}