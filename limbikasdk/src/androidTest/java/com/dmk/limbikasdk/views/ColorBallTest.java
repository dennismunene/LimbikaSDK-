package com.dmk.limbikasdk.views;

import android.graphics.Point;
import android.test.AndroidTestCase;

import com.dmk.limbikasdk.R;

/**
 * ColorBall backs the four drag handles around a LimbikaView, so its position
 * bookkeeping is what makes resizing land in the right place.
 */
public class ColorBallTest extends AndroidTestCase {

    private ColorBall ballAt(int x, int y) {
        return new ColorBall(getContext(), R.drawable.ic_circle_default,
                new Point(x, y));
    }

    public void testReportsThePositionItWasGiven() {
        ColorBall ball = ballAt(40, 60);

        assertEquals(40, ball.getX());
        assertEquals(60, ball.getY());
    }

    public void testMovesToAnAbsolutePosition() {
        ColorBall ball = ballAt(0, 0);

        ball.setX(120);
        ball.setY(80);

        assertEquals(120, ball.getX());
        assertEquals(80, ball.getY());
    }

    public void testAccumulatesRelativeMoves() {
        ColorBall ball = ballAt(10, 10);

        ball.addX(5);
        ball.addX(-3);
        ball.addY(15);

        assertEquals(12, ball.getX());
        assertEquals(25, ball.getY());
    }

    public void testWritesThroughToThePointItWasBuiltWith() {
        Point shared = new Point(1, 2);
        ColorBall ball = new ColorBall(getContext(), R.drawable.ic_circle_default,
                shared);

        ball.setX(7);
        ball.addY(3);

        assertEquals("handle and caller must share one point", 7, shared.x);
        assertEquals(5, shared.y);
    }

    public void testHandOutsIdsForFourHandlesOnly() {
        for (int i = 0; i < 8; i++) {
            int id = ballAt(0, 0).getID();

            assertTrue("id " + id + " outside the four handle slots",
                    id >= 0 && id <= 3);
        }
    }

    public void testExposesTheDecodedBitmapSize() {
        ColorBall ball = ballAt(0, 0);

        assertNotNull(ball.getBitmap());
        assertEquals(ball.getBitmap().getWidth(), ball.getWidthOfBall());
        assertEquals(ball.getBitmap().getHeight(), ball.getHeightOfBall());
        assertTrue("handle bitmap should have a size", ball.getWidthOfBall() > 0);
    }
}
