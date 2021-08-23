package org.example.services;

import org.example.SpringServer;
import org.example.models.CacheDataBaseDao;
import org.example.models.DataBaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = SpringServer.class)
public class BaseServiceTest {
    @Autowired
    protected DataBaseDao dataBaseDao;
    @Autowired
    protected CacheDataBaseDao cacheDataBaseDao;
    @Autowired
    protected GameService gameService;
    @Autowired
    protected PlayerService playerService;
    @Autowired
    protected RoomService roomService;
    @Autowired
    protected SenderService senderService;

}
