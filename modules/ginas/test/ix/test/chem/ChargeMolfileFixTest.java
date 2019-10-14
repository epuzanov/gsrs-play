package ix.test.chem;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ix.core.chem.ChemCleaner;

public class ChargeMolfileFixTest {
    @Test
    public void testAssignChiralFlagWhenMissingAndAbsolute() throws Exception {
        String molfileWithTooManyChargesOnOneLine="\n" +
                "   JSDraw212191818162D\n" +
                "\n" +
                " 10 11  0  0  0  0            999 V2000\n" +
                "   16.7960   -5.3560    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   15.4450   -4.5760    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   15.4450   -3.0160    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   16.7960   -2.2360    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   18.1470   -3.0160    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   18.1470   -4.5760    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   19.4980   -5.3560    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   20.8490   -4.5760    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   20.8490   -3.0160    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   19.4980   -2.2360    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "  1  2  1  0  0  0  0\n" +
                "  2  3  1  0  0  0  0\n" +
                "  3  4  1  0  0  0  0\n" +
                "  4  5  1  0  0  0  0\n" +
                "  5  6  1  0  0  0  0\n" +
                "  6  1  1  0  0  0  0\n" +
                "  6  7  1  0  0  0  0\n" +
                "  7  8  1  0  0  0  0\n" +
                "  8  9  1  0  0  0  0\n" +
                "  9 10  1  0  0  0  0\n" +
                " 10  5  1  0  0  0  0\n" +
                "M  CHG 10   1   1   2   1   3   1   4   1   5   1   6   1   7   1   8   1   9   1  10   1\n" +
                "M  END";
        String expected="\n" +
                "   JSDraw212191818162D\n" +
                "\n" +
                " 10 11  0  0  0  0            999 V2000\n" +
                "   16.7960   -5.3560    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   15.4450   -4.5760    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   15.4450   -3.0160    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   16.7960   -2.2360    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   18.1470   -3.0160    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   18.1470   -4.5760    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   19.4980   -5.3560    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   20.8490   -4.5760    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   20.8490   -3.0160    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   19.4980   -2.2360    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "  1  2  1  0  0  0  0\n" +
                "  2  3  1  0  0  0  0\n" +
                "  3  4  1  0  0  0  0\n" +
                "  4  5  1  0  0  0  0\n" +
                "  5  6  1  0  0  0  0\n" +
                "  6  1  1  0  0  0  0\n" +
                "  6  7  1  0  0  0  0\n" +
                "  7  8  1  0  0  0  0\n" +
                "  8  9  1  0  0  0  0\n" +
                "  9 10  1  0  0  0  0\n" +
                " 10  5  1  0  0  0  0\n" +
                "M  CHG  8   1   1   2   1   3   1   4   1   5   1   6   1   7   1   8   1\n" +
                "M  CHG  2   9   1  10   1\n" +
                "M  END";
        String mod = ChemCleaner.getCleanMolfile(molfileWithTooManyChargesOnOneLine);
        assertEquals(expected,mod);
    }
    @Test
    public void testTooManyMolfileEndsGetCleanedToOne() throws Exception {
        String molfileWithTooManyChargesOnOneLineAndAnExtraEnd="\n" +
                "   JSDraw212191818162D\n" +
                "\n" +
                " 10 11  0  0  0  0            999 V2000\n" +
                "   16.7960   -5.3560    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   15.4450   -4.5760    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   15.4450   -3.0160    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   16.7960   -2.2360    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   18.1470   -3.0160    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   18.1470   -4.5760    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   19.4980   -5.3560    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   20.8490   -4.5760    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   20.8490   -3.0160    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   19.4980   -2.2360    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "  1  2  1  0  0  0  0\n" +
                "  2  3  1  0  0  0  0\n" +
                "  3  4  1  0  0  0  0\n" +
                "  4  5  1  0  0  0  0\n" +
                "  5  6  1  0  0  0  0\n" +
                "  6  1  1  0  0  0  0\n" +
                "  6  7  1  0  0  0  0\n" +
                "  7  8  1  0  0  0  0\n" +
                "  8  9  1  0  0  0  0\n" +
                "  9 10  1  0  0  0  0\n" +
                " 10  5  1  0  0  0  0\n" +
                "M  ENDM  END";
        String expected="\n" +
                "   JSDraw212191818162D\n" +
                "\n" +
                " 10 11  0  0  0  0            999 V2000\n" +
                "   16.7960   -5.3560    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   15.4450   -4.5760    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   15.4450   -3.0160    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   16.7960   -2.2360    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   18.1470   -3.0160    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   18.1470   -4.5760    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   19.4980   -5.3560    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   20.8490   -4.5760    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   20.8490   -3.0160    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   19.4980   -2.2360    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "  1  2  1  0  0  0  0\n" +
                "  2  3  1  0  0  0  0\n" +
                "  3  4  1  0  0  0  0\n" +
                "  4  5  1  0  0  0  0\n" +
                "  5  6  1  0  0  0  0\n" +
                "  6  1  1  0  0  0  0\n" +
                "  6  7  1  0  0  0  0\n" +
                "  7  8  1  0  0  0  0\n" +
                "  8  9  1  0  0  0  0\n" +
                "  9 10  1  0  0  0  0\n" +
                " 10  5  1  0  0  0  0\n" +
                "M  END";
        String mod = ChemCleaner.getCleanMolfile(molfileWithTooManyChargesOnOneLineAndAnExtraEnd);
        assertEquals(expected,mod);
    }

