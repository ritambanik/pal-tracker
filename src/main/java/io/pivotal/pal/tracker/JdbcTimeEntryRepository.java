package io.pivotal.pal.tracker;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private JdbcTemplate jdbcTemplate;

    private final RowMapper<TimeEntry> mapper = (rs, rowNum) -> new TimeEntry(
            rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getLong("user_id"),
            rs.getDate("date").toLocalDate(),
            rs.getInt("hours")
    );

    private final ResultSetExtractor<TimeEntry> extractor =
            (rs) -> rs.next() ? mapper.mapRow(rs, 1) : null;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry any) {
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO time_entries (project_id, user_id, date, hours)"
                    + " VALUES (?,?, ?, ?)",
            RETURN_GENERATED_KEYS
            );
            preparedStatement.setLong(1, any.getProjectId());
            preparedStatement.setLong(2, any.getUserId());
            preparedStatement.setDate(3, Date.valueOf(any.getDate()));
            preparedStatement.setInt(4, any.getHours());

            return preparedStatement;
        }, generatedKeyHolder);

        return find(generatedKeyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(long timeEntryId) {
        return jdbcTemplate.query(
                "SELECT id,project_id, user_id, date, hours FROM time_entries WHERE id = ?",
                new Object[]{timeEntryId}, extractor
        );
    }

    @Override
    public List<TimeEntry> list() {
        return jdbcTemplate.query(
                "SELECT id,project_id, user_id, date, hours FROM time_entries", mapper
        );
    }

    @Override
    public TimeEntry update(long eq, TimeEntry any) {
         jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE time_entries SET project_id = ?, user_id = ?, date = ?, hours = ? WHERE id = ?",
                    RETURN_GENERATED_KEYS
            );
            preparedStatement.setLong(1, any.getProjectId());
            preparedStatement.setLong(2, any.getUserId());
            preparedStatement.setDate(3, Date.valueOf(any.getDate()));
            preparedStatement.setInt(4, any.getHours());
            preparedStatement.setLong(5, eq);
            return preparedStatement;
        });
        return find(eq);
    }

    @Override
    public TimeEntry delete(long timeEntryId) {
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM time_entries WHERE id = ?",
                    RETURN_GENERATED_KEYS
            );
            preparedStatement.setLong(1, timeEntryId);
            return preparedStatement;
        });
        return null;
    }
}
