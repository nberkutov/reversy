package org.example.services;

import org.example.exception.ServerException;
import org.example.model.PostgresStatisticDao;
import org.example.model.StatsTables;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

class PostgresStatisticDaoTest extends BaseServiceTest {

    @Autowired
    private PostgresStatisticDao postgresStatisticDao;

    @Test
    void getCacheTables() throws ServerException {
        final List<StatsTables> list = postgresStatisticDao.getCacheTables();
        assertFalse(list.isEmpty());
    }
}