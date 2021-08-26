package org.example.model;

import lombok.extern.slf4j.Slf4j;
import org.example.exception.GameErrorCode;
import org.example.exception.ServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Repository
@Transactional(rollbackFor = ServerException.class)
@Slf4j
public class PostgresStatisticImpl implements PostgresStatisticDao {
    @Autowired
    private PostgresStatisticRepository repository;

    @Override
    public List<StatsTables> getCacheTables() throws ServerException {
        try {
            return repository.cacheSizeTables();
        } catch (final RuntimeException e) {
            log.error("getCacheTable from Postgres", e);
            throw new ServerException(GameErrorCode.DATABASE_NOT_POSTGRESQL);
        }
    }

    @Override
    public BigInteger sumBytes() throws ServerException {
        try {
            return repository.getSumBytes();
        } catch (final RuntimeException e) {
            log.error("getSumBytes from Postgres", e);
            throw new ServerException(GameErrorCode.DATABASE_NOT_POSTGRESQL);
        }

    }

    @Override
    public long getSumCommits() throws ServerException {
        try {
            return repository.getCommitsNow();
        } catch (final RuntimeException e) {
            log.error("getSumCommits from Postgres", e);
            throw new ServerException(GameErrorCode.DATABASE_NOT_POSTGRESQL);
        }
    }

    @Override
    public long getSumRollback() throws ServerException {
        try {
            return repository.getRollbackNow();
        } catch (final RuntimeException e) {
            log.error("getSumRollback from Postgres", e);
            throw new ServerException(GameErrorCode.DATABASE_NOT_POSTGRESQL);
        }
    }

    @Override
    public long getSumSelectsNow() throws ServerException {
        try {
            return repository.getSelectsNow();
        } catch (final RuntimeException e) {
            log.error("getSumSelectsNow from Postgres", e);
            throw new ServerException(GameErrorCode.DATABASE_NOT_POSTGRESQL);
        }
    }

    @Override
    public long getSumInsertNow() throws ServerException {
        try {
            return repository.getInsertNow();
        } catch (final RuntimeException e) {
            log.error("getSumInsertNow from Postgres", e);
            throw new ServerException(GameErrorCode.DATABASE_NOT_POSTGRESQL);
        }
    }

    @Override
    public long getSumUpdateNow() throws ServerException {
        try {
            return repository.getUpdateNow();
        } catch (final RuntimeException e) {
            log.error("getSumUpdateNow from Postgres", e);
            throw new ServerException(GameErrorCode.DATABASE_NOT_POSTGRESQL);
        }
    }
}
