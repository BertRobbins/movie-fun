package org.superbiz.moviefun.albums;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Component;
import org.superbiz.moviefun.CsvHelper;

import java.util.List;

import static com.fasterxml.jackson.dataformat.csv.CsvSchema.ColumnType.NUMBER;

@Component
public class AlbumFixtures {

    private final ObjectReader objectReader;
    private final CsvHelper csvHelper = new CsvHelper();

    public AlbumFixtures() {
        CsvSchema schema = CsvSchema.builder()
            .addColumn("artist")
            .addColumn("title")
            .addColumn("year", NUMBER)
            .addColumn("rating", NUMBER)
            .build();

        objectReader = new CsvMapper().readerFor(Album.class).with(schema);
    }

    public List<Album> load() {
        return csvHelper.readFromCsv(objectReader, "album-fixtures.csv");
    }
}
