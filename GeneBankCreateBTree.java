/**
 * ***********************************
 * CS321 BTree project                *
 * driver class for creating a BTree  *
 * creates a BTree from a subsequence *
 * of DNA given by the input file     *
 * these sequences are coded into a   *
 * binary representation and combined *
 * into a 64-bit long                 *
 *                                    *
 * DNA-binary coding:                 *
 *    Adenine  00                     *
 *    Thymine 11                      *
 *    Cytosine 01                     *
 *    Guanine  10                     *
 *************************************
 */

//java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.NumberFormatException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipFile;

public class GeneBankCreateBTree {

    //private variables
    private static byte isCache;
    private static int degree;
    private static String gbkFile;
    private static int seqLen;
    private static int cacheSize;
    private static byte debugLvl;
    private static BTree Tree;
    private static BTreeUtil util;
    private static Cache<Sequence> cache;

    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("ERROR: not enough arguments.");
            System.out.println("Usage: GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>");
        } 
        else { //try to parse information passed in
            try {
                isCache = Byte.parseByte(args[0]);
                if (Integer.parseInt(args[1]) == 0) {
                    degree = 103;  //4 4x + 4(x+) = 4096, x = 205, for 2t-1 = 205, t = 103			
                } else {
                    degree = Integer.parseInt(args[1]);
                }

                gbkFile = args[2];
                seqLen = Integer.parseInt(args[3]);

                if (args.length == 5) {
                	if(isCache == 1){
                        cacheSize = Integer.parseInt(args[4]);
                	} else {
                		debugLvl = Byte.parseByte(args[5]);
                	}
                }
                if (args.length == 6) {
                    cacheSize = Integer.parseInt(args[4]);
                    debugLvl = Byte.parseByte(args[5]);  
                }
                

                //if made it this far - create tree and start passing in keys with BTreeUtil
                Tree = new BTree(degree);
                
                cache = new Cache(cacheSize);
                String str = BTreeUtil.buildGeneBankString(gbkFile);
                List<String> strArray = BTreeUtil.getSubsequences(seqLen, str);

                //convert strings into keys and cache 
                for (int i = 0; i < strArray.size(); i++) {
                    Long tempBase = BTreeUtil.convertStringToLong(strArray.get(i));
                    Sequence tempSeq = new Sequence(tempBase);
                    System.out.println(tempBase);
                    Tree.insert(tempSeq);
                    
                    if(isCache == 1){
                    	cache.addObject(tempSeq);
                    }
                }



                String BTreeFileName = BTreeUtil.createBTreeFileName(gbkFile, seqLen, degree);
               
                if(debugLvl == 1){
                    //TODO: Print in-order traversal of tree
                    Tree.dumpTree(Tree.root, seqLen);
                }
          
                try {
                    Tree.write(new FileOutputStream(BTreeFileName));             
                } 
                catch (IOException e) {
                    e.printStackTrace();
                } 


            } 
            catch (NumberFormatException e) {
                System.out.println("Error: 1 or more args is not a number: " + e.toString());
            }
        }   
    }

}
