package com.dmk.limbikasdk.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

/**
 * The viewState table is written with a positional
 * "insert into viewState values(...)" statement in LimbikaView.saveViewState(),
 * so the column list and its order are part of the contract between the two
 * classes. These tests fail if either side drifts.
 */
public class DatabaseTest extends AndroidTestCase {

    private static final String[] EXPECTED_COLUMNS = {
            "x", "y", "rotation", "key", "isCircleView", "circleColor", "userText",
            "textColor", "textSize", "borderColor", "backgroundColor", "drawable",
            "width", "height", "left", "right", "top", "bottom", "blob"
    };

    private Database helper;
    private SQLiteDatabase db;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getContext().deleteDatabase(Database.DATABASE_NAME);
        helper = new Database(getContext());
        db = helper.getWritableDatabase();
    }

    @Override
    protected void tearDown() throws Exception {
        if (db != null) {
            db.close();
        }
        if (helper != null) {
            helper.close();
        }
        getContext().deleteDatabase(Database.DATABASE_NAME);
        super.tearDown();
    }

    public void testCreatesViewStateTableWithExpectedColumnsInOrder() {
        Cursor cursor = db.rawQuery("select * from viewState", null);
        try {
            String[] actual = cursor.getColumnNames();

            assertEquals("column count", EXPECTED_COLUMNS.length, actual.length);
            for (int i = 0; i < EXPECTED_COLUMNS.length; i++) {
                assertEquals("column " + i, EXPECTED_COLUMNS[i], actual[i]);
            }
        } finally {
            cursor.close();
        }
    }

    public void testAcceptsThePositionalInsertSaveViewStateUses() {
        db.execSQL("insert into viewState values("
                + "12,34,90,'widget-key',1,-16777216,'hello%20world',-1,18,-65536,1,7,"
                + "256,256,10,266,20,276,'blob-data')");

        Cursor cursor = db.rawQuery(
                "select * from viewState where key='widget-key'", null);
        try {
            assertTrue("row should be readable back", cursor.moveToFirst());
            assertEquals(12, cursor.getInt(cursor.getColumnIndex("x")));
            assertEquals(34, cursor.getInt(cursor.getColumnIndex("y")));
            assertEquals(90, cursor.getInt(cursor.getColumnIndex("rotation")));
            assertEquals(1, cursor.getInt(cursor.getColumnIndex("isCircleView")));
            assertEquals("hello%20world",
                    cursor.getString(cursor.getColumnIndex("userText")));
            assertEquals(256, cursor.getInt(cursor.getColumnIndex("width")));
            assertEquals(276, cursor.getInt(cursor.getColumnIndex("bottom")));
            assertEquals("blob-data",
                    cursor.getString(cursor.getColumnIndex("blob")));
        } finally {
            cursor.close();
        }
    }

    public void testKeepsOneRowPerViewKey() {
        db.execSQL("insert into viewState values(0,0,0,'first',0,-1,'',-1,-1,-1,1,-1,"
                + "256,256,0,256,0,256,'')");
        db.execSQL("update viewState set x=99 where key='first'");

        Cursor cursor = db.rawQuery("select x from viewState where key='first'", null);
        try {
            assertEquals("update must not create a second row", 1, cursor.getCount());
            assertTrue(cursor.moveToFirst());
            assertEquals(99, cursor.getInt(0));
        } finally {
            cursor.close();
        }
    }
}
