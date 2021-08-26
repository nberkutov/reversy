package org.example.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface PostgresStatisticRepository extends JpaRepository<StatsTables, Long> {
    @Query(value = "SELECT C.oid as \"id\", relname AS \"relation\",\n" +
            "       pg_size_pretty(pg_total_relation_size(C.oid)) AS \"pretty_total_size\",\n" +
            "       pg_total_relation_size(C.oid) AS \"bytes_total_size\"\n" +
            "FROM pg_class C\n" +
            "LEFT JOIN pg_namespace N ON (N.oid = C.relnamespace)\n" +
            "WHERE nspname NOT IN ('pg_catalog', 'information_schema')\n" +
            "      AND C.relkind <> 'i'\n" +
            "      AND nspname ='public'\n" +
            "ORDER BY pg_total_relation_size(C.oid) DESC limit 10", nativeQuery = true)
    List<StatsTables> cacheSizeTables();

    @Query(value = "SELECT sum(pg_total_relation_size(C.oid))\n" +
            "FROM pg_class C\n" +
            "LEFT JOIN pg_namespace N ON (N.oid = C.relnamespace)\n" +
            "WHERE nspname NOT IN ('pg_catalog', 'information_schema')\n" +
            "      AND C.relkind <> 'i'\n" +
            "      AND nspname ='public'", nativeQuery = true)
    BigInteger getSumBytes();

    @Query(value = "SELECT SUM(xact_commit) \n" +
            "FROM pg_stat_database\n" +
            "WHERE datname NOT IN ('template0','template1')", nativeQuery = true)
    long getCommitsNow();

    @Query(value = "SELECT SUM(xact_rollback) \n" +
            "FROM pg_stat_database\n" +
            "WHERE datname NOT IN ('template0','template1')", nativeQuery = true)
    long getRollbackNow();

    @Query(value = "SELECT SUM(tup_returned) \n" +
            "FROM pg_stat_database\n" +
            "WHERE datname NOT IN ('template0','template1')", nativeQuery = true)
    long getSelectsNow();

    @Query(value = "SELECT SUM(tup_inserted) \n" +
            "FROM pg_stat_database\n" +
            "WHERE datname NOT IN ('template0','template1')", nativeQuery = true)
    long getInsertNow();

    @Query(value = "SELECT SUM(tup_updated) \n" +
            "FROM pg_stat_database\n" +
            "WHERE datname NOT IN ('template0','template1')", nativeQuery = true)
    long getUpdateNow();
}
