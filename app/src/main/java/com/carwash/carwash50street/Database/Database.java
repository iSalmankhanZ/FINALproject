package com.carwash.carwash50street.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.carwash.carwash50street.Model.Favourites;
import com.carwash.carwash50street.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;
public class Database extends SQLiteAssetHelper {
    private static final String DB_NAME = "CARWASH.db";
    private static final int DB_VER = 2;
    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public boolean checkServiceExists(String serviceId,String userPhone)
    {
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQUERY = String.format("SELECT * FROM OrderDetail WHERE UserPhone='%s' AND ProductId ='%s'",userPhone,serviceId);
        cursor = db.rawQuery(SQLQUERY,null);
        if(cursor.getCount()>0)
            flag = true;
        else
            cursor.close();
        return flag;
    }

    public List<Order> getCarts() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"ProductName", "ProductId", "Quantity", "Price", "Discount"};
        String sqlTable = "OrderDetail";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db,sqlSelect,null,null,null,null,null);

        final List<Order> result = new ArrayList<>();
        if (c.moveToFirst())
        {
            do {
                result.add(new Order(c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount"))
                ));

            } while(c.moveToNext());
        }
        return result;
    }

    public void addToCart(Order order)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO OrderDetail(ProductId,ProductName,Quantity,Price,Discount) VALUES('%s','%s','%s','%s','%s');",
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount());
        db.execSQL(query);
    }

    public void cleanCart()
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail");
        db.execSQL(query);
    }



    /*public List<Order> getCarts(String userPhone) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"UserPhone","ProductName", "ProductId", "Quantity", "Price", "Discount"};
        String sqlTable = "OrderDetail";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db,sqlSelect,"UserPhone=?",new String[]{userPhone},null,null,null);

        final List<Order> result = new ArrayList<>();
        if (c.moveToFirst())
        {
            do {
                result.add(new Order(
                        c.getString(c.getColumnIndex("UserPhone")),
                        c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount"))
                        ));

            } while(c.moveToNext());
        }
        return result;
    }

    public void addToCart(Order order)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT OR REPLACE INTO OrderDetail(UserPhone,ProductId,ProductName,Quantity,Price,Discount) VALUES('%s','%s','%s','%s','%s','%s');",
                order.getUserPhone(),
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount());
        db.execSQL(query);
    }

    public void cleanCart(String userPhone)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE UserPhone='%s'",userPhone);
        db.execSQL(query);
    }*/
    //Favourites
    public void addToFavourites(Favourites service)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Favourites("+
                "ServiceId,ServiceName,ServicePrice,ServiceMenuId,ServiceImage,ServiceDiscount,ServiceDescription,UserPhone) VALUES('%s','%s','%s','%s','%s','%s','%s','%s');",
                service.getServiceId(),
                service.getServiceName(),
                service.getServicePrice(),
                service.getServiceMenuId(),
                service.getServiceImage(),
                service.getServiceDiscount(),
                service.getServiceDescription(),
                service.getUserPhone());
        db.execSQL(query);
    }

    public void removeFromCart(String productId, String phone) {

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE UserPhone='%s' and ProductId = '%s",phone,productId);
        db.execSQL(query);
    }


    public void removeFromFavourites(String serviceId,String userPhone)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM Favourites WHERE ServiceId='%s' and UserPhone = '%s' ;",serviceId,userPhone);
        db.execSQL(query);
    }

    public boolean isFavourites(String serviceId,String userPhone)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM Favourites WHERE ServiceId = '%s' and UserPhone = '%s' ;",serviceId,userPhone);
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.getCount()<0)
        {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public List<Favourites> getAllFavourites(String userPhone) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"UserPhone","ServiceId","ServiceName","ServicePrice","ServiceMenuId","ServiceImage","ServiceDiscount","ServiceDescriprtion",};
        String sqlTable = "Favourites";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db,sqlSelect,"UserPhone=?",new String[]{userPhone},null,null,null,null,null);

        final List<Favourites> result = new ArrayList<>();
        if (c.moveToFirst())
        {
            do {
                result.add(new Favourites(
                        c.getString(c.getColumnIndex("ServiceId")),
                        c.getString(c.getColumnIndex("ServiceName")),
                        c.getString(c.getColumnIndex("ServicePrice")),
                        c.getString(c.getColumnIndex("ServiceMenuId")),
                        c.getString(c.getColumnIndex("ServiceImage")),
                        c.getString(c.getColumnIndex("ServiceDiscount")),
                        c.getString(c.getColumnIndex("ServiceDescription")),
                        c.getString(c.getColumnIndex("UserPhone"))

                ));

            } while(c.moveToNext());
        }
        return result;
    }
}
