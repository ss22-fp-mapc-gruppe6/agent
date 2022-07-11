package g6Agent.db;

import g6Agent.db.codegen.tables.records.ObstaclesRecord;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.*;

import static g6Agent.db.codegen.tables.Obstacles.OBSTACLES;


public class JooqDemo {
    public static void main(String[] args) {

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:javaagents/db.sqlite")) {
            final var ctx = DSL.using(conn, SQLDialect.SQLITE);

            // schreib eine Objekt mit CRUD api
            // https://www.jooq.org/doc/latest/manual/sql-execution/crud-with-updatablerecords/simple-crud/
            final ObstaclesRecord obstacle = ctx.newRecord(OBSTACLES);
            obstacle.setX(2);
            obstacle.setY(3);
            obstacle.insert();

            // lese das Objekt wieder mit sql select-from-where
            final Result<Record> records = ctx
                    .select()
                    .from(OBSTACLES)
                    .where(OBSTACLES.X.eq(2).and(OBSTACLES.Y.eq(3))).fetch();
            for (Record record : records) {
                System.out.println("record.getValue(OBSTACLES.X) = " + record.getValue(OBSTACLES.X));
                System.out.println("record.getValue(OBSTACLES.Y) = " + record.getValue(OBSTACLES.Y));
            }

            ctx
                    .delete(OBSTACLES)
                    .where(OBSTACLES.X.plus(OBSTACLES.Y).lessOrEqual(9999))
                    .execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
