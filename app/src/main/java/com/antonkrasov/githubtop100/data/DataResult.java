package com.antonkrasov.githubtop100.data;

import org.jetbrains.annotations.NotNull;

public class DataResult<T> {

    public T data;
    public Status status;
    public Throwable error;

    public enum Status {
        LOADING, ERROR, SUCCESS
    }

    private DataResult(T data, Status status, Throwable error) {
        this.data = data;
        this.status = status;
        this.error = error;
    }

    public static DataResult loading() {
        return new DataResult<>(null, Status.LOADING, null);
    }

    public static DataResult error(Throwable ex) {
        return new DataResult<>(null, Status.ERROR, ex);
    }

    public static <T> DataResult<T> data(T data) {
        return new DataResult<T>(data, Status.SUCCESS, null);
    }

    @NotNull
    @Override
    public String toString() {
        return "DataResult{" +
                "data=" + data +
                ", status=" + status +
                ", error=" + error +
                '}';
    }
}
