package uk.co.dnlegge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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


        beingTested = new ForgettingMapWrapper<>(10);

        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 50; i++) {
            final int count = i;
            final Runnable task = () -> {
                try {
                    beingTested.add(count, "" + count);
                    assertEquals("0", beingTested.find(0));
                    assertEquals("" + count, beingTested.find(count));
                    System.out.println(count);
                    assertEquals("1", beingTested.find(1));

                } catch (Exception e) {
                    System.out.println("exception caught " + e + e.getMessage());
                    fail();
                }

            };
            executor.submit(task);

        }
        System.out.println("All submitted");
        // As much as I hate using sleeps, let the test catch up
        Thread.sleep(1000);
        System.out.println("Times up!");
    }
}