    @Test
    public void testMoreThan31ChargesStillGetsCleaned(){
        String moreThan31Charges="\n" +
                "   JSDraw212191818462D\n" +
                "\n" +
                " 36 35  0  0  0  0            999 V2000\n" +
                "    4.4200   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "    5.7710   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "    7.1220   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "    8.4730   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "    9.8240   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   11.1750   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   12.5260   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   13.8770   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   15.2280   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   16.5790   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   17.9300   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   19.2810   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   20.6320   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   21.9830   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   23.3340   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   24.6850   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   26.0360   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   27.3870   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   28.7380   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   30.0890   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   31.4400   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   32.7910   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   34.1420   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   35.4930   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   36.8440   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   38.1950   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   39.5460   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   40.8970   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   42.2480   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   43.5990   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   44.9500   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   46.3010   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   47.6520   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   49.0030   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   50.3540   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   51.7050   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "  1  2  1  0  0  0  0\n" +
                "  2  3  1  0  0  0  0\n" +
                "  3  4  1  0  0  0  0\n" +
                "  4  5  1  0  0  0  0\n" +
                "  5  6  1  0  0  0  0\n" +
                "  6  7  1  0  0  0  0\n" +
                "  7  8  1  0  0  0  0\n" +
                "  8  9  1  0  0  0  0\n" +
                "  9 10  1  0  0  0  0\n" +
                " 10 11  1  0  0  0  0\n" +
                " 11 12  1  0  0  0  0\n" +
                " 12 13  1  0  0  0  0\n" +
                " 13 14  1  0  0  0  0\n" +
                " 14 15  1  0  0  0  0\n" +
                " 15 16  1  0  0  0  0\n" +
                " 16 17  1  0  0  0  0\n" +
                " 17 18  1  0  0  0  0\n" +
                " 18 19  1  0  0  0  0\n" +
                " 19 20  1  0  0  0  0\n" +
                " 20 21  1  0  0  0  0\n" +
                " 21 22  1  0  0  0  0\n" +
                " 22 23  1  0  0  0  0\n" +
                " 23 24  1  0  0  0  0\n" +
                " 24 25  1  0  0  0  0\n" +
                " 25 26  1  0  0  0  0\n" +
                " 26 27  1  0  0  0  0\n" +
                " 27 28  1  0  0  0  0\n" +
                " 28 29  1  0  0  0  0\n" +
                " 29 30  1  0  0  0  0\n" +
                " 30 31  1  0  0  0  0\n" +
                " 31 32  1  0  0  0  0\n" +
                " 32 33  1  0  0  0  0\n" +
                " 33 34  1  0  0  0  0\n" +
                " 34 35  1  0  0  0  0\n" +
                " 35 36  1  0  0  0  0\n" +
                "M  CHG 36   1   1   2   1   3   1   4   1   5   1   6   1   7   1   8   1   9   1  10   1  11   1  12   1  13   1  14   1  15   1  16   1  17   1  18   1  19   1  20   1  21   1  22   1  23   1  24   1  25   1  26   1  27   1  28   1  29   1  30   1  31   1  32   1  33   1  34   1  35   1  36   1\n" +
                "M  END";

        String expected="\n" +
                "   JSDraw212191818462D\n" +
                "\n" +
                " 36 35  0  0  0  0            999 V2000\n" +
                "    4.4200   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "    5.7710   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "    7.1220   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "    8.4730   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "    9.8240   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   11.1750   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   12.5260   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   13.8770   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   15.2280   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   16.5790   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   17.9300   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   19.2810   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   20.6320   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   21.9830   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   23.3340   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   24.6850   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   26.0360   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   27.3870   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   28.7380   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   30.0890   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   31.4400   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   32.7910   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   34.1420   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   35.4930   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   36.8440   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   38.1950   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   39.5460   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   40.8970   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   42.2480   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   43.5990   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   44.9500   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   46.3010   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   47.6520   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   49.0030   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   50.3540   -6.5520    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "   51.7050   -7.3320    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0\n" +
                "  1  2  1  0  0  0  0\n" +
                "  2  3  1  0  0  0  0\n" +
                "  3  4  1  0  0  0  0\n" +
                "  4  5  1  0  0  0  0\n" +
                "  5  6  1  0  0  0  0\n" +
                "  6  7  1  0  0  0  0\n" +
                "  7  8  1  0  0  0  0\n" +
                "  8  9  1  0  0  0  0\n" +
                "  9 10  1  0  0  0  0\n" +
                " 10 11  1  0  0  0  0\n" +
                " 11 12  1  0  0  0  0\n" +
                " 12 13  1  0  0  0  0\n" +
                " 13 14  1  0  0  0  0\n" +
                " 14 15  1  0  0  0  0\n" +
                " 15 16  1  0  0  0  0\n" +
                " 16 17  1  0  0  0  0\n" +
                " 17 18  1  0  0  0  0\n" +
                " 18 19  1  0  0  0  0\n" +
                " 19 20  1  0  0  0  0\n" +
                " 20 21  1  0  0  0  0\n" +
                " 21 22  1  0  0  0  0\n" +
                " 22 23  1  0  0  0  0\n" +
                " 23 24  1  0  0  0  0\n" +
                " 24 25  1  0  0  0  0\n" +
                " 25 26  1  0  0  0  0\n" +
                " 26 27  1  0  0  0  0\n" +
                " 27 28  1  0  0  0  0\n" +
                " 28 29  1  0  0  0  0\n" +
                " 29 30  1  0  0  0  0\n" +
                " 30 31  1  0  0  0  0\n" +
                " 31 32  1  0  0  0  0\n" +
                " 32 33  1  0  0  0  0\n" +
                " 33 34  1  0  0  0  0\n" +
                " 34 35  1  0  0  0  0\n" +
                " 35 36  1  0  0  0  0\n" +
                "M  CHG  8   1   1   2   1   3   1   4   1   5   1   6   1   7   1   8   1\n" +
                "M  CHG  8   9   1  10   1  11   1  12   1  13   1  14   1  15   1  16   1\n" +
                "M  CHG  8  17   1  18   1  19   1  20   1  21   1  22   1  23   1  24   1\n" +
                "M  CHG  8  25   1  26   1  27   1  28   1  29   1  30   1  31   1  32   1\n" +
                "M  CHG  4  33   1  34   1  35   1  36   1\n" +
                "M  END";

        String mod = ChemCleaner.getCleanMolfile(moreThan31Charges);

        assertEquals(expected,mod);

    }
}
