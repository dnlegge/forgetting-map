package uk.co.dnlegge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

public class ForgettingMapTest {

    private ForgettingMap<Integer, String> beingTested;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testSizeConstraint() throws Exception {

        beingTested = new ForgettingMapWrapper<>(1);

        assertEquals(1, beingTested.getMaxSize());

        beingTested = new ForgettingMapWrapper<>(5);

        assertEquals(5, beingTested.getMaxSize());

    }

    @Test
    public void testGetSize() throws Exception {

        beingTested = new ForgettingMapWrapper<>(1);

        assertEquals(0, beingTested.getSize());

        beingTested.add(0, "0");

        assertEquals(1, beingTested.getSize());

    }

    @Test
    public void testGetSize10() throws Exception {

        beingTested = new ForgettingMapWrapper<>(10);

        assertEquals(0, beingTested.getSize());

        beingTested.add(0, "0");
        beingTested.add(1, "1");
        beingTested.add(2, "2");
        beingTested.add(3, "3");
        beingTested.add(4, "4");

        assertEquals(5, beingTested.getSize());

        beingTested.add(5, "5");
        beingTested.add(6, "6");
        beingTested.add(7, "7");
        beingTested.add(8, "8");
        beingTested.add(9, "9");

        assertEquals(10, beingTested.getSize());

        beingTested.add(10, "10");

        assertEquals(10, beingTested.getSize());

    }


    @Test
    public void testAddDuplicates() throws Exception {

        beingTested = new ForgettingMapWrapper<>(10);

        assertEquals(0, beingTested.getSize());

        beingTested.add(0, "0");
        beingTested.add(0, "0");
        beingTested.add(0, "0");
        beingTested.add(0, "0");
        beingTested.add(0, "0");

        assertEquals(1, beingTested.getSize());

        beingTested.add(0, "0");
        beingTested.add(0, "0");
        beingTested.add(0, "0");
        beingTested.add(0, "0");
        beingTested.add(0, "0");

        assertEquals(1, beingTested.getSize());

        beingTested.add(0, "0");
        beingTested.add(0, "0");
        beingTested.add(0, "0");
        beingTested.add(0, "0");
        beingTested.add(0, "0");

        assertEquals(1, beingTested.getSize());

    }

    @Test
    public void testFind() throws Exception {
        beingTested = new ForgettingMapWrapper<>(1);

        assertEquals(0, beingTested.getSize());

        beingTested.add(0, "0");

        assertEquals(1, beingTested.getSize());
        assertEquals("0", beingTested.find(0));

    }

    @Test
    public void testAddSoOriginalElementForgotten() throws Exception {

        beingTested = new ForgettingMapWrapper<>(1);

        assertEquals(0, beingTested.getSize());

        beingTested.add(0, "0");

        assertEquals(1, beingTested.getSize());
        assertEquals("0", beingTested.find(0));

        beingTested.add(1, "1");

        assertEquals(1, beingTested.getSize());
        assertEquals("1", beingTested.find(1));
        //original element now forgotten
        assertEquals(null, beingTested.find(0));

    }

    @Test
    public void testFindTwoElementsNotForgotten() throws Exception {

        beingTested = new ForgettingMapWrapper<>(2);

        assertEquals(0, beingTested.getSize());

        beingTested.add(0, "0");

        assertEquals(1, beingTested.getSize());
        assertEquals("0", beingTested.find(0));

        beingTested.add(1, "1");

        assertEquals(2, beingTested.getSize());
        assertEquals("1", beingTested.find(1));
        //original element now forgotten
        assertEquals("0", beingTested.find(0));

    }

    @Test
    public void testAddFourElementsOrderingCorrect() throws Exception {

        beingTested = new ForgettingMapWrapper<>(2);

        assertEquals(0, beingTested.getSize());

        beingTested.add(0, "0");

        assertEquals(1, beingTested.getSize());
        assertEquals("0", beingTested.find(0));

        beingTested.add(1, "1");

        assertEquals(2, beingTested.getSize());
        assertEquals("1", beingTested.find(1));
        //original element now forgotten
        assertEquals("0", beingTested.find(0));

        beingTested.add(2, "2");

        assertEquals(2, beingTested.getSize());
        assertEquals(null, beingTested.find(1));
        //original element now forgotten
        assertEquals("0", beingTested.find(0));

        beingTested.add(3, "3");

        assertEquals(2, beingTested.getSize());
        assertEquals(null, beingTested.find(1));
        //original element now forgotten
        assertEquals("0", beingTested.find(0));
        assertEquals("3", beingTested.find(3));

    }


    @Test
    public void tryToTestConcurrency() throws Exception {

        beingTested = new ForgettingMapWrapper<>(50);

        ExecutorService executor = Executors.newFixedThreadPool(5);

        List<Future<String>> futures = new ArrayList<>(60);
        beingTested.add(0, "0");
        for (int i = 1; i < 50; i++) {
            final int count = i;
            Future<String> future = executor.submit(() -> {
                try {
                    beingTested.add(count, "" + count);
                    assertEquals("0", beingTested.find(0));
                    assertEquals("" + count, beingTested.find(count));
                    System.out.println(count);

                    final int size = beingTested.getSize();
                    assertTrue(0 < size);
                    assertTrue(50 >= size);

                    return "OK " + count;
                } catch (Exception e) {
                    System.out.println("exception caught " + e + e.getMessage());
                    return "Exception: " + e.getClass().getName() + " " + e.getMessage();
                }

            });
            futures.add(future);

        }
        System.out.println("All submitted");

        for (Future<String> future : futures) {
//            assertTrue(future.isDone());
            final String s = future.get(1, TimeUnit.SECONDS);
            assertTrue(s, s.startsWith("OK "));
        }

        final int size = beingTested.getSize();
        assertTrue(0 < size);
        assertTrue(50 >= size);

        beingTested.validate();

    }

    @Test
    public void tryToTestConcurrencyFurther() throws Exception {

        beingTested = new ForgettingMapWrapper<>(50);

        ExecutorService executor = Executors.newFixedThreadPool(5);

        List<Future<String>> futures = new ArrayList<>(60);

        for (int i = 0; i < 60; i++) {
            final int count = i / 10;
            Future<String> future = executor.submit(() -> {
                try {
                    beingTested.add(count, "" + count);
                    assertEquals("0", beingTested.find(0));
                    assertEquals("" + count, beingTested.find(count));
                    System.out.println(count);

                    final int size = beingTested.getSize();
                    assertTrue(0 < size);
                    assertTrue(6 >= size);

                    return "OK " + count;
                } catch (Exception e) {
                    System.out.println("exception caught " + e + e.getMessage());
                    return "Exception: " + e.getClass().getName() + " " + e.getMessage();
                }

            });
            futures.add(future);

        }
        System.out.println("All submitted");

        for (Future<String> future : futures) {
            final String s = future.get(1, TimeUnit.SECONDS);
            assertTrue(s, s.startsWith("OK "));
        }

        final int size = beingTested.getSize();
        assertTrue(0 < size);
        assertTrue(6 >= size);

        beingTested.validate();
    }
}