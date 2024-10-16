package com.zero.chronicle.queue;

import com.alibaba.fastjson2.JSONObject;
import com.zero.chronicle.queue.domain.entity.SystemUserEntity;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.RollCycles;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ZeroChronicleQueueApplicationTests {

    ChronicleQueue queue1;

    ChronicleQueue queue2;

    @BeforeEach
    void setUp1() throws Exception {
        String basePath = OS.getTarget() + "/Queue1";
        queue1 = ChronicleQueue.singleBuilder(basePath).rollCycle(RollCycles.FIVE_MINUTELY).build();
    }

    @BeforeEach
    void setUp2() throws Exception {
        String basePath = OS.getTarget() + "/QueueDocument";
        queue2 = ChronicleQueue.singleBuilder(basePath).rollCycle(RollCycles.FIVE_MINUTELY).build();
    }

    @AfterEach
    void tearDown1() throws Exception {
        queue1.close();
    }

    @Test
    void tearDown2() throws Exception {
        queue2.close();
    }

    /**
     * 测试最简单的写入字符串
     */
    @Test
    void testWrite() {
        ExcerptAppender appender = queue1.acquireAppender();
        try {
            for (int i = 0; i < 1000; i++) {
                appender.writeText("Hello World(hello world)!--" + i);
            }
        } finally {
            appender.close();
        }
    }

    /**
     * 测试最简单的读取字符串
     */
    @Test
    void testRead() {
        ExcerptTailer tailer = queue1.createTailer("reader1"); //@wjw_note: 如果是createTailer()方法,没有给定名称,会一直能读到最后的数据而不会移动索引
        try {
            String readText = null;
            while ((readText = tailer.readText()) != null) {
                System.out.println("read: " + readText);
            }
        } finally {
            tailer.close();
        }
    }

    /**
     * 测试读写实现了Marshallable接口的对象
     */
    @Test
    void testMarshallable() {
        ExcerptAppender appender = queue2.acquireAppender();

        try {
            for (int i = 0; i < 5; i++) {
                SystemUserEntity systemUserEntity = new SystemUserEntity();
                systemUserEntity.setName("Rob");
                systemUserEntity.setAge(40 + i);
                appender.writeDocument(systemUserEntity);
            }
        } finally {
            appender.close();
        }

        ExcerptTailer tailer = queue2.createTailer("reader1");
        try {
            SystemUserEntity systemUserEntity2 = new SystemUserEntity();
            while (tailer.readDocument(systemUserEntity2)) {
                System.out.println(JSONObject.toJSONString(systemUserEntity2));
            }
        } finally {
            appender.close();
        }
    }

}
