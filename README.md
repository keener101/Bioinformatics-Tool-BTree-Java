# Bioinformatics-Tool-BTree-Java

BACKGROUND:

This Bioinformatics BTree project was made in a 300-level Data Structures course. I alone coded this project.



OVERVIEW:

    This application seeks to efficiently handle a large data set of 
    DNA by using a BTree implementation to reduce the amount of disk 
    reads and writes we have to make. 


INCLUDED FILES:
    
    //JAVA FILES
    BTree.java
    BTreeNode.java
    BTreeUtil.java
    Cache.java
    GeneBankCreateBTree.java
    GeneBankSearch.java
    TreeObject.java

    //MISC FILES
    README
    
COMPILING AND RUNNING:

1) You will need the java developer kit JDK in order to compile and run this program
2) If you do not already have it you can download it from the oracle website:
     http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
	**then download the one corresponding to your operating system**
3) From the command console navigate to the folder containing these files
4) With the command console type the command: javac *.java
	--this will compile all files for you.
5) Now you can run one of the two main programs:
    
    * GeneBankCreateBTree - to create a new BTree 
    * GeneBankSearch - to search a given BTree for occurences of a DNA substring


// RUN PROGRAM - GeneBankCreateBTree

    FORMAT:
    $> java GeneBankCreateBTree <cache (0/1)> <degree> <gbk file> <seq length> [<cache size>] [<debug level>]

    PARAMS:
    <cache)>        - (0 / 1) without/with cache
    <degree>        - The degree of the BTree           
    <gbk file>      - The path to the gene bank file
    <seq length>    - Length of gene sequence from file
    
    // CONDITIONAL / OPTIONAL
    <cache size>    - (if cache == 1) specify the cache size
    <debug level>   - (0 / 1) *optional* - defaults to 0. 
    

// RUN PROGRAM- GeneBankSearch

    FORMAT:
   $> java GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> [<cache size>] [<debug level>]

    PARAMS:
    <cache)>        - (0 / 1) without/with cache         
    <btree file>    - The path to the btree file
    <query file>    - Length of gene sequence from file
    
    // CONDITIONAL / OPTIONAL
    <cache size>    - (if cache == 1) specify the cache size
    <debug level>   - (0 / 1) *optional* - defaults to 0. 


PROGRAM DESIGN AND IMPORTANT CONCEPTS:

    Obviously, designing an application as a group differs from how we
    would have designed it had we each written a separate program. We 
    were able to take advantage of splitting up the major sections, so the
    first thing we did was try and divide the efforts in the most equitable
    way we could based off of early estimations of how long certain parts of the
    project would take.  The section division we came up with looked something like this:

    *BTree Implementation
    *GeneBankSearch & GeneBankCreateBTree programs
        outputs
        debug
    *Misc -- Hooking everything up
        README
        testing
        utility classes
            Metadata Storage
            BtreeStorage as Binary file
    *Implementation Using Cache

    Before we started coding, we tried to gain a good understanding
    of the project requirements.  Reading through the project description
    multiple times, we could see that we'd benefit from abstracting some
    of the functionality into distinct classes to avoid over burdening the
    two main program classes. 

    We created utility classes so we could easier split up efforts, and to handl
    the methods we'd need for our main classes to interact. 
    Things like:
        * conversion of the DNA strings to Longs
        * reading in a GeneBank file
        * writing the dump text file
        * storing BTree binary data files

    
    //STORAGE

    Storing the Btree as a binary data file required a special layout of the 
    following format:

    The tree is stored as a zip archive, with a single entry for tree metadata, a single entry per directory for each
    node, and a set of directory entries for each node child. All objects are serialized using Java's built in
    serialization, allowing for any class structure modifications to still write correctly, without code changes.

    The completed archive has the following example structure:
    /       - root directory
    /tree   - tree metadata
    /n      - root node
    /0/     - 0th child directory
    /1/     - 1st child directory
    /2/     - 2nd child directory
    /0/n    - 0th child node
    /1/n    - 1st child node
    /2/n    - 2nd child node

    The recursive file search function is fast and memory efficient. Only the nodes that are part of the search path
    remain in memory during the search, and Java's ZipFile uses a RandomAccessFile for accessing entries directly.

TESTING:



DISCUSSION:
 
 
 
 
EXTRA CREDIT:
