package uk.co.dnlegge.order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ForgettingOrderTest {

    ForgettingOrder<String> beingTested;

    @Test
    public void testAddSimple() throws Exception {
        beingTested = new ForgettingOrderList<>();

        addOneItem();

        assertTrue(beingTested.contains("1"));

    }

    private void addOneItem() {
        assertEquals(0, beingTested.getSize());
        beingTested.add("1");
        assertEquals(1, beingTested.getSize());
    }

    @Test
    public void testAddADuplicate() throws Exception {
        beingTested = new ForgettingOrderList<>();

        addOneItem();

        assertTrue(beingTested.contains("1"));

        //try to insert the same key again
        beingTested.add("1");
        assertEquals(1, beingTested.getSize());

        assertTrue(beingTested.contains("1"));

    }

    @Test
    public void testAddCheckOrdering() throws Exception {
        beingTested = new ForgettingOrderList<>();

        addFiveItems();

        assertTrue(beingTested.contains("1"));
        assertEquals(4, beingTested.getIndexOf("1"));
        assertTrue(beingTested.contains("5"));
        assertEquals(0, beingTested.getIndexOf("5"));
    }

    @Test
    public void testRemoveAndReturnLast() throws Exception {

        beingTested = new ForgettingOrderList<>();

        addFiveItems();

        assertTrue(beingTested.contains("1"));
        assertEquals(4, beingTested.getIndexOf("1"));
        assertTrue(beingTested.contains("5"));
        assertEquals(0, beingTested.getIndexOf("5"));

        final String itemReturned = beingTested.removeAndReturnLast();
        assertEquals("1", itemReturned);
        assertEquals(4, beingTested.getSize());
        assertFalse(beingTested.contains("1"));
    }

    private void addFiveItems() {
        assertEquals(0, beingTested.getSize());
        beingTested.add("1");
        beingTested.add("2");
        beingTested.add("3");
        beingTested.add("4");
        beingTested.add("5");
        assertEquals(5, beingTested.getSize());
    }

    @Test
    public void testMoveToFront() throws Exception {
        beingTested = new ForgettingOrderList<>();

        addFiveItems();

        assertTrue(beingTested.contains("1"));
        assertEquals(4, beingTested.getIndexOf("1"));
        assertTrue(beingTested.contains("5"));
        assertEquals(0, beingTested.getIndexOf("5"));

        beingTested.moveToFront("1");
        assertEquals(5, beingTested.getSize());
        assertTrue(beingTested.contains("1"));
        assertEquals(0, beingTested.getIndexOf("1"));
        assertEquals(1, beingTested.getIndexOf("5"));
        assertEquals(4, beingTested.getIndexOf("2"));

    }

}