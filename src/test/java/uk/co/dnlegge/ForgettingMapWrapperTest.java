package uk.co.dnlegge;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ForgettingMapWrapperTest {

    private ForgettingMap<Integer, String> beingTested;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testSizeConstraint() throws Exception {

        beingTested = new ForgettingMapWrapper<>(1);

        assertEquals(1, beingTested.maxSize());

        beingTested = new ForgettingMapWrapper<>(5);

        assertEquals(5, beingTested.maxSize());

    }

    @Test
    public void testGetSize() throws Exception {

        beingTested = new ForgettingMapWrapper<>(1);

        assertEquals(0, beingTested.size());

        beingTested.add(0, "0");

        assertEquals(1, beingTested.size());

    }

    @Test
    public void testFind() throws Exception {
        beingTested = new ForgettingMapWrapper<>(1);

        assertEquals(0, beingTested.size());

        beingTested.add(0, "0");

        assertEquals(1, beingTested.size());
        assertEquals("0", beingTested.find(0));

    }

    @Test
    public void testAddSoOriginalElementForgotten() throws Exception {

        beingTested = new ForgettingMapWrapper<>(1);

        assertEquals(0, beingTested.size());

        beingTested.add(0, "0");

        assertEquals(1, beingTested.size());
        assertEquals("0", beingTested.find(0));

        beingTested.add(1, "1");

        assertEquals(1, beingTested.size());
        assertEquals("1", beingTested.find(1));
        //original element now forgotten
        assertEquals(null, beingTested.find(0));

    }

    @Test
    public void testFindTwoElementsNotForgotten() throws Exception {

        beingTested = new ForgettingMapWrapper<>(2);

        assertEquals(0, beingTested.size());

        beingTested.add(0, "0");

        assertEquals(1, beingTested.size());
        assertEquals("0", beingTested.find(0));

        beingTested.add(1, "1");

        assertEquals(2, beingTested.size());
        assertEquals("1", beingTested.find(1));
        //original element now forgotten
        assertEquals("0", beingTested.find(0));

    }

    @Test
    public void testAddThreeElementsOrderingCorrect() throws Exception {

        beingTested = new ForgettingMapWrapper<>(2);

        assertEquals(0, beingTested.size());

        beingTested.add(0, "0");

        assertEquals(1, beingTested.size());
        assertEquals("0", beingTested.find(0));

        beingTested.add(1, "1");

        assertEquals(2, beingTested.size());
        assertEquals("1", beingTested.find(1));
        //original element now forgotten
        assertEquals("0", beingTested.find(0));

        beingTested.add(2, "2");

        assertEquals(2, beingTested.size());
        assertEquals(null, beingTested.find(1));
        //original element now forgotten
        assertEquals("0", beingTested.find(0));

    }


}