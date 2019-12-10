package com.antonkrasov.githubtop100.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.antonkrasov.githubtop100.data.dao.ReposDao;
import com.antonkrasov.githubtop100.data.models.Repo;

@Database(entities = {Repo.class}, version = 1, exportSchema = false)
public abstract class ReposDB extends RoomDatabase {

    public abstract ReposDao reposDao();

    private static ReposDB sInstance;

    public static ReposDB getDatabase(final Context context) {
        if (sInstance == null) {
            synchronized (ReposDB.class) {
                if (sInstance == null) {
                    sInstance = Room
                            .databaseBuilder(context.getApplicationContext(), ReposDB.class, "repos_db")
                            .build();
                }
            }
        }
        return sInstance;
    }

}
