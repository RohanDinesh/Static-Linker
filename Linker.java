import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;

public class Linker{

	public static void main(String[] args) {
		File inputFile 	= new File(args[0]);

		try{
	        Scanner pass1	= new Scanner(inputFile);
	        Scanner pass2	= new Scanner(inputFile);

	        //************** FIRST PASS: SYMBOL TABLE & BASE ADDRESSES **************

  			System.out.printf("Symbol Table \n");
  			ArrayList<String> symbolTable 			    = new ArrayList<String>();
  			ArrayList<String> symbolTableAddresses 		= new ArrayList<String>();
  			ArrayList<Integer> modSizes				    = new ArrayList<Integer>();

  			int numDefsInMod = 0;
  			int baseAddress = 0;
  			int modSize = 0;
  			String symbol = null;
  			int symbolRelativeLocation = 0;
  			int symbolAddress = 0;
  			int numUses = 0;
  			int numModules = pass1.nextInt();

  			for (int i = 0; i < numModules; i++){
  				numDefsInMod = pass1.nextInt();
  				for (int j = 0; j < numDefsInMod; j++){
  					symbol = pass1.next();

  					// Error checking to see if variable is multiply defined
  					if (symbolTable.indexOf(symbol) != -1){
  						System.out.printf("^Error: This variable is multiply defined; first value used.\n");
  					}
  					else{
  						symbolRelativeLocation  = pass1.nextInt();
	  					symbolAddress           = baseAddress + symbolRelativeLocation;
	  					symbolTable.add(symbol);
	  					symbolTableAddresses.add(Integer.toString(symbolAddress));
	  					System.out.printf("%s = %d \n", symbol, symbolAddress);
  					}
  				}
  				//skip the use line
  				numUses = pass1.nextInt();
  				for (int j = 0; j < numUses; j++){
  					pass1.next();
  				}

  				//get the size of the module to get a new baseAddress
  				modSize = pass1.nextInt();
  				modSizes.add(modSize);

  				//now skip the rest of the text line
  				baseAddress += modSize;
				for (int j = 0; j < modSize; j++){
  					pass1.nextInt();
  				}
  			}
  			pass1.close();

  			//************** SECOND PASS: MEMORY MAP **************

  			System.out.printf("\nMemory Map \n");
  			pass2.nextInt(); // Skipping the number of modules because we alreay know it

  			baseAddress = 0;
  			modSize = 0;
  			symbolAddress = 0;
  			int numToDecode = 0;
  			int lastDigit = 0;
  			int addressToAdjust = 0;
  			int adjustedAddress = 0;
  			String symbolUsed = "";
  			int symbolUsedIndex = 0;
  			ArrayList<String> allUsedSymbols = new ArrayList<String>();
  			ArrayList<String> usedFromUseList = new ArrayList<String>();

  			//Once again looping through each module
  			for (int i = 0; i < numModules; i++){
  				// Skip the def line
	  			numDefsInMod = pass2.nextInt();
  				for (int j = 0; j < numDefsInMod; j++){
  					pass2.next();
  					pass2.next();
  				}
	  			// we need to store what's in the uses line!
  				numUses = pass2.nextInt();
  				String useList[] = new String[numUses];
  				for (int j = 0; j < numUses; j++){
  					useList[j] = pass2.next();
  					allUsedSymbols.add(useList[j]);
  				}

	  			// now we're at the text line?
	  			modSize = pass2.nextInt();
	  			
	  			// loop through the text line woo
  				for (int j = 0; j < modSize; j++){ // for each of the numbers
  					symbolAddress = -1;
  					numToDecode = pass2.nextInt();
  					lastDigit = isolateDigit(numToDecode, 1);
  					addressToAdjust = (numToDecode - lastDigit)/10;
  					boolean tooBig = false;
  					boolean exceedsUseList = false;
  					boolean relativeExceedsModule = false;
  					boolean symbolLargerThanMod = false;
  					switch (lastDigit){
  						case 1: // Immediate, unchanged
  							adjustedAddress = addressToAdjust;
  							break;
	  					case 2: // Absolute, unchanged
	  						adjustedAddress = addressToAdjust;
	  						if (adjustedAddress - (int)(Math.floor(adjustedAddress/1000)*1000) > 500){
	  							tooBig = true;
	  							adjustedAddress = (int) Math.floor(adjustedAddress/1000)*1000;
	  						}
	  						break;
	  					case 3: // Relative, relocated
	  						if (addressToAdjust - (int)(Math.floor(addressToAdjust/1000)*1000) > modSize){
	  							relativeExceedsModule = true;
	  							// Use zero, absolute
	  							adjustedAddress = (int)(Math.floor(addressToAdjust/1000)*1000);
	  						}
	  						else{
	  							adjustedAddress = addressToAdjust + baseAddress;
	  						}
	  						break;
	  					case 4:	// External, resolved
	  						symbolUsedIndex = addressToAdjust - (int)(addressToAdjust/1000) * 1000;
	  						if (symbolUsedIndex > useList.length-1){
	  							// Then treat as immediate
	  							exceedsUseList = true;
	  							adjustedAddress = addressToAdjust;
	  							break;
	  						}
	  						symbolUsed = useList[symbolUsedIndex];
	  						usedFromUseList.add(symbolUsed);
	  						if (symbolTable.indexOf(symbolUsed) == -1){
  								symbolAddress = 0;
  							}
  							else{
  								symbolAddress = Integer.parseInt(symbolTableAddresses.get(symbolTable.indexOf(symbolUsed)));
  							}
	  						adjustedAddress = addressToAdjust + symbolAddress - symbolUsedIndex;
  					}
  					System.out.printf("%2d: %d ", (j + baseAddress), adjustedAddress);
  					// Error checking to see if used, but not defined
  					if (symbolAddress == 0){
  						System.out.printf("Error: %s is not defined; zero used.", symbolUsed);
  					}
  					if (tooBig){
  						System.out.printf("Error: Absolute address exceeds machine size; zero used.");
  					}
  					if (exceedsUseList){
  						System.out.printf("Error: External address exceeds length of use list; treated as immediate.");
  					}
  					if (relativeExceedsModule){
  						System.out.printf("Error: Relative address exceeds module size; zero used.");
  					}
  					if (symbolLargerThanMod){
  						System.out.printf("Error: Address in definition exceeds module size; zero used.");
  					}
  					System.out.printf("\n");
  				}
  				baseAddress += modSize;
  			}

  			// Error checking to see if symbol was defined but not used
  			for (int i = 0; i < symbolTable.size(); i++){
  				if (allUsedSymbols.indexOf(symbolTable.get(i)) == -1){
					System.out.printf("Warning: %s was defined but never used. \n", symbolTable.get(i));
				}
  			}
  			// Error checking for: appears in useList but not actually used in the module
  			for (int i = 0; i < allUsedSymbols.size(); i++ ){
  				if (usedFromUseList.indexOf(allUsedSymbols.get(i)) == -1){
  					System.out.printf("Warning: %s appears in use list but is never used in module. \n", allUsedSymbols.get(i));
  				}
  			}

        	pass2.close();
    	} 
    	catch (FileNotFoundException e){
    	    System.out.printf("Error: File not found. \n");
    	}
	}

	public static int isolateDigit(int number, int digitPosition){
		return (number / (int) Math.pow(10, digitPosition - 1)) % 10;
	}

}
