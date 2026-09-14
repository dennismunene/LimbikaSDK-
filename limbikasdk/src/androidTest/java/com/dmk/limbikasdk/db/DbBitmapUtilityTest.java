package com.dmk.limbikasdk.db;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.test.AndroidTestCase;

/**
 * The saved view state stores the widget image as a byte array, so the
 * conversion has to survive a full round trip without losing pixels.
 */
public class DbBitmapUtilityTest extends AndroidTestCase {

    private Bitmap source;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        source = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
        source.setPixel(0, 0, Color.RED);
        source.setPixel(1, 0, Color.GREEN);
        source.setPixel(0, 1, Color.BLUE);
        source.setPixel(1, 1, Color.TRANSPARENT);
    }

    @Override
    protected void tearDown() throws Exception {
        if (source != null) {
            source.recycle();
        }
        super.tearDown();
    }

    public void testEncodesToPng() {
        byte[] bytes = DbBitmapUtility.getBytes(source);

        assertNotNull(bytes);
        assertTrue("expected a non-empty payload", bytes.length > 0);
        assertEquals((byte) 0x89, bytes[0]);
        assertEquals((byte) 'P', bytes[1]);
        assertEquals((byte) 'N', bytes[2]);
        assertEquals((byte) 'G', bytes[3]);
    }

    public void testRoundTripKeepsSizeAndPixels() {
        Bitmap restored = DbBitmapUtility.getImage(DbBitmapUtility.getBytes(source));

        assertNotNull("stored bitmap should decode again", restored);
        assertEquals(source.getWidth(), restored.getWidth());
        assertEquals(source.getHeight(), restored.getHeight());
        assertEquals(Color.RED, restored.getPixel(0, 0));
        assertEquals(Color.GREEN, restored.getPixel(1, 0));
        assertEquals(Color.BLUE, restored.getPixel(0, 1));
        assertEquals(Color.TRANSPARENT, restored.getPixel(1, 1));
        restored.recycle();
    }

    public void testReturnsNullForDataThatIsNotAnImage() {
        assertNull(DbBitmapUtility.getImage(new byte[]{1, 2, 3, 4}));
    }
}
