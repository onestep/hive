package hive.game;

public class UniversalHash {

    private static int a;
    private static int b;
    private static int c;

    public static synchronized int hashByteArray(byte[] paramArrayOfByte, int paramInt) {
	int i = paramArrayOfByte.length;
	int j = i;
	int k = 0;

	a = UniversalHash.b = -1640531527;
	c = paramInt;

	for (; j >= 12;
		j -= 12) {
	    a += paramArrayOfByte[(0 + k)] + (paramArrayOfByte[(1 + k)] << 8) + (paramArrayOfByte[(2 + k)] << 16) + (paramArrayOfByte[(3 + k)] << 24);
	    b += paramArrayOfByte[(4 + k)] + (paramArrayOfByte[(5 + k)] << 8) + (paramArrayOfByte[(6 + k)] << 16) + (paramArrayOfByte[(7 + k)] << 24);
	    c += paramArrayOfByte[(8 + k)] + (paramArrayOfByte[(9 + k)] << 8) + (paramArrayOfByte[(10 + k)] << 16) + (paramArrayOfByte[(11 + k)] << 24);
	    mix();
	    k += 12;
	}

	c += i;
	switch (j) {
	    case 11:
		c += (paramArrayOfByte[(10 + k)] << 24);
	    case 10:
		c += (paramArrayOfByte[(9 + k)] << 16);
	    case 9:
		c += (paramArrayOfByte[(8 + k)] << 8);
	    case 8:
		b += (paramArrayOfByte[(7 + k)] << 24);
	    case 7:
		b += (paramArrayOfByte[(6 + k)] << 16);
	    case 6:
		b += (paramArrayOfByte[(5 + k)] << 8);
	    case 5:
		b += paramArrayOfByte[(4 + k)];
	    case 4:
		a += (paramArrayOfByte[(3 + k)] << 24);
	    case 3:
		a += (paramArrayOfByte[(2 + k)] << 16);
	    case 2:
		a += (paramArrayOfByte[(1 + k)] << 8);
	    case 1:
		a += paramArrayOfByte[(0 + k)];
	}

	mix();

	return c;
    }

    private static void mix() {
	a -= b;
	a -= c;
	a ^= c >> 13;
	b -= c;
	b -= a;
	b ^= a << 8;
	c -= a;
	c -= b;
	c ^= b >> 13;
	a -= b;
	a -= c;
	a ^= c >> 12;
	b -= c;
	b -= a;
	b ^= a << 16;
	c -= a;
	c -= b;
	c ^= b >> 5;
	a -= b;
	a -= c;
	a ^= c >> 3;
	b -= c;
	b -= a;
	b ^= a << 10;
	c -= a;
	c -= b;
	c ^= b >> 15;
    }

    private static int hashsize(int paramInt) {
	return 1 << paramInt;
    }

    private static int hashmask(int paramInt) {
	return hashsize(paramInt) - 1;
    }
}