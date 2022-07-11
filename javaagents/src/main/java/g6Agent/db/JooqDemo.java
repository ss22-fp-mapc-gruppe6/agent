package g6Agent.db;

import g6Agent.db.codegen.tables.daos.ObstaclesDao;
import g6Agent.db.codegen.tables.pojos.Obstacles;
import g6Agent.db.codegen.tables.records.ObstaclesRecord;
import g6Agent.services.Point;
import org.jooq.Configuration;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DAOImpl;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

import java.sql.*;
import java.util.List;

import static g6Agent.db.codegen.tables.Obstacles.OBSTACLES;


public class JooqDemo {
    public static void main(String[] args) {

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:javaagents/db.sqlite")) {
            final var ctx = DSL.using(conn, SQLDialect.SQLITE);

            // schreib eine Objekt mit CRUD api
            // https://www.jooq.org/doc/latest/manual/sql-execution/crud-with-updatablerecords/simple-crud/
            final ObstaclesRecord o1 = ctx.newRecord(OBSTACLES);
            o1.setX(2);
            o1.setY(3);
            o1.insert();

            // lese das Objekt wieder mit sql select-from-where
            final var query = ctx
                    .select()
                    .from(OBSTACLES)
                    .where(OBSTACLES.X.eq(2).and(OBSTACLES.Y.eq(3)));
            final Result<Record> records = query.fetch();
            System.out.println("lese per jooq-record");
            for (Record record : records) {
                System.out.println("record.getValue(OBSTACLES.X) = " + record.getValue(OBSTACLES.X));
                System.out.println("record.getValue(OBSTACLES.Y) = " + record.getValue(OBSTACLES.Y));
            }


            // anscheinend könnten wir den query direkt in unsere Java Klassen laden, wenn die Felder stimmen.
            // wenn die Felder nicht stimmen muss jpa-annotationen setzen
            // https://www.jooq.org/doc/latest/manual/sql-execution/fetching/pojos/
//            final List<Point> points = query.fetch().into(g6Agent.services.Point.class);
//            for (Point point : points){
//                System.out.println("point = " + point);
//            }

            // jooq kann auch DAOs generieren
            final var configuration = new DefaultConfiguration().set(conn).set(SQLDialect.SQLITE);
            final var dao = new ObstaclesDao(configuration);
            dao.insert(new Obstacles(4, 4, null));
            System.out.println("lese per dao");
            for (Obstacles fromDao : dao.findAll()) {
                System.out.println("fromDao = " + fromDao);
            }


            // lösche alles
            ctx
                    .delete(OBSTACLES)
                    .where(OBSTACLES.X.plus(OBSTACLES.Y).lessOrEqual(9999))
                    .execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
