import java.math.BigInteger;
import java.security.*;
import java.util.Random;
import javax.crypto.*;

public class DigitalSignature {
    final static String pHexString = "b59dd795 68817b4b 9f678982 2d22594f 376e6a9a bc024184 6de426e5 dd8f6edd"+
            "ef00b465 f38f509b 2b183510 64704fe7 5f012fa3 46c5e2c4 42d7c99e ac79b2bc"+
            "8a202c98 327b9681 6cb80426 98ed3734 643c4c05 164e739c b72fba24 f6156b6f"+
            "47a7300e f778c378 ea301e11 41a6b25d 48f19242 68c62ee8 dd313474 5cdf7323";

    final static String gHexString = "44ec9d52 c8f9189e 49cd7c70 253c2eb3 154dd4f0 8467a64a 0267c9de fe4119f2" +
            "e373388c fa350a4e 66e432d6 38ccdc58 eb703e31 d4c84e50 398f9f91 677e8864" +
            "1a2d2f61 57e2f4ec 538088dc f5940b05 3c622e53 bab0b4e8 4b1465f5 738f5496" +
            "64bd7430 961d3e5a 2e7bceb6 2418db74 7386a58f f267a993 9833beef b7a6fd68";

    //helper function to convert hexstrings copied from assignment description to BigInt
    //I've already manually removed newline characters.
    private static BigInteger hexStringToBigInt(String hexString){
        return new BigInteger(hexString.replaceAll("\\s+",""), 16);
    }

    //Used to calculate secret key x
    //TODO link this? https://stackoverflow.com/questions/3735664/randomizing-a-biginteger
    private static BigInteger bigIntInRange(BigInteger min, BigInteger max){
        Random rnd = new Random();

        BigInteger x = new BigInteger(max.bitLength(), rnd);
        int count = 0; //Count for debugging purposes

        //while x <= min || x >= max
        //So far while testing I've only seen a max of 4 iterations of the loop.
        //Generally it's either 0 or 1 iterations
        while (x.compareTo(min) <= 0 || x.compareTo(max) >= 0){
            x = new BigInteger(max.bitLength(), rnd);
            count++;
        }
        System.out.println("Count for big int in range: " + count);
        return x;
    }

    //Right to left variant of calculating modular exponentiation.
    //Taken from my submission in assignment 1. Renamed method from performRSA to modularExponentiation
    private static BigInteger modularExponentiation(BigInteger base, BigInteger exponent, BigInteger mod){
        BigInteger y = new BigInteger("1");
        for(int i = 0; i < exponent.bitLength(); i++){
            if (exponent.testBit(i)) {
                y = y.multiply(base);
                y = y.mod(mod);
            }
            base = base.multiply(base);
            base = base.mod(mod);
        }
        return y;
    }

    private static BigInteger gcd(BigInteger bigIntA, BigInteger bigIntB){
        // Use Euclidean Algorithm of:
        //      gcd(a,0) = a
        //      gcd(a,b) = gcd(b, a mod b)

        // Base case gcd(a,0) = a
        if(bigIntB.compareTo(BigInteger.ZERO) == 0){
            return bigIntA;
        }

        //Recursive case gcd(a,b) = gcd(b, a mod b)
        else{
            return gcd(bigIntB, bigIntA.mod(bigIntB));
        }
    }

    //From spec: random value k with 0 < k < p-1 and gcd(k,p-1) = 1
    private static BigInteger generateK(BigInteger pMinusOne){
        BigInteger k = bigIntInRange(BigInteger.ZERO, pMinusOne);

        int count = 0;
        //So far while testing I've only seen a max of 6 iterations of the loop.
        while(gcd(k,pMinusOne).compareTo(BigInteger.ONE) != 0)
        {
            k = bigIntInRange(BigInteger.ZERO, pMinusOne);
            count++;
        }

        System.out.println("Count for generate k: " + count);
        return k;
    }

    public static void main(String [] args){
        BigInteger primeModulusP = hexStringToBigInt(pHexString);
        BigInteger generatorG = hexStringToBigInt(gHexString);

        //Calculate secret key x
        BigInteger secretKeyX = bigIntInRange(BigInteger.ONE, primeModulusP.subtract(BigInteger.ONE));

        //Calculate public key y
        BigInteger publicKeyY = modularExponentiation(generatorG, secretKeyX,primeModulusP);

        //Generate k
        BigInteger randomK = generateK(primeModulusP.subtract(BigInteger.ONE));

        //Generate r
        BigInteger digitalSigntaureValueR = modularExponentiation(generatorG,randomK,primeModulusP);
    }


}
