package nickronus.flight_information_calculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "flight_info.db";
    private static final int DATABASE_VERSION = 1;

    // Таблица Voyage
    private static final String TABLE_VOYAGE = "voyage";
    private static final String COLUMN_VOYAGE_NAME = "name";
    private static final String COLUMN_EMPTY_MASS = "empty_mass";
    private static final String COLUMN_AVG_PASS_MASS = "avg_pass_mass";
    private static final String COLUMN_TAKEOFF_TIME = "takeoff_time";
    private static final String COLUMN_PLANNED_TAKEOFF_TIME = "planned_takeoff_time";
    private static final String COLUMN_PRE_FLIGHT_TIME = "pre_flight_time";
    private static final String COLUMN_POST_FLIGHT_TIME = "post_flight_time";
    private static final String COLUMN_BASE_CENTERING = "base_centering";
    private static final String COLUMN_REMAINING = "remaining";

    // Таблица Flight
    private static final String TABLE_FLIGHT = "flight";
    private static final String COLUMN_FLIGHT_ID = "id";
    private static final String COLUMN_VOYAGE_NAME_FK = "voyage_name";
    private static final String COLUMN_REMAINING_FUEL = "remaining_fuel";
    private static final String COLUMN_REFUELED = "refueled";
    private static final String COLUMN_PEOPLE = "people";
    private static final String COLUMN_CARGO = "cargo";
    private static final String COLUMN_GROUND_TIME = "ground_time";
    private static final String COLUMN_PARKING_TIME = "parking_time";
    private static final String COLUMN_FLIGHT_TIME = "flight_time";
    private static final String COLUMN_LANDING_TIME = "landing_time";

    private static DbHelper instance;

    public static synchronized DbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создание таблицы Voyage (name теперь PRIMARY KEY)
        String createVoyageTable = "CREATE TABLE " + TABLE_VOYAGE + "("
                + COLUMN_VOYAGE_NAME + " TEXT PRIMARY KEY,"
                + COLUMN_EMPTY_MASS + " REAL,"
                + COLUMN_AVG_PASS_MASS + " REAL,"
                + COLUMN_TAKEOFF_TIME + " TEXT,"
                + COLUMN_PLANNED_TAKEOFF_TIME + " TEXT,"
                + COLUMN_PRE_FLIGHT_TIME + " INTEGER,"
                + COLUMN_POST_FLIGHT_TIME + " INTEGER,"
                + COLUMN_BASE_CENTERING + " INTEGER,"
                + COLUMN_REMAINING + " INTEGER"
                + ")";
        db.execSQL(createVoyageTable);

        // Создание таблицы Flight (теперь ссылается на voyage_name)
        String createFlightTable = "CREATE TABLE " + TABLE_FLIGHT + "("
                + COLUMN_FLIGHT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_VOYAGE_NAME_FK + " TEXT,"
                + COLUMN_REMAINING_FUEL + " REAL,"
                + COLUMN_REFUELED + " REAL,"
                + COLUMN_PEOPLE + " INTEGER,"
                + COLUMN_CARGO + " REAL,"
                + COLUMN_GROUND_TIME + " INTEGER,"
                + COLUMN_PARKING_TIME + " INTEGER,"
                + COLUMN_FLIGHT_TIME + " INTEGER,"
                + COLUMN_LANDING_TIME + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_VOYAGE_NAME_FK + ") REFERENCES " + TABLE_VOYAGE + "(" + COLUMN_VOYAGE_NAME + ")"
                + ")";
        db.execSQL(createFlightTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLIGHT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOYAGE);
        onCreate(db);
    }

    // Метод для сохранения Voyage (теперь обновляет, если уже существует)
    public void addVoyage(Voyage voyage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_VOYAGE_NAME, voyage.name);
        values.put(COLUMN_EMPTY_MASS, voyage.emptyAircraftMass);
        values.put(COLUMN_AVG_PASS_MASS, voyage.averagePassengerMass);
        values.put(COLUMN_TAKEOFF_TIME, voyage.takeoffTime != null ? voyage.takeoffTime.toString() : null);
        values.put(COLUMN_PLANNED_TAKEOFF_TIME, voyage.plannedTakeoffTime != null ? voyage.plannedTakeoffTime.toString() : null);
        values.put(COLUMN_PRE_FLIGHT_TIME, voyage.preFlightTime);
        values.put(COLUMN_POST_FLIGHT_TIME, voyage.postFlightTime);
        values.put(COLUMN_BASE_CENTERING, voyage.baseCentering);
        values.put(COLUMN_REMAINING, voyage.remaining);

        // Используем insertWithOnConflict для обновления существующей записи
        db.insertWithOnConflict(TABLE_VOYAGE, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        // Сохраняем все Flight этого Voyage (предварительно удаляем старые)
        if (voyage.flights != null && !voyage.flights.isEmpty()) {
            // Удаляем старые рейсы перед добавлением новых
            db.delete(TABLE_FLIGHT, COLUMN_VOYAGE_NAME_FK + " = ?", new String[]{voyage.name});

            for (Flight flight : voyage.flights) {
                addFlight(flight, voyage.name);
            }
        }

        db.close();
    }

    // Метод для сохранения Flight (теперь принимает voyageName вместо voyageId)
    public long addFlight(Flight flight, String voyageName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_VOYAGE_NAME_FK, voyageName);
        values.put(COLUMN_REMAINING_FUEL, flight.remaining);
        values.put(COLUMN_REFUELED, flight.refueled);
        values.put(COLUMN_PEOPLE, flight.people);
        values.put(COLUMN_CARGO, flight.cargo);
        values.put(COLUMN_GROUND_TIME, flight.groundTime);
        values.put(COLUMN_PARKING_TIME, flight.parkingTime);
        values.put(COLUMN_FLIGHT_TIME, flight.flightTime);
        values.put(COLUMN_LANDING_TIME, flight.landingTime != null ? flight.landingTime.toString() : null);

        long flightId = db.insert(TABLE_FLIGHT, null, values);
        db.close();
        return flightId;
    }

    // Метод для получения всех Voyage
    public List<Voyage> getAllVoyages() {
        List<Voyage> voyageList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_VOYAGE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Voyage voyage = getVoyageFromCursor(cursor);
                    voyage.flights = getFlightsForVoyage(getStringSafe(cursor, COLUMN_VOYAGE_NAME, ""));
                    voyageList.add(voyage);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
            db.close();
        }
        return voyageList;
    }

    // Метод для получения всех Flight для конкретного Voyage (теперь по имени)
    private List<Flight> getFlightsForVoyage(String voyageName) {
        List<Flight> flightList = new ArrayList<>();
        if (voyageName == null || voyageName.isEmpty()) return flightList;

        String selectQuery = "SELECT * FROM " + TABLE_FLIGHT + " WHERE " + COLUMN_VOYAGE_NAME_FK + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{voyageName});

        try {
            if (cursor.moveToFirst()) {
                do {
                    Flight flight = getFlightFromCursor(cursor);
                    flightList.add(flight);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
            db.close();
        }
        return flightList;
    }

    // Безопасное создание Voyage из Cursor
    private Voyage getVoyageFromCursor(Cursor cursor) {
        Voyage voyage = new Voyage(
                getStringSafe(cursor, COLUMN_VOYAGE_NAME, ""),
                getDoubleSafe(cursor, COLUMN_EMPTY_MASS, 0),
                getDoubleSafe(cursor, COLUMN_AVG_PASS_MASS, 0)
        );

        voyage.takeoffTime = parseDateTime(getStringSafe(cursor, COLUMN_TAKEOFF_TIME, null));
        voyage.plannedTakeoffTime = parseDateTime(getStringSafe(cursor, COLUMN_PLANNED_TAKEOFF_TIME, null));
        voyage.preFlightTime = getIntSafe(cursor, COLUMN_PRE_FLIGHT_TIME, 0);
        voyage.postFlightTime = getIntSafe(cursor, COLUMN_POST_FLIGHT_TIME, 0);
        voyage.baseCentering = getIntSafe(cursor, COLUMN_BASE_CENTERING, 0);
        voyage.remaining = getIntSafe(cursor, COLUMN_REMAINING, 0);

        return voyage;
    }

    // Безопасное создание Flight из Cursor
    private Flight getFlightFromCursor(Cursor cursor) {
        return new Flight(
                getDoubleSafe(cursor, COLUMN_REMAINING_FUEL, 0),
                getDoubleSafe(cursor, COLUMN_REFUELED, 0),
                getIntSafe(cursor, COLUMN_PEOPLE, 0),
                getDoubleSafe(cursor, COLUMN_CARGO, 0),
                getIntSafe(cursor, COLUMN_GROUND_TIME, 0),
                getIntSafe(cursor, COLUMN_PARKING_TIME, 0),
                getIntSafe(cursor, COLUMN_FLIGHT_TIME, 0),
                parseDateTime(getStringSafe(cursor, COLUMN_LANDING_TIME, null))
        );
    }

    public void recreateDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, DATABASE_VERSION, DATABASE_VERSION);
        db.close();
    }

    // Безопасные методы для работы с Cursor
    private String getStringSafe(Cursor cursor, String columnName, String defaultValue) {
        int index = cursor.getColumnIndex(columnName);
        return index >= 0 ? cursor.getString(index) : defaultValue;
    }

    private int getIntSafe(Cursor cursor, String columnName, int defaultValue) {
        int index = cursor.getColumnIndex(columnName);
        return index >= 0 ? cursor.getInt(index) : defaultValue;
    }

    private long getLongSafe(Cursor cursor, String columnName, long defaultValue) {
        int index = cursor.getColumnIndex(columnName);
        return index >= 0 ? cursor.getLong(index) : defaultValue;
    }

    private double getDoubleSafe(Cursor cursor, String columnName, double defaultValue) {
        int index = cursor.getColumnIndex(columnName);
        return index >= 0 ? cursor.getDouble(index) : defaultValue;
    }

    // Метод для парсинга строки в LocalDateTime с обработкой ошибок
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Дополнительные методы для работы с базой данных
    public boolean deleteVoyage(String voyageName) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            // Удаляем сначала все связанные Flight
            db.delete(TABLE_FLIGHT, COLUMN_VOYAGE_NAME_FK + " = ?", new String[]{voyageName});

            // Затем удаляем сам Voyage
            int deletedRows = db.delete(TABLE_VOYAGE, COLUMN_VOYAGE_NAME + " = ?", new String[]{voyageName});
            return deletedRows > 0;
        } finally {
            db.close();
        }
    }
}