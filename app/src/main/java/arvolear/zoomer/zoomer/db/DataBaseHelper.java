package arvolear.zoomer.zoomer.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import arvolear.zoomer.zoomer.market.MarketController;

public class DataBaseHelper extends SQLiteOpenHelper
{
    private static DataBaseHelper DBHelper = null;
    private static SQLiteDatabase DB = null;

    private static final String DB_NAME = "zoomer";
    private static final int DB_VERSION = 2;

    private static final String GAME_TABLE_NAME = "game";
    private static final String BOOSTERS_TABLE_NAME = "boosters";
    private static final String COLORING_TABLE_NAME = "coloring";

    private AppCompatActivity activity;

    private DataBaseHelper(AppCompatActivity activity, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(activity, name, factory, version);

        this.activity = activity;

        DB = getWritableDatabase();
    }

    public static DataBaseHelper getDatabaseHelper(AppCompatActivity activity)
    {
        if (DBHelper == null)
        {
            DBHelper = new DataBaseHelper(activity, DB_NAME, null, DB_VERSION);
        }

        return DBHelper;
    }

    private String queryString(String what)
    {
        String projection[] = {
                "title",
                "value"
        };

        String selection = "title = ?";
        String args[] = {what};

        Cursor cursor = DB.query(GAME_TABLE_NAME, projection, selection, args, null, null, null);
        cursor.moveToFirst();

        String ans = cursor.getString(cursor.getColumnIndexOrThrow("value"));

        cursor.close();

        return ans;
    }

    private void setString(String to, String what)
    {
        ContentValues values = new ContentValues();
        values.put("value", what);

        String selection = "title = ?";
        String args[] = {to};

        DB.update(GAME_TABLE_NAME, values, selection, args);
    }

    public boolean isFirstTime()
    {
        return Boolean.parseBoolean(queryString("first_time"));
    }

    public void setExperienced()
    {
        setString("first_time", "false");
    }

    public void setExperiencedPopUp(String type)
    {
        setString(type, "false");
    }

    public void setShowPopUp(String type)
    {
        setString(type, "true");
    }

    public boolean isPopUp(String type)
    {
        return Boolean.parseBoolean(queryString(type));
    }

    public boolean isFirstBuy()
    {
        return Boolean.parseBoolean(queryString("first_buy"));
    }

    public void setExperiencedBuy()
    {
        setString("first_buy", "false");
    }

    public boolean isSound()
    {
        return Boolean.parseBoolean(queryString("sound"));
    }

    public void setSound(boolean sound)
    {
        setString("sound", String.valueOf(sound));
    }

    public void setMaxZoom(double maxZoom)
    {
        setString("max_zoom", String.valueOf(maxZoom));
    }

    public double getMaxZoom()
    {
        return Double.parseDouble(queryString("max_zoom"));
    }

    public void setCoins(String coins)
    {
        setString("coins", coins);
    }

    public String getCoins()
    {
        return queryString("coins");
    }

    public void setCoinsAddsAmount(int amount)
    {
        setString("coins_adds_amount", String.valueOf(amount));
    }

    public int getCoinsAddsAmount()
    {
        return Integer.parseInt(queryString("coins_adds_amount"));
    }

    public void setCurrentDay(String day)
    {
        setString("current_day", day);
    }

    public String getCurrentDay()
    {
        return queryString("current_day");
    }

    public void setStatus(String type, int index, String status)
    {
        ContentValues values = new ContentValues();
        values.put("status", status);

        String selection = "title = ?";
        String args[] = {type + "_" + index};

        DB.update(type, values, selection, args);
    }

    public String getStatus(String type, int index)
    {
        String projection[] = {
                "title",
                "status",
        };

        String selection = "title = ?";
        String args[] = {type + "_" + index};

        Cursor cursor = DB.query(type, projection, selection, args, null, null, null);
        cursor.moveToFirst();

        String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));

        cursor.close();

        return status;
    }

    public String getEquippedIndex(String type)
    {
        String projection[] = {
                "title",
                "status"
        };

        String selection = "status = ?";
        String args[] = {"equipped"};

        Cursor cursor = DB.query(type, projection, selection, args, null, null, null);

        String element = "";

        if (cursor.moveToFirst())
        {
            element = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        }

        cursor.close();

        StringBuilder ans = new StringBuilder("");

        for (int i = element.length() - 1; i >= 0; i--)
        {
            if (!String.valueOf(element.charAt(i)).matches("[0-9]"))
            {
                break;
            }

            ans.append(element.charAt(i));
        }

        return ans.reverse().toString();
    }

    private void configureGameTable(SQLiteDatabase db)
    {
        String titles[] = {
                "first_time",
                "sound",
                "first_buy",
                "first_popup",
                "first_zoom_popup",
                "booster_popup",
                "epoch_popup",
                "coins_store_popup",
                "horizon_popup",
                "easter_popup",
                "coins",
                "max_zoom",
                "coins_adds_amount",
                "current_day"
        };

        String values[] = {
                "true",
                "true",
                "true",
                "true",
                "true",
                "true",
                "true",
                "false",
                "true",
                "true",
                "0",
                "0.0",
                "0",
                "0"
        };

        for (int i = 0; i < values.length; i++)
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put("title", titles[i]);
            contentValues.put("value", values[i]);

            db.insert(GAME_TABLE_NAME, null, contentValues);
        }
    }

    private void configureBoostersTable(SQLiteDatabase db)
    {
        ContentValues values = new ContentValues();

        for (int i = 0; i < MarketController.BOOSTERS_AMOUNT; i++)
        {
            values.put("title", "boosters_" + i);
            values.put("status", "locked"); // locked, bought, equipped

            db.insert(BOOSTERS_TABLE_NAME, null, values);
        }
    }

    private void configureColoringTable(SQLiteDatabase db)
    {
        ContentValues values = new ContentValues();

        for (int i = 0; i < MarketController.COLORING_AMOUNT; i++)
        {
            values.put("title", "coloring_" + i);
            values.put("status", "locked"); // locked, bought, equipped

            db.insert(COLORING_TABLE_NAME, null, values);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.d("Zoomer", "Create data base");

        db.execSQL("CREATE TABLE " + GAME_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "value TEXT" + ");");

        db.execSQL("CREATE TABLE " + BOOSTERS_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "status TEXT" + ");");

        db.execSQL("CREATE TABLE " + COLORING_TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "status TEXT" + ");");

        configureGameTable(db);
        configureBoostersTable(db);
        configureColoringTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.d("Zoomer", "Update data base from " + oldVersion + " to " + newVersion);

        if (oldVersion < newVersion && newVersion == 2)
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put("title", "first_zoom_popup");
            contentValues.put("value", "false");

            db.insert(GAME_TABLE_NAME, null, contentValues);
        }
    }
}
