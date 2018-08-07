javac *.java

cp ~/csclass/cs321/p4/queries/query* .
cp ~/csclass/cs321/p4/data/test* .

java GeneBankCreateBTree 1 4 test1.gbk 5 500 1
java GeneBankCreateBTree 1 3 test1.gbk 3 500 1

java GeneBankSearch 0 test1.gbk.btree.data.3.3 query3 100 1
