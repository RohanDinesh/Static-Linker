
This project aims at simulating a static linker. </br>
It simulates a two-pass linker based on file input with definitions, uses, and memory addresses (relative, absolute, immediate, external) -- with error detection.
We have written this linker using Java. The linker takes in an input file consisting of various sections of a program, reads in all symbols, and creates a complete symbol table in memory in the first pass. In the second pass, it reads in section and relocation information, updates addresses, and writes out a new file.
This new file will consist of the actual memory locations that are to be loaded by the loader.

A linker is a computer program that takes one or more object files generated by a compiler and combines them into one, executable program. 
There are 2 types of linkers:
A static linker places all the library routines into an executable whereas a dynamic linker places the name of the shared library in the executable. </br></br>
![image](https://user-images.githubusercontent.com/66969681/127359389-fcaee882-7f23-43ce-934a-bd7176d0cd0b.png)

Our project aims at implementing a static linker. 
Static linking is the result of the linker copying all library routines used in the program into the executable image. This may require more disk space and memory than dynamic linking, but is both faster and more portable, since it does not require the presence of the library on the system where it is run.

Design-
1. Read in the input file: </br>
The contents of the input file simulates the actual input to the linker. The first line of the input file has: the number of sections in the program.
Each section has the following three lines:</br>
     -‣ The number of symbols in that section along with the symbols and their relative addresses.</br>
     -‣ The number of symbols used in that section along with the symbols.</br>
     -‣ The memory addresses in that section have to be adjusted before loading it into the memory.</br>

2. First pass: </br>
We look at all the symbols defined in each section and add it to the  symbol table (here, an ArrayList simulates the symbol table). We have another ArrayList that stores the relative addresses corresponding to the symbols in the first one. Thus identification of various symbols and creation of the symbol table is done in the first pass.

3. Second pass: </br>
In the second pass, we store all the symbols used in a particular section in a list along with their addresses. Then, we parse all the memory locations of that particular section. Depending on whether the location is immediate, absolute, relative or external, we modify the addresses and resolve the symbols wherever needed.

4. Output: </br>
The output files consists of the adjusted memory locations and can be used as an input to the loader.


To run this program:
```
> javac Linker.java
> java Linker input_set_[1-8].txt
```

