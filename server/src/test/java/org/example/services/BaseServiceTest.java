package org.example.services;

import org.example.models.CacheDataBaseDao;
import org.example.models.DataBaseDao;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//
//@SpringBootTest(classes = SpringServer.class)
@RunWith(SpringRunner.class)
@SpringBootTest
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

    @BeforeClass
    public static void setupHeadlessMode() {
        System.setProperty("java.awt.headless", "false");
    }


}
