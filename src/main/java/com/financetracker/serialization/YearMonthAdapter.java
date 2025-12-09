package com.financetracker.serialization;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.YearMonth;

public class YearMonthAdapter extends TypeAdapter<YearMonth> {
    @Override
    public void write(JsonWriter out, YearMonth value) throws IOException {
        out.value(value != null ? value.toString() : null);
    }

    @Override
    public YearMonth read(JsonReader in) throws IOException {
        String asString = in.nextString();
        return asString == null ? null : YearMonth.parse(asString);
    }
}

