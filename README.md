# SHA2

This program runs a multithreaded java program that randomly generates byte arrays and hashes them with SHA256. It then checks the SHA against an AVL tree for collisions starting with the last byte in the hash. It then outputs these files to the users desktop as X.bin and Y.bin and each time a greater collision value is observed it overwrites these values. The defaults of the program limit the size of the AVL tree to around 9gb typically, however this can be changed inside the program itself. 
