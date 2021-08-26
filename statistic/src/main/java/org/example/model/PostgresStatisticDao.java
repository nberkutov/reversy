package org.example.model;

import org.example.exception.ServerException;

import java.math.BigInteger;
import java.util.List;

public interface PostgresStatisticDao {
    List<StatsTables> getCacheTables() throws ServerException;

    BigInteger sumBytes() throws ServerException;

    long getSumCommits() throws ServerException;

    long getSumRollback() throws ServerException;

    long getSumSelectsNow() throws ServerException;

    long getSumInsertNow() throws ServerException;

    long getSumUpdateNow() throws ServerException;
}
